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

package org.eclipse.dirigible.ide.ui.widget.extbrowser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Wrapper of the {@link Browser} class which allows refreshes and setting of
 * <code>null</code> URLs.
 * 
 */
public class ExtendedBrowser {

	private static final String EMPTY_HTML = "<html><body></body></html>"; //$NON-NLS-1$

	private final Composite browserHolder;

	private Browser browser = null;

	public ExtendedBrowser(Composite composite, int style) {
		browserHolder = new Composite(composite, style);
		browserHolder.setLayout(new FillLayout());

		browser = new Browser(browserHolder, SWT.NONE);
		browser.setText(EMPTY_HTML);
	}

	public Control getControl() {
		return browserHolder;
	}

	public void setFocus() {
		if (browser != null) {
			browser.setFocus();
		}
	}

	/**
	 * Returns the URL to which the browser points, or <code>null</code> if the
	 * browser has not been directed to any location.
	 */
	public String getUrl() {
		return (browser != null) ? browser.getUrl() : null;
	}

	/**
	 * Requests that the browser displays the page at the specified location.
	 */
	public void setUrl(String url) {
		if (browser == null) {
			return;
		}
		if (url == null) {
			browser.setText(EMPTY_HTML);
			return;
		}
		// Recreating the browser is the only
		// way to refresh the same page.
		if (url.equals(browser.getUrl())) {
			Composite parent = browser.getParent();
			int style = browser.getStyle();
			browser.dispose();
			browser = new Browser(parent, style);
			parent.layout();
		}
		browser.setUrl(url);
	}

	/**
	 * Refreshes the displayed web page.
	 */
	public void refresh() {
		setUrl(getUrl());
	}

	/**
	 * Requests that the browser displays the specified html text.
	 */
	public void setContent(String html) {
		if (browser != null) {
			if (html != null) {
				browser.setText(html);
			} else {
				browser.setText(EMPTY_HTML);
			}
		}
	}

}
