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
    .constant('MessageHub', new MessageHubApi())
    .provider('theming', function ThemingProvider() {
        this.$get = ['MessageHub', 'Extensions', function editorsFactory(MessageHub, Extensions) {
            let theme = JSON.parse(localStorage.getItem('platform.theme') || '{}');
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
                MessageHub.triggerEvent('platform.shell.themes.loaded');
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
                    'platform.theme',
                    JSON.stringify(themeObj),
                )
                theme = themeObj;
                if (sendEvent) MessageHub.triggerEvent('platform.shell.themes.change');
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
                    // setting sendEvent to false because of the reload caused by Golden Layout
                    setTheme('quartz-light', false);
                }
            }
        }];
    })
    .factory('Theme', ['theming', function (_theming) { // Must be injected to set defaults
        let theme = JSON.parse(localStorage.getItem('platform.theme') || '{}');
        return {
            reload: function () {
                theme = JSON.parse(localStorage.getItem('platform.theme') || '{}');
            },
            getLinks: function () {
                return theme.links || [];
            },
            getType: function () {
                return theme.type || 'light';
            }
        }
    }]).directive('theme', ['Theme', 'MessageHub', '$document', function (Theme, MessageHub, $document) {
        return {
            restrict: 'E',
            replace: true,
            transclude: false,
            link: function (scope) {
                scope.links = Theme.getLinks();
                if (Theme.getType() === 'dark') {
                    $document[0].body.classList.add('bk-dark');
                } else $document[0].body.classList.add('bk-light');
                const themeChangeListener = MessageHub.addMessageListener({
                    topic: 'platform.shell.themes.change',
                    handler: () => {
                        scope.$apply(function () {
                            Theme.reload();
                            scope.links = Theme.getLinks();
                            if (Theme.getType() === 'dark') {
                                $document[0].body.classList.add('bk-dark');
                                $document[0].body.classList.remove('bk-light');
                            } else {
                                $document[0].body.classList.add('bk-light');
                                $document[0].body.classList.remove('bk-dark');
                            }
                        });
                    }
                });
                scope.$on('$destroy', function () {
                    MessageHub.removeMessageListener(themeChangeListener);
                });
            },
            template: '<link type="text/css" rel="stylesheet" ng-repeat="link in links" ng-href="{{ link }}">'
        };
    }]);