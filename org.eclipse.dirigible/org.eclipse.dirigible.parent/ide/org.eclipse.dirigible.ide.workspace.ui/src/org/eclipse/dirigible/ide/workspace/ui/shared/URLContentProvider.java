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

package org.eclipse.dirigible.ide.workspace.ui.shared;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class URLContentProvider implements IContentProvider {

	private static final String INVALID_URL_ADDRESS = Messages.URLContentProvider_INVALID_URL_ADDRESS;
	private String url;

	public URLContentProvider() {
		this(""); //$NON-NLS-1$
	}

	public URLContentProvider(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@SuppressWarnings("unused")
	public IValidationStatus validate() {
		try {
			new URL(url);
			return ValidationStatus.createOk();
		} catch (MalformedURLException ex) {
			return ValidationStatus.createError(INVALID_URL_ADDRESS);
		}
	}

	public InputStream getContent() throws IOException {
		return new URL(url).openStream();
	}

}
