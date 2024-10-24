/*
 * Copyright (c) 2024 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
angular.module('platformView', ['platformExtensions', 'platformTheming'])
    .constant('view', (typeof viewData != 'undefined') ? viewData : (typeof editorData != 'undefined' ? editorData : {}))
    .constant('perspective', (typeof perspectiveData != 'undefined') ? perspectiveData : {})
    .constant('shell', (typeof shellData != 'undefined') ? shellData : {})
    .factory('baseHttpInterceptor', function () {
        let csrfToken = null;
        return {
            request: function (config) {
                if (config.disableInterceptors) return config;
                config.headers['X-Requested-With'] = 'Fetch';
                config.headers['X-CSRF-Token'] = csrfToken ? csrfToken : 'Fetch';
                return config;
            },
            response: function (response) {
                if (response.config.disableInterceptors) return response;
                const token = response.headers()['x-csrf-token'];
                if (token) {
                    csrfToken = token;
                    uploader.headers['X-CSRF-Token'] = csrfToken;
                }
                return response;
            }
        };
    }).config(['$httpProvider', function ($httpProvider) {
        $httpProvider.interceptors.push('baseHttpInterceptor');
    }]).factory('Views', function (Extensions) {
        let cachedViews;
        let cachedSubviews;
        const errorHandler = (error) => {
            console.error(error);
            reject(error);
        };
        const getViews = function (id) {
            if (cachedViews) {
                return new Promise((resolve) => {
                    if (id) {
                        resolve(cachedViews.find(v => v.id === id));
                    } else resolve(cachedViews);
                });
            } else {
                return Extensions.getViews().then(function (response) {
                    cachedViews = response.data;
                    if (id) return cachedViews.find(v => v.id === id);
                    return cachedViews;
                }, errorHandler);
            }
        };
        const getSubviews = function (id) {
            if (cachedSubviews) {
                return new Promise((resolve) => {
                    resolve(cachedSubviews);
                });
            } else {
                return Extensions.getSubviews().then(function (response) {
                    cachedSubviews = response.data;
                    if (id) return cachedSubviews.find(v => v.id === id);
                    return cachedSubviews;
                }, errorHandler);
            }
        };
        return {
            getViews: getViews,
            getSubviews: getSubviews,
        };
    }).factory('ViewParameters', function ($window) {
        return {
            get: function () {
                if ($window.frameElement && $window.frameElement.hasAttribute("data-parameters")) {
                    return JSON.parse($window.frameElement.getAttribute("data-parameters"));
                }
                return {};
            }
        };
    }).directive('embeddedView', function (Views, perspective, shell) {
        /**
         * viewId: String - ID of the view you want to show.
         * params: JSON - JSON object containing extra parameters/data.
         * type: String - Type of the view. Available options - 'view' (default), 'subview'.
         */
        return {
            restrict: 'E',
            transclude: false,
            replace: true,
            scope: {
                viewId: '@',
                params: '<?',
                type: '@?',
            },
            link: function (scope) {
                if (scope.params !== undefined && !(typeof scope.params === 'object' && !Array.isArray(scope.params) && scope.params !== null))
                    throw Error("embeddedView: params must be an object");
                const types = ['view', 'subview'];
                if (scope.type !== undefined && !types.includes(scope.type))
                    throw Error(`embeddedView: wrong view type. Available options - ${types.join(', ')}`);

                function setView(viewConfig) {
                    if (viewConfig) {
                        scope.$evalAsync(() => {
                            scope.view = viewConfig;
                            scope.view.params = JSON.stringify({
                                ...viewConfig.params,
                                ...scope.params,
                                container: shell.id ? 'shell' : perspective.id ? 'perspective' : 'view',
                                perspectiveId: perspective.id,
                                shellId: shell.id
                            });
                            scope.viewLabel = scope.view.label;
                        });
                    } else {
                        throw Error(`embeddedView: ${scope.type ?? types[0]} with id '${scope.viewId}' not found`);
                    }
                }

                if (scope.type === types[1]) Views.getSubviews(scope.viewId).then(setView);
                else Views.getViews(scope.viewId).then(setView);
            },
            template: `<iframe title={{::view.label}} loading="{{::view.lazyLoad ? 'lazy' : 'eager'}}" ng-src="{{::view.path}}" data-parameters="{{::view.params}}"></iframe>`
        }
    }).directive('configTitle', (perspective, view) => ({
        restrict: 'A',
        transclude: false,
        replace: false,
        link: (scope) => {
            if (perspective.label) scope.label = perspective.label;
            else if (view.label) scope.label = view.label;
            else throw Error("configTitle: missing data");
        },
        template: '{{::label}}'
    }));
