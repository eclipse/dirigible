package org.eclipse.dirigible.engine.js.graalvm.execution.js.modules;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DirigibleCoreModuleResolver {

    private static final String DIRIGIBLE_CORE_MODULE_SIGNATURE = "@dirigible";
    private static final Pattern DIRIGIBLE_CORE_MODULE_SIGNATURE_PATTERN = Pattern.compile("(@dirigible)(\\/)(\\w+)"); // e.g. @dirigible/core  => $1=dirigible $2=/ $3=core

    private final DirigibleCoreModuleESMProxyGenerator dirigibleCoreModuleESMProxyGenerator;

    public DirigibleCoreModuleResolver() {
        dirigibleCoreModuleESMProxyGenerator = new DirigibleCoreModuleESMProxyGenerator();
    }

    public boolean isCoreModule(String moduleToResolve) {
        return moduleToResolve.contains(DIRIGIBLE_CORE_MODULE_SIGNATURE);
    }

    public Path resolveCoreModulePath(String pathString) {
        Matcher modulePathMatcher = DIRIGIBLE_CORE_MODULE_SIGNATURE_PATTERN.matcher(pathString);
        if (!modulePathMatcher.matches()) {
            throw new RuntimeException("Found invalid Dirigible core modules path!");
        }

        String coreModuleName = modulePathMatcher.group(3);
        String coreModuleContent = dirigibleCoreModuleESMProxyGenerator.generate(coreModuleName, "");

        File coreModuleGeneratedFile = new File("/Users/c5326377/work/dirigible/core-modules/" + coreModuleName + ".mjs");
        try {
            FileUtils.write(coreModuleGeneratedFile, coreModuleContent, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return coreModuleGeneratedFile.toPath();
    }
}
