/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.odata2.definition;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ODataEntityDefinition {

    private String name;

    private String alias;

    private String table;

    private List<ODataProperty> properties = new ArrayList<>();

    private List<ODataNavigation> navigations = new ArrayList<>();

    private List<ODataHandler> handlers = new ArrayList<>();

    /**
     * For VIEW type the keys need to be specified explicitly, because on DB side there will be no keys definitions
     */
    private List<String> keys = new ArrayList<>();

    /**
     * <p>Define list of additional annotations for EntitySet element.</p>
     * For example:
     * <code> &lt;EntitySet Name="SomeName" EntityType="someType" sap:creatable="true" sap:updatable-path="Updatable"&gt; </code>
     */
    private Map<String, String> annotationsEntitySet = new HashMap<>();

    /**
     * <p>Define list of additional annotations for EntityType element.</p>
     * For example:
     * <code> &lt;EntityType Name="SomeTypeName" sap:semantics="aggregate""&gt; </code>
     */
    private Map<String, String> annotationsEntityType = new HashMap<>();
}
