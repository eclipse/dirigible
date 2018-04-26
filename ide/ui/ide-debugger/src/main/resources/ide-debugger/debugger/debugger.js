/**
 * Utility URL builder
 */
var UriBuilder = function UriBuilder(){
	this.pathSegments = [];
	return this;
}
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
}
UriBuilder.prototype.build = function(){
	var uriPath = '/'+this.pathSegments.join('/');
	return uriPath;
}

/**
 * Debugger Service API delegate
 */
var DebuggerService = function($http, debuggerServiceUrl){
	this.debuggerServiceUrl = debuggerServiceUrl;
	this.$http = $http;
}
DebuggerService.prototype.refresh = function(){
	var url = new UriBuilder().path(this.debuggerServiceUrl.split('/')).path("sessions").build();
	return this.$http.post(url)
			.then(function(response){
				alert('Sessions: ' + JSON.stringify(response.data));
				return response.data;
			});
}

angular.module('debugger.config', [])
	.constant('DEBUGGER_SVC_URL','/services/v3/ide/debug/rhino')
	
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
	var message = function(evtName, data){
		messageHub.post({data: data}, 'debugger.' + evtName);
	};
	var announceFileSelected = function(fileDescriptor){
		this.message('file.selected', fileDescriptor);
	};
	var announceFileCreated = function(fileDescriptor){
		this.message('file.created', fileDescriptor);
	};
	var announceFileOpen = function(fileDescriptor){
		this.message('file.open', fileDescriptor);
	};
	var announcePull = function(fileDescriptor){
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
.controller('DebuggerController', ['debuggerService', function (debuggerService) {
	
	this.refresh = function(){
		debuggerService.refresh();
	};
	
}]);