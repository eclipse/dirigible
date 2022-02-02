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
function previewLoaded() {
	$.ajax('/services/v4/ide/debug/graalvm/disable');
}

angular.module('preview', [])
	.factory('httpRequestInterceptor', function () {
		let csrfToken = null;
		return {
			request: function (config) {
				config.headers['X-Requested-With'] = 'Fetch';
				config.headers['X-CSRF-Token'] = csrfToken ? csrfToken : 'Fetch';
				return config;
			},
			response: function (response) {
				let token = response.headers()['x-csrf-token']
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
	.factory('$messageHub', [function () {
		let messageHub = new FramesMessageHub();
		let message = function (evtName, data) {
			messageHub.post({ data: data }, 'debugger.' + evtName);
		};

		let on = function (topic, callback) {
			messageHub.subscribe(callback, topic);
		};

		let refresh = function (resourcePath) {
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

		this.refresh = function () {
			let url = this.previewUrl;
			if (url) {
				url = url.indexOf('?refreshToken') > 0 ? url.substring(0, url.indexOf('?refreshToken')) : url;
				let tokenParam = 'refreshToken=' + new Date().getTime();
				this.previewUrl = url + (url.indexOf('?') > 0 ? (url.endsWith('?') ? tokenParam : ('&' + tokenParam)) : ('?' + tokenParam));
				if (url.indexOf('/graalvm/') > 0) {
					resourcePath = url.substring(url.indexOf('/graalvm/') + '/graalvm/'.length);
				} else if (url.indexOf('/js/') > 0) {
					resourcePath = url.substring(url.indexOf('/js/') + '/js/'.length);
				} else if (url.indexOf('/xsk/') > 0) {
					resourcePath = url.substring(url.indexOf('/xsk/') + '/xsk/'.length);
				}
				$messageHub.refresh("/" + resourcePath);
			}
			$http.get('/services/v4/ide/debug/graalvm/enable');
		};

		$messageHub.on('workspace.file.selected', function (msg) {
			let resourcePath = msg.data.path.substring(msg.data.path.indexOf('/', 1));
			let url = window.location.protocol + '//' + window.location.host + window.location.pathname.substr(0, window.location.pathname.indexOf('/web/'));
			let type = resourcePath.substring(resourcePath.lastIndexOf('.') + 1);
			switch (type) {
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
				case 'mjs':
					url += '/js';
					break;
				case 'graalvm':
					url += '/graalvm';
					break;
				case 'xsjs':
					url += '/xsk';
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

		$messageHub.on('workspace.file.published', function (msg) {
			this.refresh();
			$scope.$apply();
		}.bind(this));

		$scope.cancel = function (e) {
			if (e.keyCode === 27) {
				$scope.previewForm.preview.$rollbackViewValue();
			}
		};
	}]);