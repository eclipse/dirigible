/*
 * Generated by Eclipse Dirigible based on model and template.
 *
 * Do not modify the content as it may be re-generated again.
 */
const viewData = {
    id: "${projectName}-home-launchpad",
    label: "Home Launchpad",
    factory: "frame",
    region: "center",
    link: "/services/web/${projectName}/gen/${genFolderName}/ui/launchpad/Home/index.html",
    isLaunchpad: true,
};

if (typeof exports !== 'undefined') {
    exports.getView = function () {
        return viewData;
    }
}
