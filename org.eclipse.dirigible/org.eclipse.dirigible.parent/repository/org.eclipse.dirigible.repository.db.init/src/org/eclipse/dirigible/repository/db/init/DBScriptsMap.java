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

package org.eclipse.dirigible.repository.db.init;

/**
 * The static map for the scripts locations Intentionally the active scripts are
 * registered in this class
 * 
 */
public class DBScriptsMap {

	public static final String SCRIPT_GET_SCHEMA_VERSION = "/org/eclipse/dirigible/repository/db/sql/get_schema_version.sql"; //$NON-NLS-1$

	public static final String SCRIPT_CREATE_SCHEMA_1 = "/org/eclipse/dirigible/repository/db/sql/create_schema_1.sql"; //$NON-NLS-1$
	public static final String SCRIPT_CREATE_SCHEMA_2 = "/org/eclipse/dirigible/repository/db/sql/create_schema_2.sql"; //$NON-NLS-1$
	public static final String SCRIPT_CREATE_SCHEMA_3 = "/org/eclipse/dirigible/repository/db/sql/create_schema_3.sql"; //$NON-NLS-1$
	public static final String SCRIPT_CREATE_SCHEMA_4 = "/org/eclipse/dirigible/repository/db/sql/create_schema_4.sql"; //$NON-NLS-1$
	public static final String SCRIPT_CREATE_SCHEMA_5 = "/org/eclipse/dirigible/repository/db/sql/create_schema_5.sql"; //$NON-NLS-1$
	public static final String SCRIPT_CREATE_SCHEMA_6 = "/org/eclipse/dirigible/repository/db/sql/create_schema_6.sql"; //$NON-NLS-1$
	public static final String SCRIPT_CREATE_SCHEMA_7 = "/org/eclipse/dirigible/repository/db/sql/create_schema_7.sql"; //$NON-NLS-1$

	public static final String SCRIPT_GET_BINARY = "/org/eclipse/dirigible/repository/db/sql/get_binary.sql"; //$NON-NLS-1$
	public static final String SCRIPT_GET_DOCUMENT = "/org/eclipse/dirigible/repository/db/sql/get_document.sql"; //$NON-NLS-1$
	public static final String SCRIPT_GET_FILES_BY_PATH = "/org/eclipse/dirigible/repository/db/sql/get_files_by_path.sql"; //$NON-NLS-1$
	public static final String SCRIPT_GET_FILE_BY_PATH = "/org/eclipse/dirigible/repository/db/sql/get_file_by_path.sql"; //$NON-NLS-1$

	public static final String SCRIPT_INSERT_BINARY = "/org/eclipse/dirigible/repository/db/sql/insert_binary.sql"; //$NON-NLS-1$
	public static final String SCRIPT_INSERT_DOCUMENT = "/org/eclipse/dirigible/repository/db/sql/insert_document.sql"; //$NON-NLS-1$
	public static final String SCRIPT_INSERT_FILE = "/org/eclipse/dirigible/repository/db/sql/insert_file.sql"; //$NON-NLS-1$

	public static final String SCRIPT_REMOVE_BINARY = "/org/eclipse/dirigible/repository/db/sql/remove_binary.sql"; //$NON-NLS-1$
	public static final String SCRIPT_REMOVE_BINS_CASCADE = "/org/eclipse/dirigible/repository/db/sql/remove_bins_cascade.sql"; //$NON-NLS-1$
	public static final String SCRIPT_REMOVE_DOCS_CASCADE = "/org/eclipse/dirigible/repository/db/sql/remove_docs_cascade.sql"; //$NON-NLS-1$
	public static final String SCRIPT_REMOVE_DOCUMENT = "/org/eclipse/dirigible/repository/db/sql/remove_document.sql"; //$NON-NLS-1$
	public static final String SCRIPT_REMOVE_FILE_BY_PATH = "/org/eclipse/dirigible/repository/db/sql/remove_file_by_path.sql"; //$NON-NLS-1$
	public static final String SCRIPT_REMOVE_FOLDER_BY_PATH = "/org/eclipse/dirigible/repository/db/sql/remove_folder_by_path.sql"; //$NON-NLS-1$
	public static final String SCRIPT_REMOVE_FOLDER_CASCADE = "/org/eclipse/dirigible/repository/db/sql/remove_folder_cascade.sql"; //$NON-NLS-1$

