/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.db.viewer.views.actions;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import org.eclipse.dirigible.ide.db.viewer.views.IDatabaseConnectionFactory;
import org.eclipse.dirigible.ide.db.viewer.views.TableDefinition;
import org.eclipse.dirigible.ide.db.viewer.views.TreeObject;
import org.eclipse.dirigible.ide.db.viewer.views.TreeParent;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

/**
 * Action that will trigger opening of Table Definition editor.
 */
public class DeleteViewAction extends Action {

	private static final Logger logger = Logger.getLogger(DeleteViewAction.class);

	private static final String DROP_VIEW = "DROP VIEW "; //$NON-NLS-1$

	private static final String DATABASE_VIEW = Messages.DeleteTableAction_DATABASE_VIEW;

	private static final String FAILED_TO_DELETE_TABLE_S = Messages.DeleteTableAction_FAILED_TO_DELETE_TABLE_S;

	private static final String WARNING_THIS_ACTION_WILL_DELETE_THE_VIEW_CONTINUE = Messages.DeleteTableAction_WARNING_THIS_ACTION_WILL_DELETE_THE_VIEW_CONTINUE;

	private static final String WILL_DELETE_THE_VIEW = Messages.DeleteTableAction_WILL_DELETE_THE_VIEW;

	private static final String DELETE_VIEW = Messages.DeleteTableAction_DELETE_VIEW;

	private static final long serialVersionUID = 3872859942737870851L;

	private TreeViewer viewer;

	public DeleteViewAction(TreeViewer viewer) {
		this.viewer = viewer;
		setText(DELETE_VIEW);
		setToolTipText(WILL_DELETE_THE_VIEW);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void run() {
		IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
		TreeParent parent = null;

		for (Iterator nextSelection = selection.iterator(); nextSelection.hasNext();) {
			Object obj = nextSelection.next();
			if (TreeObject.class.isInstance(obj)) {
				if (((TreeObject) obj).getTableDefinition() != null) {
					TableDefinition tableDefinition = ((TreeObject) obj).getTableDefinition();

					boolean confirm = MessageDialog.openConfirm(viewer.getControl().getShell(), DELETE_VIEW,
							String.format(WARNING_THIS_ACTION_WILL_DELETE_THE_VIEW_CONTINUE, tableDefinition.getTableName()));
					if (!confirm) {
						continue;
					}
					parent = ((TreeObject) obj).getParent();
					IDatabaseConnectionFactory connectionFactory = parent.getConnectionFactory();
					try {
						deleteView(tableDefinition, connectionFactory);
					} catch (SQLException e) {
						showMessage(String.format(FAILED_TO_DELETE_TABLE_S, tableDefinition.getTableName()) + "\n\n" + e.getMessage());
						logger.error(e.getMessage(), e);
					}
				}
			}
		}
		if (parent != null) {
			RefreshViewAction refresh = new RefreshViewAction(viewer, parent.getChildren()[0]);
			refresh.run();
		}
	}

	protected void showMessage(String message) {
		MessageDialog.openError(viewer.getControl().getShell(), DATABASE_VIEW, message);
	}

	private void deleteView(TableDefinition tableDefinition, IDatabaseConnectionFactory connectionFactory) throws SQLException {
		Connection connection = connectionFactory.getDatabaseConnection();
		try {
			Statement createStatement = connection.createStatement();
			String name = tableDefinition.getFqn();
			createStatement.execute(DROP_VIEW + name);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}
}
