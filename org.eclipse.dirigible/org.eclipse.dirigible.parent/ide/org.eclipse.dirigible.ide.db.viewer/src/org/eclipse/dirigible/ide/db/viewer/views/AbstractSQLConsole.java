/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.db.viewer.views;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

import org.eclipse.dirigible.ide.common.CommonParameters;
import org.eclipse.dirigible.ide.db.viewer.views.format.ResultSetStringWriter;
import org.eclipse.dirigible.ide.editor.text.editor.AbstractTextEditorWidget;
import org.eclipse.dirigible.ide.editor.text.editor.EditorMode;
import org.eclipse.dirigible.repository.ext.security.IRoles;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;

/**
 * Base SQL Console providing the reusable functionality for both RAP and RCP environment
 */
public abstract class AbstractSQLConsole extends ViewPart implements ISQLConsole {

	private static final String EXECUTE_QUERY_STATEMENT = Messages.SQLConsole_EXECUTE_QUERY_STATEMENT;

	private static final String EXECUTE_QUERY = Messages.SQLConsole_EXECUTE_QUERY;

	private static final String EXECUTE_UPDATE_STATEMENT = Messages.SQLConsole_EXECUTE_UPDATE_STATEMENT;

	private static final String EXECUTE_UPDATE_TEXT = Messages.SQLConsole_EXECUTE_UPDATE_TEXT;

	private static final char SPACE = ' ';

	private static final char MINUS = '-';

	private static final String EMPTY = ""; //$NON-NLS-1$

	private static final String DOTS = "...\n"; //$NON-NLS-1$

	private static final String NULL = "NULL"; //$NON-NLS-1$

	private static final String ICON_EXECUTE_UPDATE_PNG = "icon-execute.png"; //$NON-NLS-1$

	private static final String ICON_EXECUTE_QUERY_PNG = "icon-execute.png"; //$NON-NLS-1$

	private static final String ICONS_SEGMENT = "/icons/"; //$NON-NLS-1$

	private static final String POPUP_MENU = "#PopupMenu"; //$NON-NLS-1$

	private static final String UPDATE_COUNT_S = Messages.SQLConsole_UPDATE_COUNT_S;

	private static final String EXECUTE_UPDATE = Messages.SQLConsole_EXECUTE_UPDATE;

	private static final String BINARY = "[BINARY]"; //$NON-NLS-1$

	private static final Logger logger = Logger.getLogger(AbstractSQLConsole.class);

	private static final String COLUMN_DELIMITER = "|"; //$NON-NLS-1$

	private static final String END_DELIMITER = "|\n"; //$NON-NLS-1$

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.eclipse.dirigible.ide.db.viewer.views.SQLConsole"; //$NON-NLS-1$

	private AbstractTextEditorWidget scriptArea = null;
	private Text outputArea = null;

	private Action actionExecuteUpdate;
	private Action actionExecuteQuery;

	public static final String SCRIPT_DELIMITER = ";"; //$NON-NLS-1$

