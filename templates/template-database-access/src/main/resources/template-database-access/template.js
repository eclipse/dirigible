exports.getTemplate = function() {
	return {
		"name": "Database Access",
		"description": "Database Access Template",
		"sources": [{
			"location": "/template-database-access/service.js.template", 
			"action": "generate",
			"rename": "{{fileName}}.js"
		}],
		"parameters": []
	};
};
