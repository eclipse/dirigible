/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ui.internal.ide;

/**
 * Help context ids for the workbench.
 * <p>
 * This interface contains constants only; it is not intended to be implemented
 * or extended.
 * </p>
 * 
 */
public interface IIDEHelpContextIds {
	public static final String PREFIX = IDEWorkbenchPlugin.IDE_WORKBENCH + "."; //$NON-NLS-1$

	// Actions
	public static final String ADD_BOOKMARK_ACTION = PREFIX
			+ "add_bookmark_action_context"; //$NON-NLS-1$

	public static final String ADD_TASK_ACTION = PREFIX
			+ "add_task_action_context"; //$NON-NLS-1$	

	public static final String INCREMENTAL_BUILD_ACTION = PREFIX
			+ "incremental_build_action_context"; //$NON-NLS-1$

	public static final String FULL_BUILD_ACTION = PREFIX
			+ "full_build_action_context"; //$NON-NLS-1$

	public static final String CLOSE_RESOURCE_ACTION = PREFIX
			+ "close_resource_action_context"; //$NON-NLS-1$

	public static final String CLOSE_UNRELATED_PROJECTS_ACTION = PREFIX
			+ "close_unrelated_projects_action_context"; //$NON-NLS-1$

	public static final String OPEN_RESOURCE_ACTION = PREFIX
			+ "open_resource_action_context"; //$NON-NLS-1$

	public static final String OPEN_FILE_ACTION = PREFIX
			+ "open_file_action_context"; //$NON-NLS-1$

	public static final String OPEN_LOCAL_FILE_ACTION = PREFIX
			+ "open_local_file_action_context"; //$NON-NLS-1$

	public static final String OPEN_SYSTEM_EDITOR_ACTION = PREFIX
			+ "open_system_editor_action_context"; //$NON-NLS-1$

	public static final String REFRESH_ACTION = PREFIX
			+ "refresh_action_context"; //$NON-NLS-1$

	public static final String MOVE_RESOURCE_ACTION = PREFIX
			+ "move_resource_action_context"; //$NON-NLS-1$

	public static final String COPY_RESOURCE_ACTION = PREFIX
			+ "copy_resource_action_context"; //$NON-NLS-1$

	public static final String MOVE_PROJECT_ACTION = PREFIX
			+ "move_project_action_context"; //$NON-NLS-1$

	public static final String COPY_PROJECT_ACTION = PREFIX
			+ "copy_project_action_context"; //$NON-NLS-1$

	public static final String RENAME_RESOURCE_ACTION = PREFIX
			+ "rename_resource_action_context"; //$NON-NLS-1$

	public static final String DELETE_RESOURCE_ACTION = PREFIX
			+ "delete_resource_action_context"; //$NON-NLS-1$

	public static final String PROJECT_PROPERTY_DIALOG_ACTION = PREFIX
			+ "project_property_dialog_action_context"; //$NON-NLS-1$

	public static final String CREATE_FOLDER_ACTION = PREFIX
			+ "create_folder_action_context"; //$NON-NLS-1$

	public static final String CREATE_FILE_ACTION = PREFIX
			+ "create_file_action_context"; //$NON-NLS-1$

	public static final String SCRUB_LOCAL_ACTION = PREFIX
			+ "scrub_local_action_context"; //$NON-NLS-1$

	public static final String GLOBAL_INCREMENTAL_BUILD_ACTION = PREFIX
			+ "global_incremental_build_action_context"; //$NON-NLS-1$

	public static final String GLOBAL_FULL_BUILD_ACTION = PREFIX
			+ "global_full_build_action_context"; //$NON-NLS-1$

	public static final String QUICK_START_ACTION = PREFIX
			+ "quick_start_action_context"; //$NON-NLS-1$

	public static final String TIPS_AND_TRICKS_ACTION = PREFIX
			+ "tips_and_tricks_action_context"; //$NON-NLS-1$

	public static final String TEXT_CUT_ACTION = PREFIX
			+ "text_cut_action_context"; //$NON-NLS-1$

	public static final String TEXT_COPY_ACTION = PREFIX
			+ "text_copy_action_context"; //$NON-NLS-1$

	public static final String TEXT_PASTE_ACTION = PREFIX
			+ "text_paste_action_context"; //$NON-NLS-1$

	public static final String TEXT_DELETE_ACTION = PREFIX
			+ "text_delete_action_context"; //$NON-NLS-1$

	public static final String TEXT_SELECT_ALL_ACTION = PREFIX
			+ "text_select_all_action_context"; //$NON-NLS-1$

