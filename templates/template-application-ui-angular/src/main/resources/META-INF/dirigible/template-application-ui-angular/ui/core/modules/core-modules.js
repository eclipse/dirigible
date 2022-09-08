/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
/*
 * Provides key microservices for constructing and managing the IDE UI
 */
#set($dollar = '$')
angular.module('idePerspective', ['ngResource', 'ngCookies', 'ideTheming', 'ideMessageHub'])
    .constant('branding', brandingInfo)
    .constant('perspective', perspectiveData)
    .service('Perspectives', ['$resource', function ($resource) {
        return $resource('/services/v4/js/${projectName}/gen/ui/core/services/perspectives.js');
    }])
    .service('Menu', ['$resource', function ($resource) {
        return $resource('/services/v4/js/${projectName}/gen/ui/core/services/menu.js');
    }])
    .service('User', ['$http', function ($http) {
        return {
            get: function () {
                let user = {};
                $http({
                    url: '/services/v4/js/${projectName}/gen/ui/core/services/user-name.js',
                    method: 'GET'
                }).then(function (data) {
                    user.name = data.data;
                });
                return user;
            }
        };
    }])
    .service('DialogWindows', ['$resource', function ($resource) {
        return $resource('/services/v4/js/${projectName}/gen/ui/core/services/dialog-windows.js');
    }])
    .filter('removeSpaces', [function () {
        return function (string) {
            if (!angular.isString(string)) return string;
            return string.replace(/[\s]/g, '');
        };
    }])
    .directive('dgBrandTitle', ['perspective', 'branding', function (perspective, branding) {
        return {
            restrict: 'A',
            transclude: false,
            replace: true,
            link: function (scope) {
                scope.name = branding.name;
                scope.perspective = perspective;
            },
            template: '<title>{{perspective.name || "Loading..."}} | {{name}}</title>'
        };
    }])
    .directive('dgBrandIcon', ['branding', function (branding) {
        return {
            restrict: 'A',
            transclude: false,
            replace: true,
            link: function (scope) {
                scope.icon = branding.icons.faviconIco;
            },
            template: `<link rel="icon" type="image/x-icon" ng-href="{{icon}}">`
        };
    }])
    .directive('ideContextmenu', ['messageHub', '$window', function (messageHub, $window) {
        return {
            restrict: 'E',
            replace: true,
            link: function (scope, element) {
                let openedMenuId = "";
                let menu = element[0].querySelector(".fd-menu");
                scope.menuItems = [];
                scope.callbackTopic = "";

                scope.menuClick = function (itemId, data, isDisabled = false) {
                    if (!isDisabled)
                        messageHub.postMessage(scope.callbackTopic, { itemId: itemId, data: data }, true);
                };

                element.on('click', function (event) {
                    event.stopPropagation();
                    element[0].classList.add("dg-hidden");
                    menu.classList.add('dg-invisible');
                    openedMenuId = '';
                    scope.hideAllSubmenus();
                });

                element.on('contextmenu', function (event) {
                    event.preventDefault();
                    event.stopPropagation();
                    element[0].classList.add("dg-hidden");
                    menu.classList.add('dg-invisible');
                    openedMenuId = '';
                    scope.hideAllSubmenus();
                });

                scope.hideAllSubmenus = function () {
                    let submenus = element[0].querySelectorAll('.fd-menu__sublist[aria-hidden="false"]');
                    let submenusLinks = element[0].querySelectorAll('.is-expanded');
                    for (let i = 0; i < submenus.length; i++)
                        submenus[i].setAttribute("aria-hidden", true);
                    for (let i = 0; i < submenusLinks.length; i++) {
                        submenusLinks[i].setAttribute("aria-expanded", false);
                        submenusLinks[i].classList.remove("is-expanded");
                    }
                }

                scope.menuHovered = function () {
                    if (openedMenuId !== "") {
                        let oldSubmenu = element[0].querySelector(`#${dollar}{openedMenuId}`);
                        let oldSubmenuLink = element[0].querySelector(`span[aria-controls="${dollar}{openedMenuId}"]`);
                        oldSubmenuLink.setAttribute("aria-expanded", false);
                        oldSubmenuLink.classList.remove("is-expanded");
                        oldSubmenu.setAttribute("aria-hidden", true);
                        openedMenuId = "";
                    }
                };

                scope.showSubmenu = function (submenuId) {
                    scope.hideAllSubmenus();
                    openedMenuId = submenuId;
                    let submenu = element[0].querySelector(`#${dollar}{submenuId}`);
                    let submenus = submenu.querySelectorAll('.fd-menu__sublist');
                    let submenusLinks = submenu.querySelectorAll('.is-expanded');
                    for (let i = 0; i < submenus.length; i++)
                        submenus[i].setAttribute("aria-hidden", true);
                    for (let i = 0; i < submenusLinks.length; i++) {
                        submenusLinks[i].setAttribute("aria-expanded", false);
                        submenusLinks[i].classList.remove("is-expanded");
                    }
                    let submenuLink = element[0].querySelector(`span[aria-controls="${dollar}{submenuId}"]`);
                    submenuLink.setAttribute("aria-expanded", true);
                    submenuLink.classList.add("is-expanded");
                    submenu.setAttribute("aria-hidden", false);
                    requestAnimationFrame(function () {
                        let rect = submenu.getBoundingClientRect();
                        let bottom = $window.innerHeight - rect.bottom;
                        let right = $window.innerWidth - rect.right;
                        if (bottom < 0) submenu.style.top = `${dollar}{bottom}px`;
                        if (right < 0) submenu.style.left = `${dollar}{right}px`;
                    });
                };

                messageHub.onDidReceiveMessage(
                    'ide-contextmenu.open',
                    function (msg) {
                        scope.$apply(function () {
                            scope.menuItems = msg.data.items;
                            scope.callbackTopic = msg.data.callbackTopic;
                            menu.style.top = `${dollar}{msg.data.posY}px`;
                            menu.style.left = `${dollar}{msg.data.posX}px`;
                            element[0].classList.remove("dg-hidden");
                            requestAnimationFrame(function () {
                                let rect = menu.getBoundingClientRect();
                                let bottom = $window.innerHeight - rect.bottom;
                                let right = $window.innerWidth - rect.right;
                                if (bottom < 0) menu.style.top = `${dollar}{rect.top + bottom}px`;
                                if (right < 0) menu.style.left = `${dollar}{rect.left + right}px`;
                                menu.classList.remove('dg-invisible');
                            });
                        });
                    },
                    true
                );
            },
            templateUrl: '/services/v4/web/${projectName}/gen/ui/core/modules/templates/contextmenu.html'
        };
    }])
    .directive('ideContextmenuSubmenu', ['$window', function ($window) {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                menuClick: "&",
                menuItems: "<",
                submenuIndex: "<",
            },
            link: function (scope, element, attr) {
                let openedMenuId = "";
                scope.menuClick = scope.menuClick();

                scope.isScrollable = function () {
                    for (let i = 0; i < scope.menuItems.length; i++)
                        if (scope.menuItems[i].items) return "";
                    return "fd-scrollbar fd-menu--overflow dg-menu__sublist--overflow";
                }

                scope.menuHovered = function () {
                    if (openedMenuId !== "" && openedMenuId !== attr["id"]) {
                        let oldSubmenu = element[0].querySelector(`#${dollar}{openedMenuId}`);
                        let oldSubmenuLink = element[0].querySelector(`span[aria-controls="${dollar}{openedMenuId}"]`);
                        oldSubmenuLink.setAttribute("aria-expanded", false);
                        oldSubmenuLink.classList.remove("is-expanded");
                        oldSubmenu.setAttribute("aria-hidden", true);
                        openedMenuId = "";
                    }
                };

                scope.showSubmenu = function (submenuId) {
                    openedMenuId = submenuId;
                    let submenu = element[0].querySelector(`#${dollar}{submenuId}`);
                    let submenus = submenu.querySelectorAll('.fd-menu__sublist');
                    let submenusLinks = submenu.querySelectorAll('.is-expanded');
                    for (let i = 0; i < submenus.length; i++)
                        submenus[i].setAttribute("aria-hidden", true);
                    for (let i = 0; i < submenusLinks.length; i++) {
                        submenusLinks[i].setAttribute("aria-expanded", false);
                        submenusLinks[i].classList.remove("is-expanded");
                    }
                    let submenuLink = element[0].querySelector(`span[aria-controls="${dollar}{submenuId}"]`);
                    submenuLink.setAttribute("aria-expanded", true);
                    submenuLink.classList.add("is-expanded");
                    submenu.setAttribute("aria-hidden", false);
                    requestAnimationFrame(function () {
                        let rect = submenu.getBoundingClientRect();
                        let bottom = $window.innerHeight - rect.bottom;
                        let right = $window.innerWidth - rect.right;
                        if (bottom < 0) submenu.style.top = `${dollar}{bottom}px`;
                        if (right < 0) submenu.style.left = `${dollar}{right}px`;
                    });
                };
            },
            templateUrl: '/services/v4/web/${projectName}/gen/ui/core/modules/templates/contextmenuSubmenu.html'
        };
    }])
    .directive('ideHeader', ['$window', '$cookies', '$resource', 'branding', 'theming', 'User', 'Menu', 'messageHub', function ($window, $cookies, $resource, branding, theming, User, Menu, messageHub) {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                menuExtId: '@',
            },
            link: function (scope, element) {
                let isMenuOpen = false;
                scope.themes = [];
                scope.currentTheme = theming.getCurrentTheme();
                scope.user = User.get();
                let menuBackdrop = element[0].querySelector(".dg-menu__backdrop");
                let themePopover = element[0].querySelector("#themePopover");
                let themePopoverButton = element[0].querySelector("#themePopoverButton");
                let userPopover = element[0].querySelector("#userPopover");
                let userPopoverButton = element[0].querySelector("#userPopoverButton");

                menuBackdrop.addEventListener('click', function (event) {
                    event.stopPropagation();
                    scope.backdropEvent();
                });

                menuBackdrop.addEventListener('contextmenu', function (event) {
                    event.preventDefault();
                    event.stopPropagation();
                    scope.backdropEvent();
                });

                function toggleThemePopover(hidden = true) {
                    isMenuOpen = !hidden;
                    if (hidden) {
                        themePopover.classList.add("dg-hidden");
                        themePopover.setAttribute("aria-expanded", false);
                        themePopover.setAttribute("aria-hidden", true);
                        themePopoverButton.setAttribute("aria-expanded", false);
                    } else {
                        themePopover.classList.remove("dg-hidden");
                        themePopover.setAttribute("aria-expanded", true);
                        themePopover.setAttribute("aria-hidden", false);
                        themePopoverButton.setAttribute("aria-expanded", true);
                    }
                }

                function toggleUserPopover(hidden = true) {
                    isMenuOpen = !hidden;
                    if (hidden) {
                        userPopover.classList.add("dg-hidden");
                        userPopover.setAttribute("aria-expanded", false);
                        userPopover.setAttribute("aria-hidden", true);
                        userPopoverButton.setAttribute("aria-expanded", false);
                    } else {
                        userPopover.classList.remove("dg-hidden");
                        userPopover.setAttribute("aria-expanded", true);
                        userPopover.setAttribute("aria-hidden", false);
                        userPopoverButton.setAttribute("aria-expanded", true);
                    }
                }

                function loadMenu() {
                    Menu.query({ id: scope.menuExtId }).$promise
                        .then(function (data) {
                            scope.menu = data;
                        });
                }

                scope.branding = branding;

                scope.showBackdrop = function () {
                    menuBackdrop.classList.remove("dg-hidden");
                };

                scope.hideBackdrop = function () {
                    menuBackdrop.classList.add("dg-hidden");
                };

                scope.backdropEvent = function () {
                    messageHub.triggerEvent('header-menu.closeAll', true);
                    if (isMenuOpen) {
                        if (!themePopover.classList.contains("dg-hidden")) {
                            toggleThemePopover(true);
                        }
                        if (!userPopover.classList.contains("dg-hidden")) {
                            toggleUserPopover(true);
                        }
                    }
                    scope.hideBackdrop();
                };

                messageHub.onDidReceiveMessage(
                    'ide-header.menuOpened',
                    function () {
                        if (isMenuOpen) {
                            if (!themePopover.classList.contains("dg-hidden")) {
                                toggleThemePopover(true);
                            }
                            if (!userPopover.classList.contains("dg-hidden")) {
                                toggleUserPopover(true);
                            }
                        }
                        scope.showBackdrop();
                    },
                    true
                );

                messageHub.onDidReceiveMessage(
                    'ide-header.menuClosed',
                    function () {
                        if (!isMenuOpen) scope.hideBackdrop();
                    },
                    true
                );

                messageHub.onDidReceiveMessage(
                    'ide.themesLoaded',
                    function () {
                        scope.themes = theming.getThemes();
                    },
                    true
                );

                if (scope.menuExtId)
                    loadMenu.call(scope);

                scope.menuClick = function (item) {
                    if (item.action === 'openView') {
                        debugger
                        messageHub.openView(item.id, item.data);
                    } else if (item.action === 'openPerspective') {
                        debugger
                        messageHub.openPerspective(item.link);
                    } else if (item.action === 'openDialogWindow') {
                        messageHub.showDialogWindow(item.dialogId);
                    } else if (item.action === 'open') {
                        window.open(item.data, '_blank');
                    } else if (item.event) {
                        messageHub.postMessage(item.event, item.data, true);
                    }
                };

                scope.themeButtonClicked = function () {
                    toggleUserPopover(true);
                    if (themePopover.classList.contains("dg-hidden")) {
                        scope.showBackdrop();
                        messageHub.triggerEvent('header-menu.closeAll', true);
                        toggleThemePopover(false);
                    } else {
                        scope.hideBackdrop();
                        toggleThemePopover(true);
                    }
                };

                scope.userButtonClicked = function () {
                    toggleThemePopover(true);
                    if (userPopover.classList.contains("dg-hidden")) {
                        scope.showBackdrop();
                        messageHub.triggerEvent('header-menu.closeAll', true);
                        toggleUserPopover(false);
                    } else {
                        scope.hideBackdrop();
                        toggleUserPopover(true);
                    }
                };

                scope.setTheme = function (themeId, name) {
                    scope.currentTheme.id = themeId;
                    scope.currentTheme.name = name;
                    theming.setTheme(themeId);
                    toggleThemePopover(true);
                };

                scope.resetTheme = function () {
                    scope.resetViews();
                    for (let cookie in $cookies.getAll()) {
                        if (cookie.startsWith("DIRIGIBLE")) {
                            $cookies.remove(cookie, { path: "/" });
                        }
                    }
                };

                scope.resetViews = function () {
                    localStorage.clear();
                    theming.reset();
                    location.reload();
                };
            },
            templateUrl: '/services/v4/web/${projectName}/gen/ui/core/modules/templates/ideHeader.html'
        };
    }])
    .directive("headerHamburgerMenu", ['messageHub', function (messageHub) {
        return {
            restrict: "E",
            replace: true,
            scope: {
                menuList: "<",
                menuHandler: "&",
            },
            link: function (scope, element) {
                let isMenuOpen = false;
                scope.menuHandler = scope.menuHandler();

                messageHub.onDidReceiveMessage(
                    'header-menu.closeAll',
                    function () {
                        if (isMenuOpen) scope.hideAllMenus();
                    },
                    true
                );

                scope.menuClicked = function (menuButton) {
                    let menu = menuButton.parentElement.querySelector(".fd-menu");
                    if (menu.classList.contains("dg-hidden")) {
                        scope.hideAllSubmenus(menu);
                        scope.hideAllMenus();
                        let offset = menuButton.getBoundingClientRect();
                        menu.style.top = `${dollar}{offset.bottom}px`;
                        menu.style.left = `${dollar}{offset.left}px`;
                        menu.classList.remove("dg-hidden");
                        messageHub.triggerEvent('ide-header.menuOpened', true);
                        isMenuOpen = true;
                    } else {
                        menu.classList.add("dg-hidden");
                        messageHub.triggerEvent('ide-header.menuClosed', true);
                        isMenuOpen = false;
                    }
                };

                scope.hideAllMenus = function () {
                    let menus = element[0].querySelectorAll(".fd-menu");
                    for (let i = 0; i < menus.length; i++) {
                        if (!menus[i].classList.contains("dg-hidden")) menus[i].classList.add("dg-hidden");
                    }
                    messageHub.triggerEvent('ide-header.menuClosed', true);
                    isMenuOpen = false;
                };

                scope.hideAllSubmenus = function () {
                    let submenus = element[0].querySelectorAll('.fd-menu__sublist[aria-hidden="false"]');
                    let submenusLinks = element[0].querySelectorAll('.is-expanded');
                    for (let i = 0; i < submenus.length; i++)
                        submenus[i].setAttribute("aria-hidden", true);
                    for (let i = 0; i < submenusLinks.length; i++) {
                        submenusLinks[i].setAttribute("aria-expanded", false);
                        submenusLinks[i].classList.remove("is-expanded");
                    }
                };

                scope.showSubmenu = function (submenuId) {
                    scope.hideAllSubmenus();
                    let submenu = element[0].querySelector(`#${dollar}{submenuId}`);
                    let submenus = submenu.querySelectorAll('.fd-menu__sublist');
                    let submenusLinks = submenu.querySelectorAll('.is-expanded');
                    for (let i = 0; i < submenus.length; i++)
                        submenus[i].setAttribute("aria-hidden", true);
                    for (let i = 0; i < submenusLinks.length; i++) {
                        submenusLinks[i].setAttribute("aria-expanded", false);
                        submenusLinks[i].classList.remove("is-expanded");
                    }
                    let submenuLink = element[0].querySelector(`span[aria-controls="${dollar}{submenuId}"]`);
                    submenuLink.setAttribute("aria-expanded", true);
                    submenuLink.classList.add("is-expanded");
                    submenu.setAttribute("aria-hidden", false);
                };
            },
            templateUrl: "/services/v4/web/${projectName}/gen/ui/core/modules/templates/headerHamburgerMenu.html",
        };
    }])
    .directive("headerMenu", ['messageHub', function (messageHub) {
        return {
            restrict: "E",
            replace: true,
            scope: {
                menuList: "<",
                menuHandler: "&",
            },
            link: function (scope, element) {
                let isMenuOpen = false;
                let openedMenuId = "";
                scope.menuHandler = scope.menuHandler();

                messageHub.onDidReceiveMessage(
                    'header-menu.closeAll',
                    function () {
                        if (isMenuOpen) {
                            scope.hideAllMenus();
                            scope.hideAllSubmenus();
                        }
                    },
                    true
                );

                scope.isScrollable = function (menuItems) {
                    for (let i = 0; i < menuItems.length; i++)
                        if (menuItems[i].items) return '';
                    return 'fd-menu--overflow fd-scrollbar dg-headermenu--overflow';
                }

                scope.menuHovered = function () {
                    if (openedMenuId !== "") {
                        let oldSubmenu = element[0].querySelector(`#${dollar}{openedMenuId}`);
                        let oldSubmenuLink = element[0].querySelector(`span[aria-controls="${dollar}{openedMenuId}"]`);
                        oldSubmenuLink.setAttribute("aria-expanded", false);
                        oldSubmenuLink.classList.remove("is-expanded");
                        oldSubmenu.setAttribute("aria-hidden", true);
                        openedMenuId = "";
                    }
                };

                scope.menuClicked = function (menuButton) {
                    let menu = menuButton.parentElement.querySelector(".fd-menu");
                    if (menu.classList.contains("dg-hidden")) {
                        scope.hideAllSubmenus(menu);
                        scope.hideAllMenus();
                        let offset = menuButton.getBoundingClientRect();
                        menu.style.top = `${dollar}{offset.bottom}px`;
                        menu.style.left = `${dollar}{offset.left}px`;
                        menu.classList.remove("dg-hidden");
                        messageHub.triggerEvent('ide-header.menuOpened', true);
                        isMenuOpen = true;
                    } else {
                        menu.classList.add("dg-hidden");
                        messageHub.triggerEvent('ide-header.menuClosed', true);
                        isMenuOpen = false;
                    }
                };

                scope.hideAllMenus = function () {
                    let menus = element[0].querySelectorAll(".fd-menu");
                    for (let i = 0; i < menus.length; i++) {
                        if (!menus[i].classList.contains("dg-hidden")) menus[i].classList.add("dg-hidden");
                    }
                    messageHub.triggerEvent('ide-header.menuClosed', true);
                    isMenuOpen = false;
                };

                scope.hideAllSubmenus = function () {
                    let submenus = element[0].querySelectorAll('.fd-menu__sublist[aria-hidden="false"]');
                    let submenusLinks = element[0].querySelectorAll('.is-expanded');
                    for (let i = 0; i < submenus.length; i++)
                        submenus[i].setAttribute("aria-hidden", true);
                    for (let i = 0; i < submenusLinks.length; i++) {
                        submenusLinks[i].setAttribute("aria-expanded", false);
                        submenusLinks[i].classList.remove("is-expanded");
                    }
                };

                scope.showSubmenu = function (submenuId) {
                    scope.hideAllSubmenus();
                    openedMenuId = submenuId;
                    let submenu = element[0].querySelector(`#${dollar}{submenuId}`);
                    let submenus = submenu.querySelectorAll('.fd-menu__sublist');
                    let submenusLinks = submenu.querySelectorAll('.is-expanded');
                    for (let i = 0; i < submenus.length; i++)
                        submenus[i].setAttribute("aria-hidden", true);
                    for (let i = 0; i < submenusLinks.length; i++) {
                        submenusLinks[i].setAttribute("aria-expanded", false);
                        submenusLinks[i].classList.remove("is-expanded");
                    }
                    let submenuLink = element[0].querySelector(`span[aria-controls="${dollar}{submenuId}"]`);
                    submenuLink.setAttribute("aria-expanded", true);
                    submenuLink.classList.add("is-expanded");
                    submenu.setAttribute("aria-hidden", false);
                };

                scope.menuItemClick = function (item, subItem) {
                    scope.hideAllMenus();
                    scope.menuHandler(item, subItem);
                };
            },
            templateUrl: "/services/v4/web/${projectName}/gen/ui/core/modules/templates/headerMenu.html",
        };
    }])
    .directive("headerSubmenu", function () {
        return {
            restrict: "E",
            replace: true,
            scope: {
                parentItem: "<",
                submenuIndex: "<",
                menuHandler: "&",
                hideMenuFn: "&",
                isToplevel: "<",
                idPrefix: "<",
            },
            link: function (scope, element, attr) {
                let openedMenuId = "";
                scope.hideMenuFn = scope.hideMenuFn();
                scope.menuHandler = scope.menuHandler();

                scope.isScrollable = function (menuItems) {
                    for (let i = 0; i < menuItems.length; i++)
                        if (menuItems[i].items) return "";
                    return "fd-scrollbar dg-menu__sublist--overflow";
                }

                scope.menuHovered = function () {
                    if (openedMenuId !== "" && openedMenuId !== attr["id"]) {
                        let oldSubmenu = element[0].querySelector(`#${dollar}{openedMenuId}`);
                        let oldSubmenuLink = element[0].querySelector(`span[aria-controls="${dollar}{openedMenuId}"]`);
                        oldSubmenuLink.setAttribute("aria-expanded", false);
                        oldSubmenuLink.classList.remove("is-expanded");
                        oldSubmenu.setAttribute("aria-hidden", true);
                        openedMenuId = "";
                    }
                };

                scope.hideAllSubmenus = function () {
                    let submenus = element[0].querySelectorAll('.fd-menu__sublist[aria-hidden="false"]');
                    let submenusLinks = element[0].querySelectorAll('.is-expanded');
                    for (let i = 0; i < submenus.length; i++)
                        submenus[i].setAttribute("aria-hidden", true);
                    for (let i = 0; i < submenusLinks.length; i++) {
                        submenusLinks[i].setAttribute("aria-expanded", false);
                        submenusLinks[i].classList.remove("is-expanded");
                    }
                };

                scope.showSubmenu = function (submenuId) {
                    scope.hideAllSubmenus();
                    openedMenuId = submenuId;
                    let submenu = element[0].querySelector(`#${dollar}{submenuId}`);
                    let submenus = submenu.querySelectorAll('.fd-menu__sublist');
                    let submenusLinks = submenu.querySelectorAll('.is-expanded');
                    for (let i = 0; i < submenus.length; i++)
                        submenus[i].setAttribute("aria-hidden", true);
                    for (let i = 0; i < submenusLinks.length; i++) {
                        submenusLinks[i].setAttribute("aria-expanded", false);
                        submenusLinks[i].classList.remove("is-expanded");
                    }
                    let submenuLink = element[0].querySelector(`span[aria-controls="${dollar}{submenuId}"]`);
                    submenuLink.setAttribute("aria-expanded", true);
                    submenuLink.classList.add("is-expanded");
                    submenu.setAttribute("aria-hidden", false);
                };

                scope.menuItemClick = function (item, subItem) {
                    scope.hideMenuFn();
                    if (scope.isToplevel) scope.menuHandler(subItem); // Temp fix for legacy menu api
                    else scope.menuHandler(item, subItem);
                };
            },
            templateUrl: "/services/v4/web/${projectName}/gen/ui/core/modules/templates/headerSubmenu.html",
        };
    })
    .directive('ideContainer', ['perspective', function (perspective) {
        return {
            restrict: 'E',
            transclude: true,
            replace: true,
            link: {
                pre: function (scope) {
                    scope.shouldLoad = true;
                    if (!perspective.id || !perspective.name) {
                        console.error('<ide-container> requires perspective service data');
                        scope.shouldLoad = false;
                    }
                },
            },
            template: `<div class="dg-main-container">
                <ide-sidebar></ide-sidebar>
                <ng-transclude ng-if="shouldLoad" class="dg-perspective-container"></ng-transclude>
            </div>`
        }
    }])
    .directive('ideSidebar', ['Perspectives', 'perspective', 'messageHub', function (Perspectives, perspective, messageHub) {
        return {
            restrict: 'E',
            replace: true,
            link: {
                pre: function (scope) {
                    scope.activeId = perspective.id;
                    scope.perspectives = Perspectives.query();
                    scope.getIcon = function (icon) {
                        if (icon) return icon;
                        return "/services/v4/web/resources/images/unknown.svg";
                    }
                },
                post: function () {
                    messageHub.onDidReceiveMessage(
                        'my-application-core.openPerspective',
                        // 'ide-core.openPerspective',
                        function (data) {
                            let url = data.link;
                            if (data.params) {
                                let urlParams = '';
                                for (const property in data.params) {
                                    urlParams += `${dollar}{property}=${dollar}{encodeURIComponent(data.params[property])}&`;
                                }
                                url += `?${dollar}{urlParams.slice(0, -1)}`;
                            }
                            window.location.href = url;
                        },
                        true
                    );
                },
            },
            templateUrl: '/services/v4/web/${projectName}/gen/ui/core/modules/templates/ideSidebar.html'
        }
    }])
    /**
     * Used for Dialogs and Window Dialogs
     */
    .directive('ideDialogs', ['messageHub', 'DialogWindows', 'perspective', function (messageHub, DialogWindows, perspective) {
        return {
            restrict: 'E',
            replace: true,
            link: function (scope, element) {
                let dialogWindows = DialogWindows.query();
                let messageBox = element[0].querySelector("#dgIdeAlert");
                let ideDialog = element[0].querySelector("#dgIdeDialog");
                let ideBusyDialog = element[0].querySelector("#dgIdeBusyDialog");
                let ideFormDialog = element[0].querySelector("#dgIdeFormDialog");
                let ideSelectDialog = element[0].querySelector("#dgIdeSelectDialog");
                let ideDialogWindow = element[0].querySelector("#dgIdeDialogWindow");
                let alerts = [];
                let windows = [];
                let dialogs = [];
                let busyDialogs = [];
                let loadingDialogs = [];
                let formDialogs = [];
                let selectDialogs = [];
                scope.searchInput = { value: "" }; // AngularJS - "If you use ng-model, you have to use an object property, not just a variable"
                scope.activeDialog = null;
                scope.excludeFromRequired = {};
                scope.alert = {
                    title: "",
                    message: "",
                    type: "information", // information, error, success, warning
                };
                scope.dialog = {
                    id: null,
                    header: "",
                    subheader: "",
                    title: "",
                    body: "",
                    footer: "",
                    buttons: [],
                    callbackTopic: null,
                    loader: false,
                };
                scope.busyDialog = {
                    id: null,
                    text: '',
                    callbackTopic: '',
                };
                scope.formDialog = {
                    id: null,
                    header: "",
                    subheader: "",
                    title: "",
                    footer: "",
                    buttons: [],
                    loadingMessage: "",
                    loader: false,
                    callbackTopic: null,
                    items: [],
                };
                scope.selectDialog = {
                    title: "",
                    listItems: [],
                    selectedItems: 0,
                    selectedItemId: "",
                    callbackTopic: "",
                    isSingleChoice: true,
                    hasSearch: false
                };
                scope.window = {
                    title: "",
                    dialogWindowId: "",
                    callbackTopic: null,
                    link: "",
                    parameters: "",
                    closable: true,
                };

                scope.showAlert = function () {
                    if (element[0].classList.contains("dg-hidden"))
                        element[0].classList.remove("dg-hidden");
                    scope.alert = alerts[0];
                    messageBox.classList.add("fd-message-box--active");
                    scope.activeDialog = 'alert';
                };

                scope.hideAlert = function () {
                    messageBox.classList.remove("fd-message-box--active");
                    alerts.shift();
                    checkForDialogs();
                };

                scope.showDialog = function () {
                    if (element[0].classList.contains("dg-hidden"))
                        element[0].classList.remove("dg-hidden");
                    scope.dialog = dialogs[0];
                    ideDialog.classList.add("fd-dialog--active");
                    scope.activeDialog = 'dialog';
                };

                scope.hideDialog = function (buttonId) {
                    if (buttonId && scope.dialog.callbackTopic) messageHub.postMessage(scope.dialog.callbackTopic, buttonId, true);
                    ideDialog.classList.remove("fd-dialog--active");
                    dialogs.shift();
                    checkForDialogs();
                };

                scope.showFormDialog = function () {
                    if (element[0].classList.contains("dg-hidden"))
                        element[0].classList.remove("dg-hidden");
                    scope.formDialog = formDialogs[0];
                    for (let key in scope.excludeFromRequired) {
                        delete scope.excludeFromRequired[key];
                    }
                    ideFormDialog.classList.add("fd-dialog--active");
                    scope.activeDialog = 'form';
                };

                scope.formDialogAction = function (buttonId) {
                    scope.formDialog.loader = true;
                    messageHub.postMessage(scope.formDialog.callbackTopic, { buttonId: buttonId, formData: scope.formDialog.items }, true);
                };

                scope.hideFormDialog = function (id) {
                    if (id === scope.formDialog.id) {
                        ideFormDialog.classList.remove("fd-dialog--active");
                        formDialogs.shift();
                        checkForDialogs();
                    } else {
                        for (let i = 0; i < formDialogs.length; i++) {
                            if (formDialogs[i].id === id) {
                                formDialogs.splice(i, 1);
                                break;
                            }
                        }
                    }
                };

                scope.showLoadingDialog = function () {
                    if (element[0].classList.contains("dg-hidden"))
                        element[0].classList.remove("dg-hidden");
                    scope.dialog = loadingDialogs[0];
                    ideDialog.classList.add("fd-dialog--active");
                    scope.activeDialog = 'dialog';
                };

                scope.hideLoadingDialog = function (id) {
                    if (id === scope.dialog.id) {
                        ideDialog.classList.remove("fd-dialog--active");
                        loadingDialogs.shift();
                        checkForDialogs();
                    } else {
                        for (let i = 0; i < loadingDialogs.length; i++) {
                            if (loadingDialogs[i].id === id) {
                                loadingDialogs.splice(i, 1);
                                break;
                            }
                        }
                    }
                };

                scope.showBusyDialog = function () {
                    if (element[0].classList.contains("dg-hidden"))
                        element[0].classList.remove("dg-hidden");
                    scope.busyDialog = busyDialogs[0];
                    ideBusyDialog.classList.add("fd-dialog--active");
                    scope.activeDialog = 'busy';
                };

                scope.hideBusyDialog = function (id, fromUser = false) {
                    if (id === scope.busyDialog.id) {
                        if (fromUser) messageHub.triggerEvent(scope.busyDialog.callbackTopic, true);
                        ideBusyDialog.classList.remove("fd-dialog--active");
                        busyDialogs.shift();
                        checkForDialogs();
                    } else {
                        for (let i = 0; i < busyDialogs.length; i++) {
                            if (busyDialogs[i].id === id) {
                                busyDialogs.splice(i, 1);
                                break;
                            }
                        }
                    }
                };

                scope.itemSelected = function (item) {
                    if (scope.selectDialog.isSingleChoice) {
                        scope.selectDialog.selectedItemId = item;
                        scope.selectDialog.selectedItems = 1;
                    } else {
                        if (item) scope.selectDialog.selectedItems += 1;
                        else scope.selectDialog.selectedItems -= 1;
                    }
                };

                scope.searchChanged = function () {
                    let value = scope.searchInput.value.toLowerCase();
                    if (value === "") scope.clearSearch();
                    else for (let i = 0; i < scope.selectDialog.listItems.length; i++) {
                        if (!scope.selectDialog.listItems[i].text.toLowerCase().includes(value))
                            scope.selectDialog.listItems[i].hidden = true;
                    }
                };

                scope.clearSearch = function () {
                    scope.searchInput.value = "";
                    for (let i = 0; i < scope.selectDialog.listItems.length; i++) {
                        scope.selectDialog.listItems[i].hidden = false;
                    }
                };

                scope.showSelectDialog = function () {
                    if (element[0].classList.contains("dg-hidden"))
                        element[0].classList.remove("dg-hidden");
                    scope.selectDialog = selectDialogs[0];
                    ideSelectDialog.classList.add("fd-dialog--active");
                    scope.activeDialog = 'select';
                };

                scope.hideSelectDialog = function (id, action) {
                    if (id === scope.selectDialog.id) {
                        if (action === "select") {
                            if (scope.selectDialog.selectedItems > 0 || scope.selectDialog.selectedItemId !== "")
                                if (scope.selectDialog.isSingleChoice)
                                    messageHub.postMessage(
                                        scope.selectDialog.callbackTopic,
                                        {
                                            selected: scope.selectDialog.selectedItemId
                                        },
                                        true
                                    );
                                else messageHub.postMessage(
                                    scope.selectDialog.callbackTopic,
                                    {
                                        selected: getSelectedItems()
                                    },
                                    true
                                );
                            else return;
                        } else {
                            let selected;
                            if (scope.selectDialog.isSingleChoice) selected = "";
                            else selected = [];
                            messageHub.postMessage(
                                scope.selectDialog.callbackTopic,
                                { selected: selected },
                                true
                            );
                        }
                        ideSelectDialog.classList.remove("fd-dialog--active");
                        element[0].classList.add("dg-hidden");
                        selectDialogs.shift();
                        checkForDialogs();
                    } else {
                        for (let i = 0; i < selectDialogs.length; i++) {
                            if (selectDialogs[i].id === id) {
                                selectDialogs.splice(i, 1);
                                break;
                            }
                        }
                    }
                };

                scope.showWindow = function () {
                    scope.window = windows[0];
                    if (scope.window.link === "") {
                        console.error(
                            "Dialog Window Error: The link property is missing."
                        );
                        windows.shift();
                        checkForDialogs();
                        return;
                    }
                    if (element[0].classList.contains("dg-hidden"))
                        element[0].classList.remove("dg-hidden");
                    ideDialogWindow.classList.add("fd-dialog--active");
                    scope.activeDialog = 'window';
                };

                scope.hideWindow = function () {
                    if (scope.window.callbackTopic) messageHub.triggerEvent(scope.window.callbackTopic, true);
                    ideDialogWindow.classList.remove("fd-dialog--active");
                    windows.shift();
                    scope.window.link = "";
                    scope.window.parameters = "";
                    checkForDialogs();
                };

                scope.shouldHide = function (item) {
                    if (item.visibility) {
                        for (let i = 0; i < scope.formDialog.items.length; i++) {
                            if (scope.formDialog.items[i].id === item.visibility.id && scope.formDialog.items[i].value === item.visibility.value) {
                                if (item.visibility.hidden) {
                                    scope.excludeFromRequired[item.id] = true;
                                    return false;
                                } else {
                                    scope.excludeFromRequired[item.id] = false;
                                    return true;
                                }
                            }
                        }
                        return item.visibility.hidden;
                    }
                    return false;
                };

                function checkForDialogs() {
                    scope.activeDialog = null;
                    if (selectDialogs.length > 0) scope.showSelectDialog();
                    else if (formDialogs.length > 0) scope.showFormDialog();
                    else if (dialogs.length > 0) scope.showDialog();
                    else if (alerts.length > 0) scope.showAlert();
                    else if (loadingDialogs.length > 0) scope.showLoadingDialog();
                    else if (busyDialogs.length > 0) scope.showBusyDialog();
                    else if (windows.length > 0) scope.showWindow();
                    else element[0].classList.add("dg-hidden");
                }

                messageHub.onDidReceiveMessage(
                    "ide.alert",
                    function (data) {
                        scope.$apply(function () {
                            let type;
                            if (data.type) {
                                switch (data.type.toLowerCase()) {
                                    case "success":
                                        type = "success";
                                        break;
                                    case "warning":
                                        type = "warning";
                                        break;
                                    case "info":
                                        type = "information";
                                        break;
                                    case "error":
                                        type = "error";
                                        break;
                                    default:
                                        type = "information";
                                        break;
                                }
                            }
                            alerts.push({
                                title: data.title,
                                message: data.message,
                                type: type,
                            });
                            if (!scope.activeDialog && alerts.length < 2) {
                                scope.showAlert();
                            }
                        });
                    },
                    true
                );

                messageHub.onDidReceiveMessage(
                    "ide.dialog",
                    function (data) {
                        scope.$apply(function () {
                            dialogs.push({
                                header: data.header,
                                subheader: data.subheader,
                                title: data.title,
                                body: data.body,
                                footer: data.footer,
                                loader: data.loader,
                                buttons: data.buttons,
                                callbackTopic: data.callbackTopic
                            });
                            if (!scope.activeDialog && dialogs.length < 2) {
                                scope.showDialog();
                            }
                        });
                    },
                    true
                );

                messageHub.onDidReceiveMessage(
                    "ide.formDialog.show",
                    function (data) {
                        scope.$apply(function () {
                            formDialogs.push({
                                id: data.id,
                                header: data.header,
                                subheader: data.subheader,
                                title: data.title,
                                items: data.items,
                                loadingMessage: data.loadingMessage,
                                loader: false,
                                footer: data.footer,
                                buttons: data.buttons,
                                callbackTopic: data.callbackTopic,
                            });
                            if (!scope.activeDialog && formDialogs.length < 2) {
                                scope.showFormDialog();
                            }
                        });
                    },
                    true
                );

                messageHub.onDidReceiveMessage(
                    "ide.formDialog.update",
                    function (data) {
                        scope.$apply(function () {
                            if (scope.formDialog && data.id === scope.formDialog.id) {
                                scope.formDialog.items = data.items;
                                if (data.subheader)
                                    scope.formDialog.subheader = data.subheader;
                                if (data.footer)
                                    scope.formDialog.footer = data.footer;
                                if (data.loadingMessage)
                                    scope.formDialog.loadingMessage = data.loadingMessage;
                                scope.formDialog.loader = false;
                            } else {
                                for (let i = 0; i < formDialogs.length; i++) {
                                    if (formDialogs[i].id === data.id) {
                                        formDialogs[i].items = data.items;
                                        if (data.subheader)
                                            formDialogs[i].subheader = data.subheader;
                                        if (data.footer)
                                            formDialogs[i].footer = data.footer;
                                        if (data.loadingMessage)
                                            formDialogs[i].loadingMessage = data.loadingMessage;
                                        formDialogs[i].loader = false;
                                        break;
                                    }
                                }
                            }
                        });
                    },
                    true
                );

                messageHub.onDidReceiveMessage(
                    "ide.formDialog.hide",
                    function (data) {
                        scope.$apply(function () {
                            scope.hideFormDialog(data.id);
                        });
                    },
                    true
                );

                messageHub.onDidReceiveMessage(
                    "ide.loadingDialog.show",
                    function (data) {
                        scope.$apply(function () {
                            loadingDialogs.push({
                                id: data.id,
                                title: data.title,
                                header: '',
                                subheader: '',
                                footer: '',
                                status: data.status,
                                loader: true,
                            });
                            if (!scope.activeDialog && loadingDialogs.length < 2) {
                                scope.showLoadingDialog();
                            }
                        });
                    },
                    true
                );

                messageHub.onDidReceiveMessage(
                    "ide.loadingDialog.update",
                    function (data) {
                        scope.$apply(function () {
                            if (scope.dialog && data.id === scope.dialog.id) {
                                scope.dialog.status = data.status;
                            } else {
                                for (let i = 0; i < loadingDialogs.length; i++) {
                                    if (loadingDialogs[i].id === data.id) {
                                        loadingDialogs[i].status = data.status;
                                        break;
                                    }
                                }
                            }
                        });
                    },
                    true
                );

                messageHub.onDidReceiveMessage(
                    "ide.loadingDialog.hide",
                    function (data) {
                        scope.$apply(function () {
                            scope.hideLoadingDialog(data.id);
                        });
                    },
                    true
                );

                messageHub.onDidReceiveMessage(
                    "ide.busyDialog.show",
                    function (data) {
                        scope.$apply(function () {
                            busyDialogs.push({
                                id: data.id,
                                text: data.text,
                                callbackTopic: data.callbackTopic,
                            });
                            if (!scope.activeDialog && busyDialogs.length < 2) {
                                scope.showBusyDialog();
                            }
                        });
                    },
                    true
                );

                messageHub.onDidReceiveMessage(
                    "ide.busyDialog.hide",
                    function (data) {
                        scope.$apply(function () {
                            scope.hideBusyDialog(data.id);
                        });
                    },
                    true
                );

                scope.isRequired = function (id, required = false) {
                    if (scope.excludeFromRequired[id] === true) {
                        return false;
                    }
                    return required;
                }

                scope.isValid = function (isValid, item) {
                    if (isValid) {
                        item.error = false;
                    } else {
                        item.error = true;
                    }
                };

                function getSelectedItems() {
                    let selected = [];
                    for (let i = 0; i < scope.selectDialog.listItems.length; i++) {
                        if (scope.selectDialog.listItems[i].selected)
                            selected.push(scope.selectDialog.listItems[i].ownId);
                    }
                    return selected;
                }

                function getSelectDialogList(listItems) {
                    return listItems.map(
                        function (item, index) {
                            return {
                                "id": `idesdl${dollar}{index}`,
                                "ownId": item.id,
                                "text": item.text,
                                "hidden": false,
                                "selected": false
                            };
                        }
                    );
                }

                messageHub.onDidReceiveMessage(
                    "ide.selectDialog",
                    function (data) {
                        scope.$apply(function () {
                            selectDialogs.push({
                                title: data.title,
                                listItems: getSelectDialogList(data.listItems),
                                selectedItems: 0,
                                callbackTopic: data.callbackTopic,
                                isSingleChoice: data.isSingleChoice,
                                hasSearch: data.hasSearch
                            });
                            if (!scope.activeDialog && selectDialogs.length < 2) {
                                scope.showSelectDialog();
                            }
                        });
                    },
                    true
                );

                messageHub.onDidReceiveMessage(
                    "ide.dialogWindow",
                    function (data) {
                        // debugger
                        scope.$apply(function () {
                            let found = false;
                            for (let i = 0; i < dialogWindows.length; i++) {
                                if (dialogWindows[i].id === data.dialogWindowId) {
                                    if (data.params) {
                                        data.params['container'] = 'dialog';
                                        data.params['perspectiveId'] = perspective.id;
                                    } else {
                                        data.parameters = {
                                            container: 'layout',
                                            perspectiveId: perspective.id,
                                        };
                                    }
                                    found = true;
                                    windows.push({
                                        title: dialogWindows[i].label,
                                        dialogWindowId: dialogWindows[i].id,
                                        callbackTopic: data.callbackTopic,
                                        link: dialogWindows[i].link,
                                        params: JSON.stringify(data.params),
                                        closable: data.closable,
                                    });
                                    break;
                                }
                            }
                            if (found) {
                                if (!scope.activeDialog && windows.length < 2) {
                                    scope.showWindow();
                                }
                            } else console.error(
                                "Dialog Window Error: There is no window dialog with such id."
                            );
                        });
                    },
                    true
                );
                messageHub.onDidReceiveMessage(
                    "ide.dialogWindow.close",
                    function (data) {
                        scope.$apply(function () {
                            if (data.dialogWindowId === scope.window.dialogWindowId) {
                                scope.hideWindow();
                            } else {
                                for (let i = 0; i < windows.length; i++) {
                                    if (windows[i].dialogWindowId === data.dialogWindowId) {
                                        windows.splice(i, 1);
                                        break;
                                    }
                                }
                            }
                        });
                    },
                    true
                );
            },
            templateUrl: '/services/v4/web/${projectName}/gen/ui/core/modules/templates/ideDialogs.html'
        }
    }])
    .directive('ideStatusBar', ['messageHub', function (messageHub) {
        return {
            restrict: 'E',
            replace: true,
            link: function (scope) {
                scope.busy = '';
                scope.message = '';
                scope.caret = '';
                scope.error = '';
                messageHub.onDidReceiveMessage(
                    'ide.status.busy',
                    function (data) {
                        scope.$apply(function () {
                            scope.busy = data.message;
                        });
                    },
                    true
                );
                messageHub.onDidReceiveMessage(
                    'ide.status.message',
                    function (data) {
                        scope.$apply(function () {
                            scope.message = data.message;
                        });
                    },
                    true
                );
                messageHub.onDidReceiveMessage(
                    'ide.status.error',
                    function (data) {
                        scope.$apply(function () {
                            scope.error = data.message;
                        });
                    },
                    true
                );
                messageHub.onDidReceiveMessage(
                    'ide.status.caret',
                    function (data) {
                        scope.$apply(function () {
                            scope.caret = data.text;
                        });
                    },
                    true
                );
                scope.cleanStatusMessages = function () {
                    scope.message = null;
                };
                scope.cleanErrorMessages = function () {
                    scope.error = null;
                };
            },
            templateUrl: '/services/v4/web/${projectName}/gen/ui/core/modules/templates/ideStatusBar.html'
        }
    }]);