package org.eclipse.dirigible.graalium.core.dirigible.modules;

import org.eclipse.dirigible.graalium.core.graal.modules.ModuleResolver;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DirigibleModuleResolver implements ModuleResolver {

    private static final String DIRIGIBLE_CORE_MODULE_SIGNATURE = "@dirigible";
    private static final Pattern DIRIGIBLE_CORE_MODULE_SIGNATURE_PATTERN = Pattern.compile("(@dirigible)(\\/)(\\w+)"); // e.g. @dirigible/core  => $1=dirigible $2=/ $3=core

    private final DirigibleModuleESMProxyGenerator dirigibleModuleESMProxyGenerator;
    private final Path cacheDirectoryPath;

    public DirigibleModuleResolver(Path cacheDirectoryPath) {
        dirigibleModuleESMProxyGenerator = new DirigibleModuleESMProxyGenerator();
        cacheDirectoryPath.toFile().mkdirs();
        this.cacheDirectoryPath = cacheDirectoryPath;
    }

    @Override
    public boolean isResolvable(String moduleToResolve) {
        return moduleToResolve.contains(DIRIGIBLE_CORE_MODULE_SIGNATURE);
    }

    @Override
    public Path resolve(String moduleToResolve) {
        Matcher modulePathMatcher = DIRIGIBLE_CORE_MODULE_SIGNATURE_PATTERN.matcher(moduleToResolve);
        if (!modulePathMatcher.matches()) {
            throw new RuntimeException("Found invalid Dirigible core modules path!");
        }

        String coreModuleName = modulePathMatcher.group(3);

        Path coreModuleGeneratedPath = cacheDirectoryPath.resolve(coreModuleName + ".mjs");
        File coreModuleGeneratedFile = coreModuleGeneratedPath.toFile();

        if (coreModuleGeneratedFile.exists()) {
            return coreModuleGeneratedFile.toPath();
        }

        String coreModuleContent = dirigibleModuleESMProxyGenerator.generate(coreModuleName, "");
        try {
            Files.writeString(coreModuleGeneratedPath, coreModuleContent, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return coreModuleGeneratedFile.toPath();
    }
}
