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
angular.module('about', [])
.controller('AboutController', ['$scope', '$http', function ($scope, $http) {

	$http.get('../../../../../services/v4/version').then(function(response) {
		$scope.version = response.data;
	});

	setInterval(function() {
				
				$http({
		            method: 'GET',
		            url: '../../../../../services/v4/healthcheck'
		        }).success(function(healthStatus){
					var jobs = [];
					for (const [key, value] of Object.entries(healthStatus.jobs.statuses)) {
						var job = new Object();
						job.name = key;
						job.status = value;
						jobs.push(job);
					}
		        	$scope.jobs = jobs;
		        }).error(function(e){
		            console.error("Error retreiving the health status", e);
		        });

			}, 10000);


}]).config(function($sceProvider) {
    $sceProvider.enabled(false);
});
