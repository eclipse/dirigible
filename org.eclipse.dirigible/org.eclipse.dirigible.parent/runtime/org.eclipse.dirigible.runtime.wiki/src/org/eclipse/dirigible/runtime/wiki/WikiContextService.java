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

package org.eclipse.dirigible.runtime.wiki;

import org.eclipse.dirigible.runtime.scripting.IContextService;
import org.eclipse.dirigible.runtime.scripting.IInjectedAPIAliases;

public class WikiContextService implements IContextService {

	@Override
	public String getName() {
		return IInjectedAPIAliases.WIKI_UTILS;
	}

	@Override
	public Object getInstance() {
		WikiUtils wikiUtils = new WikiUtils();
		return wikiUtils;
	}

}
