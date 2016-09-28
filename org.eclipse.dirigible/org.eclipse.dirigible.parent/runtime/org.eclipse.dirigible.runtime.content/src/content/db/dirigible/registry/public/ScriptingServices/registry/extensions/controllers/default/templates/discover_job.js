/*globals controllers */

controllers.controller('JobCtrl', function($scope, $resource) {
  $scope.restService = $resource('../../flow/job');
});