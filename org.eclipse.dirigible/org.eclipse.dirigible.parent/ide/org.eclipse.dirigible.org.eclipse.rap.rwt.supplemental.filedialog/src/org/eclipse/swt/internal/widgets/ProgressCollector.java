/*****************************************************************************************
 * Copyright (c) 2010, 2011 Texas Center for Applied Technology (TEES) (TAMUS) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Austin Riddle (Texas Center for Applied Technology) - initial API and implementation
 *    EclipseSource - ongoing development
 *****************************************************************************************/
package org.eclipse.swt.internal.widgets;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.supplemental.fileupload.FileUploadHandler;
import org.eclipse.swt.widgets.ProgressBar;

public class ProgressCollector {

	private static final String TOTAL_UPLOAD_PROGRESS = Messages.ProgressCollector_TOTAL_UPLOAD_PROGRESS;
	private Map<FileUploadHandler, Integer> metrics;
	private ProgressBar totalProgressBar;
	private final ValidationHandler validationHandler;

	public ProgressCollector(ValidationHandler validationHandler) {
		this.validationHandler = validationHandler;
		reset();
	}

	public synchronized void updateProgress(FileUploadHandler handler,
			int progressPercent) {
		metrics.put(handler, new Integer(progressPercent));
		updateTotalProgress();
	}

	public void updateTotalProgress() {
		if (validationHandler != null) {
			double maxProgress = validationHandler.getNumUploads() * 100;
			int totalProgress = calculateTotalProgress();
			if (totalProgressBar != null && !totalProgressBar.isDisposed()) {
				int percent = (int) Math.floor(totalProgress / maxProgress
						* 100);
				totalProgressBar.setSelection(percent);
				totalProgressBar.setToolTipText(TOTAL_UPLOAD_PROGRESS + percent
						+ "%"); //$NON-NLS-1$
			}
			if (maxProgress == totalProgress) {
				validationHandler.updateEnablement();
			}
		}
	}

	private int calculateTotalProgress() {
		Object[] progressTallies = metrics.values().toArray();
		int totalProgress = 0;
		for (int i = 0; i < metrics.size(); i++) {
			totalProgress += ((Integer) progressTallies[i]).intValue();
		}
		return totalProgress;
	}

	public boolean isFinished() {
		int totalProgress = calculateTotalProgress();
		int maxProgress = validationHandler.getNumUploads() * 100;
		return totalProgress == maxProgress;
	}

	public void reset() {
		metrics = new HashMap<FileUploadHandler, Integer>();
		if (totalProgressBar != null && !totalProgressBar.isDisposed()) {
			totalProgressBar.setMinimum(0);
		}
	}

	public void setProgressBar(ProgressBar progressBar) {
		totalProgressBar = progressBar;
	}
}