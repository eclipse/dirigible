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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import org.eclipse.dirigible.ide.db.viewer.views.IDatabaseConnectionFactory;
import org.eclipse.dirigible.ide.db.viewer.views.TableDefinition;

public class DbEditorInput implements IEditorInput {

	private static final String DATABASE_METADATA_FOR = Messages.DbEditorInput_DATABASE_METADATA_FOR;
	private TableDefinition tDefinition;
	private IDatabaseConnectionFactory iDbConnectionFactory;

	public DbEditorInput(TableDefinition tDefinition,
			IDatabaseConnectionFactory iDbConnectionFactory) {
		this.tDefinition = tDefinition;
		this.iDbConnectionFactory = iDbConnectionFactory;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	@Override
	public String getName() {
		return tDefinition.getTableName();
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return DATABASE_METADATA_FOR + tDefinition.getTableName();
	}

	public String getTableName() {
		return tDefinition.getTableName();
	}

	public TableDefinition getTableDefinition() {
		return tDefinition;
	}

	public IDatabaseConnectionFactory getDbConnectionFactory() {
		return iDbConnectionFactory;
	}

	@Override
	public boolean equals(Object obj) {
		if (DbEditorInput.class.isInstance(obj)) {
			DbEditorInput nInput = (DbEditorInput) obj;
			if (nInput.getTableDefinition().equals(getTableDefinition())) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.getTableDefinition().hashCode();
	}

}