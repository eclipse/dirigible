package org.eclipse.dirigible.runtime.databases.service;

import static java.text.MessageFormat.format;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
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

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.runtime.databases.processor.DatabaseProcessor;

/**
 * Front facing REST service serving the raw repository content
 */
@Singleton
@Path("/core/databases")
public class DatabaseRestService implements IRestService {
	
	@Inject
	private DatabaseProcessor processor;
	
	@Override
	public Class<? extends IRestService> getType() {
		return DatabaseRestService.class;
	}
	
	@GET
	@Path("")
	@Produces("application/json")
	public Response listDatabaseTypes(@Context HttpServletRequest request) {
		List<String> databaseTypes = processor.getDatabaseTypes();
		return Response.ok().entity(GsonHelper.GSON.toJson(databaseTypes)).build();
	}

	
	
	@GET
	@Path("{type}")
	@Produces("application/json")
	public Response listDatabases(@PathParam("type") String type, @Context HttpServletRequest request) {
		Set<String> list = processor.getDataSources(type);
		if (list == null) {
			String error = format("Database Type {0} not known.", type);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		return Response.ok().entity(GsonHelper.GSON.toJson(list)).build();
		
	}

	@GET
	@Path("{type}/{name}")
	@Produces("application/json")
	public Response listArtefacts(@PathParam("type") String type, @PathParam("name") String name, @Context HttpServletRequest request) throws SQLException {
		DataSource dataSource = processor.getDataSource(type, name);
		if (dataSource == null) {
			String error = format("DataSource {0} of Type {1} not known.", name, type);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		String metadata = processor.getMetadataAsJson(type, name);
		return Response.ok().entity(metadata).build();
		
	}
	
	@POST
	@Path("{type}/{name}/query")
	@Produces("text/plain")
	public Response executeQuery(@PathParam("type") String type, @PathParam("name") String name, byte[] sql, @Context HttpServletRequest request) {
		String user = request.getRemoteUser();
		if (user == null) {
			return Response.status(Status.FORBIDDEN).build();
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
	public Response executeUpdate(@PathParam("type") String type, @PathParam("name") String name, byte[] sql, @Context HttpServletRequest request) {
		String user = request.getRemoteUser();
		if (user == null) {
			return Response.status(Status.FORBIDDEN).build();
		}
		
		if (!processor.existsDatabase(type, name)) {
			String error = format("Datasource {0} does not exist as {1}.", name, type);
			return Response.status(Status.NOT_FOUND).entity(error).build();
		}
		
		String result = processor.executeUpdate(type, name, new String(sql));
		return Response.ok().entity(result).build();
	}
	
	
	
	
}
