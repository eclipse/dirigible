/*
 * Generated by Eclipse Dirigible based on model and template.
 *
 * Do not modify the content as it may be re-generated again.
 */
export function getTemplate() {
	return {
		name: "Database Table (Model)",
		description: "Database Table Template",
		sources: [{
			location: "/template-database-table/database.table.template", 
			action: "generate",
			rename: "{{fileName}}.table"
		}],
		parameters: [{
			name: "tableName",
			label: "Table Name"
		}],
		order: 40
	};
};
