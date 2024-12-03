angular.module('cruddialog', ['ideUI', 'ideView']);

angular.module('cruddialog').controller('CRUDDialogController', ['$scope', 'messageHub', 'ViewParameters', function ($scope, messageHub, ViewParameters) {
    // State management
    $scope.state = {
        dialogType: null, // 'edit', 'delete', 'create'
        isBusy: true,
        error: false,
        busyText: "Loading...",
    };

    // Data
    $scope.data = {
        selectedRow: {},
        newRow: {},
    };

    // Close dialog
    $scope.closeDialog = function () {
        $scope.state.isDialogOpen = false;
        $scope.state.dialogType = null;
        $scope.data.selectedRow = {};
        $scope.data.newRow = {};
    };

    // Confirm actions
    $scope.confirmAction = function () {
        $scope.state.isBusy = true;
        $scope.state.busyText = "Processing...";

        switch ($scope.state.dialogType) {
            case 'edit':
                messageHub.postMessage('edit-row', $scope.data.selectedRow, true);
                break;
            case 'delete':
                messageHub.postMessage('delete-row', $scope.data.selectedRow, true);
                break;
            case 'create':
                messageHub.postMessage('create-row', $scope.data.newRow, true);
                break;
        }

        $scope.closeDialog();
    };
}]);
