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
angular
	.module('import', ['angularFileUpload'])
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
			messageHub.post({ data: data }, 'properties.' + evtName);
		};
		let on = function (topic, callback) {
			messageHub.subscribe(callback, topic);
		};
		return {
			message: message,
			on: on
		};
	}])
	.controller('ImportController', ['$scope', 'FileUploader', '$messageHub', function ($scope, FileUploader, $messageHub) {

		$scope.TRANSPORT_SNAPSHOT_URL = "/services/v4/transport/snapshot";

		// FILE UPLOADER

		let uploader = $scope.uploader = new FileUploader({
			url: $scope.TRANSPORT_SNAPSHOT_URL
		});

		uploader.headers['X-Requested-With'] = 'Fetch';

		// UPLOADER FILTERS

		uploader.filters.push({
			name: 'customFilter',
			fn: function (item /*{File|FileLikeObject}*/, options) {
				return this.queue.length < 100;
			}
		});

		// UPLOADER CALLBACKS

		uploader.onWhenAddingFileFailed = function (item /*{File|FileLikeObject}*/, filter, options) {
			//        console.info('onWhenAddingFileFailed', item, filter, options);
		};
		uploader.onAfterAddingFile = function (fileItem) {

		};
		uploader.onAfterAddingAll = function (addedFileItems) {
			//        console.info('onAfterAddingAll', addedFileItems);
		};
		uploader.onBeforeUploadItem = function (item) {
			//        console.info('onBeforeUploadItem', item);
			item.url = $scope.TRANSPORT_SNAPSHOT_URL;
		};
		uploader.onProgressItem = function (fileItem, progress) {
			//        console.info('onProgressItem', fileItem, progress);
		};
		uploader.onProgressAll = function (progress) {
			//        console.info('onProgressAll', progress);
		};
		uploader.onSuccessItem = function (fileItem, response, status, headers) {
			//        console.info('onSuccessItem', fileItem, response, status, headers);
		};
		uploader.onErrorItem = function (fileItem, response, status, headers) {
			//        console.info('onErrorItem', fileItem, response, status, headers);
			alert(response.err.message);
		};
		uploader.onCancelItem = function (fileItem, response, status, headers) {
			//        console.info('onCancelItem', fileItem, response, status, headers);
		};
		uploader.onCompleteItem = function (fileItem, response, status, headers) {
			//refreshFolder();
			//        console.info('onCompleteItem', fileItem, response, status, headers);
		};
		uploader.onCompleteAll = function () {
			//        console.info('onCompleteAll');
		};

	}]);