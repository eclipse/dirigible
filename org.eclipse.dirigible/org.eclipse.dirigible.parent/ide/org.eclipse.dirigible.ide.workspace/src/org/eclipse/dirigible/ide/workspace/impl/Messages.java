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

package org.eclipse.dirigible.ide.workspace.impl;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dirigible.ide.workspace.impl.messages"; //$NON-NLS-1$
	public static String Container_COULD_NOT_GET_MEMBERS;
	public static String Container_THE_FILE_PATH_MUST_HAVE_AT_LEAST_TWO_SEGMENTS;
	public static String Container_THE_FOLDER_PATH_MUST_HAVE_AT_LEAST_TWO_SEGMENTS;
	public static String File_A_RESOURCE_WITH_THIS_PATH_EXISTS;
	public static String File_COULD_NOT_APPEND_CONTENTS;
	public static String File_COULD_NOT_CREATE_RESOURCE;
	public static String File_COULD_NOT_READ_FILE;
	public static String File_COULD_NOT_WRITE_TO_FILE;
	public static String File_FILE_DOES_NOT_EXIST;
	public static String FILE_NAME_FORMAT;
	public static String File_OWNER_PROJECT_IS_NOT_OPEN;
	public static String File_PARENT_DOES_NOT_EXIST;
	public static String File_RESOURCE_DOES_NOT_EXIST;
	public static String File_ROLLBACK_NOT_SUPPORTED;
	public static String Folder_A_RESOURCE_WITH_THIS_PATH_EXISTS;
	public static String Folder_COULD_NOT_CREATE_FOLDER;
	public static String Folder_LINKED_FOLDERS_ARE_NOT_SUPPORTED;
	public static String Folder_LINKED_FOLDERS_ARE_NOT_SUPPORTED2;
	public static String Folder_PARENT_DOES_NOT_EXIST;
	public static String Folder_PROJECT_IS_NOT_OPEN;
	public static String PROJECT_AND_FOLDER_NAME_FORMAT;
	public static String Project_COULD_NOT_CREATE_PROJECT;
	public static String Project_METHOD_NOT_SUPPORTED;
	public static String Project_PROJECT_ALREADY_EXIST;
	public static String Resource_COULD_NOT_DELETE_RESOURCE;
	public static String Resource_COULD_NOT_RENAME_RESOURCE;
	public static String Resource_METHOD_SET_DERIVED_NOT_SUPPORTED;
	public static String Resource_METHOD_SET_HIDDEN_NOT_SUPPORTED;
	public static String Resource_METHOD_SET_LOCAL_NOT_SUPPORTED;
	public static String Resource_METHOD_SET_LOCAL_TIME_STAMP_NOT_SUPPORTED;
	public static String Resource_METHOD_SET_READ_ONLY_NOT_SUPPORTED;
	public static String Resource_METHOD_SET_TEAM_PRIVATE_MEMBER_NOT_SUPPORTED;
	public static String Resource_MOVE_IS_STILL_UNSUPPORTED;
	public static String Resource_OK;
	public static String Resource_PERSISTANT_PROPERTIES_NOT_SUPPORTED;
	public static String Resource_RESOURCE_PATH_CANNOT_BE_NULL;
	public static String Resource_ROOLBACK_NOT_SUPPORTED;
	public static String Workspace_AUTO_BUILDING_IS_NOT_SUPPORTED;
	public static String Workspace_COULD_NOT_CREATE_REPOSITORY_HANDLER;
	public static String Workspace_COULD_NOT_CREATE_WORKSPACE_ROOT;
	public static String Workspace_FILE_PATH_MUST_HAVE_ATLEAST_2_SEGMENTS;
	public static String Workspace_FOLDER_PATH_MUST_HAVE_ATLEAST_2_SEGMENTS;
	public static String Workspace_INVALID_ROOT_PATH;
	public static String Workspace_INVALID_SEGMENT_IN_THE_NAME_SEGMENT_MUST_COMPLY_WITH_URI_SYNTAX;
	public static String Workspace_LISTENER_MAY_NOT_BE_NULL;
	public static String Workspace_MARKERS_ARE_NOT_SUPPORTED;
	public static String Workspace_NAME_MAY_NOT_BE_EMPTY;
	public static String Workspace_NAME_MAY_NOT_BE_MADE_OF_ONLY;
	public static String Workspace_NAME_MAY_NOT_BE_NULL;
	public static String Workspace_NAME_MAY_NOT_CONTAIN_OR;
	public static String Workspace_NAME_MAY_NOT_CONTAIN_OR2;
	public static String Workspace_NAME_MAY_NOT_CONTAIN_OR3;
	public static String Workspace_NAME_MAY_NOT_CONTAIN;
	public static String Workspace_NAME_MAY_NOT_CONTAIN2;
	public static String Workspace_NAME_MAY_NOT_END_WITH;
	public static String Workspace_NATURES_ARE_NOT_SUPPORTED;
	public static String Workspace_OK;
	public static String Workspace_ONE_OR_MORE_RESOURCES_WERE_NOT_DELETED;
	public static String Workspace_PATH_MAY_NOT_BE_EMPTY;
	public static String Workspace_PATH_MAY_NOT_BE_NULL;
	public static String Workspace_PATH_MAY_NOT_CONTAIN;
	public static String Workspace_PATH_MAY_NOT_CONTAIN_DEVICE_ID;
	public static String Workspace_PATH_MUST_BE_ABSOLUTE;
	public static String Workspace_PROJECT_BUILDING_IS_NOT_SUPPORTED;
	public static String Workspace_PROJECT_PATH_MUST_HAVE_1_SEGMENT;
	public static String Workspace_ROOT_NAME_IS_ALWAYS_INVALID;
	public static String Workspace_WORKSPACE_IS_NOT_INITIALIZED;
	public static String Workspace_WORKSPACE_NOT_INITIALIZED;
	public static String Workspace_WORKSPACE_STATE_NOT_SUPPORTED;
	public static String WorkspaceRoot_ONLY_PROJECTS_SHOULD_BE_CHILDREN_OF_THE_ROOT;
	public static String WorkspaceRoot_THE_PROJECT_PATH_MUST_HAVE_EXACTLY_ONE_SEGMENT;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
