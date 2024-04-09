/*
 * Copyright (c) 2024 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.bpm.flowable.dto;

public class TaskActionData {
    private String action;

    public String getAction() {
        return action;
    }

    public enum TaskAction {

        CLAIM("CLAIM"),

        UNCLAIM("UNCLAIM");

        private final String actionName;

        TaskAction(String actionName) {
            this.actionName = actionName;
        }

        public String getActionName() {
            return actionName;
        }
    }
}
