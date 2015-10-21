/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.db.viewer.views;

import org.eclipse.dirigible.ide.editor.ace.EditorWidget;
import org.eclipse.dirigible.ide.editor.text.editor.AbstractTextEditorWidget;
import org.eclipse.swt.custom.SashForm;

public class SQLConsole extends AbstractSQLConsole {

	@Override
	protected AbstractTextEditorWidget createSQLEditorWidget(SashForm sashForm) {
		return new EditorWidget(sashForm);
	}

}
