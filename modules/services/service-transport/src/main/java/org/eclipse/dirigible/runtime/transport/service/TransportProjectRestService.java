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
package org.eclipse.dirigible.runtime.transport.service;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.DecoderException;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.api.v3.utils.UrlFacade;
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
 * Front facing REST service serving the transport requests for projects.
 */
@Path("/transport")
@Api(value = "IDE - Transport - Project", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
@ApiResponses({ @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden") })
public class TransportProjectRestService extends AbstractRestService implements IRestService {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(TransportProjectRestService.class);

	/** The Constant FILE_UPLOAD_FAILED. */
	private static final String FILE_UPLOAD_FAILED = "Upload failed.";

	/** The processor. */
	private TransportProcessor processor = new TransportProcessor();
	
	/** The response. */
	@Context
	private HttpServletResponse response;

	/**
	 * Import project in a given path.
	 *
	 * @param path the path
	 * @param files the files
	 * @return the response
	 * @throws RepositoryImportException the repository import exception
	 */
	@POST
	@Path("/project")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation("Import Project from Zip into given path")
	@ApiResponses({ @ApiResponse(code = 200, message = "Project Imported") })
	public Response importProjectInPath(@ApiParam(value = "Path to import into", required = true) @QueryParam("path") String path, //     /path/zipContent
								  @ApiParam(value = "The Zip file(s) containing the Project artifacts", required = true) @Multipart("file") List<Attachment> files) throws RepositoryImportException {
		path = URLDecoder.decode(path, StandardCharsets.UTF_8);
		try {
			return importProject(path, false, files);
		} catch(IOException e) {
			logger.error(e.getMessage(), e);
			return createErrorResponseInternalServerError(FILE_UPLOAD_FAILED);
		}

	}

	/**
	 * Import project in a given workspace.
	 *
	 * @param workspace the workspace
	 * @param files the files
	 * @return the response
	 * @throws RepositoryImportException the repository import exception
	 */
	@POST
	@Path("/project/{workspace}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation("Import Project from Zip")
	@ApiResponses({ @ApiResponse(code = 200, message = "Project Imported") })
	public Response importProjectInWorkspace(@ApiParam(value = "Name of the Workspace", required = true) @PathParam("workspace") String workspace, //     /workspaceName/zipName
			@ApiParam(value = "The Zip file(s) containing the Project artifacts", required = true) @Multipart("file") List<Attachment> files) throws RepositoryImportException {

		try {
			return importProject(workspace, true, files);
		} catch(IOException e) {
			logger.error(e.getMessage(), e);
			return createErrorResponseInternalServerError(FILE_UPLOAD_FAILED);
		}
	}

	/**
	 * Import project.
	 *
	 * @param path the path
	 * @param isPathWorkspace the is path workspace
	 * @param attachments the attachments
	 * @return the response
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private Response importProject(String path, boolean isPathWorkspace, List<Attachment> attachments) throws IOException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		for (Attachment attachment : attachments) {
			InputStream file = attachment.getDataHandler().getInputStream();
			if (isPathWorkspace) {
				processor.importProjectInWorkspace(path, file);
			} else {
				processor.importProjectInPath(path, file);
			}
		}
		return Response.ok().build();
	}

	/**
	 * Import zip to folder.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param folder Internal folder (url encoded)
	 * @param files the files
	 * @return the response
	 * @throws RepositoryExportException the repository export exception
	 * @throws UnsupportedEncodingException the repository export exception
	 * @throws DecoderException the repository export exception
	 */
	@POST
	@Path("/zipimport/{workspace}/{project}/{folder}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation("Import Zip to Folder")
	@ApiResponses({ @ApiResponse(code = 200, message = "ZIP imported") })
	public Response importZipToFolder(@ApiParam(value = "Name of the Workspace", required = true) @PathParam("workspace") String workspace,
								  @ApiParam(value = "Name of the Project", required = true) @PathParam("project") String project,
								  @ApiParam(value = "Relative path to folder (url encoded)", required = false) @PathParam("folder") String folder,
									  @ApiParam(value = "The Zip file(s) containing the Project artifacts", required = true) @Multipart("file") List<byte[]> files) throws RepositoryExportException, UnsupportedEncodingException, DecoderException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		String relativePath;
		if (folder == null || folder.isEmpty() || folder.trim().isEmpty() || folder.equals("/"))
			relativePath = "";
		else {
			UrlFacade decodedFolder = new UrlFacade();
			relativePath = decodedFolder.decode(folder, null);
		}

		TransportProcessor processor = new TransportProcessor();

		for (byte[] file : files) {
			processor.importZipToPath(workspace, project, relativePath, file, true);
		}
		return Response.ok().build();
	}

	/**
	 * Export project.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param folder Internal folder (url encoded)
	 * @return the response
	 * @throws RepositoryExportException the repository export exception
	 * @throws UnsupportedEncodingException the repository export exception
	 * @throws DecoderException the repository export exception
	 */
	@GET
	@Path("/project/{workspace}/{project}{folder:(/folder/[^/]+?)?}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@ApiOperation("Export Project as Zip")
	@ApiResponses({ @ApiResponse(code = 200, message = "Project Exported") })
	public Response exportProject(@ApiParam(value = "Name of the Workspace", required = true) @PathParam("workspace") String workspace,
			@ApiParam(value = "Name of the Project", required = true) @PathParam("project") String project,
								  @ApiParam(value = "Internal folder", required = false) @PathParam("folder") String folder) throws RepositoryExportException, UnsupportedEncodingException, DecoderException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		
		SimpleDateFormat pattern = getDateFormat();
		byte[] zip;

		if ("*".equals(project)) {
			zip = processor.exportWorkspace(workspace);
			return Response.ok().header("Content-Disposition",  "attachment; filename=\"" + workspace + "-" + pattern.format(new Date()) + ".zip\"").entity(zip).build();
		} else
		if (folder == null || folder.isEmpty() || folder.trim().isEmpty() || folder.equals("/"))
			zip = processor.exportProject(workspace, project);
		else
			zip = processor.exportFolder(workspace, project, folder);
		return Response.ok().header("Content-Disposition",  "attachment; filename=\"" + project + "-" + pattern.format(new Date()) + ".zip\"").entity(zip).build();
	}
	
	/**
	 * Import snapshot.
	 *
	 * @param files the files
	 * @return the response
	 * @throws RepositoryImportException the repository import exception
	 */
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
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		
		for (byte[] file : files) {
			processor.importSnapshot(file);
		}		
		return Response.ok().build();
	}
	
	/**
	 * Export snapshot.
	 *
	 * @return the response
	 * @throws RepositoryExportException the repository export exception
	 */
	@GET
	@Path("/snapshot")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@ApiOperation("Export Snapshot as Zip")
	@ApiResponses({ @ApiResponse(code = 200, message = "Snapshot Exported") })
	public Response exportSnapshot() throws RepositoryExportException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		
		SimpleDateFormat pattern = getDateFormat();
		byte[] zip = processor.exportSnapshot();
		return Response.ok().header("Content-Disposition",  "attachment; filename=\"repository-snapshot-" + pattern.format(new Date()) + ".zip\"").entity(zip).build();
	}

	/**
	 * Gets the date format.
	 *
	 * @return the date format
	 */
	private SimpleDateFormat getDateFormat() {
		return new SimpleDateFormat("yyyyMMddhhmmss");
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.IRestService#getType()
	 */
	@Override
	public Class<? extends IRestService> getType() {
		return TransportProjectRestService.class;
	}
	
	/**
	 * Gets the logger.
	 *
	 * @return the logger
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.AbstractRestService#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return logger;
	}

}
