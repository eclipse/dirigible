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

package org.eclipse.dirigible.runtime.js.debug;

import java.beans.PropertyChangeSupport;

import org.eclipse.dirigible.repository.ext.debug.IDebugProtocol;

public class DebugProtocolHandler extends PropertyChangeSupport implements IDebugProtocol {

	private static final long serialVersionUID = 5008236386408550199L;

	public DebugProtocolHandler() {
		super(new Object());
	}

}
