/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.db.viewer.editor;

import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dirigible.ide.common.CommonParameters;
import org.eclipse.dirigible.repository.datasource.DataSources;
import org.eclipse.dirigible.repository.datasource.DataSources.ColumnsIteratorCallback;
import org.eclipse.dirigible.repository.datasource.DataSources.IndicesIteratorCallback;
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

@SuppressWarnings("javadoc")
public class TableDetailsEditorPage extends EditorPart {

	private static final String LENGTH_TEXT = Messages.TableDetailsEditorPage_LENGTH_TEXT;
	private static final String KEY_TEXT = Messages.TableDetailsEditorPage_KEY_TEXT;
	private static final String ALLOW_NULL_TEXT = Messages.TableDetailsEditorPage_ALLOW_NULL_TEXT;
	// private static final String DELETE_RULE_TEXT = Messages.TableDetailsEditorPage_DELETE_RULE_TEXT;
	// private static final String UPDATE_RULE_TEXT = Messages.TableDetailsEditorPage_UPDATE_RULE_TEXT;
	// private static final String FOREIGN_KEY_COLUMN_TEXT = Messages.TableDetailsEditorPage_FOREIGN_KEY_COLUMN_TEXT;
	// private static final String FOREIGN_KEY_TABLE_TEXT = Messages.TableDetailsEditorPage_FOREIGN_KEY_TABLE_TEXT;
	// private static final String FOREIGN_KEY_TEXT = Messages.TableDetailsEditorPage_FOREIGN_KEY_TEXT;
	// private static final String PK_COLUMN_NAME_TEXT = Messages.TableDetailsEditorPage_PK_COLUMN_NAME_TEXT;
	// private static final String PK_TABLE_TEXT = Messages.TableDetailsEditorPage_PK_TABLE_TEXT;
	// private static final String PRIMARY_KEY_NAME_TEXT = Messages.TableDetailsEditorPage_PRIMARY_KEY_NAME_TEXT;
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

	private static final String SELECTED_DATASOURCE_NAME = "SELECTED_DATASOURCE_NAME";

	public TableDetailsEditorPage() {
		super();
	}

	@Override
	public void doSave(final IProgressMonitor monitor) {
		//
	}

	@Override
	public void doSaveAs() {
		//
	}

	@Override
	public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
	}

	protected boolean dirty = false;
	private Tree defTreeTable;
	private Tree indexesTable;

	// private Tree foreignKeyTable;

	@Override
	public boolean isDirty() {
		return false;
	}

	protected void setDirty(final boolean value) {
		// dirty = value;
		// firePropertyChange( PROP_DIRTY );
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
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
		indexesTable = new Tree(indexComposite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
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
		filterCondition.setText(FILTER_CONDITION_TEXT);
		filterCondition.setWidth(120);

	}

	// // not in use since there is no foreign keys in maxdb and hana
	// private void createForegnKeysTable(Composite mainComposite) {
	// GridData gd = new GridData();
	// gd.horizontalAlignment = SWT.FILL;
	// gd.verticalAlignment = SWT.FILL;
	// gd.grabExcessHorizontalSpace = true;
	// gd.grabExcessVerticalSpace = true;
	// gd.minimumHeight = 200;
	// foreignKeyTable = new Tree(mainComposite, SWT.BORDER | SWT.H_SCROLL
	// | SWT.V_SCROLL);
	// foreignKeyTable.setLayoutData(gd);
	// foreignKeyTable.setHeaderVisible(true);
	// TreeColumn pkName = new TreeColumn(foreignKeyTable, SWT.LEFT);
	// pkName.setText(PRIMARY_KEY_NAME_TEXT);
	// pkName.setWidth(200);
	// TreeColumn pkTable = new TreeColumn(foreignKeyTable, SWT.CENTER);
	// pkTable.setText(PK_TABLE_TEXT);
	// pkTable.setWidth(200);
	// TreeColumn pkColumnName = new TreeColumn(foreignKeyTable, SWT.RIGHT);
	// pkColumnName.setText(PK_COLUMN_NAME_TEXT);
	// pkColumnName.setWidth(80);
	// TreeColumn allowNull = new TreeColumn(foreignKeyTable, SWT.RIGHT);
	// allowNull.setText(FOREIGN_KEY_TEXT);
	// allowNull.setWidth(80);
	// TreeColumn fkTable = new TreeColumn(foreignKeyTable, SWT.RIGHT);
	// fkTable.setText(FOREIGN_KEY_TABLE_TEXT);
	// fkTable.setWidth(50);
	// TreeColumn ftColumn = new TreeColumn(foreignKeyTable, SWT.RIGHT);
	// ftColumn.setText(FOREIGN_KEY_COLUMN_TEXT);
	// ftColumn.setWidth(50);
	// TreeColumn updateRule = new TreeColumn(foreignKeyTable, SWT.RIGHT);
	// updateRule.setText(UPDATE_RULE_TEXT);
	// updateRule.setWidth(50);
	// TreeColumn deleteRule = new TreeColumn(foreignKeyTable, SWT.RIGHT);
	// deleteRule.setText(DELETE_RULE_TEXT);
	// deleteRule.setWidth(50);
	// }

	private void createDefinitionTable(final Composite parent) {
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		defTreeTable = new Tree(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
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
		String tableName = input.getTableName();
		String catalogName = input.getTableDefinition().getCatalogName();
		String schemaName = input.getTableDefinition().getSchemaName();
		Connection connection = input.getDbConnectionFactory().getDatabaseConnection();
		DataSources dataSource = new DataSources(CommonParameters.get(SELECTED_DATASOURCE_NAME), connection);

		try {
			dataSource.iterateTableDefinition(tableName, catalogName, schemaName, new ColumnsIteratorCallback() {
				@Override
				public void onColumn(String columnName, String columnType, String columnSize, String isNullable, String isKey) {
					TreeItem item = new TreeItem(defTreeTable, SWT.NONE);
					item.setText(new String[] { columnName, columnType, columnSize, isNullable, isKey });
				}
			}, new IndicesIteratorCallback() {
				@Override
				public void onIndex(String indexName, String indexType, String columnName, String isNonUnique, String indexQualifier,
						String ordinalPosition, String sortOrder, String cardinality, String pagesIndex, String filterCondition) {
					TreeItem item = new TreeItem(indexesTable, SWT.NONE);
					item.setText(new String[] { indexName, indexType, columnName, isNonUnique, indexQualifier, ordinalPosition, sortOrder,
							cardinality, pagesIndex, filterCondition });
				}
			});
		} finally {
			dataSource.release();
		}
	}

	@Override
	public void setFocus() {
		defTreeTable.setFocus();
	}
}
