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
package org.eclipse.ui.internal.ide;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;

/**
 * Utility class to create status objects.
 * 
 * PRIVATE This class is an internal implementation class and should not be
 * referenced or sub-classed outside of the workbench
 * 
 * @since 3.0
 */
public class StatusUtil {

	/**
	 * Answer a flat collection of the passed status and its recursive children
	 */
	protected static List<IStatus> flatten(IStatus aStatus) {
		List<IStatus> result = new ArrayList<IStatus>();

		if (aStatus.isMultiStatus()) {
			IStatus[] children = aStatus.getChildren();
			for (int i = 0; i < children.length; i++) {
				IStatus currentChild = children[i];
				if (currentChild.isMultiStatus()) {
					Iterator<IStatus> childStatiiEnum = flatten(currentChild).iterator();
					while (childStatiiEnum.hasNext()) {
						result.add(childStatiiEnum.next());
					}
				} else {
					result.add(currentChild);
				}
			}
		} else {
			result.add(aStatus);
		}

		return result;
	}

	/**
	 * This method must not be called outside the workbench.
	 * 
	 * Utility method for creating status.
	 */
	protected static IStatus newStatus(IStatus[] stati, String message,
			Throwable exception) {

		if (message == null || message.trim().length() == 0) {
			throw new IllegalArgumentException();
		}
		return new MultiStatus(IDEWorkbenchPlugin.IDE_WORKBENCH, IStatus.ERROR,
				stati, message, exception);
	}

	/**
	 * This method must not be called outside the workbench.
	 * 
	 * Utility method for creating status.
	 * 
	 * @param severity
	 * @param message
	 * @param exception
	 * @return {@link IStatus}
	 */
	public static IStatus newStatus(int severity, String message,
			Throwable exception) {

		String statusMessage = message;
		if (message == null || message.trim().length() == 0) {
			if (exception == null) {
				throw new IllegalArgumentException();
			} else if (exception.getMessage() == null) {
				statusMessage = exception.toString();
			} else {
				statusMessage = exception.getMessage();
			}
		}

		return new Status(severity, IDEWorkbenchPlugin.IDE_WORKBENCH, severity,
				statusMessage, exception);
	}

	/**
	 * This method must not be called outside the workbench.
	 * 
	 * Utility method for creating status.
	 * 
	 * @param children
	 * @param message
	 * @param exception
	 * @return {@link IStatus}
	 */
	public static IStatus newStatus(List<?> children, String message,
			Throwable exception) {

		List<IStatus> flatStatusCollection = new ArrayList<IStatus>();
		Iterator<?> iter = children.iterator();
		while (iter.hasNext()) {
			IStatus currentStatus = (IStatus) iter.next();
			Iterator<IStatus> childrenIter = flatten(currentStatus).iterator();
			while (childrenIter.hasNext()) {
				flatStatusCollection.add(childrenIter.next());
			}
		}

		IStatus[] stati = new IStatus[flatStatusCollection.size()];
		flatStatusCollection.toArray(stati);
		return newStatus(stati, message, exception);
	}

}
