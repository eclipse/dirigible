/*
 * Copyright (c) 2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

angular.module('jobs', [])
.controller('JobsController', ['$scope', '$http', function ($scope, $http) {

	$http.get('../../../../services/v3/ops/jobs').then(function(response) {
		$scope.jobsList = response.data;
	});


}]).config(function($sceProvider) {
    $sceProvider.enabled(false);
});
