
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
	.constant('VARIABLES_SVC_URL','/services/v3/ide/debug/rhino/session/variables');
	
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
	var onDebugRefresh = function(callback) {
		this.on('debugger.debug.enabled', callback);
	};
	var onDebugContinue = function(callback) {
		this.on('debugger.debug.continue', callback);
	};
	var onDebugStepInto = function(callback) {
		this.on('debugger.debug.stepInto', callback);
	};
	var onDebugStepOver = function(callback) {
		this.on('debugger.debug.stepOver', callback);
	};
	return {
		message: message,
		on: on,
		onDebugRefresh: onDebugRefresh,
		onDebugContinue: onDebugContinue,
		onDebugStepInto: onDebugStepInto,
		onDebugStepOver: onDebugStepOver
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

	$messageHub.onDebugRefresh($scope.refresh);
	$messageHub.onDebugContinue($scope.refresh);
	$messageHub.onDebugStepInto($scope.refresh);
	$messageHub.onDebugStepOver($scope.refresh);
}]);