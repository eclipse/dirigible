package org.eclipse.dirigible.graalium.web;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.eclipse.dirigible.commons.api.service.AbstractRestService;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/graalium")
public class GraaliumJavascriptRestService extends AbstractRestService implements IRestService {
	
	private static final Logger logger = LoggerFactory.getLogger(GraaliumJavascriptRestService.class.getCanonicalName());
    private static final String HTTP_PATH_MATCHER = "/{projectName}/{projectFilePath:.*\\.js|.*\\.mjs}";
    private static final String HTTP_PATH_WITH_PARAM_MATCHER = "/{projectName}/{projectFilePath:.*\\.js|.*\\.mjs}/{projectFilePathParam}";
    private final GraaliumJavascriptHandler requestHandler = new GraaliumJavascriptHandler();

    @GET
    @Path(HTTP_PATH_MATCHER)
    public Response get(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @QueryParam("debug") String debug
    ) {
        return executeJavaScript(projectName, projectFilePath, debug != null);
    }

    @GET
    @Path(HTTP_PATH_WITH_PARAM_MATCHER)
    public Response get(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @PathParam("projectFilePathParam") String projectFilePathParam,
            @QueryParam("debug") String debug
    ) {
        return executeJavaScript(projectName, projectFilePath, projectFilePathParam, debug != null);
    }

    @POST
    @Path(HTTP_PATH_MATCHER)
    public Response post(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @QueryParam("debug") String debug
    ) {
        return executeJavaScript(projectName, projectFilePath, debug != null);
    }

    @POST
    @Path(HTTP_PATH_WITH_PARAM_MATCHER)
    public Response post(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @PathParam("projectFilePathParam") String projectFilePathParam,
            @QueryParam("debug") String debug
    ) {
        return executeJavaScript(projectName, projectFilePath, projectFilePathParam, debug != null);
    }

    @PUT
    @Path(HTTP_PATH_MATCHER)
    public Response put(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @QueryParam("debug") String debug
    ) {
        return executeJavaScript(projectName, projectFilePath, debug != null);
    }

    @PUT
    @Path(HTTP_PATH_WITH_PARAM_MATCHER)
    public Response put(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @PathParam("projectFilePathParam") String projectFilePathParam,
            @QueryParam("debug") String debug
    ) {
        return executeJavaScript(projectName, projectFilePath, projectFilePathParam, debug != null);
    }

    @PATCH
    @Path(HTTP_PATH_MATCHER)
    public Response patch(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @QueryParam("debug") String debug
    ) {
        return executeJavaScript(projectName, projectFilePath, debug != null);
    }

    @PATCH
    @Path(HTTP_PATH_WITH_PARAM_MATCHER)
    public Response patch(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @PathParam("projectFilePathParam") String projectFilePathParam,
            @QueryParam("debug") String debug
    ) {
        return executeJavaScript(projectName, projectFilePath, projectFilePathParam, debug != null);
    }

    @DELETE
    @Path(HTTP_PATH_MATCHER)
    public Response delete(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @QueryParam("debug") String debug
    ) {
        return executeJavaScript(projectName, projectFilePath, debug != null);
    }

    @DELETE
    @Path(HTTP_PATH_WITH_PARAM_MATCHER)
    public Response delete(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @PathParam("projectFilePathParam") String projectFilePathParam,
            @QueryParam("debug") String debug
    ) {
        return executeJavaScript(projectName, projectFilePath, projectFilePathParam, debug != null);
    }

    @HEAD
    @Path(HTTP_PATH_MATCHER)
    public Response head(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @QueryParam("debug") String debug
    ) {
        return executeJavaScript(projectName, projectFilePath, debug != null);
    }

    @HEAD
    @Path(HTTP_PATH_WITH_PARAM_MATCHER)
    public Response head(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @PathParam("projectFilePathParam") String projectFilePathParam,
            @QueryParam("debug") String debug
    ) {
        return executeJavaScript(projectName, projectFilePath, projectFilePathParam, debug != null);
    }

    private Response executeJavaScript(String projectName, String projectFilePath, boolean debug) {
        return executeJavaScript(projectName, projectFilePath, "", debug);
    }

    private Response executeJavaScript(String projectName, String projectFilePath, String projectFilePathParam, boolean debug) {
        requestHandler.handleRequest(projectName, projectFilePath, projectFilePathParam, debug);
        return Response.ok().build();
    }

	@Override
	public Class<? extends IRestService> getType() {
		return GraaliumJavascriptRestService.class;
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}
}
