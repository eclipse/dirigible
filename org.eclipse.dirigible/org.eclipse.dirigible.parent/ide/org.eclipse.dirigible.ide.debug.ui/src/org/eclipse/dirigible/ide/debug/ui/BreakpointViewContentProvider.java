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

import java.util.Set;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import org.eclipse.dirigible.repository.ext.debug.BreakpointMetadata;
import org.eclipse.dirigible.repository.ext.debug.BreakpointsMetadata;

public class BreakpointViewContentProvider implements ITreeContentProvider {
	
	private static final long serialVersionUID = 5189974338674989869L;

	public BreakpointViewContentProvider(BreakpointsMetadata metadata) {
		this.metadata = metadata;
	}
	
	private BreakpointsMetadata metadata;

	public BreakpointsMetadata getBreakpointMetadata() {
		return metadata;
	}

//	public void setBreakpointMetadata(BreakpointsMetadata metadata) {
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
		Set<BreakpointMetadata> breakpoints = null;

		if (metadata != null) {
			breakpoints = metadata.getBreakpoints();
			if (breakpoints != null) {
				elements = breakpoints.toArray(new BreakpointMetadata[breakpoints.size()]);
			}
		}
		if (elements == null) {
			elements = new BreakpointMetadata[] {};
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
