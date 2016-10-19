package org.eclipse.dirigible.runtime.scripting;

import javax.servlet.http.HttpServletRequest;

public interface ILifecycleService {

	public void activateProject(String projectName, HttpServletRequest request) throws Exception;

	public void publishProject(String projectName, HttpServletRequest request) throws Exception;

	public void publishTemplate(String projectName, HttpServletRequest request) throws Exception;

	public void activateAll(HttpServletRequest request) throws Exception;

	public void publishAll(HttpServletRequest request) throws Exception;

}
