package org.eclipse.dirigible.components.ide.workspace.service;

import org.apache.commons.io.FileUtils;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class TypeScriptService {

    @Autowired
    private IRepository repository;

    public boolean isTypeScriptFile(String path) {
        return path.endsWith(".ts");
    }

    boolean shouldCompileTypeScript(String projectName, String entryPath) {
        if (entryPath != null & entryPath.endsWith(".ts")) {
            return true;
        }

        var projectDir = getProjectDirFile(projectName);
        return !getTypeScriptFilesInDir(projectDir).isEmpty();
    }

    private File getProjectDirFile(String projectName) {
        var registryRelativeProjectPath = new RepositoryPath(IRepositoryStructure.PATH_REGISTRY_PUBLIC, projectName).toString();
        return new File(repository.getInternalResourcePath(registryRelativeProjectPath));
    }

    private Collection<File> getTypeScriptFilesInDir(File projectDir) {
        return FileUtils.listFiles(projectDir, new String[]{"ts"}, true);
    }

    void compileTypeScript(String projectName, String entryPath) {
        var projectDir = getProjectDirFile(projectName);

        List<String> tsFilesToCompile;
        if (entryPath != null && !entryPath.equals("")) {
            var tsFilePathString = new RepositoryPath(IRepositoryStructure.PATH_REGISTRY_PUBLIC, projectName, entryPath).toString();
            var tsFilePath = new File(repository.getInternalResourcePath(tsFilePathString)).toPath();
            tsFilesToCompile = Collections.singletonList(tsFilePath.toString());
        } else {
            tsFilesToCompile = getTypeScriptFilesInDir(projectDir)
                    .stream()
                    .map(x -> projectDir.toPath().relativize(x.toPath()).toString())
                    .collect(Collectors.toList());
        }


        var esbuildCommand = new ArrayList<String>();
        esbuildCommand.add("esbuild");
        esbuildCommand.addAll(tsFilesToCompile);
        esbuildCommand.add("--outdir=.");
        esbuildCommand.add("--out-extension:.js=.mjs");

        var processBuilder = new ProcessBuilder(esbuildCommand)
                .directory(projectDir)
                .redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            int statusCode = process.waitFor();
            if (statusCode != 0) {
                throw new RuntimeException("esbuild error: finished with: " + statusCode);
            }
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException("Could not run esbuild", e);
        }
    }
}
