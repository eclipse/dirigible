/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * An operation which delegates its work to a runnable that modifies the
 * workspace.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @noextend This class is not intended to be subclassed by clients.
 */
public class WorkspaceModifyDelegatingOperation extends
		WorkspaceModifyOperation {

	/**
	 * The runnable to delegate work to at execution time.
	 */
	private IRunnableWithProgress content;

	/**
	 * Creates a new operation which will delegate its work to the given
	 * runnable using the provided scheduling rule.
	 * 
	 * @param content
	 *            the runnable to delegate to when this operation is executed
	 * @param rule
	 *            The ISchedulingRule to use or <code>null</code>.
	 */
	public WorkspaceModifyDelegatingOperation(IRunnableWithProgress content,
			ISchedulingRule rule) {
		super(rule);
		this.content = content;
	}

	/**
	 * Creates a new operation which will delegate its work to the given
	 * runnable. Schedule using the supplied s
	 * 
	 * @param content
	 *            the runnable to delegate to when this operation is executed
	 */
	public WorkspaceModifyDelegatingOperation(IRunnableWithProgress content) {
		super();
		this.content = content;
	}

	/*
	 * (non-Javadoc) Method declared on WorkbenchModifyOperation.
	 */
	protected void execute(IProgressMonitor monitor) throws CoreException,
			InterruptedException {
		try {
			content.run(monitor);
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof CoreException) {
				throw (CoreException) e.getTargetException();
			}
			if (e.getTargetException() instanceof RuntimeException) {
				throw (RuntimeException) e.getTargetException();
			}
			if (e.getTargetException() instanceof Error) {
				throw (Error) e.getTargetException();
			}
			e.getTargetException().printStackTrace();
		}
	}
}
