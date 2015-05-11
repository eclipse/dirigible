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

package org.eclipse.dirigible.repository.ext.debug;

public class DebugConstants {
	private static final String UNDERSCORE = "_";
	public static final String VIEW = "VIEW";
	public static final String DEBUG = "DEBUG";

	public static final String VIEW_REGISTER = VIEW + UNDERSCORE + "REGISTER";
	public static final String VIEW_FINISH = VIEW + UNDERSCORE + "FINISH";
	public static final String VIEW_SESSIONS = VIEW + UNDERSCORE + "SESSIONS";
	public static final String VIEW_ON_LINE_CHANGE = VIEW + UNDERSCORE + "ON_LINE_CHANGE";
	public static final String VIEW_VARIABLE_VALUES = VIEW + UNDERSCORE + "VARIABLE_VALUES";
	public static final String VIEW_BREAKPOINT_METADATA = VIEW + UNDERSCORE + "BREAKPOINT_METADATA";

	public static final String DEBUG_REFRESH = DEBUG + UNDERSCORE + "REFRESH";
	public static final String DEBUG_STEP_INTO = DEBUG + UNDERSCORE + "STEP_INTO";
	public static final String DEBUG_STEP_OVER = DEBUG + UNDERSCORE + "STEP_OVER";
	public static final String DEBUG_CONTINUE = DEBUG + UNDERSCORE + "CONTINUE";;
	public static final String DEBUG_SKIP_ALL_BREAKPOINTS = DEBUG + UNDERSCORE
			+ "SKIP_ALL_BREAKPOINTS";
	public static final String DEBUG_SET_BREAKPOINT = DEBUG + UNDERSCORE + "SET_BREAKPOINT";
	public static final String DEBUG_CLEAR_BREAKPOINT = DEBUG + UNDERSCORE + "CLEAR_BREAKPOINT";
	public static final String DEBUG_CLEAR_ALL_BREAKPOINTS = DEBUG + UNDERSCORE
			+ "CLEAR_ALL_BREAKPOINTS";
	public static final String DEBUG_CLEAR_ALL_BREAKPOINTS_FOR_FILE = DEBUG + UNDERSCORE
			+ "CLEAR_ALL_BREAKPOINTS_FOR_FILE";


}
