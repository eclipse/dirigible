var messageHub = new FramesMessageHub();
var topic = 'fileselected';

angular.module('preview', []).controller('PreviewController', function ($scope, $http) {

	$scope.refresh = function() {
		var url = $scope.previewUrl;
		url = url.indexOf('?refreshToken') > 0 ? url.substring(0, url.indexOf('?refreshToken')) : url;
		$scope.previewUrl = url + '?refreshToken=' + new Date().getTime();
	};

	messageHub.subscribe(function(msg) {
		var resourcePath = msg.data.path.substring(msg.data.path.indexOf('/', 1))
		var url = window.top.location.protocol + '//' + window.top.location.host + '/services/v3';
		var type = resourcePath.substring(resourcePath.lastIndexOf('.') + 1);
		switch(type) {
			case 'rhino':
				url += '/rhino';
				break;
			case 'nashorn':
				url += '/nashorn';
				break;
			case 'v8':
				url += 'v8';
				break;
			case 'js':
				url += '/js';
				break;
			default:
				url += '/web';
		}
		url += resourcePath;
		$scope.previewUrl = url;
		$scope.$apply();
	}, topic);

}).config(function($sceProvider) {
    $sceProvider.enabled(false);
});
