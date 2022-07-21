package org.eclipse.dirigible.graalium.web;

import org.eclipse.dirigible.engine.js.service.JavascriptHandler;
import org.eclipse.dirigible.graalium.core.dirigible.DirigibleJSCodeRunner;
import org.eclipse.dirigible.graalium.core.dirigible.modules.DirigibleSourceProvider;
import org.eclipse.dirigible.api.v3.http.HttpRequestFacade;

import java.io.IOException;
import java.nio.file.Path;

public class GraaliumJavascriptHandler implements JavascriptHandler {

    private final DirigibleSourceProvider dirigibleSourceProvider = new DirigibleSourceProvider();

    @Override
    public void handleRequest(String projectName, String projectFilePath, String projectFilePathParam) {
        try {
            if (HttpRequestFacade.isValid()) {
                HttpRequestFacade.setAttribute(HttpRequestFacade.ATTRIBUTE_REST_RESOURCE_PATH, projectFilePathParam);
            }

            String maybeJSCode = dirigibleSourceProvider.getSource(projectName, projectFilePath);
            if (maybeJSCode == null) {
                throw new IOException("JS source for project name '" + projectName + "' and file name '" + projectFilePath + " could not be found");
            }

            Path jsCodePath = dirigibleSourceProvider.getAbsoluteSourcePath(projectName, projectFilePath);
            new DirigibleJSCodeRunner().run(jsCodePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
