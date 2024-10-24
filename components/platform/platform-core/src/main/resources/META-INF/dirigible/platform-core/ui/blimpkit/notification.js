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
blimpkit.directive('bkNotification', (classNames) => ({
    restrict: 'E',
    transclude: true,
    replace: true,
    scope: {
        isSelected: '<?',
        isBanner: '<?',
    },
    link: (scope) => {
        scope.getClasses = () => classNames('fd-notification', {
            'fd-notification--banner': scope.isBanner,
            'is-selected': scope.isSelected,
        });
    },
    template: '<div ng-class="getClasses()" tabindex="0"><div class="fd-notification__body" ng-transclude></div></div>',
})).directive('bkNotificationContent', () => ({
    restrict: 'A',
    link: (_scope, element) => { element.addClass('fd-notification__content') },
})).directive('bkNotificationHeader', () => ({
    restrict: 'A',
    link: (_scope, element) => { element.addClass('fd-notification__header') },
})).directive('bkNotificationIconPositive', () => ({
    restrict: 'A',
    link: (_scope, element) => {
        element.addClass(['sap-icon', 'sap-icon--color-positive', 'sap-icon--sys-enter-2']);
    },
})).directive('bkNotificationIconNegative', () => ({
    restrict: 'A',
    link: (_scope, element) => {
        element.addClass(['sap-icon', 'sap-icon--color-negative', 'sap-icon--error']);
    },
})).directive('bkNotificationIconWarning', () => ({
    restrict: 'A',
    link: (_scope, element) => {
        element.addClass(['sap-icon', 'sap-icon--color-critical', 'sap-icon--warning']);
    },
})).directive('bkNotificationIconInformation', () => ({
    restrict: 'A',
    link: (_scope, element) => {
        element.addClass(['sap-icon', 'sap-icon--color-information', 'sap-icon--information']);
    },
})).directive('bkNotificationTitle', () => ({
    restrict: 'A',
    scope: { isUnread: '<?' },
    link: (scope, element) => {
        element.addClass('fd-notification__title');
        const unreadWatch = scope.$watch('isUnread', (newValue) => {
            if (newValue) element.addClass('fd-notification__title--unread');
            else element.removeClass('fd-notification__title--unread');
        });
        scope.$on('$destroy', () => {
            unreadWatch();
        });
    },
})).directive('bkNotificationParagraph', () => ({
    restrict: 'A',
    link: (_scope, element) => { element.addClass('fd-notification__paragraph') },
})).directive('bkNotificationFooter', () => ({
    restrict: 'A',
    link: (_scope, element) => { element.addClass('fd-notification__footer') },
})).directive('bkNotificationFooterContent', () => ({
    restrict: 'A',
    link: (_scope, element) => { element.addClass('fd-notification__footer-content') },
})).directive('bkNotificationSeparator', () => ({
    restrict: 'A',
    link: (_scope, element) => { element.addClass('fd-notification__separator') },
})).directive('bkNotificationActions', () => ({
    restrict: 'A',
    link: (_scope, element) => { element.addClass('fd-notification__actions') },
}));