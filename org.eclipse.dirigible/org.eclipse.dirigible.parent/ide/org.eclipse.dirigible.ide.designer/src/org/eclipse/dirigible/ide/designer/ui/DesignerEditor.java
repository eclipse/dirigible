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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;

import org.eclipse.dirigible.ide.editor.text.editor.ContentProviderException;
import org.eclipse.dirigible.ide.editor.text.editor.TextEditor;
import org.eclipse.dirigible.repository.logging.Logger;

public class DesignerEditor extends TextEditor {

	private static final String EMPTY_HTML = "<!DOCTYPE html><html><head></head><body></body></html>"; //$NON-NLS-1$

	private static final String ERROR = Messages.DesignerEditor_Error;

	private static final String CANNOT_LOAD_DOCUMENT = Messages.DesignerEditor_Cannot_load_document;

	private static final Logger logger = Logger
			.getLogger(DesignerEditor.class);

	private DesignerEditorWidget text = null;

	/**
	 * {@inheritDoc}
	 */
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());

		final DesignerEditor designerEditor = this;
		IEditorInput input = getEditorInput();
		text = new DesignerEditorWidget(parent);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		try {
			String content = getContentProvider(input).getContent(input);
			if (content == null
					|| "".equals(content)) {
				content = EMPTY_HTML;
			}
			text.setText(content);
		} catch (ContentProviderException e) {
			logger.error(CANNOT_LOAD_DOCUMENT, e);
			MessageDialog.openError(null, ERROR, CANNOT_LOAD_DOCUMENT);
		}

		text.setListener(new IDesignerEditorWidgetListener() {

			@Override
			public void dirtyStateChanged(boolean dirty) {
				designerEditor.setDirty(dirty);
			}

			@Override
			public void save() {
				doSave(null);
			}
		});
	}

	@Override
	protected String getEditorContents() {
		return text != null ? text.getText() : null;
	}

	@Override
	protected Control getEditorControl() {
		return text;
	}

	@Override
	protected void setDirty(boolean b) {
		super.setDirty(b);
		if (text != null) {
			text.setDirty(b);
		}
	}

	@Override
	public void setPartName(String partName) {
		super.setPartName(partName);
	}
}
