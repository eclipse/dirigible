package org.eclipse.dirigible.graalium.web.dirigible;

import org.eclipse.dirigible.engine.js.service.JSWebHandler;
import org.eclipse.dirigible.graalium.core.dirigible.DirigibleJSCodeRunner;
import org.eclipse.dirigible.graalium.core.dirigible.modules.DirigibleSourceProvider;
import org.eclipse.dirigible.api.v3.http.HttpRequestFacade;

import java.io.IOException;
import java.nio.file.Path;

public class GraaliumJSWebHandler implements JSWebHandler {

    private final DirigibleSourceProvider dirigibleSourceProvider = new DirigibleSourceProvider();

    @Override
    public void handleJSRequest(String projectName, String projectFilePath, String projectFilePathParam) {
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
