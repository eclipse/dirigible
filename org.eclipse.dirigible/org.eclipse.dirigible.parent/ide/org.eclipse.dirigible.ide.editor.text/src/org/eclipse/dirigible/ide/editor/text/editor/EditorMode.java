/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.editor.text.editor;

import java.util.Arrays;

public enum EditorMode {

	JS("javascript", "js"), //$NON-NLS-1$ //$NON-NLS-2$
	SQL("sql", "sql"), //$NON-NLS-1$ //$NON-NLS-2$
	JSON("json", "json", "odata", "ws", "table", "view", "entity", "menu", "access", "extensionpoint", "extension", "command", "flow", "job"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$
	XML("xml", "xml", "xsd", "wsdl", "xsl", "xslt", "routes"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
	HTML("html", "html"), //$NON-NLS-1$ //$NON-NLS-2$
	CSS("css", "css"), //$NON-NLS-1$ //$NON-NLS-2$
	MARKDOWN("markdown", "markdown", "mdown", "mkdn", "md", "mkd", "mdwn"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
	TEXTILE("textile", "textile"), //$NON-NLS-1$ //$NON-NLS-2$
	TXT("text", "txt"), //$NON-NLS-1$ //$NON-NLS-2$
	RUBY("ruby", "rb"), //$NON-NLS-1$ //$NON-NLS-2$
	JAVA("java", "java"), //$NON-NLS-1$ //$NON-NLS-2$
	GROOVY("groovy", "groovy", "gvy", "gy", "gsh"), PROPERTIES("properties", "properties");

	private static final EditorMode DEFAULT_MODE = TXT;
	private String name;
	private String[] extensions;

	private EditorMode(String name, String... extensions) {
		this.name = name;
		this.extensions = extensions;
		Arrays.sort(this.extensions);
	}

	public String getName() {
		return name;
	}

	public static EditorMode getByExtension(String extension) {
		for (EditorMode mode : EditorMode.values()) {
			if (Arrays.binarySearch(mode.extensions, extension) >= 0) {
				return mode;
			}
		}

		return DEFAULT_MODE;
	}

}
