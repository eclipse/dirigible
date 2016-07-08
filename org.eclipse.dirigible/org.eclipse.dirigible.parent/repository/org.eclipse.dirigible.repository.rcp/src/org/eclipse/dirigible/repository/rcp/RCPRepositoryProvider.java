/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.rcp;

import java.util.Map;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryProvider;

public class RCPRepositoryProvider implements IRepositoryProvider {

	public static final String TYPE = "rcp";

	@Override
	public IRepository createRepository(Map<String, Object> parameters) {
		return RCPRepository.getInstance();
	}

	@Override
	public String getType() {
		return TYPE;
	}

}
