/*
 * Generated by Eclipse Dirigible based on model and template.
 *
 * Do not modify the content as it may be re-generated again.
 */
export function getTemplate() {
	return {
		name: "TypeScript (API)",
		description: "TypeScript Sample Template",
		sources: [{
			location: "/template-typescript/project.json.template",
			action: "generate",
			rename: "project.json"
		}, {
			location: "/template-typescript/tsconfig.json.template",
			action: "copy",
			rename: "tsconfig.json"
		}, {
			location: "/template-typescript/service.ts.template",
			action: "copy",
			rename: "{{fileName}}.ts"
		}],
		parameters: [],
		order: 1
	};
};
