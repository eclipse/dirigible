package org.eclipse.dirigible.runtime.transport.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.service.AbstractRestService;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.repository.api.RepositoryExportException;
import org.eclipse.dirigible.repository.api.RepositoryImportException;
import org.eclipse.dirigible.runtime.transport.processor.TransportProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 * Front facing REST service serving the transport requests for projects
 */
@Singleton
@Path("/transport")
@Api(value = "IDE - Transport - Project", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
@ApiResponses({ @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden") })
public class TransportProjectRestService extends AbstractRestService implements IRestService {
	
	private static final Logger logger = LoggerFactory.getLogger(TransportProjectRestService.class);

	@Inject
	private TransportProcessor processor;
	
	@Context
	private HttpServletResponse response;

	@POST
	@Path("/project/{workspace}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation("Import Project from Zip")
	@ApiResponses({ @ApiResponse(code = 200, message = "Project Imported") })
	public Response importProject(@ApiParam(value = "Name of the Workspace", required = true) @PathParam("workspace") String workspace,
			@ApiParam(value = "The Zip file(s) containing the Project artifacts", required = true) @Multipart("file") List<byte[]> files) throws RepositoryImportException {
		String user = UserFacade.getName();
		if (user == null) {
			sendErrorForbidden(response, NO_LOGGED_IN_USER);
			return Response.status(Status.FORBIDDEN).build();
		}
		
		for (byte[] file : files) {
			processor.importProject(workspace, file);
		}		
		return Response.ok().build();
	}
	
	@GET
	@Path("/project/{workspace}/{project}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@ApiOperation("Export Project as Zip")
	@ApiResponses({ @ApiResponse(code = 200, message = "Project Exported") })
	public Response exportProject(@ApiParam(value = "Name of the Workspace", required = true) @PathParam("workspace") String workspace,
			@ApiParam(value = "Name of the Project", required = true) @PathParam("project") String project) throws RepositoryExportException {
		String user = UserFacade.getName();
		if (user == null) {
			sendErrorForbidden(response, NO_LOGGED_IN_USER);
			return Response.status(Status.FORBIDDEN).build();
		}
		
		SimpleDateFormat pattern = getDateFormat();
		if ("*".equals(project)) {
			byte[] zip = processor.exportWorkspace(workspace);
			return Response.ok().header("Content-Disposition",  "attachment; filename=\"" + workspace + "-" + pattern.format(new Date()) + ".zip\"").entity(zip).build();
		}
		byte[] zip = processor.exportProject(workspace, project);
		return Response.ok().header("Content-Disposition",  "attachment; filename=\"" + project + "-" + pattern.format(new Date()) + ".zip\"").entity(zip).build();
	}
	
	@POST
	@Path("/snapshot")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation("Import Snapshot from Zip")
	@ApiResponses({ @ApiResponse(code = 200, message = "Snapshot Imported") })
	public Response importSnapshot(
			@ApiParam(value = "The Zip file(s) containing the Snapshot contents", required = true) @Multipart("file") List<byte[]> files) throws RepositoryImportException {
		String user = UserFacade.getName();
		if (user == null) {
			sendErrorForbidden(response, NO_LOGGED_IN_USER);
			return Response.status(Status.FORBIDDEN).build();
		}
		
		for (byte[] file : files) {
			processor.importSnapshot(file);
		}		
		return Response.ok().build();
	}
	
	@GET
	@Path("/snapshot")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@ApiOperation("Export Snapshot as Zip")
	@ApiResponses({ @ApiResponse(code = 200, message = "Snapshot Exported") })
	public Response exportSnapshot() throws RepositoryExportException {
		String user = UserFacade.getName();
		if (user == null) {
			sendErrorForbidden(response, NO_LOGGED_IN_USER);
			return Response.status(Status.FORBIDDEN).build();
		}
		
		SimpleDateFormat pattern = getDateFormat();
		byte[] zip = processor.exportSnapshot();
		return Response.ok().header("Content-Disposition",  "attachment; filename=\"repository-snapshot-" + pattern.format(new Date()) + ".zip\"").entity(zip).build();
	}

	private SimpleDateFormat getDateFormat() {
		return new SimpleDateFormat("yyyyMMddhhmmss");
	}

	@Override
	public Class<? extends IRestService> getType() {
		return TransportProjectRestService.class;
	}
	
	@Override
	protected Logger getLogger() {
		return logger;
	}

}
