/*
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
/**
 * Utility URL builder
 */
var UriBuilder = function UriBuilder(){
	this.pathSegments = [];
	return this;
};

UriBuilder.prototype.path = function(_pathSegments){
	if(!Array.isArray(_pathSegments))
		_pathSegments = [_pathSegments];
	_pathSegments = _pathSegments.filter(function(segment){
			return segment;
		})
		.map(function(segment){
			if(segment.length){
				if(segment.charAt(segment.length-1) ==='/')
					segment = segment.substring(0, segment.length-2);
				segment = encodeURIComponent(segment);
			} 
			return segment;
		});
	this.pathSegments = this.pathSegments.concat(_pathSegments);
	return this;
};
UriBuilder.prototype.build = function(){
	var uriPath = '/' + this.pathSegments.join('/');
	return uriPath;
};

/**
 * Debugger Service API delegate
 */
var DebuggerService = function($http, debuggerServiceUrl, workspacesServiceUrl) {
	this.debuggerServiceUrl = debuggerServiceUrl;
	this.$http = $http;
	this.workspacesServiceUrl = workspacesServiceUrl;
};
DebuggerService.prototype.enable = function() {
	var url = new UriBuilder().path(this.debuggerServiceUrl.split('/')).path("enable").build();
	return this.$http.get(url);
};
DebuggerService.prototype.disable = function() {
	var url = new UriBuilder().path(this.debuggerServiceUrl.split('/')).path('disable').build();
	this.$http.get(url);
};
DebuggerService.prototype.refresh = function() {
	var url = new UriBuilder().path(this.debuggerServiceUrl.split('/')).path('sessions').build();
	return this.$http.get(url);
};
DebuggerService.prototype.continue = function() {
	var url = new UriBuilder().path(this.debuggerServiceUrl.split('/')).path('session').path('continue').build();
	this.$http.get(url).then();
};
DebuggerService.prototype.pause = function() {
	var url = new UriBuilder().path(this.debuggerServiceUrl.split('/')).path('session').path('pause').build();
	this.$http.get(url).then();
};
DebuggerService.prototype.stepInto = function() {
	var url = new UriBuilder().path(this.debuggerServiceUrl.split('/')).path('session').path('stepInto').build();
	this.$http.get(url).then();
};
DebuggerService.prototype.stepOver = function() {
	var url = new UriBuilder().path(this.debuggerServiceUrl.split('/')).path('session').path('stepOver').build();
	this.$http.get(url).then();
};
DebuggerService.prototype.activateSession = function(sessionId) {
	var url = new UriBuilder().path(this.debuggerServiceUrl.split('/')).path('session').path('activate').path(sessionId).build();
	return this.$http.get(url);
};
DebuggerService.prototype.listWorkspaces = function() {
	return this.$http.get(this.workspacesServiceUrl);
};

angular.module('debugger.config', [])
	.constant('DEBUGGER_SVC_URL','../../../../../../services/v4/ide/debug/rhino')
	.constant('WORKSPACES_SVC_URL','../../../../../../services/v4/ide/workspaces');

	
