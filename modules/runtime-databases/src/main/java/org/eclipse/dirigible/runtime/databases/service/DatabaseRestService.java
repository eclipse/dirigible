package org.eclipse.dirigible.runtime.databases.service;

import static java.text.MessageFormat.format;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.database.api.metadata.DatabaseMetadata;
import org.eclipse.dirigible.databases.helpers.DatabaseMetadataHelper;
import org.eclipse.dirigible.runtime.databases.processor.DatabaseProcessor;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 * Front facing REST service serving the raw repository content
 */
@Singleton
@Path("/core/databases")
@Api(value = "Core - Databases", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
public class DatabaseRestService implements IRestService {

	@Inject
	private DatabaseProcessor processor;

	@GET
	@Path("")
	@Produces("application/json")
	@ApiOperation(value = "List all the databases types")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "List of Databases Types", response = String.class, responseContainer = "List"),
			@ApiResponse(code = 401, message = "Unauthorized") })
	public Response listDatabaseTypes(@Context HttpServletRequest request) {
		List<String> databaseTypes = processor.getDatabaseTypes();
		return Response.ok().entity(databaseTypes).build();
	}

	@GET
	@Path("{type}")
	@Produces("application/json")
	@ApiOperation(value = "Returns all the available data sources for the given database {type}")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "List of Data Sources", response = String.class, responseContainer = "List"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Data Sources for the requested database {type} does not exist") })
	public Response listDataSources(@PathParam("type") String type, @Context HttpServletRequest request) {
		Set<String> list = processor.getDataSources(type);
		if (list == null) {
			String error = format("Database Type {0} not known.", type);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		return Response.ok().entity(list).build();

	}

	@GET
	@Path("{type}/{name}")
	@Produces("application/json")
	@ApiOperation(value = "Returns the metadata of the given data source with {name} and {type}")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Database Metadata", response = DatabaseMetadata.class),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Database Metadata for the requested database {type} does not exist") })
	public Response listArtifacts(@PathParam("type") String type, @PathParam("name") String name, @Context HttpServletRequest request)
			throws SQLException {
		DataSource dataSource = processor.getDataSource(type, name);
		if (dataSource == null) {
			String error = format("DataSource {0} of Type {1} not known.", name, type);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		String metadata = DatabaseMetadataHelper.getMetadataAsJson(dataSource);
		return Response.ok().entity(metadata).build();

	}

	@POST
	@Path("{type}/{name}/query")
	@Produces("text/plain")
	@ApiOperation(value = "Executes a query operation on the datasource {name} and {type} and returns the result in a tabular format")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Datasource updated successfully", response = String.class),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Datasource with {name} for the requested database {type} does not exist") })
	public Response executeQuery(@PathParam("type") String type, @PathParam("name") String name, byte[] sql, @Context HttpServletRequest request) {
		String user = request.getRemoteUser();
		if (user == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}

		if (!processor.existsDatabase(type, name)) {
			String error = format("Datasource {0} does not exist as {1}.", name, type);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}

		String result = processor.executeQuery(type, name, new String(sql));
		return Response.ok().entity(result).build();
	}

	@POST
	@Path("{type}/{name}/update")
	@Produces("text/plain")
	@ApiOperation(value = "Executes an update operation on the datasource {name} and {type} and returns the result in a tabular format")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Datasource updated successfully", response = String.class),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Datasource with {name} for the requested database {type} does not exist") })
	public Response executeUpdate(@PathParam("type") String type, @PathParam("name") String name, byte[] sql, @Context HttpServletRequest request) {
		String user = request.getRemoteUser();
		if (user == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}

		if (!processor.existsDatabase(type, name)) {
			String error = format("Datasource {0} does not exist as {1}.", name, type);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}

		String result = processor.executeUpdate(type, name, new String(sql));
		return Response.ok().entity(result).build();
	}

	@Override
	public Class<? extends IRestService> getType() {
		return DatabaseRestService.class;
	}

}
