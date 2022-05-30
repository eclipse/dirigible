/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.runtime.git.service;

import static java.text.MessageFormat.format;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.helpers.ContentTypeHelper;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.service.AbstractRestService;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.core.git.GitCommitInfo;
import org.eclipse.dirigible.core.git.GitConnectorException;
import org.eclipse.dirigible.core.git.project.ProjectOriginUrls;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.runtime.git.model.GitCheckoutModel;
import org.eclipse.dirigible.runtime.git.model.GitCloneModel;
import org.eclipse.dirigible.runtime.git.model.GitDiffModel;
import org.eclipse.dirigible.runtime.git.model.GitProjectChangedFiles;
import org.eclipse.dirigible.runtime.git.model.GitProjectLocalBranches;
import org.eclipse.dirigible.runtime.git.model.GitProjectRemoteBranches;
import org.eclipse.dirigible.runtime.git.model.GitPullModel;
import org.eclipse.dirigible.runtime.git.model.GitPushModel;
import org.eclipse.dirigible.runtime.git.model.GitResetModel;
import org.eclipse.dirigible.runtime.git.model.GitShareModel;
import org.eclipse.dirigible.runtime.git.model.GitUpdateDependenciesModel;
import org.eclipse.dirigible.runtime.git.processor.GitProcessor;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
/**
 * Front facing REST service serving the Git commands.
 */
