/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.publish.ui.command;

import org.eclipse.osgi.util.NLS;

public final class PublishCommandMessages extends NLS {

	public static String PUBLISH_FAIL_TITLE;
	public static String NO_PROJECTS_IN_SELECTION_NOTHING_TO_PUBLISH;
	public static String NOTHING_IS_SELECTED_TO_BE_PUBLISHED;
	public static String UNKNOWN_SELECTION_TYPE;
	public static String ACTIVATION_FAIL_TITLE;
	public static String NO_PROJECTS_IN_SELECTION_NOTHING_TO_ACTIVATE;
	public static String NOTHING_IS_SELECTED_TO_BE_ACTIVATED;

	private static final String BUNDLE_NAME = "org.eclipse.dirigible.ide.publish.ui.command.publish_messages"; //$NON-NLS-1$

	static {
		NLS.initializeMessages(BUNDLE_NAME, PublishCommandMessages.class);
	}

	/*
	 * Disable instantiation
	 */
	private PublishCommandMessages() {
		super();
	}

}
