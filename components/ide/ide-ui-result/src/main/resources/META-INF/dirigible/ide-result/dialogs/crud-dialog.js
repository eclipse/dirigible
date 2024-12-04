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

    $scope.inputRules = {
        patterns: ['^(?! ).*(?<! )$']
    };

    $scope.dialogType = null;

    // Data
    $scope.data = {
        row: {},
        primaryKeys: null,
        specialKeys: null
    };

    $scope.cancel = function () {
        messageHub.closeDialogWindow('result-view-crud');
        $scope.data.row = {};
        $scope.data.primaryKeys = null;
        $scope.data.specialKeys = null;
        $scope.dialogType = null;
    };

    $scope.dataParameters = ViewParameters.get();
    if (!$scope.dataParameters.hasOwnProperty('dialogType')) {
        $scope.state.error = true;
        $scope.errorMessage = "The 'type' parameter is missing.";
    } else {
        $scope.dialogType = $scope.dataParameters.dialogType;
        if (!$scope.dataParameters.hasOwnProperty('data')) {
            $scope.state.error = true;
            $scope.errorMessage = "The 'data' parameter is missing.";
        } else {
            if ($scope.dataParameters.dialogType == "edit") {
                $scope.data.row = $scope.dataParameters.data.row;
                $scope.data.primaryKeys = $scope.dataParameters.data.primaryKeys
                    ? $scope.dataParameters.data.primaryKeys
                    : null;
                $scope.data.specialKeys = $scope.dataParameters.data.specialKeys
                    ? $scope.dataParameters.data.specialKeys
                    : null;
            } else {
                $scope.data.row = $scope.dataParameters.data;
            }
        }
        console.log($scope.data);
        $scope.state.isBusy = false;
    }

    $scope.confirmAction = function () {
        $scope.state.isBusy = true;
        $scope.state.busyText = "Processing...";

        switch ($scope.dialogType) {
            case 'create':
                messageHub.postMessage('create-row', $scope.data.row, true);
                break;
            case 'edit':
                messageHub.postMessage('edit-row', $scope.data.row, true);
                break;
            case 'delete':
                messageHub.postMessage('delete-row', $scope.data.row, true);
                break;
        }

        $scope.closeDialog();
    };

    $scope.isPrimaryOrSpecialKey = function (key) {
        return ($scope.data.primaryKeys && $scope.data.primaryKeys.includes(key)) ||
            ($scope.data.specialKeys && $scope.data.specialKeys.includes(key));
    };

}]);
