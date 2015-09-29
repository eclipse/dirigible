/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.ext.db.dialect;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface IDialectSpecifier {

	public static final String DIALECT_TIMESTAMP = "$TIMESTAMP$"; //$NON-NLS-1$
	public static final String DIALECT_BLOB = "$BLOB$"; //$NON-NLS-1$
	public static final String DIALECT_CURRENT_TIMESTAMP = "$CURRENT_TIMESTAMP$"; //$NON-NLS-1$

	public static final String DIALECT_KEY_VARCHAR = "$KEY_VARCHAR$"; //$NON-NLS-1$
	public static final String DIALECT_BIG_VARCHAR = "$BIG_VARCHAR$"; //$NON-NLS-1$
	
	String specify(String sql);

	String getSpecificType(String commonType);

	String createLimitAndOffset(int limit, int offset);

	String createTopAndStart(int limit, int offset);
	
	boolean isSchemaFilterSupported();
	
	String getSchemaFilterScript();
	
	String getAlterAddOpen();
	
	String getAlterAddClose();

	InputStream getBinaryStream(ResultSet resultSet, String columnName) throws SQLException;

	boolean isCatalogForSchema();

}
