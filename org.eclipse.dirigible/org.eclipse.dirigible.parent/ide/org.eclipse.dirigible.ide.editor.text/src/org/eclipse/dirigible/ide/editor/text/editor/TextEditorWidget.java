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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class TextEditorWidget extends AbstractTextEditorWidget {
	
	private static final long serialVersionUID = -4840499933475637489L;
	
	private Text text;

	public TextEditorWidget(Composite parent, int style) {
		super(parent, style);
		super.setLayout(new FillLayout());

		this.text = new Text(this, SWT.NONE);
	}

	public TextEditorWidget(Composite parent) {
		this(parent, SWT.NONE);
	}

	@Override
	public void setText(String text, EditorMode mode, boolean readOnly,
			boolean breakpointsEnabled, int row) {
		this.text.setText(text);
	}

	@Override
	public String getText() {
		return this.text.getText();
	}

}
