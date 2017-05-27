package org.eclipse.dirigible.engine.web.service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.eclipse.dirigible.api.v3.net.http.APIv3Request;
import org.eclipse.dirigible.engine.web.processor.WebEngineProcessor;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class WebEngineService {
	
	@Inject
	private WebEngineProcessor processor;
	
	/**
	 * @param path
	 * @param request
	 * @return resource content
	 */
	@GET
	@Path("/services/web/{path:.*}")
	public String getResource(@PathParam("path") String path, @Context HttpServletRequest request) {
		APIv3Request.set(request);
		try {
			String result = processor.getResource(path);
			return result;
		} finally {
			APIv3Request.set(null);
		}
	}

}
