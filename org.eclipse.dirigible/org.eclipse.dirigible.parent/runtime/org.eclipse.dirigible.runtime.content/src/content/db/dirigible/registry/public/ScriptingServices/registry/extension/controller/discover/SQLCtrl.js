/*globals controllers */
/*eslint-env browser */

controllers.controller('SQLCtrl', function($scope, $resource) {
  $scope.restService = $resource('../../scripting/sql');
});
