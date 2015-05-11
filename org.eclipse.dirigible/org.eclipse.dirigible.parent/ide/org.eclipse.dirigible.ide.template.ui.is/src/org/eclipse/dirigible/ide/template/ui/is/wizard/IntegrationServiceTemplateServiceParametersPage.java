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

package org.eclipse.dirigible.ide.template.ui.is.wizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.eclipse.dirigible.ide.workspace.ui.shared.FocusableWizardPage;

public class IntegrationServiceTemplateServiceParametersPage extends FocusableWizardPage {

	private static final long serialVersionUID = -1909594239505630715L;

	private static final String ORIGINAL_ENDPOINT_ADDRESS = Messages.IntegrationServiceTemplateServiceParametersPage_ORIGINAL_ENDPOINT_ADDRESS;

	private static final String INPUT_THE_CONDITION_PARAMETER_NAME = Messages.IntegrationServiceTemplateServiceParametersPage_INPUT_THE_CONDITION_PARAMETER_NAME;

	private static final String CONDITION_PARAMETER_NAME = Messages.IntegrationServiceTemplateServiceParametersPage_CONDITION_PARAMETER_NAME;

	private static final String INPUT_THE_ENDPOINT_ADDRESS = Messages.IntegrationServiceTemplateServiceParametersPage_INPUT_THE_ENDPOINT_ADDRESS;

	private static final String INPUT_THE_ROUTE_IDENTIFIER = Messages.IntegrationServiceTemplateServiceParametersPage_INPUT_THE_ROUTE_IDENTIFIER;

	private static final String ROUTE_IDENTIFIER = Messages.IntegrationServiceTemplateServiceParametersPage_ROUTE_IDENTIFIER;

	private static final String SET_THE_SERVICE_PARAAMETERS_WHICH_WILL_BE_USED_DURING_THE_GENERATION = Messages.IntegrationServiceTemplateServiceParametersPage_SET_THE_SERVICE_PARAAMETERS_WHICH_WILL_BE_USED_DURING_THE_GENERATION;

	private static final String SERVICE_PARAMETERS = Messages.IntegrationServiceTemplateServiceParametersPage_SERVICE_PARAMETERS;

	private static final String PAGE_NAME = "org.eclipse.dirigible.ide.template.ui.is.wizard.IntegrationServiceTemplateServiceParametersPage"; //$NON-NLS-1$

	private IntegrationServiceTemplateModel model;

	private Composite composite;

	private Text idText;
	private Text endpointAddressText;
//	private Label parameterNameLabel;
//	private Text parameterNameText;
//	private Label originalEndpointLabel;
//	private Text originalEndpointText;

	protected IntegrationServiceTemplateServiceParametersPage(
			IntegrationServiceTemplateModel model) {
		super(PAGE_NAME);
		this.model = model;
		setTitle(SERVICE_PARAMETERS);
		setDescription(SET_THE_SERVICE_PARAAMETERS_WHICH_WILL_BE_USED_DURING_THE_GENERATION);
	}

	@Override
	public void createControl(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		composite.setLayout(new GridLayout(2, false));

		createIdField(composite);
		createEndpointNameField(composite);
//		createParameterNameField(composite);
//		createOriginalEndpointField(composite);
		checkPageStatus();
	}

	private void createIdField(Composite parent) {
		final Label label = new Label(parent, SWT.NONE);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		label.setText(ROUTE_IDENTIFIER);

		idText = new Text(parent, SWT.BORDER);
		idText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		idText.addModifyListener(new ModifyListener() {

			private static final long serialVersionUID = -6899815752954102238L;

			@Override
			public void modifyText(ModifyEvent event) {
				if (idText.getText() == null
						|| "".equals(idText.getText())) { //$NON-NLS-1$
					setErrorMessage(INPUT_THE_ROUTE_IDENTIFIER);
				} else {
					setErrorMessage(null);
					model.setId(idText.getText());
				}
				checkPageStatus();
			}
		});
		setFocusable(idText);
	}

