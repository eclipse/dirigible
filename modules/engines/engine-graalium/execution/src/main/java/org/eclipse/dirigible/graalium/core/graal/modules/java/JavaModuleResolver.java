package org.eclipse.dirigible.graalium.core.graal.modules.java;

import org.eclipse.dirigible.graalium.core.graal.modules.ModuleResolver;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaModuleResolver implements ModuleResolver {

    private static final Pattern JAVA_MODULE_PATTERN = Pattern.compile("(@java)(\\/)(.+[^\"])");

    private final Path cacheDirectoryPath;
    private final JavaPackageProxyGenerator javaPackageProxyGenerator;

    public JavaModuleResolver(Path cacheDirectoryPath) {
        javaPackageProxyGenerator = new JavaPackageProxyGenerator();
        this.cacheDirectoryPath = cacheDirectoryPath;
    }

    @Override
    public boolean isResolvable(String moduleToResolve) {
        return moduleToResolve.contains("@java");
    }

    @Override
    public Path resolve(String moduleToResolve) {
        Matcher modulePathMatcher = JAVA_MODULE_PATTERN.matcher(moduleToResolve);
        if (!modulePathMatcher.matches()) {
            throw new RuntimeException("Found invalid Java module path!");
        }

        String javaPackageName = modulePathMatcher.group(3);

        Path javaPackageProxyGeneratedPath = cacheDirectoryPath.resolve(javaPackageName + ".mjs");
        File javaPackageProxyGeneratedFile = javaPackageProxyGeneratedPath.toFile();

        if (javaPackageProxyGeneratedFile.exists()) {
            return javaPackageProxyGeneratedPath;
        }

        String coreModuleContent = javaPackageProxyGenerator.generate(javaPackageName);
        try {
            Files.writeString(javaPackageProxyGeneratedPath, coreModuleContent, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return javaPackageProxyGeneratedPath;
    }
}
