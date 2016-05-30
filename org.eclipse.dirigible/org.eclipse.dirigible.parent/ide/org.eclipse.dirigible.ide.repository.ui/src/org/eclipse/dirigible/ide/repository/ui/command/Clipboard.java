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

package org.eclipse.dirigible.ide.repository.ui.command;

import java.util.ArrayList;

import org.eclipse.dirigible.ide.common.CommonIDEParameters;

public class Clipboard extends ArrayList<Object> {

	private static final long serialVersionUID = 6272208252465388075L;

	private static final String DIRIGIBLE_CLIPBOARD = "dirigible.clipboard"; //$NON-NLS-1$

	private String command;

	public static Clipboard getInstance() {
		Clipboard clipboard = (Clipboard) CommonIDEParameters.getObject(DIRIGIBLE_CLIPBOARD);
		if (clipboard == null) {
			clipboard = new Clipboard();
			CommonIDEParameters.setObject(DIRIGIBLE_CLIPBOARD, clipboard);
		}
		return clipboard;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

}