	private void createEndpointNameField(Composite parent) {
		final Label label = new Label(parent, SWT.NONE);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		label.setText(Messages.IntegrationServiceTemplateServiceParametersPage_ENDPOINT_ADDRESS);

		endpointAddressText = new Text(parent, SWT.BORDER);
		endpointAddressText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false));
		endpointAddressText.addModifyListener(new ModifyListener() {

			private static final long serialVersionUID = 4792106221991822271L;

			@Override
			public void modifyText(ModifyEvent event) {
				if (endpointAddressText.getText() == null
						|| "".equals(endpointAddressText.getText())) { //$NON-NLS-1$
					setErrorMessage(INPUT_THE_ENDPOINT_ADDRESS);
				} else {
					setErrorMessage(null);
					model.setEndpointName(endpointAddressText.getText());
				}
				checkPageStatus();
			}
		});
	}

//	private void createParameterNameField(Composite parent) {
//		parameterNameLabel = new Label(parent, SWT.NONE);
//		parameterNameLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
//				false, false));
//		parameterNameLabel.setText(CONDITION_PARAMETER_NAME);
//
//		parameterNameText = new Text(parent, SWT.BORDER);
//		parameterNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
//				true, false));
//		parameterNameText.addModifyListener(new ModifyListener() {
//
//			private static final long serialVersionUID = -3477560761099570898L;
//
//			@Override
//			public void modifyText(ModifyEvent event) {
//				if (parameterNameText.getText() == null
//						|| "".equals(parameterNameText.getText())) { //$NON-NLS-1$
//					setErrorMessage(INPUT_THE_CONDITION_PARAMETER_NAME);
//				} else {
//					setErrorMessage(null);
//					model.setParameterName(parameterNameText.getText());
//				}
//				checkPageStatus();
//			}
//		});
//	}
//
//	private void createOriginalEndpointField(Composite parent) {
//		originalEndpointLabel = new Label(parent, SWT.NONE);
//		originalEndpointLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
//				false, false));
//		originalEndpointLabel.setText(ORIGINAL_ENDPOINT_ADDRESS);
//
//		originalEndpointText = new Text(parent, SWT.BORDER);
//		originalEndpointText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
//				true, false));
//		originalEndpointText.addModifyListener(new ModifyListener() {
//
//			private static final long serialVersionUID = -4627422058319183501L;
//
//			@Override
//			public void modifyText(ModifyEvent event) {
//				if (originalEndpointText.getText() == null
//						|| "".equals(originalEndpointText.getText())) { //$NON-NLS-1$
//					setErrorMessage(INPUT_THE_CONDITION_PARAMETER_NAME);
//				} else {
//					setErrorMessage(null);
//					model.setOriginalEndpoint(originalEndpointText.getText());
//				}
//				checkPageStatus();
//			}
//		});
//	}

	private void checkPageStatus() {
		if (model.getId() == null || "".equals(model.getId())) { //$NON-NLS-1$
			setPageComplete(false);
			return;
		}
		if (model.getEndpointAddress() == null
				|| "".equals(model.getEndpointAddress())) { //$NON-NLS-1$
			setPageComplete(false);
			return;
		}
//		if (model.getTemplate().getValidParameters().contains("parameterName")) { //$NON-NLS-1$
//			if (model.getParameterName() == null
//					|| "".equals(model.getParameterName())) { //$NON-NLS-1$
//				setPageComplete(false);
//				return;
//			}
//		}
//		if (model.getTemplate().getValidParameters()
//				.contains("originalEndpoint")) { //$NON-NLS-1$
//			if (model.getOriginalEndpoint() == null
//					|| "".equals(model.getOriginalEndpoint())) { //$NON-NLS-1$
//				setPageComplete(false);
//				return;
//			}
//		}

		setPageComplete(true);
	}

	@Override
	public void setVisible(boolean visible) {

//		if (visible && model.getTemplate() != null) {
//			parameterNameLabel.setEnabled(model.getTemplate()
//					.getValidParameters().contains("parameterName")); //$NON-NLS-1$
//			parameterNameText.setEnabled(model.getTemplate()
//					.getValidParameters().contains("parameterName")); //$NON-NLS-1$
//			originalEndpointLabel.setEnabled(model.getTemplate()
//					.getValidParameters().contains("originalEndpoint")); //$NON-NLS-1$
//			originalEndpointText.setEnabled(model.getTemplate()
//					.getValidParameters().contains("originalEndpoint")); //$NON-NLS-1$
//			// composite.layout();
//		}
		super.setVisible(visible);
	}

}
