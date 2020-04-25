/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.runtime.git.module;

import org.eclipse.dirigible.commons.api.module.AbstractDirigibleModule;
import org.eclipse.dirigible.commons.config.Configuration;

public class GitModule extends AbstractDirigibleModule {

	private static final String MODULE_NAME = "Git Module";

	@Override
	protected void configure() {
		Configuration.load("/dirigible-git.properties");
	}

	@Override
	public String getName() {
		return MODULE_NAME;
	}

}
