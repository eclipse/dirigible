exports.consumeFiles = function(request) {
    var maxSize = 2 * 1024 * 1024;
	var files = [];
	
	if(upload.isMultipartContent(request)) {
        var fileItems = upload.parseRequest(request);
        for(var i = 0; i < fileItems.size(); i ++){
            var file = createEntity(fileItems.get(i));
            if(file.name){
                files.push(file);
            }
        }
	}
	return files;
};


function createEntity(fileItem) {
    var file = {
        "name": fileItem.getName(),
        "data": io.toByteArray(fileItem.getInputStream()),
        "contentType": getContentType(fileItem.getContentType()),
        "size": fileItem.getSize()
    };
    return file;
}

function getContentType(contentType){
    return contentType ? contentType : "";
}