/*
 * Generated by Eclipse Dirigible based on model and template.
 *
 * Do not modify the content as it may be re-generated again.
 */
export function getSources(parameters) {
	return [{
		location: "/template-application-ui-angular/ui/perspectives/index.html.template",
		action: "generate",
		rename: "gen/{{genFolderName}}/ui/{{perspectiveName}}/index.html",
		engine: "velocity",
		collection: "uiPerspectives"
	}, {
		location: "/template-application-ui-angular/ui/perspectives/extensions/perspective/perspective.extension.template",
		action: "generate",
		rename: "gen/{{genFolderName}}/ui/{{perspectiveName}}/extensions/perspective/perspective.extension",
		engine: "velocity",
		collection: "uiPerspectives"
	}, {
		location: "/template-application-ui-angular/ui/perspectives/extensions/perspective/perspective.js.template",
		action: "generate",
		rename: "gen/{{genFolderName}}/ui/{{perspectiveName}}/extensions/perspective/perspective.js",
		engine: "velocity",
		collection: "uiPerspectives"
	}, {
		location: "/template-application-ui-angular/ui/perspectives/extensions/view.extensionpoint.template",
		action: "generate",
		rename: "gen/{{genFolderName}}/ui/{{perspectiveName}}/extensions/view.extensionpoint",
		engine: "velocity",
		collection: "uiPerspectives"
	}];
};
