exports.getTemplate = function() {
	return {
		"name": "HTML5 (AngularJS)",
		"description": "HTML5 Template with AngularJS",
		"sources": [{
			"location": "/template-html/index.html.template", 
			"action": "generate",
			"rename": "{{fileName}}.html",
			"start" : "[[",
			"end" : "]]"
		}],
		"parameters": []
	};
};
