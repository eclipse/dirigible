/*globals controllers */
/*eslint-env browser */

controllers.controller('ListenerCtrl', function($scope, $resource) {
  $scope.restService = $resource('../../flow/listener');
});
