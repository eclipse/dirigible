/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.js.debug.model;

public interface IDebugController extends IDebugView {
	
//	public void refresh();

	public void stepInto();

	public void stepOver();

	public void continueExecution();

	public void skipAllBreakpoints();

	public void setBreakpoint(String path, int row);

	public void removeBreakpoint(String path, int row);

	public void removeAllBreakpoints();

//	public void clearAllBreakpoints(String path);

}
