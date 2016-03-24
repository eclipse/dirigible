/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ui.internal.ide.dialogs;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IPathVariableManager;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.dirigible.ide.workspace.RemoteResourcesPlugin;
// I061150: This next class is no longer available in org.eclipse.rap.jface
// import org.eclipse.jface.internal.RAPDialogUtil;
// import org.eclipse.swt.graphics.FontMetrics;
// import org.eclipse.swt.graphics.GC;
// import org.eclipse.swt.widgets.DirectoryDialog;
// import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.rap.rwt.internal.textsize.TextSizeUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ide.dialogs.PathVariableSelectionDialog;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.filesystem.FileSystemConfiguration;
import org.eclipse.ui.internal.ide.filesystem.FileSystemSupportRegistry;

/**
 * Widget group for specifying a linked file or folder target.
 *
 * @since 2.1
 */
@SuppressWarnings("deprecation")
public class CreateLinkedResourceGroup {
	private static final String EMPTY_STRING = "";

	private Listener listener;

	private String linkTarget = EMPTY_STRING;

	private int type;

	private boolean createLink = false;

	// used to compute layout sizes
	// private FontMetrics fontMetrics;

	// widgets
	private Composite groupComposite;

	private Text linkTargetField;

	private Button browseButton;

	private Button variablesButton;

	private Label resolvedPathLabelText;

	private Label resolvedPathLabelData;

	private final IStringValue updatableResourceName;

	/**
	 * Helper interface intended for updating a string value based on the
	 * currently selected link target.
	 *
	 * @since 3.2
	 */
	public static interface IStringValue {
		/**
		 * Sets the String value.
		 *
		 * @param string
		 *            a non-null String
		 */
		void setValue(String string);

		/**
		 * Gets the String value.
		 *
		 * @return the current value, or <code>null</code>
		 */
		String getValue();
	}

	private String lastUpdatedValue;

	private FileSystemSelectionArea fileSystemSelectionArea;

	/**
	 * Creates a link target group
	 *
	 * @param type
	 *            specifies the type of resource to link to.
	 *            <code>IResource.FILE</code> or <code>IResource.FOLDER</code>
	 * @param listener
	 *            listener to notify when one of the widgets' value is changed.
	 * @param updatableResourceName
	 *            an updatable string value that will be updated to reflect the
	 *            link target's last segment, or <code>null</code>. Updating
	 *            will only happen if the current value of that string is null
	 *            or the empty string, or if it has not been changed since the
	 *            last time it was updated.
	 */
	public CreateLinkedResourceGroup(int type, Listener listener, IStringValue updatableResourceName) {
		this.type = type;
		this.listener = listener;
		this.updatableResourceName = updatableResourceName;
		if (updatableResourceName != null) {
			lastUpdatedValue = updatableResourceName.getValue();
		}
	}

