import { response } from "@dirigible/http"
import { extensions } from "@dirigible/extensions";

let fileTypeExtensions = extensions.getExtensions("ide-file-types");

let mappings = {};
for (let i = 0; i < fileTypeExtensions?.length; i++) {
    let fileTypes;
    try {
        let extension = await import(`../../${fileTypeExtensions[i]}`);
        fileTypes = extension.getFileTypes();
    } catch (e) {
        // Fallback for not migrated extensions
        let extension = require(fileTypeExtensions[i]);
        fileTypes = extension.getFileTypes();
    }
    for (let fileExtension in fileTypes) {
        if (!mappings[fileExtension]) {
            mappings[fileExtension] = fileTypes[fileExtension];
        }
    }
}
response.setContentType("application/json");
response.println(JSON.stringify(mappings));
response.flush();
response.close();