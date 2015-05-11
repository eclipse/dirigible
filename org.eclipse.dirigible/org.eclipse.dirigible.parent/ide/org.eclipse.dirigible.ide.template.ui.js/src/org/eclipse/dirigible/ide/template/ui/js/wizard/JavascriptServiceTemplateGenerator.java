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

package org.eclipse.dirigible.ide.template.ui.js.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateGenerator;
import org.eclipse.dirigible.repository.api.ICommonConstants;

public class JavascriptServiceTemplateGenerator extends TemplateGenerator {

	private static final String LOG_TAG = "JAVASCRIPT_SERVICE_GENERATOR"; //$NON-NLS-1$

	private JavascriptServiceTemplateModel model;

	public JavascriptServiceTemplateGenerator(
			JavascriptServiceTemplateModel model) {
		this.model = model;
	}

	@Override
	protected Map<String, Object> prepareParameters() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("projectName", model.getProjectName()); //$NON-NLS-1$
		if (model.getPackageName() != null) {
			parameters.put("packageName", model.getPackageName()); //$NON-NLS-1$
		} else {
			parameters.put("packageName", constructPackageName()); //$NON-NLS-1$
		}
		parameters.put("tableName", model.getTableName()); //$NON-NLS-1$
		parameters.put("tableType", model.getTableType()); //$NON-NLS-1$
		parameters.put("entityName", toCamelCase(model.getTableName())); //$NON-NLS-1$
		parameters.put("tableColumns", model.getTableColumns()); //$NON-NLS-1$
		parameters.put("tableColumnsWithoutKeys", //$NON-NLS-1$
				getTableColumnsWithoutKeys(model.getTableColumns()));
		parameters.put("fileName", model.getFileName()); //$NON-NLS-1$
		parameters.put("fileNameNoExtension", model.getFileNameNoExtension()); //$NON-NLS-1$

		parameters.put("INTEGER", java.sql.Types.INTEGER); //$NON-NLS-1$
		parameters.put("BIGINT", java.sql.Types.BIGINT); //$NON-NLS-1$
		parameters.put("SMALLINT", java.sql.Types.SMALLINT); //$NON-NLS-1$

		parameters.put("REAL", java.sql.Types.REAL); //$NON-NLS-1$
		parameters.put("DOUBLE", java.sql.Types.DOUBLE); //$NON-NLS-1$
		// parameters.put("REAL", java.sql.Types.REAL);
		// parameters.put("DECIMAL", java.sql.Types.DECIMAL);
		// parameters.put("NUMERIC", java.sql.Types.NUMERIC);

		parameters.put("VARCHAR", java.sql.Types.VARCHAR); //$NON-NLS-1$
		parameters.put("CHAR", java.sql.Types.CHAR); //$NON-NLS-1$

		parameters.put("DATE", java.sql.Types.DATE); //$NON-NLS-1$
		parameters.put("TIME", java.sql.Types.TIME); //$NON-NLS-1$
		parameters.put("TIMESTAMP", java.sql.Types.TIMESTAMP); //$NON-NLS-1$

//		parameters.put("CLOB", java.sql.Types.CLOB); //$NON-NLS-1$
//		parameters.put("BLOB", java.sql.Types.BLOB); //$NON-NLS-1$

		return parameters;
	}
	
	public String constructPackageName() {
		StringBuilder result = new StringBuilder();
		IPath location = new Path(model.getTargetContainer());
		if (location.segmentCount() > 2) {
			for (int i = 2; i < location.segmentCount(); i++) {
				result.append(location.segment(i) + ICommonConstants.SEPARATOR);
			}
			result.delete(result.length() - ICommonConstants.SEPARATOR.length(), result.length());
		} else {
			result.append(location.segment(0));
		}
		String constructedPackage = result.toString().replace(ICommonConstants.SEPARATOR, ".");
		return constructedPackage;
	}


	private TableColumn[] getTableColumnsWithoutKeys(TableColumn[] tableColumns) {
		if (tableColumns == null) {
			return null;
		}
		List<TableColumn> list = new ArrayList<TableColumn>();
		for (int i = 0; i < tableColumns.length; i++) {
			TableColumn tableColumn = tableColumns[i];
			if (!tableColumn.isKey()) {
				list.add(tableColumn);
			}
		}
		return list.toArray(new TableColumn[] {});
	}

	private Object toCamelCase(String input) {
		if (input == null) {
			return null;
		}
		StringBuffer result = new StringBuffer();
		result.append(input.substring(0, 1).toUpperCase());
		result.append(input.substring(1).toLowerCase());
		return result.toString();
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
		String content = new String(bytes);
		content = content.replace("\\$", "$"); //$NON-NLS-1$ //$NON-NLS-2$
		content = content.replace("\\{", "{"); //$NON-NLS-1$ //$NON-NLS-2$
		content = content.replace("\\}", "}"); //$NON-NLS-1$ //$NON-NLS-2$
		content = content.replace("\\.", "."); //$NON-NLS-1$ //$NON-NLS-2$
		byte[] result = content.getBytes();
		return result;
	}

	@Override
	public void generate() throws Exception {
		super.generate();

		if ("/org/eclipse/dirigible/ide/template/ui/js/templates/database-crud.js" //$NON-NLS-1$
				.equals(model.getTemplate().getLocation())) {
			generateFile(
					"/org/eclipse/dirigible/ide/template/ui/js/templates/database-crud.entity", //$NON-NLS-1$
					model.getTargetLocation(), model.getFileNameNoExtension()
							+ ".entity"); //$NON-NLS-1$
			generateFile(
					"/org/eclipse/dirigible/ide/template/ui/js/templates/database-crud-lib.js", //$NON-NLS-1$
					model.getTargetLocation(), model.getFileNameNoExtension()
							+ "_lib.js"); //$NON-NLS-1$

		}

		if ("/org/eclipse/dirigible/ide/template/ui/js/templates/ruby-service.rb" //$NON-NLS-1$
				.equals(model.getTemplate().getLocation())) {
			generateFile(
					"/org/eclipse/dirigible/ide/template/ui/js/templates/ruby-module.rb", //$NON-NLS-1$
					model.getTargetLocation(), "module1.rb"); //$NON-NLS-1$

		}
		
		if ("/org/eclipse/dirigible/ide/template/ui/js/templates/groovy-service.groovy" //$NON-NLS-1$
				.equals(model.getTemplate().getLocation())) {
			generateFile(
					"/org/eclipse/dirigible/ide/template/ui/js/templates/groovy-module.groovy", //$NON-NLS-1$
					model.getTargetLocation(), "module1.groovy"); //$NON-NLS-1$

		}
	}
}
