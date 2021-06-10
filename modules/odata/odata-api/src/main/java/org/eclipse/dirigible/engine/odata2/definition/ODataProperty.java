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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class ODataProperty {

    private String name;

    private String column;

    private boolean nullable;

    private String type;

    /**
     * <p>Define list of additional annotations for Property element.</p>
     * For example:
     * <code> &lt;Property Name="SomeName" Type="Edm.String" sap:label="SomeLabel" sap:filterable="false"/&gt; </code>
     */
    private Map<String ,String> annotationsProperty = new HashMap<>();
}
