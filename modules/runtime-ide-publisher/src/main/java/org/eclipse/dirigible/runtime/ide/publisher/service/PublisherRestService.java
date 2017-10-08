package org.eclipse.dirigible.runtime.ide.publisher.service;

import static java.text.MessageFormat.format;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.helpers.ContentTypeHelper;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.core.publisher.api.PublisherException;
import org.eclipse.dirigible.core.publisher.definition.PublishLogDefinition;
import org.eclipse.dirigible.core.publisher.definition.PublishRequestDefinition;
import org.eclipse.dirigible.runtime.ide.publisher.processor.PublisherProcessor;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 * Front facing REST service serving the publishing requests
 */
@Singleton
@Path("/ide/publisher")
@RolesAllowed({ "Developer" })
@Api(value = "IDE - Publish", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
@ApiResponses({ @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden") })
public class PublisherRestService implements IRestService {

	@Inject
	private PublisherProcessor processor;

	@Override
	public Class<? extends IRestService> getType() {
		return PublisherRestService.class;
	}

	@POST
	@Path("request/{workspace}/{path:.*}")
	@ApiOperation("Publish Workspace Resources")
	@ApiResponses({ @ApiResponse(code = 200, message = "Execution Result") })
	public Response requestPublishing(@ApiParam(value = "Name of the Workspace", required = true) @PathParam("workspace") String workspace,
			@ApiParam(value = "Resource Path") @PathParam("path") String path, @Context HttpServletRequest request)
			throws PublisherException, URISyntaxException {
		String user = UserFacade.getName();
		if (user == null) {
			return Response.status(Status.FORBIDDEN).build();
		}

		if (!processor.existsWorkspace(user, workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}

		long id = processor.requestPublishing(user, workspace, path);

		return Response.created(new URI("ide/publisher/" + id)).build();
	}

	@GET
	@Path("request/{id}")
	public Response getRequest(@PathParam("id") long id, @Context HttpServletRequest request) throws PublisherException {
		String user = UserFacade.getName();
		if (user == null) {
			return Response.status(Status.FORBIDDEN).build();
		}

		PublishRequestDefinition publishRequestDefinition = processor.getPublishingRequest(id);
		if (publishRequestDefinition != null) {
			return Response.ok().entity(publishRequestDefinition).type(ContentTypeHelper.APPLICATION_JSON).build();
		}
		return Response.status(Status.NOT_FOUND).entity("Publishing request does not exist or has already been processed.").build();
	}

	@GET
	@Path("log")
	public Response listLog(@Context HttpServletRequest request) throws PublisherException {
		String user = UserFacade.getName();
		if (user == null) {
			return Response.status(Status.FORBIDDEN).build();
		}

		List<PublishLogDefinition> publishLogDefinitions = processor.listPublishingLog();
		return Response.ok().entity(publishLogDefinitions).type(ContentTypeHelper.APPLICATION_JSON).build();
	}

	@DELETE
	@Path("log")
	public Response clearLog(@Context HttpServletRequest request) throws PublisherException {
		String user = UserFacade.getName();
		if (user == null) {
			return Response.status(Status.FORBIDDEN).build();
		}

		processor.clearPublishingLog();
		return Response.noContent().build();
	}

}
