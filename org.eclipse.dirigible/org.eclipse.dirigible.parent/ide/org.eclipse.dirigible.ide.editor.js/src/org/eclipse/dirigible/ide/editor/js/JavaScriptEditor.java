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

package org.eclipse.dirigible.ide.editor.js;

import org.eclipse.dirigible.ide.common.CommonUtils;
import org.eclipse.dirigible.ide.debug.model.DebugModelFacade;
import org.eclipse.dirigible.ide.editor.text.editor.ContentProviderException;
import org.eclipse.dirigible.ide.editor.text.editor.EditorMode;
import org.eclipse.dirigible.ide.editor.text.editor.IEditorWidgetListener;
import org.eclipse.dirigible.ide.editor.text.editor.TextEditor;
import org.eclipse.dirigible.ide.shared.editor.SourceFileEditorInput;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.ext.debug.DebugModel;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;

public class JavaScriptEditor extends TextEditor {

	private static final String ERROR = Messages.JavaScriptEditor_ERROR;

	private static final String CANNOT_LOAD_DOCUMENT = Messages.JavaScriptEditor_CANNOT_LOAD_DOCUMENT;

	private static final Logger logger = Logger.getLogger(JavaScriptEditor.class);

	private EditorWidget text = null;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite parent) {
		parent.setLayout(new GridLayout());

		final JavaScriptEditor textEditor = this;
		final IEditorInput input = getEditorInput();
		text = new EditorWidget(parent, true);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		String fileName = null;
		try {
			if (input instanceof SourceFileEditorInput) {
				SourceFileEditorInput sfei = (SourceFileEditorInput) input;
				fileName = sfei.getName();
			}
			final String content = getContentProvider(input).getContent(input);
			if (input instanceof SourceFileEditorInput) {
				SourceFileEditorInput sfei = (SourceFileEditorInput) input;
				fileName = sfei.getName();
				text.setText(content, getMode(), sfei.isReadOnly(), sfei.isBreakpointsEnabled(),
						sfei.getRow());
			} else {
				text.setText(content, getMode(), false, false, 0);
			}

		} catch (final ContentProviderException e) {
			logger.error(CANNOT_LOAD_DOCUMENT, e);
			if (fileName != null) {
				MessageDialog.openError(null, ERROR, CANNOT_LOAD_DOCUMENT + " " + fileName);
			} else {
				MessageDialog.openError(null, ERROR, CANNOT_LOAD_DOCUMENT);
			}
		}

		text.setListener(new IEditorWidgetListener() {

			@Override
			public void dirtyStateChanged(final boolean dirty) {
				textEditor.setDirty(dirty);
				if (dirty) {
					// IEditorInput editorInput = getEditorInput();
					// if (editorInput instanceof FileEditorInput) {
					// String path = ((FileEditorInput)
					// editorInput).getPath().toString();
					// String formatedPath = formatServicePath(path);
					// DebugModel.getInstance(null).clearAllBreakpoints(formatedPath);
					// }
				}
			}

			@Override
			public void save() {
				doSave(null);
			}

			@Override
			public void setBreakpoint(final int row) {
				IEditorInput editorInput = getEditorInput();
				if (editorInput instanceof FileEditorInput) {
					DebugModel debugModel = DebugModelFacade.getDebugModel();
					if (debugModel != null) {
						debugModel.getDebugController().setBreakpoint(CommonUtils.formatToRuntimePath(
								ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES,
								((FileEditorInput) editorInput).getPath().toString()), row);
					}
				}
			}

			@Override
			public void clearBreakpoint(final int row) {
				IEditorInput editorInput = getEditorInput();
				if (editorInput instanceof FileEditorInput) {
					DebugModel debugModel = DebugModelFacade.getDebugModel();
					if (debugModel != null) {
						String formatToRuntimePath = CommonUtils.formatToRuntimePath(
								ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES,
								((FileEditorInput) editorInput).getPath().toString());
						debugModel.getDebugController().clearBreakpoint(formatToRuntimePath, row);
					}
				}
			}
		});
	}

	private EditorMode getMode() {
		final IEditorInput input = getEditorInput();
		final String name = input.getName();
		final String ext = name.substring(name.lastIndexOf('.') + 1);

		return EditorMode.getByExtension(ext);
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
	protected void setDirty(final boolean b) {
		super.setDirty(b);
		if (text != null) {
			text.setDirty(b);
		}
	}

	public void setDebugRow(final int row) {
		if (text != null) {
			text.setDebugRow(row);
		}
	}

}
