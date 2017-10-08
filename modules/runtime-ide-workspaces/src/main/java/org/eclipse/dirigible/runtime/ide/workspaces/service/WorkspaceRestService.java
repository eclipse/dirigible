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

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.helpers.ContentTypeHelper;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.core.workspace.api.IFile;
import org.eclipse.dirigible.core.workspace.api.IFolder;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.runtime.ide.workspaces.processor.WorkspaceProcessor;

import com.google.gson.Gson;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 * Front facing REST service serving the Workspace content
 */
@Singleton
@Path("/ide/workspaces")
@RolesAllowed({ "Developer" })
@Api(value = "IDE - Workspace", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
@ApiResponses({ @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden") })
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
		String user = UserFacade.getName();
		if (user == null) {
			return Response.status(Status.FORBIDDEN).build();
		}
		List<IWorkspace> workspaces = processor.listWorkspaces();
		List<String> workspacesNames = new ArrayList<String>();
		for (IWorkspace workspace : workspaces) {
			workspacesNames.add(workspace.getName());
		}
		return Response.ok().entity(new Gson().toJson(workspacesNames)).type(ContentTypeHelper.APPLICATION_JSON).build();
	}

	@GET
	@Path("{workspace}")
	public Response getWorkspace(@PathParam("workspace") String workspace, @Context HttpServletRequest request) {
		String user = UserFacade.getName();
		if (user == null) {
			return Response.status(Status.FORBIDDEN).build();
		}

		if (!processor.existsWorkspace(workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}

		IWorkspace workspaceObject = processor.getWorkspace(workspace);
		if (!workspaceObject.exists()) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok().entity(processor.renderWorkspaceTree(workspaceObject)).type(ContentTypeHelper.APPLICATION_JSON).build();
	}

	@POST
	@Path("{workspace}")
	public Response createWorkspace(@PathParam("workspace") String workspace, @Context HttpServletRequest request) throws URISyntaxException {
		String user = UserFacade.getName();
		if (user == null) {
			return Response.status(Status.FORBIDDEN).build();
		}

		if (processor.existsWorkspace(workspace)) {
			return Response.notModified().build();
		}

		IWorkspace workspaceObject = processor.createWorkspace(workspace);
		if (!workspaceObject.exists()) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.created(processor.getURI(workspace, null, null)).entity(processor.renderWorkspaceTree(workspaceObject))
				.type(ContentTypeHelper.APPLICATION_JSON).build();
	}

	@DELETE
	@Path("{workspace}")
	public Response deleteWorkspace(@PathParam("workspace") String workspace, @Context HttpServletRequest request) {
		String user = UserFacade.getName();
		if (user == null) {
			return Response.status(Status.FORBIDDEN).build();
		}

		if (!processor.existsWorkspace(workspace)) {
			return Response.ok().build();
		}

		processor.deleteWorkspace(workspace);
		return Response.noContent().build();
	}

	// Project

	@GET
	@Path("{workspace}/{project}")
	public Response getProject(@PathParam("workspace") String workspace, @PathParam("project") String project, @Context HttpServletRequest request) {
		String user = UserFacade.getName();
		if (user == null) {
			return Response.status(Status.FORBIDDEN).build();
		}

		if (!processor.existsWorkspace(workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}

		if (!processor.existsProject(workspace, project)) {
			String error = format("Project {0} does not exist in Workspace {1}.", project, workspace);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}

		IProject projectObject = processor.getProject(workspace, project);
		if (!projectObject.exists()) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok().entity(processor.renderProjectTree(projectObject)).type(ContentTypeHelper.APPLICATION_JSON).build();
	}

	@POST
	@Path("{workspace}/{project}")
	public Response createProject(@PathParam("workspace") String workspace, @PathParam("project") String project, @Context HttpServletRequest request)
			throws URISyntaxException {
		String user = UserFacade.getName();
		if (user == null) {
			return Response.status(Status.FORBIDDEN).build();
		}

		if (!processor.existsWorkspace(workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}

		if (processor.existsProject(workspace, project)) {
			return Response.notModified().build();
		}

		IProject projectObject = processor.createProject(workspace, project);
		if (!projectObject.exists()) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.created(processor.getURI(workspace, project, null)).entity(processor.renderProjectTree(projectObject))
				.type(ContentTypeHelper.APPLICATION_JSON).build();
	}

	@DELETE
	@Path("{workspace}/{project}")
	public Response deleteProject(@PathParam("workspace") String workspace, @PathParam("project") String project,
			@Context HttpServletRequest request) {
		String user = UserFacade.getName();
		if (user == null) {
			return Response.status(Status.FORBIDDEN).build();
		}

		if (!processor.existsWorkspace(workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}

		if (!processor.existsProject(workspace, project)) {
			return Response.notModified().build();
		}

		processor.deleteProject(workspace, project);
		return Response.noContent().build();
	}

	// Folders and Files

	@GET
	@Path("{workspace}/{project}/{path:.*}")
	public Response getFile(@PathParam("workspace") String workspace, @PathParam("project") String project, @PathParam("path") String path,
			@Context HttpServletRequest request) {
		String user = UserFacade.getName();
		if (user == null) {
			return Response.status(Status.FORBIDDEN).build();
		}

		if (!processor.existsWorkspace(workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}

		if (!processor.existsProject(workspace, project)) {
			String error = format("Project {0} does not exist in Workspace {1}.", project, workspace);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}

		IFile file = processor.getFile(workspace, project, path);
		if (!file.exists()) {
			IFolder folder = processor.getFolder(workspace, project, path);
			if (!folder.exists()) {
				return Response.status(Status.NOT_FOUND).build();
			}
			return Response.ok().entity(processor.renderFolderTree(folder)).type(ContentTypeHelper.APPLICATION_JSON).build();
		}
		String headerContentType = request.getHeader("describe");
		if ((headerContentType != null) && ContentTypeHelper.APPLICATION_JSON.equals(headerContentType)) {
			return Response.ok().entity(processor.renderFileDescription(file)).type(ContentTypeHelper.APPLICATION_JSON).build();
		}
		if (file.isBinary()) {
			return Response.ok().entity(file.getContent()).type(file.getContentType()).build();
		}
		return Response.ok(new String(file.getContent())).type(ContentTypeHelper.TEXT_PLAIN).build();
	}

	@POST
	@Path("{workspace}/{project}/{path:.*}")
	public Response createFile(@PathParam("workspace") String workspace, @PathParam("project") String project, @PathParam("path") String path,
			byte[] content, @Context HttpServletRequest request) throws URISyntaxException {
		String user = UserFacade.getName();
		if (user == null) {
			return Response.status(Status.FORBIDDEN).build();
		}

		if (!processor.existsWorkspace(workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}

		if (!processor.existsProject(workspace, project)) {
			String error = format("Project {0} does not exist in Workspace {1}.", project, workspace);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}

		if (path.endsWith(IRepositoryStructure.SEPARATOR)) {
			IFolder folder = processor.getFolder(workspace, project, path);
			if (folder.exists()) {
				String error = format("Folder {0} already exists in Project {1} in Workspace {2}.", path, project, workspace);
				return Response.status(Status.BAD_REQUEST).entity(error).build();
			}

			folder = processor.createFolder(workspace, project, path);
			return Response.created(processor.getURI(workspace, project, path)).build();
		}

		IFile file = processor.getFile(workspace, project, path);
		if (file.exists()) {
			String error = format("File {0} already exists in Project {1} in Workspace {2}.", path, project, workspace);
			return Response.status(Status.BAD_REQUEST).entity(error).build();
		}

		file = processor.createFile(workspace, project, path, content, request.getContentType());
		return Response.created(processor.getURI(workspace, project, path)).build();
	}

	@POST
	@Path("{workspace}/{project}/{path:.*}")
	public Response createFile(@PathParam("workspace") String workspace, @PathParam("project") String project, @PathParam("path") String path,
			String content, @Context HttpServletRequest request) throws URISyntaxException {
		return createFile(workspace, project, path, content.getBytes(), request);
	}

	@PUT
	@Path("{workspace}/{project}/{path:.*}")
	public Response updateFile(@PathParam("workspace") String workspace, @PathParam("project") String project, @PathParam("path") String path,
			byte[] content, @Context HttpServletRequest request) throws URISyntaxException {
		String user = UserFacade.getName();
		if (user == null) {
			return Response.status(Status.FORBIDDEN).build();
		}

		if (!processor.existsWorkspace(workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}

		if (!processor.existsProject(workspace, project)) {
			String error = format("Project {0} does not exist in Workspace {1}.", project, workspace);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}

		IFile file = processor.getFile(workspace, project, path);
		if (!file.exists()) {
			String error = format("File {0} does not exists in Project {1} in Workspace {2}.", path, project, workspace);
			return Response.status(Status.BAD_REQUEST).entity(error).build();
		}

		file = processor.updateFile(workspace, project, path, content);
		return Response.noContent().build();
	}

	@PUT
	@Path("{workspace}/{project}/{path:.*}")
	public Response updateFile(@PathParam("workspace") String workspace, @PathParam("project") String project, @PathParam("path") String path,
			String content, @Context HttpServletRequest request) throws URISyntaxException {
		return updateFile(workspace, project, path, content.getBytes(), request);
	}

	@DELETE
	@Path("{workspace}/{project}/{path:.*}")
	public Response deleteFile(@PathParam("workspace") String workspace, @PathParam("project") String project, @PathParam("path") String path,
			byte[] content, @Context HttpServletRequest request) throws URISyntaxException {
		String user = UserFacade.getName();
		if (user == null) {
			return Response.status(Status.FORBIDDEN).build();
		}

		if (!processor.existsWorkspace(workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}

		if (!processor.existsProject(workspace, project)) {
			String error = format("Project {0} does not exist in Workspace {1}.", project, workspace);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}

		IFolder folder = processor.getFolder(workspace, project, path);
		if (!folder.exists()) {
			IFile file = processor.getFile(workspace, project, path);
			if (!file.exists()) {
				return Response.notModified().build();
			}

			processor.deleteFile(workspace, project, path);
			return Response.noContent().build();
		}
		processor.deleteFolder(workspace, project, path);
		return Response.noContent().build();
	}

}
