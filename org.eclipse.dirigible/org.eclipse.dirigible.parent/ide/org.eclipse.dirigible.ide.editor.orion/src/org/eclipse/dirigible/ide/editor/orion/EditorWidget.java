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

package org.eclipse.dirigible.ide.editor.orion;

import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.dirigible.ide.common.CommonParameters;
import org.eclipse.dirigible.ide.common.CommonUtils;
import org.eclipse.dirigible.ide.debug.model.DebugModel;
import org.eclipse.dirigible.ide.debug.model.DebugModelFacade;
import org.eclipse.dirigible.ide.editor.text.editor.AbstractTextEditorWidget;
import org.eclipse.dirigible.ide.editor.text.editor.EditorMode;
import org.eclipse.dirigible.ide.editor.text.editor.IEditorWidgetListener;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.logging.Logger;

@SuppressWarnings("unused")
public class EditorWidget extends AbstractTextEditorWidget {

	private static final long serialVersionUID = -8881201238299386468L;

	private static final Logger logger = Logger.getLogger(EditorWidget.class);
	private static final String SCRIPT_EVALUATION_FAILED = Messages.EditorWidget_SCRIPT_EVALUATION_FAILED;
	private static final int EVALUATE_ATTEMPTS = 15;
	private static final String EDITOR_URL = "/orion/examples/editor/embeddededitor.html"; //$NON-NLS-1$
	private Browser browser;
	private String text;
	private IEditorWidgetListener listener;
	private String mode;
	private boolean loaded;

	private boolean disabledForReadOnly;

	private boolean readOnly;

	private boolean breakpointsEnabled;

	private int row;

	public EditorWidget(final Composite parent) {
		this(parent, false);
	}

	@SuppressWarnings("serial")
	public EditorWidget(final Composite parent, final boolean javaScriptEditor) {
		super(parent, SWT.NONE);
		super.setLayout(new FillLayout());

		browser = new Browser(this, SWT.NONE);
		browser.setUrl(CommonParameters.getContextPath() + EDITOR_URL);
		browser.addProgressListener(new ProgressListener() {

			@Override
			public void completed(final ProgressEvent event) {
				loaded = true;
				updateWidgetContents();
				if (javaScriptEditor) {
					DebugModel debugModel = DebugModelFacade.getActiveDebugModel();

					if (debugModel != null
							&& debugModel.getCurrentLineBreak() != null) {
						String filePath = debugModel.getCurrentLineBreak().getFullPath();
						String path = CommonUtils.formatToRuntimePath(
								ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES, filePath);
						int[] breakpoints = debugModel.getBreakpointsMetadata()
								.getBreakpoints(path);

						loadBreakpoints(breakpoints);
					}
				}
			}

			@Override
			public void changed(final ProgressEvent event) {
				//
			}
		});

		// DO NOT REMOVE THIS
		new BrowserFunction(browser, "saveCalled") { //$NON-NLS-1$
			@Override
			public Object function(final Object[] arguments) {
				if (listener != null) {
					listener.save();
				}
				return null;
			}
		};

		// DO NOT REMOVE THIS
		new BrowserFunction(browser, "dirtyChanged") { //$NON-NLS-1$
			@Override
			public Object function(final Object[] arguments) {
				if (listener != null) {
					listener.dirtyStateChanged((Boolean) arguments[0]);
				}
				return null;
			}
		};

		// DO NOT REMOVE THIS
		new BrowserFunction(browser, "setBreakpoint") { //$NON-NLS-1$
			@Override
			public Object function(final Object[] arguments) {
				if ((listener != null) && (arguments[0] != null) && (arguments[0] instanceof Number)) {
					listener.setBreakpoint(((Number) arguments[0]).intValue());
				}
				return null;
			}
		};

		// DO NOT REMOVE THIS
		new BrowserFunction(browser, "clearBreakpoint") { //$NON-NLS-1$
			@Override
			public Object function(final Object[] arguments) {
				if ((listener != null) && (arguments[0] != null) && (arguments[0] instanceof Number)) {
					listener.clearBreakpoint(((Number) arguments[0]).intValue());
				}
				return null;
			}
		};

	}

	public void setListener(final IEditorWidgetListener listener) {
		this.listener = listener;
	}

	public void setText(final String text, final EditorMode mode, boolean readOnly,
			boolean breakpointsEnabled, int row) {
		this.text = text;
		this.mode = mode.getName();
		if ("javascript".equals(this.mode)) {
			this.mode = "js"; // orion specific
		}
		this.readOnly = readOnly;
		this.breakpointsEnabled = breakpointsEnabled;
		this.row = row;
		if (loaded) {
			updateWidgetContents();
		}
	}

	public String getText() {
		return (String) browser.evaluate("return getText();"); //$NON-NLS-1$
	}

	public void setDirty(final boolean dirty) {
		execute("setDirty", dirty); //$NON-NLS-1$
	}

	public void setDebugRow(final int row) {
		execute("setDebugRow", row); //$NON-NLS-1$
	}

	public void loadBreakpoints(int[] breakpoints) {
		for (int i = 0; i < breakpoints.length; i++) {
			int breakpoint = breakpoints[i];
			execute("loadBreakpoint", breakpoint); //$NON-NLS-1$
		}
	}

	private void updateWidgetContents() {
		evaluate("setText", text, mode, readOnly, breakpointsEnabled, row); //$NON-NLS-1$
	}

	public void setMode(final String mode) {
		evaluate("setMode", mode); //$NON-NLS-1$
	}

	public void setReadOnly(final boolean readOnly) {
		if (disabledForReadOnly) {
			execute("setReadOnly", false); //$NON-NLS-1$
			return;
		}
		execute("setReadOnly", readOnly); //$NON-NLS-1$
	}

	public void setBreakpointsEnabled(final boolean status) {
		evaluate("setBreakpointsEnabled", status); //$NON-NLS-1$
	}

	private void execute(final String function, final Object... arguments) {
		browser.execute(buildFunctionCall(function, arguments));
	}

	private Object evaluate(final String function, final Object... arguments) {
		final String script = buildFunctionCall(function, arguments);
		for (int i = 0; i < EVALUATE_ATTEMPTS; i++) {
			try {
				return browser.evaluate(script);
			} catch (final Exception ex) {
				logger.debug(ex.getMessage(), ex);
			}
		}

		throw new IllegalStateException(SCRIPT_EVALUATION_FAILED + script);
	}

	private String buildFunctionCall(final String function, final Object... arguments) {
		final StringBuilder call = new StringBuilder();
		call.append(function).append('(');
		if (arguments != null) {
			for (final Object argument : arguments) {
				String strArg = null;
				if (argument instanceof String) {
					strArg = prepareStringArgument((String) argument);
				} else {
					strArg = String.valueOf(argument);
				}
				call.append(strArg).append(","); //$NON-NLS-1$
			}
			if (arguments.length > 0) {
				call.deleteCharAt(call.length() - 1);
			}
		}
		call.append(')');

		return call.toString();
	}

	private String prepareStringArgument(final String argument) {
		return "'" + StringEscapeUtils.escapeJavaScript(argument) + "'"; //$NON-NLS-1$ //$NON-NLS-2$
	}

}
