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

import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.dirigible.ide.common.CommonParameters;
import org.eclipse.dirigible.repository.logging.Logger;

@SuppressWarnings("unused")
public class DesignerEditorWidget extends Composite {

	private static final long serialVersionUID = -8881201238299386468L;
	
	private static final Logger logger = Logger.getLogger(DesignerEditorWidget.class);
	private static final String SCRIPT_EVALUATION_FAILED = Messages.DesignerEditorWidget_Script_evaluation_failed;
	private static final int EVALUATE_ATTEMPTS = 5;
	private static final String EDITOR_URL = "/wysiwyg/wysiwyg.html"; //$NON-NLS-1$
	private Browser browser;
	private String text;
	private IDesignerEditorWidgetListener listener;
	private String mode;
	private boolean loaded;

	@SuppressWarnings("serial")
	public DesignerEditorWidget(Composite parent) {
		super(parent, SWT.NONE);
		super.setLayout(new FillLayout());

		browser = new Browser(this, SWT.NONE);
		browser.setUrl(CommonParameters.getContextPath() + EDITOR_URL);
		browser.addProgressListener(new ProgressListener() {

			@Override
			public void completed(ProgressEvent event) {
				loaded = true;
				updateWidgetContents();
			}

			@Override
			public void changed(ProgressEvent event) {
				//
			}
		});

		// DO NOT REMOVE THIS
		new BrowserFunction(browser, "saveCalled") { //$NON-NLS-1$
			@Override
			public Object function(Object[] arguments) {
				if (listener != null) {
					listener.save();
				}
				return null;
			}
		};

		// DO NOT REMOVE THIS
		new BrowserFunction(browser, "dirtyChanged") { //$NON-NLS-1$
			@Override
			public Object function(Object[] arguments) {
				if (listener != null) {
					listener.dirtyStateChanged((Boolean) arguments[0]);
				}
				return null;
			}
		};

	}

	public void setListener(IDesignerEditorWidgetListener listener) {
		this.listener = listener;
	}

	public void setText(String text) {
		this.text = text;
		if (loaded) {
			updateWidgetContents();
		}
	}

	public String getText() {
		return (String) browser.evaluate("return getEditorContent();"); //$NON-NLS-1$
	}

	public void setDirty(boolean dirty) {
		execute("setDirty", dirty); //$NON-NLS-1$
	}

	private void updateWidgetContents() {
		evaluate("window.setEditorContent", text); //$NON-NLS-1$
	}

//	public void setMode(String mode) {
//		evaluate("setMode", mode); //$NON-NLS-1$
//	}

	private void execute(String function, Object... arguments) {
		browser.execute(buildFunctionCall(function, arguments));
	}

	private Object evaluate(String function, Object... arguments) {
		String script = buildFunctionCall(function, arguments);
//		for (int i = 0; i < EVALUATE_ATTEMPTS; i++) {
//			try {
				return browser.evaluate(script);
//			} catch (Exception ex) {
//				logger.debug(ex.getMessage(), ex);
//			}
//		}

//		throw new IllegalStateException(SCRIPT_EVALUATION_FAILED + script);
	}

	private String buildFunctionCall(String function, Object... arguments) {
		StringBuilder call = new StringBuilder();
		call.append(function).append('(');
		if (arguments != null) {
			for (Object argument : arguments) {
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

	private String prepareStringArgument(String argument) {
		return "'" + StringEscapeUtils.escapeJavaScript(argument) + "'"; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
