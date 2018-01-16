exports.getTemplate = function() {
	return {
		"name": "Extension View Template",
		"description": "Extension view for the IDE",
		"sources": [{
			"location": "/template-extension-view/view.html.template", 
			"action": "generate",
			"rename": "{{fileName}}.html"
		}, {
			"location": "/template-extension-view/extension-view.js.template", 
			"action": "generate",
			"rename": "{{fileName}}.js"
		}, {
			"location": "/template-extension-view/view.extension.template", 
			"action": "generate",
			"rename": "{{fileName}}.extension"
		}],
		"parameters": [{
			"name": "viewName",
			"label": "Name"
		}]
	};
};