/*globals controllers */
/*eslint-env browser */

controllers.controller('JobCtrl', function($scope, $resource) {
  $scope.restService = $resource('../../flow/job');
});