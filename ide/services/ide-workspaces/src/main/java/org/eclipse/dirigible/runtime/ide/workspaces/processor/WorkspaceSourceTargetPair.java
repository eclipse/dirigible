/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.runtime.ide.workspaces.processor;

/**
 * The Workspace Source Target Pair.
 */
public class WorkspaceSourceTargetPair {

    private String sourceWorkspace;

    private String source;

    private String targetWorkspace;

    private String target;

    /**
     * Gets the source workspace.
     *
     * @return the sourceWorkspace
     */
    public String getSourceWorkspace() {
        return sourceWorkspace;
    }

    /**
     * Sets the source.
     *
     * @param sourceWorkspace the new source workspace
     */
    public void setSourceWorkspace(String sourceWorkspace) {
        this.sourceWorkspace = sourceWorkspace;
    }

    /**
     * Gets the source.
     *
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the source.
     *
     * @param source the new source
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Gets the target workspace.
     *
     * @return the targetWorkspace
     */
    public String getTargetWorkspace() {
        return targetWorkspace;
    }

    /**
     * Sets the target workspace.
     *
     * @param targetWorkspace the new target workspace
     */
    public void setTargetWorkspace(String targetWorkspace) {
        this.targetWorkspace = targetWorkspace;
    }

    /**
     * Gets the target.
     *
     * @return the target
     */
    public String getTarget() {
        return target;
    }

    /**
     * Sets the target.
     *
     * @param target the new target
     */
    public void setTarget(String target) {
        this.target = target;
    }

}
