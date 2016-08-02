/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.db.wizard;

import org.eclipse.dirigible.repository.datasource.DBSupportedTypesMap.DataTypes;

public class ColumnDefinition {

	private String name;
	private String type;
	private boolean notNull;
	private boolean primaryKey;
	private String defaultValue = ""; //$NON-NLS-1$
	private int length;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isNotNull() {
		return notNull;
	}

	public void setNotNull(boolean notNull) {
		this.notNull = notNull;
	}

	public boolean nullable() {
		return !isNotNull();
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public boolean isLengthSupported() {
		if (getType() != null) {
			if (DataTypes.VARCHAR.equals(DataTypes.valueOf(getType())) || DataTypes.CHAR.equals(DataTypes.valueOf(getType()))) {
				return true;
			}
		}
		return false;
	}
}
