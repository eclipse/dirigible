exports.getTemplate = function() {
	return {
		"name": "HTTP Client",
		"description": "HTTP Client Template",
		"sources": [{
			"location": "/template-http-client/service.js.template", 
			"action": "generate",
			"rename": "{{fileName}}.js"
		}],
		"parameters": []
	};
};
