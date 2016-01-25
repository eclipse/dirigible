/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.mobile.wizard;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.ui.common.validation.IValidationStatus;
import org.eclipse.dirigible.ide.ui.common.validation.ValidationStatus;
import org.eclipse.dirigible.repository.api.ICommonConstants;

/**
 * Mobile generation template model
 */
public class MobileTemplateModel extends GenerationModel {

	private static final String TARGET_LOCATION_IS_NOT_ALLOWED = Messages.MobileTemplateModel_TARGET_LOCATION_IS_NOT_ALLOWED;

	@Override
	public IValidationStatus validate() {
		IValidationStatus locationStatus = validateLocation();
		if (locationStatus.hasErrors()) {
			return locationStatus;
		}
		IValidationStatus templateStatus = validateTemplate();
		if (locationStatus.hasErrors()) {
			return locationStatus;
		}

		return ValidationStatus.getValidationStatus(locationStatus, templateStatus);
	}

	/**
	 * Validates the location
	 *
	 * @return validation status
	 */
	public IValidationStatus validateLocation() {
		IValidationStatus status;
		try {
			status = validateLocationGeneric();
			if (status.hasErrors()) {
				return status;
			}
			IPath location = new Path(getTargetLocation()).append(getFileName());
			// TODO - more precise test for the location ../MobileApplications/...
			if (location.toString().indexOf(ICommonConstants.ARTIFACT_TYPE.MOBILE_APPLICATIONS) == -1) {
				return ValidationStatus.createError(TARGET_LOCATION_IS_NOT_ALLOWED);
			}
		} catch (Exception e) {
			// temp workaround due to another bug - context menu is not context
			// aware => target location and name are null (in the first page of
			// the wizard)
			return ValidationStatus.createError(""); //$NON-NLS-1$
		}
		return status;
	}
}
