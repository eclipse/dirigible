/*
 * Generated by Eclipse Dirigible based on model and template.
 *
 * Do not modify the content as it may be re-generated again.
 */
export function getTemplate() {
	return {
		name: "Message Listener (Model)",
		description: "Listener for a message with a simple Javascript handler",
		sources: [{
			location: "/template-listener/listener.template", 
			action: "generate",
			rename: "{{fileName}}.listener"
		}, {
			location: "/template-listener/handler.js.template", 
			action: "generate",
			rename: "{{fileName}}-handler.js"
		}, {
			location: "/template-listener/trigger.js.template", 
			action: "generate",
			rename: "{{fileName}}-trigger.js"
		}],
		parameters: [],
		order: 51
	};
};
