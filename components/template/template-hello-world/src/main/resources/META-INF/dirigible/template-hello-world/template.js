/*
 * Generated by Eclipse Dirigible based on model and template.
 *
 * Do not modify the content as it may be re-generated again.
 */
exports.getTemplate = function () {
	return {
		"name": "Hello World",
		"description": "Hello World Template",
		"sources": [{
			"location": "/template-hello-world/project.json.template",
			"action": "generate",
			"rename": "project.json"
		}, {
			"location": "/template-hello-world/tsconfig.json.template",
			"action": "copy",
			"rename": "tsconfig.json"
		}, {
			"location": "/template-hello-world/service.ts.template",
			"action": "copy",
			"rename": "{{fileName}}-ts.ts"
		}, {
			"location": "/template-hello-world/service.mjs.template",
			"action": "copy",
			"rename": "{{fileName}}.mjs"
		}, {
			"location": "/template-hello-world/service.js.template",
			"action": "copy",
			"rename": "{{fileName}}.js"
		}],
		"parameters": [],
		"order": -1
	};
};
