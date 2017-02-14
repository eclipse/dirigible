package org.eclipse.dirigible.ide.template.ui.mobile.service;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.ide.template.ui.common.service.GenerationUtils;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.ext.generation.IGenerationWorker;
import org.eclipse.dirigible.repository.ext.generation.IGenerationWorkerProvider;

public class MobileForEntityGenerationWorkerProvider implements IGenerationWorkerProvider {

	@Override
	public String getType() {
		return ICommonConstants.TEMPLATE_TYPE.MOBILE_APPLICATION_FOR_ENTITY;
	}

	@Override
	public IGenerationWorker createWorker(HttpServletRequest request) throws Exception {
		return new MobileForEntityGenerationWorker(GenerationUtils.getRepository(request),
				GenerationUtils.getWorkspace(request));
	}
}
