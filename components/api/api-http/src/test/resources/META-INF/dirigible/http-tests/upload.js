
var upload = require('http/upload');
var request = require('http/request');
var response = require('http/response');

if (request.getMethod() === "POST") {
	if (upload.isMultipartContent()) {
		var fileItems = upload.parseRequest();
		for (i=0; i<fileItems.size(); i++) {
			var fileItem = fileItems.get(i);
			if (!fileItem.isFormField()) {
				response.println("File Name: " + fileItem.getName());
				response.println("File Bytes (as text): " + String.fromCharCode.apply(null, fileItem.getBytes()));
			} else {
				response.println("Field Name: " + fileItem.getFieldName());
				response.println("Field Text: " + fileItem.getText());
			}
		}
	} else {
		response.println("The request's content must be 'multipart'");
	}
} else if (request.getMethod() === "GET") {
	response.println("Use POST request.");
}

response.flush();
response.close();
