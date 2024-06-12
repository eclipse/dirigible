/*
 * Generated by Eclipse Dirigible based on model and template.
 *
 * Do not modify the content as it may be re-generated again.
 */
const reportFileTemplate = dirigibleRequire("template-application-ui-angular/template/ui/reportFile");
const generateUtils = dirigibleRequire("ide-generate-service/template/generateUtils");
const parameterUtils = dirigibleRequire("ide-generate-service/template/parameterUtils");

exports.generate = function (model, parameters) {
    model = JSON.parse(model);
    let templateSources = exports.getTemplate(parameters).sources;
    model?.columns?.forEach(e => {
        const parsedDataType = parameterUtils.parseDataTypes(e.type);
        e.typeJava = parsedDataType.java;
        e.typeTypescript = parsedDataType.ts;
        if (e.typeTypescript === "Date") {
            model.hasDates = true
        }
    });
    model?.parameters?.forEach(e => {
        const parsedDataType = parameterUtils.parseDataTypes(e.type);
        e.typeJava = parsedDataType.java;
        e.typeTypescript = parsedDataType.ts;
        model?.conditions?.forEach(c => {
            if (c.right === `:${e.name}` && e.typeTypescript === 'string' && c.operation === 'LIKE') {
                e.isLikeCondition = true;
            }
        });
    });
    model.queryLines = model.query.split("\n");
    if (parameters.extensionPoint === undefined) {
        parameters.extensionPoint = parameters.projectName;
    }
    if (parameters.name === undefined) {
        parameters.name = model.name;
    }
    if (parameters.perspectiveName === undefined) {
        parameters.perspectiveName = model.name;
    }
    return generateUtils.generateGeneric(model, parameters, templateSources);
};

exports.getTemplate = function (parameters) {
    return {
        name: "Application Report - Table",
        description: "Application Table Report",
        extension: "report",
        sources: reportFileTemplate.getSources(),
        parameters: [
            {
                name: "extensionPoint",
                label: "Extension Point",
                placeholder: "Enter Extension Point, if not provided defaults to the project name",
                required: false
            },
            {
                name: "brand",
                label: "Brand",
                placeholder: "Enter Brand"
            },
            {
                name: "brandUrl",
                label: "Brand URL",
                placeholder: "Enter Brand URL"
            },
            {
                name: "title",
                label: "Title",
                placeholder: "Enter Title"
            }]
    };
};