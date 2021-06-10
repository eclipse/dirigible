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

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ODataNavigation {

    private String name;

    private String association;

    /**
     * <p>Define list of additional annotations for NavigationProperty element.</p>
     * For example:
     * <code> &lt;NavigationProperty Name="SomeName" Relationship="SomeRel" FromRole="FromRoleName" ToRole="ToRoleName" sap:filterable="false"/&gt; </code>
     */
    private Map<String ,String> annotationsNavigationProperty = new HashMap<>();
}
