package org.eclipse.dirigible.runtime.extensions.service;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.eclipse.dirigible.commons.api.helpers.ContentTypeHelper;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.core.extensions.api.ExtensionsException;
import org.eclipse.dirigible.runtime.extensions.processor.ExtensionsProcessor;

/**
 * Front facing REST service serving the raw repository content
 */
@Singleton
public class ExtensionsRestService implements IRestService {
	
	@Inject
	private ExtensionsProcessor processor;
	
	@GET
	@Path("/extensions")
	public Response list() throws ExtensionsException {
		return Response.ok().entity(processor.renderTree()).type(ContentTypeHelper.APPLICATION_JSON).build();
	}
	
	@Override
	public Class<? extends IRestService> getType() {
		return ExtensionsRestService.class;
	}
}
