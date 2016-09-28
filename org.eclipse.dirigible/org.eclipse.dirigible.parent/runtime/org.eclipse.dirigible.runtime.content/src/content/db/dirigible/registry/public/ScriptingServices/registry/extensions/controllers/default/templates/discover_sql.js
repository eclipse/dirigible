/*globals controllers */

controllers.controller('SQLCtrl', function($scope, $resource) {
  $scope.restService = $resource('../../scripting/sql');
});