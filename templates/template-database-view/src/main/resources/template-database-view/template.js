exports.getTemplate = function() {
	return {
		"name": "Database View",
		"description": "Database View Template",
		"sources": [{
			"location": "/template-database-view/view.template", 
			"action": "generate",
			"rename": "{{fileName}}.view"
		}],
		"parameters": [{
			"name": "viewName",
			"label": "View Name"
		}]
	};
};
