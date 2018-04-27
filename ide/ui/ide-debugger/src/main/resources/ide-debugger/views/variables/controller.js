
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
.factory('variablesService', ['$http', 'VARIABLES_SVC_URL', function($http, VARIABLES_SVC_URL){
	return new VariablesService($http, VARIABLES_SVC_URL);
}])
.controller('VariablesController', ['$scope', 'variablesService', function ($scope, variablesService) {

	$scope.refresh = function() {
		variablesService.refresh().success(function(data) {
			$scope.variables = data.variableValueList;
		});
	};
}]);