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

package org.eclipse.dirigible.ide.designer.ui;

import org.eclipse.dirigible.ide.editor.text.command.TextEditorHandler;

public class DesignerEditorHandler extends TextEditorHandler {

	private static final String EDITOR_ID = "org.eclipse.dirigible.ide.editor.DesignerEditor"; //$NON-NLS-1$

	@Override
	protected String getEditorId() {
		return EDITOR_ID;
	}
}
