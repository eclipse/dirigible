let response = require("http/response");
let extensions = require("extensions/extensions");

let fileTypeExtensions = extensions.getExtensions("ide-file-types");

let mappings = {};
for (let i = 0; i < fileTypeExtensions.length; i++) {
    let extension = require(fileTypeExtensions[i]);
    let fileTypes = extension.getFileTypes();
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