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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

public class TextEditor extends AbstractTextEditor {
	
	public static final String ID = "org.eclipse.dirigible.ide.editor.text.editor.TextEditor";

	private static final String CANNOT_LOAD_DOCUMENT = Messages.TextEditor_CANNOT_LOAD_DOCUMENT;
	private static final String ERROR = Messages.TextEditor_ERROR;
	private static final String EDITOR_INPUT_CANNOT_BE_NULL = Messages.TextEditor_EDITOR_INPUT_CANNOT_BE_NULL;
	private Text text = null;

	public TextEditor() {

	}

	@Override
	public void doSaveAs() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		if (input == null) {
			throw new PartInitException(EDITOR_INPUT_CANNOT_BE_NULL);
		}

		setSite(site);
		setInput(input);
		setPartName(input.getName());
		setDirty(false);
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("serial")
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());

		final TextEditor textEditor = this;
		// This is the proper way that it should be done, however, I believe
		// it also requires that undo and redo are available.
		// int readOnlyFlag = getInput().readOnly() ? SWT.READ_ONLY : SWT.NONE;

		// int readOnlyFlag = SWT.READ_ONLY;
		// text = new Text(parent, SWT.MULTI | readOnlyFlag);
		text = createTextControl(parent);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		try {
			IEditorInput input = getEditorInput();
			String content = getContentProvider(input).getContent(input);
			text.setText(content);
		} catch (ContentProviderException e) {
			MessageDialog.openError(null, ERROR, CANNOT_LOAD_DOCUMENT);
		}

		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent event) {
				textEditor.setDirty(true);
			}
		});

	}

	protected Text createTextControl(Composite parent) {
		return new Text(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
	}

	@Override
	public void dispose() {
		text = null;
		super.dispose();
	}

	@Override
	protected String getEditorContents() {
		return text.getText();
	}

	@Override
	protected Control getEditorControl() {
		return text;
	}
	
	@Override
	public void setPartName(String partName) {
		super.setPartName(partName);
	}
}
