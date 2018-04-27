/**
 * Utility URL builder
 */
var UriBuilder = function UriBuilder(){
	this.pathSegments = [];
	return this;
};

UriBuilder.prototype.path = function(_pathSegments){
	if(!Array.isArray(_pathSegments))
		_pathSegments = [_pathSegments];
	_pathSegments = _pathSegments.filter(function(segment){
			return segment;
		})
		.map(function(segment){
			if(segment.length){
				if(segment.charAt(segment.length-1) ==='/')
					segment = segment.substring(0, segment.length-2);
				segment = encodeURIComponent(segment);
			} 
			return segment;
		});
	this.pathSegments = this.pathSegments.concat(_pathSegments);
	return this;
};
UriBuilder.prototype.build = function(){
	var uriPath = '/' + this.pathSegments.join('/');
	return uriPath;
};

/**
 * Debugger Service API delegate
 */
var DebuggerService = function($http, debuggerServiceUrl) {
	this.debuggerServiceUrl = debuggerServiceUrl;
	this.$http = $http;
};
DebuggerService.prototype.enable = function() {
	var url = new UriBuilder().path(this.debuggerServiceUrl.split('/')).path("enable").build();
	this.$http.get(url).then(function() {
		var wsUrl = window.location.protocol === 'https:' ? 'wss' : 'ws' + '://' + window.location.host + '/websockets/v3/ide/debug/sessions';
		new WebSocket(wsUrl);
	});
};
DebuggerService.prototype.disable = function() {
	var url = new UriBuilder().path(this.debuggerServiceUrl.split('/')).path('disable').build();
	this.$http.get(url);
};
DebuggerService.prototype.refresh = function() {
	var url = new UriBuilder().path(this.debuggerServiceUrl.split('/')).path('sessions').build();
	return this.$http.get(url);
};
DebuggerService.prototype.continue = function() {
	var url = new UriBuilder().path(this.debuggerServiceUrl.split('/')).path('session').path('continue').build();
	this.$http.get(url).then();
};
DebuggerService.prototype.pause = function() {
	var url = new UriBuilder().path(this.debuggerServiceUrl.split('/')).path('session').path('pause').build();
	this.$http.get(url).then();
};
DebuggerService.prototype.stepInto = function() {
	var url = new UriBuilder().path(this.debuggerServiceUrl.split('/')).path('session').path('stepInto').build();
	this.$http.get(url).then();
};
DebuggerService.prototype.stepOver = function() {
	var url = new UriBuilder().path(this.debuggerServiceUrl.split('/')).path('session').path('stepOver').build();
	this.$http.get(url).then();
};

angular.module('debugger.config', [])
	.constant('DEBUGGER_SVC_URL','/services/v3/ide/debug/rhino');
	
angular.module('debugger', ['debugger.config', 'ngAnimate', 'ngSanitize', 'ui.bootstrap'])
.config(['$httpProvider', function($httpProvider) {
	//check if response is error. errors currently are non-json formatted and fail too early
	$httpProvider.defaults.transformResponse.unshift(function(data, headersGetter, status){
		if(status>399){
			data = {
				"error": data
			}
			data = JSON.stringify(data);
		}
		return data;
	});
}])
.factory('$messageHub', [function(){
	var messageHub = new FramesMessageHub();	
	var message = function(evtName, data) {
		messageHub.post({data: data}, 'debugger.' + evtName);
	};
	var announceFileSelected = function(fileDescriptor) {
		this.message('file.selected', fileDescriptor);
	};
	var announceFileCreated = function(fileDescriptor) {
		this.message('file.created', fileDescriptor);
	};
	var announceFileOpen = function(fileDescriptor) {
		this.message('file.open', fileDescriptor);
	};
	var announcePull = function(fileDescriptor) {
		this.message('file.pull', fileDescriptor);
	};

	return {
		message: message,
		announceFileSelected: announceFileSelected,
		announceFileCreated: announceFileCreated,
		announceFileOpen: announceFileOpen,
		announcePull: announcePull
	};
}])
.factory('debuggerService', ['$http', 'DEBUGGER_SVC_URL', function($http, DEBUGGER_SVC_URL){
	return new DebuggerService($http, DEBUGGER_SVC_URL);
}])
.controller('DebuggerController', ['$scope', 'debuggerService', function ($scope, debuggerService) {

	$scope.debugEnabled = false;

	$scope.enable = function() {
		$scope.debugEnabled = !$scope.debugEnabled;
		if ($scope.debugEnabled) {
			debuggerService.enable();
		} else {
			debuggerService.disable();
		}
	};

	$scope.refresh = function() {
		if ($scope.debugEnabled) {
			debuggerService.refresh().then(function(response) {
				$scope.sessions = response.data;
			});
		}
	};

	$scope.continue = function() {
		if ($scope.debugEnabled) {
			debuggerService.continue();
		}

	};

	$scope.pause = function() {
		if ($scope.debugEnabled) {
			debuggerService.pause();
		}
	};

	$scope.stepInto = function() {
		if ($scope.debugEnabled) {
			debuggerService.stepInto();
		}
	};

	$scope.stepOver = function() {
		if ($scope.debugEnabled) {
			debuggerService.stepOver();
		}

	};
}]);