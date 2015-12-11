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

import java.util.Set;

import org.eclipse.dirigible.repository.datasource.DataSourceFacade;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class DatasourcesPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private static final long serialVersionUID = 4250022466641459908L;

	public DatasourcesPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
	}

	@Override
	protected void createFieldEditors() {

		Set<String> names = DataSourceFacade.getInstance().getNamedDataSourcesNames();

		DatasourcesListEditor datasourcesListField = new DatasourcesListEditor("DATASOURCES_LIST", "&Data Sources:", getFieldEditorParent());

		List list = datasourcesListField.getListControl(getFieldEditorParent());

		addField(datasourcesListField);

		datasourcesListField.setInitalized(false);
		for (String name : names) {
			list.add(name);
		}
		datasourcesListField.setInitalized(true);
	}

	@Override
	public void init(IWorkbench workbench) {
		super.initialize();
	}

}
