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
angular.module('flowableModeler').controller('FlowableMessageRefCtrl', [ '$scope', function($scope) {

    // Find the parent shape on which the message definitions are defined
    var messageDefinitionsProperty = undefined;
    var parent = $scope.selectedShape;
    while (parent !== null && parent !== undefined && messageDefinitionsProperty === undefined) {
        if (parent.properties && parent.properties.get('oryx-messagedefinitions')) {
        	messageDefinitionsProperty = parent.properties.get('oryx-messagedefinitions');
        } else {
            parent = parent.parent;
        }
    }

    try {
        messageDefinitionsProperty = JSON.parse(messageDefinitionsProperty);
        if (typeof messageDefinitionsProperty == 'string') {
            messageDefinitionsProperty = JSON.parse(messageDefinitionsProperty);
        }
    } catch (err) {
        // Do nothing here, just to be sure we try-catch it
    }

    $scope.messageDefinitions = messageDefinitionsProperty;


    $scope.messageChanged = function() {
    	$scope.updatePropertyInModel($scope.property);
    };
}]);