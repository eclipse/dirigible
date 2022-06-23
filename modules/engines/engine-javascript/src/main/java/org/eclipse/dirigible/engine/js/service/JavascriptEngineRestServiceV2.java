package org.eclipse.dirigible.engine.js.service;

import org.eclipse.dirigible.afterburner.core.CodeRunner;
import org.eclipse.dirigible.api.v3.http.HttpRequestFacade;
import org.eclipse.dirigible.commons.api.service.AbstractRestService;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.engine.js.graalium.platform.DirigibleCodeRunnerFactory;
import org.eclipse.dirigible.engine.js.graalium.platform.internal.modules.DirigibleSourceProvider;
import org.eclipse.dirigible.repository.api.IRepository;
import org.graalvm.polyglot.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/js")
public class JavascriptEngineRestServiceV2 extends AbstractRestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(JavascriptEngineRestServiceV2.class.getCanonicalName());
    private static final String HTTP_PATH_MATCHER = "/{projectName}/{projectFilePath:.*\\.js|.*\\.mjs}";
    private static final String HTTP_PATH_WITH_PARAM_MATCHER = "/{projectName}/{projectFilePath:.*\\.js|.*\\.mjs}/{projectFilePathParam}";
    private final DirigibleSourceProvider dirigibleSourceProvider = new DirigibleSourceProvider();

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
        try {
            if (HttpRequestFacade.isValid()) {
                HttpRequestFacade.setAttribute(HttpRequestFacade.ATTRIBUTE_REST_RESOURCE_PATH, projectFilePathParam);
            }

            String maybeJSCode = dirigibleSourceProvider.getSource(projectName, projectFilePath);
            if (maybeJSCode == null) {
                return Response.status(404).build();
            }

            Source jsSource = Source.newBuilder("js", maybeJSCode, projectFilePath).build();

            if ("test".equals(projectName)) {
                CodeRunnerPool.CodeRunnerPoolable codeRunnerPoolable = CodeRunnerPool.get();
                try {
                    CodeRunner codeRunner = codeRunnerPoolable.getCodeRunner();
                    codeRunner.run(jsSource);
                } finally {
                    if (codeRunnerPoolable != null) {
                        codeRunnerPoolable.release();
                    }
                }
            } else {
                CodeRunner codeRunner = createJavaScriptCodeRunner(projectName);
                codeRunner.run(jsSource);
            }

            return Response.ok().build();
        } catch (Throwable e) {
            String message = e.getMessage();
            LOGGER.error(message, e);
            createErrorResponseInternalServerError(message);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    private static CodeRunner createJavaScriptCodeRunner(String projectName) {
        IRepository repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);
        java.nio.file.Path projectPath = java.nio.file.Path.of(projectName);
        java.nio.file.Path repositoryRootPath = java.nio.file.Path.of(repository.getRepositoryPath());
        java.nio.file.Path projectDirectoryPath = repositoryRootPath.resolve("registry/public").resolve(projectPath);

        return DirigibleCodeRunnerFactory.createDirigibleJSCodeRunner(projectDirectoryPath);
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    @Override
    public Class<? extends IRestService> getType() {
        return JavascriptEngineRestServiceV2.class;
    }
}
