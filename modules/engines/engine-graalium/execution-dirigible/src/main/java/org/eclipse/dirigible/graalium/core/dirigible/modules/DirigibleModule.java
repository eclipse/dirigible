package org.eclipse.dirigible.graalium.core.dirigible.modules;

import java.util.Objects;

public class DirigibleModule {
    private final String name;
    private final String api;
    private final String[] versionedPaths;
    private final String pathDefault;
    private final boolean isPackageDescription;
    private final Boolean shouldBeUnexposedToESM;

    DirigibleModule(String name, String api, String[] versionedPaths, String pathDefault, boolean isPackageDescription, Boolean shouldBeUnexposedToESM) {
        this.name = name;
        this.api = api;
        this.versionedPaths = versionedPaths;
        this.pathDefault = pathDefault;
        this.isPackageDescription = isPackageDescription;
        this.shouldBeUnexposedToESM = shouldBeUnexposedToESM;
    }

    String getName() {
        return name;
    }

    public String getApi() {
        return api;
    }

    public String[] getVersionedPaths() {
        return versionedPaths;
    }

    public String getPathDefault() {
        return pathDefault;
    }

    public boolean getShouldBeUnexposedToESM() {
        return Objects.requireNonNullElse(shouldBeUnexposedToESM, false);
    }

    public boolean isPackageDescription() {
        return isPackageDescription;
    }
}
