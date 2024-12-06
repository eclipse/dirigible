const viewData = {
    id: "result-view-crud",
    label: "Row Dialog",
    link: "../ide-result/dialogs/crud-dialog.html"
};
if (typeof exports !== 'undefined') {
    exports.getDialogWindow = function () {
        return viewData;
    }
}