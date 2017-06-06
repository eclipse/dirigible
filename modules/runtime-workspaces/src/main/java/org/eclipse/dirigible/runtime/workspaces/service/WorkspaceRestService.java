package org.eclipse.dirigible.runtime.workspaces.service;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.dirigible.commons.api.helpers.ContentTypeHelper;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.runtime.workspaces.processor.WorkspaceProcessor;

/**
 * Front facing REST service serving the raw repository content
 */
@Singleton
public class WorkspaceRestService implements IRestService {
	
	@Inject
	private WorkspaceProcessor processor;
	
	@GET
	@Path("/workspace/{user}/{workspace}/{path:.*}")
	public Response getResource(@PathParam("user") String user, @PathParam("workspace") String workspace, @PathParam("path") String path) {
		IResource resource = processor.getResource(user, workspace, path);
		if (!resource.exists()) {
			ICollection collection = processor.getCollection(user, workspace, path);
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
	
	@Override
	public Class<? extends IRestService> getType() {
		return WorkspaceRestService.class;
	}
}
