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
const previewView = angular.module('debug-preview', ['ideUI', 'ideView']);

previewView.config(["messageHubProvider", function (messageHubProvider) {
	messageHubProvider.eventIdPrefix = 'debug-preview-view';
}]);

previewView.controller('DebugPreviewController', ['$scope', 'messageHub', function ($scope, messageHub) {

	this.urlLocked = false;
	this.iframe = document.getElementById('preview-iframe');
	this.history = {
		idx: -1,
		state: []
	};

	this.iframe.addEventListener("load", () => {
		const previewEl = document.querySelector('.preview');
		const elementStyle = getComputedStyle(previewEl);
		let iframeDocument = this.iframe.contentDocument || this.iframe.contentWindow.document;
		if (iframeDocument) {
			const bodyElements = iframeDocument.getElementsByTagName("body");
			const body = bodyElements.length === 1 && bodyElements[0];
			if (body.outerText) {
				body.style.color = elementStyle.color;
				body.style['font-family'] = elementStyle['font-family'];
			}
			const preElements = iframeDocument.getElementsByTagName('pre');
			const pre = preElements.length === 1 && preElements[0];
			if (pre) {
				pre.style.color = elementStyle.color;
			}
		}
	});

	this.reload = function () {
		let iframeDocument = this.iframe.contentDocument || this.contentWindow.document;
		if (iframeDocument) {
			iframeDocument.location.reload(true);
		}
	}

	this.getCurrentUrl = function () {
		return this.history.state[this.history.idx];
	}

	this.hasBack = function () {
		return this.history.idx > 0;
	}

	this.hasForward = function () {
		return this.history.idx < this.history.state.length - 1;
	}

	this.goBack = function () {
		if (this.hasBack()) {
			const url = this.history.state[--this.history.idx];
			this.replaceLocationUrl(url);
		}
	}

	this.goForward = function () {
		if (this.hasForward()) {
			const url = this.history.state[++this.history.idx];
			this.replaceLocationUrl(url);
		}
	}

	this.gotoUrl = function (url, shouldReload = true) {
		const currentUrl = this.getCurrentUrl();
		if (currentUrl && currentUrl === url) {
			if (shouldReload)
				this.reload();
			return;
		};

		if (this.history.idx >= 0)
			this.history.state.length = this.history.idx + 1;

		this.history.state.push(url);
		this.history.idx++;

		this.replaceLocationUrl(url);
	}

	this.replaceLocationUrl = function (url) {
		this.previewUrl = url;
		this.iframe.contentWindow.location.replace(url);
	}

	this.inputUrlKeyUp = function (e) {
		switch (e.key) {
			case 'Escape': // cancel url edit
				const currentUrl = this.getCurrentUrl();
				this.previewUrl = currentUrl || '';
				break;
			case 'Enter':
				if (this.previewUrl) {
					this.gotoUrl(this.previewUrl);
				}
				break;
		}
	};

	this.makeUrlFromPath = function (resourcePath) {
		let url = window.location.protocol + '//' + window.location.host + window.location.pathname.substring(window.location.pathname.indexOf('/web/'), 0);
		let type = resourcePath.substring(resourcePath.lastIndexOf('.') + 1);
		let isOData = resourcePath.endsWith(".odata");
		if (isOData) {
			url = window.location.protocol + '//' + window.location.host + "/odata/v2/";
		} else {
			switch (type) {
				case 'rhino':
					url += '/rhino';
					break;
				case 'nashorn':
					url += '/nashorn';
					break;
				case 'v8':
					url += '/v8';
					break;
				case 'graalvm':
					url += '/graalvm';
					break;
				case 'js':
					url += '/js';
					break;
				case 'mjs':
					url += '/js';
					break;
				case 'ts':
					url += '/js';
					break;
				case 'xsjs':
					url += '/kronos';
					break;
				case 'md':
					url += '/wiki';
					break;
				case 'command':
					url += '/command';
					break;
				case 'xsodata':
					url += '/web';
					break;
				case 'edm':
				case 'dsm':
				case 'bpmn':
				case 'job':
				case 'xsjob':
				case 'calculationview':
				case 'websocket':
				case 'hdi':
				case 'hdbtable':
				case 'hdbstructurе':
				case 'hdbview':
				case 'hdbtablefunction':
				case 'hdbprocedure':
				case 'hdbschema':
				case 'hdbsynonym':
				case 'hdbdd':
				case 'hdbsequence':
				case 'hdbcalculationview':
				case 'xsaccess':
				case 'xsprivileges':
				case 'xshttpdest':
				case 'listener':
				case 'extensionpoint':
				case 'extension':
				case 'table':
				case 'view':
				case 'access':
				case 'roles':
				case 'sh':
				case 'csv':
				case 'csvim':
				case 'hdbti':
				case 'form':
					return;
				default:
					url += '/web';
			}
			url += resourcePath;
		}
		return url;
	}

	messageHub.onFileSelected((fileDescriptor) => {
		if (this.urlLocked)
			return;

		let url = this.makeUrlFromPath(fileDescriptor.path);
		if (url) {
			url += "?debug=true"
			this.gotoUrl(url, false);
			$scope.$apply();
		}
	});

	messageHub.onPublish((fileDescriptor) => {
		if (this.urlLocked)
			return;

		if (fileDescriptor.path) {
			let url = this.makeUrlFromPath(fileDescriptor.path);
			if (url) {
				this.gotoUrl(url);
				$scope.$apply();
			}
		} else {
			this.reload();
			$scope.$apply();
		}
	});

	messageHub.onUnpublish((fileDescriptor) => {
		if (this.urlLocked)
			return;

		if (fileDescriptor.path) {
			let url = this.makeUrlFromPath(fileDescriptor.path);
			if (url) {
				this.gotoUrl(url);
				$scope.$apply();
			}
		} else {
			this.reload();
			$scope.$apply();
		}
	});








	// this.refresh = function () {
	// 	let url = this.previewUrl;
	// 	if (url) {
	// 		url = url.indexOf('?refreshToken') > 0 ? url.substring(0, url.indexOf('?refreshToken')) : url;
	// 		let tokenParam = 'refreshToken=' + new Date().getTime();
	// 		this.previewUrl = url + (url.indexOf('?') > 0 ? (url.endsWith('?') ? tokenParam : ('&' + tokenParam)) : ('?' + tokenParam)) + "&debug=true";
	// 		if (url.indexOf('/graalvm/') > 0) {
	// 			resourcePath = url.substring(url.indexOf('/graalvm/') + '/graalvm/'.length);
	// 		} else if (url.indexOf('/js/') > 0) {
	// 			resourcePath = url.substring(url.indexOf('/js/') + '/js/'.length);
	// 		} else if (url.indexOf('/kronos/') > 0) {
	// 			resourcePath = url.substring(url.indexOf('/kronos/') + '/kronos/'.length);
	// 		}
	// 		messageHub.refresh("/" + resourcePath);
	// 	}
	// };

	// messageHub.on('workspace.file.selected', function (msg) {
	// 	let resourcePath = msg.data.path.substring(msg.data.path.indexOf('/', 1));
	// 	let url = window.location.protocol + '//' + window.location.host + window.location.pathname.substr(0, window.location.pathname.indexOf('/web/'));
	// 	let type = resourcePath.substring(resourcePath.lastIndexOf('.') + 1);
	// 	switch (type) {
	// 		case 'rhino':
	// 			url += '/rhino';
	// 			break;
	// 		case 'nashorn':
	// 			url += '/nashorn';
	// 			break;
	// 		case 'v8':
	// 			url += 'v8';
	// 			break;
	// 		case 'js':
	// 		case 'mjs':
	// 			url += '/js';
	// 			break;
	// 		case 'graalvm':
	// 			url += '/graalvm';
	// 			break;
	// 		case 'xsjs':
	// 			url += '/kronos';
	// 			break;
	// 		case 'md':
	// 			url += '/wiki';
	// 			break;
	// 		case 'command':
	// 			url += '/command';
	// 			break;
	// 		case 'edm':
	// 		case 'dsm':
	// 		case 'bpmn':
	// 		case 'job':
	// 		case 'listener':
	// 		case 'extensionpoint':
	// 		case 'extension':
	// 		case 'table':
	// 		case 'view':
	// 		case 'access':
	// 		case 'roles':
	// 		case 'sh':
	// 			return;
	// 		default:
	// 			url += '/web';
	// 	}
	// 	url += resourcePath;
	// 	this.previewUrl = url;
	// 	this.refresh();
	// 	$scope.$apply();
	// }.bind(this));

	// $messageHub.on('workspace.file.published', function (msg) {
	// 	this.refresh();
	// 	$scope.$apply();
	// }.bind(this));

	// $scope.cancel = function (e) {
	// 	if (e.keyCode === 27) {
	// 		$scope.previewForm.preview.$rollbackViewValue();
	// 	}
	// };
}]);