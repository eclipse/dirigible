/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Austin Riddle (Texas Center for Applied Technology) - RAP implementation
 *     EclipseSource - ongoing development
 *******************************************************************************/
package org.eclipse.swt.widgets;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.lifecycle.UICallBack;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.ExtensionValidationStrategy;
import org.eclipse.swt.internal.widgets.ProgressCollector;
import org.eclipse.swt.internal.widgets.UploadPanel;
import org.eclipse.swt.internal.widgets.ValidationHandler;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

/**
 * Instances of this class allow the user to navigate the file system and select
 * a file name. The selected file will be uploaded to the server and the path
 * made available as the result of the dialog.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd><!--SAVE, OPEN,--> MULTI</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * The OPEN style is applied by default and setting any other styles has no
 * effect. <!--Note: Only one of the styles SAVE and OPEN may be specified.-->
 * </p>
 * <p>
 * IMPORTANT: This class is intended to be subclassed <em>only</em> within the
 * SWT implementation.
 * </p>
 * 
 * @see <a href="http://www.eclipse.org/swt/snippets/#filedialog">FileDialog
 *      snippets</a>
 * @see <a href="http://www.eclipse.org/swt/examples.php">SWT Example:
 *      ControlExample, Dialog tab</a>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further
 *      information</a>
 */
@SuppressWarnings("deprecation")
public class FileDialog extends Dialog {
	private static final String SPECIFY_FILES_IN_ALL_EMPTY_SELECTORS_TO_CONTINUE = Messages.FileDialog_SPECIFY_FILES_IN_ALL_EMPTY_SELECTORS_TO_CONTINUE;

	private static final String WAITING_FOR_UPLOADS_TO_FINISH = Messages.FileDialog_WAITING_FOR_UPLOADS_TO_FINISH;

	private static final String UPLOADING = Messages.FileDialog_UPLOADING;

	private static final String SELECTED_FILENAME_FILTER = Messages.FileDialog_SELECTED_FILENAME_FILTER;

	private static final String ADD_FILE = Messages.FileDialog_ADD_FILE;

	private static final String TOTAL_UPLOAD_PROGRESS = Messages.FileDialog_TOTAL_UPLOAD_PROGRESS;

	private static final long serialVersionUID = 1L;

	// RAP implementation fields taken from JFace
	private final static int HORIZONTAL_DIALOG_UNIT_PER_CHAR = 4;
	private final static int BUTTON_WIDTH = 61;
	// SWT FileDialog API fields
	String[] filterNames = new String[0];
	String[] filterExtensions = new String[0];
	String[] fileNames = new String[0];
	String filterPath = ""; //$NON-NLS-1$
	String fileName;
	int filterIndex;
	boolean overwrite = false;
	// RAP implementation fields
	private java.util.List<UploadPanel> uploadPanels;
	private Button okButton;
	private ProgressBar totalProgressBar;
	private Combo filterSelector;
	private Button addFileSelectorButton;
	private Composite scrollChild;
	private Composite uploadsWrapper;
	private ScrolledComposite uploadScroller;
	private boolean autoUpload;
	private boolean uploadLocked;
	private ProgressCollector progressCollector;
	private transient ValidationHandler validationHandler;
	private Image addImage;

	/**
	 * Constructs a new instance of this class given only its parent.
	 * 
	 * @param parent
	 *            a shell which will be the parent of the new instance
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the parent</li>
	 *                <li>ERROR_INVALID_SUBCLASS - if this class is not an
	 *                allowed subclass</li>
	 *                </ul>
	 */
	public FileDialog(Shell parent) {
		this(parent, SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL);
	}

	/**
	 * Constructs a new instance of this class given its parent and a style
	 * value describing its behavior and appearance.
	 * <p>
	 * The style value is either one of the style constants defined in class
	 * <code>SWT</code> which is applicable to instances of this class, or must
	 * be built by <em>bitwise OR</em>'ing together (that is, using the
	 * <code>int</code> "|" operator) two or more of those <code>SWT</code>
	 * style constants. The class description lists the style constants that are
	 * applicable to the class. Style bits are also inherited from superclasses.
	 * </p>
	 * 
	 * @param parent
	 *            a shell which will be the parent of the new instance
	 * @param style
	 *            the style of dialog to construct
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the parent</li>
	 *                <li>ERROR_INVALID_SUBCLASS - if this class is not an
	 *                allowed subclass</li>
	 *                </ul>
	 */
	public FileDialog(Shell parent, int style) {
		super(parent, checkStyle(parent, style));
		checkSubclass();
	}

