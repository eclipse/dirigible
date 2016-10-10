/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.common;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.dirigible.ide.workspace.dual.WorkspaceLocator;
import org.eclipse.dirigible.ide.workspace.ui.viewer.ReservedFolderFilter;
import org.eclipse.dirigible.ide.workspace.ui.viewer.WorkspaceContainerFilter;
import org.eclipse.dirigible.ide.workspace.ui.viewer.WorkspaceViewer;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public abstract class TemplateTargetLocationPage extends WizardPage {

	private static final String THERE_IS_NO_SELECTED_PROJECT = "There is no selected project";

	private static final long serialVersionUID = 1L;

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	private static final String INPUT_THE_FILE_NAME = Messages.TemplateTargetLocationPage_INPUT_THE_FILE_NAME;

	private static final String INPUT_THE_PACKAGE_NAME = Messages.TemplateTargetLocationPage_INPUT_THE_PACKAGE_NAME;

	private static final String SELECT_THE_LOCATION_OF_THE_GENERATED_PAGE = Messages.TemplateTargetLocationPage_SELECT_THE_LOCATION_OF_THE_GENERATED_PAGE;

	protected abstract GenerationModel getModel();

	private WorkspaceViewer projectViewer;

	private Text packageNameText;

	private Text fileNameText;

	private String targetLocation = null;

	protected TemplateTargetLocationPage(String pageName) {
		super(pageName);
	}

	@Override
	public void createControl(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		composite.setLayout(new GridLayout(1, false));

		createProjectViewerField(composite);
		createPackageNameField(composite);
		createFileNameField(composite);
		checkPageStatus();
	}

	private void setPreselectedElement() {
		projectViewer.getViewer().setSelection(getPreselectedElement(), true);
	}

	private void createProjectViewerField(Composite parent) {
		projectViewer = new WorkspaceViewer(parent, SWT.BORDER);
		projectViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		projectViewer.getViewer().addFilter(getFilter());
		projectViewer.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				IStructuredSelection selection = (IStructuredSelection) projectViewer.getSelectionProvider().getSelection();
				if ((selection.getFirstElement() == null) || !(selection.getFirstElement() instanceof IContainer)) {
					setErrorMessage(SELECT_THE_LOCATION_OF_THE_GENERATED_PAGE);
				} else {
					setErrorMessage(null);
					IContainer container = ((IContainer) selection.getFirstElement());
					// #177
					targetLocation = container.getFullPath().toString();

					getModel().setTargetContainer(targetLocation);

					if (container.getProjectRelativePath().segmentCount() <= 1) {
						// project is selected
						if (getModel().getProjectName() != null) {
							// set package name equal to project name - default
							getModel().setProjectPackageName(getModel().getProjectName());
						} else {
							// get first project as a target
							if (WorkspaceLocator.getWorkspace().getRoot().getProjects().length == 0) {
								setErrorMessage(THERE_IS_NO_SELECTED_PROJECT);
							} else {
								getModel().setProjectPackageName(WorkspaceLocator.getWorkspace().getRoot().getProjects()[0].getName());
							}
						}
					} else {
						// a sub-folder of project is selected
						String packageName = getModel().genPackageName();
						getModel().setProjectPackageName(packageName);
						packageNameText.setText(packageName);
						packageNameText.setEnabled(false);
					}
				}
				checkPageStatus();
			}
		});
		projectViewer.getViewer().expandToLevel(2);
	}

	protected ViewerFilter getFilter() {
		if (getArtifactContainerName() == null) {
			return new WorkspaceContainerFilter();
		} else {
			return new ReservedFolderFilter(getArtifactContainerName());
		}

	}

	/**
	 * Override if you wish to filter where the file can be created. Method
	 * should return one of the main types of artifacts: javascript service,
	 * integration service etc. Null if no the artifact can be created
	 * everywhere.
	 *
	 * @return Method should return one of the main types of artifacts:
	 *         javascript service, integration service etc.
	 */
	protected String getArtifactContainerName() {
		return null;
	}

	protected Object getApropriateFolderForAction(String folderName) {
		Object[] expandedElements = projectViewer.getViewer().getExpandedElements();
		if ((expandedElements == null) || (expandedElements.length == 0)) {
			return null;
		}
		ITreeContentProvider contentProvider = (ITreeContentProvider) projectViewer.getViewer().getContentProvider();
		IResource sourceResource = getModel().getSourceResource();
		if (sourceResource != null) {
			String[] segments = sourceResource.getLocation().segments();
			for (String segment : segments) {
				if (segment.equals(folderName)) {
					return sourceResource;
				}
			}
		}

		// backup variant if selection is on different artifact type or on
		// project level
		try {
			if (sourceResource != null) {
				IProject project = sourceResource.getProject();
				Object[] children = contentProvider.getChildren(project);
				for (Object object : children) {
					IResource r = (IResource) object;
					if ((r instanceof IFolder) && r.getName().equals(folderName)) {
						return r;
					}
				}
			} else {
				return null;
			}
		} catch (NullPointerException e) {
			// project is not selected, return null
		}
		return null;
	}

	private void createFileNameField(Composite parent) {
		final Label label = new Label(parent, SWT.NONE);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		label.setText(Messages.TemplateTargetLocationPage_FILE_NAME);

		fileNameText = new Text(parent, SWT.BORDER);
		fileNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fileNameText.addModifyListener(new ModifyListener() {

			private static final long serialVersionUID = 72329751007839679L;

			@Override
			public void modifyText(ModifyEvent event) {
				if ((fileNameText.getText() == null) || EMPTY_STRING.equals(fileNameText.getText())) {
					setErrorMessage(INPUT_THE_FILE_NAME);
				} else {
					setErrorMessage(null);
					getModel().setFileName(fileNameText.getText());
				}
				checkPageStatus();
			}
		});

	}

	private void createPackageNameField(Composite parent) {
		final Label label = new Label(parent, SWT.NONE);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		label.setText(Messages.TemplateTargetLocationPage_PACKAGE_NAME);

		packageNameText = new Text(parent, SWT.BORDER);
		packageNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		packageNameText.addModifyListener(new ModifyListener() {

			private static final long serialVersionUID = 72329751007839679L;

			@Override
			public void modifyText(ModifyEvent event) {
				if ((packageNameText.getText() == null) || EMPTY_STRING.equals(packageNameText.getText())) {
					setErrorMessage(INPUT_THE_PACKAGE_NAME);
				} else {
					setErrorMessage(null);
					getModel().setProjectPackageName(packageNameText.getText());
					if (!targetLocation.endsWith(packageNameText.getText())) {
						getModel().setTargetContainer(targetLocation + IRepository.SEPARATOR + packageNameText.getText());
					}
				}
				checkPageStatus();
			}
		});

	}

	protected abstract void checkPageStatus();

	protected abstract String getDefaultFileName(String preset);

	@Override
	public void setVisible(boolean visible) {
		setPreselectedElement();

		if (packageNameText.isEnabled()) {
			String packageName = getDefaultPackageName();
			packageNameText.setText((packageName == null) ? "" : packageName);
		}

		if ((fileNameText.getText() == null) || EMPTY_STRING.equals(fileNameText.getText())) {
			fileNameText.setText(getDefaultFileName(null));
		} else {
			if (isForcedFileName()
			// && getModel().getFileName() == null
			) {
				fileNameText.setText(getDefaultFileName(fileNameText.getText()));
			}
		}

		super.setVisible(visible);
		preselectFileNameText();

	}

	private String getDefaultPackageName() {
		return (getModel().getProjectPackageName() == null) ? getModel().getProjectName() : getModel().getProjectPackageName();
	}

	private void preselectFileNameText() {
		fileNameText.setFocus();
		String defaultName = getDefaultFileName(fileNameText.getText());
		if ((defaultName != null) && (defaultName.length() > 0)) {
			int lastIndexOf = defaultName.indexOf("."); //$NON-NLS-1$
			if (lastIndexOf == -1) {
				lastIndexOf = defaultName.length();
			}
			fileNameText.setSelection(0, lastIndexOf);
		}

	}

	/**
	 * Method returns preselected element that is the correct type of artifact
	 * parent location.
	 *
	 * @return default selection
	 */
	protected StructuredSelection getPreselectedElement() {
		if (getArtifactContainerName() != null) {
			Object byName = getApropriateFolderForAction(getArtifactContainerName());
			if (byName != null) {
				return new StructuredSelection(byName);
			}
		}
		return new StructuredSelection();

	}

	protected boolean isForcedFileName() {
		return false;
	}

}
