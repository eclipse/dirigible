const crudDialog = angular.module('crudDialog', ['ideUI', 'ideView']);

crudDialog.controller('CRUDDialogController', ['$scope', 'messageHub', 'ViewParameters', function ($scope, messageHub, ViewParameters) {
    // State management
    $scope.state = {
        dialogType: null, // 'edit', 'delete', 'create'
        isBusy: true,
        error: false,
        busyText: "Loading...",
    };

    $scope.forms = {
        crudForm: {},
    };

    $scope.dialogType = null;

    // Data
    $scope.data = {
        selectedRow: {},
        newRow: {},
    };

    // Close dialog
    $scope.cancel = function () {
        messageHub.closeDialogWindow('result-view-crud');
    };

    $scope.dataParameters = ViewParameters.get();
    if (!$scope.dataParameters.hasOwnProperty('dialogType')) {
        $scope.state.error = true;
        $scope.errorMessage = "The 'type' parameter is missing.";
    } else {
        $scope.dialogType = $scope.dataParameters.dialogType;
        if ($scope.type == "edit" || $scope.type == "delete") {
            if (!$scope.dataParameters.hasOwnProperty('data')) {
                $scope.state.error = true;
                $scope.errorMessage = "The 'data' parameter is missing.";
            } else {
                $scope.data.selectedRow = $scope.dataParameters.data;
            }
        } else $scope.data.newRow = $scope.dataParameters.data;
        console.log($scope.data);
        console.log($scope.dialogType);
        $scope.state.isBusy = false;
    }

    // Confirm actions
    $scope.confirmAction = function () {
        $scope.state.isBusy = true;
        $scope.state.busyText = "Processing...";

        switch ($scope.state.dialogType) {
            case 'create':
                messageHub.postMessage('create-row', $scope.data.newRow, true);
                break;
            case 'edit':
                messageHub.postMessage('edit-row', $scope.data.selectedRow, true);
                break;
            case 'delete':
                messageHub.postMessage('delete-row', $scope.data.selectedRow, true);
                break;
        }

        $scope.closeDialog();
    };
}]);
