let ideBpmProcessContextView = angular.module('ide-bpm-process-context', ['ideUI', 'ideView', 'ui.bootstrap']);

ideBpmProcessContextView.config(["messageHubProvider", function (messageHubProvider) {
    messageHubProvider.eventIdPrefix = 'bpm';
}]);

ideBpmProcessContextView.controller('IDEBpmProcessContextViewController', ['$scope', '$http', '$timeout', 'messageHub', function ($scope, $http, $timeout, messageHub,) {

    $scope.variablesList = [];
    $scope.currentProcessInstanceId = null;

    $scope.reload = function () {
        console.log("Reloading data for current process instance id: " + $scope.currentProcessInstanceId)
        $scope.fetchData($scope.currentProcessInstanceId);
    };

    $scope.fetchData = function(processInstanceId) {
        $http.get('/services/ide/bpm/bpm-processes/instance/' + processInstanceId + '/variables', { params: { 'limit': 100 } })
                .then((response) => {
                    $scope.variablesList = response.data;
                });
    }

    $scope.addProcessVariable = function(processInstanceId, varName, varValue) {
        const apiUrl = '/services/ide/bpm/bpm-processes/instance/' + processInstanceId + '/variables';
        const requestBody = { 'name': varName, 'value': varValue };

        $http({
            method: 'POST',
            url: apiUrl,
            data: requestBody,
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then((response) => {
            console.log('Successfully added variable with name [' + varName + '] and value [' + varValue + ']');
            $scope.reload();
            messageHub.hideFormDialog("processContextVariableAdd");
        })
        .catch((error) => {
            console.error('Error making POST request:', error);
        });
    }

    $scope.getNoDataMessage = function () {
        return 'No variables have been detected.';
    }

    $scope.openAddDialog = function () {
        messageHub.showFormDialog(
            "processContextVariableAdd",
            "Add new process context variable",
            [{
                id: "prcva",
                type: "input",
                label: "Name",
                placeholder: 'Variable name'
            },
            {
                id: "prcvb",
                type: "input",
                label: "Value",
                placeholder: 'Variable value'
            }],
            [{
                id: "b1",
                type: "emphasized",
                label: "Add",
            },
            {
                id: "b2",
                type: "transparent",
                label: "Cancel",
            }],
            "bpm.dialogs.variable.add",
            "Applying data...",
            "",
            "The variable will be added to the context of the currently selected process instance"
        );
    }

    messageHub.onDidReceiveMessage(
        "bpm.dialogs.variable.add",
        function (msg) {
            if (msg.data.buttonId === "b1") {
                const varName = msg.data.formData[0].value;
                const varValue = msg.data.formData[1].value;
                $scope.addProcessVariable($scope.currentProcessInstanceId, varName, varValue);
            } else {
                messageHub.hideFormDialog("processContextVariableAdd");
            }
        },
        true
    );

    messageHub.onDidReceiveMessage('instance.selected', function (msg) {
        const processInstanceId = msg.data.instance;
        $scope.fetchData(processInstanceId);
        $scope.$apply(function () {
            $scope.currentProcessInstanceId = processInstanceId;
        });
    });
}]);