/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.debug.ui;

import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.ext.debug.DebugModelFacade;
import org.eclipse.dirigible.repository.ext.debug.DebugSessionModel;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class SessionsViewContentProvider implements ITreeContentProvider {
	private static final long serialVersionUID = -7946214629624017357L;

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
		String[] elements = new String[DebugModelFacade.getDebugModel(CommonIDEParameters.getUserName()).getSessions().size()];
		int i = 0;
		for (DebugSessionModel session : DebugModelFacade.getDebugModel(CommonIDEParameters.getUserName()).getSessions()) {
			StringBuilder label = new StringBuilder();
			label.append(session.getUserId()).append(ICommonConstants.DEBUG_SEPARATOR).append(i + 1).append(ICommonConstants.DEBUG_SEPARATOR)
					.append(session.getExecutionId()).append(ICommonConstants.DEBUG_SEPARATOR);
			elements[i++] = label.toString();
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
