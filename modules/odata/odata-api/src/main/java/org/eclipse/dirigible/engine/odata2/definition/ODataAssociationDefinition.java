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

/**
 * The Class ODataAssociationDefinition.
 */
public class ODataAssociationDefinition {

    /** The name. */
    private String name;

    /** The from. */
    private ODataAssociationEndDefinition from;

    /** The to. */
    private ODataAssociationEndDefinition to;

    /**
     * <p>Define list of additional annotations for EntitySet element.</p>
     * For example:
     * <code> &lt;AssociationSet Name="someName" Association="someName" sap:creatable="true" sap:updatable="true" sap:deletable="true"&gt; </code>
     */
    private Map<String, String> annotationsAssociationSet = new HashMap<>();

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the from.
	 *
	 * @return the from
	 */
	public ODataAssociationEndDefinition getFrom() {
		return from;
	}

	/**
	 * Sets the from.
	 *
	 * @param from the new from
	 */
	public void setFrom(ODataAssociationEndDefinition from) {
		this.from = from;
	}

	/**
	 * Gets the to.
	 *
	 * @return the to
	 */
	public ODataAssociationEndDefinition getTo() {
		return to;
	}

	/**
	 * Sets the to.
	 *
	 * @param to the new to
	 */
	public void setTo(ODataAssociationEndDefinition to) {
		this.to = to;
	}

	/**
	 * Gets the annotations association set.
	 *
	 * @return the annotations association set
	 */
	public Map<String, String> getAnnotationsAssociationSet() {
		return annotationsAssociationSet;
	}

	/**
	 * Sets the annotations association set.
	 *
	 * @param annotationsAssociationSet the annotations association set
	 */
	public void setAnnotationsAssociationSet(Map<String, String> annotationsAssociationSet) {
		this.annotationsAssociationSet = annotationsAssociationSet;
	}
    
    
    
}
