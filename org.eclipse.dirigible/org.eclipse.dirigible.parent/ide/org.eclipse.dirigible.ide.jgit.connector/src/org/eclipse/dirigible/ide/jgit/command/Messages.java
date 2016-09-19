/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.jgit.command;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dirigible.ide.jgit.command.messages"; //$NON-NLS-1$
	public static String CloneCommandHandler_CLONING_GIT_REPOSITORY;
	public static String CloneCommandHandler_MASTER;
	public static String CloneCommandHandler_NO_REMOTE_REPOSITORY_FOR;
	public static String CloneCommandHandler_PROJECT_WAS_CLONED;
	public static String CloneCommandHandler_TASK_CLONING_REPOSITORY;
	public static String CloneCommandHandler_WHILE_CLONING_REPOSITORY_ERROR_OCCURED;
	public static String CloneCommandHandler_NOT_SUPPORTED_REPOSITORY_TYPE;
	public static String HardResetCommandHandler_DO_YOU_REALLY_WANT_TO_HARD_RESET_PROJECT_S;
	public static String HardResetCommandHandler_ERROR_DURING_HARD_RESET;
	public static String HardResetCommandHandler_HARD_RESET;
	public static String HardResetCommandHandler_NO_PROJECT_IS_SELECTED_FOR_HARD_RESET;
	public static String HardResetCommandHandler_ONLY_ONE_PROJECT_CAN_BE_HARD_RESETED_AT_A_TIME;
	public static String HardResetCommandHandler_PLEASE_CHECK_IF_PROXY_SETTINGS_ARE_SET_PROPERLY;
	public static String HardResetCommandHandler_PLEASE_SELECT_ONE;
	public static String HardResetCommandHandler_reset;
	public static String HardResetCommandHandler_WHILE_HARD_RESETING_PROJECT_ERROR_OCCURED;
	public static String PullCommandHandler_ERROR_DURING_PULL;
	public static String PullCommandHandler_NO_PROJECT_IS_SELECTED_FOR_PULL;
	public static String PullCommandHandler_ONLY_ONE_PROJECT_CAN_BE_PULLEDT_AT_A_TIME;
	public static String PullCommandHandler_PROJECT_HAS_BEEN_PULLED_FROM_REMOTE_REPOSITORY;
	public static String PullCommandHandler_TASK_PULLING_FROM_REMOTE_REPOSITORY;
	public static String PullCommandHandler_WHILE_PULLING_PROJECT_ERROR_OCCURED;
	public static String CloneDependenciesCommandHandler_NO_PROJECT_IS_SELECTED_FOR_CLONE_DEPENDENCIES;
	public static String PushCommandHandler_CONFLICTING_FILES;
	public static String PushCommandHandler_ERROR_DURING_PUSH;
	public static String PushCommandHandler_INCORRECT_USERNAME_AND_OR_PASSWORD_OR_GIT_REPOSITORY_URI;
	public static String PushCommandHandler_NO_PROJECT_IS_SELECTED_FOR_PUSH;
	public static String PushCommandHandler_ONLY_ONE_PROJECT_CAN_BE_PUSHED_AT_A_TIME;
	public static String PushCommandHandler_PLEASE_CHECK_IF_PROXY_SETTINGS_ARE_SET_PROPERLY;
	public static String PushCommandHandler_PLEASE_MERGE_TO_MASTER_AND_THEN_CONTINUE_WORKING_ON_PROJECT;
	public static String PushCommandHandler_PLEASE_SELECT_ONE;
	public static String PushCommandHandler_PROJECT_HAS_BEEN_PUSHED_TO_REMOTE_REPOSITORY;
	public static String PushCommandHandler_PROJECT_HAS_D_CONFILCTING_FILES;
	public static String PushCommandHandler_PROJECT_HAS_D_CONFILCTING_FILES_DO_PUSH_OR_RESET;
	public static String PushCommandHandler_PUSHED_TO_REMOTE_BRANCH_S;
	public static String PushCommandHandler_TASK_PUSHING_TO_REMOTE_REPOSITORY;
	public static String PushCommandHandler_THIS_IS_NOT_A_GIT_PROJECT;
	public static String PushCommandHandler_WHILE_PUSHING_PROJECT_ERROR_OCCURED;
	public static String ResetCommandHandler_PROJECT_S_SUCCESSFULY_RESETED;
	public static String ResetCommandHandler_TASK_RESETING_PROJECT;
	public static String ShareCommandHandler_ERROR_DURING_SHARE;
	public static String ShareCommandHandler_INCORRECT_USERNAME_AND_OR_PASSWORD_OR_GIT_REPOSITORY_URI;
	public static String ShareCommandHandler_NO_PROJECT_IS_SELECTED_FOR_SHARE;
	public static String ShareCommandHandler_ONLY_ONE_PROJECT_CAN_BE_SHARED_AT_A_TIME;
	public static String ShareCommandHandler_PLEASE_CHECK_IF_PROXY_SETTINGS_ARE_SET_PROPERLY;
	public static String ShareCommandHandler_PLEASE_SELECT_ONE;
	public static String ShareCommandHandler_PROJECT_S_SUCCESSFULY_SHARED;
	public static String ShareCommandHandler_TASK_SHARING_PROJECT;
	public static String ShareCommandHandler_WHILE_SHARING_PROJECT_ERROR_OCCURED;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
