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

public class ODataAssociationDefinition {
	
	private String name;
	
	private ODataAssiciationEndDefinition from;
	
	private ODataAssiciationEndDefinition to;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the from
	 */
	public ODataAssiciationEndDefinition getFrom() {
		return from;
	}

	/**
	 * @param from the from to set
	 */
	public void setFrom(ODataAssiciationEndDefinition from) {
		this.from = from;
	}

	/**
	 * @return the to
	 */
	public ODataAssiciationEndDefinition getTo() {
		return to;
	}

	/**
	 * @param to the to to set
	 */
	public void setTo(ODataAssiciationEndDefinition to) {
		this.to = to;
	}

}
