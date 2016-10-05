/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.listener;

import org.eclipse.dirigible.runtime.filter.AbstractRegistrySecureFilter;

public class ListenerRegistrySecureFilter extends AbstractRegistrySecureFilter {

	private static final String LISTENER_SECURED_MAPPING = "/listener-secured"; //$NON-NLS-1$

	@Override
	protected String getSecuredMapping() {
		return LISTENER_SECURED_MAPPING;
	}

}