	static int checkStyle(Shell parent, int style) {
		int result = style;
		int mask = SWT.PRIMARY_MODAL | SWT.APPLICATION_MODAL | SWT.SYSTEM_MODAL;
		if ((result & SWT.SHEET) != 0) {
			result &= ~SWT.SHEET;
			if ((result & mask) == 0) {
				result |= parent == null ? SWT.APPLICATION_MODAL
						: SWT.PRIMARY_MODAL;
			}
		}
		if ((result & mask) == 0) {
			result |= SWT.APPLICATION_MODAL;
		}
		if ((result & (SWT.LEFT_TO_RIGHT)) == 0) {
			if (parent != null) {
				if ((parent.getStyle() & SWT.LEFT_TO_RIGHT) != 0) {
					result |= SWT.LEFT_TO_RIGHT;
				}
			}
		}
		// [if] force SWT.TITLE as in SWT titlebar is always shown
		result |= SWT.TITLE;
		// [if] Min button has no sense in RAP
		result &= ~SWT.MIN;
		return result;
	}

	/**
	 * Returns the path of the first file that was selected in the dialog
	 * relative to the filter path, or an empty string if no such file has been
	 * selected.
	 * 
	 * @return the relative path of the file
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Returns a (possibly empty) array with the paths of all files that were
	 * selected in the dialog relative to the filter path.
	 * 
	 * @return the relative paths of the files
	 */
	public String[] getFileNames() {
		return Arrays.copyOf(fileNames, fileNames.length);
	}

	/**
	 * Returns the file extensions which the dialog will use to filter the files
	 * it shows.
	 * 
	 * @return the file extensions filter
	 */
	public String[] getFilterExtensions() {
		return Arrays.copyOf(filterExtensions, filterExtensions.length);
	}

	/**
	 * Get the 0-based index of the file extension filter which was selected by
	 * the user, or -1 if no filter was selected.
	 * <p>
	 * This is an index into the FilterExtensions array and the FilterNames
	 * array.
	 * </p>
	 * 
	 * @return index the file extension filter index
	 * @see #getFilterExtensions
	 * @see #getFilterNames
	 */
	public int getFilterIndex() {
		return filterIndex;
	}

	/**
	 * Returns the names that describe the filter extensions which the dialog
	 * will use to filter the files it shows.
	 * 
	 * @return the list of filter names
	 */
	public String[] getFilterNames() {
		return Arrays.copyOf(filterNames, filterNames.length);
	}

	/**
	 * Returns the directory path that the dialog will use, or an empty string
	 * if this is not set. File names in this path will appear in the dialog,
	 * filtered according to the filter extensions.
	 * 
	 * @return the directory path string
	 * @see #setFilterExtensions
	 */
	public String getFilterPath() {
		return filterPath;
	}

	/**
	 * Returns the flag that the dialog will use to determine whether to prompt
	 * the user for file overwrite if the selected file already exists.
	 * 
	 * @return true if the dialog will prompt for file overwrite, false
	 *         otherwise
	 */
	public boolean getOverwrite() {
		return overwrite;
	}

	/**
	 * Set the initial filename which the dialog will select by default when
	 * opened to the argument, which may be null. The name will be prefixed with
	 * the filter path when one is supplied.
	 * 
	 * @param string
	 *            the file name
	 */
	public void setFileName(String string) {
		fileName = string;
	}

	/**
	 * Set the file extensions which the dialog will use to filter the files it
	 * shows to the argument, which may be null.
	 * <p>
	 * The strings are platform specific. For example, on some platforms, an
	 * extension filter string is typically of the form "*.extension", where
	 * "*.*" matches all files. For filters with multiple extensions, use
	 * semicolon as a separator, e.g. "*.jpg;*.png".
	 * </p>
	 * 
	 * @param extensions
	 *            the file extension filter
	 * @see #setFilterNames to specify the user-friendly names corresponding to
	 *      the extensions
	 */
	public void setFilterExtensions(String[] extensions) {
		filterExtensions = extensions;
	}

