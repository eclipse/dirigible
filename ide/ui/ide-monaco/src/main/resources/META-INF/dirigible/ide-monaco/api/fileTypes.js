let response = require("http/v4/response");
let extensions = require("core/v4/extensions");

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

response.println(JSON.stringify(mappings));
response.flush();
response.close();