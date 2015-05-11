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

package org.eclipse.dirigible.ide.db.viewer.editor;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dirigible.ide.db.viewer.editor.messages"; //$NON-NLS-1$
	public static String DbEditorInput_DATABASE_METADATA_FOR;
	public static String DbTableMetadataEditor_TABLE_DETAILS;
	public static String TableDetailsEditorPage_ALLOW_NULL_TEXT;
	public static String TableDetailsEditorPage_ASC_DESC_TEXT;
	public static String TableDetailsEditorPage_CARDINALITY_TEXT;
	public static String TableDetailsEditorPage_COLUMN_NAME_TEXT;
	public static String TableDetailsEditorPage_DELETE_RULE_TEXT;
	public static String TableDetailsEditorPage_FILTER_CONDITION_TEXT;
	public static String TableDetailsEditorPage_FOREIGN_KEY_COLUMN_TEXT;
	public static String TableDetailsEditorPage_FOREIGN_KEY_TABLE_TEXT;
	public static String TableDetailsEditorPage_FOREIGN_KEY_TEXT;
	public static String TableDetailsEditorPage_INDEX_NAME_TEXT;
	public static String TableDetailsEditorPage_INDEXES_TEXT;
	public static String TableDetailsEditorPage_KEY_TEXT;
	public static String TableDetailsEditorPage_LENGTH_TEXT;
	public static String TableDetailsEditorPage_NON_UNIQUE_TEXT;
	public static String TableDetailsEditorPage_ORDINAL_POSITION_TEXT;
	public static String TableDetailsEditorPage_PAGES_TEXT;
	public static String TableDetailsEditorPage_PK_COLUMN_NAME_TEXT;
	public static String TableDetailsEditorPage_PK_TABLE_TEXT;
	public static String TableDetailsEditorPage_PRIMARY_KEY_NAME_TEXT;
	public static String TableDetailsEditorPage_QUALIFIER_TEXT;
	public static String TableDetailsEditorPage_TYPE_TEXT;
	public static String TableDetailsEditorPage_UPDATE_RULE_TEXT;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