	public static final String OPEN_WORKSPACE_FILE_ACTION = PREFIX
			+ "open_workspace_file_action_context"; //$NON-NLS-1$

	// Dialogs
	public static final String PROJECT_LOCATION_SELECTION_DIALOG = PREFIX
			+ "project_location_selection_dialog_context"; //$NON-NLS-1$

	public static final String CONTAINER_SELECTION_DIALOG = PREFIX
			+ "container_selection_dialog_context"; //$NON-NLS-1$

	public static final String FILE_SELECTION_DIALOG = PREFIX
			+ "file_selection_dialog_context"; //$NON-NLS-1$

	public static final String RESOURCE_SELECTION_DIALOG = PREFIX
			+ "resource_selection_dialog_context"; //$NON-NLS-1$

	public static final String DELETE_PROJECT_DIALOG = PREFIX
			+ "delete_project_dialog_context"; //$NON-NLS-1$

	public static final String MARKER_RESOLUTION_SELECTION_DIALOG = PREFIX
			+ "marker_resolution_selection_dialog_context"; //$NON-NLS-1$

	public static final String WELCOME_PAGE_SELECTION_DIALOG = PREFIX
			+ "welcome_page_selection_dialog"; //$NON-NLS-1$

	public static final String TIPS_AND_TRICKS_PAGE_SELECTION_DIALOG = PREFIX
			+ "tips_and_tricks_page_selection_dialog"; //$NON-NLS-1$

	public static final String OPEN_RESOURCE_DIALOG = PREFIX
			+ "open_resource_dialog"; //$NON-NLS-1$

	public static final String NEW_FOLDER_DIALOG = PREFIX + "new_folder_dialog"; //$NON-NLS-1$

	public static final String PATH_VARIABLE_SELECTION_DIALOG = PREFIX
			+ "path_variable_selection_dialog"; //$NON-NLS-1$

	public static final String SAVE_AS_DIALOG = PREFIX
			+ "save_as_dialog_context"; //$NON-NLS-1$

	// Editors
	public static final String WELCOME_EDITOR = PREFIX
			+ "welcome_editor_context"; //$NON-NLS-1$

	// Preference pages
	public static final String BUILD_ORDER_PREFERENCE_PAGE = PREFIX
			+ "build_order_preference_page_context"; //$NON-NLS-1$

	public static final String FILE_STATES_PREFERENCE_PAGE = PREFIX
			+ "file_states_preference_page_context"; //$NON-NLS-1$

	public static final String LINKED_RESOURCE_PREFERENCE_PAGE = PREFIX
			+ "linked_resource_preference_page_context"; //$NON-NLS-1$

	// Property pages
	public static final String PROJECT_REFERENCE_PROPERTY_PAGE = PREFIX
			+ "project_reference_property_page_context"; //$NON-NLS-1$

	public static final String RESOURCE_INFO_PROPERTY_PAGE = PREFIX
			+ "resource_info_property_page_context"; //$NON-NLS-1$

	// Wizard pages
	public static final String NEW_PROJECT_WIZARD_PAGE = PREFIX
			+ "new_project_wizard_page_context"; //$NON-NLS-1$

	public static final String NEW_PROJECT_REFERENCE_WIZARD_PAGE = PREFIX
			+ "new_project_reference_wizard_page_context"; //$NON-NLS-1$

	public static final String NEW_FOLDER_WIZARD_PAGE = PREFIX
			+ "new_folder_wizard_page_context"; //$NON-NLS-1$

	public static final String NEW_FILE_WIZARD_PAGE = PREFIX
			+ "new_file_wizard_page_context"; //$NON-NLS-1$

	public static final String NEW_LINK_WIZARD_PAGE = PREFIX
			+ "new_link_wizard_page_context"; //$NON-NLS-1$

	public static final String WORKING_SET_RESOURCE_PAGE = PREFIX
			+ "working_set_resource_page"; //$NON-NLS-1$	

	public static final String WORKSPACE_PREFERENCE_PAGE = PREFIX
			+ "workspace_preference_page_context"; //$NON-NLS-1$

	// Wizards

	public static final String NEW_FILE_WIZARD = PREFIX
			+ "new_file_wizard_context"; //$NON-NLS-1$

	public static final String NEW_FOLDER_WIZARD = PREFIX
			+ "new_folder_wizard_context"; //$NON-NLS-1$

	public static final String NEW_PROJECT_WIZARD = PREFIX
			+ "new_project_wizard_context"; //$NON-NLS-1$

	public static final String SWITCH_WORKSPACE_ACTION = PREFIX
			+ "switch_workspace_dialog_context"; //$NON-NLS-1$
}
