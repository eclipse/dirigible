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
angular.module('platformTheming', ['platformExtensions'])
    .constant('ThemingApi', new ThemingApi())
    .constant('themeStateKey', `${brandingInfo.keyPrefix}.platform.theme`)
    .provider('theming', function ThemingProvider() {
        this.$get = ['ThemingApi', 'Extensions', 'themeStateKey', function editorsFactory(ThemingApi, Extensions, themeStateKey) {
            let theme = JSON.parse(localStorage.getItem(themeStateKey) || '{}');
            let themes = [];

            Extensions.getThemes().then((response) => {
                themes = response.data;
                if (!theme.version) {
                    setTheme('blimpkit-light');
                } else {
                    for (let i = 0; i < themes.length; i++) {
                        if (themes[i].id === theme.id) {
                            if (themes[i].version !== theme.version) {
                                setThemeObject(themes[i]);
                                break;
                            }
                        }
                    }
                }
                ThemingApi.themesLoaded();
            });

            function setTheme(themeId, sendEvent = true) {
                for (let i = 0; i < themes.length; i++) {
                    if (themes[i].id === themeId) {
                        setThemeObject(themes[i], sendEvent);
                    }
                }
            }

            function setThemeObject(themeObj, sendEvent = true) {
                localStorage.setItem(
                    themeStateKey,
                    JSON.stringify(themeObj),
                )
                theme = themeObj;
                if (sendEvent) ThemingApi.themeChanged({
                    id: themeObj.id,
                    type: themeObj.type,
                    links: themeObj.links
                });
            }

            return {
                setTheme: setTheme,
                getThemes: () => themes.map((item) => ({
                    'id': item['id'],
                    'name': item['name']
                })),
                getCurrentTheme: () => ({
                    id: theme['id'] || 'blimpkit-light',
                    name: theme['name'] || 'BlimpKit Light',
                }),
                reset: () => {
                    // setting sendEvent to false because the application will reload anyway
                    setTheme('blimpkit-light', false);
                }
            }
        }];
    })
    .factory('Theme', ['theming', 'themeStateKey', function (_theming, themeStateKey) { // theming must be injected to set defaults
        let theme = JSON.parse(localStorage.getItem(themeStateKey) || '{}');
        return {
            reload: () => {
                theme = JSON.parse(localStorage.getItem(themeStateKey) || '{}');
            },
            getLinks: () => {
                return theme.links || [];
            },
            getType: () => {
                return theme.type || 'light';
            }
        }
    }]).directive('theme', (Theme, ThemingApi, $document) => ({
        restrict: 'E',
        replace: true,
        transclude: false,
        link: (scope) => {
            scope.links = Theme.getLinks();
            if (Theme.getType() === 'dark') {
                $document[0].body.classList.add('bk-dark');
            } else $document[0].body.classList.add('bk-light');
            const themeChangeListener = ThemingApi.onThemeChange((themeData) => {
                if (themeData.type === 'dark') {
                    $document[0].body.classList.add('bk-dark');
                    $document[0].body.classList.remove('bk-light');
                } else {
                    $document[0].body.classList.add('bk-light');
                    $document[0].body.classList.remove('bk-dark');
                }
                scope.$applyAsync(() => {
                    scope.links = themeData.links;
                });
            });
            scope.$on('$destroy', () => {
                ThemingApi.removeMessageListener(themeChangeListener);
            });
        },
        template: '<link type="text/css" rel="stylesheet" ng-repeat="link in links" ng-href="{{ link }}">'
    }));