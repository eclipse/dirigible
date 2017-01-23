(function(angular){
"use strict";

	angular.module('users', ['ngResource', 'angular-loading-bar', 'angularFileUpload'])
	
	.config(['cfpLoadingBarProvider', function(cfpLoadingBarProvider) {
		cfpLoadingBarProvider.includeSpinner = false;	    		
	}])
	
	.service('$LoggedUser', ['$resource', '$log', function($resource) {
		var UserSvc =  $resource('../../js/usr/svc/user.js/$current', {}, 
	  					{get: {method:'GET', params:{}, isArray:false, ignoreLoadingBar: true}});
	  	var get = function(){
		  	return UserSvc.get().$promise;
	  	};
	  	return {
	  		get: get
	  	};
	}])
	.service('$UserImg', ['$resource', '$log', function($resource) {
		var UserImgSvc = $resource('../../js/usr/svc/user.js/$pics/:userName', {}, 
	  					{get: {method:'GET', params:{}, isArray:false, cache: true, ignoreLoadingBar: false}});
		var get = function(userName){
		  	return UserImgSvc.get({"userName":userName}).$promise
		  	.then(function(userImgData){
		  		return userImgData;
		  	});
	  	};	  					
	  	return {
	  		get: get
	  	};	  					
	}])
	
	.directive('avatar', [function() {
		 return {
		    require: '^user',
		    restrict: 'AE',
		    templateUrl: 'views/avatar.html',
			scope: {
		        user : "="
		    }
		  };		
	}]);

})(angular);
