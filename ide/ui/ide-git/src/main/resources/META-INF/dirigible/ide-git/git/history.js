/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
/**
 * Utility URL builder
 */
let UriBuilder = function UriBuilder() {
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
UriBuilder.prototype.build = function (isBasePath = true) {
	if (isBasePath) return '/' + this.pathSegments.join('/');
	return this.pathSegments.join('/');
}

angular.module('git.config', []).constant('GIT_SVC_URL', '/services/v4/ide/git');


/**
 * Git Service API delegate
 */
let GitService = function ($http, gitServiceUrl) {
	this.gitServiceUrl = gitServiceUrl;
	this.$http = $http;
}

GitService.prototype.history = function (workspace, project, file) {
	let url = new UriBuilder().path(this.gitServiceUrl.split('/')).path(workspace).path(project).path("history").build();
	if (file) {
		url += "?path=" + file;
	}
	return this.$http.get(url);
}

let historyApp = angular.module('historyApp', ['git.config', 'ngAnimate', 'ngSanitize', 'ui.bootstrap'])

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
		let messageHub = new FramesMessageHub();
		let announceAlert = function (title, message, type) {
			messageHub.post({
				data: {
					title: title,
					message: message,
					type: type
				}
			}, 'ide.alert');
		};
		let announceAlertSuccess = function (title, message) {
			announceAlert(title, message, "success");
		};
		let announceAlertInfo = function (title, message) {
			announceAlert(title, message, "info");
		};
		let announceAlertWarning = function (title, message) {
			announceAlert(title, message, "warning");
		};
		let announceAlertError = function (title, message) {
			announceAlert(title, message, "error");
		};
		return {
			announceAlert: announceAlert,
			announceAlertSuccess: announceAlertSuccess,
			announceAlertInfo: announceAlertInfo,
			announceAlertWarning: announceAlertWarning,
			announceAlertError: announceAlertError,
			on: function (evt, cb) {
				messageHub.subscribe(cb, evt);
			}
		};
	}])
	.factory('gitService', ['$http', 'GIT_SVC_URL', function ($http, GIT_SVC_URL) {
		return new GitService($http, GIT_SVC_URL);
	}])
	.controller('HistoryContoller', ['gitService', '$messageHub', '$scope', function (gitService, $messageHub, $scope) {

		$scope.selectedWorkspace;
		$scope.selectedProject;
		$scope.selectedFile;

		$scope.refreshRepository = function () {
			$scope.selectedFile = null;
			$scope.refresh();
		}

		$scope.refresh = function () {
			if (!$scope.selectedWorkspace || !$scope.selectedProject) {
				$scope.history = [];
				return;
			}
			gitService.history($scope.selectedWorkspace, $scope.selectedProject, $scope.selectedFile)
				.then(function (response) {
					$scope.history = response.data;
					$scope.history.map(e => e.shortId = e.id.substring(0, 7));
				}.bind(this), function (response) {
					let errorMessage = JSON.parse(response.data.error).message;
					$messageHub.announceAlertError("Loading Git Repository History Error", errorMessage);
				});
		};

		$messageHub.on('git.repository.selected', function (msg) {
			if (msg.data.isGitProject) {
				$scope.selectedWorkspace = msg.data.workspace;
				$scope.selectedProject = msg.data.project;
				$scope.selectedFile = null;
			} else {
				$scope.selectedProject = null;
			}
			$scope.refresh();
		}.bind(this));

		$messageHub.on('git.repository.file.selected', function (msg) {
			if (msg.data.isGitProject) {
				$scope.selectedWorkspace = msg.data.workspace;
				$scope.selectedProject = msg.data.project;
				$scope.selectedFile = msg.data.file;
			} else {
				$scope.selectedProject = null;
			}
			$scope.refresh();
		}.bind(this));
	}]);