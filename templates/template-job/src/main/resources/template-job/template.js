exports.getTemplate = function() {
	return {
		"name": "Job Template",
		"description": "Scheduled Job definition with a simple Javascript handler",
		"sources": [
		{
			"location": "/template-job/job.template", 
			"action": "generate",
			"rename": "{{fileName}}.job"
		},
		{
			"location": "/template-job/handler.js.template", 
			"action": "generate",
			"rename": "{{fileName}}-handler.js"
		}],
		"parameters": []
	};
};
