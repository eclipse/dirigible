/*
 * Generated by Eclipse Dirigible based on model and template.
 *
 * Do not modify the content as it may be re-generated again.
 */
import * as restTemplateManager from "template-application-rest/template/template";
import * as uiTemplate from "template-application-ui-angular/template/ui/template";
import * as generateUtils from "ide-generate-service/template/generateUtils";
import * as parameterUtils from "ide-generate-service/template/parameterUtils";

export function generate(model, parameters) {
    model = JSON.parse(model).model;
    let templateSources = getTemplate(parameters).sources;
    parameterUtils.process(model, parameters)
    return generateUtils.generateFiles(model, parameters, templateSources);
};

export function getTemplate(parameters) {
    let restTemplate = restTemplateManager.getTemplate(parameters);

    let templateSources = [];
    templateSources = templateSources.concat(restTemplate.sources);
    templateSources = templateSources.concat(uiTemplate.getSources(parameters));

    let templateParameters = getTemplateParameters();
    templateParameters = templateParameters.concat(restTemplate.parameters);

    return {
        name: "Application - UI (AngularJS)",
        description: "Application with UI, REST APIs and DAOs",
        extension: "model",
        sources: templateSources,
        parameters: templateParameters
    };
};

function getTemplateParameters() {
    return [
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
        },
        {
            name: "description",
            label: "Description",
            placeholder: "Enter Description"
        }
    ];
}