(function (angular) {
	"use strict";

	angular.module('discussion-boards')
		.factory('httpRequestInterceptor', function () {
			let csrfToken = null;
			return {
				request: function (config) {
					config.headers['X-Requested-With'] = 'Fetch';
					config.headers['X-CSRF-Token'] = csrfToken ? csrfToken : 'Fetch';
					return config;
				},
				response: function (response) {
					let token = response.headers()['x-csrf-token'];
					if (token) {
						csrfToken = token;
					}
					return response;
				}
			};
		})
		.config(['$httpProvider', function ($httpProvider) {
			$httpProvider.interceptors.push('httpRequestInterceptor');
		}])
		.service('Board', ['$resource', '$log', function ($resource, $log) {
			return $resource('/services/v4/js/ide-discussions/svc/board.js/:boardId', { boardId: '@id' }, {
				save: {
					method: 'POST',
					interceptor: {
						response: function (res) {
							let location = res.headers('Location');
							if (location) {
								let id = location.substring(location.lastIndexOf('/') + 1);
								angular.extend(res, { "id": id });
							} else {
								$log.error('Cannot infer id after save operation. HTTP Response Header "Location" is missing: ' + location);
							}
							return res;
						}
					},
					isArray: false
				},
				query: {
					method: 'GET',
					interceptor: {
						response: function (res) {
							let _count = res.headers('X-dservice-list-count');
							if (_count !== undefined) {
								_count = parseInt(_count, 10);
								res.resource.$count = _count;
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
		.service('BoardCount', ['$resource', function ($resource) {
			return $resource('/services/v4/js/ide-discussions/svc/board.js/count', {},
				{ get: { method: 'GET', params: {}, isArray: false, ignoreLoadingBar: true } });
		}])
		.service('BoardVote', ['$resource', function ($resource) {
			return $resource('/services/v4/js/ide-discussions/svc/board.js/:boardId/vote', {},
				{ get: { method: 'GET', params: {}, isArray: false, ignoreLoadingBar: true } },
				{ save: { method: 'POST', params: {}, isArray: false, ignoreLoadingBar: true } });
		}])
		.service('BoardVisits', ['$resource', function ($resource) {
			return $resource('/services/v4/js/ide-discussions/svc/board.js/:boardId/visit', {},
				{ update: { method: 'PUT', params: {}, isArray: false, ignoreLoadingBar: true } });
		}])
		.service('BoardTags', ['$resource', function ($resource) {
			return $resource('/services/v4/js/ide-discussions/svc/board.js/:boardId/tags', {},
				{
					get: { method: 'GET', params: {}, isArray: true, ignoreLoadingBar: true },
					save: { method: 'POST', params: {}, isArray: true, ignoreLoadingBar: true },
					remove: { method: 'DELETE', params: {}, isArray: true, ignoreLoadingBar: true }
				});
		}])
		.service('BoardComments', ['$resource', function ($resource) {
			return $resource('/services/v4/js/ide-discussions/svc/comment.js', {},
				{ get: { method: 'GET', params: {}, isArray: true, ignoreLoadingBar: true } });
		}])
		.service('$Comment', ['$resource', '$log', function ($resource, $log) {
			return $resource('/services/v4/js/ide-discussions/svc/comment.js/:commentId', { commentId: '@id' }, {
				save: {
					method: 'POST',
					interceptor: {
						response: function (res) {
							let location = res.headers('Location');
							if (location) {
								let id = location.substring(location.lastIndexOf('/') + 1);
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
		.service('$Tags', ['$resource', function ($resource) {
			return $resource('/services/v4/js/ide-discussions/svc/tags.js', {},
				{ get: { method: 'GET', params: {}, isArray: true, ignoreLoadingBar: true } });
		}]);
})(angular);