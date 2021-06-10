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
public class ODataAssociationDefinition {

    private String name;

    private ODataAssociationEndDefinition from;

    private ODataAssociationEndDefinition to;

    /**
     * <p>Define list of additional annotations for EntitySet element.</p>
     * For example:
     * <code> &lt;AssociationSet Name="someName" Association="someName" sap:creatable="true" sap:updatable="true" sap:deletable="true"&gt; </code>
     */
    private Map<String ,String> annotationsAssociationSet = new HashMap<>();
}
