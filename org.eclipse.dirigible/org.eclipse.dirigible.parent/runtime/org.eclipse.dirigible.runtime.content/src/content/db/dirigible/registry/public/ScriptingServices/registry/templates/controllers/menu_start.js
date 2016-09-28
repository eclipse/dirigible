/*globals angular menuControllers*/
/*eslint-env browser */

menuControllers.controller('MenuCtrl', ['$scope', '$http',
	function($scope) {
		$scope.menus = [];
