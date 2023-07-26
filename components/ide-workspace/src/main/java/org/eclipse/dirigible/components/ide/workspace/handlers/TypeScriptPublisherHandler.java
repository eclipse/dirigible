package org.eclipse.dirigible.components.ide.workspace.handlers;

import org.eclipse.dirigible.components.base.publisher.PublisherHandler;
import org.eclipse.dirigible.components.ide.workspace.service.TypeScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TypeScriptPublisherHandler implements PublisherHandler {
    private final TypeScriptService typeScriptService;

    @Autowired
    public TypeScriptPublisherHandler(TypeScriptService typeScriptService) {
        this.typeScriptService = typeScriptService;
    }

    @Override
    public void afterPublish(String workspaceLocation, String registryLocation, AfterPublishMetadata metadata) {
        if (typeScriptService.shouldCompileTypeScript(metadata.projectName(), metadata.entryPath())) {
            typeScriptService.compileTypeScript(metadata.projectName(), metadata.entryPath());
        }
    }
}
