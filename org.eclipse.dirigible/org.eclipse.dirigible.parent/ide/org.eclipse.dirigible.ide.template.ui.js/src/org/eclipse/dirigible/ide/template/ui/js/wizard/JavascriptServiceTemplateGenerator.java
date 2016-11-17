/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.js.wizard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateGenerator;
import org.eclipse.dirigible.repository.ext.utils.CommonUtils;
import org.eclipse.dirigible.repository.logging.Logger;

public class JavascriptServiceTemplateGenerator extends TemplateGenerator {

	private static final Logger logger = Logger.getLogger(JavascriptServiceTemplateGenerator.class);

	private static final String LOG_TAG = "JAVASCRIPT_SERVICE_GENERATOR"; //$NON-NLS-1$

	private JavascriptServiceTemplateModel model;

	public JavascriptServiceTemplateGenerator(JavascriptServiceTemplateModel model) {
		this.model = model;
	}

	@Override
	protected Map<String, Object> prepareParameters() {
		Map<String, Object> parameters = super.prepareParameters();
		parameters.put("tableName", model.getTableName()); //$NON-NLS-1$
		parameters.put("tableType", model.getTableType()); //$NON-NLS-1$
		parameters.put("entityName", CommonUtils.toCamelCase(model.getTableName())); //$NON-NLS-1$
		parameters.put("tableColumns", model.getTableColumns()); //$NON-NLS-1$
		parameters.put("tableColumnsWithoutKeys", //$NON-NLS-1$
				getTableColumnsWithoutKeys(model.getTableColumns()));

		parameters.put("primaryKey", getPrimaryKey()); //$NON-NLS-1$

		parameters.put("INTEGER", java.sql.Types.INTEGER); //$NON-NLS-1$
		parameters.put("BIGINT", java.sql.Types.BIGINT); //$NON-NLS-1$
		parameters.put("SMALLINT", java.sql.Types.SMALLINT); //$NON-NLS-1$

		parameters.put("FLOAT", java.sql.Types.REAL); //$NON-NLS-1$
		parameters.put("DOUBLE", java.sql.Types.DOUBLE); //$NON-NLS-1$
		// parameters.put("REAL", java.sql.Types.REAL);
		// parameters.put("DECIMAL", java.sql.Types.DECIMAL);
		// parameters.put("NUMERIC", java.sql.Types.NUMERIC);

		parameters.put("VARCHAR", java.sql.Types.VARCHAR); //$NON-NLS-1$
		parameters.put("CHAR", java.sql.Types.CHAR); //$NON-NLS-1$

		parameters.put("DATE", java.sql.Types.DATE); //$NON-NLS-1$
		parameters.put("TIME", java.sql.Types.TIME); //$NON-NLS-1$
		parameters.put("TIMESTAMP", java.sql.Types.TIMESTAMP); //$NON-NLS-1$

		parameters.put("BOOLEAN", java.sql.Types.BOOLEAN); //$NON-NLS-1$

		// parameters.put("CLOB", java.sql.Types.CLOB); //$NON-NLS-1$
		// parameters.put("BLOB", java.sql.Types.BLOB); //$NON-NLS-1$

		return parameters;
	}

	private Object getPrimaryKey() {
		if (model.getTableName() != null) {
			TableColumn[] columns = model.getTableColumns();
			TableColumn primaryKey = null;
			for (TableColumn column : columns) {
				if (column.isKey()) {
					primaryKey = column;
				}
			}
			if (primaryKey == null) {
				logger.error(String.format("There is no primary key in table %s, which can produce errornous artifacts", model.getTableName()));
				return null;
			}
			return primaryKey;
		}

		return null;
	}

	private TableColumn[] getTableColumnsWithoutKeys(TableColumn[] tableColumns) {
		if (tableColumns == null) {
			return null;
		}
		List<TableColumn> list = new ArrayList<TableColumn>();
		for (TableColumn tableColumn2 : tableColumns) {
			TableColumn tableColumn = tableColumn2;
			if (!tableColumn.isKey()) {
				list.add(tableColumn);
			}
		}
		return list.toArray(new TableColumn[] {});
	}

	@Override
	protected GenerationModel getModel() {
		return model;
	}

	@Override
	protected String getLogTag() {
		return LOG_TAG;
	}

	@Override
	protected byte[] afterGeneration(byte[] bytes) {
		byte[] result = model.normalizeEscapes(bytes);
		return result;
	}

}
