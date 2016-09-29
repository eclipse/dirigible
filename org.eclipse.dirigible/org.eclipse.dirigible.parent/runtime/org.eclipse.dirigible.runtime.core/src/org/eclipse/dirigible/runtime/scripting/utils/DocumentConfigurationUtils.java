package org.eclipse.dirigible.runtime.scripting.utils;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.runtime.scripting.IDocumentService;

public class DocumentConfigurationUtils implements IDocumentService {

	@Override
	public Object getSession() {
		return System.getProperties().get(ICommonConstants.CMIS_CONFIGURATION);
	}

}
