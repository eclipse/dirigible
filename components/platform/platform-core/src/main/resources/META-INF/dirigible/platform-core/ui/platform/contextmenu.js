/*
 * Copyright (c) 2010-2024 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
angular.module('platformContextMenu', []).directive('contextMenu', function (MessageHub) {
    return {
        restrict: "E",
        replace: true,
        controller: ['$scope', '$element', function ($scope, $element) {
            $scope.menuConfig = {
                items: []
            };
            MessageHub.addMessageListener({
                topic: 'platform.contextmenu',
                handler: (config) => {
                    if (!$scope.menuConfig.items.length) $scope.$apply(function () {
                        $scope.menuConfig = config;
                    });
                }
            });

            $scope.backdropStyle = () => ({
                display: $scope.menuConfig.items.length ? 'block' : 'none',
                'background-color': 'transparent',
                position: 'fixed',
                width: '100%',
                height: '100%',
                top: 0,
                left: 0,
                'z-index': 220, //Just below the dialogs, just above the backdrop elements
            });

            $scope.getStyle = () => ({
                position: 'fixed',
                top: `${$scope.menuConfig.posY}px`,
                left: `${$scope.menuConfig.posX}px`
            });

            $scope.canScroll = () => {
                for (let i = 0; i < $scope.menuConfig.items.length; i++) {
                    if ($scope.menuConfig.items[i].items) return false;
                }
                return false
            };

            this.itemClick = (itemId, event) => {
                if (event) {
                    event.preventDefault();
                    if ($element[0].firstElementChild.contains(event.target)) return;
                }
                if (itemId)
                    MessageHub.postMessage({ topic: $scope.menuConfig.topic, data: itemId });
                else MessageHub.triggerEvent($scope.menuConfig.topic);
                $scope.menuConfig.items.length = 0;
            }

            $scope.itemClick = this.itemClick;
        }],
        template: `<div ng-style="backdropStyle()" ng-click="itemClick(undefined, $event)" ng-on-contextmenu="itemClick(undefined, $event)">
        <bk-menu ng-if="menuConfig.items.length" ng-style="getStyle()" aria-label="{{menuConfig.ariaLabel || 'system contextmenu'}}" no-backdrop="true" can-scroll="canScroll()" has-icons="menuConfig.icons">
            <context-menu-submenu items="menuConfig.items"></context-menu-submenu>
        </bk-menu><div>`,
    };
}).directive('contextMenuSubmenu', function () {
    return {
        restrict: "E",
        replace: true,
        require: '^^contextMenu',
        scope: {
            items: '='
        },
        link: function (scope, _element, _attrs, contextMenuCtrl) {
            scope.canScroll = () => {
                for (let i = 0; i < scope.items.length; i++) {
                    if (scope.items[i].items) return false;
                }
                return false
            };

            scope.itemClick = (itemId, event) => {
                contextMenuCtrl.itemClick(itemId, event);
            };
        },
        template: `<div><bk-menu-item ng-repeat-start="item in items track by item.id" ng-if="!item.items" title="{{item.label}}" ng-click="!item.disabled && itemClick(item.id)" is-disabled="item.disabled" has-separator="item.separator"></bk-menu-item>
        <bk-menu-sublist ng-if="item.items" title="{{item.label}}" can-scroll="canScroll()" is-disabled="item.disabled" has-separator="item.separator" ng-repeat-end><context-menu-submenu items="item.items"></context-menu-submenu></bk-menu-sublist></div>`,
    };
});