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
'use strict';

flowableApp.controller('AboutFlowablePopupCtrl', ['$rootScope', '$scope', '$http', '$translate', '$interval', '$dateFormatter', function($rootScope, $scope, $http, $translate, $interval, $dateFormatter) {
    $scope.popup = {
        loading: true,
        activitiVersion: {},
        licenseHolder: ''
    };

    $http({method: 'GET', url: FLOWABLE.APP_URL.getAboutInfoUrl()}).
        success(function(response, status, headers, config) {
            $scope.popup.licenseHolder = response.holder;
            $scope.popup.activitiVersion = response.versionInfo.edition + ' v' + response.versionInfo.majorVersion + '.' + response.versionInfo.minorVersion + '.' + response.versionInfo.revisionVersion;
            $scope.popup.activitiVersionType = response.versionInfo.type;
            $scope.popup.loading = false;
        }).
        error(function(response, status, headers, config) {
            $scope.popup.loading = false;
        });


    $scope.cancel = function() {
        $scope.close();
    };


    $scope.close = function() {
        $scope.$hide();
    }

}]);
