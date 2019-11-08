/*
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
/**
 * Breakpoints Service API delegate
 */
var BreakpointsService = function($http, breakpointsServiceUrl, breakpointServiceUrl) {
	this.breakpointsServiceUrl = breakpointsServiceUrl;
	this.breakpointServiceUrl = breakpointServiceUrl;
	this.$http = $http;
};
BreakpointsService.prototype.refresh = function() {
	return this.$http.get(this.breakpointsServiceUrl);
};
BreakpointsService.prototype.setBreakpoint = function(file, row) {
	var a = file.split('/');a.shift();a.shift();file = a.join('/');
	return this.$http.get(this.breakpointServiceUrl + "/set/" + row + '/' + file);
};
BreakpointsService.prototype.clearBreakpoint = function(file, row) {
	var a = file.split('/');a.shift();a.shift();file = a.join('/');
	return this.$http.get(this.breakpointServiceUrl + "/remove/" + row+ '/' + file);
};

angular.module('breakpoints.config', [])
	.constant('BREAKPOINTS_SVC_URL','../../../../../../services/v4/ide/debug/rhino/breakpoints')
	.constant('BREAKPOINT_SVC_URL','../../../../../../services/v4/ide/debug/rhino/breakpoint');
	
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
	var on = function(topic, callback) {
		messageHub.subscribe(callback, topic);
	};
	var onDebugBreakpointSet = function(callback) {
		this.on('debugger.editor.breakpoint.set', callback);
	};
	var onDebugBreakpointClear = function(callback) {
		this.on('debugger.editor.breakpoint.clear', callback);
	};

	return {
		message: message,
		on: on,
		onDebugBreakpointSet: onDebugBreakpointSet,
		onDebugBreakpointClear: onDebugBreakpointClear
	};
}])
.factory('breakpointsService', ['$http', 'BREAKPOINTS_SVC_URL', 'BREAKPOINT_SVC_URL', function($http, BREAKPOINTS_SVC_URL, BREAKPOINT_SVC_URL){
	return new BreakpointsService($http, BREAKPOINTS_SVC_URL, BREAKPOINT_SVC_URL);
}])
.controller('BreakpointsController', ['$scope', '$messageHub', 'breakpointsService', function ($scope, $messageHub, breakpointsService) {

	$scope.refresh = function() {
		breakpointsService.refresh().success(function(data) {
			$scope.breakpoints = data.breakpointsList;
		});
	};
	
	$messageHub.onDebugBreakpointSet(function(data) {
		breakpointsService.setBreakpoint(data.file, data.row).success($scope.refresh);
	});
	
	$messageHub.onDebugBreakpointClear(function(data) {
		breakpointsService.clearBreakpoint(data.file, data.row).success($scope.refresh);
	});
	
}]);
