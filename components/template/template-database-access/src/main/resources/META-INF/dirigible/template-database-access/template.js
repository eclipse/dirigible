/*
 * Generated by Eclipse Dirigible based on model and template.
 *
 * Do not modify the content as it may be re-generated again.
 */
exports.getTemplate = function() {
	return {
		"name": "Database Access (API)",
		"description": "Database Access Template",
		"sources": [{
			"location": "/template-database-access/service.mjs.template", 
			"action": "generate",
			"rename": "{{fileName}}.mjs"
		}],
		"parameters": [],
		"order": 21
	};
};
