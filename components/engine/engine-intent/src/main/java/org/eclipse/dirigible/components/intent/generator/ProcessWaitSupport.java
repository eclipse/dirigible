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

import org.eclipse.dirigible.components.intent.model.EntityIntent;
import org.eclipse.dirigible.components.intent.model.IntentModel;
import org.eclipse.dirigible.components.intent.model.ProcessIntent;
import org.eclipse.dirigible.components.intent.model.StepIntent;

/**
 * A {@code wait} step parks the process on a BPMN message intermediate catch event until an entity
 * lifecycle event resumes it: {@code args: { onCreate: CaseMessage, via: Case, when: "...", next:
 * ... }}. The BPMN generator emits the {@code <message>} + {@code <intermediateCatchEvent>}; this
 * support derives the glue listener the events template generates - a {@code MessageHandler} on the
 * event entity's topic that resolves the process-carrying record (through the {@code via:} to-one
 * back-reference, or the event record itself when the event entity IS the trigger entity), reads
 * its stamped {@code ProcessId} and correlates the message. Correlation rides the ProcessId the
 * trigger listener already writes back - no new bookkeeping; a missing/blank ProcessId or an
 * instance not parked on the message is a no-op, never an error (the fail-soft glue convention).
 * <p>
 * A <b>timed</b> wait ({@code args: { after: PT10S, next: ... }}) is the pause of a polling loop: a
 * timer intermediate catch event and nothing else - no message, no listener, because no record
 * resumes it.
 */
public final class ProcessWaitSupport {

    /**
     * Entity events a {@code wait} step may bind to. Delete cannot resume a wait - there is no record
     * left to correlate through. {@code onTransition} can, and had to be added: a status write
     * publishes {@code -transitioned} only, so a wait bound to {@code onUpdate} never woke for one -
     * and since {@code abortOn:} already binds that same channel, a transition could KILL a parked
     * instance but never resume it.
     */
    public static final List<String> EVENT_KINDS = List.of("onCreate", "onUpdate", "onTransition");

    /** The {@code wait} arg that makes it a timed wait: an ISO-8601 duration ({@code PT10S}). */
    public static final String AFTER = "after";

    private ProcessWaitSupport() {}

    /**
     * One wait listener to generate (one per {@code wait} step).
     *
     * @param process the owning process
     * @param step the wait step name (also the catch event's element id)
     * @param className the generated handler class simple name base (e.g. {@code
     *        CaseHandlingAwaitReply}; the template appends {@code Wait})
     * @param messageName the BPMN message name the catch event subscribes to and the listener
     *        correlates
     * @param eventKind the resuming lifecycle event ({@code onCreate}/{@code onUpdate})
     * @param eventEntity the entity whose event resumes the wait
     * @param eventPerspective the event entity's resolved perspective (its gen data subfolder)
     * @param eventKeyProperty the event entity's PK property (re-load for the direct case)
     * @param when the optional guard expression over the event record (raw, {@code null} when none)
     * @param viaFkProperty the PascalCase FK property walked to the process-carrying record, or
     *        {@code null} when the event entity is the trigger entity itself
     * @param parentEntity the trigger entity carrying the {@code ProcessId} (the {@code via:} target;
     *        equals {@code eventEntity} in the direct case)
     * @param parentPerspective the trigger entity's resolved perspective
     */
    public record Wait(String process, String step, String className, String messageName, String eventKind, String eventEntity,
            String eventPerspective, String eventKeyProperty, String when, String viaFkProperty, String parentEntity,
            String parentPerspective) {
    }

    /** Every wait listener across every process in the model. */
    public static List<Wait> waits(IntentModel model) {
        List<Wait> waits = new ArrayList<>();
        Map<String, EntityIntent> byName = IntentEntities.byName(model);
        Map<String, String> compositionParents = IntentEntities.compositionParents(model);
        for (ProcessIntent process : model.getProcesses()) {
            String triggerEntity = TriggerSupport.triggerEntity(process);
            if (process.getName() == null || triggerEntity == null || !byName.containsKey(triggerEntity)) {
                continue; // no trigger entity -> no ProcessId column to correlate on
            }
            for (StepIntent step : process.getSteps()) {
                if (!"wait".equals(step.getKind()) || step.getName() == null || isTimed(step)) {
                    continue; // a timed wait resumes on its timer - nothing to correlate
                }
                String eventKind = eventKind(step);
                String eventEntity = eventKind == null ? null : stringArg(step, eventKind);
                if (eventEntity == null || !byName.containsKey(eventEntity)) {
                    continue; // parser-reported; skip so no dangling glue is emitted
                }
                String via = stringArg(step, "via");
                boolean direct = eventEntity.equals(triggerEntity);
                if (!direct && (via == null || via.isBlank())) {
                    continue; // parser-reported
                }
                waits.add(new Wait(process.getName(), step.getName(), className(process.getName(), step.getName()),
                        messageName(process.getName(), step.getName()), eventKind, eventEntity,
                        IntentEntities.resolvePerspective(eventEntity, compositionParents, model),
                        IntentEntities.keyFieldName(byName.get(eventEntity)), stringArg(step, "when"),
                        direct ? null : IntentNaming.pascalCase(via), triggerEntity,
                        IntentEntities.resolvePerspective(triggerEntity, compositionParents, model)));
            }
        }
        return waits;
    }

    /** The generated handler class simple name base: process + step, PascalCase. */
    public static String className(String process, String step) {
        return IntentNaming.pascalCase(process) + IntentNaming.pascalCase(step);
    }

    /**
     * The BPMN message name a wait step's catch event subscribes to. Unique per process + step;
     * correlation additionally scopes by process-instance id, so equal names across processes would
     * still be safe - the compound name just keeps the Processes admin view readable.
     */
    public static String messageName(String process, String step) {
        return IntentNaming.pascalCase(process) + IntentNaming.pascalCase(step);
    }

    /** Whether a wait step parks for a duration ({@code after:}) rather than on an entity event. */
    public static boolean isTimed(StepIntent step) {
        return stringArg(step, AFTER) != null;
    }

    /**
     * The lifecycle event kind a wait step binds to, or {@code null} when none is declared - and for a
     * timed wait, which is not an event wait whatever else it declares (the parser refuses the mix).
     */
    public static String eventKind(StepIntent step) {
        if (isTimed(step)) {
            return null;
        }
        for (String kind : EVENT_KINDS) {
            if (stringArg(step, kind) != null) {
                return kind;
            }
        }
        return null;
    }

    private static String stringArg(StepIntent step, String key) {
        Object value = step.getArgs() == null ? null
                : step.getArgs()
                      .get(key);
        return value == null ? null : value.toString();
    }
}
