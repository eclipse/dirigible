
/**
 * Breakpoints Service API delegate
 */
var BreakpointsService = function($http, breakpointsServiceUrl) {
	this.breakpointsServiceUrl = breakpointsServiceUrl;
	this.$http = $http;
};
BreakpointsService.prototype.refresh = function() {
	return this.$http.get(this.breakpointsServiceUrl);
};

angular.module('breakpoints.config', [])
	.constant('BREAKPOINTS_SVC_URL','/services/v3/ide/debug/rhino/breakpoints');
	
angular.module('breakpoints', ['breakpoints.config', 'ngAnimate', 'ngSanitize', 'ui.bootstrap'])
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
		messageHub.post({data: data}, 'breakpoints.' + evtName);
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
.factory('breakpointsService', ['$http', 'BREAKPOINTS_SVC_URL', function($http, BREAKPOINTS_SVC_URL){
	return new BreakpointsService($http, BREAKPOINTS_SVC_URL);
}])
.controller('BreakpointsController', ['$scope', 'breakpointsService', function ($scope, breakpointsService) {

	$scope.refresh = function() {
		breakpointsService.refresh().success(function(data) {
			$scope.breakpoints = data.breakpointsList;
		});
	};
}]);