package org.eclipse.dirigible.graalium.core.typescript;

import java.lang.ProcessBuilder;
import java.io.BufferedReader;
import java.lang.InterruptedException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

class TypescriptCompiler {

    private final Path cwd;

    public TypescriptCompiler(Path cwd) {
        this.cwd = cwd;
    }

    void compile(String filePattern) {
        compileTypeScriptFile(filePattern);
    }

    private void compileTypeScriptFile(String filePattern) {
//        var tscFilesPattern = filePath != null ? filePath.toString() : "**/*.ts";
    	ProcessBuilder processBuilder = new ProcessBuilder(
                "tsc",
                "--target",
                "es2022",
                "--module",
                "esnext",
                "--strict",
                "--skipLibCheck",
                filePattern
        )
                .directory(cwd.toFile());
//                .redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            process.waitFor();
        } catch (InterruptedException | IOException e) {
            throw new TypescriptCompilationException("Could not run tsc", e);
        }
    }
}