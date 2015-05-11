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
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.SubStatusLineManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.widgets.Control;

public class DefaultProgressMonitor implements IProgressMonitor {

	private final IStatusLineManager statusLineManager;

	public DefaultProgressMonitor() {
		this.statusLineManager = StatusLineManagerUtil
				.getDefaultStatusLineManager();
	}

	@Override
	public void beginTask(String name, int totalWork) {
		this.statusLineManager.setCancelEnabled(true);
		delegate().beginTask(name, totalWork);
		this.statusLineManager.update(true);
		if (statusLineManager instanceof SubStatusLineManager) {
			((SubStatusLineManager) statusLineManager).getParent().update(true);
		}
	}

	private IProgressMonitor delegate() {
		return statusLineManager.getProgressMonitor();
	}

	@Override
	public void done() {
		delegate().done();
		this.statusLineManager.update(true);
	}

	@Override
	public void internalWorked(double work) {
		delegate().internalWorked(work);
	}

	@Override
	public boolean isCanceled() {
		return delegate().isCanceled();
	}

	@Override
	public void setCanceled(boolean value) {
		delegate().setCanceled(value);
	}

	@Override
	public void setTaskName(String name) {
		delegate().setTaskName(name);
	}

	@Override
	public void subTask(String name) {
		delegate().subTask(name);
	}

	@Override
	public void worked(int work) {
		delegate().worked(work);
	}

	public Control getControl() {
		if (statusLineManager instanceof StatusLineManager)
			return ((StatusLineManager) statusLineManager).getControl();
		return null;
	}

	public void setMessage(final String message) {
		this.statusLineManager.setMessage(message);

	}

	/**
	 * Sets the message, along with a image corresponding to error/warning/info
	 * severity. If another argument is provided in severity, the method
	 * returns.
	 */
	public void setMessage(final String message, final int severity) {
		String imageCode = null;
		switch (severity) {
		case (IStatus.INFO):
			imageCode = Dialog.DLG_IMG_MESSAGE_INFO;
			break;
		case (IStatus.WARNING):
			imageCode = Dialog.DLG_IMG_MESSAGE_WARNING;
			break;
		case (IStatus.ERROR):
			imageCode = Dialog.DLG_IMG_MESSAGE_ERROR;
			break;
		}
		if (imageCode == null) {
			return;
		} else {
			this.statusLineManager.setMessage(
					JFaceResources.getImage(imageCode), message);
		}
	}

}
