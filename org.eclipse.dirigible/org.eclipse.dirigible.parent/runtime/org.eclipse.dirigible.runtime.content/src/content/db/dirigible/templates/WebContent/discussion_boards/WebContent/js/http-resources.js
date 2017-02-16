#set( $D = '$' )
(function(angular){
"use strict";

	angular.module('discussion-boards')
	.service('Board', ['${D}resource', '${D}log', function(${D}resource, ${D}log) {
	  	return ${D}resource('../../js/${packageName}/svc/board.js/:boardId', { boardId:'@id' }, {
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
			    query : {
					method: 'GET',
			        interceptor: {
						response: function(res) {
		                	var _count= res.headers('X-dservice-list-count');
		                	if(_count!==undefined){
		                		_count = parseInt(_count, 10);
		                		res.resource.${D}count = _count;
	                		}
	                        return res.resource;
		                }
		            }, 
		            isArray: true
			    },
			    update: {
			        method: 'PUT'
			    }
			});
	}])
	.service('BoardCount', ['${D}resource', function(${D}resource) {
	  	return ${D}resource('../../js/${packageName}/svc/board.js/count', {}, 
	  			{get: {method:'GET', params:{}, isArray:false, ignoreLoadingBar: true}});
	}])	
	.service('BoardVote', ['${D}resource', function(${D}resource) {
	  	return ${D}resource('../../js/${packageName}/svc/board.js/:boardId/vote', {}, 
	  			{get: {method:'GET', params:{}, isArray:false, ignoreLoadingBar: true}},
	  			{save: {method:'POST', params:{}, isArray:false, ignoreLoadingBar: true}});
	}])	
	.service('BoardVisits', ['${D}resource', function(${D}resource) {
	  	return ${D}resource('../../js/${packageName}/svc/board.js/:boardId/visit', {}, 
	  			{update: {method:'PUT', params:{}, isArray:false, ignoreLoadingBar: true}});
	}])		
	.service('BoardTags', ['${D}resource', function(${D}resource) {
	  	return ${D}resource('../../js/${packageName}/svc/board.js/:boardId/tags', {}, 
	  			{
	  				get: {method:'GET', params:{}, isArray:true, ignoreLoadingBar: true},
	  				save: {method:'POST', params:{}, isArray:true, ignoreLoadingBar: true},
	  				remove: {method:'DELETE', params:{}, isArray:true, ignoreLoadingBar: true}
	  			});
	}])	
	.service('BoardComments', ['${D}resource', function(${D}resource) {
	  	return ${D}resource('../../js/${packageName}/svc/comment.js', {}, 
	  			{get: {method:'GET', params:{}, isArray:true, ignoreLoadingBar: true}});
	}])		
	.service('${D}Comment', ['${D}resource', '${D}log', function(${D}resource, ${D}log) {
	 	return ${D}resource('../../js/${packageName}/svc/comment.js/:commentId', { commentId:'@id' }, {
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
	.service('${D}Tags', ['${D}resource', function(${D}resource) {
	  	return ${D}resource('../../js/annotations/svc/tags.js', {}, 
	  					{get: {method:'GET', params:{}, isArray:true, ignoreLoadingBar: true}});
	}]);
})(angular);
