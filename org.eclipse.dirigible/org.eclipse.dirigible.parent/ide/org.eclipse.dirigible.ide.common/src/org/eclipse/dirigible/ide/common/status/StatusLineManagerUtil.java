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

package org.eclipse.dirigible.ide.common.status;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

public class StatusLineManagerUtil {

	private static final String PLUGIN_ID = "org.eclipse.dirigible.ide.common"; //$NON-NLS-1$
	private static final String CLEAR_MESSAGES_JOB = "Clear Messages Job"; //$NON-NLS-1$
	private static final int CLEAR_MESSAGE_JOB_DELAY = 10 * 1000;
	
	public static final String ARTIFACT_HAS_BEEN_CREATED = Messages.StatusLineManagerUtil_ARTIFACT_HAS_BEEN_CREATED;
	public static final String ARTIFACT_HAS_BEEN_ACTIVATED = Messages.StatusLineManagerUtil_ARTIFACT_HAS_BEEN_ACTIVATED;
	public static final String ARTIFACT_HAS_BEEN_PUBLISHED = Messages.StatusLineManagerUtil_ARTIFACT_HAS_BEEN_PUBLISHED;

	public static IStatusLineManager getDefaultStatusLineManager() {
		IStatusLineManager statusLineManager;
		IWorkbenchPart workbenchPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getPartService().getActivePart();
		if (workbenchPart instanceof IViewPart) {
			statusLineManager = ((IViewPart) workbenchPart).getViewSite().getActionBars()
					.getStatusLineManager();
		} else if (workbenchPart instanceof IEditorPart) {
			statusLineManager = ((IEditorPart) workbenchPart).getEditorSite().getActionBars()
					.getStatusLineManager();
		} else {
			statusLineManager = new StatusLineManager();
		}
		return statusLineManager;
	}

	public static void clearMessages() {
		UIJob job = new UIJob(CLEAR_MESSAGES_JOB) {

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				final IStatusLineManager statusLineManager = getDefaultStatusLineManager();
				statusLineManager.removeAll();
				statusLineManager.setMessage(null, null);
				statusLineManager.setErrorMessage(null, null);
				return new Status(IStatus.OK, PLUGIN_ID, "");
			}
		};
		job.schedule(CLEAR_MESSAGE_JOB_DELAY);
	}

	public static void setInfoMessage(String message) {
		getDefaultStatusLineManager().removeAll();
		getDefaultStatusLineManager().setMessage(
				JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_INFO), message);
		clearMessages();
	}

	public static void setErrorMessage(String message) {
		getDefaultStatusLineManager().removeAll();
		getDefaultStatusLineManager().setErrorMessage(
				JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_ERROR), message);
		clearMessages();
	}

	public static void setWarningMessage(String message) {
		getDefaultStatusLineManager().removeAll();
		getDefaultStatusLineManager().setMessage(
				JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_WARNING), message);
		clearMessages();
	}

	/*
	 * Prevent instantiation.
	 */
	private StatusLineManagerUtil() {
		super();
	}
}
