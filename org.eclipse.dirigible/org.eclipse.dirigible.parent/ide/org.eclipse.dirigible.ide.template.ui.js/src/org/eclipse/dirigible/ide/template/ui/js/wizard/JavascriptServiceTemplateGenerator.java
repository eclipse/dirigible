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

/**
 * JavaScript Service Template Generator
 */
public class JavascriptServiceTemplateGenerator extends TemplateGenerator {

	private static final Logger logger = Logger.getLogger(JavascriptServiceTemplateGenerator.class);

	private static final String LOG_TAG = "JAVASCRIPT_SERVICE_GENERATOR"; //$NON-NLS-1$

	private static final String PARAMETER_TABLE_NAME = "tableName"; //$NON-NLS-1$
	private static final String PARAMETER_TABLE_TYPE = "tableType"; //$NON-NLS-1$
	private static final String PARAMETER_ENTITY_NAME = "entityName"; //$NON-NLS-1$
	private static final String PARAMETER_TABLE_COLUMNS = "tableColumns"; //$NON-NLS-1$
	private static final String PARAMETER_TABLE_COLUMNS_WITHOUT_KEYS = "tableColumnsWithoutKeys"; //$NON-NLS-1$
	private static final String PARAMETER_PRIMARY_KEY = "primaryKey"; //$NON-NLS-1$

	private static final String PARAMETER_INTEGER = "INTEGER"; //$NON-NLS-1$
	private static final String PARAMETER_BIGINT = "BIGINT"; //$NON-NLS-1$
	private static final String PARAMETER_SMALLINT = "SMALLINT"; //$NON-NLS-1$
	private static final String PARAMETER_FLOAT = "FLOAT"; //$NON-NLS-1$
	private static final String PARAMETER_DOUBLE = "DOUBLE"; //$NON-NLS-1$
	private static final String PARAMETER_VARCHAR = "VARCHAR"; //$NON-NLS-1$
	private static final String PARAMETER_CHAR = "CHAR"; //$NON-NLS-1$
	private static final String PARAMETER_DATE = "DATE"; //$NON-NLS-1$
	private static final String PARAMETER_TIME = "TIME"; //$NON-NLS-1$
	private static final String PARAMETER_TIMESTAMP = "TIMESTAMP"; //$NON-NLS-1$
	private static final String PARAMETER_BOOLEAN = "BOOLEAN"; //$NON-NLS-1$

	private JavascriptServiceTemplateModel model;

	/**
	 * Constructor
	 *
	 * @param model
	 */
	public JavascriptServiceTemplateGenerator(JavascriptServiceTemplateModel model) {
		this.model = model;
	}

	@Override
	protected Map<String, Object> prepareParameters() {
		Map<String, Object> parameters = super.prepareParameters();
		parameters.put(PARAMETER_TABLE_NAME, model.getTableName());
		parameters.put(PARAMETER_TABLE_TYPE, model.getTableType());
		parameters.put(PARAMETER_ENTITY_NAME, CommonUtils.toCamelCase(model.getTableName()));
		parameters.put(PARAMETER_TABLE_COLUMNS, model.getTableColumns());
		parameters.put(PARAMETER_TABLE_COLUMNS_WITHOUT_KEYS, getTableColumnsWithoutKeys(model.getTableColumns()));
		parameters.put(PARAMETER_PRIMARY_KEY, getPrimaryKey());

		parameters.put(PARAMETER_INTEGER, java.sql.Types.INTEGER);
		parameters.put(PARAMETER_BIGINT, java.sql.Types.BIGINT);
		parameters.put(PARAMETER_SMALLINT, java.sql.Types.SMALLINT);
		parameters.put(PARAMETER_FLOAT, java.sql.Types.REAL);
		parameters.put(PARAMETER_DOUBLE, java.sql.Types.DOUBLE);
		// parameters.put("REAL", java.sql.Types.REAL);
		// parameters.put("DECIMAL", java.sql.Types.DECIMAL);
		// parameters.put("NUMERIC", java.sql.Types.NUMERIC);
		parameters.put(PARAMETER_VARCHAR, java.sql.Types.VARCHAR);
		parameters.put(PARAMETER_CHAR, java.sql.Types.CHAR);
		parameters.put(PARAMETER_DATE, java.sql.Types.DATE);
		parameters.put(PARAMETER_TIME, java.sql.Types.TIME);
		parameters.put(PARAMETER_TIMESTAMP, java.sql.Types.TIMESTAMP);
		parameters.put(PARAMETER_BOOLEAN, java.sql.Types.BOOLEAN);
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
		return model.normalizeEscapes(bytes);
	}
}
