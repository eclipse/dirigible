angular.module('dialogManager', ['ideUI', 'ideView']);

angular.module('dialogManager').controller('DialogController', ['$scope', 'messageHub', function ($scope, messageHub) {
    // State management
    $scope.state = {
        isDialogOpen: false,
        dialogType: null, // 'edit', 'delete', 'create'
        isBusy: false,
        error: false,
        busyText: "",
    };

    // Data
    $scope.data = {
        selectedRow: {},
        newRow: {},
    };

    // Open dialog
    messageHub.onDidReceiveMessage('open-dialog', function (msg) {
        if (msg.data) {
            $scope.openDialog(msg.data.type, msg.data.data || null);
        }
    });
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