@Path("/ide/git/{workspace}")
@Api(value = "IDE - Git", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
@ApiResponses({ @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden") })
public class GitRestService extends AbstractRestService implements IRestService {


	private static final Logger logger = LoggerFactory.getLogger(GitRestService.class);

	private GitProcessor processor = new GitProcessor();
	
	@Context
	private HttpServletResponse response;

	/**
	 * Clone repository.
	 *
	 * @param workspace the workspace
	 * @param model the model
	 * @return the response
	 * @throws GitConnectorException the git connector exception
	 */
	@POST
	@Path("/clone")
	@Produces("application/json")
	@ApiOperation("Clone Git Repository")
	@ApiResponses({ @ApiResponse(code = 200, message = "Git Repository Cloned") })
	public Response cloneRepository(@ApiParam(value = "Name of the Workspace", required = true) @PathParam("workspace") String workspace,
			GitCloneModel model) throws GitConnectorException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		processor.clone(workspace, model);
		return Response.ok().build();
	}

	/**
	 * Pull projects.
	 *
	 * @param workspace the workspace
	 * @param model the model
	 * @return the response
	 * @throws GitConnectorException 
	 */
	@POST
	@Path("/pull")
	@Produces("application/json")
	@ApiOperation("Pull Git Projects into the Workspace")
	@ApiResponses({ @ApiResponse(code = 200, message = "Git Projects Pulled") })
	public Response pullProjects(@ApiParam(value = "Name of the Workspace", required = true) @PathParam("workspace") String workspace,
			GitPullModel model) throws GitConnectorException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		processor.pull(workspace, model);
		return Response.ok().build();
	}

	/**
	 * Pull project.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param model the model
	 * @return the response
	 * @throws GitConnectorException 
	 */
	@POST
	@Path("/{project}/pull")
	@Produces("application/json")
	@ApiOperation("Pull Git Project into the Workspace")
	@ApiResponses({ @ApiResponse(code = 200, message = "Git Project Pulled") })
	public Response pullProject(@ApiParam(value = "Name of the Workspace", required = true) @PathParam("workspace") String workspace,
			@ApiParam(value = "Name of the Project", required = true) @PathParam("project") String project, GitPullModel model) throws GitConnectorException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		model.setProjects(Arrays.asList(project));
		processor.pull(workspace, model);
		return Response.ok().build();
	}

	/**
	 * Push projects.
	 *
	 * @param workspace the workspace
	 * @param model the model
	 * @return the response
	 * @throws GitConnectorException 
	 */
	@POST
	@Path("/push")
	@Produces("application/json")
	@ApiOperation("Push Git Projects into Git Repository")
	@ApiResponses({ @ApiResponse(code = 200, message = "Git Projects Pushed") })
	public Response pushProjects(@ApiParam(value = "Name of the Workspace", required = true) @PathParam("workspace") String workspace,
			GitPushModel model) throws GitConnectorException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		processor.push(workspace, model);
		return Response.ok().build();
	}

	/**
	 * Push project.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param model the model
	 * @return the response
	 * @throws GitConnectorException 
	 */
	@POST
	@Path("/{project}/push")
	@Produces("application/json")
	@ApiOperation("Push Git Project into Git Repository")
	@ApiResponses({ @ApiResponse(code = 200, message = "Git Project Pushed") })
	public Response pushProject(@ApiParam(value = "Name of the Workspace", required = true) @PathParam("workspace") String workspace,
			@ApiParam(value = "Name of the Project", required = true) @PathParam("project") String project, GitPushModel model) throws GitConnectorException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		model.setProjects(Arrays.asList(project));
		processor.push(workspace, model);
		return Response.ok().build();
	}

	/**
	 * Reset projects.
	 *
	 * @param workspace the workspace
	 * @param model the model
	 * @return the response
	 * @throws GitConnectorException 
	 */
	@POST
	@Path("/reset")
	@Produces("application/json")
	@ApiOperation("Hard Reset Git Projects in the Workspace")
	@ApiResponses({ @ApiResponse(code = 200, message = "Git Projects Reset") })
	public Response resetProjects(@ApiParam(value = "Name of the Workspace", required = true) @PathParam("workspace") String workspace,
			GitResetModel model) throws GitConnectorException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		processor.reset(workspace, model);
		return Response.ok().build();
	}

	/**
	 * Reset project.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param model the model
	 * @return the response
	 * @throws GitConnectorException 
	 */
	@POST
	@Path("/{project}/reset")
	@Produces("application/json")
	@ApiOperation("Hard Reset Git Project in the Workspace")
	@ApiResponses({ @ApiResponse(code = 200, message = "Git Project Reset") })
	public Response resetProject(@ApiParam(value = "Name of the Workspace", required = true) @PathParam("workspace") String workspace,
			@ApiParam(value = "Name of the Project", required = true) @PathParam("project") String project, GitResetModel model) throws GitConnectorException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		model.setProjects(Arrays.asList(project));
		processor.reset(workspace, model);
		return Response.ok().build();
	}

	/**
	 * Reset project.
	 *
	 * @param workspace the workspace
	 * @param repositoryName the project
	 * @return the response
	 * @throws GitConnectorException in case of exception 
	 */
	@DELETE
	@Path("/{repositoryName}/delete")
	@Produces("application/json")
	@ApiOperation("Delete Git Repository")
	@ApiResponses({ @ApiResponse(code = 200, message = "Git Project Reset") })
	public Response deleteGitRepository(@ApiParam(value = "Name of the Workspace", required = true) @PathParam("workspace") String workspace,
			@ApiParam(value = "Name of the Project", required = true) @PathParam("repositoryName") String repositoryName) throws GitConnectorException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		processor.delete(workspace, repositoryName);
		return Response.ok().build();
	}

	/**
	 * Share project.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param model the model
	 * @return the response
	 * @throws GitConnectorException 
	 */
	@POST
	@Path("/{project}/share")
	@Produces("application/json")
	@ApiOperation("Share Git Project into Git Repository")
	@ApiResponses({ @ApiResponse(code = 200, message = "Git Project Shared") })
	public Response shareProject(@ApiParam(value = "Name of the Workspace", required = true) @PathParam("workspace") String workspace,
			@ApiParam(value = "Name of the Project", required = true) @PathParam("project") String project, GitShareModel model) throws GitConnectorException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		model.setProject(project);
		processor.share(workspace, model);
		return Response.ok().build();
	}
	
	/**
	 * Checkout project.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param model the model
	 * @return the response
	 * @throws GitConnectorException 
	 */
	@POST
	@Path("/{project}/checkout")
	@Produces("application/json")
	@ApiOperation("Checkout Git Project into Git Repository")
	@ApiResponses({ @ApiResponse(code = 200, message = "Git Branch is Checked-out") })
	public Response checkoutBranch(@ApiParam(value = "Name of the Workspace", required = true) @PathParam("workspace") String workspace,
			@ApiParam(value = "Name of the Project", required = true) @PathParam("project") String project, GitCheckoutModel model) throws GitConnectorException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		model.setProject(project);
		processor.checkout(workspace, model);
		return Response.ok().build();
	}
	
	/**
	 * Commit project.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param model the model
	 * @return the response
	 * @throws GitConnectorException 
	 */
	@POST
	@Path("/{project}/commit")
	@Produces("application/json")
	@ApiOperation("Commit Git Project into Git Repository")
	@ApiResponses({ @ApiResponse(code = 200, message = "Git Project Committed") })
	public Response commitProject(@ApiParam(value = "Name of the Workspace", required = true) @PathParam("workspace") String workspace,
			@ApiParam(value = "Name of the Project", required = true) @PathParam("project") String project, GitPushModel model) throws GitConnectorException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		model.setProjects(Arrays.asList(project));
		processor.commit(workspace, model);
		return Response.ok().build();
	}

	/**
	 * Update projects dependencies.
	 *
	 * @param workspace the workspace
	 * @param model the model
	 * @return the response
	 * @throws GitConnectorException the git connector exception
	 */
	@POST
	@Path("/uppdate-dependencies")
	@Produces("application/json")
	@ApiOperation("Update Git Projects Dependencies")
	@ApiResponses({ @ApiResponse(code = 200, message = "Git Projects Dependencies Updated") })
	public Response updateProjectsDependencies(@ApiParam(value = "Name of the Workspace", required = true) @PathParam("workspace") String workspace,
			GitUpdateDependenciesModel model) throws GitConnectorException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		processor.updateDependencies(workspace, model);
		return Response.ok().build();
	}

	/**
	 * Update project dependencies.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param model the model
	 * @return the response
	 * @throws GitConnectorException the git connector exception
	 */
	@POST
	@Path("/{project}/uppdate-dependencies")
	@Produces("application/json")
	@ApiOperation("Update Git Project Dependencies")
	@ApiResponses({ @ApiResponse(code = 200, message = "Git Project Dependencies Updated") })
	public Response updateProjectDependencies(@ApiParam(value = "Name of the Workspace", required = true) @PathParam("workspace") String workspace,
			@ApiParam(value = "Name of the Project", required = true) @PathParam("project") String project, GitUpdateDependenciesModel model)
			throws GitConnectorException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		model.setProjects(Arrays.asList(project));
		processor.updateDependencies(workspace, model);
		return Response.ok().build();
	}
	
	
	
	
	
	/**
	 * Get local branches.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @return the response
	 * @throws GitConnectorException the git connector exception
	 */
	@GET
	@Path("/{project}/branches/local")
	@Produces("application/json")
	@ApiOperation("Get Project Local Branches")
	@ApiResponses({ @ApiResponse(code = 200, message = "Git Project Local Branches") })
	public Response getProjectLocalBranches(@ApiParam(value = "Name of the Workspace", required = true) @PathParam("workspace") String workspace,
			@ApiParam(value = "Name of the Project", required = true) @PathParam("project") String project)
			throws GitConnectorException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		GitProjectLocalBranches gitProjectBranches = processor.getLocalBranches(workspace, project);
		return Response.ok().entity(GsonHelper.GSON.toJson(gitProjectBranches)).type(ContentTypeHelper.APPLICATION_JSON).build();
	}
	
	/**
	 * Get remote branches.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @return the response
	 * @throws GitConnectorException the git connector exception
	 */
	@GET
	@Path("/{project}/branches/remote")
	@Produces("application/json")
	@ApiOperation("Get Project Remote Branches")
	@ApiResponses({ @ApiResponse(code = 200, message = "Git Project Remote Branches") })
	public Response getProjectRemoteBranches(@ApiParam(value = "Name of the Workspace", required = true) @PathParam("workspace") String workspace,
			@ApiParam(value = "Name of the Project", required = true) @PathParam("project") String project)
			throws GitConnectorException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		GitProjectRemoteBranches gitProjectBranches = processor.getRemoteBranches(workspace, project);
		return Response.ok().entity(GsonHelper.GSON.toJson(gitProjectBranches)).type(ContentTypeHelper.APPLICATION_JSON).build();
	}
	
	/**
	 * Gets the workspace.
	 *
	 * @param workspace
	 *            the workspace
	 * @param request
	 *            the request
	 * @return the workspace
	 */
	@GET
	@Path("/")
	public Response getGitRepositories(@PathParam("workspace") String workspace, @Context HttpServletRequest request) {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		if (!processor.existsWorkspace(workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			return createErrorResponseNotFound(error);
		}

		IWorkspace workspaceObject = processor.getWorkspace(workspace);
		if (!workspaceObject.exists()) {
			return createErrorResponseNotFound(workspace);
		}
		return Response.ok().entity(processor.renderGitRepositories(user, workspace)).type(ContentTypeHelper.APPLICATION_JSON).build();
	}

	/**
	 * Get unstaged files.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @return the response
	 * @throws GitConnectorException the git connector exception
	 */
	@GET
	@Path("/{project}")
	@Produces("application/json")
	@ApiOperation("Get Project Files")
	@ApiResponses({ @ApiResponse(code = 200, message = "Git Project Files") })
	public Response getProject(@ApiParam(value = "Name of the Workspace", required = true) @PathParam("workspace") String workspace,
			@ApiParam(value = "Name of the Project", required = true) @PathParam("project") String project)
			throws GitConnectorException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		IWorkspace workspaceObject = processor.getWorkspace(workspace);
		return Response.ok().entity(processor.renderWorkspaceProject(workspaceObject, project)).type(ContentTypeHelper.APPLICATION_JSON).build();
	}
	/**
	 * Get unstaged files.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @return the response
	 * @throws GitConnectorException the git connector exception
	 */
	@GET
	@Path("/{project}/unstaged")
	@Produces("application/json")
	@ApiOperation("Get Project Unstaged Files")
	@ApiResponses({ @ApiResponse(code = 200, message = "Git Project Unstaged Files") })
	public Response getProjectUnstagedFiles(@ApiParam(value = "Name of the Workspace", required = true) @PathParam("workspace") String workspace,
			@ApiParam(value = "Name of the Project", required = true) @PathParam("project") String project)
			throws GitConnectorException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		GitProjectChangedFiles gitProjectFiles = processor.getUnstagedFiles(workspace, project);
		return Response.ok().entity(GsonHelper.GSON.toJson(gitProjectFiles)).type(ContentTypeHelper.APPLICATION_JSON).build();
	}
	
	/**
	 * Get staged files.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @return the response
	 * @throws GitConnectorException the git connector exception
	 */
	@GET
	@Path("/{project}/staged")
	@Produces("application/json")
	@ApiOperation("Get Project Staged Files")
	@ApiResponses({ @ApiResponse(code = 200, message = "Git Project Staged Files") })
	public Response getProjectStagedFiles(@ApiParam(value = "Name of the Workspace", required = true) @PathParam("workspace") String workspace,
			@ApiParam(value = "Name of the Project", required = true) @PathParam("project") String project)
			throws GitConnectorException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		GitProjectChangedFiles gitProjectFiles = processor.getStagedFiles(workspace, project);
		return Response.ok().entity(GsonHelper.GSON.toJson(gitProjectFiles)).type(ContentTypeHelper.APPLICATION_JSON).build();
	}

	/**
	 * Add file to index.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param paths the paths to be added
	 * @return the response
	 * @throws GitConnectorException the git connector exception
	 */
	@POST
	@Path("/{project}/add")
	@ApiOperation("Add file to index")
	@ApiResponses({ @ApiResponse(code = 200, message = "Add file to index") })
	public Response addFileToIndex(@ApiParam(value = "Name of the Workspace", required = true) @PathParam("workspace") String workspace,
			@ApiParam(value = "Name of the Project", required = true) @PathParam("project") String project, String paths)
			throws GitConnectorException {
		if (paths == null || "".equals(paths)) {
			return Response.status(Status.NOT_FOUND).build();
		}
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		processor.addFileToIndex(workspace, project, paths);
		return Response.ok().build();
	}
	
	/**
	 * Revert file to index.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param paths the paths to be added
	 * @return the response
	 * @throws GitConnectorException the git connector exception
	 */
	@POST
	@Path("/{project}/revert")
	@ApiOperation("Revert file to index")
	@ApiResponses({ @ApiResponse(code = 200, message = "Revert file to index") })
	public Response revertToHeadRevision(@ApiParam(value = "Name of the Workspace", required = true) @PathParam("workspace") String workspace,
			@ApiParam(value = "Name of the Project", required = true) @PathParam("project") String project, String paths)
			throws GitConnectorException {
		if (paths == null || "".equals(paths)) {
			return Response.status(Status.NOT_FOUND).build();
		}
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		processor.revertToHeadRevision(workspace, project, paths);
		return Response.ok().build();
	}
	
	/**
	 * Remove file from index.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param paths the paths to be added
	 * @return the response
	 * @throws GitConnectorException the git connector exception
	 */
	@POST
	@Path("/{project}/remove")
	@ApiOperation("Remove file from index.")
	@ApiResponses({ @ApiResponse(code = 200, message = "Remove file from index.") })
	public Response removeFileFromIndex(@ApiParam(value = "Name of the Workspace", required = true) @PathParam("workspace") String workspace,
			@ApiParam(value = "Name of the Project", required = true) @PathParam("project") String project, String paths)
			throws GitConnectorException {
		if (paths == null || "".equals(paths)) {
			return Response.status(Status.NOT_FOUND).build();
		}
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		processor.removeFileFromIndex(workspace, project, paths);
		return Response.ok().build();
	}

	/**
	 * Get remote origin URLs.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @return the response
	 * @throws GitConnectorException in case of exception
	 */
	@GET
	@Path("/{project}/origin-urls")
	@Produces("application/json")
	@ApiOperation("Get Project Repo URLs")
	@ApiResponses({ @ApiResponse(code = 200, message = "Git Project Repo URLs") })
	public Response getOriginUrl(@PathParam("workspace") String workspace, @PathParam("project") String project) throws GitConnectorException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		ProjectOriginUrls originUrls = processor.getOriginUrls(workspace, project);
		return Response.ok(originUrls).build();
	}

	/**
	 * Update remote origin fetch URL.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param url the new fetch URL   
	 * @return the response
	 * @throws GitConnectorException Git Connector Exception
	 * @throws GitAPIException Git API Exception
	 * @throws URISyntaxException URL with wrong format provided
	 */
	@POST
	@Path("/{project}/fetch-url")
	@Produces("application/json")
	@ApiOperation("Set origin fetch URL")
	@ApiResponses({ @ApiResponse(code = 200, message = "Git File Diff") })
	public Response setFetchUrl(@PathParam("workspace") String workspace, @PathParam("project") String project, @ApiParam(value = "New fetch URL", required = true) JsonObject url) throws GitConnectorException, GitAPIException, URISyntaxException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		String newurl = url.get("url").getAsString();
		processor.setFetchUrl(workspace, project, newurl);
		JsonObject res = new JsonObject();
		res.addProperty("status", "success");
		res.addProperty("url", newurl);
		return Response.ok().entity(res).type(ContentTypeHelper.APPLICATION_JSON).build();
	}

	/**
	 * Update remote origin push URL.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param url the new fetch URL
	 * @return the response
	 * @throws GitConnectorException Git Connector Exception
	 * @throws GitAPIException Git API Exception
	 * @throws URISyntaxException URL with wrong format provided
	 */
	@POST
	@Path("/{project}/push-url")
	@Produces("application/json")
	@ApiOperation("Set origin push URL")
	@ApiResponses({ @ApiResponse(code = 200, message = "Git File Diff") })
	public Response setPushUrl(@PathParam("workspace") String workspace, @PathParam("project") String project, @ApiParam(value = "New push URL", required = true) JsonObject url) throws GitConnectorException, GitAPIException, URISyntaxException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		String newurl = url.get("url").getAsString();
		processor.setPushUrl(workspace, project, newurl);
		JsonObject res = new JsonObject();
		res.addProperty("status", "success");
		res.addProperty("url", newurl);
		return Response.ok().entity(res).type(ContentTypeHelper.APPLICATION_JSON).build();
	}

	/**
	 * Get file diff.
	 * 
	 * @param workspace the workspace
	 * @param repositoryName the project
	 * @param path the path
	 * @return the response
	 * @throws GitConnectorException in case of exception
	 */
	@GET
	@Path("/{repositoryName}/diff")
	@Produces("application/json")
	@ApiOperation("Get File Diff")
	@ApiResponses({ @ApiResponse(code = 200, message = "Git File Diff") })
	public Response getFileDiff(@ApiParam(value = "Name of the Workspace", required = true) @PathParam("workspace") String workspace,
			@ApiParam(value = "Name of the Project", required = true) @PathParam("repositoryName") String repositoryName, @QueryParam("path") String path)
			throws GitConnectorException {
		if (path == null || "".equals(path)) {
			return Response.status(Status.NOT_FOUND).build();
		}
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		GitDiffModel diff = processor.getFileDiff(workspace, repositoryName, path);
		return Response.ok().entity(GsonHelper.GSON.toJson(diff)).type(ContentTypeHelper.APPLICATION_JSON).build();
	}

	/**
	 * Get file diff.
	 * 
	 * @param workspace the workspace
	 * @param project the project
	 * @param path the path
	 * @return the response
	 * @throws GitConnectorException in case of exception
	 */
	@GET
	@Path("/{project}/history")
	@Produces("application/json")
	@ApiOperation("Get History")
	@ApiResponses({ @ApiResponse(code = 200, message = "Git File Diff") })
	public Response getHistory(@ApiParam(value = "Name of the Workspace", required = true) @PathParam("workspace") String workspace,
			@ApiParam(value = "Name of the Project", required = true) @PathParam("project") String project, @QueryParam("path") String path)
			throws GitConnectorException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		List<GitCommitInfo> history = processor.getHistory(workspace, project, path);
		return Response.ok().entity(GsonHelper.GSON.toJson(history)).type(ContentTypeHelper.APPLICATION_JSON).build();
	}

	/**
	 * Push project.
	 *
	 * @param workspace the workspace
	 * @param repository the project
	 * @return the response
	 * @throws GitConnectorException in case of exception
	 */
	@POST
	@Path("/{repository}/import")
	@Produces("application/json")
	@ApiOperation("Import Git Repository Project(s)")
	@ApiResponses({ @ApiResponse(code = 200, message = "Git Project Pushed") })
	public Response importProjects(@ApiParam(value = "Name of the Workspace", required = true) @PathParam("workspace") String workspace,
			@ApiParam(value = "Name of the Repository", required = true) @PathParam("repository") String repository) throws GitConnectorException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		processor.importProjects(workspace, repository);
		return Response.ok().build();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.IRestService#getType()
	 */
	@Override
	public Class<? extends IRestService> getType() {
		return GitRestService.class;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.AbstractRestService#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return logger;
	}

}
