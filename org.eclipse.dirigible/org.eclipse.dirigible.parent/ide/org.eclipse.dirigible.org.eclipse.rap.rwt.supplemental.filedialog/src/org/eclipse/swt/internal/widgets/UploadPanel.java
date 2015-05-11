/*****************************************************************************************
 * Copyright (c) 2010, 2012 Texas Center for Applied Technology (TEES) (TAMUS) and others.
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

import java.io.File;

import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.supplemental.fileupload.DiskFileUploadReceiver;
import org.eclipse.rap.rwt.supplemental.fileupload.FileUploadEvent;
import org.eclipse.rap.rwt.supplemental.fileupload.FileUploadHandler;
import org.eclipse.rap.rwt.supplemental.fileupload.FileUploadListener;
import org.eclipse.rap.rwt.supplemental.fileupload.FileUploadReceiver;
import org.eclipse.rap.rwt.widgets.FileUpload;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;

@SuppressWarnings("deprecation")
public class UploadPanel extends Composite implements FileUploadListener {
	private static final String UPLOAD_PROGRESS2 = Messages.UploadPanel_UPLOAD_PROGRESS2;

	private static final String WARNING_SELECTED_FILE_DOES_NOT_MATCH_FILTER = Messages.UploadPanel_WARNING_SELECTED_FILE_DOES_NOT_MATCH_FILTER;

	private static final String REMOVE_FILE = Messages.UploadPanel_REMOVE_FILE;

	private static final String SELECT_A_FILE = Messages.UploadPanel_SELECT_A_FILE;

	private static final String BROWSE = Messages.UploadPanel_BROWSE;

	private static final String UPLOAD_PROGRESS = Messages.UploadPanel_UPLOAD_PROGRESS;

	private static final String SELECTED_FILE = Messages.UploadPanel_SELECTED_FILE;

	private static final long serialVersionUID = 1L;

	public static final int COMPACT = 1;
	public static final int FULL = 2;
	public static final int REMOVEABLE = 4;
	public static final int PROGRESS = 8;
	private final int panelStyle;
	private final FileUploadHandler handler;
	private ValidationHandler validationHandler;
	private ProgressCollector progressCollector;
	private FileUpload browseButton;
	private Text fileText;
	private ProgressBar progressBar;
	private Label progressLabel;
	private Button removeButton;
	private boolean inProgress;
	private File uploadedFile;
	private String contentType;
	private boolean autoUpload;
	private Image deleteImage;

	public UploadPanel(Composite parent, int style) {
		super(parent, checkStyle(style));
		panelStyle = style;
		FileUploadReceiver receiver = new DiskFileUploadReceiver();
		handler = new FileUploadHandler(receiver);
		createChildren();
	}

	public void addSelectionListener(SelectionListener listener) {
		checkWidget();
		browseButton.addSelectionListener(listener);
	}

	public void setValidationHandler(ValidationHandler validationHandler) {
		this.validationHandler = validationHandler;
	}

	@Override
	public void setEnabled(boolean enabled) {
		checkWidget();
		super.setEnabled(enabled);
		browseButton.setEnabled(enabled);
		fileText.setEnabled(enabled);
		if (removeButton != null) {
			removeButton.setEnabled(enabled);
		}
	}

	public boolean isFinished() {
		return false;
	}

	public String getSelectedFilename() {
		checkWidget();
		return fileText.getText();
	}

	public String getContentType() {
		return contentType;
	}

	public File getUploadedFile() {
		return uploadedFile;
	}

	public void startUpload() {
		checkWidget();
		inProgress = true;
		String url = handler.getUploadUrl();
		handler.addUploadListener(this);
		browseButton.submit(url);
	}

	@Override
	public void dispose() {
		handler.removeUploadListener(this);
		handler.dispose();
		super.dispose();
	}

	public void setProgressCollector(ProgressCollector progressCollector) {
		this.progressCollector = progressCollector;
	}

	public void setAutoUpload(boolean autoUpload) {
		this.autoUpload = autoUpload;
	}

	public boolean isStarted() {
		return inProgress;
	}

	static int checkStyle(int style) {
		int mask = COMPACT | FULL | REMOVEABLE | PROGRESS;
		return style & mask;
	}

	private boolean hasStyle(int testStyle) {
		return (panelStyle & (testStyle)) != 0;
	}

	private void createChildren() {
		GridLayout layout = new GridLayout(5, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);
		fileText = new Text(this, SWT.BORDER);
		fileText.setToolTipText(SELECTED_FILE);
		fileText.setEditable(false);
		if (hasStyle(PROGRESS)) {
			progressBar = new ProgressBar(this, SWT.HORIZONTAL | SWT.SMOOTH);
			progressBar.setToolTipText(UPLOAD_PROGRESS);
			progressBar.setMinimum(0);
			progressBar.setMaximum(100);
			progressLabel = new Label(this, SWT.NONE);
			progressLabel.setText(progressBar.getSelection() + "%"); //$NON-NLS-1$
		}
		browseButton = new FileUpload(this, SWT.BORDER);
		browseButton.setText(BROWSE);
		browseButton.setToolTipText(SELECT_A_FILE);
		browseButton.addSelectionListener(new SelectionAdapter() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 7003175115027989510L;

			@Override
			public void widgetSelected(SelectionEvent event) {
				String filename = browseButton.getFileName();
				fileText.setText(filename);
				validate();
				if (autoUpload) {
					startUpload();
				}
			}
		});
		if (hasStyle(REMOVEABLE)) {
			removeButton = new Button(this, SWT.PUSH);
			Image removeIcon = Display.getCurrent().getSystemImage(
					SWT.ICON_CANCEL);
			removeButton.setImage(removeIcon);
			if (deleteImage == null) {
				deleteImage = Graphics.getImage("resources/delete_obj.gif", //$NON-NLS-1$
						getClass().getClassLoader());
			}
			removeButton.setImage(deleteImage);
			removeButton.setToolTipText(REMOVE_FILE);
			removeButton.addSelectionListener(new SelectionAdapter() {
				/**
				 * 
				 */
				private static final long serialVersionUID = -3555474065927976901L;

				@Override
				public void widgetSelected(SelectionEvent e) {
					if (progressCollector != null) {
						progressCollector.updateProgress(handler, 0);
					}
					dispose();
				}
			});
		}
		layoutChildren();
	}

	private void layoutChildren() {
		checkWidget();
		if (hasStyle(COMPACT)) {
			browseButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
					false));
			GridData textLayoutData = new GridData(SWT.FILL, SWT.FILL, true,
					false);
			textLayoutData.minimumWidth = 186;
			fileText.setLayoutData(textLayoutData);
			if (progressBar != null) {
				GridData progressLayoutData = new GridData(SWT.FILL, SWT.FILL,
						false, false);
				progressLayoutData.minimumWidth = 48;
				progressLayoutData.widthHint = 128;
				progressBar.setLayoutData(progressLayoutData);
				GridData lblLayoutData = new GridData(SWT.FILL, SWT.FILL,
						false, false);
				float avgCharWidth = Graphics.getAvgCharWidth(progressLabel
						.getFont());
				lblLayoutData.minimumWidth = (int) avgCharWidth * 6;
				lblLayoutData.widthHint = (int) avgCharWidth * 6;
				progressLabel.setLayoutData(lblLayoutData);
			}
			if (removeButton != null) {
				removeButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
						false, false));
			}
		} else {
			browseButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
					false));
			GridData textLayoutData = new GridData(SWT.FILL, SWT.FILL, true,
					false);
			textLayoutData.minimumWidth = 186;
			textLayoutData.horizontalSpan = 4;
			fileText.setLayoutData(textLayoutData);
			if (progressBar != null) {
				GridData progressLayoutData = new GridData(SWT.FILL, SWT.FILL,
						true, false);
				progressLayoutData.horizontalSpan = 4;
				progressBar.setLayoutData(progressLayoutData);
				GridData lblLayoutData = new GridData(SWT.FILL, SWT.FILL,
						false, false);
				float avgCharWidth = Graphics.getAvgCharWidth(progressLabel
						.getFont());
				lblLayoutData.minimumWidth = (int) avgCharWidth * 6;
				lblLayoutData.widthHint = (int) avgCharWidth * 6;
				progressLabel.setLayoutData(lblLayoutData);
			}
			if (removeButton != null) {
				removeButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
						false, false));
			}
		}
	}

	public void validate() {
		if (validationHandler == null
				|| validationHandler.validate(fileText.getText())) {
			fileText.setToolTipText(SELECTED_FILE);
			// TODO replace this with something from theming
			fileText.setBackground(null);
		} else {
			fileText.setToolTipText(WARNING_SELECTED_FILE_DOES_NOT_MATCH_FILTER);
			// TODO replace this with something from theming
			fileText.setBackground(Display.getCurrent().getSystemColor(
					SWT.COLOR_YELLOW));
			validationHandler.updateEnablement();
		}
	}

	public void uploadProgress(final FileUploadEvent uploadEvent) {
		// checkWidget();
		browseButton.getDisplay().asyncExec(new Runnable() {
			public void run() {
				double fraction = uploadEvent.getBytesRead()
						/ (double) uploadEvent.getContentLength();
				int percent = (int) Math.floor(fraction * 100);
				if (progressBar != null && !progressBar.isDisposed()) {
					progressBar.setSelection(percent);
					progressBar
							.setToolTipText(UPLOAD_PROGRESS2 + percent + "%"); //$NON-NLS-1$
					progressLabel.setText(percent + "%"); //$NON-NLS-1$
				}
				// allow the uploadFinished call to notify collector of 100%
				// progress since
				// the file is actually written then
				if (progressCollector != null && percent < 100) {
					progressCollector.updateProgress(handler, percent);
				}
			}
		});
	}

	public void uploadFinished(final FileUploadEvent uploadEvent) {
		// checkWidget();
		DiskFileUploadReceiver receiver = (DiskFileUploadReceiver) handler
				.getReceiver();
		uploadedFile = receiver.getTargetFile();
		contentType = uploadEvent.getContentType();
		browseButton.getDisplay().asyncExec(new Runnable() {
			public void run() {
				int percent = 100;
				if (progressBar != null && !progressBar.isDisposed()) {
					progressBar.setSelection(percent);
					progressBar
							.setToolTipText(UPLOAD_PROGRESS2 + percent + "%"); //$NON-NLS-1$
					progressLabel.setText(percent + "%"); //$NON-NLS-1$
				}
				if (progressCollector != null) {
					progressCollector.updateProgress(handler, percent);
				}
			}
		});
	}

	public void uploadFailed(final FileUploadEvent uploadEvent) {
		// checkWidget();
		uploadedFile = null;
		contentType = null;
		browseButton.getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (progressBar != null && !progressBar.isDisposed()) {
					progressBar.setState(SWT.ERROR);
					progressBar.setToolTipText(uploadEvent.getException()
							.getMessage());
				}
			}
		});
	}
}