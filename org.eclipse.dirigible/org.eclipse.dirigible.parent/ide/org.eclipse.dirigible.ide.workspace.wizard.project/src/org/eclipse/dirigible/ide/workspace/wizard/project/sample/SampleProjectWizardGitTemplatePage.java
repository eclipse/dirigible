/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.workspace.wizard.project.sample;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.ide.common.image.ImageUtils;
import org.eclipse.dirigible.ide.jgit.utils.GitFileUtils;
import org.eclipse.dirigible.ide.workspace.ui.shared.IValidationStatus;
import org.eclipse.dirigible.ide.workspace.wizard.project.create.ProjectTemplateType;
import org.eclipse.dirigible.repository.ext.git.JGitConnector;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class SampleProjectWizardGitTemplatePage extends WizardPage {

	private static final long serialVersionUID = 1L;

	private static final String PAGE_NAME = Messages.SampleProjectWizardGitTemplatePage_PAGE_NAME;
	private static final String PAGE_TITLE = Messages.SampleProjectWizardGitTemplatePage_PAGE_TITLE;
	private static final String PAGE_DESCRIPTION = Messages.SampleProjectWizardGitTemplatePage_PAGE_DESCRIPTION;
	private static final String ERROR_ON_LOADING_GIT_TEMPLATES_FOR_GENERATION = Messages.SampleProjectWizardGitTemplatePage_ERROR_ON_LOADING_GIT_TEMPLATES_FOR_GENERATION;
	private static final String HELP_DIRECTORY = "HelpDirectory"; //$NON-NLS-1$
	public static final String TEMP_DIRECTORY_PREFIX = "org.eclipse.dirigible.jgit."; //$NON-NLS-1$
	protected static final String SELECT_TEMPLATE_TYPE_FORM_THE_LIST = Messages.SampleProjectWizardGitTemplatePage_SELECT_TEMPLATE_TYPE_FORM_THE_LIST;
	private static final String GIT_TEMPLATE_DIRECTORY = "org.eclipse.dirigible.ide.workspace.wizard.project.sample"; //$NON-NLS-1$
	private static final Logger logger = Logger.getLogger(SampleProjectWizardGitTemplatePage.class);

	private final SampleProjectWizardModel model;

	private TreeViewer typeViewer;
	private Label labelPreview;

	public SampleProjectWizardGitTemplatePage(SampleProjectWizardModel model) {
		super(PAGE_NAME);
		setTitle(PAGE_TITLE);
		setDescription(PAGE_DESCRIPTION);
		this.model = model;
	}

	@Override
	public void createControl(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		composite.setLayout(new GridLayout());
		createTypeField(composite);
		createPreviewLabel(composite);

		checkPageStatus();
	}

	private static final Image previewImage = ImageUtils.createImage(getIconURL("preview.png")); //$NON-NLS-1$

	public static URL getIconURL(String iconName) {
		URL url = ImageUtils.getIconURL("org.eclipse.dirigible.ide.workspace.wizard.project", //$NON-NLS-1$
				"/icons/", iconName); //$NON-NLS-1$
		return url;
	}

	private void createPreviewLabel(final Composite composite) {
		labelPreview = new Label(composite, SWT.NONE);
		labelPreview.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		labelPreview.setBounds(0, 0, 450, 300);
		labelPreview.setBackground(new org.eclipse.swt.graphics.Color(null, 0, 0, 0));
		labelPreview.setImage(previewImage);
	}

	private void createTypeField(Composite parent) {
		final Label label = new Label(parent, SWT.NONE);
		label.setText(Messages.SampleProjectWizardGitTemplatePage_AVAILABLE_GIT_TEMPLATES);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false));

		typeViewer = new TreeViewer(parent, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
		typeViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		typeViewer.setContentProvider(new SamplesContentProvider());
		typeViewer.setLabelProvider(new SamplesLabelProvider());
		typeViewer.setSorter(new ViewerSorter());
		typeViewer.setInput(createGitTemplateTypes());

		typeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if ((selection.getFirstElement() == null) || !(selection.getFirstElement() instanceof SamplesProject)) {
					setPageComplete(false);
					setErrorMessage(SELECT_TEMPLATE_TYPE_FORM_THE_LIST);
					labelPreview.setImage(previewImage);
					labelPreview.pack(true);
				} else {
					setErrorMessage(null);
					SamplesProject gitTemplate = ((SamplesProject) selection.getFirstElement());
					getModel().setTemplate(gitTemplate.getTemplate());
					checkPageStatus();
					labelPreview.setImage(gitTemplate.getTemplate().getImagePreview());
					labelPreview.pack(true);
				}
			}
		});
	}

	private SamplesCategory createGitTemplateTypes() {
		try {
			Map<String, List<SamplesProject>> categories = new HashMap<String, List<SamplesProject>>();
			for (ProjectTemplateType sampleProject : prepareGitTemplateTypes()) {
				String category = sampleProject.getCategory();
				if (categories.containsKey(category)) {
					categories.get(category).add(new SamplesProject(sampleProject));
				} else {
					List<SamplesProject> sampleProjectsList = new ArrayList<SamplesProject>();
					sampleProjectsList.add(new SamplesProject(sampleProject));
					categories.put(category, sampleProjectsList);
				}
			}

			SamplesCategory root = new SamplesCategory(""); //$NON-NLS-1$
			for (Entry<String, List<SamplesProject>> entry : categories.entrySet()) {
				root.addCategory(new SamplesCategory(entry.getKey(), entry.getValue()));
			}
			return root;
		} catch (IOException e) {
			logger.error(ERROR_ON_LOADING_GIT_TEMPLATES_FOR_GENERATION, e);
		}
		return null;
	}

	@SuppressWarnings("null")
	protected ProjectTemplateType[] prepareGitTemplateTypes() throws IOException {
		File gitDirectory = null;
		boolean isCloned = false;

		File file = GitFileUtils.createTempDirectory(HELP_DIRECTORY);
		File tempDirectory = file.getParentFile();
		GitFileUtils.deleteDirectory(file);

		for (File temp : tempDirectory.listFiles()) {
			if (temp.isDirectory() && temp.getName().startsWith(GIT_TEMPLATE_DIRECTORY)) {
				isCloned = true;
				gitDirectory = temp;
				break;
			}
		}

		if (isCloned) {
			doPull(gitDirectory.getCanonicalPath());
		} else {
			gitDirectory = GitFileUtils.createTempDirectory(GIT_TEMPLATE_DIRECTORY);
			doClone(gitDirectory);
		}

		if (gitDirectory.listFiles().length <= 0) {
			model.setUseTemplate(false);
			return new ProjectTemplateType[] {};
		}

		List<ProjectTemplateType> projectTemplateTypesList = new ArrayList<ProjectTemplateType>();

		for (File projectTemplate : gitDirectory.listFiles()) {
			if (!projectTemplate.getName().equalsIgnoreCase(".git") //$NON-NLS-1$
					&& projectTemplate.isDirectory()) {

				projectTemplateTypesList.add(ProjectTemplateType.createGitTemplateType(projectTemplate));
			}
		}
		return projectTemplateTypesList.toArray(new ProjectTemplateType[] {});
	}

	private void checkPageStatus() {
		if (getModel().isUseTemplate()) {
			if ((getModel().getTemplateLocation() == null) || "".equals(getModel().getTemplateLocation())) { //$NON-NLS-1$
				setPageComplete(false);
				return;
			} else {
				revalidateModel();
			}
			return;
		}
		setPageComplete(false);
	}

	public SampleProjectWizardModel getModel() {
		return this.model;
	}

	private void revalidateModel() {
		String projectName = getModel().getTemplate().getName();
		getModel().setProjectName(projectName);
		IValidationStatus status = getModel().validate();
		if (status.hasErrors()) {
			this.setErrorMessage(status.getMessage());
			this.setWarningMessage(null);
			this.setCanFinish(false);
		} else if (status.hasWarnings()) {
			this.setErrorMessage(null);
			this.setWarningMessage(status.getMessage());
			this.setCanFinish(true);
		} else {
			this.setErrorMessage(null);
			this.setWarningMessage(null);
			this.setCanFinish(true);
		}
	}

	public void setWarningMessage(String message) {
		setMessage(message, WARNING);
	}

	public void setCanFinish(boolean value) {
		setPageComplete(value);
	}

	private static void doPull(String gitDirectoryPath) throws IOException {
		Repository repository = null;
		try {
			repository = JGitConnector.getRepository(gitDirectoryPath);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		JGitConnector jgit = new JGitConnector(repository);
		try {
			jgit.pull();
			jgit.add("."); //$NON-NLS-1$
			jgit.hardReset();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private static void doClone(File gitDirectory) {
		try {
			JGitConnector.cloneRepository(gitDirectory, CommonIDEParameters.GIT_REPOSITORY_URL);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
