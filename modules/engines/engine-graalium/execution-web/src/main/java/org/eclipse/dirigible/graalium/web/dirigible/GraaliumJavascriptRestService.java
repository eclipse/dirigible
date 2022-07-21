package org.eclipse.dirigible.graalium.web.dirigible;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/js")
public class GraaliumJavascriptRestService {
    private static final String HTTP_PATH_MATCHER = "/{projectName}/{projectFilePath:.*\\.js|.*\\.mjs}";
    private static final String HTTP_PATH_WITH_PARAM_MATCHER = "/{projectName}/{projectFilePath:.*\\.js|.*\\.mjs}/{projectFilePathParam}";
    private final GraaliumJavascriptHandler requestHandler = new GraaliumJavascriptHandler();

    @GET
    @Path(HTTP_PATH_MATCHER)
    public Response get(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath
    ) {
        return executeJavaScript(projectName, projectFilePath);
    }

    @GET
    @Path(HTTP_PATH_WITH_PARAM_MATCHER)
    public Response get(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @PathParam("projectFilePathParam") String projectFilePathParam
    ) {
        return executeJavaScript(projectName, projectFilePath, projectFilePathParam);
    }

    @POST
    @Path(HTTP_PATH_MATCHER)
    public Response post(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath
    ) {
        return executeJavaScript(projectName, projectFilePath);
    }

    @POST
    @Path(HTTP_PATH_WITH_PARAM_MATCHER)
    public Response post(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @PathParam("projectFilePathParam") String projectFilePathParam
    ) {
        return executeJavaScript(projectName, projectFilePath, projectFilePathParam);
    }

    @PUT
    @Path(HTTP_PATH_MATCHER)
    public Response put(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath
    ) {
        return executeJavaScript(projectName, projectFilePath);
    }

    @PUT
    @Path(HTTP_PATH_WITH_PARAM_MATCHER)
    public Response put(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @PathParam("projectFilePathParam") String projectFilePathParam
    ) {
        return executeJavaScript(projectName, projectFilePath, projectFilePathParam);
    }

    @PATCH
    @Path(HTTP_PATH_MATCHER)
    public Response patch(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath
    ) {
        return executeJavaScript(projectName, projectFilePath);
    }

    @PATCH
    @Path(HTTP_PATH_WITH_PARAM_MATCHER)
    public Response patch(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @PathParam("projectFilePathParam") String projectFilePathParam
    ) {
        return executeJavaScript(projectName, projectFilePath, projectFilePathParam);
    }

    @DELETE
    @Path(HTTP_PATH_MATCHER)
    public Response delete(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath
    ) {
        return executeJavaScript(projectName, projectFilePath);
    }

    @DELETE
    @Path(HTTP_PATH_WITH_PARAM_MATCHER)
    public Response delete(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @PathParam("projectFilePathParam") String projectFilePathParam
    ) {
        return executeJavaScript(projectName, projectFilePath, projectFilePathParam);
    }

    @HEAD
    @Path(HTTP_PATH_MATCHER)
    public Response head(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath
    ) {
        return executeJavaScript(projectName, projectFilePath);
    }

    @HEAD
    @Path(HTTP_PATH_WITH_PARAM_MATCHER)
    public Response head(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @PathParam("projectFilePathParam") String projectFilePathParam
    ) {
        return executeJavaScript(projectName, projectFilePath, projectFilePathParam);
    }

    private Response executeJavaScript(String projectName, String projectFilePath) {
        return executeJavaScript(projectName, projectFilePath, "");
    }

    private Response executeJavaScript(String projectName, String projectFilePath, String projectFilePathParam) {
        requestHandler.handleJSRequest(projectName, projectFilePath, projectFilePathParam);
        return Response.ok().build();
    }
}
