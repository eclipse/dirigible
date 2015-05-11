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

package org.eclipse.dirigible.ide.jgit.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import org.eclipse.dirigible.repository.logging.Logger;

public class CommandHandlerUtils {
	private static final String UNKNOWN_SELECTION_TYPE = Messages.CommandHandlerUtils_UNKNOWN_SELECTION_TYPE;

	public static IProject[] getProjects(ISelection selection, Logger logger) {
		if (!(selection instanceof IStructuredSelection)) {
			logger.error(UNKNOWN_SELECTION_TYPE);
			return new IProject[0];
		}
		final List<IProject> result = new ArrayList<IProject>();
		final IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		for (Object element : structuredSelection.toArray()) {
			if (element instanceof IProject) {
				result.add((IProject) element);
			}
		}
		return result.toArray(new IProject[0]);
	}
}
