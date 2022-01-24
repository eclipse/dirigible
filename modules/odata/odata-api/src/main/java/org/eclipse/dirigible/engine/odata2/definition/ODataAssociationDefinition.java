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
package org.eclipse.dirigible.engine.odata2.definition;

import java.util.HashMap;
import java.util.Map;

public class ODataAssociationDefinition {

    private String name;

    private ODataAssociationEndDefinition from;

    private ODataAssociationEndDefinition to;

    /**
     * <p>Define list of additional annotations for EntitySet element.</p>
     * For example:
     * <code> &lt;AssociationSet Name="someName" Association="someName" sap:creatable="true" sap:updatable="true" sap:deletable="true"&gt; </code>
     */
    private Map<String, String> annotationsAssociationSet = new HashMap<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ODataAssociationEndDefinition getFrom() {
		return from;
	}

	public void setFrom(ODataAssociationEndDefinition from) {
		this.from = from;
	}

	public ODataAssociationEndDefinition getTo() {
		return to;
	}

	public void setTo(ODataAssociationEndDefinition to) {
		this.to = to;
	}

	public Map<String, String> getAnnotationsAssociationSet() {
		return annotationsAssociationSet;
	}

	public void setAnnotationsAssociationSet(Map<String, String> annotationsAssociationSet) {
		this.annotationsAssociationSet = annotationsAssociationSet;
	}
    
    
    
}
