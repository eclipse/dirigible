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

/*
 * Condition expression
 */

angular.module('flowableModeler').controller('FlowableConditionExpressionCtrl', [ '$scope', '$modal', function($scope, $modal) {

    // Config for the modal window
    var opts = {
        template: 'editor-app/configuration/properties/condition-expression-popup.html?version=' + Date.now(),
        scope: $scope
    };

    // Open the dialog
    _internalCreateModal(opts, $modal, $scope);
}]);

angular.module('flowableModeler').controller('FlowableConditionExpressionPopupCtrl',
    [ '$rootScope', '$scope', '$translate', 'FormBuilderService', function($rootScope, $scope, $translate, FormBuilderService) {
    	
    // Put json representing assignment on scope
    if ($scope.property.value !== undefined && $scope.property.value !== null
        && $scope.property.value.expression !== undefined
        && $scope.property.value.expression !== null) {

        $scope.expression = $scope.property.value.expression;

    } else if ($scope.property.value !== undefined && $scope.property.value !== null) {
        $scope.expression = {type: 'static', staticValue: $scope.property.value};
        
    } else {
        $scope.expression = {};
    }

    $scope.save = function() {
        $scope.property.value = {expression: $scope.expression};
        $scope.updatePropertyInModel($scope.property);
        $scope.close();
    };

    // Close button handler
    $scope.close = function() {
    	$scope.property.mode = 'read';
    	$scope.$hide();
    };

}]);