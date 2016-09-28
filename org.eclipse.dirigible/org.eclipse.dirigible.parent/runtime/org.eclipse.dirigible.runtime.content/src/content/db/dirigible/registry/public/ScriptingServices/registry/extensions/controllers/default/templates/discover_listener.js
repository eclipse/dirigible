/*globals controllers */

controllers.controller('ListenerCtrl', function($scope, $resource) {
  $scope.restService = $resource('../../flow/listener');
});