exports.getTemplate = function() {
	return {
		"name": "HTML",
		"description": "HTML Template",
		"sources": [{
			"location": "/template-html/index.html.tmpl", 
			"action": "generate",
			"rename": "{{fileName}}.html"
		}],
		"parameters": []
	};
};