angular.module('debugger', ['debugger.config', 'ngAnimate', 'ngSanitize', 'ui.bootstrap'])
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
		messageHub.post({data: data}, 'debugger.' + evtName);
	};
	var on = function(topic, callback) {
		messageHub.subscribe(callback, topic);
	};
	var announceDebugEnabled = function() {
		this.message('debug.enabled');
	};
	var announceDebugDisabled = function() {
		this.message('debug.disabled');
	};
	var announceDebugRegistered = function() {
		this.message('debug.registered');
	};
	var announceDebugFinished = function() {
		this.message('debug.finished');
	};
	var announceDebugSessions = function(sessions) {
		this.message('debug.sessions', sessions);
	};
	var announceDebugVariables = function(variables) {
		this.message('debug.variables', variables);
	};
	var announceDebugLinebreak = function(linebreak, workspace) {
		this.message('debug.linebreak', linebreak);
		var filePath = '/' + workspace + '/' + linebreak.breakpoint.fullPath;
		this.message('editor.open', {
			'path': filePath,
			'label': filePath.split('/').pop()
		});
		this.message('editor.line.set', {
			'file': filePath,
			'row': linebreak.breakpoint.row
		});
	};

	var onDebugSessions = function(callback) {
		this.on('debugger.debug.sessions', callback);
	};

	var announceDebugRefresh = function() {
		this.message('debug.refresh');
	};
	var announceDebugContinue = function() {
		this.message('debug.continue');
	};
	var announceDebugPause = function() {
		this.message('debug.pause');
	};
	var announceDebugStepInto = function() {
		this.message('debug.stepInto');
	};
	var announceDebugStepOver = function() {
		this.message('debug.stepOver');
	};
	return {
		message: message,
		on: on,
		announceDebugEnabled: announceDebugEnabled,
		announceDebugDisabled: announceDebugDisabled,
		announceDebugRegistered: announceDebugRegistered,
		announceDebugFinished: announceDebugFinished,
		announceDebugSessions: announceDebugSessions,
		announceDebugVariables: announceDebugVariables,
		announceDebugLinebreak: announceDebugLinebreak,
		onDebugSessions: onDebugSessions,
		announceDebugRefresh: announceDebugRefresh,
		announceDebugContinue: announceDebugContinue,
		announceDebugPause: announceDebugPause,
		announceDebugStepInto: announceDebugStepInto,
		announceDebugStepOver: announceDebugStepOver
	};
}])
.factory('debuggerService', ['$http', 'DEBUGGER_SVC_URL', 'WORKSPACES_SVC_URL', function($http, DEBUGGER_SVC_URL, WORKSPACES_SVC_URL){
	return new DebuggerService($http, DEBUGGER_SVC_URL, WORKSPACES_SVC_URL);
}])
.controller('DebuggerController', ['$scope', '$messageHub', 'debuggerService', function ($scope, $messageHub, debuggerService) {

	$scope.debugEnabled = false;

	$scope.selectedWorkspace = null;

	debuggerService.listWorkspaces().success(function(data) {
		$scope.workspaces = data;
		var storedWorkspace = JSON.parse(localStorage.getItem('DIRIGIBLE.workspace'));
		if (storedWorkspace !== null) {
			$scope.selectedWorkspace = storedWorkspace.name;
		} else if(this.workspaces[0]) {
			$scope.selectedWorkspace = $scope.workspaces[0];
		}
	});

	$scope.enable = function() {
		$scope.debugEnabled = !$scope.debugEnabled;
		if ($scope.debugEnabled) {
			debuggerService.enable().then(function() {
				var xhr = new XMLHttpRequest();
				xhr.open('GET', '../../../../js/ide-core/services/user-name.js', false);
				xhr.send();
				if (xhr.status === 200) {
					$scope.username = xhr.responseText;
				} else {
					console.error("Cannot get User Name");
				}
				var protocol = 'window.location.protocol === https:' ? 'wss' : 'ws';
				var wsUrl = protocol + '://' + window.location.host + '/websockets/v4/ide/debug/sessions?' + $scope.username;
				var webSocket = new WebSocket(wsUrl);
				webSocket.onmessage = function(event) {
					var data = JSON.parse(event.data);
					if (!data.type) {
						return;
					}

					switch(data.type) {
						case 'register':
							$messageHub.announceDebugRegistered();
							break;
						case 'finished':
							$messageHub.announceDebugFinished();
							break;
						case 'sessions':
							$messageHub.announceDebugSessions(data.sessions);
							break;
						case 'variables':
							$messageHub.announceDebugVariables(data.variables);
							break;
						case 'linebreak':
							$messageHub.announceDebugLinebreak(data.linebreak, $scope.selectedWorkspace);
							break;
					}

				};
		
				webSocket.onclose = function(event) {
					console.error(JSON.stringify(event));
				};
		
				webSocket.onerror = function(event) {
					console.error(JSON.stringify(event));
				};
			});
			$messageHub.announceDebugEnabled();
		} else {
			debuggerService.disable();
			$messageHub.announceDebugDisabled();
			$scope.sessions = [];
		}
	};

	$messageHub.onDebugSessions(function(event) {
		$scope.sessions = event.data;
		$scope.$apply();
	});

	$scope.refresh = function() {
		if ($scope.debugEnabled) {
			debuggerService.refresh().then(function(response) {
				$scope.sessions = response.data;
				$messageHub.announceDebugRefresh();
			});
		}
	};

	$scope.continue = function() {
		if ($scope.debugEnabled) {
			debuggerService.continue();
			$messageHub.announceDebugContinue();
			$scope.refresh();
		}
	};

	$scope.pause = function() {
		if ($scope.debugEnabled) {
			debuggerService.pause();
			$messageHub.announceDebugPause();
			$scope.refresh();
		}
	};

	$scope.stepInto = function() {
		if ($scope.debugEnabled) {
			debuggerService.stepInto();
			$messageHub.announceDebugStepInto();
			$scope.refresh();
		}
	};

	$scope.stepOver = function() {
		if ($scope.debugEnabled) {
			debuggerService.stepOver();
			$messageHub.announceDebugStepOver();
			$scope.refresh();
		}
	};

	$scope.activateSession = function(session) {
		if ($scope.debugEnabled) {
			debuggerService.activateSession(session.executionId).success(function() {
				$scope.refresh();
				$messageHub.announceDebugVariables();
			});
		}
	};
}]);

