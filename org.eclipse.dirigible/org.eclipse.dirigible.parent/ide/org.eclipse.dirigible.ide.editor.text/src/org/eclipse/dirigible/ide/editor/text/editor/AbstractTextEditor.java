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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.EditorPart;

import org.eclipse.dirigible.repository.logging.Logger;

public abstract class AbstractTextEditor extends EditorPart {

	private static final String COLON = ": "; //$NON-NLS-1$

	private static final String SAVE_ERROR = Messages.AbstractTextEditor_SAVE_ERROR;

	private static final String CANNOT_SAVE_DOCUMENT = Messages.AbstractTextEditor_CANNOT_SAVE_DOCUMENT;

	private static final Logger logger = Logger
			.getLogger(AbstractTextEditor.class);

	private boolean isDirty;

	@Override
	public void doSave(IProgressMonitor monitor) {
		IEditorInput input = getEditorInput();
		String contents = getEditorContents();
		IContentProvider contentProvider = getContentProvider(input);
		if (contentProvider != null && contents != null) {
			try {
				contentProvider.save(monitor, input, contents, true);
				setDirty(false);
			} catch (ContentProviderException e) {
				logger.error(CANNOT_SAVE_DOCUMENT, e);
				MessageDialog.openError(null, SAVE_ERROR, CANNOT_SAVE_DOCUMENT
						+ COLON + e.getMessage());
			}
		}

	}

	protected void setDirty(boolean b) {
		this.isDirty = b;
		firePropertyChange(PROP_DIRTY);
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	@Override
	public void setFocus() {
		Control control = getEditorControl();
		if (control != null) {
			control.setFocus();
		}
	}

	protected abstract String getEditorContents();

	protected abstract Control getEditorControl();

	protected IContentProvider getContentProvider(IEditorInput input) {
		return ContentProviderFactory.getInstance().getContentProvider(input);
	}
}
