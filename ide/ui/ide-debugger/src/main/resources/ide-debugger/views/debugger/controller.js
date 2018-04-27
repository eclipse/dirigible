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
DebuggerService.prototype.activateSession = function(sessionId) {
	var url = new UriBuilder().path(this.debuggerServiceUrl.split('/')).path('session').path('activate').path(sessionId).build();
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
	var announceDebugEnabled = function() {
		this.message('debug.enabled');
	};
	var announceDebugDisabled = function() {
		this.message('debug.disabled');
	};
	var announceDebugRefresh = function() {
		this.message('debug.refresh');
	};
	var announceDebugContinue = function() {
		this.message('debug.continue');
	};
	var announceDebugPause = function() {
		this.message('debug.pause');
	};
	var announceDebugStepInto = function() {
		this.message('debug.stepInto');
	};
	var announceDebugStepOver = function() {
		this.message('debug.stepOver');
	};
	return {
		message: message,
		announceDebugEnabled: announceDebugEnabled,
		announceDebugDisabled: announceDebugDisabled,
		announceDebugRefresh: announceDebugRefresh,
		announceDebugContinue: announceDebugContinue,
		announceDebugPause: announceDebugPause,
		announceDebugStepInto: announceDebugStepInto,
		announceDebugStepOver: announceDebugStepOver
	};
}])
.factory('debuggerService', ['$http', 'DEBUGGER_SVC_URL', function($http, DEBUGGER_SVC_URL){
	return new DebuggerService($http, DEBUGGER_SVC_URL);
}])
.controller('DebuggerController', ['$scope', '$messageHub', 'debuggerService', function ($scope, $messageHub, debuggerService) {

	$scope.debugEnabled = false;

	$scope.enable = function() {
		$scope.debugEnabled = !$scope.debugEnabled;
		if ($scope.debugEnabled) {
			debuggerService.enable();
			$messageHub.announceDebugEnabled();
		} else {
			debuggerService.disable();
			$messageHub.announceDebugDisabled();
			$scope.sessions = [];
		}
	};

	$scope.refresh = function() {
		if ($scope.debugEnabled) {
			debuggerService.refresh().then(function(response) {
				$scope.sessions = response.data;
				$messageHub.announceDebugRefresh();
			});
		}
	};

	$scope.continue = function() {
		if ($scope.debugEnabled) {
			debuggerService.continue();
			$messageHub.announceDebugContinue();
			$scope.refresh();
		}
	};

	$scope.pause = function() {
		if ($scope.debugEnabled) {
			debuggerService.pause();
			$messageHub.announceDebugPause();
			$scope.refresh();
		}
	};

	$scope.stepInto = function() {
		if ($scope.debugEnabled) {
			debuggerService.stepInto();
			$messageHub.announceDebugStepInto();
			$scope.refresh();
		}
	};

	$scope.stepOver = function() {
		if ($scope.debugEnabled) {
			debuggerService.stepOver();
			$messageHub.announceDebugStepOver();
			$scope.refresh();
		}
	};

	$scope.activateSession = function(session) {
		if ($scope.debugEnabled) {
			debuggerService.activateSession(session.sessionId).success(function() {
				$scope.refresh();
			});
		}
	};
}]);