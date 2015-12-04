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

public class DatabaseDriverPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private static final long serialVersionUID = -877187045002896492L;

	private static final Logger logger = Logger.getLogger(DatabaseDriverPreferencePage.class);

	public DatabaseDriverPreferencePage() {
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

				StringFieldEditor databaseDriverNameField = new StringFieldEditor("DATABASE_DRIVER_NAME", "&Driver Name:", getFieldEditorParent());
				text = databaseDriverNameField.getTextControl(getFieldEditorParent());
				text.setEditable(false);
				String driverName = dmd.getDriverName();
				text.setText(driverName != null ? driverName : DatabasePreferencePage.N_A);
				addField(databaseDriverNameField);

				StringFieldEditor databaseMinorVersionField = new StringFieldEditor("DATABASE_DRIVER_MINOR_VERSION", "&Minor Version:",
						getFieldEditorParent());
				text = databaseMinorVersionField.getTextControl(getFieldEditorParent());
				text.setEditable(false);
				String driverMinor = dmd.getDriverMinorVersion() + "";
				text.setText(driverMinor);
				addField(databaseMinorVersionField);

				StringFieldEditor databaseMajorVersionField = new StringFieldEditor("DATABASE_DRIVER_MAJOR_VERSION", "&Major Version:",
						getFieldEditorParent());
				text = databaseMajorVersionField.getTextControl(getFieldEditorParent());
				text.setEditable(false);
				String driverMajor = dmd.getDriverMajorVersion() + "";
				text.setText(driverMajor);
				addField(databaseMajorVersionField);

				StringFieldEditor databaseConnectionClassNameField = new StringFieldEditor("DATABASE_CONNECTION_CLASS_NAME",
						"&Connection Class Name:", getFieldEditorParent());
				text = databaseConnectionClassNameField.getTextControl(getFieldEditorParent());
				text.setEditable(false);
				String driverClass = connection.getClass().getCanonicalName();
				text.setText(driverClass != null ? driverClass : DatabasePreferencePage.N_A);
				addField(databaseConnectionClassNameField);
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
