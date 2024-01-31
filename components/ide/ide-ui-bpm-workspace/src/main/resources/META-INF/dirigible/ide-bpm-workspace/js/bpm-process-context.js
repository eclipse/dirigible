let ideBpmProcessContextView = angular.module('ide-bpm-process-context', ['ideUI', 'ideView']);

ideBpmProcessContextView.config(["messageHubProvider", function (messageHubProvider) {
    messageHubProvider.eventIdPrefix = 'bpm';
}]);

ideBpmProcessContextView.controller('IDEBpmProcessContextViewController', ['$scope', '$http', '$timeout', 'messageHub', function ($scope, $http, $timeout, messageHub,) {

    this.variablesList = [];
    this.currentProcessInstanceId = null;

    this.reload = function () {
        console.log("Reloading data for current process instance id: " + this.currentProcessInstanceId)
        this.fetchData(this.currentProcessInstanceId);
    };

    this.fetchData = function(processInstanceId) {
        $http.get('/services/ide/bpm/bpm-processes/instance/' + processInstanceId + '/variables', { params: { 'limit': 100 } })
                .then((response) => {
                    $scope.variables.variablesList = response.data;
                });
    }

    this.getNoDataMessage = function () {
        return this.filterBy ? 'No variables found.' : 'No variables have been detected.';
    }

    messageHub.onDidReceiveMessage('instance.selected', function (msg) {
        $scope.$apply(function () {
            if (!msg.data.hasOwnProperty('instance')) {
                $scope.state.error = true;
                $scope.errorMessage = "The 'definition' parameter is missing.";
            } else {
                var processInstanceId = msg.data.instance;
                $scope.variables.currentProcessInstanceId = processInstanceId;
                $scope.variables.fetchData(processInstanceId);
            }
        });
    });
}]);