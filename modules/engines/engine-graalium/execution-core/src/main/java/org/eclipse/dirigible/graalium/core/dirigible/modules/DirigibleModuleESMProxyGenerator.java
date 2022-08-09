package org.eclipse.dirigible.graalium.core.dirigible.modules;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Class DirigibleModuleESMProxyGenerator.
 */
public class DirigibleModuleESMProxyGenerator {

    /** The Constant API_MODULES_JSON_PATH. */
    private static final String API_MODULES_JSON_PATH = "/extensions/modules.json";

    /** The Constant NAME_PLACEHOLDER. */
    private static final String NAME_PLACEHOLDER = "<name_placeholder>";
    
    /** The Constant PATH_PLACEHOLDER. */
    private static final String PATH_PLACEHOLDER = "<path_placeholder>";
    
    /** The Constant NAMES_LIST_PLACEHOLDER. */
    private static final String NAMES_LIST_PLACEHOLDER = "<names_list_placeholder>";

    /** The Constant DEFAULT_EXPORT_PATTERN. */
    private static final String DEFAULT_EXPORT_PATTERN = "export default { " + NAMES_LIST_PLACEHOLDER + " }";
    
    /** The Constant EXPORT_PATTERN. */
    private static final String EXPORT_PATTERN =
            "export const " + NAME_PLACEHOLDER + " = dirigibleRequire('" + PATH_PLACEHOLDER + "');";
    
    /** The gson. */
    private final Gson gson = new Gson();
    
    /** The dirigible source provider. */
    private final DirigibleSourceProvider dirigibleSourceProvider = new DirigibleSourceProvider();

    /**
     * Generate.
     *
     * @param path the path
     * @param apiVersion the api version
     * @return the string
     */
    public String generate(String path, String apiVersion) {
        DirigibleModule[] modules = readApiModuleJson(path + API_MODULES_JSON_PATH);
        StringBuilder source = new StringBuilder();
        StringBuilder moduleNames = new StringBuilder();

        for (DirigibleModule module : modules) {
            if (module.isPackageDescription() || module.getShouldBeUnexposedToESM()) {
                continue;
            }

            String api = module.getApi();
            String dir = resolvePath(module, apiVersion);

            source.append(EXPORT_PATTERN
                    .replace(NAME_PLACEHOLDER, api)
                    .replace(PATH_PLACEHOLDER, dir));
            source.append(System.lineSeparator());
            moduleNames.append(api);
            moduleNames.append(',');
        }

        if (moduleNames.length() > 0) {
            moduleNames.setLength(moduleNames.length() - 1);
        }

        source.append(DEFAULT_EXPORT_PATTERN.replace(NAMES_LIST_PLACEHOLDER, moduleNames.toString()));
        source.append(System.lineSeparator());
        return source.toString();
    }

    /**
     * Read api module json.
     *
     * @param path the path
     * @return the dirigible module[]
     */
    private DirigibleModule[] readApiModuleJson(String path) {
        String apiModuleJson = dirigibleSourceProvider.getSource(path);
        return gson.fromJson(apiModuleJson, DirigibleModule[].class);
    }

    /**
     * Resolve path.
     *
     * @param module the module
     * @param apiVersion the api version
     * @return the string
     */
    private String resolvePath(DirigibleModule module, String apiVersion) {
        if (apiVersion.isEmpty()) {
            return module.getPathDefault();
        }

        List<String> foundPaths = Arrays.stream(module.getVersionedPaths())
                .filter(p -> p.contains(apiVersion))
                .collect(Collectors.toList());

        if (foundPaths.size() != 1) {
            StringBuilder message = new StringBuilder();
            message.append("Searching for single api path containing '");
            message.append(apiVersion);
            message.append("' but found: ");
            for (String foundPath : foundPaths) {
                message.append("'");
                message.append(foundPath);
                message.append("' ");
            }
            throw new RuntimeException(message.toString());
        }

        return foundPaths.get(0);
    }
}
