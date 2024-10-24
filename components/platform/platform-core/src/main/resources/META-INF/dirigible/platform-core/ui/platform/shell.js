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
angular.module('platformShell', ['ngCookies', 'platformUser', 'platformBrand', 'platformExtensions', 'platformTheming', 'platformDialogs', 'platformContextMenu'])
    .value('shellState', {
        perspectiveInternal: {
            id: '',
            label: ''
        },
        perspectiveListeners: [],
        set perspective(newData) {
            this.perspectiveInternal = newData;
            for (let l = 0; l < this.perspectiveListeners.length; l++) {
                this.perspectiveListeners[l](newData);
            }
        },
        get perspective() {
            return this.perspectiveInternal;
        },
        registerStateListener: function (listener) {
            this.perspectiveListeners.push(listener);
        }
    })
    .constant('MessageHub', new MessageHubApi())
    .config(function config($compileProvider) {
        $compileProvider.debugInfoEnabled(false);
        $compileProvider.commentDirectivesEnabled(false);
        $compileProvider.cssClassDirectivesEnabled(false);
    }).directive('shellHeader', function ($cookies, $http, $window, branding, theming, User, Extensions, shellState, MessageHub, ButtonStates) {
        return {
            restrict: 'E',
            replace: true,
            link: function (scope, element) {
                const dialogApi = new DialogApi();
                scope.themes = [];
                scope.perspectiveId = shellState.perspective.id;
                shellState.registerStateListener((data) => {
                    scope.perspectiveId = data.id;
                    scope.collapseMenu = false;
                });
                scope.branding = branding;
                scope.currentTheme = theming.getCurrentTheme();
                scope.username = undefined;
                User.getName().then(function (data) {
                    scope.username = data.data;
                });
                scope.menus = {};
                scope.systemMenus = {
                    help: undefined,
                    window: undefined
                };
                scope.menu = [];
                scope.collapseMenu = false;

                Extensions.getMenus().then(function (response) {
                    for (let i = 0; i < response.data.length; i++) {
                        if (!response.data[i].systemMenu) {
                            scope.menus[response.data[i].perspectiveId] = {
                                include: response.data[i].include,
                                items: response.data[i].items
                            }
                        } else {
                            if (response.data[i].id === 'help') {
                                scope.systemMenus.help = response.data[i].menu;
                            } else if (response.data[i].id === 'window') {
                                scope.systemMenus.window = response.data[i].menu;
                            }
                        }
                    }
                });

                let thresholdWidth = 0;
                const resizeObserver = new ResizeObserver((entries) => {
                    if (scope.collapseMenu && element[0].offsetWidth > thresholdWidth) {
                        return scope.$apply(() => scope.collapseMenu = false);
                    } else if (entries[0].contentRect.width === 0) {
                        thresholdWidth = element[0].offsetWidth;
                        scope.$apply(() => scope.collapseMenu = true);
                    }
                });
                resizeObserver.observe(element.find('#spacer')[0]);

                const themesLoadedListener = MessageHub.addMessageListener({
                    topic: 'platform.shell.themes.loaded',
                    handler: () => scope.themes = theming.getThemes()
                });

                scope.menuClick = function (item) {
                    if (item.action === 'openView') {
                        MessageHub.postMessage({
                            topic: 'platform.layout.view.open',
                            data: { id: item.id, params: item.data }
                        });
                    } else if (item.action === 'showPerspective') {
                        shellState.perspective = {
                            id: item.id,
                            label: item.label
                        };
                    } else if (item.action === 'openWindow') {
                        dialogApi.showWindow({
                            hasHeader: item.hasHeader,
                            id: item.windowId
                        });
                    } else if (item.action === 'open') {
                        $window.open(item.data, '_blank');
                    } else if (item.event) {
                        MessageHub.postMessage({ topic: item.event, data: item.data });
                    }
                };

                scope.setTheme = function (themeId, name) {
                    scope.currentTheme.id = themeId;
                    scope.currentTheme.name = name;
                    theming.setTheme(themeId);
                };

                scope.isScrollable = function (items) {
                    if (items) {
                        for (let i = 0; i < items.length; i++)
                            if (items[i].items) return false;
                    }
                    return true;
                };

                scope.resetAll = function () {
                    dialogApi.showDialog({
                        title: `Reset ${scope.branding.brand}`,
                        message: `This will clear all settings, open tabs and cache.\n${scope.branding.brand} will then reload.\nDo you wish to continue?`,
                        buttons: [
                            { id: 'yes', label: 'Yes', state: ButtonStates.Emphasized },
                            { id: 'no', label: 'No' }
                        ],
                        closeButton: false
                    }).then(function (buttonId) {
                        if (buttonId === "yes") {
                            dialogApi.showBusyDialog('Resetting...');
                            localStorage.clear();
                            theming.reset();
                            $http.get('/services/js/platform-core/services/clear-cache.js').then(function () {
                                for (let cookie in $cookies.getAll()) {
                                    if (cookie.startsWith('DIRIGIBLE')) {
                                        $cookies.remove(cookie, { path: '/' });
                                    }
                                }
                                location.reload();
                            }, function (error) {
                                console.log(error);
                                dialogApi.closeBusyDialog();
                                dialogApi.showAlert({
                                    title: 'Failed to reset',
                                    message: 'There was an error during the reset process. Please refresh manually.',
                                    type: AlertTypes.Error,
                                    preformatted: false,
                                });
                            });
                        }
                    });
                };

                scope.logout = function () {
                    location.replace('/logout');
                };

                scope.$on('$destroy', function () {
                    MessageHub.removeMessageListener(themesLoadedListener);
                });
            },
            templateUrl: '/services/web/platform-core/ui/templates/header.html',
        };
    }).directive('submenu', function () {
        return {
            restrict: "E",
            replace: false,
            scope: {
                sublist: '<',
                menuHandler: '&',
            },
            link: function (scope) {
                scope.menuHandler = scope.menuHandler();
                scope.isScrollable = function (index) {
                    for (let i = 0; i < scope.sublist[index].items.length; i++)
                        if (scope.sublist[index].items[i].items) return false;
                    return true;
                };
            },
            template: `<bk-menu-item ng-repeat-start="item in sublist track by $index" ng-if="!item.items" has-separator="::item.divider" title="{{ ::item.label }}" ng-click="::menuHandler(item)"></bk-menu-item>
            <bk-menu-sublist ng-if="item.items" has-separator="::item.divider" title="{{ ::item.label }}" can-scroll="::isScrollable($index)" ng-repeat-end><submenu sublist="::item.items" menu-handler="::menuHandler"></submenu></bk-menu-sublist>`,
        };
    }).directive('perspectiveContainer', function (Extensions, shellState) {
        /**
         * condensed: Boolean - If the side navigation should show both icons and labels.
         * config: Object - Sidebar configuration containing perspectives and perspective groups. 
         */
        return {
            restrict: 'E',
            transclude: true,
            replace: true,
            scope: {
                condensed: '<?',
                config: '=?'
            },
            link: {
                pre: (scope, element) => {
                    scope.noStatusbar = true;
                    for (let i = 0; i < element[0].parentElement.children.length; i++) {
                        if (element[0].parentElement.children[i].classList.contains('bk-statusbar')) {
                            scope.noStatusbar = false;
                            break;
                        }
                    }
                },
                post: function (scope) {
                    function setDefaultPerspective() {
                        if (scope.config.perspectives.length) {
                            if (scope.config.perspectives[0].items) {
                                scope.activeId = scope.config.perspectives[0].items[0].id;
                                shellState.perspective = {
                                    id: scope.config.perspectives[0].items[0].id,
                                    label: scope.config.perspectives[0].items[0].label
                                };
                            } else {
                                scope.activeId = scope.config.perspectives[0].id;
                                shellState.perspective = {
                                    id: scope.config.perspectives[0].id,
                                    label: scope.config.perspectives[0].label
                                };
                            }
                        }
                    }
                    if (!scope.config) {
                        Extensions.getPerspectives().then(function (response) {
                            scope.config = response.data;
                            setDefaultPerspective();
                        });
                    } else {
                        setDefaultPerspective();
                    }
                    scope.isActive = (id, groupId) => {
                        if (id === scope.activeId) {
                            scope.activeGroupId = groupId;
                            return true;
                        }
                        return false;
                    }
                    scope.getIcon = (icon) => {
                        if (icon) return icon;
                        return '/services/web/platform-core/ui/images/unknown.svg';
                    };
                    scope.switchPerspective = (id, label) => {
                        scope.activeId = id;
                        shellState.perspective = {
                            id: id,
                            label: label
                        };
                    };

                    scope.getDataParams = (params = {}) => {
                        return JSON.stringify({
                            container: 'shell',
                            ...params
                        });
                    };
                },
            },
            template: `<div class="bk-main-container" no-statusbar="{{::noStatusbar}}">
                <bk-vertical-nav class="bk-sidebar" condensed="condensed" can-scroll="true">
                    <bk-vertical-nav-main-section aria-label="perspective navigation">
                        <bk-list aria-label="Perspective list" ng-if="!condensed">
                            <bk-list-navigation-group-header ng-repeat-start="navItem in config.perspectives track by navItem.id" ng-if="navItem.items && navItem.headerLabel">{{::navItem.headerLabel}}</bk-list-navigation-group-header>
                            <bk-list-navigation-item ng-if="navItem.items" expandable="true" ng-click="navItem.expanded = !navItem.expanded" is-expanded="navItem.expanded">
                                <bk-list-navigation-item-icon icon-size="lg" svg-path="{{getIcon(navItem.icon)}}"></bk-list-navigation-item-icon>
                                <span bk-list-navigation-item-text>{{::navItem.label}}</span>
                                <bk-list-navigation-item-arrow aria-label="expand perspective group" is-expanded="navItem.expanded"></bk-list-navigation-item-arrow>
                                <bk-list>
                                    <bk-list-navigation-item ng-repeat="navGroupItem in navItem.items track by navGroupItem.id" ng-click="$event.stopPropagation();switchPerspective(navGroupItem.id, navGroupItem.label)">
                                        <span bk-list-navigation-item-text>{{navGroupItem.label}}</span>
                                        <bk-list-navigation-item-indicator ng-if="isActive(navGroupItem.id, navItem.id)"></bk-list-navigation-item-indicator>
                                    </bk-list-navigation-item>
                                </bk-list>
                                <bk-list-navigation-item-indicator ng-if="navItem.id === activeGroupId"></bk-list-navigation-item-indicator>
                            </bk-list-navigation-item>
                            <bk-list-navigation-group-header ng-if="!navItem.items && navItem.headerLabel">{{::navItem.headerLabel}}</bk-list-navigation-group-header>
                            <bk-list-navigation-item ng-repeat-end ng-if="!navItem.items" ng-click="switchPerspective(navItem.id, navItem.label)" title="{{::navItem.label}}">
                                <bk-list-navigation-item-icon icon-size="lg" svg-path="{{getIcon(navItem.icon)}}"></bk-list-navigation-item-icon>
                                <span bk-list-navigation-item-text>{{::navItem.label}}</span>
                                <bk-list-navigation-item-indicator ng-if="navItem.id === activeId"></bk-list-navigation-item-indicator>
                            </bk-list-navigation-item>
                        </bk-list>
                        <bk-list aria-label="Perspective list" ng-if="condensed">
                            <bk-list-navigation-item ng-repeat="navItem in config.perspectives track by navItem.id" ng-click="switchPerspective(navItem.id, navItem.label)" title="{{::navItem.label}}">
                                <bk-list-navigation-item-icon icon-size="lg" svg-path="{{getIcon(navItem.icon)}}"></bk-list-navigation-item-icon>
                                <span bk-list-navigation-item-text>{{::navItem.label}}</span>
                                <bk-list-navigation-item-indicator ng-if="navItem.id === activeId"></bk-list-navigation-item-indicator>
                            </bk-list-navigation-item>
                        </bk-list>
                    </bk-vertical-nav-main-section>
                    <bk-vertical-nav-utility-section ng-if="config.utilities.length" aria-label="utility navigation">
                        <bk-list>
                            <bk-list-navigation-item ng-repeat="navItem in config.utilities track by navItem.id" ng-click="switchPerspective(navItem.id, navItem.label)" title="{{::navItem.label}}">
                                <bk-list-navigation-item-icon icon-size="lg" svg-path="{{getIcon(navItem.icon)}}"></bk-list-navigation-item-icon>
                                <span bk-list-navigation-item-text>{{::navItem.label}}</span>
                                <bk-list-navigation-item-indicator ng-if="navItem.id === activeId"></bk-list-navigation-item-indicator>
                            </bk-list-navigation-item>
                        </bk-list>
                    </bk-vertical-nav-utility-section>
                </bk-vertical-nav>
                <iframe ng-repeat-start="perspective in config.perspectives track by perspective.id" ng-if="!perspective.items" ng-show="perspective.id === activeId" title="{{::perspective.label}}" ng-src="{{::perspective.path}}" data-parameters="{{getDataParams(perspective.params)}}" loading="lazy"></iframe>
                <iframe ng-repeat-end ng-if="perspective.items" ng-repeat="subperspective in perspective.items track by subperspective.id" ng-show="subperspective.id === activeId" title="{{::subperspective.label}}" ng-src="{{::subperspective.path}}" data-parameters="{{getDataParams(subperspective.params)}}" loading="lazy"></iframe>
                <iframe ng-repeat="perspective in config.utilities track by perspective.id" ng-show="perspective.id === activeId" title="{{::perspective.label}}" ng-src="{{::perspective.path}}" data-parameters="{{getDataParams(perspective.params)}}" loading="lazy"></iframe>
            </div>`
        }
    }).directive('statusBar', function (MessageHub) {
        return {
            restrict: 'E',
            replace: true,
            link: function (scope) {
                scope.busy = '';
                scope.message = '';
                scope.label = '';
                scope.error = '';
                MessageHub.addMessageListener({
                    topic: 'platform.shell.status.busy',
                    handler: (text) => {
                        scope.$apply(function () {
                            scope.busy = text;
                        });
                    }
                });
                MessageHub.addMessageListener({
                    topic: 'platform.shell.status.message',
                    handler: (message) => {
                        scope.$apply(function () {
                            scope.message = message;
                        });
                    }
                });
                MessageHub.addMessageListener({
                    topic: 'platform.shell.status.error',
                    handler: (message) => {
                        scope.$apply(function () {
                            scope.error = message;
                        });
                    }
                });
                MessageHub.addMessageListener({
                    topic: 'platform.shell.status.label',
                    handler: (label) => {
                        scope.$apply(function () {
                            scope.label = label;
                        });
                    }
                });
            },
            template: `<div class="bk-statusbar">
                <div class="bk-statusbar-busy" ng-if="busy">
                    <bk-busy-indicator size="m"></bk-busy-indicator>
                    <span class="bk-statusbar__text">{{ busy }}</span>
                </div>
                <div class="bk-statusbar-message" ng-style="{'visibility': message ? 'visible':'hidden'}">
                    <i class="bk-statusbar__icon sap-icon--information"></i>
                    <span class="bk-statusbar__text">{{ message }}</span>
                    <i class="bk-statusbar__icon bk-statusbar--link sap-icon--delete" ng-click="message = ''"></i>
                </div>
                <div class="bk-statusbar-error" ng-style="{'visibility': error ? 'visible':'hidden'}">
                    <i class=" bk-statusbar__icon sap-icon--message-warning"></i>
                    <span class="bk-statusbar__text">{{ error }}</span>
                    <i class="bk-statusbar__icon bk-statusbar--link sap-icon--delete" ng-click="error = ''"></i>
                </div>
                <div class="bk-statusbar-label">{{ label }}</div>
            </div>`
        }
    });