	@SuppressWarnings("unused")
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());

		ToolBar toolBar = new ToolBar(parent, SWT.FLAT | SWT.WRAP | SWT.RIGHT | SWT.BORDER | SWT.SHADOW_OUT);

		SashForm sashForm = new SashForm(parent, SWT.VERTICAL | SWT.BORDER);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		scriptArea = createSQLEditorWidget(sashForm);
		scriptArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		scriptArea.setText(EMPTY, getMode(), false, false, 0);

		outputArea = new Text(sashForm, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY);
		outputArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		outputArea.setText(EMPTY);
		outputArea.setFont(new Font(null, "Courier New", 12, SWT.NORMAL)); //$NON-NLS-1$

		ToolItem itemQuery = new ToolItem(toolBar, SWT.PUSH | SWT.SEPARATOR);
		itemQuery.setText(EXECUTE_QUERY);
		Image iconQuery = ImageDescriptor.createFromURL(AbstractSQLConsole.class.getResource(ICONS_SEGMENT + ICON_EXECUTE_QUERY_PNG)).createImage();
		itemQuery.setImage(iconQuery);
		itemQuery.addSelectionListener(new SelectionListener() {
			private static final long serialVersionUID = 1281159157504712273L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				actionExecuteQuery.run();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				//
			}
		});

		boolean isOperator = CommonParameters.isUserInRole(IRoles.ROLE_OPERATOR);
		if (isOperator) {
			new ToolItem(toolBar, SWT.SEPARATOR);
			ToolItem itemUpdate = new ToolItem(toolBar, SWT.PUSH);
			itemUpdate.setText(EXECUTE_UPDATE);
			Image iconUpdate = ImageDescriptor.createFromURL(AbstractSQLConsole.class.getResource(ICONS_SEGMENT + ICON_EXECUTE_UPDATE_PNG))
					.createImage();
			itemUpdate.setImage(iconUpdate);
			itemUpdate.addSelectionListener(new SelectionListener() {
				private static final long serialVersionUID = 1281159157504712273L;

				@Override
				public void widgetSelected(SelectionEvent e) {
					actionExecuteUpdate.run();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					//
				}
			});
		}
		// Create the help context id for the viewer's control
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(scriptArea,
		// "org.eclipse.dirigible.ide.db.viewer.views.SQLConsole");

		makeActions();
		hookContextMenu();
		// hookDoubleClickAction();
		// contributeToActionBars();

	}

	protected abstract AbstractTextEditorWidget createSQLEditorWidget(SashForm sashForm);

	@Override
	public void setFocus() {
		scriptArea.setFocus();
	}

	private void makeActions() {
		actionExecuteQuery = new Action() {
			private static final long serialVersionUID = -4666336820729503841L;

			@Override
			public void run() {
				executeStatement(true);
			}
		};
		actionExecuteQuery.setText(EXECUTE_QUERY);
		actionExecuteQuery.setToolTipText(EXECUTE_QUERY_STATEMENT);

		actionExecuteUpdate = new Action() {
			private static final long serialVersionUID = -4666336820729503841L;

			@Override
			public void run() {
				executeStatement(false);
			}
		};
		actionExecuteUpdate.setText(EXECUTE_UPDATE_TEXT);
		actionExecuteUpdate.setToolTipText(EXECUTE_UPDATE_STATEMENT);
	}

	@Override
	public void executeStatement(boolean isQuery) {

		String sql = scriptArea.getText();
		if ((sql == null) || (sql.length() == 0)) {
			return;
		}

		StringTokenizer tokenizer = new StringTokenizer(sql, SCRIPT_DELIMITER);
		while (tokenizer.hasMoreTokens()) {
			String line = tokenizer.nextToken();
			if (EMPTY.equals(line.trim())) {
				continue;
			}
			executeSingleStatement(line, isQuery);
		}

	}

	private void executeSingleStatement(String sql, boolean isQuery) {

		try {
			Connection connection = getConnection();
			try {

				PreparedStatement preparedStatement = connection.prepareStatement(sql);

				if (isQuery) {
					preparedStatement.executeQuery();
					ResultSet resultSet = preparedStatement.getResultSet();
					printResultSet(resultSet);
				} else {
					preparedStatement.executeUpdate();
					printUpdateCount(preparedStatement.getUpdateCount());
				}
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			outputArea.setText(e.getMessage());
		}
	}

	protected Connection getConnection() throws Exception {
		return DatabaseViewer.getConnectionFromSelectedDatasource();
	}

	private void printResultSet(ResultSet resultSet) throws SQLException {
		/*
		 * StringBuffer buff = new StringBuffer();
		 * // header
		 * int headerLength = 0;
		 * ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
		 * for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
		 * String columnLabel = resultSetMetaData.getColumnLabel(i);
		 * String columnLabelPrint = null;
		 * int columnType = resultSetMetaData.getColumnType(i);
		 * if (isBinaryType(columnType)) {
		 * columnLabelPrint = prepareStringForSize(columnLabel, BINARY.length(), SPACE);
		 * } else {
		 * columnLabelPrint = prepareStringForSize(columnLabel, resultSetMetaData.getColumnDisplaySize(i), SPACE);
		 * }
		 * buff.append(columnLabelPrint);
		 * headerLength += columnLabelPrint.length();
		 * }
		 * buff.append(END_DELIMITER);
		 * buff.append(prepareStringForSize(EMPTY, headerLength - 1, MINUS));
		 * buff.append(END_DELIMITER);
		 * // data
		 * int count = 0;
		 * while (resultSet.next()) {
		 * for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
		 * String data = null;
		 * int columnType = resultSetMetaData.getColumnType(i);
		 * if (isBinaryType(columnType)) {
		 * data = BINARY;
		 * } else {
		 * data = resultSet.getString(i);
		 * }
		 * String dataPrint = null;
		 * if (isBinaryType(columnType)) {
		 * dataPrint = prepareStringForSize(data, BINARY.length(), ' ');
		 * } else {
		 * dataPrint = prepareStringForSize(data, resultSetMetaData.getColumnDisplaySize(i), ' ');
		 * }
		 * buff.append(dataPrint);
		 * }
		 * buff.append(END_DELIMITER);
		 * if (++count > 100) {
		 * buff.append(DOTS);
		 * break;
		 * }
		 * }
		 */

		ResultSetStringWriter writer = new ResultSetStringWriter();
		String tableString = writer.writeTable(resultSet);

		outputArea.setText(tableString);
	}

	private boolean isBinaryType(int columnType) {
		for (int c : CommonParameters.BINARY_TYPES) {
			if (columnType == c) {
				return true;
			}
		}
		return false;
	}

	private String prepareStringForSize(String columnLabel, int columnDisplaySize, char c) {
		String result;
		if (columnLabel == null) {
			columnLabel = NULL;
		}
		if (columnLabel.length() == columnDisplaySize) {
			result = columnLabel;
		} else if (columnLabel.length() > columnDisplaySize) {
			result = columnLabel.substring(0, columnDisplaySize);
		} else {
			StringBuffer buff = new StringBuffer();
			buff.append(columnLabel);
			for (int i = 0; i < (columnDisplaySize - columnLabel.length()); i++) {
				buff.append(c);
			}
			result = buff.toString();
		}

		return COLUMN_DELIMITER + result;
	}

	private void printUpdateCount(int updateCount) {
		outputArea.setText(String.format(UPDATE_COUNT_S, updateCount));
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager(POPUP_MENU);
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			private static final long serialVersionUID = 7417283863427269417L;

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				AbstractSQLConsole.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(scriptArea);
		scriptArea.setMenu(menu);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(actionExecuteUpdate);
		manager.add(new Separator());
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	@Override
	public void setQuery(String query) {
		scriptArea.setText(query, getMode(), false, false, 0);
	}

	private EditorMode getMode() {
		return EditorMode.SQL;
	}
}
