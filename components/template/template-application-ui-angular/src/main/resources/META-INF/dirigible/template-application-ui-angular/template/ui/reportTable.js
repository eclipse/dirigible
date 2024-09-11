/*
 * Generated by Eclipse Dirigible based on model and template.
 *
 * Do not modify the content as it may be re-generated again.
 */
export function getSources(parameters) {
	return [
		// Location: "gen/{{genFolderName}}/ui/perspective"
		{
			location: "/template-application-ui-angular/ui/perspective/index.html",
			action: "generate",
			engine: "velocity",
			rename: "gen/{{genFolderName}}/ui/{{perspectiveName}}/index.html",
			collection: "uiReportTableModels"
		},
		{
			location: "/template-application-ui-angular/ui/perspective/perspective.extension",
			action: "generate",
			rename: "gen/{{genFolderName}}/ui/{{perspectiveName}}/perspective.extension",
			collection: "uiReportTableModels"
		},
		{
			location: "/template-application-ui-angular/ui/perspective/perspective-portal.extension",
			action: "generate",
			rename: "gen/{{genFolderName}}/ui/{{perspectiveName}}/perspective-portal.extension",
			collection: "uiReportTableModels"
		},
		{
			location: "/template-application-ui-angular/ui/perspective/perspective.js.template",
			action: "generate",
			engine: "velocity",
			rename: "gen/{{genFolderName}}/ui/{{perspectiveName}}/perspective.js",
			collection: "uiReportTableModels"
		},
		// Location: "gen/{{genFolderName}}/ui/perspective/list"
		{
			location: "/template-application-ui-angular/ui/perspective/report-table/dialog-window/controller.js.template",
			action: "generate",
			engine: "velocity",
			rename: "gen/{{genFolderName}}/ui/{{perspectiveName}}/{{name}}/dialog-window/controller.js",
			collection: "uiReportTableModels"
		},
		{
			location: "/template-application-ui-angular/ui/perspective/report-table/dialog-window/index.html.template",
			action: "generate",
			engine: "velocity",
			rename: "gen/{{genFolderName}}/ui/{{perspectiveName}}/{{name}}/dialog-window/index.html",
			collection: "uiReportTableModels"
		},
		{
			location: "/template-application-ui-angular/ui/perspective/report-table/dialog-window/view.extension",
			action: "generate",
			rename: "gen/{{genFolderName}}/ui/{{perspectiveName}}/{{name}}/dialog-window/view.extension",
			collection: "uiReportTableModels"
		},
		{
			location: "/template-application-ui-angular/ui/perspective/report-table/dialog-window/view.js.template",
			action: "generate",
			engine: "velocity",
			rename: "gen/{{genFolderName}}/ui/{{perspectiveName}}/{{name}}/dialog-window/view.js",
			collection: "uiReportTableModels"
		},
		{
			location: "/template-application-ui-angular/ui/perspective/report-table/dialog-window-filter/controller.js.template",
			action: "generate",
			engine: "velocity",
			rename: "gen/{{genFolderName}}/ui/{{perspectiveName}}/{{name}}/dialog-window-filter/controller.js",
			collection: "uiReportTableModels"
		},
		{
			location: "/template-application-ui-angular/ui/perspective/report-table/dialog-window-filter/index.html.template",
			action: "generate",
			engine: "velocity",
			rename: "gen/{{genFolderName}}/ui/{{perspectiveName}}/{{name}}/dialog-window-filter/index.html",
			collection: "uiReportTableModels"
		},
		{
			location: "/template-application-ui-angular/ui/perspective/report-table/dialog-window-filter/view.extension",
			action: "generate",
			rename: "gen/{{genFolderName}}/ui/{{perspectiveName}}/{{name}}/dialog-window-filter/view.extension",
			collection: "uiReportTableModels"
		},
		{
			location: "/template-application-ui-angular/ui/perspective/report-table/dialog-window-filter/view.js.template",
			action: "generate",
			engine: "velocity",
			rename: "gen/{{genFolderName}}/ui/{{perspectiveName}}/{{name}}/dialog-window-filter/view.js",
			collection: "uiReportTableModels"
		},
		{
			location: "/template-application-ui-angular/ui/perspective/report-table/controller.js.template",
			action: "generate",
			engine: "velocity",
			rename: "gen/{{genFolderName}}/ui/{{perspectiveName}}/{{name}}/controller.js",
			collection: "uiReportTableModels"
		},
		{
			location: "/template-application-ui-angular/ui/perspective/report-table/index.html.template",
			action: "generate",
			engine: "velocity",
			rename: "gen/{{genFolderName}}/ui/{{perspectiveName}}/{{name}}/index.html",
			collection: "uiReportTableModels"
		},
		{
			location: "/template-application-ui-angular/ui/perspective/report-table/tile.extension",
			action: "generate",
			rename: "gen/{{genFolderName}}/ui/{{perspectiveName}}/{{name}}/tile.extension",
			collection: "uiReportTableModels"
		},
		{
			location: "/template-application-ui-angular/ui/perspective/report-table/tile-portal.extension",
			action: "generate",
			rename: "gen/{{genFolderName}}/ui/{{perspectiveName}}/{{name}}/tile-portal.extension",
			collection: "uiReportTableModels"
		},
		{
			location: "/template-application-ui-angular/ui/perspective/report-table/tile.js.template",
			action: "generate",
			engine: "velocity",
			rename: "gen/{{genFolderName}}/ui/{{perspectiveName}}/{{name}}/tile.js",
			collection: "uiReportTableModels"
		},
		{
			location: "/template-application-ui-angular/ui/perspective/report-table/view.extension",
			action: "generate",
			rename: "gen/{{genFolderName}}/ui/{{perspectiveName}}/{{name}}/view.extension",
			collection: "uiReportTableModels"
		},
		{
			location: "/template-application-ui-angular/ui/perspective/report-table/view.js.template",
			action: "generate",
			engine: "velocity",
			rename: "gen/{{genFolderName}}/ui/{{perspectiveName}}/{{name}}/view.js",
			collection: "uiReportTableModels"
		}];
};