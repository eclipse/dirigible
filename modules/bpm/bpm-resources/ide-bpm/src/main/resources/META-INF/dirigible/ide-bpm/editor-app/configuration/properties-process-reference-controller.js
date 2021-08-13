/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

angular.module('flowableModeler').controller('FlowableProcessReferenceCtrl',
    [ '$scope', '$modal', '$http', function($scope, $modal, $http) {
	
     // Config for the modal window
     var opts = {
         template:  'editor-app/configuration/properties/process-reference-popup.html?version=' + Date.now(),
         scope: $scope
     };

     // Open the dialog
        _internalCreateModal(opts, $modal, $scope);
}]);

angular.module('flowableModeler').controller('FlowableProcessReferencePopupCtrl', [ '$scope', '$http', function($scope, $http) {
	
    $scope.state = {'loadingProcesses' : true, 'error' : false};
    
    // Close button handler
    $scope.close = function() {
    	$scope.property.mode = 'read';
        $scope.$hide();
    };
    
    // Selecting/deselecting a process
    $scope.selectProcess = function(processModel, $event) {
   	 	$event.stopPropagation();
   	 	if ($scope.selectedProcess && $scope.selectedProcess.id && processModel.id == $scope.selectedProcess.id) {
   	 		// un-select the current selection
   	 		$scope.selectedProcess = null;
   	 	} else {
   	 		$scope.selectedProcess = processModel;
   	 	}
    };
    
    // Saving the selected value
    $scope.save = function() {
   	 	if ($scope.selectedProcess) {
   	 		$scope.property.value = {'id' : $scope.selectedProcess.id, 'name' : $scope.selectedProcess.name};
   	 	} else {
   	 		$scope.property.value = null; 
   	 	}
   	 	$scope.updatePropertyInModel($scope.property);
   	 	$scope.close();
    };
    
    $scope.loadProcesses = function() {
   	 
    	$http.get(FLOWABLE.APP_URL.getModelsUrl("?modelType=0"))
    		.success(
    			function(response) {
    				$scope.state.loadingProcesses = false;
    				$scope.state.processError = false;
    				$scope.processModels = response.data;
    			})
    		.error(
    			function(data, status, headers, config) {
    				$scope.state.loadingProcesses = false;
    				$scope.state.processError = true;
    			});
    };
    
    if ($scope.property && $scope.property.value && $scope.property.value.id) {
   	 	$scope.selectedProcess = $scope.property.value;
    }
    
    $scope.loadProcesses();  
}]);
