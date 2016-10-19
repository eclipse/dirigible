package org.eclipse.dirigible.runtime.scripting.utils;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.runtime.scripting.ILifecycleService;

public class LifecycleServiceUtils implements ILifecycleService {

	private ILifecycleService lifecycleService;

	public LifecycleServiceUtils() throws Exception {
		Object injectedLifecycleService = System.getProperties().get(ICommonConstants.LIFECYCLE_SERVICE);
		if (injectedLifecycleService != null) {
			this.lifecycleService = (ILifecycleService) injectedLifecycleService;
		} else {
			throw new Exception("Lifecycle Service doesn't exist or have not been injected.");
		}
	}

	@Override
	public void activateProject(String projectName, HttpServletRequest request) throws Exception {
		this.lifecycleService.activateProject(projectName, request);
	}

	@Override
	public void publishProject(String projectName, HttpServletRequest request) throws Exception {
		this.lifecycleService.publishProject(projectName, request);
	}

	@Override
	public void publishTemplate(String projectName, HttpServletRequest request) throws Exception {
		this.lifecycleService.publishTemplate(projectName, request);
	}

	@Override
	public void activateAll(HttpServletRequest request) throws Exception {
		this.lifecycleService.activateAll(request);
	}

	@Override
	public void publishAll(HttpServletRequest request) throws Exception {
		this.lifecycleService.publishAll(request);
	}

}
