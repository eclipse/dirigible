import { response } from "@dirigible/http"
import { extensions } from "@dirigible/extensions";

const fileTypeExtensions = await extensions.loadExtensionModules("ide-file-types");

const mappings = {};
for (let i = 0; i < fileTypeExtensions?.length; i++) {
    const fileTypes = fileTypeExtensions[i].getFileTypes();

    for (const fileExtension in fileTypes) {
        if (!mappings[fileExtension]) {
            mappings[fileExtension] = fileTypes[fileExtension];
        }
    }
}
response.setContentType("application/json");
response.println(JSON.stringify(mappings));
response.flush();
response.close();