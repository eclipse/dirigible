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

package org.eclipse.dirigible.ide.editor.text.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public abstract class AbstractTextEditorWidget extends Composite {

	private static final long serialVersionUID = 3624167150503876670L;

	AbstractTextEditorWidget(Composite parent) {
		super(parent, SWT.NONE);
	}

	public AbstractTextEditorWidget(Composite parent, int style) {
		super(parent, style);
	}

	public abstract void setText(final String text, final EditorMode mode, boolean readOnly,
			boolean breakpointsEnabled, int row);
	
	public abstract String getText();
	
}
