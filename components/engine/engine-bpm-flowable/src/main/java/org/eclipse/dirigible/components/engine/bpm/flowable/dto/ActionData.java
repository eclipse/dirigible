/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.bpm.flowable.dto;

public class ActionData {

    private String action;

    public String getAction() {
        return action;
    }

    public enum Action {
        RETRY("RETRY"), SKIP("SKIP");

        private final String actionName;

        Action(String actionName) {
            this.actionName = actionName;
        }

        public String getActionName() {
            return actionName;
        }

    }
}
