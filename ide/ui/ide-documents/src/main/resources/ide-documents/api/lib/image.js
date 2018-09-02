var streams = require('io/v3/streams');
var imageIO = require('io/v3/image');
var documentLib = require("ide-documents/api/lib/document");
 
 
exports.uploadImageWithResize = function(folder, name, image, width, height) {
	
	console.error(">>>>>>>>>>>>>>");
	
	var fileName = name;
    var mimetype = image.getContentType();
    var originalInputStream = image.getInputStream();
    var inputStream = new streams.InputStream();
	inputStream.uuid = originalInputStream.uuid;
               
    var imageType = mimetype.split('/')[1];
    
    var resizedInputStream = imageIO.resize(inputStream, imageType, width, height);
    
    image.getInputStream = function(){
    	return resizedInputStream;//new streams.InputStream(fis);
    }
    
    documentLib.uploadDocument(folder, image);
}