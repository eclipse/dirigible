package org.eclipse.dirigible.components.ide.workspace.domain;

import org.eclipse.dirigible.components.api.security.UserFacade;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TypeScriptFile {

    private static final Pattern IMPORT_STATEMENTS_PATTERN = Pattern.compile("(?:import|export)\\s+\\{.*\\}\\s+from\\s+['\"](.*)['\"];*");

    private final IRepository repository;
    private final String workspace;
    private final String project;
    private final String filePath;
    private final String sourceCode;
    private final List<String> importedFilesNames;

    public TypeScriptFile(IRepository repository, String workspace, String project, String filePath) {
        var user = UserFacade.getName();
        this.repository = repository;
        this.workspace = workspace;
        this.project = project;
        this.filePath = filePath;
        this.sourceCode = getSourceCode(user, workspace, project, filePath);
        this.importedFilesNames = getImportedFilesNames(user, workspace, project, filePath, sourceCode);
    }

    private String getSourceCode(String user, String workspace, String project, String filePath) {
        var repositoryPath = generateWorkspaceProjectFilePath(user, workspace, project, filePath);
        var resource = repository.getResource(repositoryPath);
        var content = new String(resource.getContent(), StandardCharsets.UTF_8);
        return content;
    }

    private List<String> getImportedFilesNames(String user, String workspace, String project, String filePath, String sourceCode) {
        var fileRepositoryPath = Path.of(repository.getInternalResourcePath(generateWorkspaceProjectFilePath(user, workspace, project, filePath)));
        var projectRepositoryPath = Path.of(repository.getInternalResourcePath(generateUserRepositoryPath(user)));
        var fileRepositoryPathParentDir = fileRepositoryPath.getParent();

        var allImports = new HashSet<String>();

        Matcher importStatementsMatcher = IMPORT_STATEMENTS_PATTERN.matcher(sourceCode);
        while (importStatementsMatcher.find()) {
            var fromStatement = importStatementsMatcher.group(1);
            allImports.add(fromStatement);
        }

        var relativeImports = allImports.stream()
                .filter(x -> x.startsWith("./") || x.startsWith("../"))
                .map(x -> {
                    try {
                        var importedModule = x.endsWith(".ts") ? x : x + ".ts";
                        return fileRepositoryPathParentDir
                                .resolve(Path.of(importedModule))
                                .toRealPath();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(x -> "/" + projectRepositoryPath.relativize(x))
                .collect(Collectors.toList());

        return relativeImports;
    }

    private String generateWorkspaceProjectFilePath(String user, String workspace, String project, String path) {
        return IRepositoryStructure.PATH_USERS +
                IRepositoryStructure.SEPARATOR + user +
                IRepositoryStructure.SEPARATOR + workspace +
                IRepositoryStructure.SEPARATOR + project +
                IRepositoryStructure.SEPARATOR + path;
    }

    private String generateUserRepositoryPath(String user) {
        return IRepositoryStructure.PATH_USERS + IRepositoryStructure.SEPARATOR + user;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public List<String> getImportedFilesNames() {
        return importedFilesNames;
    }

    public String getWorkspace() {
        return workspace;
    }

    public String getProject() {
        return project;
    }

    public String getFilePath() {
        return filePath;
    }
}