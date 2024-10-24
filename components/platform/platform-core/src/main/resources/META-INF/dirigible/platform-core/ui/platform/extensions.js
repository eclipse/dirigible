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
angular.module('platformExtensions', [])
    /*
     * The 'extensionPoints' constant can be used to replace the default extension points from your controller.
     * Here is an example:
     * .constant('extensionPoints', {
     *     perspectives: ["example-perspectives"],
     *     views: ["example-views"],
     *     subviews: ["example-subviews"],
     *     editors: ["example-editors"],
     *     menus: ["example-menus"],
     *     windows: ["example-windows"],
     *     themes: ["example-themes"]
     * })
     */
    .constant('extensionPoints', {})
    .factory('Extensions', ['$http', 'extensionPoints', function ($http, extensionPoints) {
        return {
            getViews: function (exPoints = extensionPoints.views) {
                return $http.get('/services/js/platform-core/extension-services/views.js', { params: { extensionPoints: exPoints }, cache: true });
            },
            getSubviews: function (exPoints = extensionPoints.subviews) {
                return $http.get('/services/js/platform-core/extension-services/views.js', { params: { extensionPoints: exPoints ?? ['platform-subviews'] }, cache: true });
            },
            getWindows: function (exPoints = extensionPoints.windows) {
                return $http.get('/services/js/platform-core/extension-services/views.js', { params: { extensionPoints: exPoints ?? ['platform-windows'] }, cache: true });
            },
            getEditors: function (exPoints = extensionPoints.editors) {
                return $http.get('/services/js/platform-core/extension-services/editors.js', { params: { extensionPoints: exPoints }, cache: true });
            },
            getPerspectives: function (exPoints = extensionPoints.perspectives) {
                return $http.get('/services/js/platform-core/extension-services/perspectives.js', { params: { extensionPoints: exPoints }, cache: true });
            },
            getMenus: function (exPoints = extensionPoints.menus) {
                return $http.get('/services/js/platform-core/extension-services/menus.js', { params: { extensionPoints: exPoints }, cache: true });
            },
            getThemes: function (exPoints = extensionPoints.themes) {
                return $http.get('/services/js/platform-core/extension-services/themes.js', { params: { extensionPoints: exPoints }, cache: true });
            },
        };
    }]);