/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.db.preferences;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.eclipse.dirigible.ide.common.CommonParameters;
import org.eclipse.dirigible.repository.datasource.DataSourceFacade;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class DatabaseAttributesPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private static final Logger logger = Logger.getLogger(DatabaseAttributesPreferencePage.class);

	private static final long serialVersionUID = -877187045002896492L;

	public DatabaseAttributesPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
	}

	@Override
	protected void createFieldEditors() {
		Text text = null;

		DataSource dataSource = DataSourceFacade.getInstance().getDataSource(CommonParameters.getRequest());
		Connection connection = null;
		try {
			try {
				connection = dataSource.getConnection();
				DatabaseMetaData dmd = connection.getMetaData();

				StringFieldEditor databaseProductNameField = new StringFieldEditor("DATABASE_PRODUCT_NAME", "&Product Name:", getFieldEditorParent());
				text = databaseProductNameField.getTextControl(getFieldEditorParent());
				text.setEditable(false);
				String productName = dmd.getDatabaseProductName();
				text.setText(productName != null ? productName : DatabasePreferencePage.N_A);
				addField(databaseProductNameField);

				StringFieldEditor databaseProductVersionField = new StringFieldEditor("DATABASE_PRODUCT_VERSION", "&Product Version:",
						getFieldEditorParent());
				text = databaseProductVersionField.getTextControl(getFieldEditorParent());
				text.setEditable(false);
				String productVersion = dmd.getDatabaseProductVersion();
				text.setText(productVersion != null ? productVersion : DatabasePreferencePage.N_A);
				addField(databaseProductVersionField);

				StringFieldEditor databaseMinorVersionField = new StringFieldEditor("DATABASE_MINOR_VERSION", "&Minor Version:",
						getFieldEditorParent());
				text = databaseMinorVersionField.getTextControl(getFieldEditorParent());
				text.setEditable(false);
				String productVersionMinor = dmd.getDatabaseMinorVersion() + "";
				text.setText(productVersionMinor);
				addField(databaseMinorVersionField);

				StringFieldEditor databaseMajorVersionField = new StringFieldEditor("DATABASE_MAJOR_VERSION", "&Major Version:",
						getFieldEditorParent());
				text = databaseMajorVersionField.getTextControl(getFieldEditorParent());
				text.setEditable(false);
				String productVersionMajor = dmd.getDatabaseMajorVersion() + "";
				text.setText(productVersionMajor);
				addField(databaseMajorVersionField);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}

	}

	@Override
	public void init(IWorkbench workbench) {
		super.initialize();
	}

}
