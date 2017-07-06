package org.eclipse.dirigible.runtime.repository.service;

import static java.text.MessageFormat.format;

import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.dirigible.commons.api.helpers.ContentTypeHelper;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.runtime.repository.processor.RepositoryProcessor;

/**
 * Front facing REST service serving the raw repository content
 */
@Singleton
public class RepositoryRestService implements IRestService {
	
	@Inject
	private RepositoryProcessor processor;
	
	@GET
	@Path("/repository/{path:.*}")
	public Response getResource(@PathParam("path") String path) {
		IResource resource = processor.getResource(path);
		if (!resource.exists()) {
			ICollection collection = processor.getCollection(path);
			if (!collection.exists()) {
				return Response.status(Status.NOT_FOUND).build();
			}
			return Response.ok().entity(processor.renderTree(collection)).type(ContentTypeHelper.APPLICATION_JSON).build();
		}
		if (resource.isBinary()) {
			return Response.ok().entity(resource.getContent()).type(resource.getContentType()).build();
		}
		return Response.ok(new String(resource.getContent())).type(resource.getContentType()).build();
	}
	
	@POST
	@Path("/repository/{path:.*}")
	public Response createResource(@PathParam("path") String path, byte[] content, @Context HttpServletRequest request) throws URISyntaxException {
		IResource resource = processor.getResource(path);
		if (resource.exists()) {
			return Response.status(Status.BAD_REQUEST).entity(format("Resource at location {0} already exists", path)).build();
		}
		resource = processor.createResource(path, content, request.getContentType());
		return Response.created(new URI(resource.getPath())).build();
	}
	
	@PUT
	@Path("/repository/{path:.*}")
	public Response updateResource(@PathParam("path") String path, byte[] content) {
		IResource resource = processor.getResource(path);
		if (!resource.exists()) {
			return Response.status(Status.NOT_FOUND).entity(format("Resource at location {0} does not exist", path)).build();
		}
		resource = processor.updateResource(path, content);
		return Response.noContent().build();
	}
	
	@DELETE
	@Path("/repository/{path:.*}")
	public Response deleteResource(@PathParam("path") String path) {
		IResource resource = processor.getResource(path);
		if (!resource.exists()) {
			return Response.status(Status.NOT_FOUND).entity(format("Resource at location {0} does not exist", path)).build();
		}
		processor.deleteResource(path);
		return Response.noContent().build();
	}
	
	

	@Override
	public Class<? extends IRestService> getType() {
		return RepositoryRestService.class;
	}
}
