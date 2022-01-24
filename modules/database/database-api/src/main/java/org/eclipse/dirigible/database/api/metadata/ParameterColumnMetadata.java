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
package org.eclipse.dirigible.database.api.metadata;

/**
 * The Procedure Column Metadata transport object.
 */
public class ParameterColumnMetadata {

	private String name;

	private int kind;

	private String type;

	private int precision;

	private int length;
	
	private int scale;
	
	private int radix;
	
	private boolean nullable;

	private String remarks;

	/**
	 * Procedure Column Metadata
	 * 
	 * @param name name
	 * @param kind kind
	 * @param type type
	 * @param precision precision
	 * @param length length
	 * @param scale scale
	 * @param radix radix
	 * @param nullable nullable
	 * @param remarks remarks
	 */
	public ParameterColumnMetadata(String name, int kind, String type, int precision, int length, int scale, int radix,
			boolean nullable, String remarks) {
		super();
		this.name = name;
		this.kind = kind;
		this.type = type;
		this.precision = precision;
		this.length = length;
		this.scale = scale;
		this.radix = radix;
		this.nullable = nullable;
		this.remarks = remarks;
	}

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
	 * @return the kind
	 */
	public int getKind() {
		return kind;
	}

	/**
	 * @param kind the kind to set
	 */
	public void setKind(int kind) {
		this.kind = kind;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the precision
	 */
	public int getPrecision() {
		return precision;
	}

	/**
	 * @param precision the precision to set
	 */
	public void setPrecision(int precision) {
		this.precision = precision;
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * @return the scale
	 */
	public int getScale() {
		return scale;
	}

	/**
	 * @param scale the scale to set
	 */
	public void setScale(int scale) {
		this.scale = scale;
	}

	/**
	 * @return the radix
	 */
	public int getRadix() {
		return radix;
	}

	/**
	 * @param radix the radix to set
	 */
	public void setRadix(int radix) {
		this.radix = radix;
	}

	/**
	 * @return the nullable
	 */
	public boolean getNullable() {
		return nullable;
	}

	/**
	 * @param nullable the nullable to set
	 */
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	/**
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * @param remarks the remarks to set
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	
	
	
	

}
