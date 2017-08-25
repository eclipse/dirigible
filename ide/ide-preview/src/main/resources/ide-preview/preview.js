var messageHub = new FramesMessageHub();
var topic = 'fileselected';

angular.module('preview', []).controller('PreviewController', function ($scope, $http) {

	messageHub.subscribe(function(msg) {
		var resourcePath = msg.data.path.substring(msg.data.path.indexOf('/', 1))
		var url = window.top.location.protocol + '//' + window.top.location.host + '/services/v3';
		var type = resourcePath.substring(resourcePath.lastIndexOf('.') + 1);
		switch(type) {
			case 'js':
				url += '/js';
				break;
			case 'html':
				url += '/web';
				break;
			default:
				return;
		}
		url += resourcePath;
		$scope.previewUrl = url;
		$scope.$apply();
	}, topic);

}).config(function($sceProvider) {
    $sceProvider.enabled(false);
});
