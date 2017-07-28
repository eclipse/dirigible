package org.eclipse.dirigible.runtime.ide.workspaces.service;

import static java.text.MessageFormat.format;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
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
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.runtime.ide.workspaces.processor.WorkspaceProcessor;

import com.google.gson.Gson;

/**
 * Front facing REST service serving the raw repository content
 */
@Singleton
@Path("/ide/workspaces")
@RolesAllowed({"Developer"})
public class WorkspaceRestService implements IRestService {
	
	@Inject
	private WorkspaceProcessor processor;
	
	@Override
	public Class<? extends IRestService> getType() {
		return WorkspaceRestService.class;
	}
	
	// Workspace

	@GET
	@Path("/")
	public Response listWorkspaces(@Context HttpServletRequest request) {
		String user = request.getRemoteUser();
		if (user == null) {
			return Response.status(Status.FORBIDDEN).build();
		}

		ICollection collection = processor.listWorkspaces(user);
		List<String> workspaces = new ArrayList<String>();
		for (ICollection next : collection.getCollections()) {
			workspaces.add(next.getName());
		}
		return Response.ok().entity(new Gson().toJson(workspaces)).type(ContentTypeHelper.APPLICATION_JSON).build();
	}

	@GET
	@Path("{workspace}")
	public Response getWorkspace(@PathParam("workspace") String workspace, @Context HttpServletRequest request) {
		String user = request.getRemoteUser();
		if (user == null) {
			return Response.status(Status.FORBIDDEN).build();
		}
		
		if (!processor.existsWorkspace(user, workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		
		ICollection collection = processor.getWorkspace(user, workspace);
		if (!collection.exists()) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok().entity(processor.renderTree(collection)).type(ContentTypeHelper.APPLICATION_JSON).build();
	}
	
	@POST
	@Path("{workspace}")
	public Response createWorkspace(@PathParam("workspace") String workspace, @Context HttpServletRequest request) throws URISyntaxException {
		String user = request.getRemoteUser();
		if (user == null) {
			return Response.status(Status.FORBIDDEN).build();
		}
		
		if (processor.existsWorkspace(user, workspace)) {
			return Response.notModified().build();
		}
		
		ICollection collection = processor.createWorkspace(user, workspace);
		if (!collection.exists()) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.created(processor.getURI(workspace, null, null)).entity(processor.renderTree(collection)).type(ContentTypeHelper.APPLICATION_JSON).build();
	}
	
	@DELETE
	@Path("{workspace}")
	public Response deleteWorkspace(@PathParam("workspace") String workspace, @Context HttpServletRequest request) {
		String user = request.getRemoteUser();
		if (user == null) {
			return Response.status(Status.FORBIDDEN).build();
		}
		
		if (!processor.existsWorkspace(user, workspace)) {
			return Response.ok().build();
		}
		
		processor.deleteWorkspace(user, workspace);
		return Response.noContent().build();
	}
	
	// Project
	
	@GET
	@Path("{workspace}/{project}")
	public Response getProject(@PathParam("workspace") String workspace, @PathParam("project") String project, @Context HttpServletRequest request) {
		String user = request.getRemoteUser();
		if (user == null) {
			return Response.status(Status.FORBIDDEN).build();
		}
		
		if (!processor.existsWorkspace(user, workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		
		if (!processor.existsProject(user, workspace, project)) {
			String error = format("Project {0} does not exist in Workspace {1}.", project, workspace);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		
		ICollection collection = processor.getProject(user, workspace, project);
		if (!collection.exists()) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok().entity(processor.renderTree(collection)).type(ContentTypeHelper.APPLICATION_JSON).build();
	}
	
	@POST
	@Path("{workspace}/{project}")
	public Response createProject(@PathParam("workspace") String workspace, @PathParam("project") String project, @Context HttpServletRequest request) throws URISyntaxException {
		String user = request.getRemoteUser();
		if (user == null) {
			return Response.status(Status.FORBIDDEN).build();
		}
		
		if (!processor.existsWorkspace(user, workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		
		if (processor.existsProject(user, workspace, project)) {
			return Response.notModified().build();
		}
		
		ICollection collection = processor.createProject(user, workspace, project);
		if (!collection.exists()) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.created(processor.getURI(workspace, project, null)).entity(processor.renderTree(collection)).type(ContentTypeHelper.APPLICATION_JSON).build();
	}
	
	@DELETE
	@Path("{workspace}/{project}")
	public Response deleteProject(@PathParam("workspace") String workspace, @PathParam("project") String project, @Context HttpServletRequest request) {
		String user = request.getRemoteUser();
		if (user == null) {
			return Response.status(Status.FORBIDDEN).build();
		}
		
		if (!processor.existsWorkspace(user, workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		
		if (!processor.existsProject(user, workspace, project)) {
			return Response.notModified().build();
		}
		
		processor.deleteProject(user, workspace, project);
		return Response.noContent().build();
	}

	
	// Resource

	@GET
	@Path("{workspace}/{project}/{path:.*}")
	public Response getResource(@PathParam("workspace") String workspace, @PathParam("project") String project, @PathParam("path") String path, @Context HttpServletRequest request) {
		String user = request.getRemoteUser();
		if (user == null) {
			return Response.status(Status.FORBIDDEN).build();
		}
		
		if (!processor.existsWorkspace(user, workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		
		if (!processor.existsProject(user, workspace, project)) {
			String error = format("Project {0} does not exist in Workspace {1}.", project, workspace);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		
		IResource resource = processor.getResource(user, workspace, project, path);
		if (!resource.exists()) {
			ICollection collection = processor.getCollection(user, workspace, project, path);
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
	@Path("{workspace}/{project}/{path:.*}")
	public Response createResource(@PathParam("workspace") String workspace, @PathParam("project") String project, @PathParam("path") String path, byte[] content, @Context HttpServletRequest request) throws URISyntaxException {
		String user = request.getRemoteUser();
		if (user == null) {
			return Response.status(Status.FORBIDDEN).build();
		}
		
		if (!processor.existsWorkspace(user, workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		
		if (!processor.existsProject(user, workspace, project)) {
			String error = format("Project {0} does not exist in Workspace {1}.", project, workspace);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		
		if (path.endsWith(IRepositoryStructure.SEPARATOR)) {
			ICollection collection = processor.getCollection(user, workspace, project, path);
			if (collection.exists()) {
				String error = format("Collection {0} already exists in Project {1} in Workspace {2}.", path, project, workspace);
				return Response.status(Status.BAD_REQUEST).entity(error).build();
			}
			
			collection = processor.createCollection(user, workspace, project, path);
			return Response.created(processor.getURI(workspace, project, path)).build();
		}
		
		IResource resource = processor.getResource(user, workspace, project, path);
		if (resource.exists()) {
			String error = format("Resource {0} already exists in Project {1} in Workspace {2}.", path, project, workspace);
			return Response.status(Status.BAD_REQUEST).entity(error).build();
		}
		
		resource = processor.createResource(user, workspace, project, path, content, request.getContentType());
		return Response.created(processor.getURI(workspace, project, path)).build();
	}
	
	@PUT
	@Path("{workspace}/{project}/{path:.*}")
	public Response updateResource(@PathParam("workspace") String workspace, @PathParam("project") String project, @PathParam("path") String path, byte[] content, @Context HttpServletRequest request) throws URISyntaxException {
		String user = request.getRemoteUser();
		if (user == null) {
			return Response.status(Status.FORBIDDEN).build();
		}
		
		if (!processor.existsWorkspace(user, workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		
		if (!processor.existsProject(user, workspace, project)) {
			String error = format("Project {0} does not exist in Workspace {1}.", project, workspace);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		
		IResource resource = processor.getResource(user, workspace, project, path);
		if (!resource.exists()) {
			String error = format("Resource {0} does not exists in Project {1} in Workspace {2}.", path, project, workspace);
			return Response.status(Status.BAD_REQUEST).entity(error).build();
		}
		
		resource = processor.updateResource(user, workspace, project, path, content);
		return Response.noContent().build();
	}
	
	@DELETE
	@Path("{workspace}/{project}/{path:.*}")
	public Response deleteResource(@PathParam("workspace") String workspace, @PathParam("project") String project, @PathParam("path") String path, byte[] content, @Context HttpServletRequest request) throws URISyntaxException {
		String user = request.getRemoteUser();
		if (user == null) {
			return Response.status(Status.FORBIDDEN).build();
		}
		
		if (!processor.existsWorkspace(user, workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		
		if (!processor.existsProject(user, workspace, project)) {
			String error = format("Project {0} does not exist in Workspace {1}.", project, workspace);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		
		if (path.endsWith(IRepositoryStructure.SEPARATOR)) {
			ICollection collection = processor.getCollection(user, workspace, project, path);
			if (!collection.exists()) {
				return Response.notModified().build();
			}
			
			processor.deleteCollection(user, workspace, project, path);
			return Response.noContent().build();
		}
		
		IResource resource = processor.getResource(user, workspace, project, path);
		if (!resource.exists()) {
			return Response.notModified().build();
		}
		
		processor.deleteResource(user, workspace, project, path);
		return Response.noContent().build();
	}
	
}
