exports.getTemplate = function() {
	return {
		"name": "Extension Perspective Template",
		"description": "Extension perspective for the IDE",
		"sources": [{
			"location": "/template-extension-perspective/extensions/menu/help.extension.template", 
			"action": "generate",
			"rename": "/extensions/menu/help.extension"
		}, {
			"location": "/template-extension-perspective/extensions/menu/window.extension.template", 
			"action": "generate",
			"rename": "/extensions/menu/window.extension"
		}, {
			"location": "/template-extension-perspective/extensions/menu/perspective.extension.template", 
			"action": "generate",
			"rename": "/extensions/menu/{{fileName}}.extension"
		}, {
			"location": "/template-extension-perspective/extensions/menu/menu-perspective.extensionpoint.template", 
			"action": "generate",
			"rename": "/extensions/menu/menu-{{fileName}}.extensionpoint"
		}, {
			"location": "/template-extension-perspective/extensions/perspective-perspective.extension.template", 
			"action": "generate",
			"rename": "/extensions/perspective-{{fileName}}.extension"
		}, {
			"location": "/template-extension-perspective/perspective.html.template", 
			"action": "generate",
			"rename": "{{fileName}}.html"
		}, {
			"location": "/template-extension-perspective/services/menu/perspective.js.template", 
			"action": "generate",
			"rename": "/services/menu/{{fileName}}.js"
		}, {
			"location": "/template-extension-perspective/services/menu-perspective.js.template", 
			"action": "generate",
			"rename": "/services/menu-{{fileName}}.js"
		}, {
			"location": "/template-extension-perspective/services/perspective-perspective.js.template", 
			"action": "generate",
			"rename": "/services/perspective-{{fileName}}.js"
		}],
		"parameters": []
	};
};