	/**
	 * Creates the widgets
	 *
	 * @param parent
	 *            parent composite of the widget group
	 * @return the widget group
	 */
	public Composite createContents(Composite parent) {
		Font font = parent.getFont();
		initializeDialogUnits(parent);
		// top level group
		groupComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		groupComposite.setLayout(layout);
		groupComposite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.FILL_HORIZONTAL));
		groupComposite.setFont(font);

		final Button createLinkButton = new Button(groupComposite, SWT.CHECK);
		if (type == IResource.FILE) {
			createLinkButton.setText(IDEWorkbenchMessages.CreateLinkedResourceGroup_linkFileButton);
		} else {
			createLinkButton.setText(IDEWorkbenchMessages.CreateLinkedResourceGroup_linkFolderButton);
		}
		createLinkButton.setSelection(createLink);
		createLinkButton.setFont(font);
		SelectionListener selectionListener = new SelectionAdapter() {
			/**
			 *
			 */
			private static final long serialVersionUID = 5033824740408692618L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				createLink = createLinkButton.getSelection();
				browseButton.setEnabled(createLink);
				variablesButton.setEnabled(createLink);
				// Set the required field color if the field is enabled
				linkTargetField.setEnabled(createLink);
				if (fileSystemSelectionArea != null) {
					fileSystemSelectionArea.setEnabled(createLink);
				}

				if (listener != null) {
					listener.handleEvent(new Event());
				}
			}
		};
		createLinkButton.addSelectionListener(selectionListener);

		createLinkLocationGroup(groupComposite, createLink);
		return groupComposite;
	}

	/**
	 * Creates the link target location widgets.
	 *
	 * @param locationGroup
	 *            the parent composite
	 * @param enabled
	 *            sets the initial enabled state of the widgets
	 */
	private void createLinkLocationGroup(Composite locationGroup, boolean enabled) {
		Button button = new Button(locationGroup, SWT.CHECK);
		int indent = button.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;

		button.dispose();

		// linkTargetGroup is necessary to decouple layout from
		// resolvedPathGroup layout
		Composite linkTargetGroup = new Composite(locationGroup, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		linkTargetGroup.setLayout(layout);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalIndent = indent;
		linkTargetGroup.setLayoutData(data);

		// link target location entry field
		linkTargetField = new Text(linkTargetGroup, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = IDialogConstants.ENTRY_FIELD_WIDTH;
		data.horizontalSpan = 2;
		linkTargetField.setLayoutData(data);
		linkTargetField.setEnabled(enabled);
		linkTargetField.addModifyListener(new ModifyListener() {
			/**
			 *
			 */
			private static final long serialVersionUID = -259851859813867177L;

			@Override
			public void modifyText(ModifyEvent e) {
				linkTarget = linkTargetField.getText();
				resolveVariable();
				if (updatableResourceName != null) {
					String value = updatableResourceName.getValue();
					if ((value == null) || value.equals(EMPTY_STRING) || value.equals(lastUpdatedValue)) {
						IPath linkTargetPath = new Path(linkTarget);
						String lastSegment = linkTargetPath.lastSegment();
						if (lastSegment != null) {
							lastUpdatedValue = lastSegment;
							updatableResourceName.setValue(lastSegment);
						}
					}
				}
				if (listener != null) {
					listener.handleEvent(new Event());
				}
			}
		});

		// browse button
		browseButton = new Button(linkTargetGroup, SWT.PUSH);
		browseButton.setText(IDEWorkbenchMessages.CreateLinkedResourceGroup_browseButton);
		browseButton.addSelectionListener(new SelectionAdapter() {
			/**
			 *
			 */
			private static final long serialVersionUID = 4454955026260403382L;

			@Override
			public void widgetSelected(SelectionEvent event) {
				handleLinkTargetBrowseButtonPressed();
			}
		});
		browseButton.setEnabled(enabled);
		setButtonLayoutData(browseButton);

		// variables button
		variablesButton = new Button(linkTargetGroup, SWT.PUSH);
		variablesButton.setText(IDEWorkbenchMessages.CreateLinkedResourceGroup_variablesButton);
		variablesButton.addSelectionListener(new SelectionAdapter() {
			/**
			 *
			 */
			private static final long serialVersionUID = -2134519092656414349L;

			@Override
			public void widgetSelected(SelectionEvent event) {
				handleVariablesButtonPressed();
			}
		});
		variablesButton.setEnabled(enabled);
		setButtonLayoutData(variablesButton);

		createFileSystemSelection(linkTargetGroup, enabled);

		createResolvedPathGroup(locationGroup, indent);

		if (linkTarget != null) {
			linkTargetField.setText(linkTarget);
		}
	}

	/**
	 * Create the file system selection area.
	 *
	 * @param composite
	 * @param enabled
	 *            the initial enablement state.
	 */
	private void createFileSystemSelection(Composite composite, boolean enabled) {

		// Always use the default if that is all there is.
		if (FileSystemSupportRegistry.getInstance().hasOneFileSystem()) {
			return;
		}

		fileSystemSelectionArea = new FileSystemSelectionArea();
		fileSystemSelectionArea.createContents(composite);
		fileSystemSelectionArea.setEnabled(enabled);
	}

	/**
	 * Create the composite for the resolved path.
	 *
	 * @param locationGroup
	 * @param indent
	 */
	private void createResolvedPathGroup(Composite locationGroup, int indent) {
		GridLayout layout;
		GridData data;
		Composite resolvedPathGroup = new Composite(locationGroup, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		resolvedPathGroup.setLayout(layout);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalIndent = indent;
		resolvedPathGroup.setLayoutData(data);

		resolvedPathLabelText = new Label(resolvedPathGroup, SWT.SINGLE);
		resolvedPathLabelText.setText(IDEWorkbenchMessages.CreateLinkedResourceGroup_resolvedPathLabel);
		resolvedPathLabelText.setVisible(false);

		resolvedPathLabelData = new Label(resolvedPathGroup, SWT.SINGLE);
		data = new GridData(GridData.FILL_HORIZONTAL);
		resolvedPathLabelData.setLayoutData(data);
		resolvedPathLabelData.setVisible(false);
	}

	/**
	 * Returns a new status object with the given severity and message.
	 *
	 * @return a new status object with the given severity and message.
	 */
	private IStatus createStatus(int severity, String message) {
		return new Status(severity, IDEWorkbenchPlugin.getDefault().getBundle().getSymbolicName(), severity, message, null);
	}

	/**
	 * Disposes the group's widgets.
	 */
	public void dispose() {
		if ((groupComposite != null) && (groupComposite.isDisposed() == false)) {
			groupComposite.dispose();
		}
	}

	/**
	 * Returns the link target location entered by the user.
	 *
	 * @return the link target location entered by the user. null if the user
	 *         chose not to create a link.
	 */
	public URI getLinkTargetURI() {
		if (!createLink) {
			return null;
		}
		// resolve path variable if we have a relative path
		if (!linkTarget.startsWith("/")) { //$NON-NLS-1$
			IPathVariableManager pathVariableManager = RemoteResourcesPlugin.getWorkspace().getPathVariableManager();
			try {

				URI path = new URI(linkTarget.replace(java.io.File.separatorChar, '/'));
				URI resolved = pathVariableManager.resolveURI(path);
				if (path != resolved) {
					// we know this is a path variable, but return unresolved
					// path so resource will be created with variable intact
					return path;
				}
			} catch (URISyntaxException e) {
				// link target is not a valid URI. Fall through to handle this
				// below
			}
		}

		FileSystemConfiguration configuration = getSelectedConfiguration();
		if (configuration == null) {
			return URIUtil.toURI(linkTarget);
		}
		// validate non-local file system location
		return configuration.getContributor().getURI(linkTarget);
	}

	/**
	 * Opens a file or directory browser depending on the link type.
	 */
	private void handleLinkTargetBrowseButtonPressed() {
		IFileStore store = null;
		String selection = null;
		FileSystemConfiguration config = getSelectedConfiguration();
		@SuppressWarnings("unused")
		boolean isDefault = (config == null) || (FileSystemSupportRegistry.getInstance().getDefaultConfiguration()).equals(config);

		if (linkTarget.length() > 0) {
			store = IDEResourceInfoUtils.getFileStore(linkTarget);
			if (!store.fetchInfo().exists()) {
				store = null;
			}
		}
		if (type == IResource.FILE) {
			// if (isDefault) {
			// FileDialog dialog = new FileDialog(linkTargetField.getShell());
			// dialog.setText(IDEWorkbenchMessages.CreateLinkedResourceGroup_targetSelectionTitle);
			// if (store != null) {
			// if (store.fetchInfo().isDirectory()) {
			// dialog.setFilterPath(linkTarget);
			// } else {
			// dialog.setFileName(linkTarget);
			// }
			// }
			// selection = dialog.open();
			// } else {
			@SuppressWarnings("null")
			URI uri = config.getContributor().browseFileSystem(linkTarget, linkTargetField.getShell());
			if (uri != null) {
				selection = uri.toString();
				// }
			}
		} else {
			String filterPath = null;
			if (store != null) {
				IFileStore path = store;
				if (!store.fetchInfo().isDirectory()) {
					path = store.getParent();
				}
				if (path != null) {
					filterPath = store.toString();
				}
			}

			// if (isDefault) {
			// DirectoryDialog dialog = new DirectoryDialog(linkTargetField
			// .getShell());
			// dialog
			// .setMessage(IDEWorkbenchMessages.CreateLinkedResourceGroup_targetSelectionLabel);
			// if (filterPath != null)
			// dialog.setFilterPath(filterPath);
			// selection = dialog.open();
			// } else {
			String initialPath = IDEResourceInfoUtils.EMPTY_STRING;
			if (filterPath != null) {
				initialPath = filterPath;
			}
			if (config != null) {
				URI uri = config.getContributor().browseFileSystem(initialPath, linkTargetField.getShell());
				if (uri != null) {
					selection = uri.toString();
				}
			}
			// }
		}
		if (selection != null) {
			linkTargetField.setText(selection);
		}
	}

	/**
	 * Return the selected configuration or <code>null</code> if there is not
	 * one selected.
	 *
	 * @return FileSystemConfiguration or <code>null</code>
	 */
	private FileSystemConfiguration getSelectedConfiguration() {
		if (fileSystemSelectionArea == null) {
			return null;
		}
		return fileSystemSelectionArea.getSelectedConfiguration();
	}

	/**
	 * Opens a path variable selection dialog
	 */
	private void handleVariablesButtonPressed() {
		int variableTypes = IResource.FOLDER;

		// allow selecting file and folder variables when creating a
		// linked file
		if (type == IResource.FILE) {
			variableTypes |= IResource.FILE;
		}

		PathVariableSelectionDialog dialog = new PathVariableSelectionDialog(linkTargetField.getShell(), variableTypes);
		if (dialog.open() == IDialogConstants.OK_ID) {
			String[] variableNames = (String[]) dialog.getResult();
			if ((variableNames != null) && (variableNames.length == 1)) {
				linkTargetField.setText(variableNames[0]);
			}
		}
	}

	/**
	 * Initializes the computation of horizontal and vertical dialog units based
	 * on the size of current font.
	 * <p>
	 * This method must be called before <code>setButtonLayoutData</code> is
	 * called.
	 * </p>
	 *
	 * @param control
	 *            a control from which to obtain the current font
	 */
	protected void initializeDialogUnits(Control control) {
		// Compute and store a font metric
		// GC gc = new GC(control);
		// gc.setFont(control.getFont());
		// fontMetrics = gc.getFontMetrics();
		// gc.dispose();
	}

	/**
	 * Tries to resolve the value entered in the link target field as a
	 * variable, if the value is a relative path. Displays the resolved value if
	 * the entered value is a variable.
	 */
	private void resolveVariable() {
		IPathVariableManager pathVariableManager = RemoteResourcesPlugin.getWorkspace().getPathVariableManager();
		IPath path = new Path(linkTarget);
		IPath resolvedPath = pathVariableManager.resolvePath(path);

		if (path.equals(resolvedPath)) {
			resolvedPathLabelText.setVisible(false);
			resolvedPathLabelData.setVisible(false);
		} else {
			resolvedPathLabelText.setVisible(true);
			resolvedPathLabelData.setVisible(true);
		}
		resolvedPathLabelData.setText(resolvedPath.toOSString());
	}

	/**
	 * Sets the <code>GridData</code> on the specified button to be one that is
	 * spaced for the current dialog page units. The method
	 * <code>initializeDialogUnits</code> must be called once before calling
	 * this method for the first time.
	 *
	 * @param button
	 *            the button to set the <code>GridData</code>
	 * @return the <code>GridData</code> set on the specified button
	 */
	private GridData setButtonLayoutData(Button button) {
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		// int widthHint = Dialog.convertHorizontalDLUsToPixels(fontMetrics,
		// IDialogConstants.BUTTON_WIDTH);
		// I061150: Previously used RAPDialogUtil. But as the class is
		// no longer available, I have extracted the method here.
		int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		button.setLayoutData(data);
		return data;
	}

	// I061150: Note: This method replaces the DAPDialogUtil's
	// convertHorizontalDLUsToPixels method as the RAPDialogUtil class
	// is no longer available in newer versions of the org.eclipse.rap.jface
	// package.
	@Deprecated
	private static int convertHorizontalDLUsToPixels(int dlus) {
		return convertHorizontalDLUsToPixels(JFaceResources.getDialogFont(), dlus);
	}

	// I061150: Note: This method replaces the DAPDialogUtil's
	// convertHorizontalDLUsToPixels method as the RAPDialogUtil class
	// is no longer available in newer versions of the org.eclipse.rap.jface
	// package.
	@Deprecated
	private static int convertHorizontalDLUsToPixels(Font font, int dlus) {
		final int HORIZONTAL_DIALOG_UNIT_PER_CHAR = 4;
		// round to the nearest pixel
		float avgCharWidth = TextSizeUtil.getAvgCharWidth(font);
		return (int) (((avgCharWidth * dlus) + (HORIZONTAL_DIALOG_UNIT_PER_CHAR / 2)) / HORIZONTAL_DIALOG_UNIT_PER_CHAR);
	}

	/**
	 * Sets the value of the link target field
	 *
	 * @param target
	 *            the value of the link target field
	 */
	public void setLinkTarget(String target) {
		linkTarget = target;
		if ((linkTargetField != null) && (linkTargetField.isDisposed() == false)) {
			linkTargetField.setText(target);
		}
	}

	/**
	 * Validates the type of the given file against the link type specified in
	 * the constructor.
	 *
	 * @param linkTargetFile
	 *            file to validate
	 * @return IStatus indicating the validation result. IStatus.OK if the given
	 *         file is valid.
	 */
	private IStatus validateFileType(IFileInfo linkTargetFile) {
		if ((type == IResource.FILE) && linkTargetFile.isDirectory()) {
			return createStatus(IStatus.ERROR, IDEWorkbenchMessages.CreateLinkedResourceGroup_linkTargetNotFile);
		} else if ((type == IResource.FOLDER) && !linkTargetFile.isDirectory()) {
			return createStatus(IStatus.ERROR, IDEWorkbenchMessages.CreateLinkedResourceGroup_linkTargetNotFolder);
		}
		return Status.OK_STATUS;
	}

	/**
	 * Validates this page's controls.
	 *
	 * @param linkHandle
	 *            The target to check
	 * @return IStatus indicating the validation result. IStatus.OK if the
	 *         specified link target is valid given the linkHandle.
	 */
	public IStatus validateLinkLocation(IResource linkHandle) {
		if ((linkTargetField == null) || linkTargetField.isDisposed() || !createLink) {
			return Status.OK_STATUS;
		}
		IWorkspace workspace = IDEWorkbenchPlugin.getPluginWorkspace();
		FileSystemConfiguration configuration = getSelectedConfiguration();
		if ((configuration == null) || EFS.SCHEME_FILE.equals(configuration.getScheme())) {
			// Special handling for UNC paths. See bug 90825
			IPath location = new Path(linkTarget);
			if (location.isUNC()) {
				return createStatus(IStatus.WARNING, IDEWorkbenchMessages.CreateLinkedResourceGroup_unableToValidateLinkTarget);
			}
		}
		URI locationURI = getLinkTargetURI();
		if (locationURI == null) {
			return createStatus(IStatus.WARNING, IDEWorkbenchMessages.CreateLinkedResourceGroup_unableToValidateLinkTarget);
		}
		IStatus locationStatus = workspace.validateLinkLocationURI(linkHandle, locationURI);
		if ((locationStatus.getSeverity() == IStatus.ERROR) || linkTarget.trim().equals(EMPTY_STRING)) {
			return locationStatus;
		}

		// use the resolved link target name
		URI resolved = workspace.getPathVariableManager().resolveURI(locationURI);
		IFileInfo linkTargetFile = IDEResourceInfoUtils.getFileInfo(resolved);
		if ((linkTargetFile != null) && linkTargetFile.exists()) {
			IStatus fileTypeStatus = validateFileType(linkTargetFile);
			if (!fileTypeStatus.isOK()) {
				return fileTypeStatus;
			}
		} else if (locationStatus.isOK()) {
			// locationStatus takes precedence over missing location warning.
			return createStatus(IStatus.WARNING, IDEWorkbenchMessages.CreateLinkedResourceGroup_linkTargetNonExistent);
		}
		return locationStatus;
	}
}
