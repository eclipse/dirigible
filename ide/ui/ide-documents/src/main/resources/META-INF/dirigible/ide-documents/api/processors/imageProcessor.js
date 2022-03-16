let documentUtils = require("ide-documents/utils/cmis/document");
let imageUtils = require("ide-documents/utils/cmis/image");

exports.resize = function (path, documents, width, height) {
    let result = [];
    for (let i = 0; i < documents.size(); i++) {
        let folder = folderUtils.getFolder(path);
        let name = documents.get(i).getName();
        if (width && height && name) {
            result.push(imageUtils.uploadImageWithResize(folder, name, documents.get(i), parseInt(width), parseInt(height)));
        } else {
            result.push(documentUtils.uploadDocument(folder, documents.get(i)));
        }
    }
    return result;
};