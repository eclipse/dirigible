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
/**
 * Configure the HTTP provider such that no caching happens when fetching
 * a resource using $http.
 */

flowableModule.factory('NotPermittedInterceptor', [ '$q', '$window', function($q, $window) {
    return {
        responseError: function ( response ) {

            if (response.status === 403) {
                $window.location.href = FLOWABLE.CONFIG.contextRoot;
                $window.location.reload();
                return $q.reject(response);
            }
            else{
                return $q.reject(response);
            }
        }
    }
}]);

flowableModule.config(['$httpProvider', function($httpProvider) {

    if (!$httpProvider.defaults.headers.get) {
        $httpProvider.defaults.headers.get = {};
    }

    $httpProvider.defaults.headers.get['Cache-Control'] = 'no-cache, no-store, must-revalidate';
    $httpProvider.defaults.headers.get['Pragma'] = 'no-cache';
    $httpProvider.defaults.headers.get['Expires'] = '0';

    $httpProvider.interceptors.push('NotPermittedInterceptor');

}]);