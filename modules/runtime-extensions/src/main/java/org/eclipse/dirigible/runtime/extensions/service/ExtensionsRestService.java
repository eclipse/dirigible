package org.eclipse.dirigible.runtime.extensions.service;

import static java.text.MessageFormat.format;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.core.extensions.api.ExtensionsException;
import org.eclipse.dirigible.runtime.extensions.processor.ExtensionPoint;
import org.eclipse.dirigible.runtime.extensions.processor.ExtensionsProcessor;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 * Front facing REST service serving the raw repository content
 */
@Singleton
@Path("/core/extensions")
@Api(value = "Core - Extensions", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
@ApiResponses({ @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden") })
public class ExtensionsRestService implements IRestService {

	@Inject
	private ExtensionsProcessor processor;

	@GET
	@Path("/")
	@Produces("application/json")
	@ApiOperation("List all the Extension Points with their Extensions")
	@ApiResponses({ @ApiResponse(code = 200, message = "List of Extension Points", response = ExtensionPoint.class, responseContainer = "List") })
	public Response listExtensionPoints() throws ExtensionsException {
		return Response.ok().entity(processor.renderExtensionPoints()).build();
	}

	@GET
	@Path("/{name}")
	@Produces("application/json")
	@ApiOperation("Returns the Extension Point with their Extensions requested by its name")
	@ApiResponses({ @ApiResponse(code = 200, message = "The Extension Point", response = ExtensionPoint.class),
			@ApiResponse(code = 404, message = "Extension Point with the requested name does not exist") })
	public Response getExtensionPoint(@ApiParam(value = "Name of the ExtensionPoint", required = true) @PathParam("name") String name)
			throws ExtensionsException {
		String json = processor.renderExtensionPoint(name);
		if (json == null) {
			return Response.status(Status.NOT_FOUND).entity(format("ExtensionPoint with name [{0}] does not exist", name)).build();
		}
		return Response.ok().entity(json).build();
	}

	@Override
	public Class<? extends IRestService> getType() {
		return ExtensionsRestService.class;
	}
}
