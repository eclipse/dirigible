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
import java.util.List;
import java.util.Map;

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

    /** The {@code java.time} class a {@code {now}} setter assigns, keyed by the intent field type. */
    private static final Map<String, String> NOW_JAVA_TYPES = Map.of("timestamp", "Instant", "date", "LocalDate");

    private static final Logger LOGGER = LoggerFactory.getLogger(SetFieldSupport.class);

    private SetFieldSupport() {}

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
    public record Setter(String process, String step, String className, String entity, String perspective, String keyProperty,
            String keyAccessor, String field, String value, boolean relation, boolean clear, boolean now, String javaType) {
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
                // setRelationField: set a to-one relation's FK to a seed id (an integer). Allowed on a
                // serviceTask or a userTask (the BPMN runs the setter right after the user task completes).
                String relField = stringArg(step, "setRelationField");
                if (relField != null && !relField.isBlank()) {
                    if (relationOf(owner, relField) == null) {
                        LOGGER.warn("Step [{}] in process [{}] sets unknown to-one relation [{}] of [{}] - skipping",
                                LoggedValue.of(step.getName()), LoggedValue.of(process.getName()), LoggedValue.of(relField),
                                LoggedValue.of(triggerEntity));
                        continue;
                    }
                    String value = stringArg(step, "value");
                    setters.add(new Setter(process.getName(), step.getName(), className(process.getName(), step.getName()), triggerEntity,
                            perspective, keyProp, keyAcc, IntentNaming.pascalCase(relField), value == null ? "" : value, true, false, false,
                            null));
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
                FieldIntent target = fieldOf(owner, field);
                if (target == null) {
                    LOGGER.warn("Step [{}] in process [{}] sets unknown field [{}] of [{}] - skipping", LoggedValue.of(step.getName()),
                            LoggedValue.of(process.getName()), LoggedValue.of(field), LoggedValue.of(triggerEntity));
                    continue;
                }
                // {now} stamps the moment on a timestamp/date field (the parser refuses it anywhere else).
                boolean now = value != null && NOW_TOKEN.equals(value.trim());
                String javaType = now ? NOW_JAVA_TYPES.get(target.getType()) : null;
                setters.add(new Setter(process.getName(), step.getName(), className(process.getName(), step.getName()), triggerEntity,
                        perspective, keyProp, keyAcc, IntentNaming.pascalCase(field), value == null ? "" : value, false, clear, now,
                        javaType));
            }
        }
        return setters;
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
