/*
 * Generated by Eclipse Dirigible based on model and template.
 *
 * Do not modify the content as it may be re-generated again.
 */
const viewData = {
    id: "{{name}}",
    label: "{{name}}",
    factory: "frame",
    region: "center",
    link: "/services/web/{{projectName}}/gen/ui/{{perspectiveName}}/{{name}}/index.html",
    perspectiveName: "{{perspectiveName}}"
};

if (typeof exports !== 'undefined') {
    exports.getView = function () {
        return viewData;
    }
}
