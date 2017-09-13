package org.eclipse.dirigible.runtime.transport.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.repository.api.RepositoryExportException;
import org.eclipse.dirigible.repository.api.RepositoryImportException;
import org.eclipse.dirigible.runtime.transport.processor.TransportProcessor;

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
@Path("/transport/project")
@Api(value = "IDE - Transport - Project", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
@ApiResponses({ @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden") })
public class TransportProjectRestService implements IRestService {

	@Inject
	private TransportProcessor processor;

	@POST
	@Path("{workspace}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation("Import Project from Zip")
	@ApiResponses({ @ApiResponse(code = 200, message = "Project Imported") })
	public Response importProject(@ApiParam(value = "Name of the Workspace", required = true) @PathParam("workspace") String workspace,
			@ApiParam(value = "The Zip file(s) containing the Project artifacts", required = true) @Multipart("file") List<byte[]> files) throws RepositoryImportException {
		for (byte[] file : files) {
			processor.importProject(workspace, file);
		}		
		return Response.ok().build();
	}
	
	@GET
	@Path("{workspace}/{project}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@ApiOperation("Export Project as Zip")
	@ApiResponses({ @ApiResponse(code = 200, message = "Project Exported") })
	public Response exportProject(@ApiParam(value = "Name of the Workspace", required = true) @PathParam("workspace") String workspace,
			@ApiParam(value = "Name of the Project", required = true) @PathParam("project") String project) throws RepositoryExportException {
		SimpleDateFormat pattern = new SimpleDateFormat("yyyyMMddhhmmss");
		if ("*".equals(project)) {
			byte[] zip = processor.exportWorkspace(workspace);
			return Response.ok().header("Content-Disposition",  "attachment; filename=\"" + workspace + "-" + pattern.format(new Date()) + ".zip\"").entity(zip).build();
		}
		byte[] zip = processor.exportProject(workspace, project);
		return Response.ok().header("Content-Disposition",  "attachment; filename=\"" + project + "-" + pattern.format(new Date()) + ".zip\"").entity(zip).build();
	}

	@Override
	public Class<? extends IRestService> getType() {
		return TransportProjectRestService.class;
	}

}