	public static final String SCRIPT_IS_FOLDER_EMPTY = "/org/eclipse/dirigible/repository/db/sql/is_folder_empty.sql"; //$NON-NLS-1$

	public static final String SCRIPT_SEARCH_NAME = "/org/eclipse/dirigible/repository/db/sql/search_name.sql"; //$NON-NLS-1$
	public static final String SCRIPT_SEARCH_NAME_SENSE = "/org/eclipse/dirigible/repository/db/sql/search_name_sense.sql"; //$NON-NLS-1$
	public static final String SCRIPT_SEARCH_NAME_UNDER_ROOT = "/org/eclipse/dirigible/repository/db/sql/search_name_under_root.sql"; //$NON-NLS-1$
	public static final String SCRIPT_SEARCH_NAME_UNDER_ROOT_SENSE = "/org/eclipse/dirigible/repository/db/sql/search_name_under_root_sense.sql"; //$NON-NLS-1$
	public static final String SCRIPT_SEARCH_TEXT = "/org/eclipse/dirigible/repository/db/sql/search_text.sql"; //$NON-NLS-1$
	public static final String SCRIPT_SEARCH_TEXT_SENSE = "/org/eclipse/dirigible/repository/db/sql/search_text_sense.sql"; //$NON-NLS-1$

	public static final String SCRIPT_GET_FILE_VERSION_BY_PATH = "/org/eclipse/dirigible/repository/db/sql/get_file_version_by_path.sql"; //$NON-NLS-1$
	public static final String SCRIPT_GET_FILE_VERSIONS_BY_PATH = "/org/eclipse/dirigible/repository/db/sql/get_file_versions_by_path.sql"; //$NON-NLS-1$
	public static final String SCRIPT_GET_NEXT_FILE_VERSION_BY_PATH = "/org/eclipse/dirigible/repository/db/sql/get_next_file_version_by_path.sql"; //$NON-NLS-1$
	public static final String SCRIPT_INSERT_FILE_VERSION = "/org/eclipse/dirigible/repository/db/sql/insert_file_version.sql"; //$NON-NLS-1$
	public static final String SCRIPT_REMOVE_ALL_FILE_VERSIONS = "/org/eclipse/dirigible/repository/db/sql/remove_all_file_versions_by_path.sql"; //$NON-NLS-1$
	public static final String SCRIPT_REMOVE_ALL_FILE_VERSIONS_BEFORE_DATE = "/org/eclipse/dirigible/repository/db/sql/remove_all_file_versions_before_date.sql"; //$NON-NLS-1$

	public static final String SCRIPT_GET_FILES_BY_PATH_CASCADE = "/org/eclipse/dirigible/repository/db/sql/get_files_by_path_cascade.sql"; //$NON-NLS-1$
	public static final String SCRIPT_GET_DOCUMENTS_BY_PATH_CASCADE = "/org/eclipse/dirigible/repository/db/sql/get_documents_by_path_cascade.sql"; //$NON-NLS-1$
	public static final String SCRIPT_RENAME_FILE = "/org/eclipse/dirigible/repository/db/sql/rename_file.sql"; //$NON-NLS-1$
	public static final String SCRIPT_RENAME_DOCUMENT = "/org/eclipse/dirigible/repository/db/sql/rename_document.sql"; //$NON-NLS-1$

	public static final String SCRIPT_SET_MODIFIED = "/org/eclipse/dirigible/repository/db/sql/set_modified.sql"; //$NON-NLS-1$

	public static final String SCRIPT_UPDATE_FILE = "/org/eclipse/dirigible/repository/db/sql/update_file.sql"; //$NON-NLS-1$
}
