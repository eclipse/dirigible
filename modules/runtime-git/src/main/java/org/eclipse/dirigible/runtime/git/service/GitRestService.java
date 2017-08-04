package org.eclipse.dirigible.runtime.git.service;

import java.util.Arrays;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.core.git.GitConnectorException;
import org.eclipse.dirigible.runtime.git.model.GitCloneModel;
import org.eclipse.dirigible.runtime.git.model.GitPullModel;
import org.eclipse.dirigible.runtime.git.model.GitPushModel;
import org.eclipse.dirigible.runtime.git.model.GitResetModel;
import org.eclipse.dirigible.runtime.git.model.GitShareModel;
import org.eclipse.dirigible.runtime.git.model.GitUpdateDepenciesModel;
import org.eclipse.dirigible.runtime.git.processor.GitProcessor;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;

/**
 * Front facing REST service serving the raw repository content
 */
@Singleton
@Path("/core/git/{workspace}")
@Api(value = "Core - Git", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
public class GitRestService implements IRestService {

	@Inject
	private GitProcessor processor;

	@POST
	@Path("/clone")
	@Produces("application/json")
	// @ApiOperation(value = "List all the databases types")
	// @ApiResponses(value = { @ApiResponse(code = 200, message = "List of Databases Types", response = String.class,
	// responseContainer = "List"),
	// @ApiResponse(code = 401, message = "Unauthorized") })
	public Response cloneRepository(@PathParam("workspace") String workspace, GitCloneModel model) throws GitConnectorException {
		processor.clone(workspace, model);
		// {
		// "workspace": "project1",
		// "projects": ["api", "k8s", "ide"]
		// }
		return Response.ok().build();
	}

	@POST
	@Path("/pull")
	@Produces("application/json")
	public Response pullProjects(@PathParam("workspace") String workspace, GitPullModel model) {
		processor.pull(workspace, model);
		return Response.ok().build();
	}

	@POST
	@Path("/pull/{project}")
	@Produces("application/json")
	public Response pullProject(@PathParam("workspace") String workspace, @PathParam("project") String project, GitPullModel model) {
		model.setProjects(Arrays.asList(project));
		processor.pull(workspace, model);
		return Response.ok().build();
	}

	@POST
	@Path("/push")
	@Produces("application/json")
	public Response pushProjects(@PathParam("workspace") String workspace, GitPushModel model) {
		processor.push(workspace, model);
		return Response.ok().build();
	}

	@POST
	@Path("/push/{project}")
	@Produces("application/json")
	public Response pushProject(@PathParam("workspace") String workspace, @PathParam("project") String project, GitPushModel model) {
		model.setProjects(Arrays.asList(project));
		processor.push(workspace, model);
		return Response.ok().build();
	}

	@POST
	@Path("/reset")
	@Produces("application/json")
	public Response resetProjects(@PathParam("workspace") String workspace, GitResetModel model) {
		processor.reset(workspace, model);
		return Response.ok().build();
	}

	@POST
	@Path("/reset/{project}")
	@Produces("application/json")
	public Response resetProject(@PathParam("workspace") String workspace, @PathParam("project") String project, GitResetModel model) {
		model.setProjects(Arrays.asList(project));
		processor.reset(workspace, model);
		return Response.ok().build();
	}

	@POST
	@Path("/share/{project}")
	@Produces("application/json")
	public Response shareProject(@PathParam("workspace") String workspace, @PathParam("project") String project, GitShareModel model) {
		model.setProject(project);
		processor.share(workspace, model);
		return Response.ok().build();
	}

	@POST
	@Path("/uppdate-dependencies")
	@Produces("application/json")
	public Response updateProjectsDependencies(@PathParam("workspace") String workspace, GitUpdateDepenciesModel model) throws GitConnectorException {
		processor.updateDependencies(workspace, model);
		return Response.ok().build();
	}

	@POST
	@Path("/uppdate-dependencies/{project}")
	@Produces("application/json")
	public Response updateProjectDependencies(@PathParam("workspace") String workspace, @PathParam("project") String project,
			GitUpdateDepenciesModel model) throws GitConnectorException {
		model.setProjects(Arrays.asList(project));
		processor.updateDependencies(workspace, model);
		return Response.ok().build();
	}

	@Override
	public Class<? extends IRestService> getType() {
		return GitRestService.class;
	}

}
