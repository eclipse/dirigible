/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.engine.js.debug.model;

public interface IDebugView {
	
	public void register(DebugSessionModel session);

	public void finish(DebugSessionModel session);

	public void onLineChange(LinebreakMetadata linebreak, DebugSessionModel session);

	public void refreshVariables();
	
	public void refreshBreakpoints();

}
