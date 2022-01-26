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
function showAlert(title, message, $scope) {
	$scope.$parent.alertTitle = title;
	$scope.$parent.alertStatus = "warning";
	$scope.$parent.alertMessage = message;
	$scope.$apply();
	$("#alertOpen").click();
}

function showInfo(title, message, $scope) {
	$scope.$parent.infoTitle = title;
	$scope.$parent.infoMessage = message;
	$scope.$apply();
	$("#infoOpen").click();
}