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

package org.eclipse.dirigible.runtime.flow;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.runtime.registry.AbstractRegistryServiceServlet;

public class FlowRegistryServlet extends AbstractRegistryServiceServlet {

    private static final long serialVersionUID = -8255379751142002763L;
    
    public static final String INTEGRATION_FOLDER = ICommonConstants.ARTIFACT_TYPE.INTEGRATION_SERVICES + IRepository.SEPARATOR;

    @Override
    protected String getServletMapping() {
        return "/flow/";
    }

    @Override
    protected String getFileExtension() {
        return ".flow";
    }

    @Override
    protected String getRequestProcessingFailedMessage() {
        return "Flow execution failed.";
    }
    
    protected String getServicesFolder() {
		return INTEGRATION_FOLDER;
	}

}
