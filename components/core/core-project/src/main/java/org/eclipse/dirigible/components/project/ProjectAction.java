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
package org.eclipse.dirigible.components.project;

import java.util.List;
import org.eclipse.dirigible.components.command.CommandDescriptor;

/**
 * The Class ProjectAction.
 */
public class ProjectAction {

    /** The name. */
    private final String name;

    /** The commands. */
    private final List<CommandDescriptor> commands;

    /** The publish. */
    private final boolean publish;

    /** The publish. */
    private final boolean registry;

    public ProjectAction(String name, List<CommandDescriptor> commands, boolean publish, boolean afterPublish) {
        this.name = name;
        this.commands = commands;
        this.publish = publish;
        this.registry = afterPublish;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the commands.
     *
     * @return the commands
     */
    public List<CommandDescriptor> getCommands() {
        return commands;
    }

    /**
     * Checks if is publish.
     *
     * @return true, if is publish
     */
    public boolean isPublish() {
        return publish;
    }

    /**
     * Checks if is registry.
     *
     * @return true, if is registry
     */
    public boolean isRegistry() {
        return registry;
    }

    @Override
    public String toString() {
        return "ProjectAction{" + "name='" + name + '\'' + ", commands=" + commands + ", publish=" + publish + ", registry=" + registry
                + '}';
    }
}
