package org.eclipse.dirigible.graalium.core.modules;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.dirigible.graalium.core.JavascriptSourceProvider;
import org.eclipse.dirigible.graalium.core.graal.modules.ModuleResolver;

import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DirigibleEsmModuleResolver implements ModuleResolver {
    private static final Pattern DIRIGIBLE_CORE_MODULE_SIGNATURE_PATTERN = Pattern.compile("(@dirigible)/(\\w+)(?:/(.+))?"); // e.g. @dirigible/core/module/submodule  => $1=dirigible $2=core $3=module/submodule

    private final JavascriptSourceProvider sourceProvider;

    public DirigibleEsmModuleResolver(JavascriptSourceProvider sourceProvider) {
        this.sourceProvider = sourceProvider;
    }

    @Override
    public boolean isResolvable(String moduleToResolve) {
        return moduleToResolve.contains("@dirigible") && DirigibleModulesMetadata.isPureEsmModule(moduleToResolve);
    }

    @Override
    public Path resolve(String moduleToResolve) {
        Matcher modulePathMatcher = DIRIGIBLE_CORE_MODULE_SIGNATURE_PATTERN.matcher(moduleToResolve);
        if (!modulePathMatcher.matches()) {
            throw new RuntimeException("Found invalid Dirigible core modules path!");
        }

        String dirigibleModuleDir = modulePathMatcher.group(2);
        String dirigibleModuleFile = modulePathMatcher.group(3);

        boolean hasDirigibleModuleFile = !StringUtils.isEmpty(dirigibleModuleFile);

        Path dirigibleModulePath = sourceProvider.getAbsoluteProjectPath("modules")
                .resolve("dist")
                .resolve("esm")
                .resolve(dirigibleModuleDir)
                .resolve(hasDirigibleModuleFile ? dirigibleModuleFile + ".mjs" : "index.mjs");
        return dirigibleModulePath;
    }
}
