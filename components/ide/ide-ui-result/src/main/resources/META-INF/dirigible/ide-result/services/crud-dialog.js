const viewData = {
    id: "result-view-crud",
    label: "CRUD",
    link: "../ide-result/dialogs/crud-dialog.html"
};
if (typeof exports !== 'undefined') {
    exports.getDialogWindow = function () {
        return viewData;
    }
}