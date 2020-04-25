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
function previewLoaded() {
	$.ajax('../../../../ide/debug/graalvm/disable');
}

angular.module('preview', [])
.factory('httpRequestInterceptor', function () {
	var csrfToken = null;
	return {
		request: function (config) {
			config.headers['X-Requested-With'] = 'Fetch';
			config.headers['X-CSRF-Token'] = csrfToken ? csrfToken : 'Fetch';
			return config;
		},
		response: function(response) {
			var token = response.headers()['x-csrf-token']
			if (token) {
				csrfToken = token;
			}
			return response;
		}
	};
})
.config(['$httpProvider', function($httpProvider) {
	$httpProvider.interceptors.push('httpRequestInterceptor');
}])
.factory('$messageHub', [function(){
	var messageHub = new FramesMessageHub();	
	var message = function(evtName, data){
		messageHub.post({data: data}, 'debugger.' + evtName);
	};

	var on = function(topic, callback) {
		messageHub.subscribe(callback, topic);
	};

	var refresh = function(resourcePath) {
		message('refresh', {
			resourcePath: resourcePath
		});
	};

	return {
		message: message,
		on: on,
		refresh: refresh
	};
}])
.controller('DebugPreviewController', ['$scope', '$http', '$messageHub', function ($scope, $http, $messageHub) {

	this.refresh = function() {
		var url = this.previewUrl;
		if (url) {
			url = url.indexOf('?refreshToken') > 0 ? url.substring(0, url.indexOf('?refreshToken')) : url;
			this.previewUrl = url + '?refreshToken=' + new Date().getTime();
			if (url.indexOf('/graalvm/') > 0) {
				resourcePath = url.substring(url.indexOf('/graalvm/') + '/graalvm/'.length);
			} else if (url.indexOf('/js/') > 0) {
				resourcePath = url.substring(url.indexOf('/js/') + '/js/'.length);
			}
			$messageHub.refresh("/" + resourcePath);
		}
		$http.get('../../../../ide/debug/graalvm/enable');
	};

	$messageHub.on('workspace.file.selected', function(msg) {
		var resourcePath = msg.data.path.substring(msg.data.path.indexOf('/', 1));
		var url = window.location.protocol + '//' + window.location.host +  window.location.pathname.substr(0, window.location.pathname.indexOf('/web/'));
		var type = resourcePath.substring(resourcePath.lastIndexOf('.') + 1);
		switch(type) {
			case 'rhino':
				url += '/rhino';
				break;
			case 'nashorn':
				url += '/nashorn';
				break;
			case 'v8':
				url += 'v8';
				break;
			case 'js':
				url += '/js';
				break;
			case 'graalvm':
				url += '/graalvm';
				break;
			case 'xsjs':
				url += '/xsc';
				break;
			case 'md':
				url += '/wiki';
				break;
			case 'command':
				url += '/command';
				break;
			case 'edm':
			case 'dsm':
			case 'bpmn':
			case 'job':
			case 'listener':
			case 'extensionpoint':
			case 'extension':
			case 'table':
			case 'view':
			case 'access':
			case 'roles':
			case 'sh':
				return;
			default:
				url += '/web';
		}
		url += resourcePath;
		this.previewUrl = url;
		this.refresh();
		$scope.$apply();
	}.bind(this));
	
	$messageHub.on('workspace.file.published', function(msg) {
		this.refresh();
		$scope.$apply();
	}.bind(this));

	$scope.cancel = function(e) {
		if (e.keyCode === 27) {
			$scope.previewForm.preview.$rollbackViewValue();
		}
	};
}]).config(function($sceProvider) {
    $sceProvider.enabled(false);
});
