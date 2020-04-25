/*
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
/**
 * Utility URL builder
 */
var UriBuilder = function UriBuilder() {
	this.pathSegments = [];
	return this;
}
UriBuilder.prototype.path = function (_pathSegments) {
	if (!Array.isArray(_pathSegments))
		_pathSegments = [_pathSegments];
	_pathSegments = _pathSegments.filter(function (segment) {
		return segment;
	})
		.map(function (segment) {
			if (segment.length) {
				if (segment.charAt(segment.length - 1) === '/')
					segment = segment.substring(0, segment.length - 2);
				segment = encodeURIComponent(segment);
			}
			return segment;
		});
	this.pathSegments = this.pathSegments.concat(_pathSegments);
	return this;
}
UriBuilder.prototype.build = function () {
	return this.pathSegments.join('/');
}

angular.module('git.config', [])
	.constant('GIT_SVC_URL', '../../../../../services/v4/ide/git');


/**
 * Git Service API delegate
 */
var GitService = function ($http, gitServiceUrl) {
	this.gitServiceUrl = gitServiceUrl;
	this.$http = $http;
}

GitService.prototype.history = function (workspace, project, file) {
	var url = new UriBuilder().path(this.gitServiceUrl.split('/')).path(workspace).path(project).path("history").build();
	if (file) {
		url += "?path=" + file;
	}
	return this.$http.get(url);
}

var historyApp = angular.module('historyApp', ['git.config', 'ngAnimate', 'ngSanitize', 'ui.bootstrap'])

	.factory('httpRequestInterceptor', function () {
		var csrfToken = null;
		return {
			request: function (config) {
				config.headers['X-Requested-With'] = 'Fetch';
				config.headers['X-CSRF-Token'] = csrfToken ? csrfToken : 'Fetch';
				return config;
			},
			response: function (response) {
				var token = response.headers()['x-csrf-token'];
				if (token) {
					csrfToken = token;
				}
				return response;
			}
		};
	})
	.config(['$httpProvider', function ($httpProvider) {
		//check if response is error. errors currently are non-json formatted and fail too early
		$httpProvider.defaults.transformResponse.unshift(function (data, headersGetter, status) {
			if (status > 399) {
				data = {
					"error": data
				}
				data = JSON.stringify(data);
			}
			return data;
		});
		$httpProvider.interceptors.push('httpRequestInterceptor');
	}])
	.factory('$messageHub', [function () {
		var messageHub = new FramesMessageHub();

		return {
			on: function (evt, cb) {
				messageHub.subscribe(cb, evt);
			}
		};
	}])
	.factory('gitService', ['$http', 'GIT_SVC_URL', function ($http, GIT_SVC_URL) {
		return new GitService($http, GIT_SVC_URL);
	}])
	.controller('HistoryContoller', ['gitService', '$messageHub', '$http', '$scope', function (gitService, $messageHub, $http, $scope) {

		this.gitService = gitService;
		this.http = $http;
		this.scope = $scope;

		this.refresh = function () {
			if (!this.selectedWorkspace || !this.selectedProject) {
				this.history = [];
				this.scope.$apply();
				return;
			}
			gitService.history(this.selectedWorkspace, this.selectedProject)
				.then(function (response) {
					this.history = response.data;
					this.history.map(e => e.shortId = e.id.substring(0, 7));
					try {
						this.scope.$apply();
					} catch (e) {
						//
					}
				}.bind(this));
		};

		$messageHub.on('git.repository.selected', function (msg) {
			if (msg.data.isGitProject) {
				this.selectedWorkspace = msg.data.workspace;
				this.selectedProject = msg.data.project;
			} else {
				this.selectedProject = null;
			}
			this.refresh();
		}.bind(this));

	}]);

