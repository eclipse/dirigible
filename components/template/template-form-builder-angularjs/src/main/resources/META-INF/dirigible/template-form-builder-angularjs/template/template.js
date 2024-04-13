/*
 * Generated by Eclipse Dirigible based on model and template.
 *
 * Do not modify the content as it may be re-generated again.
 */
const generateUtils = dirigibleRequire("ide-generate-service/template/generateUtils");

exports.generate = function (model, parameters) {
    const templateModel = JSON.parse(model);
    const templateSources = exports.getTemplate(parameters).sources;
    if (templateModel.code) {
        templateModel.code = templateModel.code.split("\n").map(e => `    ${e}`).join('\n');
    }
    return generateUtils.generateGeneric(templateModel, parameters, templateSources);
};

exports.getTemplate = function (parameters) {
    return {
        name: "AngularJS Generator from Form Model",
        description: "AngularJS Form Model generator template",
        extension: "form",
        sources: [
            {
                location: "/template-form-builder-angularjs/ui/controller.js.template",
                action: "generate",
                rename: "gen/forms/{{fileName}}/controller.js",
                engine: "velocity",
            },
            {
                location: "/template-form-builder-angularjs/ui/index.html.template",
                action: "generate",
                rename: "gen/forms/{{fileName}}/index.html",
                engine: "velocity",
            },
            {
                location: "/template-form-builder-angularjs/ui/view.js.template",
                action: "generate",
                rename: "gen/forms/{{fileName}}/view.js",
                engine: "velocity",
            },
        ],
        parameters: []
    };
}