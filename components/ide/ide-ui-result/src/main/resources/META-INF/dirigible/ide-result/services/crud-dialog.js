const viewData = {
    id: "result-view-crud",
    label: "Edit Table",
    link: "../ide-result/dialogs/crud-dialog.html"
};
if (typeof exports !== 'undefined') {
    exports.getDialogWindow = function () {
        return viewData;
    }
}