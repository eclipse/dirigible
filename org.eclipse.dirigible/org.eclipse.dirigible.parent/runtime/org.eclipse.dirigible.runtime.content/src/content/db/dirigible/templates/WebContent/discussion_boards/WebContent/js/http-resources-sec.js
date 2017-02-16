#set( $D = '$' )
(function(angular){
"use strict";

	angular.module('discussion-boards')
	.service('SecureBoard', ['${D}resource', '${D}log', function(${D}resource, ${D}log) {
	  	return ${D}resource('../../js-secured/${packageName}/svc/user/board.js/:boardId', { boardId:'@id' }, {
			    save: {
			        method: 'POST',
			        interceptor: {
		                response: function(res) {
		                	var location = res.headers('Location');
		                	if(location){
		                		var id = location.substring(location.lastIndexOf('/')+1);
		                		angular.extend(res, { "id": id });
	                		} else {
	                			${D}log.error('Cannot infer id after save operation. HTTP Response Header "Location" is missing: ' + location);
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
	.service('SecureBoardVote', ['${D}resource', function(${D}resource) {
	  	return ${D}resource('../../js-secured/${packageName}/svc/user/board.js/:boardId/vote', {}, 
	  			{get: {method:'GET', params:{}, isArray:false, ignoreLoadingBar: true}},
	  			{save: {method:'POST', params:{}, isArray:false, ignoreLoadingBar: true}});
	}])		
	.service('SecureBoardTags', ['${D}resource', function(${D}resource) {
	  	return ${D}resource('../../js-secured/${packageName}/svc/user/board.js/:boardId/tags', {}, 
	  			{
	  				get: {method:'GET', params:{}, isArray:true, ignoreLoadingBar: true},
	  				save: {method:'POST', params:{}, isArray:true, ignoreLoadingBar: true},
	  				remove: {method:'DELETE', params:{}, isArray:true, ignoreLoadingBar: true}
	  			});
	}])		
	.service('${D}SecureComment', ['${D}resource', '${D}log', function(${D}resource, ${D}log) {
	 	return ${D}resource('../../js-secured/${packageName}/svc/user/comment.js/:commentId', { commentId:'@id' }, {
			    save: {
			        method: 'POST',
			        interceptor: {
		                response: function(res) {
		                	var location = res.headers('Location');
		                	if(location){
		                		var id = location.substring(location.lastIndexOf('/')+1);
		                		angular.extend(res.resource, { "id": id });
	                		} else {
	                			${D}log.error('Cannot infer id after save operation. HTTP Response Header "Location" is missing: ' + location);
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
	.service('${D}LoggedUser', ['${D}resource', function(${D}resource) {
		var UserSvc =  ${D}resource('../../js/profile/user.js', {}, 
	  					{get: {method:'GET', params:{}, isArray:false, ignoreLoadingBar: true}});
	  	var get = function(){
		  	return UserSvc.get().${D}promise;
	  	};
	  	return {
	  		get: get
	  	};
	}])
	.service('${D}LoggedUserProfile', ['${D}resource', function($resource) {
		var UserSvc =  ${D}resource('../../js-secured/${packageName}/svc/user/profile.js/logout', {}, 
	  					{get: {method:'GET', params:{}, isArray:false, ignoreLoadingBar: true}});
	  	var logout = function(){
		  	return UserSvc.get().${D}promise;
	  	};
	  	return {
	  		logout: logout
	  	};
	}]);
})(angular);
