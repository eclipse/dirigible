/*
 * Generated by Eclipse Dirigible based on model and template.
 *
 * Do not modify the content as it may be re-generated again.
 */
exports.getSources = function (parameters) {
    var sources = [];
    sources = sources.concat(getMaster(parameters));
    sources = sources.concat(getDetails(parameters));
    return sources;
};

function getMaster(parameters) {
    return [
        // Location: "gen/ui/perspective"
        {
            location: "/template-application-ui-angular/ui/perspective/index.html",
            action: "generate",
            engine: "velocity",
            rename: "gen/ui/{{perspectiveName}}/index.html",
            collection: "uiListMasterModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/perspective.extension",
            action: "generate",
            rename: "gen/ui/{{perspectiveName}}/perspective.extension",
            collection: "uiListMasterModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/perspective-portal.extension",
            action: "generate",
            rename: "gen/ui/{{perspectiveName}}/perspective-portal.extension",
            collection: "uiListMasterModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/perspective.js",
            action: "generate",
            rename: "gen/ui/{{perspectiveName}}/perspective.js",
            collection: "uiListMasterModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/controller.js.template",
            action: "generate",
            engine: "velocity",
            rename: "gen/ui/{{perspectiveName}}/{{name}}/controller.js",
            collection: "uiListMasterModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/index.html.template",
            action: "generate",
            engine: "velocity",
            rename: "gen/ui/{{perspectiveName}}/{{name}}/index.html",
            collection: "uiListMasterModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/tile.extension",
            action: "generate",
            rename: "gen/ui/{{perspectiveName}}/{{name}}/tile.extension",
            collection: "uiListMasterModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/tile-portal.extension",
            action: "generate",
            rename: "gen/ui/{{perspectiveName}}/{{name}}/tile-portal.extension",
            collection: "uiListMasterModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/tile.js",
            action: "generate",
            rename: "gen/ui/{{perspectiveName}}/{{name}}/tile.js",
            collection: "uiListMasterModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/view.extension",
            action: "generate",
            rename: "gen/ui/{{perspectiveName}}/{{name}}/view.extension",
            collection: "uiListMasterModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/view.js",
            action: "generate",
            rename: "gen/ui/{{perspectiveName}}/{{name}}/view.js",
            collection: "uiListMasterModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/main-details/controller.js.template",
            action: "generate",
            engine: "velocity",
            rename: "gen/ui/{{perspectiveName}}/{{name}}/main-details/controller.js",
            collection: "uiListMasterModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/main-details/index.html.template",
            action: "generate",
            engine: "velocity",
            rename: "gen/ui/{{perspectiveName}}/{{name}}/main-details/index.html",
            collection: "uiListMasterModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/main-details/view.extension",
            action: "generate",
            rename: "gen/ui/{{perspectiveName}}/{{name}}/main-details/view.extension",
            collection: "uiListMasterModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/main-details/view.js",
            action: "generate",
            rename: "gen/ui/{{perspectiveName}}/{{name}}/main-details/view.js",
            collection: "uiListMasterModels"
        },
    ];
}

function getDetails(parameters) {
    return [
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/detail/controller.js.template",
            action: "generate",
            engine: "velocity",
            rename: "gen/ui/{{perspectiveName}}/{{masterEntity}}/{{name}}/controller.js",
            collection: "uiListDetailsModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/detail/index.html.template",
            action: "generate",
            engine: "velocity",
            rename: "gen/ui/{{perspectiveName}}/{{masterEntity}}/{{name}}/index.html",
            collection: "uiListDetailsModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/detail/view.extension",
            action: "generate",
            engine: "velocity",
            rename: "gen/ui/{{perspectiveName}}/{{masterEntity}}/{{name}}/view.extension",
            collection: "uiListDetailsModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/detail/view.js",
            action: "generate",
            engine: "velocity",
            rename: "gen/ui/{{perspectiveName}}/{{masterEntity}}/{{name}}/view.js",
            collection: "uiListDetailsModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/detail/dialog-window/controller.js.template",
            action: "generate",
            engine: "velocity",
            rename: "gen/ui/{{perspectiveName}}/{{masterEntity}}/{{name}}/dialog-window/controller.js",
            collection: "uiListDetailsModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/detail/dialog-window/index.html.template",
            action: "generate",
            engine: "velocity",
            rename: "gen/ui/{{perspectiveName}}/{{masterEntity}}/{{name}}/dialog-window/index.html",
            collection: "uiListDetailsModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/detail/dialog-window/view.extension",
            action: "generate",
            engine: "velocity",
            rename: "gen/ui/{{perspectiveName}}/{{masterEntity}}/{{name}}/dialog-window/view.extension",
            collection: "uiListDetailsModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/detail/dialog-window/view.js",
            action: "generate",
            engine: "velocity",
            rename: "gen/ui/{{perspectiveName}}/{{masterEntity}}/{{name}}/dialog-window/view.js",
            collection: "uiListDetailsModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/detail/dialog-filter/controller.js.template",
            action: "generate",
            engine: "velocity",
            rename: "gen/ui/{{perspectiveName}}/{{masterEntity}}/{{name}}/dialog-filter/controller.js",
            collection: "uiListDetailsModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/detail/dialog-filter/index.html.template",
            action: "generate",
            engine: "velocity",
            rename: "gen/ui/{{perspectiveName}}/{{masterEntity}}/{{name}}/dialog-filter/index.html",
            collection: "uiListDetailsModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/detail/dialog-filter/view.extension",
            action: "generate",
            engine: "velocity",
            rename: "gen/ui/{{perspectiveName}}/{{masterEntity}}/{{name}}/dialog-filter/view.extension",
            collection: "uiListDetailsModels"
        },
        {
            location: "/template-application-ui-angular/ui/perspective/master-list/detail/dialog-filter/view.js",
            action: "generate",
            engine: "velocity",
            rename: "gen/ui/{{perspectiveName}}/{{masterEntity}}/{{name}}/dialog-filter/view.js",
            collection: "uiListDetailsModels"
        }
    ];
}
