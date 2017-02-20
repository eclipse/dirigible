package org.eclipse.dirigible.runtime.scripting;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.repository.ext.generation.IGenerationService;
import org.eclipse.dirigible.repository.ext.generation.IGenerationWorker;
import org.eclipse.dirigible.repository.ext.utils.OSGiUtils;

public class GenerationServiceFactory implements IGenerationService {

	@Override
	public IGenerationWorker getGenerationWorker(String type, HttpServletRequest request) {
		// if (OSGiUtils.isOSGiEnvironment()) {
		return new GenerationServiceFactoryOSGi().getGenerationWorker(type, request);
		// }
		// return new GenerationServiceFactoryNonOSGi().getGenerationWorker(type, request);
	}

	@Override
	public String[] getGenerationWorkerTypes() {
		if (OSGiUtils.isOSGiEnvironment()) {
			return new GenerationServiceFactoryOSGi().getGenerationWorkerTypes();
		}
		return new GenerationServiceFactoryNonOSGi().getGenerationWorkerTypes();
	}

}
