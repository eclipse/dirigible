exports.getTemplate = function() {
	return {
		"name": "Bookstore Application",
		"description": "Bookstore Application Sample with a Table, a REST Service and an AngularJS User Interface",
		"sources": [
		{
			"location": "/template-bookstore/bookstore.table.template", 
			"action": "generate",
			"rename": "{{fileName}}.table"
		},
		{
			"location": "/template-bookstore/bookstore.js.template", 
			"action": "generate",
			"rename": "{{fileName}}.js"
		},
		{
			"location": "/template-bookstore/index.html.template", 
			"action": "generate",
			"rename": "{{fileName}}.html",
			"start" : "[[",
			"end" : "]]"
		}],
		"parameters": []
	};
};
