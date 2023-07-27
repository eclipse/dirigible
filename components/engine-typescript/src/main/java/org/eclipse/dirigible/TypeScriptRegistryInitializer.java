package org.eclipse.dirigible;

import org.eclipse.dirigible.components.base.initializer.Initializer;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

@Component
public class TypeScriptRegistryInitializer implements Initializer {

    private final IRepository repository;
    private final TypeScriptService typeScriptService;

    @Autowired
    public TypeScriptRegistryInitializer(
            IRepository repository,
            TypeScriptService typeScriptService
    ) {
        this.repository = repository;
        this.typeScriptService = typeScriptService;
    }

    @Override
    public void initialize() {
        onEachRegistryProject(this::maybeCompileRegistryProject);
    }

    private void maybeCompileRegistryProject(Path projectPath) {
        try {
            File projectDir = projectPath.toFile();
            if (typeScriptService.shouldCompileTypeScript(projectDir)) {
                typeScriptService.compileTypeScript(projectDir);
            }
        } catch (Exception e) {
            throw new TypeScriptException("Could not compile TypeScript in: " + projectPath, e);
        }
    }

    private void onEachRegistryProject(Consumer<Path> callback) {
        var registryPath = registryPath();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(registryPath)) {
            for (Path projectPath : directoryStream) {
                callback.accept(projectPath);
            }
        } catch (IOException e) {
            throw new TypeScriptException("Error while reading registry projects", e);
        }
    }

    private Path registryPath() {
        return Path.of(repository.getInternalResourcePath(IRepositoryStructure.PATH_REGISTRY_PUBLIC));
    }
}
