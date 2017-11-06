exports.getTemplate = function() {
	return {
		"name": "HTML",
		"description": "HTML Template",
		"sources": [{
			"location": "/template-html/html.template", 
			"action": "generate",
			"rename": "{{fileName}}.html"
		}],
		"parameters": []
	};
};
