/*
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
angular.module('logs', [])
.controller('LogsController', ['$scope', '$http', function ($scope, $http) {

	$scope.selectedLog = null;
	$http.get('../../../../services/v3/ops/logs').then(function(response) {
		$scope.logsList = response.data;
	});
	
	$scope.logChanged = function() {
		if ($scope.selectedLog) {
			$http.get('../../../../services/v3/ops/logs/' + $scope.selectedLog).then(function(response) {
				$scope.logContent = response.data;
			});	
		}
	}


}]).config(function($sceProvider) {
    $sceProvider.enabled(false);
});
