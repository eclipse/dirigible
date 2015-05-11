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

package org.eclipse.dirigible.ide.repository.ui.command;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import org.eclipse.dirigible.repository.api.IEntity;

public abstract class AbstractClipboardHandler extends AbstractHandler {

	public static final String CUT = "REPOSITORY_CUT"; //$NON-NLS-1$
	public static final String COPY = "REPOSITORY_COPY"; //$NON-NLS-1$
	public static final String PASTE = "REPOSITORY_PASTE"; //$NON-NLS-1$

	// TODO: Must determine when this command is enabled or not.

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			execute(event, (IStructuredSelection) selection);
		}
		return null;
	}

	public void execute(ExecutionEvent event, IStructuredSelection selection) {
		Comparator<IEntity> comparator = new ResourceComparator();
		SortedSet<IEntity> resources = new TreeSet<IEntity>(comparator);
		for (Object element : selection.toArray()) {
			if (element instanceof IEntity) {
				resources.add((IEntity) element);
			}
		}
		execute(event, resources);
	}

	protected abstract void execute(ExecutionEvent event,
			SortedSet<IEntity> resources);

	private class ResourceComparator implements Comparator<IEntity> {

		@Override
		public int compare(IEntity o1, IEntity o2) {
			String path1 = o1.getPath().toString();
			String path2 = o2.getPath().toString();
			return path2.compareTo(path1);
		}

	}

}
