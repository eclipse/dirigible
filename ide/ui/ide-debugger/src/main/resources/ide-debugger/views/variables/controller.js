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
 * Variables Service API delegate
 */
var VariablesService = function($http, variablesServiceUrl) {
	this.variablesServiceUrl = variablesServiceUrl;
	this.$http = $http;
};
VariablesService.prototype.refresh = function() {
	return this.$http.get(this.variablesServiceUrl);
};

angular.module('variables.config', [])
	.constant('VARIABLES_SVC_URL','../../../../../../services/v4/ide/debug/rhino/session/variables');
	
angular.module('variables', ['variables.config', 'ngAnimate', 'ngSanitize', 'ui.bootstrap'])
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
		messageHub.post({data: data}, 'variables.' + evtName);
	};
	var on = function(topic, callback) {
		messageHub.subscribe(callback, topic);
	};
	var onDebugVariables = function(callback) {
		this.on('debugger.debug.variables', callback);
	};
	var onDebugDisabled = function(callback) {
		this.on('debugger.debug.disabled', callback);
	};
	return {
		message: message,
		on: on,
		onDebugVariables: onDebugVariables,
		onDebugDisabled: onDebugDisabled
	};
}])
.factory('variablesService', ['$http', 'VARIABLES_SVC_URL', function($http, VARIABLES_SVC_URL){
	return new VariablesService($http, VARIABLES_SVC_URL);
}])
.controller('VariablesController', ['$scope', '$messageHub', 'variablesService', function ($scope, $messageHub, variablesService) {

	$scope.refresh = function() {
		variablesService.refresh().success(function(data) {
			$scope.variables = data.variableValueList;
		});
	};

	$messageHub.onDebugVariables($scope.refresh);

	$messageHub.onDebugDisabled(function() {
		$scope.variables = [];
	});

}]);
