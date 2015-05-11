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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public class TableDetailsEditorPage extends EditorPart {

	private static final String FILTER_CONDITION = "FILTER_CONDITION"; //$NON-NLS-1$
	private static final String PAGES_INDEX = "PAGES"; //$NON-NLS-1$
	private static final String ASC_OR_DESC = "ASC_OR_DESC"; //$NON-NLS-1$
	private static final String ORDINAL_POSITION = "ORDINAL_POSITION"; //$NON-NLS-1$
	private static final String INDEX_QUALIFIER = "INDEX_QUALIFIER"; //$NON-NLS-1$
	private static final String NON_UNIQUE = "NON_UNIQUE"; //$NON-NLS-1$
	private static final String TYPE_INDEX = "TYPE"; //$NON-NLS-1$
	private static final String INDEX_NAME = "INDEX_NAME"; //$NON-NLS-1$
	private static final String EMPTY = ""; //$NON-NLS-1$
	private static final String PK = "PK"; //$NON-NLS-1$
	private static final String IS_NULLABLE = "IS_NULLABLE"; //$NON-NLS-1$
	private static final String COLUMN_SIZE = "COLUMN_SIZE"; //$NON-NLS-1$
	private static final String TYPE_NAME = "TYPE_NAME"; //$NON-NLS-1$
	private static final String COLUMN_NAME = "COLUMN_NAME"; //$NON-NLS-1$
	private static final String LENGTH_TEXT = Messages.TableDetailsEditorPage_LENGTH_TEXT;
	private static final String KEY_TEXT = Messages.TableDetailsEditorPage_KEY_TEXT;
	private static final String ALLOW_NULL_TEXT = Messages.TableDetailsEditorPage_ALLOW_NULL_TEXT;
//	private static final String DELETE_RULE_TEXT = Messages.TableDetailsEditorPage_DELETE_RULE_TEXT;
//	private static final String UPDATE_RULE_TEXT = Messages.TableDetailsEditorPage_UPDATE_RULE_TEXT;
//	private static final String FOREIGN_KEY_COLUMN_TEXT = Messages.TableDetailsEditorPage_FOREIGN_KEY_COLUMN_TEXT;
//	private static final String FOREIGN_KEY_TABLE_TEXT = Messages.TableDetailsEditorPage_FOREIGN_KEY_TABLE_TEXT;
//	private static final String FOREIGN_KEY_TEXT = Messages.TableDetailsEditorPage_FOREIGN_KEY_TEXT;
//	private static final String PK_COLUMN_NAME_TEXT = Messages.TableDetailsEditorPage_PK_COLUMN_NAME_TEXT;
//	private static final String PK_TABLE_TEXT = Messages.TableDetailsEditorPage_PK_TABLE_TEXT;
//	private static final String PRIMARY_KEY_NAME_TEXT = Messages.TableDetailsEditorPage_PRIMARY_KEY_NAME_TEXT;
	private static final String PAGES_TEXT = Messages.TableDetailsEditorPage_PAGES_TEXT;
	private static final String CARDINALITY_TEXT = Messages.TableDetailsEditorPage_CARDINALITY_TEXT;
	private static final String ASC_DESC_TEXT = Messages.TableDetailsEditorPage_ASC_DESC_TEXT;
	private static final String ORDINAL_POSITION_TEXT = Messages.TableDetailsEditorPage_ORDINAL_POSITION_TEXT;
	private static final String QUALIFIER_TEXT = Messages.TableDetailsEditorPage_QUALIFIER_TEXT;
	private static final String NON_UNIQUE_TEXT = Messages.TableDetailsEditorPage_NON_UNIQUE_TEXT;
	private static final String COLUMN_NAME_TEXT = Messages.TableDetailsEditorPage_COLUMN_NAME_TEXT;
	private static final String TYPE_TEXT = Messages.TableDetailsEditorPage_TYPE_TEXT;
	private static final String INDEX_NAME_TEXT = Messages.TableDetailsEditorPage_INDEX_NAME_TEXT;
	private static final String INDEXES_TEXT = Messages.TableDetailsEditorPage_INDEXES_TEXT;
	private static final String FILTER_CONDITION_TEXT = Messages.TableDetailsEditorPage_FILTER_CONDITION_TEXT;

	public TableDetailsEditorPage() {
		super();
	}

	public void doSave(final IProgressMonitor monitor) {
		//
	}

	public void doSaveAs() {
		//
	}

	public void init(final IEditorSite site, final IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
	}

	protected boolean dirty = false;
	private Tree defTreeTable;
	private Tree indexesTable;
//	private Tree foreignKeyTable;

	public boolean isDirty() {
		return false;
	}

	protected void setDirty(final boolean value) {
		// dirty = value;
		// firePropertyChange( PROP_DIRTY );
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public void createPartControl(final Composite parent) {
		SashForm sashForm = new SashForm(parent, SWT.VERTICAL | SWT.BORDER);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		createDefinitionTable(sashForm);

		createIndexesTable(sashForm);
		sashForm.setWeights(new int[] { 70, 30 });
		try {
			initData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void createIndexesTable(Composite mainComposite) {
		Composite indexComposite = new Composite(mainComposite, SWT.NONE);
		GridLayout flat = new GridLayout();
		flat.horizontalSpacing = 0;
		flat.marginWidth = 0;
		indexComposite.setLayout(flat);
		GridData gridDataTable = new GridData();
		gridDataTable.horizontalAlignment = SWT.FILL;
		gridDataTable.verticalAlignment = SWT.FILL;
		gridDataTable.grabExcessHorizontalSpace = true;
		gridDataTable.grabExcessVerticalSpace = true;
		gridDataTable.horizontalIndent = 0;
		// FormToolkit toolkit = new FormToolkit(mainComposite.getDisplay());
		// Section section = toolkit.createSection(mainComposite, SWT.NONE);
		// section.setText("Indexes");
		// Composite client = toolkit.createComposite(section, SWT.NONE);
		// client.setLayout(new GridLayout());
		Label l = new Label(indexComposite, SWT.LEFT);
		l.setText(INDEXES_TEXT);
		indexesTable = new Tree(indexComposite, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL);
		// indexesTable = toolkit.createTree(client, SWT.NONE);
		indexesTable.setLayoutData(gridDataTable);
		indexesTable.setHeaderVisible(true);
		TreeColumn iName = new TreeColumn(indexesTable, SWT.LEFT);
		iName.setText(INDEX_NAME_TEXT);
		iName.setWidth(220);
		TreeColumn iType = new TreeColumn(indexesTable, SWT.CENTER);
		iType.setText(TYPE_TEXT);
		iType.setWidth(50);
		TreeColumn cname = new TreeColumn(indexesTable, SWT.RIGHT);
		cname.setText(COLUMN_NAME_TEXT);
		cname.setWidth(150);
		TreeColumn nonUnique = new TreeColumn(indexesTable, SWT.RIGHT);
		nonUnique.setText(NON_UNIQUE_TEXT);
		nonUnique.setWidth(90);
		TreeColumn indexQualifier = new TreeColumn(indexesTable, SWT.RIGHT);
		indexQualifier.setText(QUALIFIER_TEXT);
		indexQualifier.setWidth(80);
		TreeColumn ordinalPosition = new TreeColumn(indexesTable, SWT.RIGHT);
		ordinalPosition.setText(ORDINAL_POSITION_TEXT);
		ordinalPosition.setWidth(120);
		TreeColumn ascVsDesc = new TreeColumn(indexesTable, SWT.RIGHT);
		ascVsDesc.setText(ASC_DESC_TEXT);
		ascVsDesc.setWidth(80);
		TreeColumn cardinality = new TreeColumn(indexesTable, SWT.RIGHT);
		cardinality.setText(CARDINALITY_TEXT);
		cardinality.setWidth(80);
		TreeColumn pages = new TreeColumn(indexesTable, SWT.RIGHT);
		pages.setText(PAGES_TEXT);
		pages.setWidth(50);
		TreeColumn filterCondition = new TreeColumn(indexesTable, SWT.RIGHT);
		filterCondition.setText(FILTER_CONDITION_TEXT); //$NON-NLS-1$
		filterCondition.setWidth(120);

	}

//	// not in use since there is no foreign keys in maxdb and hana
//	private void createForegnKeysTable(Composite mainComposite) {
//		GridData gd = new GridData();
//		gd.horizontalAlignment = SWT.FILL;
//		gd.verticalAlignment = SWT.FILL;
//		gd.grabExcessHorizontalSpace = true;
//		gd.grabExcessVerticalSpace = true;
//		gd.minimumHeight = 200;
//		foreignKeyTable = new Tree(mainComposite, SWT.BORDER | SWT.H_SCROLL
//				| SWT.V_SCROLL);
//		foreignKeyTable.setLayoutData(gd);
//		foreignKeyTable.setHeaderVisible(true);
//		TreeColumn pkName = new TreeColumn(foreignKeyTable, SWT.LEFT);
//		pkName.setText(PRIMARY_KEY_NAME_TEXT);
//		pkName.setWidth(200);
//		TreeColumn pkTable = new TreeColumn(foreignKeyTable, SWT.CENTER);
//		pkTable.setText(PK_TABLE_TEXT);
//		pkTable.setWidth(200);
//		TreeColumn pkColumnName = new TreeColumn(foreignKeyTable, SWT.RIGHT);
//		pkColumnName.setText(PK_COLUMN_NAME_TEXT);
//		pkColumnName.setWidth(80);
//		TreeColumn allowNull = new TreeColumn(foreignKeyTable, SWT.RIGHT);
//		allowNull.setText(FOREIGN_KEY_TEXT);
//		allowNull.setWidth(80);
//		TreeColumn fkTable = new TreeColumn(foreignKeyTable, SWT.RIGHT);
//		fkTable.setText(FOREIGN_KEY_TABLE_TEXT);
//		fkTable.setWidth(50);
//		TreeColumn ftColumn = new TreeColumn(foreignKeyTable, SWT.RIGHT);
//		ftColumn.setText(FOREIGN_KEY_COLUMN_TEXT);
//		ftColumn.setWidth(50);
//		TreeColumn updateRule = new TreeColumn(foreignKeyTable, SWT.RIGHT);
//		updateRule.setText(UPDATE_RULE_TEXT);
//		updateRule.setWidth(50);
//		TreeColumn deleteRule = new TreeColumn(foreignKeyTable, SWT.RIGHT);
//		deleteRule.setText(DELETE_RULE_TEXT);
//		deleteRule.setWidth(50);
//	}

	private void createDefinitionTable(final Composite parent) {
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		defTreeTable = new Tree(parent, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL);
		defTreeTable.setLayoutData(gd);
		defTreeTable.setHeaderVisible(true);
		TreeColumn columnName = new TreeColumn(defTreeTable, SWT.LEFT);
		columnName.setText(COLUMN_NAME_TEXT);
		columnName.setWidth(200);
		TreeColumn columnType = new TreeColumn(defTreeTable, SWT.CENTER);
		columnType.setText(TYPE_TEXT);
		columnType.setWidth(200);
		TreeColumn columnLength = new TreeColumn(defTreeTable, SWT.RIGHT);
		columnLength.setText(LENGTH_TEXT);
		columnLength.setWidth(80);
		// TreeColumn unsigned = new TreeColumn(tree, SWT.RIGHT);
		// unsigned.setText("Signed");
		// unsigned.setWidth(100);
		// TreeColumn zeroFill = new TreeColumn(tree, SWT.RIGHT);
		// zeroFill.setText("Zerofill");
		// zeroFill.setWidth(100);
		// TreeColumn binary = new TreeColumn(tree, SWT.RIGHT);
		// binary.setText("Binary");
		// binary.setWidth(100);
		TreeColumn allowNull = new TreeColumn(defTreeTable, SWT.RIGHT);
		allowNull.setText(ALLOW_NULL_TEXT);
		allowNull.setWidth(80);
		TreeColumn key = new TreeColumn(defTreeTable, SWT.RIGHT);
		key.setText(KEY_TEXT);
		key.setWidth(50);
		// TreeColumn encoding = new TreeColumn(tree, SWT.RIGHT);
		// encoding.setText("Encoding");
		// encoding.setWidth(200);
	}

	private void initData() throws SQLException {

		DbEditorInput input = (DbEditorInput) getEditorInput();
		Connection connection = input.getDbConnectionFactory()
				.getDatabaseConnection();
		try {
			DatabaseMetaData databaseMetaData = connection.getMetaData();
			String tableName = input.getTableName();
			try {
				ResultSet columns = databaseMetaData.getColumns(input
						.getTableDefinition().getCatalogName(), input
						.getTableDefinition().getSchemaName(), tableName, null);
				ResultSet pks = databaseMetaData.getPrimaryKeys(input
						.getTableDefinition().getCatalogName(), input
						.getTableDefinition().getSchemaName(), tableName);
				// FK
				// ResultSet importedKeys =
				// databaseMetaData.getImportedKeys(input.getTableDefinition().getCatalogName(),
				// input.getTableDefinition().getSchemaName(), tableName);
				ResultSet indexes = databaseMetaData.getIndexInfo(input
						.getTableDefinition().getCatalogName(), input
						.getTableDefinition().getSchemaName(), tableName,
						false, false);

				List<String> pkList = new ArrayList<String>();
				while (pks.next()) {
					String pkName = pks.getString(COLUMN_NAME);
					pkList.add(pkName);
				}

				while (columns.next()) {
					TreeItem item = new TreeItem(defTreeTable, SWT.NONE);
					String cname = columns.getString(COLUMN_NAME);
					String ctype = columns.getString(TYPE_NAME);
					String csize = columns.getInt(COLUMN_SIZE) + EMPTY;
					String cIsNullable = columns.getString(IS_NULLABLE);
					String cKey = pkList.contains(cname) ? PK : EMPTY;
					item.setText(new String[] { cname, ctype, csize,
							cIsNullable, cKey });
				}
				while (indexes.next()) {
					TreeItem item = new TreeItem(indexesTable, SWT.NONE);
					String iName = indexes.getString(INDEX_NAME);
					String type = indexes.getString(TYPE_INDEX);
					String cname = indexes.getString(COLUMN_NAME);
					String nonUnique = indexes.getString(NON_UNIQUE);
					String indexQualifier = indexes.getString(INDEX_QUALIFIER);
					String ordinalPosition = indexes.getShort(ORDINAL_POSITION)
							+ EMPTY;
					String asc_desc = indexes.getString(ASC_OR_DESC);
					String cardinality = indexes.getInt("CARDINALITY") + EMPTY; //$NON-NLS-1$
					String pages = indexes.getInt(PAGES_INDEX) + EMPTY;
					String filterCondition = indexes
							.getString(FILTER_CONDITION);

					item.setText(new String[] { iName, type, cname, nonUnique,
							indexQualifier, ordinalPosition, asc_desc,
							cardinality, pages, filterCondition });
				}
				// FK
				// while (importedKeys.next()) {
				// TreeItem item = new TreeItem(indexesTable, SWT.NONE);
				// String pkName = importedKeys.getString("PK_NAME");
				// String tname = importedKeys.getString("PKTABLE_NAME");
				// String ctype = importedKeys.getString("PKCOLUMN_NAME");
				// String fkName = importedKeys.getString("FK_NAME");
				// String ftName = importedKeys.getInt("FKTABLE_NAME")+"";
				// String ftcName = importedKeys.getString("FKCOLUMN_NAME");
				// String updateRule = importedKeys.getString("UPDATE_RULE");
				// String deleteRule = importedKeys.getString("DELETE_RULE");
				// item.setText(new String[] {pkName, tname, ctype, fkName,
				// ftName, ftcName, updateRule, deleteRule});
				// }
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	public void setFocus() {
		defTreeTable.setFocus();
	}
}
