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

package org.eclipse.dirigible.runtime.mobile;

import org.eclipse.dirigible.runtime.filter.AbstractRegistrySecureFilter;

public class MobileRegistrySecureFilter extends AbstractRegistrySecureFilter {

	private static final String MOBILE_SECURED_MAPPING = "/services/mobile-secured"; //$NON-NLS-1$

	@Override
	protected String getSecuredMapping() {
		return MOBILE_SECURED_MAPPING;
	}

}
