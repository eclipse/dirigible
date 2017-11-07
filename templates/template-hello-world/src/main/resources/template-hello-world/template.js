exports.getTemplate = function() {
	return {
		"name": "Hello World",
		"description": "Hello World Template",
		"sources": [{
			"location": "/template-hello-world/service.js.template", 
			"action": "generate",
			"rename": "{{fileName}}.js"
		}],
		"parameters": []
	};
};
