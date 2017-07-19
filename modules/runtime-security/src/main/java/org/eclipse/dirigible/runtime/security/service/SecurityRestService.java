package org.eclipse.dirigible.runtime.security.service;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.eclipse.dirigible.commons.api.helpers.ContentTypeHelper;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.core.security.api.AccessException;
import org.eclipse.dirigible.runtime.security.processor.SecurityProcessor;

/**
 * Front facing REST service serving the raw repository content
 */
@Singleton
public class SecurityRestService implements IRestService {
	
	@Inject
	private SecurityProcessor processor;
	
	@GET
	@Path("/core/security/access")
	public Response listAccess() throws AccessException {
		return Response.ok().entity(processor.renderAccess()).type(ContentTypeHelper.APPLICATION_JSON).build();
	}
	
	@GET
	@Path("/core/security/roles")
	public Response listRoles() throws AccessException {
		return Response.ok().entity(processor.renderRoles()).type(ContentTypeHelper.APPLICATION_JSON).build();
	}
	
	@Override
	public Class<? extends IRestService> getType() {
		return SecurityRestService.class;
	}
}