	/**
	 * Set the 0-based index of the file extension filter which the dialog will
	 * use initially to filter the files it shows to the argument.
	 * <p>
	 * This is an index into the FilterExtensions array and the FilterNames
	 * array.
	 * </p>
	 * 
	 * @param index
	 *            the file extension filter index
	 * @see #setFilterExtensions
	 * @see #setFilterNames
	 */
	public void setFilterIndex(int index) {
		filterIndex = index;
	}

	/**
	 * Sets the names that describe the filter extensions which the dialog will
	 * use to filter the files it shows to the argument, which may be null.
	 * <p>
	 * Each name is a user-friendly short description shown for its
	 * corresponding filter. The <code>names</code> array must be the same
	 * length as the <code>extensions</code> array.
	 * </p>
	 * 
	 * @param names
	 *            the list of filter names, or null for no filter names
	 * @see #setFilterExtensions
	 */
	public void setFilterNames(String[] names) {
		filterNames = names;
	}

	/**
	 * Sets the directory path that the dialog will use to the argument, which
	 * may be null. File names in this path will appear in the dialog, filtered
	 * according to the filter extensions. If the string is null, then the
	 * operating system's default filter path will be used.
	 * <p>
	 * Note that the path string is platform dependent. For convenience, either
	 * '/' or '\' can be used as a path separator.
	 * </p>
	 * 
	 * @param string
	 *            the directory path
	 * @see #setFilterExtensions
	 */
	public void setFilterPath(String string) {
		filterPath = string;
	}

	/**
	 * Sets the flag that the dialog will use to determine whether to prompt the
	 * user for file overwrite if the selected file already exists.
	 * 
	 * @param overwrite
	 *            true if the dialog will prompt for file overwrite, false
	 *            otherwise
	 */
	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	/**
	 * Sets the auto-upload state of the dialog. If true, the dialog will start
	 * uploading files immediately after they are selected. In any case, if the
	 * dialog is canceled, files are not made available to the application.
	 * <p>
	 * <strong>Note:</strong> this method is <em>not</em> part of the SWT API.
	 * It only exists in the RWT version.
	 * </p>
	 * 
	 * @param autoUpload
	 *            <code>true</code> to set the dialog into autoupload mode,
	 *            <code>false</code> otherwise
	 */
	public void setAutoUpload(boolean autoUpload) {
		this.autoUpload = autoUpload;
	}

	/**
	 * Returns the auto-upload state of the dialog. If true, the dialog will
	 * upload files automatically as they are selected.
	 * <p>
	 * <strong>Note:</strong> this method is <em>not</em> part of the SWT API.
	 * It only exists in the RWT version.
	 * </p>
	 * 
	 * @return <code>true</code> if the dialog is configured to auto upload
	 *         files, <code>false</code> otherwise
	 */
	public boolean getAutoUpload() {
		return autoUpload;
	}

	public String open() {
		prepareOpen();
		runEventLoop(shell);
		return fileName;
	}

	@Override
	protected void prepareOpen() {
		UICallBack.activate(FileDialog.class.getName() + hashCode());
		initializeDefaults();
		createShell();
		createControls();
		layoutAndCenterShell();
	}

	private void initializeDefaults() {
		uploadPanels = new ArrayList<UploadPanel>();
		uploadLocked = false;
		// [ar] - add a strategy for content type?
		ExtensionValidationStrategy validationStrategy = new ExtensionValidationStrategy(
				filterExtensions, filterIndex);
		validationHandler = new ValidationHandler() {
			@Override
			public void updateEnablement() {
				FileDialog.this.updateEnablement();
			}
		};
		validationHandler.addValidationStrategy(validationStrategy);
		progressCollector = new ProgressCollector(validationHandler);
		validationHandler.setNumUploads(uploadPanels.size());
	}

