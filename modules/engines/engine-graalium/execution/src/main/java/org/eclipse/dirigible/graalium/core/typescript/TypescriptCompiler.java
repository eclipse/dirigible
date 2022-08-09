package org.eclipse.dirigible.graalium.core.typescript;

import java.lang.ProcessBuilder;
import java.io.BufferedReader;
import java.lang.InterruptedException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

/**
 * The Class TypescriptCompiler.
 */
class TypescriptCompiler {

    /** The cwd. */
    private final Path cwd;

    /**
     * Instantiates a new typescript compiler.
     *
     * @param cwd the cwd
     */
    public TypescriptCompiler(Path cwd) {
        this.cwd = cwd;
    }

    /**
     * Compile.
     *
     * @param filePattern the file pattern
     */
    void compile(String filePattern) {
        compileTypeScriptFile(filePattern);
    }

    /**
     * Compile type script file.
     *
     * @param filePattern the file pattern
     */
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