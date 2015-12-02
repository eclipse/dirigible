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

import org.eclipse.dirigible.ide.common.CommonParameters;
import org.eclipse.dirigible.repository.datasource.DataSourceFacade;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class DatabaseDriverPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private static final long serialVersionUID = -877187045002896492L;

	public DatabaseDriverPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
	}

	@Override
	protected void createFieldEditors() {
		Text text = null;

		StringFieldEditor databaseDriverNameField = new StringFieldEditor(DataSourceFacade.DATABASE_DRIVER_NAME, "&Driver Name:",
				getFieldEditorParent());
		text = databaseDriverNameField.getTextControl(getFieldEditorParent());
		text.setEditable(false);
		text.setText(CommonParameters.get(DataSourceFacade.DATABASE_DRIVER_NAME) != null ? CommonParameters.get(DataSourceFacade.DATABASE_DRIVER_NAME)
				: DatabasePreferencePage.N_A);
		addField(databaseDriverNameField);

		StringFieldEditor databaseMinorVersionField = new StringFieldEditor(DataSourceFacade.DATABASE_DRIVER_MINOR_VERSION, "&Minor Version:",
				getFieldEditorParent());
		text = databaseMinorVersionField.getTextControl(getFieldEditorParent());
		text.setEditable(false);
		text.setText(CommonParameters.get(DataSourceFacade.DATABASE_DRIVER_MINOR_VERSION) != null
				? CommonParameters.get(DataSourceFacade.DATABASE_DRIVER_MINOR_VERSION) : DatabasePreferencePage.N_A);
		addField(databaseMinorVersionField);

		StringFieldEditor databaseMajorVersionField = new StringFieldEditor(DataSourceFacade.DATABASE_DRIVER_MAJOR_VERSION, "&Major Version:",
				getFieldEditorParent());
		text = databaseMajorVersionField.getTextControl(getFieldEditorParent());
		text.setEditable(false);
		text.setText(CommonParameters.get(DataSourceFacade.DATABASE_DRIVER_MAJOR_VERSION) != null
				? CommonParameters.get(DataSourceFacade.DATABASE_DRIVER_MAJOR_VERSION) : DatabasePreferencePage.N_A);
		addField(databaseMajorVersionField);

		StringFieldEditor databaseConnectionClassNameField = new StringFieldEditor(DataSourceFacade.DATABASE_CONNECTION_CLASS_NAME,
				"&Connection Class Name:", getFieldEditorParent());
		text = databaseConnectionClassNameField.getTextControl(getFieldEditorParent());
		text.setEditable(false);
		text.setText(CommonParameters.get(DataSourceFacade.DATABASE_CONNECTION_CLASS_NAME) != null
				? CommonParameters.get(DataSourceFacade.DATABASE_CONNECTION_CLASS_NAME) : DatabasePreferencePage.N_A);
		addField(databaseConnectionClassNameField);
	}

	@Override
	public void init(IWorkbench workbench) {
		super.initialize();
	}

}