	private void createShell() {
		// int style = SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL;
		shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		shell.addShellListener(new ShellAdapter() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 8638215194422140522L;

			@Override
			public void shellClosed(ShellEvent e) {
				handleShellClose();
			}
		});
	}

	private void layoutAndCenterShell() {
		Point prefSize = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		prefSize.x += 50;
		prefSize.y += 10;
		if (allowMultiple()) {
			// leave room for five upload slots + add button
			prefSize.y += 100;
		}
		shell.setSize(prefSize);
		shell.setMinimumSize(prefSize);
		Rectangle parentSize = getParent().getBounds();
		int locationX = (parentSize.width - prefSize.x) / 2 + parentSize.x;
		int locationY = (parentSize.height - prefSize.y) / 2 + parentSize.y;
		shell.setLocation(locationX, locationY);
	}

	private void createControls() {
		GridLayout mainLayout = new GridLayout();
		mainLayout.marginWidth = 10;
		mainLayout.marginHeight = 10;
		mainLayout.horizontalSpacing = 10;
		mainLayout.verticalSpacing = 10;
		shell.setLayout(mainLayout);
		createDialogArea(shell);
		createButtonArea(shell);
	}

	private boolean allowMultiple() {
		return (getStyle() & (SWT.MULTI)) != 0;
	}

	private Control createDialogArea(Composite parentComposite) {
		// create a composite with standard margins and spacing
		Composite main = new Composite(parentComposite, SWT.NONE);
		main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(1, true);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		main.setLayout(layout);
		if (allowMultiple()) {
			createMultiSelector(main);
		} else {
			createSingleSelector(main);
		}
		Composite footerComp = new Composite(main, SWT.NONE);
		GridLayout footerLayout = new GridLayout(2, false);
		footerLayout.marginWidth = 0;
		footerLayout.marginHeight = 0;
		footerComp.setLayout(footerLayout);
		footerComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		if (allowMultiple()) {
			createAddSelectorButton(scrollChild);
		}
		filterSelector = createFilterSelector(footerComp);
		filterSelector.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false));
		totalProgressBar = new ProgressBar(footerComp, SWT.HORIZONTAL
				| SWT.SMOOTH);
		totalProgressBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false));
		totalProgressBar.setToolTipText(TOTAL_UPLOAD_PROGRESS);
		totalProgressBar.setMaximum(100);
		progressCollector.setProgressBar(totalProgressBar);
		return main;
	}

	private void createSingleSelector(Composite main) {
		UploadPanel uploadPanel = new UploadPanel(main, UploadPanel.FULL);
		uploadPanel.setValidationHandler(validationHandler);
		uploadPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		uploadPanel.setProgressCollector(progressCollector);
		uploadPanel.setAutoUpload(getAutoUpload());
		uploadPanels.add(uploadPanel);
		validationHandler.setNumUploads(uploadPanels.size());
	}

	private void createMultiSelector(Composite main) {
		uploadScroller = new ScrolledComposite(main, SWT.V_SCROLL | SWT.BORDER);
		uploadScroller.setExpandHorizontal(true);
		uploadScroller.setExpandVertical(true);
		GridData uploadScrollerLayoutData = new GridData(SWT.FILL, SWT.FILL,
				true, true);
		uploadScroller.setLayoutData(uploadScrollerLayoutData);
		scrollChild = new Composite(uploadScroller, SWT.NONE);
		GridLayout scrollChildLayout = new GridLayout(1, true);
		scrollChild.setLayout(scrollChildLayout);
		uploadsWrapper = new Composite(scrollChild, SWT.NONE);
		GridLayout uploadWrapperLayout = new GridLayout(1, true);
		// [if] marginBottom = 1 is needed to avoid default composite size (64)
		// if it is empty
		uploadWrapperLayout.marginBottom = 1;
		uploadWrapperLayout.marginWidth = 0;
		uploadWrapperLayout.marginHeight = 0;
		uploadsWrapper.setLayout(uploadWrapperLayout);
		uploadsWrapper.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,
				false));
		addUploadPanel();
		uploadScroller.setContent(scrollChild);
		uploadScroller.setMinSize(scrollChild.computeSize(SWT.DEFAULT,
				SWT.DEFAULT));
	}

	private void createAddSelectorButton(Composite parent) {
		addFileSelectorButton = new Button(parent, SWT.PUSH);
		if (addImage == null) {
			addImage = Graphics.getImage("resources/add_obj.gif", getClass() //$NON-NLS-1$
					.getClassLoader());
		}
		addFileSelectorButton.setImage(addImage);
		addFileSelectorButton.setToolTipText(ADD_FILE);
		addFileSelectorButton.addSelectionListener(new SelectionAdapter() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -1589894572947835804L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				final UploadPanel uploadPanel = addUploadPanel();
				progressCollector.updateTotalProgress();
				uploadScroller.setMinSize(scrollChild.computeSize(SWT.DEFAULT,
						SWT.DEFAULT));
				// [if] workaround for ScrolledComposite scroll issue - see bug
				// 349301 and 349301
				uploadPanel.setEnabled(false);
				uploadPanel.setVisible(false);
				uploadScroller.getDisplay().timerExec(10, new Runnable() {
					public void run() {
						uploadPanel.setEnabled(true);
						uploadPanel.setVisible(true);
						int scrollTop = scrollChild.getSize().y
								- uploadScroller.getClientArea().y;
						uploadScroller.setOrigin(0, Math.max(0, scrollTop));
					}
				});
			}
		});
	}

	private UploadPanel addUploadPanel() {
		int uploadPanelStyle = UploadPanel.COMPACT | UploadPanel.PROGRESS
				| UploadPanel.REMOVEABLE;
		final UploadPanel uploadPanel = new UploadPanel(uploadsWrapper,
				uploadPanelStyle);
		uploadPanel.addDisposeListener(new DisposeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -2865569050297236847L;

			public void widgetDisposed(DisposeEvent event) {
				Display.getCurrent().asyncExec(new Runnable() {
					public void run() {
						if (!uploadsWrapper.isDisposed()) {
							uploadPanels.remove(uploadPanel);
							validationHandler.setNumUploads(uploadPanels.size());
							scrollChild.pack(true);
							progressCollector.updateTotalProgress();
							uploadScroller.setMinSize(scrollChild.computeSize(
									SWT.DEFAULT, SWT.DEFAULT));
							updateEnablement();
						}
					}
				});
			}
		});
		uploadPanel.setValidationHandler(validationHandler);
		uploadPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		uploadPanel.setProgressCollector(progressCollector);
		uploadPanel.setAutoUpload(getAutoUpload());
		uploadPanels.add(uploadPanel);
		validationHandler.setNumUploads(uploadPanels.size());
		scrollChild.pack(true);
		updateEnablement();
		return uploadPanel;
	}

	private Combo createFilterSelector(Composite headerComp) {
		final Combo filterCombo = new Combo(headerComp, SWT.DROP_DOWN
				| SWT.READ_ONLY);
		filterCombo.setToolTipText(SELECTED_FILENAME_FILTER);
		String[] names = getFilterNames();
		String[] exts = getFilterExtensions();
		if ((names == null || names.length == 0)
				&& (exts == null || exts.length == 0)) {
			names = new String[] { Messages.FileDialog_ALL_FILES };
			exts = new String[] { "*.*" }; //$NON-NLS-1$
		}
		for (int i = 0; i < exts.length; i++) {
			StringBuffer sb = new StringBuffer();
			if (names != null && i < names.length) {
				sb.append(names[i]).append(" "); //$NON-NLS-1$
			}
			sb.append("("); //$NON-NLS-1$
			sb.append(exts[i]);
			sb.append(")"); //$NON-NLS-1$
			filterCombo.add(sb.toString());
		}
		filterCombo.select(getFilterIndex());
		filterCombo.addSelectionListener(new SelectionAdapter() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -1546342534378262751L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				setFilterIndex(filterCombo.getSelectionIndex());
				for (int i = 0; i < uploadPanels.size(); i++) {
					UploadPanel panel = uploadPanels.get(i);
					panel.validate();
				}
			}
		});
		return filterCombo;
	}

	private void createButtonArea(Composite parent) {
		Composite buttonComposite = new Composite(parent, SWT.NONE);
		GridData layoutData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		layoutData.horizontalSpan = 2;
		buttonComposite.setLayoutData(layoutData);
		GridLayout buttonCompLayout = new GridLayout(2, false);
		buttonCompLayout.marginWidth = 0;
		buttonCompLayout.marginHeight = 0;
		buttonComposite.setLayout(buttonCompLayout);
		String okText = SWT.getMessage("SWT_OK"); //$NON-NLS-1$
		okButton = createButton(buttonComposite, okText);
		parent.getShell().setDefaultButton(okButton);
		okButton.forceFocus();
		String cancelText = SWT.getMessage("SWT_Cancel"); //$NON-NLS-1$
		Button cancelButton = createButton(buttonComposite, cancelText);
		okButton.addSelectionListener(new SelectionAdapter() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 2782032774699131505L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				startUploads();
			}
		});
		cancelButton.addSelectionListener(new SelectionAdapter() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -5858595397576869062L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				closeShell();
			}
		});
		updateEnablement();
	}

	// [if] Calling shell.close() in the selection listener directly throws
	// IllegalAccessError
	// see bug 253818
	private void closeShell() {
		shell.close();
	}

	private Button createButton(Composite parent, String text) {
		Button result = new Button(parent, SWT.PUSH);
		result.setText(text);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		Font dialogFont = result.getFont();
		float charWidth = Graphics.getAvgCharWidth(dialogFont);
		float width = charWidth * BUTTON_WIDTH
				+ HORIZONTAL_DIALOG_UNIT_PER_CHAR / 2;
		int widthHint = (int) (width / HORIZONTAL_DIALOG_UNIT_PER_CHAR);
		Point minSize = result.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		data.widthHint = Math.max(widthHint, minSize.x);
		result.setLayoutData(data);
		return result;
	}

	private void startUploads() {
		java.util.List<UploadPanel> needsProcessing = new ArrayList<UploadPanel>();
		for (int i = 0; i < uploadPanels.size(); i++) {
			UploadPanel panel = uploadPanels.get(i);
			if (!panel.isFinished()) {
				needsProcessing.add(panel);
			}
		}
		uploadLocked = true;
		if (needsProcessing.size() > 0) {
			okButton.setText(UPLOADING);
			okButton.setToolTipText(WAITING_FOR_UPLOADS_TO_FINISH);
			okButton.setEnabled(false);
			if (addFileSelectorButton != null) {
				addFileSelectorButton.setEnabled(false);
			}
			for (int i = 0; i < needsProcessing.size(); i++) {
				UploadPanel panel = needsProcessing.get(i);
				if (!panel.isStarted()) {
					panel.startUpload();
				}
				panel.setEnabled(false);
			}
		}
		updateEnablement();
	}

	private void updateEnablement() {
		if (okButton != null && !okButton.isDisposed()) {
			String okText = SWT.getMessage("SWT_OK"); //$NON-NLS-1$
			if (uploadPanels.size() == 0) {
				okButton.setText(okText);
				okButton.setEnabled(false);
			} else if (uploadPanels.size() > 0) {
				boolean enabled = true;
				if (uploadLocked) {
					if (progressCollector.isFinished()) {
						okButton.setText(okText);
						fileNames = new String[uploadPanels.size()];
						fileName = null;
						for (int i = 0; i < uploadPanels.size(); i++) {
							UploadPanel uploadPanel = uploadPanels.get(i);
							File uploadedFile = uploadPanel.getUploadedFile();
							// TODO [rst] Understand if file can be null
							if (uploadedFile != null) {
								fileNames[i] = uploadedFile.getAbsolutePath();
							}
							if (fileName == null || fileName.length() == 0) {
								fileName = fileNames[0];
							}
						}
						shell.close();
					} else {
						okButton.setText(UPLOADING);
						okButton.setToolTipText(WAITING_FOR_UPLOADS_TO_FINISH);
						okButton.setEnabled(false);
					}
				} else {
					okButton.setText(okText);
					for (int i = 0; i < uploadPanels.size(); i++) {
						UploadPanel panel = uploadPanels.get(i);
						if (panel.getSelectedFilename().length() == 0) {
							enabled = false;
						}
					}
					if (!enabled) {
						okButton.setToolTipText(SPECIFY_FILES_IN_ALL_EMPTY_SELECTORS_TO_CONTINUE);
					} else {
						okButton.setToolTipText(""); //$NON-NLS-1$
					}
					okButton.setEnabled(enabled);
				}
			}
		}
	}

	private void cleanup() {
		UICallBack.deactivate(FileDialog.class.getName() + hashCode());
	}

	private void handleShellClose() {
		cleanup();
	}
}
