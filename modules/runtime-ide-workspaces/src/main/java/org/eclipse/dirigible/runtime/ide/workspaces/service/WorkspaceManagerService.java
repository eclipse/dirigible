package org.eclipse.dirigible.runtime.ide.workspaces.service;

import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.eclipse.dirigible.runtime.ide.workspaces.processor.WorkspaceProcessor;
import org.eclipse.dirigible.runtime.ide.workspaces.service.manager.SourceTargetPair;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 * Front facing RPC service serving the Workspace actions
 */
@Singleton
@Path("/ide/workspace")
@RolesAllowed({ "Developer" })
@Api(value = "IDE - Workspace Manager", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
@ApiResponses({ @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden") })
public class WorkspaceManagerService implements IRestService {

	@Inject
	private WorkspaceProcessor processor;

	@Override
	public Class<? extends IRestService> getType() {
		return WorkspaceManagerService.class;
	}

	@POST
	@Path("{workspace}/copy")
	public Response copy(@PathParam("workspace") String workspace, Reader content, @Context HttpServletRequest request)
			throws URISyntaxException, UnsupportedEncodingException {
		String user = request.getRemoteUser();
		if (user == null) {
			return Response.status(Status.FORBIDDEN).build();
		}

		SourceTargetPair workspaceManagerCopy = GsonHelper.GSON.fromJson(content, SourceTargetPair.class);
		if ((workspaceManagerCopy.getSource() == null) || (workspaceManagerCopy.getTarget() == null)) {
			return Response.status(Status.BAD_REQUEST).entity("Source and Target paths have to be present in the body of the request").build();
		}

		RepositoryPath sourcePath = new RepositoryPath(workspaceManagerCopy.getSource());
		if (sourcePath.getSegments().length == 0) {
			return Response.status(Status.BAD_REQUEST).entity("Source path is empty").build();
		}

		RepositoryPath targetPath = new RepositoryPath(workspaceManagerCopy.getTarget());
		if (targetPath.getSegments().length == 0) {
			return Response.status(Status.BAD_REQUEST).entity("Target path is empty").build();
		}

		String sourceProject = sourcePath.getSegments()[0];
		String targetProject = targetPath.getSegments()[0];
		if (sourcePath.getSegments().length == 1) {
			// a project is selected as a source
			processor.copyProject(workspace, sourceProject, targetProject);
			return Response.created(processor.getURI(workspace, targetProject, null)).build();
		}

		String targetFilePath = targetPath.constructPathFrom(1);
		if (!processor.existsFolder(workspace, sourceProject, targetFilePath)) {
			return Response.status(Status.BAD_REQUEST).entity("Target path points to a non-existing folder").build();
		}

		String sourceFilePath = sourcePath.constructPathFrom(1);
		if (processor.existsFile(workspace, sourceProject, sourceFilePath)) {
			processor.copyFile(workspace, sourceProject, sourceFilePath, targetProject, targetFilePath);
		} else {
			processor.copyFolder(workspace, sourceProject, sourceFilePath, targetProject, targetFilePath);
		}

		return Response.created(processor.getURI(workspace, null, workspaceManagerCopy.getTarget())).build();
	}

	@POST
	@Path("{workspace}/move")
	public Response move(@PathParam("workspace") String workspace, Reader content, @Context HttpServletRequest request)
			throws URISyntaxException, UnsupportedEncodingException {
		String user = request.getRemoteUser();
		if (user == null) {
			return Response.status(Status.FORBIDDEN).build();
		}

		SourceTargetPair workspaceManagerCopy = GsonHelper.GSON.fromJson(content, SourceTargetPair.class);
		if ((workspaceManagerCopy.getSource() == null) || (workspaceManagerCopy.getTarget() == null)) {
			return Response.status(Status.BAD_REQUEST).entity("Source and Target paths have to be present in the body of the request").build();
		}

		RepositoryPath sourcePath = new RepositoryPath(workspaceManagerCopy.getSource());
		if (sourcePath.getSegments().length == 0) {
			return Response.status(Status.BAD_REQUEST).entity("Source path is empty").build();
		}

		RepositoryPath targetPath = new RepositoryPath(workspaceManagerCopy.getTarget());
		if (targetPath.getSegments().length == 0) {
			return Response.status(Status.BAD_REQUEST).entity("Target path is empty").build();
		}

		String sourceProject = sourcePath.getSegments()[0];
		String targetProject = targetPath.getSegments()[0];
		if (sourcePath.getSegments().length == 1) {
			// a project is selected as a source
			processor.moveProject(workspace, sourceProject, targetProject);
			return Response.created(processor.getURI(workspace, targetProject, null)).build();
		}

		String sourceFilePath = sourcePath.constructPathFrom(1);
		String targetFilePath = targetPath.constructPathFrom(1);
		if (processor.existsFile(workspace, sourceProject, sourceFilePath)) {
			processor.moveFile(workspace, sourceProject, sourceFilePath, targetProject, targetFilePath);
		} else {
			processor.moveFolder(workspace, sourceProject, sourceFilePath, targetProject, targetFilePath);
		}

		return Response.created(processor.getURI(workspace, null, workspaceManagerCopy.getTarget())).build();
	}

	@POST
	@Path("{workspace}/rename")
	public Response rename(@PathParam("workspace") String workspace, Reader content, @Context HttpServletRequest request)
			throws URISyntaxException, UnsupportedEncodingException {
		return move(workspace, content, request);
	}

}
