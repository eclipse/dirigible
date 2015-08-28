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

package org.eclipse.dirigible.ide.debug.ui;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.dirigible.repository.ext.debug.DebugModel;
import org.eclipse.dirigible.repository.ext.debug.DebugSessionModel;
import org.eclipse.dirigible.repository.ext.debug.VariableValue;
import org.eclipse.dirigible.repository.ext.debug.VariableValuesMetadata;

public class VariablesViewContentProvider implements ITreeContentProvider {
	
	private static final long serialVersionUID = -7946214629624017357L;
	
	private DebugModel debugModel;
	
//	private VariableValuesMetadata metadata;
	
	public VariablesViewContentProvider(DebugModel debugModel) {
		this.debugModel = debugModel;
	}

//	public VariableValuesMetadata getVariablesMetaData() {
//		return metadata;
//	}
//
//	public void setVariablesMetaData(VariableValuesMetadata metadata) {
//		this.metadata = metadata;
//	}

	@Override
	public void dispose() {
		//
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		//
	}

	@Override
	public Object[] getElements(Object inputElement) {
		Object[] elements = null;
		
		DebugSessionModel session = this.debugModel.getActiveSession();
		
		if (session != null
				&& session.getVariableValuesMetadata() != null) {
			List<VariableValue> variableValueList = session.getVariableValuesMetadata().getVariableValueList();
			if (variableValueList != null) {
				elements = variableValueList.toArray(new VariableValue[variableValueList.size()]);
			}
		}
		if (elements == null) {
			elements = new VariableValue[] {};
		}
		return elements;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return null;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return false;
	}

}
