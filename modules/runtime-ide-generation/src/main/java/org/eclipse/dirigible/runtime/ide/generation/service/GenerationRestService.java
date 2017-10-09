package org.eclipse.dirigible.runtime.ide.generation.service;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
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
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.core.workspace.api.IFile;
import org.eclipse.dirigible.core.workspace.api.IFolder;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.runtime.ide.generation.processor.GenerationProcessor;
import org.eclipse.dirigible.runtime.ide.generation.processor.GenerationTemplateParameters;

import com.google.gson.Gson;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 * Front facing REST service serving the Generation content
 */
@Singleton
@Path("/ide/generation")
@RolesAllowed({ "Developer" })
@Api(value = "IDE - Generation", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
@ApiResponses({ @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden") })
public class GenerationRestService implements IRestService {

	@Inject
	private GenerationProcessor processor;

	@Override
	public Class<? extends IRestService> getType() {
		return GenerationRestService.class;
	}

	@POST
	@Path("{workspace}/{project}/{path:.*}")
	public Response generateFile(@PathParam("workspace") String workspace, @PathParam("project") String project, @PathParam("path") String path,
			GenerationTemplateParameters parameters, @Context HttpServletRequest request) throws URISyntaxException, ScriptingException, IOException {
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
		if (file.exists()) {
			String error = format("File {0} already exists in Project {1} in Workspace {2}.", path, project, workspace);
			return Response.status(Status.BAD_REQUEST).entity(error).build();
		}

		List<IFile> files = processor.generateFile(workspace, project, path, parameters);
		return Response.created(processor.getURI(workspace, project, path)).build();
	}

}
