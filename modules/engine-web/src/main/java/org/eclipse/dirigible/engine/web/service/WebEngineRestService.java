package org.eclipse.dirigible.engine.web.service;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.dirigible.commons.api.context.ThreadContextFacade;
import org.eclipse.dirigible.commons.api.scripting.ScriptingContextException;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.engine.web.processor.WebEngineProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Front facing REST service serving the raw web content
 *
 */
@Singleton
public class WebEngineRestService implements IRestService {
	
	private static final Logger logger = LoggerFactory.getLogger(WebEngineRestService.class.getCanonicalName());
	
	@Inject
	private WebEngineProcessor processor;
	
	/**
	 * @param path
	 * @param request
	 * @return resource content
	 */
	@GET
	@Path("/web/{path:.*}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getResource(@PathParam("path") String path, @Context HttpServletRequest request, @Context HttpServletResponse response) {
		try {
			ThreadContextFacade.setUp();
			ThreadContextFacade.set(HttpServletRequest.class.getCanonicalName(), request);
			ThreadContextFacade.set(HttpServletResponse.class.getCanonicalName(), response);
			try {
				String result = processor.getResource(path);
				return Response.ok(result).build();
			} finally {
				ThreadContextFacade.tearDown();
			}
		} catch(ScriptingContextException e) {
			logger.error(e.getMessage(), e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@Override
	public Class<? extends IRestService> getType() {
		return WebEngineRestService.class;
	}
}
