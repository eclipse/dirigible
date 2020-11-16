/*
 * Copyright (c) 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
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

GitService.prototype.commit = function (workspace, project, commitMessage, username, password, email, branch) {
	var url = new UriBuilder().path(this.gitServiceUrl.split('/')).path(workspace).path(project).path("commit").build();
	return this.$http.post(url, {
		"commitMessage": commitMessage,
		"username": username,
		"password": btoa(password),
		"email": email,
		"branch": branch,
		"autoAdd": false,
		"autoCommit": false
	})
		.then(function (response) {
			return response.data;
		});
}
GitService.prototype.push = function (workspace, project, commitMessage, username, password, email, branch) {
	var url = new UriBuilder().path(this.gitServiceUrl.split('/')).path(workspace).path(project).path("push").build();
	return this.$http.post(url, {
		"commitMessage": commitMessage,
		"username": username,
		"password": btoa(password),
		"email": email,
		"branch": branch,
		"autoAdd": false,
		"autoCommit": false
	})
		.then(function (response) {
			return response.data;
		});
}
GitService.prototype.getUnstagedFiles = function (workspace, project) {
	var url = new UriBuilder().path(this.gitServiceUrl.split('/')).path(workspace).path(project).path("unstaged").build();
	return this.$http.get(url, {})
		.then(function (response) {
			return response.data.files;
		});
}
GitService.prototype.getStagedFiles = function (workspace, project) {
	var url = new UriBuilder().path(this.gitServiceUrl.split('/')).path(workspace).path(project).path("staged").build();
	return this.$http.get(url, {})
		.then(function (response) {
			return response.data.files;
		});
}
GitService.prototype.addFiles = function (workspace, project, files) {
	var url = new UriBuilder().path(this.gitServiceUrl.split('/')).path(workspace).path(project).path("add").build();
	var list = files.join(",");
	return this.$http.post(url, JSON.stringify(list));
}
GitService.prototype.revertFiles = function (workspace, project, files) {
	var url = new UriBuilder().path(this.gitServiceUrl.split('/')).path(workspace).path(project).path("revert").build();
	var list = files.join(",");
	return this.$http.post(url, JSON.stringify(list));
}
GitService.prototype.removeFiles = function (workspace, project, files) {
	var url = new UriBuilder().path(this.gitServiceUrl.split('/')).path(workspace).path(project).path("remove").build();
	var list = files.join(",");
	return this.$http.post(url, JSON.stringify(list));
}

var stagingApp = angular.module('stagingApp', ['git.config', 'ngAnimate', 'ngSanitize', 'ui.bootstrap'])
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
		var message = function (evtName, data) {
			messageHub.post({ data: data }, 'git.' + evtName);
		};
		var announceFileDiff = function (fileDescriptor) {
			this.message('staging.file.diff', fileDescriptor);
		};
		return {
			message: message,
			announceFileDiff: announceFileDiff,
			on: function (evt, cb) {
				messageHub.subscribe(cb, evt);
			}
		};
	}])
	.factory('gitService', ['$http', 'GIT_SVC_URL', function ($http, GIT_SVC_URL) {
		return new GitService($http, GIT_SVC_URL);
	}])
	.controller('StagingContoller', ['gitService', '$messageHub', '$http', '$scope', function (gitService, $messageHub, $http, $scope) {

		this.gitService = gitService;
		this.http = $http;
		this.scope = $scope;

		this.okCommitAndPushClicked = function () {
			gitService.commit(this.selectedWorkspace, this.selectedProject, this.commitMessage, this.username, this.password, this.email, this.branch)
				.then(function () {
					gitService.push(this.selectedWorkspace, this.selectedProject, this.commitMessage, this.username, this.password, this.email, this.branch)
						.then(function () {
							this.commitMessage = "";
							this.refresh();
						}.bind(this));
				}.bind(this));

		};

		this.okCommitClicked = function () {
			gitService.commit(this.selectedWorkspace, this.selectedProject, this.commitMessage, this.username, this.password, this.email, this.branch)
				.then(function () {
					this.commitMessage = "";
					this.refresh();
				}.bind(this));
		};

		this.refresh = function () {
			if (!this.selectedWorkspace || !this.selectedProject) {
				this.unstagedFiles = [];
				this.stagedFiles = [];
				this.scope.$apply();
				return;
			}
			gitService.getUnstagedFiles(this.selectedWorkspace, this.selectedProject)
				.then(function (files) {
					this.unstagedFiles = files;
					this.unstagedFiles.map(e => { e.label = this.typeIcon(e.type) + ' ' + e.path });
					try {
						this.scope.$apply();
					} catch (e) {
						//
					}
				}.bind(this));
			gitService.getStagedFiles(this.selectedWorkspace, this.selectedProject)
				.then(function (files) {
					this.stagedFiles = files;
					this.stagedFiles.map(e => { e.label = this.typeIcon(e.type) + ' ' + e.path });
					try {
						this.scope.$apply();
					} catch (e) {
						//
					}
				}.bind(this));
		};

		this.unstagedFiles = [];

		this.selectedUnstagedFiles = [];

		this.stagedFiles = [];

		this.selectedStagedFiles = [];


		this.downClicked = function () {
			gitService.addFiles(this.selectedWorkspace, this.selectedProject, this.selectedUnstagedFiles)
				.then(function () {
					this.refresh();
				}.bind(this));
		}

		this.upClicked = function () {
			gitService.removeFiles(this.selectedWorkspace, this.selectedProject, this.selectedStagedFiles)
				.then(function () {
					this.refresh();
				}.bind(this));
		}

		this.diffClicked = function (file) {
			if (file) {
				$messageHub.announceFileDiff({
					project: this.selectedWorkspace + "/" + this.selectedProject,
					file: file.path
				});
			} else if (this.selectedUnstagedFiles && this.selectedUnstagedFiles.length > 0) {
				this.selectedUnstagedFiles.forEach(function(file){
					$messageHub.announceFileDiff({
						project: this.selectedWorkspace + "/" + this.selectedProject,
						file: file
					});
				}.bind(this));
			}
		}

		this.revertClicked = function () {
			gitService.revertFiles(this.selectedWorkspace, this.selectedProject, this.selectedUnstagedFiles)
				.then(function () {
					this.refresh();
				}.bind(this));
		}

		$messageHub.on('git.repository.selected', function (msg) {
			if (msg.data.isGitProject) {
				this.selectedWorkspace = msg.data.workspace;
				this.selectedProject = msg.data.project;
			} else {
				this.selectedProject = null;
			}
			this.refresh();
		}.bind(this));

		types = {};
		types[0] = "&#xf071;"; //"conflicting";
		types[1] = "&#xf055;"; //"plus-circle";//"added"; // staged
		types[2] = "&#xf058;"; //"check-circle"//"changed"; // staged
		types[3] = "&#xf05c;"; //"times-circle-o"//"missing"; // unstaged
		types[4] = "&#xf05d;"; //"check-circle-o"//"modified"; // unstaged
		types[5] = "&#xf057;"; //"times-circle"//"removed"; // staged
		types[6] = "&#xf10c;"; //"question-circle-o"//"untracked"; // unstaged
		this.typeIcon = function (i) {
			return types[i];
		}

	}]);

