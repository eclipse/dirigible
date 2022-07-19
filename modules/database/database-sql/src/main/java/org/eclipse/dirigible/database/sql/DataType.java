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
package org.eclipse.dirigible.database.sql;

/**
 * The Enum DataType.
 */
public enum DataType {
	VARCHAR("VARCHAR"),
	CHAR("CHAR"),
	DATE("DATE"),
	SECONDDATE("SECONDDATE"),
	TIME("TIME"),
	TIMESTAMP("TIMESTAMP"),
	INTEGER("INTEGER"),
	TINYINT("TINYINT"),
	BIGINT("BIGINT"),
	SMALLINT("SMALLINT"),
	REAL("REAL"),
	DOUBLE("DOUBLE"),
	DOUBLE_PRECISION("DOUBLE PRECISION"),
	BOOLEAN("BOOLEAN"),
	BLOB("BLOB"),
	DECIMAL("DECIMAL"),
	BIT("BIT"),
	NVARCHAR("NVARCHAR"),
	FLOAT("FLOAT"),
	BYTE("BYTE"),
	NCLOB("NCLOB"),
	ARRAY("ARRAY"),
	VARBINARY("VARBINARY"),
	BINARY_VARYING("BINARY VARYING"),
	SHORTTEXT("SHORTTEXT"),
	ALPHANUM("ALPHANUM"),
	CLOB("CLOB"),
	SMALLDECIMAL("SMALLDECIMAL"),
	BINARY("BINARY"),
	ST_POINT("ST_POINT"),
	ST_GEOMETRY("ST_GEOMETRY"),
	CHARACTER_VARYING("CHARACTER VARYING"),
	BINARY_LARGE_OBJECT("BINARY LARGE OBJECT"),
	CHARACTER_LARGE_OBJECT("CHARACTER LARGE OBJECT"),
	CHARACTER("CHARACTER"),
	NCHAR("NCHAR"),
	NUMERIC("NUMERIC");
	
	private String name;

	DataType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
    
    public String toString() {
        return name;
    }
    
    public static final DataType valueOfByName(String name) {
    	for(DataType type : DataType.class.getEnumConstants()) {
            if(type.toString().equals(name)) {
                return type;
            }
        }
       throw new IllegalArgumentException("DataType not found: " + name);
    }
    
    
}
