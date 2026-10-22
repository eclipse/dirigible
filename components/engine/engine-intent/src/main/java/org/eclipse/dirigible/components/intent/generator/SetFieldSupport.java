/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.intent.generator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.dirigible.components.intent.LoggedValue;
import org.eclipse.dirigible.components.intent.model.EntityIntent;
import org.eclipse.dirigible.components.intent.model.FieldIntent;
import org.eclipse.dirigible.components.intent.model.IntentModel;
import org.eclipse.dirigible.components.intent.model.ProcessIntent;
import org.eclipse.dirigible.components.intent.model.RelationIntent;
import org.eclipse.dirigible.components.intent.model.StepIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads the {@code setField} / {@code setRelationField} steps of a process and turns each into a
 * <b>field setter</b>: a BPMN service task that sets a field of the process's trigger entity to a
 * fixed value and persists it - generated as a {@code JavaDelegate} glue class instead of a
 * hand-written {@code custom/} stub.
 * <p>
 * A step authored as {@code { kind: serviceTask, args: { setField: status, value: ACTIVE } }} on a
 * {@code MemberApproval} process triggered by {@code onCreate: Member} persists
 * {@code status = "ACTIVE"} for the process-variable id via the repository's targeted
 * {@code updateProperty} - only the set column is in the UPDATE statement, so a concurrent write to
 * any other column cannot be reverted, and no {@code onUpdate} reaction re-fires (a system write).
 * Two such steps on the two branches of an approve/reject decision are how "Approve -> ACTIVE,
 * Reject -> REJECTED" is expressed as glue, with no custom Java.
 * <p>
 * {@code setRelationField: Status, value: 2} is the generic counterpart for a status modelled as a
 * to-one relation (an FK to a settings/nomenclature entity): it assigns the relation's FK property
 * to the integer seed id, unquoted. It is allowed on a {@code serviceTask} (bound directly) or a
 * {@code userTask} (the BPMN runs the setter right after the task completes, like the writer).
 * <p>
 * {@code clearField: errorMessage} is the erasure twin of {@code setField} (#7386): the same
 * targeted write, assigning {@code null} instead of a literal, so a process can take back a field
 * it wrote earlier - an error route's failure text that a re-driven, now successful instance would
 * otherwise leave standing on a success status.
 * <p>
 * {@code setField: approvedAt, value: "{now}"} records <b>when</b> the flow reached the step
 * (#7873): on a {@code timestamp} field it assigns {@code java.time.Instant.now()}, on a
 * {@code date} field {@code java.time.LocalDate.now()} - the Java types the generated entity
 * declares for those columns - through the same targeted write.
 * <p>
 * Each of the three may name a member one hop away (#7934): {@code setRelationField: order.Status}
 * writes the {@code Status} of the {@code Order} the trigger record's {@code order} relation points
 * at. The step loads the trigger row, reads that FK and writes through the TARGET's repository -
 * the same targeted write the target's own setter makes, so its {@code lifecycle:}, {@code stampOn}
 * and history apply - and publishes the target's {@code -transitioned}. The first segment is a
 * local to-one relation of the trigger entity (parser-enforced); a null FK at run time is a logged
 * no-op.
 * <p>
 * {@code set: { slotKey: null, Status: DONE }} writes several members of ONE record in one
 * statement (#8042): each key is what a single-member setter would accept, its value follows that
 * setter's rule ({@code null} is the {@code clearField} erasure, a to-one relation takes a seed
 * id), and the whole map goes through one {@code updateProperties}, so the record's lifecycle,
 * status stamps and label judge the change as a whole and one {@code -transitioned} is published.
 * All keys address the same record - the trigger, or one hop through the same relation
 * (parser-enforced). A one-key map is the single-member setter of that key.
 * <p>
 * Scope: {@code setField} assigns a literal to a {@code string}/{@code text} field, or the
 * whole-value {@code {now}} token to a {@code timestamp}/{@code date} field; any other expression
 * value is out of scope. {@code setRelationField} assigns an integer seed id to a
 * {@code manyToOne}/ {@code oneToOne} relation's FK (validated by the parser).
 */
public final class SetFieldSupport {

    /**
     * The whole-value {@code setField} token that stamps the moment the step runs - only on a
     * {@code timestamp}/{@code date} field, and only as the whole value (both parser-enforced).
     */
    public static final String NOW_TOKEN = "{now}";

    /** The step arg carrying a multi-member write: a map of member path to value (#8042). */
    public static final String SET_ARG = "set";

    /** The {@code java.time} class a {@code {now}} setter assigns, keyed by the intent field type. */
    private static final Map<String, String> NOW_JAVA_TYPES = Map.of("timestamp", "Instant", "date", "LocalDate");

    private static final Logger LOGGER = LoggerFactory.getLogger(SetFieldSupport.class);

    private SetFieldSupport() {}

    /**
     * One member a setter writes.
     *
     * @param field the PascalCase property assigned (a string field for {@code setField}, or a to-one
     *        relation's FK property for {@code setRelationField})
     * @param value the value assigned (a string literal, or a seed id for a relation FK); empty for a
     *        {@code clearField}, which assigns nothing
     * @param relation {@code true} for a {@code setRelationField} (assign the FK to the integer
     *        {@code value}, unquoted); {@code false} for a {@code setField} (assign the quoted literal)
     * @param clear {@code true} for a {@code clearField} (assign {@code null} - the erasure), which is
     *        never a relation and carries no value
     * @param now {@code true} for a {@code setField} whose value is the whole {@link #NOW_TOKEN}
     *        (assign the moment the step runs instead of a literal)
     * @param javaType the {@code java.time} class a {@code now} setter assigns ({@code Instant} for a
     *        {@code timestamp} field, {@code LocalDate} for a {@code date} field); {@code null} for
     *        every other setter
     */
    public record Assignment(String field, String value, boolean relation, boolean clear, boolean now, String javaType) {
    }

    /**
     * One field setter to generate.
     *
     * @param process the owning process
     * @param step the {@code serviceTask} step that declared the {@code setField}
     * @param className the generated handler class simple name (e.g. {@code MemberApprovalActivate})
     * @param entity the trigger entity whose field is set (e.g. {@code Member})
     * @param perspective the entity's resolved perspective (its gen data subfolder)
     * @param keyProperty the process variable holding the entity's PK (e.g. {@code Id})
     * @param keyAccessor the {@link Number} accessor matching the PK type ({@code intValue} /
     *        {@code longValue})
     * @param assignments the members written, in the authored order - one for a single-member setter,
     *        several for a {@code set:} map, all of the same record
     * @param via for a one-hop setter ({@code order.Status}), the trigger entity's FK property the
     *        written record is reached through (e.g. {@code Order}); {@code null} for a setter of the
     *        trigger record itself
     * @param targetEntity the entity a one-hop setter writes (e.g. {@code Order}); {@code null}
     *        otherwise
     * @param targetPerspective the target entity's resolved perspective; {@code null} otherwise
     * @param targetKeyAccessor the {@link Number} accessor matching the target's PK type; {@code null}
     *        otherwise
     */
    public record Setter(String process, String step, String className, String entity, String perspective, String keyProperty,
            String keyAccessor, List<Assignment> assignments, String via, String targetEntity, String targetPerspective,
            String targetKeyAccessor) {

        /** The entity whose row the setter writes: the one-hop target, else the trigger entity. */
        public String writtenEntity() {
            return via == null ? entity : targetEntity;
        }

        /**
         * The value the setter assigns to a to-one relation's FK property, or {@code null} when it does not
         * write that relation.
         *
         * @param property the PascalCase FK property (e.g. {@code Status})
         * @return the assigned value (a seed id once resolved), or {@code null}
         */
        public String relationValue(String property) {
            for (Assignment assignment : assignments) {
                if (assignment.relation() && property.equals(assignment.field())) {
                    return assignment.value();
                }
            }
            return null;
        }
    }

    /**
     * A setter path resolved to the record it writes.
     *
     * @param via the to-one relation of the trigger entity the path goes through; {@code null} for a
     *        plain path, which writes the trigger record itself
     * @param target the entity whose member is written
     * @param member the member name as authored (a field, or a to-one relation for
     *        {@code setRelationField})
     */
    public record Hop(RelationIntent via, EntityIntent target, String member) {
    }

    /**
     * Resolves a setter path against the trigger entity: a plain name is a member of the trigger
     * itself, {@code <relation>.<member>} a member of the LOCAL entity a to-one relation of the trigger
     * points at.
     *
     * @param trigger the process's trigger entity
     * @param path the authored path
     * @param byName the model's entities, by name
     * @return the resolved hop, or {@code null} when a dotted path does not resolve (more than one dot,
     *         an empty segment, a first segment that is not a to-one relation, a cross-model or unknown
     *         target) - the parser names the reason
     */
    public static Hop hop(EntityIntent trigger, String path, Map<String, EntityIntent> byName) {
        int dot = path.indexOf('.');
        if (dot < 0) {
            return new Hop(null, trigger, path);
        }
        String member = path.substring(dot + 1);
        if (dot == 0 || member.isEmpty() || member.indexOf('.') >= 0) {
            return null;
        }
        RelationIntent relation = relationOf(trigger, path.substring(0, dot));
        if (relation == null || relation.isCrossModel()) {
            return null;
        }
        EntityIntent target = byName.get(relation.getTo());
        return target == null ? null : new Hop(relation, target, member);
    }

    /** Every field setter across every process in the model. */
    public static List<Setter> setters(IntentModel model) {
        List<Setter> setters = new ArrayList<>();
        Map<String, EntityIntent> byName = IntentEntities.byName(model);
        Map<String, String> compositionParents = IntentEntities.compositionParents(model);
        for (ProcessIntent process : model.getProcesses()) {
            String triggerEntity = TriggerSupport.triggerEntity(process);
            EntityIntent owner = triggerEntity == null ? null : byName.get(triggerEntity);
            if (owner == null) {
                continue; // no trigger entity -> no entity instance to set a field on
            }
            String perspective = IntentEntities.resolvePerspective(triggerEntity, compositionParents, model);
            String keyProp = IntentEntities.keyFieldName(owner);
            String keyAcc = idAccessor(IntentEntities.primaryKeyOf(owner));
            for (StepIntent step : process.getSteps()) {
                if (step.getName() == null) {
                    continue;
                }
                Map<?, ?> set = setMap(step);
                if (set != null) {
                    Setter setter =
                            setMapSetter(process, step, owner, perspective, keyProp, keyAcc, set, byName, model, compositionParents);
                    if (setter != null) {
                        setters.add(setter);
                    }
                    continue;
                }
                // setRelationField: set a to-one relation's FK to a seed id (an integer). Allowed on a
                // serviceTask or a userTask (the BPMN runs the setter right after the user task completes).
                String relField = stringArg(step, "setRelationField");
                if (relField != null && !relField.isBlank()) {
                    Hop hop = hop(owner, relField, byName);
                    if (hop == null || relationOf(hop.target(), hop.member()) == null) {
                        LOGGER.warn("Step [{}] in process [{}] sets unknown to-one relation [{}] of [{}] - skipping",
                                LoggedValue.of(step.getName()), LoggedValue.of(process.getName()), LoggedValue.of(relField),
                                LoggedValue.of(triggerEntity));
                        continue;
                    }
                    String value = stringArg(step, "value");
                    setters.add(setter(process, step, owner, perspective, keyProp, keyAcc, hop,
                            List.of(relationAssignment(hop.member(), value)), model, compositionParents));
                    continue;
                }
                // setField: set a string/text field to a literal value (serviceTask only).
                if (!"serviceTask".equals(step.getKind())) {
                    continue;
                }
                // setField writes a literal; its clearField twin erases the same kind of field and
                // carries no value (the parser refuses the two together).
                String field = stringArg(step, "setField");
                String value = stringArg(step, "value");
                boolean clear = false;
                if (field == null || field.isBlank()) {
                    field = stringArg(step, "clearField");
                    if (field == null || field.isBlank()) {
                        continue;
                    }
                    value = null;
                    clear = true;
                }
                Hop hop = hop(owner, field, byName);
                FieldIntent target = hop == null ? null : fieldOf(hop.target(), hop.member());
                if (target == null) {
                    LOGGER.warn("Step [{}] in process [{}] sets unknown field [{}] of [{}] - skipping", LoggedValue.of(step.getName()),
                            LoggedValue.of(process.getName()), LoggedValue.of(field), LoggedValue.of(triggerEntity));
                    continue;
                }
                setters.add(setter(process, step, owner, perspective, keyProp, keyAcc, hop,
                        List.of(fieldAssignment(hop.member(), target, clear ? null : value == null ? "" : value)), model,
                        compositionParents));
            }
        }
        return setters;
    }

    /**
     * The setter of a {@code set:} step: one assignment per key, in the authored order, all of the
     * record the first key addresses. A key that does not resolve, or addresses another record, skips
     * the step - the parser has already named it.
     */
    private static Setter setMapSetter(ProcessIntent process, StepIntent step, EntityIntent owner, String perspective, String keyProp,
            String keyAcc, Map<?, ?> set, Map<String, EntityIntent> byName, IntentModel model, Map<String, String> compositionParents) {
        List<Assignment> assignments = new ArrayList<>();
        Hop record = null;
        for (Map.Entry<?, ?> entry : set.entrySet()) {
            String path = String.valueOf(entry.getKey());
            Hop hop = hop(owner, path, byName);
            if (hop == null || (record != null && !sameRecord(record, hop))) {
                LOGGER.warn("Step [{}] in process [{}] sets [{}], which is not a member of the record the step writes - skipping",
                        LoggedValue.of(step.getName()), LoggedValue.of(process.getName()), LoggedValue.of(path));
                return null;
            }
            record = record == null ? hop : record;
            Object value = entry.getValue();
            FieldIntent field = fieldOf(hop.target(), hop.member());
            if (relationOf(hop.target(), hop.member()) != null && value != null) {
                assignments.add(relationAssignment(hop.member(), value.toString()));
            } else if (field != null && "serviceTask".equals(step.getKind())) {
                assignments.add(fieldAssignment(hop.member(), field, value == null ? null : value.toString()));
            } else {
                LOGGER.warn("Step [{}] in process [{}] sets [{}] to a value its setter does not accept - skipping",
                        LoggedValue.of(step.getName()), LoggedValue.of(process.getName()), LoggedValue.of(path));
                return null;
            }
        }
        return record == null ? null
                : setter(process, step, owner, perspective, keyProp, keyAcc, record, assignments, model, compositionParents);
    }

    /**
     * Whether two resolved paths write the same record: both the trigger, or both through one relation.
     */
    public static boolean sameRecord(Hop first, Hop second) {
        String firstVia = first.via() == null ? null
                : first.via()
                       .getName();
        String secondVia = second.via() == null ? null
                : second.via()
                        .getName();
        return Objects.equals(firstVia, secondVia);
    }

    /** A {@code setRelationField} assignment of a seed id. */
    private static Assignment relationAssignment(String member, String value) {
        return new Assignment(IntentNaming.pascalCase(member), value == null ? "" : value, true, false, false, null);
    }

    /**
     * A field assignment: the erasure for a {@code null} value, the moment for the whole {@code {now}}
     * token (the parser refuses it on any other field type), else the literal.
     */
    private static Assignment fieldAssignment(String member, FieldIntent field, String value) {
        if (value == null) {
            return new Assignment(IntentNaming.pascalCase(member), "", false, true, false, null);
        }
        boolean now = NOW_TOKEN.equals(value.trim());
        return new Assignment(IntentNaming.pascalCase(member), value, false, false, now, now ? NOW_JAVA_TYPES.get(field.getType()) : null);
    }

    /**
     * One setter, its one-hop target coordinates filled in only when the path goes through a relation.
     */
    private static Setter setter(ProcessIntent process, StepIntent step, EntityIntent owner, String perspective, String keyProp,
            String keyAcc, Hop hop, List<Assignment> assignments, IntentModel model, Map<String, String> compositionParents) {
        String via = null;
        String targetEntity = null;
        String targetPerspective = null;
        String targetKeyAccessor = null;
        if (hop.via() != null) {
            via = IntentNaming.pascalCase(hop.via()
                                             .getName());
            targetEntity = hop.target()
                              .getName();
            targetPerspective = IntentEntities.resolvePerspective(targetEntity, compositionParents, model);
            targetKeyAccessor = idAccessor(IntentEntities.primaryKeyOf(hop.target()));
        }
        return new Setter(process.getName(), step.getName(), className(process.getName(), step.getName()), owner.getName(), perspective,
                keyProp, keyAcc, List.copyOf(assignments), via, targetEntity, targetPerspective, targetKeyAccessor);
    }

    /**
     * The {@code set:} map a step declares, or {@code null} when it declares none (or not as a map,
     * which the parser refuses).
     */
    public static Map<?, ?> setMap(StepIntent step) {
        Object set = step.getArgs() == null ? null
                : step.getArgs()
                      .get(SET_ARG);
        return set instanceof Map<?, ?> map ? map : null;
    }

    /**
     * Whether the step is a setter: it declares {@code setField}, {@code clearField},
     * {@code setRelationField} or a {@code set:} map - each bound to the generated
     * {@code <Process><Step>} delegate.
     */
    public static boolean isSetterStep(StepIntent step) {
        return !isBlank(stringArg(step, "setField")) || !isBlank(stringArg(step, "clearField"))
                || !isBlank(stringArg(step, "setRelationField")) || (step.getArgs() != null && step.getArgs()
                                                                                                   .get(SET_ARG) != null);
    }

    /**
     * The to-one relation writes a step MAY carry, as authored path to value, before the model says
     * which paths are relations: its {@code setRelationField} with its {@code value}, and every key of
     * its {@code set:} map. A caller matching a path against a relation name needs no model to tell
     * them apart - a {@code set:} key naming a field never matches a relation.
     *
     * @param step the step
     * @return the candidate writes, in the authored order; a value is {@code null} when absent
     */
    public static Map<String, String> relationWrites(StepIntent step) {
        Map<String, String> writes = new LinkedHashMap<>();
        String relation = stringArg(step, "setRelationField");
        if (!isBlank(relation)) {
            writes.put(relation, stringArg(step, "value"));
        }
        Map<?, ?> set = setMap(step);
        if (set != null) {
            for (Map.Entry<?, ?> entry : set.entrySet()) {
                writes.put(String.valueOf(entry.getKey()), entry.getValue() == null ? null
                        : entry.getValue()
                               .toString());
            }
        }
        return writes;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    /**
     * The generated handler class name for a {@code setField} step: the process name plus the step
     * name, both PascalCase ({@code MemberApproval} + {@code activate} ->
     * {@code MemberApprovalActivate} ), so it is unique across processes that reuse a step name.
     */
    public static String className(String process, String step) {
        return IntentNaming.pascalCase(process) + IntentNaming.pascalCase(step);
    }

    /**
     * Whether a {@code setField} on a field of this intent type takes the {@link #NOW_TOKEN} (and only
     * it): the moment-holding types whose generated Java property is a {@code java.time} value.
     */
    public static boolean isNowFieldType(String type) {
        return NOW_JAVA_TYPES.containsKey(type);
    }

    /**
     * The Java expression for "now" in the type the generated entity declares for a field of this
     * intent type - {@code java.time.Instant.now()} for a {@code timestamp}, {@code
     * java.time.LocalDate.now()} for a {@code date}. A {@code stampOn: { status }} field (#7874) is
     * stamped with it.
     *
     * @param type the intent field type
     * @return the expression, or {@code null} for a type that holds no moment
     */
    public static String nowExpression(String type) {
        String javaType = type == null ? null : NOW_JAVA_TYPES.get(type);
        return javaType == null ? null : "java.time." + javaType + ".now()";
    }

    private static FieldIntent fieldOf(EntityIntent entity, String name) {
        for (FieldIntent field : entity.getFields()) {
            if (name.equals(field.getName())) {
                return field;
            }
        }
        return null;
    }

    /** The named to-one relation of the entity (the FK whose id a {@code setRelationField} assigns). */
    private static RelationIntent relationOf(EntityIntent entity, String name) {
        for (RelationIntent rel : entity.getRelations()) {
            if (name.equals(rel.getName()) && ("manyToOne".equals(rel.getKind()) || "oneToOne".equals(rel.getKind()))) {
                return rel;
            }
        }
        return null;
    }

    private static String idAccessor(FieldIntent pk) {
        String type = pk == null || pk.getType() == null ? "integer" : pk.getType();
        return "long".equals(type) ? "longValue" : "intValue";
    }

    private static String stringArg(StepIntent step, String key) {
        Map<String, Object> args = step.getArgs();
        if (args == null) {
            return null;
        }
        Object value = args.get(key);
        return value == null ? null : value.toString();
    }
}
