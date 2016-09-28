/*globals controllers */

controllers.controller('FlowCtrl', function($scope, $resource) {
  $scope.restService = $resource('../../flow/flow');
});