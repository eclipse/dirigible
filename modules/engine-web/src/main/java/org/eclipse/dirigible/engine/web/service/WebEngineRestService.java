package org.eclipse.dirigible.engine.web.service;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.eclipse.dirigible.commons.api.service.IRestService;

/**
 * Front facing REST service serving the raw web content from the registry/public space
 */
@Singleton
public class WebEngineRestService extends AbstractWebEngineRestService {
	
	@GET
	@Path("/web/{path:.*}")
	public Response getResource(@PathParam("path") String path) {
		return super.getResource(path);
	}

	@Override
	public Class<? extends IRestService> getType() {
		return WebEngineRestService.class;
	}
}
