package org.eclipse.dirigible.graalium.web;

import org.eclipse.dirigible.engine.js.service.JavascriptHandler;
import org.eclipse.dirigible.graalium.core.dirigible.DirigibleJavascriptCodeRunner;
import org.eclipse.dirigible.graalium.core.dirigible.modules.DirigibleSourceProvider;
import org.eclipse.dirigible.api.v3.http.HttpRequestFacade;

import java.io.IOException;
import java.nio.file.Path;

/**
 * The Class GraaliumJavascriptHandler.
 */
public class GraaliumJavascriptHandler implements JavascriptHandler {

    /** The dirigible source provider. */
    private final DirigibleSourceProvider dirigibleSourceProvider = new DirigibleSourceProvider();

    /**
     * Handle request.
     *
     * @param projectName the project name
     * @param projectFilePath the project file path
     * @param projectFilePathParam the project file path param
     * @param debug the debug
     */
    @Override
    public void handleRequest(String projectName, String projectFilePath, String projectFilePathParam, boolean debug) {
        try {
            if (HttpRequestFacade.isValid()) {
                HttpRequestFacade.setAttribute(HttpRequestFacade.ATTRIBUTE_REST_RESOURCE_PATH, projectFilePathParam);
            }

            String maybeJSCode = dirigibleSourceProvider.getSource(projectName, projectFilePath);
            if (maybeJSCode == null) {
                throw new IOException("JS source for project name '" + projectName + "' and file name '" + projectFilePath + " could not be found");
            }

            Path jsCodePath = dirigibleSourceProvider.getAbsoluteSourcePath(projectName, projectFilePath);
            new DirigibleJavascriptCodeRunner(debug).run(jsCodePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
