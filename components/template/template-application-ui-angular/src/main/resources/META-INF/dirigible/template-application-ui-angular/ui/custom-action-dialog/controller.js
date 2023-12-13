angular.module('page', ["ideUI", "ideView", "entityApi"])
    .config(["messageHubProvider", function (messageHubProvider) {
        messageHubProvider.eventIdPrefix = 'test.entities.Student';
    }])
    .controller('PageController', ['$scope', 'ViewParameters', function ($scope, ViewParameters) {

        $scope.src = ViewParameters.get().src;

    }]);