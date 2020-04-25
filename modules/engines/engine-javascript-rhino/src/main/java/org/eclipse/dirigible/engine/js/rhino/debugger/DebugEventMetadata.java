/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.engine.js.rhino.debugger;

import java.util.List;

import org.eclipse.dirigible.engine.js.debug.model.BreakpointsMetadata;
import org.eclipse.dirigible.engine.js.debug.model.DebugSessionMetadata;
import org.eclipse.dirigible.engine.js.debug.model.LinebreakMetadata;
import org.eclipse.dirigible.engine.js.debug.model.VariableValuesMetadata;

public class DebugEventMetadata {
	
	public String type;
	
	public List<DebugSessionMetadata> sessions;
	
	public VariableValuesMetadata variables;
	
	public BreakpointsMetadata breakpoints;
	
	public LinebreakMetadata linebreak;
	
	public DebugSessionMetadata session;

}
