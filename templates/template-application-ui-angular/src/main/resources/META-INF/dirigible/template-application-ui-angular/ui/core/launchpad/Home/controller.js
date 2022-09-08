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
angular.module('page', ["ideUI", "ideView", "entityApi"])
	.config(["messageHubProvider", function (messageHubProvider) {
		messageHubProvider.eventIdPrefix = '{{projectName}}.launchpad.Home';
	}])
	.config(["entityApiProvider", function (entityApiProvider) {
		entityApiProvider.baseUrl = "/services/v4/js/{{projectName}}/gen/ui/core/services/tiles.js";
	}])
	.controller('PageController', ['$scope', 'messageHub', 'entityApi', function ($scope, messageHub, entityApi) {

		$scope.openView = function (location) {
			messageHub.postMessage("openView", {
				location: location
			});
		}

		entityApi.list().then(function (response) {
			if (response.status != 200) {
				messageHub.showAlertError("Home", `Unable to get Home Launchpad: '${response.message}'`);
				return;
			}
			$scope.data = response.data;
			$scope.groups = Object.keys(response.data);
		});

	}]);
