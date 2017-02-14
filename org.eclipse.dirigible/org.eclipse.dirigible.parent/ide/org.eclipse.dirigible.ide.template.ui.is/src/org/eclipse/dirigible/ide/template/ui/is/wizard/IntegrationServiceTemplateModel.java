/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.is.wizard;

import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.repository.api.ICommonConstants;

@SuppressWarnings("javadoc")
public class IntegrationServiceTemplateModel extends GenerationModel {

	private static final String TARGET_LOCATION_IS_NOT_ALLOWED = Messages.IntegrationServiceTemplateModel_TARGET_LOCATION_IS_NOT_ALLOWED;

	public String id;
	public String endpointAddress;
	public String parameterName;
	public String originalEndpoint;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEndpointAddress() {
		return endpointAddress;
	}

	public void setEndpointName(String endpointAddress) {
		this.endpointAddress = endpointAddress;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getOriginalEndpoint() {
		return originalEndpoint;
	}

	public void setOriginalEndpoint(String originalEndpoint) {
		this.originalEndpoint = originalEndpoint;
	}

	@Override
	protected String getArtifactType() {
		return ICommonConstants.ARTIFACT_TYPE.INTEGRATION_SERVICES;
	}

	@Override
	protected String getTargetLocationErrorMessage() {
		return TARGET_LOCATION_IS_NOT_ALLOWED;
	}

}
