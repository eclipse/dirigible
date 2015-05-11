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

package org.eclipse.dirigible.ide.db.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class DatabasePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static final String N_A = "n/a";
	
	private static final long serialVersionUID = 4250022466641459908L;
	
	public DatabasePreferencePage() {
		super(FieldEditorPreferencePage.GRID);
	}
	
	@Override
	protected void createFieldEditors() {
		//
	}

	@Override
	public void init(IWorkbench workbench) {
		super.initialize();
	}

}
