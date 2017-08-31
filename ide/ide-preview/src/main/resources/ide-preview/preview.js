angular.module('preview', [])
.factory('$messageHub', [function(){
	var messageHub = new FramesMessageHub();	
	var message = function(evtName, data){
		messageHub.post({data: data}, 'workspace.' + evtName);
	};
	var on = function(topic, callback){
		messageHub.subscribe(callback, topic);
	};
	return {
		message: message,
		on: on
	};
}])
.controller('PreviewController', ['$scope', '$http', '$messageHub', function ($scope, $http, $messageHub) {

	this.refresh = function() {
		var url = this.previewUrl;
		url = url.indexOf('?refreshToken') > 0 ? url.substring(0, url.indexOf('?refreshToken')) : url;
		this.previewUrl = url + '?refreshToken=' + new Date().getTime();
	};

	$messageHub.on('workspace.file.open', function(msg) {
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
		this.previewUrl = url;
		$scope.$apply();
	}.bind(this));

}]).config(function($sceProvider) {
    $sceProvider.enabled(false);
});
