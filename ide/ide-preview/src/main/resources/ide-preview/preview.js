var messageHub = new FramesMessageHub();
var topic = 'fileselected';

angular.module('preview', []).controller('PreviewController', function ($scope, $http) {

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
