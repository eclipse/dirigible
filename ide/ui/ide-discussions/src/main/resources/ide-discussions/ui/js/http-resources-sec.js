(function(angular){
"use strict";

	angular.module('discussion-boards')
	.service('SecureBoard', ['$resource', '$log', function($resource, $log) {
	  	return $resource('../../../js/ide-discussions/svc/user/board.js/:boardId', { boardId:'@id' }, {
			    save: {
			        method: 'POST',
			        interceptor: {
		                response: function(res) {
		                	var location = res.headers('Location');
		                	if(location){
		                		var id = location.substring(location.lastIndexOf('/')+1);
		                		angular.extend(res, { "id": id });
	                		} else {
	                			$log.error('Cannot infer id after save operation. HTTP Response Header "Location" is missing: ' + location);
	            			}
	                        return res;
		                }
		            }, 
		            isArray: false
			    },
			    update: {
			        method: 'PUT'
			    }
			});
	}])
	.service('SecureBoardVote', ['$resource', function($resource) {
	  	return $resource('../../../js/ide-discussions/svc/user/board.js/:boardId/vote', {}, 
	  			{get: {method:'GET', params:{}, isArray:false, ignoreLoadingBar: true}},
	  			{save: {method:'POST', params:{}, isArray:false, ignoreLoadingBar: true}});
	}])		
	.service('SecureBoardTags', ['$resource', function($resource) {
	  	return $resource('../../../js/ide-discussions/svc/user/board.js/:boardId/tags', {}, 
	  			{
	  				get: {method:'GET', params:{}, isArray:true, ignoreLoadingBar: true},
	  				save: {method:'POST', params:{}, isArray:true, ignoreLoadingBar: true},
	  				remove: {method:'DELETE', params:{}, isArray:true, ignoreLoadingBar: true}
	  			});
	}])		
	.service('$SecureComment', ['$resource', '$log', function($resource, $log) {
	 	return $resource('../../../js/ide-discussions/svc/user/comment.js/:commentId', { commentId:'@id' }, {
			    save: {
			        method: 'POST',
			        interceptor: {
		                response: function(res) {
		                	var location = res.headers('Location');
		                	if(location){
		                		var id = location.substring(location.lastIndexOf('/')+1);
		                		angular.extend(res.resource, { "id": id });
	                		} else {
	                			$log.error('Cannot infer id after save operation. HTTP Response Header "Location" is missing: ' + location);
	            			}
	                        return res.resource;
		                }
		            }, 
		            isArray: false
			    },
			    update: {
			        method: 'PUT'
			    }
			});
	}])
	.service('$LoggedUser', ['$resource', function($resource) {
		var UserSvc =  $resource('../../../js/ide-discussions/svc/user/user.js', {}, //$resource('../../js/profile/user.js', {}, 
	  					{get: {method:'GET', params:{}, isArray:false, ignoreLoadingBar: true}});
	  	var get = function(){
		  	return UserSvc.get().$promise;
	  	};
	  	return {
	  		get: get
	  	};
	}])
	.service('$LoggedUserProfile', ['$resource', function($resource) {
		var UserSvc =  $resource('../../../js/ide-discussions/svc/user/profile.js/logout', {}, 
	  					{get: {method:'GET', params:{}, isArray:false, ignoreLoadingBar: true}});
	  	var logout = function(){
		  	return UserSvc.get().$promise;
	  	};
	  	return {
	  		logout: logout
	  	};
	}]);
})(angular);
