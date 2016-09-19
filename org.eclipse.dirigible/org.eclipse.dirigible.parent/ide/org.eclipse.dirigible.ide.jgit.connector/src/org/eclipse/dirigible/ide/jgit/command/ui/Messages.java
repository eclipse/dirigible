/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.jgit.command.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dirigible.ide.jgit.command.ui.messages"; //$NON-NLS-1$
	public static String BaseCommandDialog_PASSWORD_IS_EMPTY;
	public static String BaseCommandDialog_USERNAME_IS_EMPTY;
	public static String CloneCommandDialog_CLONING_GIT_REPOSITORY;
	public static String CloneCommandDialog_ENTER_GIT_REPOSITORY_URL;
	public static String CloneCommandDialog_INVALID_GIT_REPOSITORY_URL;
	public static String CommandDialog_COMMIT_MESSAGE;
	public static String BaseCommandDialog_PASSWORD;
	public static String BaseCommandDialog_PUSH_CHANGES_TO_REMOTE_GIT_REPOSITORY;
	public static String BaseCommandDialog_USERNAME;
	public static String CommandDialog_REPOSITORY_URI;
	public static String CommandDialog_REPOSITORY_BRANCH;
	public static String ShareCommandDialog_REPOSITORY_URI_IS_EMPTY;
	public static String ShareCommandDialog_SHARE_TO_REMOTE_GIT_REPOSITORY;
	public static String PushCommandDialog_COMMIT_MESSAGE_IS_EMPTY;
	public static String PushCommandDialog_EMAIL;
	public static String PushCommandDialog_EMAIL_IS_EMPTY;
	public static String CloneDependenciesCommandDialog_CLONING_GIT_REPOSITORY;
	public static String CloneDependenciesCommandDialog_ENTER_GIT_REPOSITORY_URL;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
