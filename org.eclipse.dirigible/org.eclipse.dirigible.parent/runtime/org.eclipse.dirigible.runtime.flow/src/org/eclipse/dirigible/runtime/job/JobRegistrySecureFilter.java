/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.job;

import org.eclipse.dirigible.runtime.filter.AbstractRegistrySecureFilter;

public class JobRegistrySecureFilter extends AbstractRegistrySecureFilter {

	private static final String JOB_SECURED_MAPPING = "/job-secured"; //$NON-NLS-1$

	@Override
	protected String getSecuredMapping() {
		return JOB_SECURED_MAPPING;
	}

}
