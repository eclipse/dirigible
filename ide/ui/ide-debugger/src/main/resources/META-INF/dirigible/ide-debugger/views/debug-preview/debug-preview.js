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
angular.module('preview', [])
	.factory('$messageHub', [function () {
		let messageHub = new FramesMessageHub();
		let message = function (evtName, data) {
			messageHub.post({ data: data }, 'workspace.' + evtName);
		};
		let on = function (topic, callback) {
			messageHub.subscribe(callback, topic);
		};
		return {
			message: message,
			on: on
		};
	}])
	.controller('PreviewController', ['$scope', '$messageHub', function ($scope, $messageHub) {

		this.refresh = function () {
			let url = this.previewUrl;
			if (url) {
				url = url.indexOf('?refreshToken') > 0 ? url.substring(0, url.indexOf('?refreshToken')) : url;
				let tokenParam = 'refreshToken=' + new Date().getTime();
				this.previewUrl = url + (url.indexOf('?') > 0 ? (url.endsWith('?') ? tokenParam : ('&' + tokenParam)) : ('?' + tokenParam));
			}
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
					url += '/js';
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