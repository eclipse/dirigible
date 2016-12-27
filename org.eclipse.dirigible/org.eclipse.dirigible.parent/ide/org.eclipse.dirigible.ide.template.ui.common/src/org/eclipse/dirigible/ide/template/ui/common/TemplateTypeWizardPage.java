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

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.dirigible.ide.common.ExtensionPointUtils;
import org.eclipse.dirigible.ide.common.image.ImageUtils;
import org.eclipse.dirigible.ide.ui.common.validation.IValidationStatus;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public abstract class TemplateTypeWizardPage extends WizardPage {

	private static final String EXTENSION_POINT_0_COULD_NOT_BE_FOUND = Messages.TemplateTypeWizardPage_EXTENSION_POINT_0_COULD_NOT_BE_FOUND;
	private static final long serialVersionUID = -5435162447712125969L;
	private static final String EMPTY_STRING = ""; //$NON-NLS-1$
	private static final String ERROR_ON_LOADING_TEMPLATES_FOR_GENERATION = Messages.TemplateTypeWizardPage_ERROR_ON_LOADING_TEMPLATES_FOR_GENERATION;
	private static final String SELECT_TEMPLATE_TYPE_FORM_THE_LIST = Messages.TemplateTypeWizardPage_SELECT_TEMPLATE_TYPE_FORM_THE_LIST;
	private static final String AVAILABLE_TEMPLATES = Messages.TemplateTypeWizardPage_AVAILABLE_TEMPLATES;

	private static final String TEMPLATE_TYPE_EXTENSION_POINT_ID = "org.eclipse.dirigible.ide.template.type"; //$NON-NLS-1$
	private static final String TEMPLATE_TYPE_ELEMENT_NAME = "template"; //$NON-NLS-1$
	private static final String TEMPLATE_TYPE_TEXT_ATTRIBUTE = "text"; //$NON-NLS-1$
	private static final String TEMPLATE_TYPE_LOCATION_ATTRIBUTE = "location"; //$NON-NLS-1$
	private static final String TEMPLATE_TYPE_IMAGE_ATTRIBUTE = "image"; //$NON-NLS-1$
	private static final String TEMPLATE_TYPE_PREVIEW_ATTRIBUTE = "preview"; //$NON-NLS-1$
	private static final String TEMPLATE_TYPE_CATEGORY_ATTRIBUTE = "category"; //$NON-NLS-1$
	private static final String TEMPLATE_TYPE_PARAMETER_ATTRIBUTE = "parameter"; //$NON-NLS-1$
	private static final String TEMPLATE_TYPE_VALUE_ATTRIBUTE = "value"; //$NON-NLS-1$

	protected TemplateTypeWizardPage(String pageName) {
		super(pageName);
	}

	private static final Logger logger = Logger.getLogger(TemplateTypeWizardPage.class);

	protected abstract GenerationModel getModel();

	private TableViewer typeViewer;

	private Label labelPreview;

	@Override
	public void createControl(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		composite.setLayout(new GridLayout());

		SashForm sashForm = new SashForm(composite, SWT.VERTICAL);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		createTypeField(sashForm);

		createPreviewLabel(sashForm);

		sashForm.setWeights(new int[] { 70, 30 });

		composite.pack(true);

		checkPageStatus();
	}

	private static final Image previewImage = ImageUtils.createImage(getIconURL("preview.png"));

	public static URL getIconURL(String iconName) {
		URL url = ImageUtils.getIconURL("org.eclipse.dirigible.ide.template.ui.common", "/icons/", iconName);
		return url;
	}

	private void createPreviewLabel(final Composite composite) {
		Composite indexComposite = new Composite(composite, SWT.NONE);
		GridLayout flat = new GridLayout();
		flat.horizontalSpacing = 0;
		flat.marginWidth = 0;
		indexComposite.setLayout(flat);
		labelPreview = new Label(indexComposite, SWT.NONE);
		labelPreview.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		labelPreview.setBounds(0, 0, 450, 300);
		labelPreview.setBackground(new org.eclipse.swt.graphics.Color(null, 255, 255, 255));
		labelPreview.setImage(previewImage);
	}

	private void createTypeField(Composite parent) {
		Composite indexComposite = new Composite(parent, SWT.NONE);
		GridLayout flat = new GridLayout();
		flat.horizontalSpacing = 0;
		flat.marginWidth = 0;
		indexComposite.setLayout(flat);
		GridData gridDataTable = new GridData();
		gridDataTable.horizontalAlignment = SWT.FILL;
		gridDataTable.verticalAlignment = SWT.FILL;
		gridDataTable.grabExcessHorizontalSpace = true;
		gridDataTable.grabExcessVerticalSpace = true;
		gridDataTable.horizontalIndent = 0;

		final Label label = new Label(indexComposite, SWT.NONE);
		label.setText(AVAILABLE_TEMPLATES);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false));

		typeViewer = new TableViewer(indexComposite, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
		typeViewer.getControl().setLayoutData(gridDataTable);
		typeViewer.setContentProvider(new ArrayContentProvider());
		typeViewer.setLabelProvider(new TemplateTypePageLabelProvider());
		TemplateType[] templateTypes = createTemplateTypes();
		typeViewer.setInput(templateTypes);
		typeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if ((selection.getFirstElement() == null) || !(selection.getFirstElement() instanceof TemplateType)) {
					setErrorMessage(SELECT_TEMPLATE_TYPE_FORM_THE_LIST);
					labelPreview.setImage(previewImage);
					labelPreview.pack(true);
				} else {
					setErrorMessage(null);
					TemplateType templateType = ((TemplateType) selection.getFirstElement());
					getModel().setTemplate(templateType);
					String preview = templateType.getTemplateMetadata().getPreview();
					if (preview != null) {
						Image image = getPreviewImage(preview);
						if (image != null) {
							labelPreview.setImage(image);
						} else {
							labelPreview.setImage(previewImage);
						}
					} else {
						labelPreview.setImage(previewImage);
					}
					labelPreview.pack(true);
				}
				checkPageStatus();
			}
		});

	}

	private Image getPreviewImage(String imageLocation) {
		Image image;
		try {
			image = TemplateUtils.createImageFromResource(imageLocation);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			return null;
		}
		return image;
	}

	private TemplateType[] createTemplateTypes() {
		try {
			TemplateType[] templateTypes = prepareTemplateTypes();
			return templateTypes;
		} catch (IOException e) {
			logger.error(ERROR_ON_LOADING_TEMPLATES_FOR_GENERATION, e);
		}
		return null;
	}

	// protected abstract TemplateType[] prepareTemplateTypes() throws IOException;

	private void checkPageStatus() {
		if ((getModel().getTemplateLocation() == null) || EMPTY_STRING.equals(getModel().getTemplateLocation())) {
			setPageComplete(false);
			return;
		}
		IValidationStatus status = getModel().validateTemplate();
		if (status.hasErrors()) {
			setPageComplete(false);
		} else if (status.hasWarnings()) {
			setPageComplete(true);
		} else {
			setPageComplete(true);
		}
	}

	public List<TemplateTypeDescriptor> getTemplateTypeDescriptors(String category) {

		List<TemplateTypeDescriptor> templateTypeDescriptors = new ArrayList<TemplateTypeDescriptor>();
		final IExtensionPoint extensionPoint = ExtensionPointUtils.getExtensionPoint(TEMPLATE_TYPE_EXTENSION_POINT_ID);
		if (extensionPoint == null) {
			throw new TemplateTypeException(format(EXTENSION_POINT_0_COULD_NOT_BE_FOUND, TEMPLATE_TYPE_EXTENSION_POINT_ID));
		}
		final IConfigurationElement[] templateTypeDescriptorElements = getTemplateElements(extensionPoint.getExtensions());

		for (IConfigurationElement templateTypeDescriptorElement : templateTypeDescriptorElements) {
			TemplateTypeDescriptor templateTypeDescriptor = createTemplateTypeDescriptor(category, templateTypeDescriptorElement);
			if (templateTypeDescriptor != null) {
				templateTypeDescriptors.add(templateTypeDescriptor);
			}
		}

		return templateTypeDescriptors;
	}

	private IConfigurationElement[] getTemplateElements(IExtension[] extensions) {
		final List<IConfigurationElement> result = new ArrayList<IConfigurationElement>();
		for (IExtension extension : extensions) {
			for (IConfigurationElement element : extension.getConfigurationElements()) {
				if (TEMPLATE_TYPE_ELEMENT_NAME.equals(element.getName())) {
					result.add(element);
				}
			}
		}
		return result.toArray(new IConfigurationElement[0]);
	}

	private TemplateTypeDescriptor createTemplateTypeDescriptor(String category, IConfigurationElement templateTypeDescriptorElement) {

		if (category.equals(templateTypeDescriptorElement.getAttribute(TEMPLATE_TYPE_CATEGORY_ATTRIBUTE))) {

			TemplateTypeDescriptor templateTypeDescriptor = new TemplateTypeDescriptor();
			templateTypeDescriptor.setText(templateTypeDescriptorElement.getAttribute(TEMPLATE_TYPE_TEXT_ATTRIBUTE));
			templateTypeDescriptor.setLocation(templateTypeDescriptorElement.getAttribute(TEMPLATE_TYPE_LOCATION_ATTRIBUTE));
			templateTypeDescriptor.setImage(templateTypeDescriptorElement.getAttribute(TEMPLATE_TYPE_IMAGE_ATTRIBUTE));
			templateTypeDescriptor.setPreview(templateTypeDescriptorElement.getAttribute(TEMPLATE_TYPE_PREVIEW_ATTRIBUTE));

			Set<String> parameters = new HashSet<String>();
			IConfigurationElement[] parameterElements = templateTypeDescriptorElement.getChildren(TEMPLATE_TYPE_PARAMETER_ATTRIBUTE);
			for (IConfigurationElement parameterElement : parameterElements) {
				parameters.add(parameterElement.getAttribute(TEMPLATE_TYPE_VALUE_ATTRIBUTE));
			}

			templateTypeDescriptor.setParameters(parameters);
			// templateTypeDescriptor.setPages(pages);

			return templateTypeDescriptor;
		}

		return null;
	}

	// private TemplateType[] prepareTemplateTypes() throws MalformedURLException {
	// List<TemplateTypeDescriptor> templateTypeDescriptors = getTemplateTypeDescriptors(getCategory());
	// List<TemplateType> templateTypesList = new ArrayList<TemplateType>();
	// for (TemplateTypeDescriptor templateTypeDescriptor : templateTypeDescriptors) {
	// templateTypesList.add(TemplateType.createTemplateType(templateTypeDescriptor.getText(),
	// templateTypeDescriptor.getLocation(),
	// templateTypeDescriptor.getImage(), this.getClass(), templateTypeDescriptor.getParameters().toArray(new String[]
	// {})));
	// }
	//
	// TemplateType[] templateTypes = templateTypesList.toArray(new TemplateType[] {});
	// return templateTypes;
	// }

	protected TemplateType[] prepareTemplateTypes() throws IOException {
		return TemplateTypesEnumerator.prepareTemplateTypes(getTemplatesPath(), getCategory());
	}

	protected abstract String getTemplatesPath();

	protected abstract String getCategory();

}
