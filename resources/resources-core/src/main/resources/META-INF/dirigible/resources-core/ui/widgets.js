/*
 * Copyright (c) 2010-2022 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
angular.module('ideUI', ['ngAria', 'ideMessageHub'])
    .constant('SplitPaneState', {
        EXPANDED: 0,
        COLLAPSED: 1
    }).factory('uuid', function () {
        return {
            generate: function () {
                function _p8(s) {
                    let p = (Math.random().toString(16) + "000000000").substr(2, 8);
                    return s ? "-" + p.substr(0, 4) + "-" + p.substr(4, 4) : p;
                }
                return _p8() + _p8(true) + _p8(true) + _p8();
            }
        };
    }).factory('backdrop', function ($document) {
        let backdrop = $document[0].createElement('div');
        backdrop.classList.add('dg-backdrop');
        $document[0].body.appendChild(backdrop);
        backdrop.addEventListener('contextmenu', function (event) {
            event.stopPropagation();
        });

        let activate = function () {
            $document[0].body.classList.add('dg-backdrop--active');
        };
        let deactivate = function () {
            $document[0].body.classList.remove('dg-backdrop--active');
        };
        return {
            activate: activate,
            deactivate: deactivate,
            element: backdrop,
        };
    }).factory('classNames', function () {
        function classNames(...args) {
            let classes = [];

            for (let i = 0; i < args.length; i++) {
                let arg = args[i];
                if (!arg) continue;

                const argType = typeof arg;

                if (argType === 'string' || argType === 'number') {
                    classes.push(arg);
                } else if (Array.isArray(arg)) {
                    if (arg.length) {
                        let inner = classNames(...arg);
                        if (inner) {
                            classes.push(inner);
                        }
                    }
                } else if (argType === 'object') {
                    if (arg.toString === Object.prototype.toString) {
                        for (const [key, value] of Object.entries(arg)) {
                            if (value)
                                classes.push(key)
                        }
                    } else {
                        classes.push(arg.toString());
                    }
                }
            }

            return classes.join(' ');
        }

        return classNames;

    }).directive('dgInputRules', function ($parse) {
        /**
         * How to use:
         * <input ng-model="inputModel" ng-required dg-input-rules="inputRules"
         * Example object (inputRules):
         * {
         *    excluded: ['this', 'that'],
         *    patterns: ['^[^/]*$'],
         * }
         */
        return {
            require: 'ngModel',
            link: function (scope, element, attr, controller) {
                let parseFn = $parse(attr.dgInputRules);
                scope.inputRules = parseFn(scope);

                function validation(modelValue, viewValue) {
                    if (viewValue) {
                        let isValid = true;
                        if (scope.inputRules.excluded) isValid = !scope.inputRules.excluded.includes(viewValue);
                        if (isValid && scope.inputRules.patterns) {
                            for (let i = 0; i < scope.inputRules.patterns.length; i++) {
                                isValid = RegExp(scope.inputRules.patterns[i]).test(viewValue);
                                if (!isValid) break;
                            }
                        }
                        return isValid;
                    } else if (attr.required) return false;
                    return true;
                }
                controller.$validators.pattern = validation;
            }
        };
    }).directive('split', ['SplitPaneState', function (SplitPaneState) {
        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            scope: {
                direction: '@',
                width: '@',
                height: '@',
                state: '=?'
            },
            controller: ['$scope', '$element', function ($scope, $element) {
                $scope.panes = [];
                $scope.state = $scope.state || [];

                this.addPane = function (pane) {
                    $scope.panes.push(pane);
                    $scope.state.push(SplitPaneState.EXPANDED);

                    $scope.panes.sort((a, b) => {
                        let elementA = a.element[0];
                        let elementB = b.element[0];
                        if (elementA.previousElementSibling === null || elementB.nextElementSibling === null) return -1;
                        if (elementA.nextElementSibling === null || elementB.previousElementSibling === null) return 1;
                        if (elementA.nextElementSibling === elementB || elementB.previousElementSibling === elementA) return -1;
                        if (elementB.nextElementSibling === elementA || elementA.previousElementSibling === elementB) return 1;
                        return 0;
                    });
                };

                this.removePane = function (pane) {
                    let index = $scope.panes.indexOf(pane);
                    if (index !== -1) {
                        $scope.panes.splice(index, 1);
                    }
                };

                function normalizeSizes(sizes, index = -1) {
                    let isOpen = (size, i) => {
                        return Math.floor(size) > 0 && (index === -1 || index !== i);
                    };

                    let totalSize = sizes.reduce((x, y) => x + y, 0);
                    if (totalSize !== 100) {
                        let openCount = sizes.reduce((count, size, i) => isOpen(size, i) ? count + 1 : count, 0);
                        if (openCount > 0) {
                            let d = (100 - totalSize) / openCount;
                            for (let i = 0; i < sizes.length; i++) {
                                if (isOpen(sizes[i], i))
                                    sizes[i] += d;
                            }
                        }
                    }
                }

                function calcAutoSize() {
                    let sizes = $scope.panes.map(pane => pane.size);
                    let fixedSizeTotal = sizes.reduce((sum, size) => size !== 'auto' ? sum + Number(size) : sum, 0);
                    let autoSizeCount = sizes.reduce((count, size) => size === 'auto' ? count + 1 : count, 0);
                    let autoSize = 0;
                    if (fixedSizeTotal < 100 && autoSizeCount > 0) {
                        autoSize = (100 - fixedSizeTotal) / autoSizeCount;
                    }
                    return autoSize;
                }

                function getPaneSizes() {
                    let autoSize = calcAutoSize();
                    return $scope.panes.map(pane => pane.size === 'auto' ? autoSize : Number(pane.size));
                }

                $scope.$watch('direction', function (newDirection, oldDirection) {
                    if (oldDirection)
                        $element.removeClass(oldDirection);

                    $element.addClass(newDirection || 'horizontal');
                });

                $scope.$watchCollection('panes', function () {
                    if ($scope.split) {
                        $scope.split.destroy();
                        $scope.split = null;
                    }

                    if ($scope.panes.length === 0 || $scope.panes.some(a => a.element === undefined)) {
                        return;
                    }

                    if ($scope.panes.length === 1) {
                        $scope.panes[0].element.css('width', '100%');
                        $scope.panes[0].element.css('height', '100%');
                        return;
                    }

                    let sizes = getPaneSizes();// $scope.panes.map(pane => pane.size || 0);

                    normalizeSizes(sizes);

                    let minSizes = $scope.panes.map(pane => pane.minSize);
                    let elements = $scope.panes.map(pane => pane.element[0]);
                    let snapOffsets = $scope.panes.map(pane => pane.snapOffset);

                    $scope.split = Split(elements, {
                        direction: $scope.direction,
                        sizes: sizes,
                        minSize: minSizes,
                        expandToMin: true,
                        gutterSize: 1,
                        gutterAlign: 'start',
                        snapOffset: snapOffsets,
                        onDragEnd: function (newSizes) {
                            for (let i = 0; i < newSizes.length; i++) {
                                $scope.state[i] = Math.floor(newSizes[i]) === 0 ? SplitPaneState.COLLAPSED : SplitPaneState.EXPANDED;
                            }
                            $scope.$apply();
                        },
                    });
                });

                $scope.$watchCollection('state', function (newState, oldState) {
                    if (newState.length === oldState.length) {
                        //Process the collapsing first
                        for (let i = 0; i < newState.length; i++) {
                            if (newState[i] !== oldState[i]) {
                                if (newState[i] === SplitPaneState.COLLAPSED) {
                                    let sizes = $scope.split.getSizes();
                                    let size = Math.floor(sizes[i]);
                                    if (size > 0) {
                                        $scope.panes[i].lastSize = size;
                                        $scope.split.collapse(i);
                                    }
                                }
                            }
                        }
                        // ... and then the expanding/restore if necessary
                        for (let i = 0; i < newState.length; i++) {
                            if (newState[i] !== oldState[i]) {
                                if (newState[i] === SplitPaneState.EXPANDED) {
                                    let sizes = $scope.split.getSizes();
                                    let size = Math.floor(sizes[i]);
                                    if (size === 0) {
                                        let pane = $scope.panes[i];
                                        sizes[i] = pane.lastSize || (pane.size == 'auto' ? calcAutoSize() : Number(pane.size));
                                        normalizeSizes(sizes, i);
                                        $scope.split.setSizes(sizes);
                                    }
                                }
                            }
                        }
                    }
                });
            }],
            template: '<div class="dg-split" ng-transclude></div>'
        };
    }]).directive('splitPane', function () {
        return {
            restrict: 'E',
            require: '^split',
            replace: true,
            transclude: true,
            scope: {
                size: '@',
                minSize: '@',
                snapOffset: '@'
            },
            link: function (scope, element, attrs, bgSplitCtrl) {
                let paneData = scope.paneData = {
                    element: element,
                    size: scope.size,
                    minSize: Number(scope.minSize),
                    snapOffset: Number(scope.snapOffset)
                };

                bgSplitCtrl.addPane(paneData);

                scope.$on('$destroy', function () {
                    bgSplitCtrl.removePane(paneData);
                });
            },
            template: '<div class="dg-split-pane" ng-transclude></div>'
        }
    }).directive('fdScrollbar', [function () {
        return {
            restrict: 'E',
            transclude: true,
            replace: true,
            template: `<div class="fd-scrollbar" ng-transclude><div>`,
        }
    }]).directive('fdScrollbar', [function () {
        return {
            restrict: 'A',
            link: {
                pre: function (scope, element) {
                    element.addClass('fd-scrollbar');
                }
            },
        }
    }]).directive('fdAvatar', [function () {
        /**
         * dgText: String - One or two letters representing a username or something similiar.
         * accentColor: String - The number of the accent color to be applied. Ranges from 1 to 10. Omitting this will result in a transparent avatar.
         * tile: Boolean - Avatar with a tile icon background.
         * dgPlaceholder: Boolean - The avatar will have a placeholder background.
         * dgSize: String - The size of the avatar. Possible options are 'xs', 's', 'm', 'l' and 'xl'. Default is 'xs'.
         * glyph: String - Icon class.
         * dgBorder: Boolean - Show border around avatar.
         * circle: Boolean - Avatar is in a circular shape.
         * zoomIcon: String -  Icon class.
         * zoomLabel: String - ARIA description of the zoom button.
         * image: String - Link to an image.
         */
        return {
            restrict: 'E',
            transclude: false,
            replace: true,
            scope: {
                dgText: '@',
                accentColor: '@',
                tile: '@',
                dgPlaceholder: '@',
                dgSize: '@',
                glyph: '@',
                dgBorder: '@',
                circle: '@',
                zoomIcon: '@',
                zoomLabel: '@',
                image: '@',
            },
            link: function (scope, element, attrs) {
                if (!attrs.hasOwnProperty('ariaLabel'))
                    console.error('fd-avatar error: You should provide a description using the "aria-label" attribute');
                if (scope.zoomIcon && !scope.zoomLabel)
                    console.error('fd-avatar error: You should provide a description of the zoom button using the "zoom-label" attribute');

                if (scope.image) {
                    if (scope.dgText) scope.dgText = '';
                    element[0].style.backgroundImage = `url("${scope.image}")`;
                    element[0].setAttribute("role", "img");
                }

                scope.getClasses = function () {
                    let classList = [];
                    if (scope.tile === 'true') classList.push('fd-avatar--tile');
                    else {
                        if (scope.accentColor) classList.push(`fd-avatar--accent-color-${scope.accentColor}`);
                        else {
                            if (scope.dgPlaceholder) classList.push('fd-avatar--placeholder');
                            else classList.push('fd-avatar--transparent');
                        }
                    }
                    if (scope.dgSize) classList.push(`fd-avatar--${scope.dgSize}`);
                    else classList.push('fd-avatar--xs');
                    if (scope.dgBorder === 'true') classList.push('fd-avatar--border');
                    if (scope.circle === 'true') classList.push('fd-avatar--circle');
                    return classList.join(' ');
                };
                scope.getIconClass = function () {
                    if (scope.glyph) return scope.glyph;
                };
            },
            template: `<span class="fd-avatar" ng-class="getClasses()">
                <i ng-if="!dgText && !image" class="fd-avatar__icon" ng-class="getIconClass()" role="presentation"></i>
                {{ dgText }}
                <i ng-if="zoomIcon" class="fd-avatar__zoom-icon" ng-class="zoomIcon" aria-label="{{ zoomLabel }}"></i>
            </span>`,
        }
    }]).directive('fdBusyIndicator', [function () {
        /**
         * dgSize: String - The size of the avatar. Possible options are 'm' and 'l'.
         * contrast: Boolean - Contrast mode.
         */
        return {
            restrict: 'E',
            transclude: false,
            replace: true,
            scope: {
                dgSize: '@',
                contrast: '@',
            },
            link: {
                pre: function (scope) {
                    scope.getClasses = function () {
                        let classList = [];
                        if (scope.dgSize) classList.push(`fd-busy-indicator--${scope.dgSize}`);
                        if (scope.contrast === 'true') classList.push('contrast');
                        return classList.join(' ');
                    }
                },
            },
            template: `<div class="fd-busy-indicator" ng-class="getClasses()" aria-label="Loading">
                <div class="fd-busy-indicator__circle"></div>
				<div class="fd-busy-indicator__circle"></div>
				<div class="fd-busy-indicator__circle"></div>
            </div>`,
        }
    }]).directive('fdBusyIndicatorExtended', [function () {
        /**
         * dgSize: String - The size of the avatar. Possible options are 'm' and 'l'.
         * contrast: Boolean - Contrast mode.
         */
        return {
            restrict: 'E',
            transclude: true,
            replace: true,
            scope: {
                dgSize: '@',
                contrast: '@',
            },
            template: `<div class="fd-busy-indicator-extended">
                <fd-busy-indicator dg-size="{{dgSize}}" contrast="{{contrast}}"></fd-busy-indicator>
                <div class="fd-busy-indicator-extended__label" ng-transclude></div>
            </div>`,
        }
    }]).directive('fdFieldset', [function () {
        /**
         * dgLabel: String - Title for the legend.
         */
        return {
            restrict: 'E',
            transclude: true,
            replace: true,
            scope: {
                dgLabel: '@',
            },
            template: `<fieldset class="fd-fieldset">
                <legend ng-if="dgLabel" class="fd-fieldset__legend">{{ dgLabel }}</legend>
                <ng-transclude></ng-transclude>
            </fieldset>`,
        }
    }]).directive('fdFormGroup', ['uuid', function (uuid) {
        /**
         * dgInline: Boolean - Form items are displayed horizontally.
         * dgHeader: String - Text for the group header.
         * compact: Boolean - Heaader size.
         */
        return {
            restrict: 'E',
            transclude: true,
            replace: true,
            scope: {
                dgInline: '@',
                dgHeader: '@',
                compact: '@',
            },
            link: {
                pre: function (scope, element) {
                    if (scope.dgHeader) {
                        scope.headerId = `fgh${uuid.generate()}`;
                        element.attr('aria-labelledby', scope.headerId);
                    }
                }
            },
            template: `<div class="fd-form-group" ng-class="{'true': 'fd-form-group--inline'}[dgInline]" role="group">
                <fd-form-group-header ng-if="dgHeader" header-id="{{ headerId }}" compact="{{ compact }}">{{ dgHeader }}</fd-form-group-header>
                <ng-transclude></ng-transclude>
            </div>`,
        }
    }]).directive('fdFormGroupHeader', [function () {
        /**
         * compact: Boolean - Heaader size.
         * headerId: String - Id for the header element. Used mostly because of 'aria-labelledby'.
         */
        return {
            restrict: 'E',
            transclude: true,
            replace: true,
            scope: {
                compact: '@',
                headerId: '@'
            },
            template: `<div class="fd-form-group__header" ng-class="{'true': 'fd-form-group__header--compact'}[compact]"><h1 id="{{ headerId }}" class="fd-form-group__header-text" ng-transclude></h1></div>`,
        }
    }]).directive('fdFormItem', [function () {
        /**
         * horizontal: Boolean - If true, items will be displayed horizontally.
         */
        return {
            restrict: 'E',
            transclude: true,
            replace: true,
            scope: {
                horizontal: '@',
            },
            template: `<div class="fd-form-item" ng-class="{'true':'fd-form-item--horizontal'}[horizontal]" ng-transclude></div>`,
        }
    }]).directive('fdFormLabel', [function () {
        /**
         * dgColon: Boolean - Puts a colon at the end of the label.
         * dgRequired: Boolean - If the checkbox is required.
         */
        return {
            restrict: 'E',
            transclude: true,
            replace: true,
            scope: {
                dgRequired: '@',
                dgColon: '@',
            },
            link: {
                pre: function (scope) {
                    scope.getClasses = function () {
                        let classList = [];
                        if (scope.dgColon === 'true') classList.push('fd-form-label--colon');
                        if (scope.dgRequired === 'true') classList.push('fd-form-label--required');
                        return classList.join(' ');
                    };
                },
            },
            template: `<label class="fd-form-label" ng-class="getClasses()" ng-required="dgRequired === 'true'" ng-transclude></label>`,
        }
    }]).directive('fdFormHeader', [function () {
        return {
            restrict: 'E',
            transclude: true,
            replace: true,
            template: `<div class="fd-form-header"><span class="fd-form-header__text" ng-transclude></span></div>`,
        }
    }]).directive('fdInput', [function () {
        /**
         * compact: Boolean - Input size.
         * dgRequired: Boolean - If the input is required.
         * dgDisabled: Boolean - If the input is disabled.
         * inGroup: Boolean - If the input is inside an fd-input-group element.
         * state: String - You have five options - 'error', 'success', 'warning' and 'information'.
         */
        return {
            restrict: 'E',
            transclude: true,
            replace: true,
            scope: {
                compact: '@',
                dgRequired: '@',
                dgDisabled: '@',
                inGroup: '@',
                state: '@',
            },
            link: {
                pre: function (scope, element, attrs) {
                    if (!attrs.type)
                        console.error('fd-input error: Inputs must have the "type" HTML attribute');
                    else {
                        let forbiddenTypes = ['checkbox', 'radio', 'file', 'image', 'range'];
                        if (forbiddenTypes.includes(attrs.type))
                            console.error('fd-input error: Invalid input type. Possible options are "color", "date", "datetime-local", "email", "hidden", "month", "number", "password", "search", "tel", "text", "time", "url" and "week".');
                    }
                    scope.getClasses = function () {
                        let classList = [];
                        if (scope.compact === 'true') classList.push('fd-input--compact');
                        if (scope.state) classList.push(`is-${scope.state}`);
                        if (scope.dgDisabled === 'true') classList.push('is-disabled');
                        if (scope.inGroup === "true") classList.push('fd-input-group__input');
                        return classList.join(' ');
                    };
                },
            },
            template: `<input class="fd-input" ng-class="getClasses()" ng-disabled="dgDisabled" ng-transclude>`,
        }
    }]).directive('fdInputGroup', [function () {
        /**
         * compact: Boolean - Input size.
         * dgRequired: Boolean - If the input is required.
         * state: String - You have five options - 'error', 'success', 'warning' and 'information'.
         * dgFocus: Boolean - If the input group is in a focused state.
         * dgDisabled: Boolean - If the input group is disabled.
         */
        return {
            restrict: 'E',
            transclude: true,
            replace: true,
            scope: {
                compact: '@',
                dgRequired: '@',
                dgFocus: '@',
                dgDisabled: '@',
                state: '@',
            },
            link: {
                pre: function (scope) {
                    scope.getClasses = function () {
                        let classList = [];
                        if (scope.compact === 'true') classList.push('fd-input--compact');
                        if (scope.state) classList.push(`is-${scope.state}`);
                        if (scope.dgFocus === 'true') classList.push('is-focus');
                        if (scope.dgDisabled === 'true') classList.push('is-disabled');
                        return classList.join(' ');
                    };
                },
            },
            template: `<div class="fd-input-group" ng-class="getClasses()" ng-disabled="dgDisabled" ng-transclude></div>`,
        }
    }]).directive('fdInputGroupAddon', [function () {
        /**
         * compact: Boolean - Addon size.
         * hasButton: Boolean - Addon contains a button.
         * isReadonly: Boolean - If the addon is in readonly mode.
         */
        return {
            restrict: 'E',
            transclude: true,
            replace: true,
            scope: {
                compact: '@',
                hasButton: '@',
                isReadonly: '@'
            },
            link: {
                pre: function (scope) {
                    scope.getClasses = function () {
                        let classList = [];
                        if (scope.compact === 'true') classList.push('fd-input-group__addon--compact');
                        if (scope.isReadonly) classList.push('fd-input-group__addon--readonly');
                        if (scope.hasButton === 'true') classList.push('fd-input-group__addon--button');
                        return classList.join(' ');
                    };
                },
            },
            template: `<span class="fd-input-group__addon" ng-class="getClasses()" ng-transclude></span>`,
        }
    }]).directive('fdFormInputMessageGroup', ['uuid', function (uuid) {
        /**
         * dgInactive: Boolean - If the message popover should not be shown.
         */
        return {
            restrict: 'E',
            transclude: {
                'control': 'fdInput',
                'message': 'fdFormMessage',
            },
            scope: {
                dgInactive: '@',
            },
            replace: true,
            link: {
                pre: function (scope) {
                    scope.popoverId = `fimg${uuid.generate()}`;
                },
                post: function (scope, element) {
                    element.on('focusout', function () {
                        if (scope.dgInactive !== "true")
                            scope.$apply(scope.togglePopover());
                    });
                    scope.$watch('dgInactive', function () {
                        if (scope.dgInactive === "true") {
                            if (!scope.popoverControl) {
                                scope.popoverControl = element[0].querySelector(`[aria-controls="${scope.popoverId}"]`);
                                scope.popoverBody = element[0].querySelector(`#${scope.popoverId}`);
                            }
                            scope.popoverControl.setAttribute('aria-expanded', 'false');
                            scope.popoverBody.setAttribute('aria-hidden', 'true');
                        }
                    });
                    scope.togglePopover = function () {
                        if (scope.dgInactive !== "true") {
                            if (!scope.popoverControl) {
                                scope.popoverControl = element[0].querySelector(`[aria-controls="${scope.popoverId}"]`);
                                scope.popoverBody = element[0].querySelector(`#${scope.popoverId}`);
                            }
                            if (scope.popoverBody.getAttribute('aria-hidden') === 'true') {
                                scope.popoverControl.setAttribute('aria-expanded', 'true');
                                scope.popoverBody.setAttribute('aria-hidden', 'false');
                            } else {
                                scope.popoverControl.setAttribute('aria-expanded', 'false');
                                scope.popoverBody.setAttribute('aria-hidden', 'true');
                            };
                        }
                    };
                },
            },
            template: `<div class="fd-form-input-message-group fd-popover fd-popover--input-message-group">
                <div class="fd-popover__control" aria-controls="{{ popoverId }}" aria-expanded="false" aria-haspopup="true" ng-click="togglePopover()" ng-transclude="control"></div>
                <div class="fd-popover__body fd-popover__body--no-arrow" aria-hidden="true" id="{{ popoverId }}" ng-transclude="message"></div>
            </div>`,
        }
    }]).directive('fdFormMessage', [function () {
        /**
         * dgType: String - The type of message. Possible values are 'error', 'information', 'success' and 'warning'.
         */
        return {
            restrict: 'E',
            transclude: true,
            replace: true,
            scope: {
                dgType: '@',
            },
            link: {
                pre: function (scope) {
                    scope.getClasses = function () {
                        if (scope.dgType) return `fd-form-message--${scope.dgType}`;
                        else return '';
                    };
                },
            },
            template: '<div class="fd-form-message" ng-class="getClasses()" ng-transclude></div>',
        }
    }]).directive('fdCheckbox', [function () {
        /**
         * compact: Boolean - Checkbox size.
         * dgDisabled: Boolean - If the checkbox is disabled.
         * state: String - You have five options - 'error', 'success', 'warning' and 'information'.
         */
        return {
            restrict: 'E',
            transclude: false,
            replace: true,
            scope: {
                compact: '@',
                dgDisabled: '@',
                state: '@',
            },
            link: {
                pre: function (scope) {
                    scope.getClasses = function () {
                        let classList = [];
                        if (scope.compact === 'true') classList.push('fd-checkbox--compact');
                        if (scope.state) classList.push(`is-${scope.state}`);
                        if (scope.dgDisabled === 'true') classList.push('is-disabled');
                        return classList.join(' ');
                    };
                },
            },
            template: `<input type="checkbox" class="fd-checkbox" ng-class="getClasses()" ng-disabled="dgDisabled">`,
        }
    }]).directive('fdCheckboxLabel', [function () {
        /**
         * compact: Boolean - Checkbox label size.
         * dgRequired: Boolean - If the checkbox is required.
         * empty: Boolean - If the label has text
         */
        return {
            restrict: 'E',
            transclude: true,
            replace: true,
            scope: {
                compact: '@',
                dgRequired: '@',
                empty: '@'
            },
            link: {
                pre: function (scope) {
                    scope.getClasses = function () {
                        let classList = [];
                        if (scope.compact === 'true') classList.push('fd-checkbox__label--compact');
                        if (scope.dgRequired === 'true') classList.push('fd-checkbox__label--required');
                        return classList.join(' ');
                    };
                },
            },
            template: `<label class="fd-checkbox__label" ng-class="getClasses()">
                <div ng-if="empty!=='true'" class="fd-checkbox__label-container"><span class="fd-checkbox__text" ng-transclude></span></div>
            </label>`,
        }
    }]).directive('fdRadio', [function () {
        /**
         * compact: Boolean - Radio size.
         * dgDisabled: Boolean - If the radio button is disabled.
         * state: String - You have five options - 'error', 'success', 'warning' and 'information'.
         */
        return {
            restrict: 'E',
            transclude: false,
            replace: true,
            scope: {
                compact: '@',
                dgDisabled: '@',
                state: '@',
            },
            link: {
                pre: function (scope) {
                    scope.getClasses = function () {
                        let classList = [];
                        if (scope.compact === 'true') {
                            classList.push('fd-radio--compact');
                        }
                        if (scope.state) classList.push(`is-${scope.state}`);
                        if (scope.dgDisabled === 'true') classList.push('is-disabled');
                        return classList.join(' ');
                    };
                },
            },
            template: `<input type="radio" class="fd-radio" ng-class="getClasses()" ng-disabled="dgDisabled">`,
        }
    }]).directive('fdRadioLabel', [function () {
        return {
            restrict: 'E',
            transclude: true,
            replace: true,
            template: '<label class="fd-radio__label" ng-transclude></label>',
        }
    }]).directive('fdTextarea', [function () {
        /**
         * compact: Boolean - Textarea size.
         * dgDisabled: Boolean - If the radio button is disabled.
         * state: String - You have five options - 'error', 'success', 'warning' and 'information'.
         */
        return {
            restrict: 'E',
            transclude: true,
            replace: true,
            scope: {
                compact: '@',
                dgDisabled: '@',
                state: '@',
            },
            link: {
                pre: function (scope) {
                    scope.getClasses = function () {
                        let classList = [];
                        if (scope.compact === 'true') classList.push('fd-textarea--compact');
                        if (scope.state) classList.push(`is-${scope.state}`);
                        if (scope.dgDisabled === 'true') classList.push('is-disabled');
                        return classList.join(' ');
                    };
                },
            },
            template: `<textarea class="fd-textarea" ng-class="getClasses()" ng-disabled="dgDisabled" ng-transclude></textarea>`,
        }
    }]).factory('fdButtonTemplates', function () {
        return {
            getTemplate: function (tagName) {
                return `<${tagName} fd-button-internal class="fd-button" ng-class="getClasses()" ng-disabled="{'disabled':true}[state]"
                    aria-selected="{{ state === 'selected' ? true : false }}" ng-attr-aria-expanded="{{ state === 'expanded' ? true : undefined }}">
                    <i ng-if="glyph" ng-class="glyph" role="presentation" aria-hidden="true"></i>
                    <span ng-if="dgLabel" ng-class="getTextClasses()">{{ dgLabel }}</span>
                    <span ng-if="badge" class="fd-button__badge">{{ badge }}</span>
                    <i ng-if="isMenu" class="sap-icon--slim-arrow-down" aria-hidden="true"></i>
                    <p ng-if="dgAriaDesc" class="fd-button__instructions" id="{{ uuid }}">{{ dgAriaDesc }}</p>
                </${tagName}>`
            }
        }
    }).directive('fdButtonInternal', ['uuid', function (uuid) {
        return {
            restrict: 'A',
            link: {
                pre: function (scope) {
                    if (scope.dgAriaDesc)
                        scope.buttonId = `b${uuid.generate()}`;
                },
                post: function (scope, element, attrs) {
                    scope.lastState = '';
                    if (!scope.dgLabel && scope.glyph && !attrs.hasOwnProperty('ariaLabel'))
                        console.error('fd-button error: Icon-only buttons must have the "aria-label" attribute');
                    scope.getClasses = function () {
                        let classList = [];
                        if (scope.lastState === 'disabled-focusable' && scope.state !== 'disabled-focusable')
                            element[0].setAttribute('aria-live', 'polite');
                        if (scope.isMenu === "true") {
                            if (scope.dgLabel && scope.glyph) {
                                scope.glyph = '';
                                console.error('fd-button error: menu buttons cannot have both text and icon');
                            }
                            scope.badge = '';
                            classList.push('fd-button--menu');
                            if (scope.isOverflow === "true") classList.push('fd-toolbar-overflow-button-menu');
                        } else if (scope.isOverflow === "true") {
                            classList.push('fd-toolbar__overflow-button');
                        }
                        if (scope.dgType) classList.push(`fd-button--${scope.dgType}`);
                        if (scope.compact === "true") classList.push('fd-button--compact');
                        if (scope.inGroup === "true") classList.push('fd-input-group__button');
                        if (scope.state === "selected") {
                            element[0].removeAttribute('aria-disabled');
                            classList.push('is-selected');
                        }
                        else if (scope.state === 'expanded') {
                            classList.push('is-expanded');
                        }
                        else if (scope.state === "disabled") {
                            element[0].removeAttribute('aria-disabled');
                            classList.push('is-disabled');
                        }
                        else if (scope.state === "disabled-focusable") {
                            classList.push('is-disabled');
                            element[0].setAttribute('aria-disabled', true);
                            if (!scope.dgAriaDesc || scope.dgAriaDesc === '')
                                console.error('fd-button error: when using "disabled - focusable" state, you must provide a description.');
                            scope.lastState = scope.state;
                        } else element[0].removeAttribute('aria-disabled');
                        if (scope.dgAriaDesc) {
                            if (!scope.buttonId) scope.buttonId = uuid.generate();
                            element[0].setAttribute('aria-describedby', scope.buttonId);
                        }
                        else element[0].removeAttribute('aria-describedby');
                        return classList.join(' ');
                    };
                    scope.getTextClasses = function () {
                        let classList = [];
                        if (scope.isSplit === "true") {
                            if (scope.compact === "true") classList.push("fd-button-split__text--compact");
                            else classList.push("fd-button-split__text");
                        }
                        else classList.push("fd-button__text");
                        return classList.join(' ');
                    };
                }
            }
        }
    }]).directive('fdLinkButton', ['fdButtonTemplates', function (fdButtonTemplates) {
        return {
            restrict: 'AE',
            transclude: false,
            replace: true,
            scope: {
                dgLabel: '@',
                compact: '@',
                badge: '@',
                glyph: '@',
                state: '@',
                dgType: '@',
                dgAriaDesc: '@',
                isMenu: '@',
                isOverflow: '@',
                isSplit: '@',
                inGroup: '@',
            },
            template: fdButtonTemplates.getTemplate('a'),
        }
    }]).directive('fdButton', ['fdButtonTemplates', function (fdButtonTemplates) {
        /**
         * dgLabel: String - Button text.
         * compact: Boolean - Button size.
         * badge: String/Number - Used for showing a badge inside the button.
         * glyph: String - Icon class/classes.
         * state: String - Possible options are 'selected', 'expanded', 'disabled' and 'disabled-focusable' (must be used with dgAriaDesc). If not specified, normal state is assumed.
         * dgType: String - 'emphasized', 'transparent', 'ghost', 'positive', 'negative' and 'attention'. If not specified, normal state is assumed.
         * dgAriaDesc: String - Short description of the button. If the button is disabled, it should contain the reason and what needs to be done to enable it.
         * isMenu: Boolean - Adds an arrow to the button.
         * isOverflow: Boolean - Used when the button is in a toolbar overflow popover.
         * isSplit: Boolean - (Internal use) If the button is part of a split button.
         * inGroup: Boolean - If the button is inside an fd-input-group-addon element.
         */
        return {
            restrict: 'AE',
            transclude: false,
            replace: true,
            scope: {
                dgLabel: '@',
                compact: '@',
                badge: '@',
                glyph: '@',
                state: '@',
                dgType: '@',
                dgAriaDesc: '@',
                isMenu: '@',
                isOverflow: '@',
                isSplit: '@',
                inGroup: '@',
            },
            template: fdButtonTemplates.getTemplate('button'),
        };
    }]).directive('fdSegmentedButton', [function () {
        return {
            restrict: 'E',
            transclude: true,
            replace: true,
            link: function (scope, element, attrs) {
                if (!attrs.hasOwnProperty('ariaLabel'))
                    console.error('fd-segmented-button error: You should provide a description of the group using the "aria-label" attribute');
            },
            template: '<div class="fd-segmented-button" role="group" ng-transclude></div>'
        }
    }]).directive('fdSplitButton', ['uuid', '$window', 'backdrop', function (uuid, $window, backdrop) {
        /**
         * mainAction: String - Main button text
         * mainGlyph: String - Icon class for the button.
         * dgAlign: String - Relative position of the popover. Possible values are "left" and "right". If not provided, right is assumed.
         * compact: Boolean - Button size.
         * glyph: String - Icon class for the dropdown button.
         * state: String - Possible options are 'selected' and 'disabled'. If not specified, normal state is assumed.
         * dgType: String - 'emphasized', 'transparent', 'ghost', 'positive', 'negative' and 'attention'. If not specified, normal state is assumed.
         * callback: Function - The passed function will be called when the main action button is clicked.
         */
        return {
            restrict: 'E',
            transclude: true,
            replace: true,
            scope: {
                mainAction: '@',
                mainGlyph: '@',
                dgAlign: '@',
                compact: '@',
                glyph: '@',
                state: '@',
                dgType: '@',
                callback: '&',
            },
            link: {
                pre: function (scope, element, attrs) {
                    if (scope.callback) scope.callback = scope.callback();
                    scope.popoverId = `sb${uuid.generate()}`;
                    if (!attrs.hasOwnProperty('ariaLabel'))
                        console.error('fd-split-button error: You should provide a description of the split button using the "aria-label" attribute');
                    scope.getSplitClasses = function () {
                        if (scope.dgType) return `fd-button-split--${scope.dgType}`;
                        else return 'fd-has-margin-right-small';
                    };
                },
                post: function (scope, element) {
                    let isHidden = true;
                    scope.pointerHandler = function (e) {
                        if (!element[0].contains(e.target)) {
                            scope.$apply(scope.hidePopover());
                        }
                    };
                    element.on('focusout', function (e) {
                        if (e.relatedTarget && !element[0].contains(e.relatedTarget)) {
                            scope.$apply(scope.hidePopover);
                        }
                    });

                    element.on('pointerup', function (e) {
                        if (e.originalEvent && e.originalEvent.isSubmenuItem) return;
                        else if (scope.popoverControl && e.target === scope.popoverControl) return;
                        else if (element[0].contains(e.target) && !isHidden) scope.hidePopover();
                    });

                    scope.mainActionClicked = function () {
                        scope.callback();
                    };

                    scope.hidePopover = function () {
                        if (scope.popoverBody) {
                            scope.popoverControl.setAttribute('aria-expanded', 'false');
                            scope.popoverBody.setAttribute('aria-hidden', 'true');
                        }
                        isHidden = true;
                        $window.removeEventListener('pointerup', scope.pointerHandler);
                        backdrop.deactivate();
                    };

                    scope.togglePopover = function () {
                        if (!scope.popoverBody) {
                            scope.popoverControl = element[0].querySelector(`[aria-controls="${scope.popoverId}"]`);
                            scope.popoverBody = element[0].querySelector(`#${scope.popoverId}`);
                        }
                        if (isHidden) {
                            scope.popoverControl.setAttribute('aria-expanded', 'true');
                            scope.popoverBody.setAttribute('aria-hidden', 'false');
                            isHidden = false;
                            $window.addEventListener('pointerup', scope.pointerHandler);
                            backdrop.activate();
                        } else {
                            scope.hidePopover();
                        };
                    };
                },
            },
            template: `<div class="fd-button-split" ng-class="getSplitClasses()" role="group">
                <fd-button glyph="{{ mainGlyph }}" dg-label="{{ mainAction }}" state="{{ state }}" dg-type="{{ dgType }}" is-split="true" compact="{{ compact || 'false' }}" ng-click="mainActionClicked()">
                </fd-button>
                <fd-button glyph="{{ glyph || 'sap-icon--slim-arrow-down' }}" state="{{ state }}" dg-type="{{ dgType }}" compact="{{ compact || 'false' }}" aria-label="arrow down" aria-controls="{{ popoverId }}"
                    aria-haspopup="true" aria-expanded="{{ popupExpanded }}" ng-click="togglePopover()">
                </fd-button>
                <fd-popover-body no-arrow="true" dg-align="{{ dgAlign || 'right' }}">
                  <ng-transclude></ng-transclude>
                </fd-popover-body>
            </div>`,
        }
    }]).directive('fdPopover', ['uuid', '$window', 'backdrop', function (uuid, $window, backdrop) {
        /**
         * dgAlign: String - Relative position of the popover. Possible values are "left" and "right". If not provided, left is assumed.
         * closeInnerclick: Boolean - If the popover should close when there is a click event inside it(*1). Default is true.
         * 
         * Comments:
         * *1 - If the body contains an element that doesn't focus, you will have to add `tabindex="0"` to the first child in the body.
         */
        return {
            restrict: 'E',
            transclude: {
                'control': 'fdPopoverControl',
                'body': 'fdPopoverBody',
            },
            replace: true,
            scope: {
                dgAlign: '@',
                closeInnerclick: '<'
            },
            link: {
                pre: function (scope) {
                    scope.popoverId = `p${uuid.generate()}`;
                    if (!angular.isDefined(scope.closeInnerclick))
                        scope.closeInnerclick = true;
                },
                post: function (scope, element) {
                    let isHidden = true;
                    scope.pointerHandler = function (e) {
                        if (!element[0].contains(e.target)) {
                            scope.$apply(scope.hidePopover());
                        }
                    };
                    if (scope.closeInnerclick) {
                        element.on('focusout', function (e) {
                            if (e.relatedTarget && !element[0].contains(e.relatedTarget)) {
                                scope.$apply(scope.hidePopover);
                            }
                        });
                        element.on('pointerup', function (e) {
                            if (e.originalEvent && e.originalEvent.isSubmenuItem) return;
                            else if (scope.popoverControl && e.target === scope.popoverControl) return;
                            else if (element[0].contains(e.target) && !isHidden) scope.hidePopover();
                        });
                    }

                    scope.hidePopover = function () {
                        if (scope.popoverBody) {
                            scope.popoverControl.setAttribute('aria-expanded', 'false');
                            scope.popoverBody.setAttribute('aria-hidden', 'true');
                        }
                        isHidden = true;
                        $window.removeEventListener('pointerup', scope.pointerHandler);
                        backdrop.deactivate();
                    };

                    scope.togglePopover = function () {
                        if (!scope.popoverBody) {
                            scope.popoverControl = element[0].querySelector(`[aria-controls="${scope.popoverId}"]`);
                            scope.popoverBody = element[0].querySelector(`#${scope.popoverId}`);
                        }
                        if (isHidden) {
                            scope.popoverControl.setAttribute('aria-expanded', 'true');
                            scope.popoverBody.setAttribute('aria-hidden', 'false');
                            isHidden = false;
                            $window.addEventListener('pointerup', scope.pointerHandler);
                            backdrop.activate();
                        } else {
                            scope.hidePopover();
                        };
                    };
                }
            },
            template: `<div class="fd-popover" ng-class="{'right': 'fd-popover--right'}[dgAlign]">
                <ng-transclude ng-transclude-slot="control"></ng-transclude>
                <ng-transclude ng-transclude-slot="body"></ng-transclude>
            </div>`,
        }
    }]).directive('fdPopoverControl', [function () {
        return {
            restrict: 'E',
            transclude: true,
            replace: true,
            link: function (scope, element) {
                if (scope.$parent && scope.$parent.popoverId) {
                    scope.control = element[0].firstElementChild;
                    scope.control.setAttribute('aria-controls', scope.$parent.popoverId);
                    scope.control.setAttribute('aria-expanded', 'false');
                    scope.control.setAttribute('aria-haspopup', 'true');
                    scope.control.addEventListener("click", function () {
                        scope.$parent.togglePopover();
                    });
                }
            },
            template: '<div class="fd-popover__control" ng-transclude></div>',
        }
    }]).directive('fdPopoverBody', ['$window', function ($window) {
        /**
         * dgAlign: String - Relative position of the popover. Possible values are "left" and "right". If not provided, left is assumed.
         * maxHeight: Number - Maximum popover height in pixels before it starts scrolling. Default is the height of the window.
         * noArrow: Boolean - If the popup should have an arrow.
         * dropdownFill: Boolean - The dropdown body will be adjusted to match the text length.
         * canScroll: Boolean - Enable/disable scroll popover support. Default is true.
         */
        return {
            restrict: 'E',
            transclude: true,
            replace: true,
            widgetId: "",
            scope: {
                maxHeight: '@?',
                dgAlign: '@?',
                noArrow: '@?',
                dropdownFill: '@?',
                canScroll: '<?',
            },
            controller: function PopoverBodyController() {
                this.widgetId = "popoverBody";
            },
            link: {
                pre: function (scope, element) {
                    scope.setDefault = function () {
                        if (!angular.isDefined(scope.canScroll))
                            scope.canScroll = true;
                        let rect = element[0].getBoundingClientRect();
                        scope.defaultHeight = $window.innerHeight - rect.top;
                    };
                    scope.setDefault();
                    $window.addEventListener('resize', function () {
                        scope.$apply(function () { scope.setDefault() });
                    });
                    if (scope.$parent && scope.$parent.popoverId)
                        scope.popoverId = scope.$parent.popoverId;
                    else if (scope.$parent && scope.$parent.$parent && scope.$parent.$parent.popoverId)
                        scope.popoverId = scope.$parent.$parent.popoverId;
                    scope.getClasses = function () {
                        let classList = [];
                        if (scope.dgAlign === 'right') classList.push('fd-popover__body--right');
                        if (scope.noArrow === 'true') classList.push('fd-popover__body--no-arrow');
                        if (scope.dropdownFill === 'true') classList.push('fd-popover__body--dropdown-fill');
                        return classList.join(' ');
                    };
                },
            },
            template: `<div id="{{ popoverId }}" class="fd-popover__body" ng-class="getClasses()" aria-hidden="true">
                <div ng-if="canScroll" class="fd-popover__wrapper fd-scrollbar" style="max-height:{{ maxHeight || defaultHeight }}px;" ng-transclude></div>
                <ng-transclude ng-if="!canScroll"></ng-transclude>
            </div>`,
        }
    }]).directive('fdMenu', ['$window', 'backdrop', 'classNames', function ($window, backdrop, classNames) {
        /**
         * maxHeight: Number - Maximum height in pixels before it starts scrolling. Default is the height of the window.
         * canScroll: Boolean - Enable/disable scroll menu support. Default is false.
         * show: Boolean - Use this instead of the CSS 'display' property. Otherwise, the menu will not work properly. Default is true.
         * noBackdrop: Boolean - Disables the backdrop. This may break the menu if not used properly. Default is false.
         * noShadow: Boolean - Removes the shadow effect. Default is false.
         * closeOnOuterClick: Boolean - Hide the menu when a user clicks outside it Default is true.
         */
        return {
            restrict: 'E',
            transclude: true,
            replace: true,
            require: '?^^fdPopoverBody',
            scope: {
                maxHeight: '@?',
                canScroll: '<?',
                show: '=?',
                noBackdrop: '<?',
                noShadow: '<?',
                closeOnOuterClick: '<?',
            },
            link: {
                pre: function (scope, element, attrs, parentCtrl) {
                    if (!attrs.hasOwnProperty('ariaLabel'))
                        console.error('fdMenu error: You must set the "aria-label" attribute');
                    if (!angular.isDefined(scope.show))
                        scope.show = true;
                    if (!angular.isDefined(scope.noBackdrop))
                        scope.noBackdrop = false;
                    if (parentCtrl !== null && parentCtrl.widgetId === "popoverBody") {
                        scope.noBackdrop = true;
                        scope.noShadow = true;
                        scope.closeOnOuterClick = false;
                    } else scope.closeOnOuterClick = true;
                    let rect = element[0].getBoundingClientRect();
                    scope.defaultHeight = $window.innerHeight - rect.top;
                },
                post: function (scope, element) {
                    $window.addEventListener('resize', function () {
                        let rect = element[0].getBoundingClientRect();
                        scope.defaultHeight = $window.innerHeight - rect.top;
                    });
                    scope.backdropClickEvent = function () {
                        scope.$apply(function () { scope.show = false; });
                    };
                    scope.backdropRightClickEvent = function (event) {
                        event.stopPropagation();
                        scope.$apply(function () { scope.show = false; });
                    };
                    scope.$watch('show', function () {
                        if (!scope.noBackdrop) {
                            if (scope.show) {
                                backdrop.activate();
                                if (scope.closeOnOuterClick) {
                                    backdrop.element.addEventListener('click', scope.backdropClickEvent);
                                    backdrop.element.addEventListener('contextmenu', scope.backdropRightClickEvent);
                                }
                            } else {
                                backdrop.deactivate();
                                if (scope.closeOnOuterClick) {
                                    backdrop.element.removeEventListener('click', scope.backdropClickEvent);
                                    backdrop.element.removeEventListener('contextmenu', scope.backdropRightClickEvent);
                                }
                            }
                        }
                    });
                    scope.getMenuClasses = function () {
                        if (scope.canScroll) element[0].style.maxHeight = `${scope.maxHeight || scope.defaultHeight}px`;
                        else element[0].style.removeProperty('max-height');
                        return classNames({ 'fd-menu--overflow': scope.canScroll });
                    };
                    scope.getListClasses = () => classNames('fd-menu__list', {
                        'fd-menu__list--no-shadow': scope.noShadow === true
                    });
                },
            },
            template: `<nav class="fd-menu" ng-show="show" ng-class="getMenuClasses()"><ul ng-class="getListClasses()" role="menu" ng-transclude></ul></nav>`
        }
    }]).directive('fdMenuItem', [function () {
        /**
         * title: String - Title/label of the menu item.
         * maxHeight: Number - Maximum height in pixels before it starts scrolling. Default is the height of the window.
         * canScroll: Boolean - Enable/disable scroll menu support. Default is false.
         * iconBefore: String - Icon class. Displays the icon before the title. Use 'none' to display a transparent icon.
         * iconAfter: String - Icon class. Displays the icon at the end of the menu item.
         */
        return {
            restrict: 'E',
            transclude: false,
            replace: true,
            scope: {
                title: '@',
                isActive: '@?',
                isSelected: '@?',
                iconBefore: '@?',
                iconAfter: '@?',
            },
            link: function (scope) {
                scope.getClasses = function () {
                    let classList = [];
                    if (scope.isActive === 'true') classList.push('is-active');
                    if (scope.isSelected === 'true') classList.push('is-selected');
                    return classList.join(' ');
                };
            },
            template: `<li class="fd-menu__item" role="presentation">
                <span class="fd-menu__link" ng-class="getClasses()" role="menuitem">
                    <span ng-if="iconBefore" class="fd-menu__addon-before">
                        <i class="{{ iconBefore }}" ng-if="iconBefore !== 'none'" role="presentation"></i>
                    </span>
                    <span class="fd-menu__title">{{ title }}</span>
                    <span ng-if="iconAfter" class="fd-menu__addon-after">
                        <i class="{{ iconAfter }}" role="presentation"></i>
                    </span>
                </span>
            </li>`
        }
    }]).directive('fdMenuSublist', ['uuid', '$window', function (uuid, $window) {
        /**
         * title: String - Title/label of the menu item.
         * maxHeight: Number - Maximum height in pixels before it starts scrolling. Default is the height of the window.
         * canScroll: Boolean - Enable/disable scroll menu support. Default is false.
         * icon: String - Icon class. Displays the icon before the title.
         */
        return {
            restrict: 'E',
            transclude: true,
            replace: true,
            scope: {
                title: '@',
                icon: '@?',
                maxHeight: '@?',
                canScroll: '<?',
            },
            link: {
                pre: function (scope) {
                    scope.sublistId = `sl${uuid.generate()}`;
                    scope.isExpanded = false;
                    scope.defaultHeight = $window.innerHeight;
                },
                post: function (scope, element) {
                    let toHide = 0;
                    $window.addEventListener('resize', function () {
                        scope.$apply(function () {
                            scope.defaultHeight = $window.innerHeight;
                            scope.setPosition();
                        });
                    });
                    scope.pointerHandler = function (e) {
                        if (!element[0].contains(e.target)) {
                            scope.$apply(scope.hideSubmenu());
                        }
                    };
                    element.on('pointerup', function (e) {
                        let listItem;
                        let list;
                        if (e.target.tagName !== "LI") {
                            listItem = e.target.closest('li');
                        } else {
                            listItem = e.target;
                        }
                        for (let i = 0; i < listItem.children.length; i++) {
                            if (listItem.children[i].tagName === 'UL') {
                                list = listItem.children[i];
                            }
                        }
                        if (list && list.id === scope.sublistId) {
                            e.originalEvent.isSubmenuItem = true;
                            if (e.originalEvent.pointerType !== 'mouse')
                                scope.$apply(scope.show());
                        }
                    });
                    scope.getClasses = function () {
                        let classList = [];
                        if (scope.isExpanded === 'true') classList.push('is-expanded');
                        return classList.join(' ');
                    };
                    scope.setPosition = function () {
                        if (!angular.isDefined(scope.menu)) scope.menu = element[0].querySelector(`#${scope.sublistId}`);
                        requestAnimationFrame(function () {
                            let rect = scope.menu.getBoundingClientRect();
                            let bottom = $window.innerHeight - rect.bottom;
                            let right = $window.innerWidth - rect.right;
                            if (bottom < 0) scope.menu.style.top = `${bottom}px`;
                            if (right < 0) {
                                scope.menu.style.left = `${scope.menu.offsetWidth * -1}px`;
                                scope.menu.classList.add('dg-submenu--left');
                            }
                        });
                    };
                    scope.show = function () {
                        if (toHide) clearTimeout(toHide);
                        if (!scope.isExpanded) {
                            scope.isExpanded = true;
                            scope.setPosition();
                            $window.addEventListener('pointerup', scope.pointerHandler);
                        }
                    };
                    scope.hideSubmenu = function () {
                        scope.isExpanded = false;
                        scope.menu.style.removeProperty('top');
                        scope.menu.style.removeProperty('left');
                        scope.menu.classList.remove('dg-submenu--left');
                        $window.removeEventListener('pointerup', scope.pointerHandler);
                    };
                    scope.hide = function (event) {
                        if (scope.isExpanded) {
                            if (event.relatedTarget) {
                                if (typeof event.relatedTarget.className === 'string' && event.relatedTarget.className.includes('fd-menu__')) {
                                    scope.hideSubmenu();
                                } else if (!element[0].contains(event.relatedTarget)) {
                                    toHide = setTimeout(function () {
                                        scope.$apply(scope.hideSubmenu());
                                    }, 300);
                                }
                            } else if (!element[0] === event.currentTarget) { // Firefox tooltip fix
                                scope.hideSubmenu();
                            }
                        }
                    };
                }
            },
            template: `<li class="fd-menu__item" role="presentation" ng-mouseenter="show()" ng-mouseleave="hide($event)">
                <span class="fd-menu__link has-child" aria-controls="{{sublistId}}" aria-expanded="{{isExpanded}}" aria-haspopup="true" role="menuitem" ng-class="getClasses()">
                    <span ng-if="icon" class="fd-menu__addon-before"><i class="{{icon}}" role="presentation"></i></span>
                    <span class="fd-menu__title">{{title}}</span>
                    <span class="fd-menu__addon-after fd-menu__addon-after--submenu"></span>
                </span>
                <ul ng-if="canScroll" class="fd-menu__sublist fd-menu--overflow fd-scrollbar dg-menu__sublist--overflow" id="{{sublistId}}" aria-hidden="{{!isExpanded}}" role="menu" style="max-height:{{ maxHeight || defaultHeight }}px;" ng-transclude></ul>
                <ul ng-if="!canScroll" class="fd-menu__sublist" id="{{sublistId}}" aria-hidden="{{!isExpanded}}" role="menu" ng-transclude></ul>
            </li>`
        }
    }]).directive('fdMenuSeparator', [function () {
        return {
            restrict: 'E',
            transclude: false,
            replace: true,
            template: '<span class="fd-menu__separator"></span>'
        }
    }]).directive('fdTable', [function () {
        /**
         * innerBorders: String - Table inner borders. One of 'horizontal', 'vertical', 'none' or 'all' (default value)
         * outerBorders: String - Table outer borders. One of 'horizontal', 'vertical', 'none' or 'all (default value)
         * displayMode: String - The size of the table. Could be one of 'compact', 'condensed' or 'standard' (default value)
         */
        return {
            restrict: 'A',
            transclude: true,
            replace: true,
            scope: {
                innerBorders: '@',
                outerBorders: '@',
                displayMode: '@'
            },
            controller: ['$scope', '$element', function ($scope, $element) {

                this.setAriaDescribedBy = function (id) {
                    $element[0].setAttribute('aria-describedby', id);
                };

                $scope.getClasses = function () {
                    let classList = ['fd-table'];
                    if ($scope.innerBorders === 'horizontal' || $scope.innerBorders === 'none') {
                        classList.push('fd-table--no-horizontal-borders');
                    }
                    if ($scope.innerBorders === 'vertical' || $scope.innerBorders === 'none') {
                        classList.push('fd-table--no-vertical-borders');
                    }
                    if ($scope.displayMode === 'compact') {
                        classList.push('fd-table--compact');
                    } else if ($scope.displayMode === 'condensed') {
                        classList.push('fd-table--condensed');
                    }
                    switch ($scope.outerBorders) {
                        case 'vertical':
                            classList.push('dg-table--no-outer-horizontal-borders');
                            break;
                        case 'horizontal':
                            classList.push('dg-table--no-outer-vertical-borders');
                            break;
                        case 'none':
                            classList.push('fd-table--no-outer-border');
                            break;
                    }
                    return classList.join(' ');
                };
            }],
            template: `<table ng-class="getClasses()" ng-transclude></table>`
        }
    }]).directive('fdTableCaption', ['uuid', function (uuid) {
        return {
            restrict: 'A',
            transclude: true,
            replace: true,
            require: '^fdTable',
            link: function (scope, element, attrs, tableCtrl) {
                let id = `fdt-${uuid.generate()}`
                element[0].setAttribute('id', id);
                tableCtrl.setAriaDescribedBy(id);
            },
            template: '<caption class="fd-table__caption" aria-live="polite" ng-transclude></caption>'
        }
    }]).directive('fdTableFixed', [function () {
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            template: `<div class="fd-table--fixed" ng-transclude></div>`
        }
    }]).directive('fdTableHeader', [function () {
        /**
         * sticky: Boolean - Makes header sticky when scrolling the table
         */
        return {
            restrict: 'A',
            transclude: true,
            replace: true,
            scope: {
                sticky: '<'
            },
            link: function (scope) {
                scope.getClasses = function () {
                    let classList = ['fd-table__header'];
                    if (scope.sticky) {
                        classList.push('dg-table__header-sticky')
                    }
                    return classList.join(' ');
                };
            },
            template: `<thead ng-class="getClasses()" ng-transclude></thead>`
        }
    }]).directive('fdTableBody', [function () {
        /**
         * innerBorders: String - Table inner borders. One of 'horizontal', 'vertical', 'none' or 'all' (default value)
         */
        return {
            restrict: 'A',
            transclude: true,
            replace: true,
            scope: {
                innerBorders: '@'
            },
            link: function (scope) {
                scope.getClasses = function () {
                    let classList = ['fd-table__body'];
                    if (scope.innerBorders === 'horizontal' || scope.innerBorders === 'none') {
                        classList.push('fd-table__body--no-horizontal-borders');
                    }
                    if (scope.innerBorders === 'vertical' || scope.innerBorders === 'none') {
                        classList.push('fd-table__body--no-vertical-borders');
                    }
                    return classList.join(' ');
                };
            },
            template: `<tbody ng-class="getClasses()" ng-transclude></tbody>`
        }
    }]).directive('fdTableFooter', [function () {
        /**
         * sticky: Boolean - Makes footer sticky when scrolling the table
         */
        return {
            restrict: 'A',
            transclude: true,
            replace: true,
            scope: {
                sticky: '<'
            },
            link: function (scope) {
                scope.getClasses = function () {
                    let classList = ['fd-table__footer'];
                    if (scope.sticky) {
                        classList.push('dg-table__footer-sticky')
                    }
                    return classList.join(' ');
                };
            },
            template: `<tfoot ng-class="getClasses()" ng-transclude></tfoot>`
        }
    }]).directive('fdTableRow', ['classNames', function (classNames) {
        /**
         * dgSelected: Boolean - Whether or not the table row is selected. Defaults to 'false'
         * activable: Boolean - Displays the row as active when clicked. Defaults to 'false'
         * hoverable: Boolean - Highlights the row on hover. Defaults to 'false'
         */
        return {
            restrict: 'A',
            transclude: true,
            replace: true,
            require: '?^fdTableGroup',
            scope: {
                dgSelected: '<',
                activable: '<',
                hoverable: '<'
            },
            link: function (scope, element, attrs, tableGroupCtrl) {
                scope.getClasses = () => classNames('fd-table__row', {
                    'fd-table__cell--activable': scope.activable,
                    'fd-table__cell--hoverable': scope.hoverable,
                    'dg-hidden': tableGroupCtrl && tableGroupCtrl.shouldHideRow(element[0])
                });

                scope.isRowExpanded = () => tableGroupCtrl && tableGroupCtrl.isRowExpanded(element[0])
                scope.getAriaSelected = () => scope.dgSelected ? 'true' : undefined;
            },
            template: `<tr ng-class="getClasses()" ng-attr-aria-selected="{{ getAriaSelected() }}" ng-transclude></tr>`
        }
    }]).directive('fdTableHeaderCell', [function () {
        /**
         * contentType: String - The type of the inner element. Could be one of 'checkbox', 'statusIndicator' or 'any' (default value)
         * fixed: Boolean|String - Renders the cell as fixed. Could be one of 'true', 'false' or 'last' (if that's the last fixed cell). Defaults to 'false'
         * activable: Boolean - Displays the cell as active when clicked. Defaults to 'false'
         * hoverable: Boolean - Highlights the cell on hover. Defaults to 'false'
         */
        return {
            restrict: 'A',
            transclude: true,
            replace: true,
            scope: {
                contentType: '@',
                fixed: '@',
                activable: '<',
                hoverable: '<',
            },
            link: {
                post: function (scope, element) {
                    scope.getClasses = function () {
                        let classList = ['fd-table__cell'];
                        switch (scope.contentType) {
                            case 'checkbox':
                                classList.push('fd-table__cell--checkbox');
                                break;
                            case 'statusIndicator':
                                classList.push('fd-table__cell--status-indicator');
                                break;

                        }
                        if (scope.fixed) {
                            classList.push('fd-table__cell--fixed');
                            if (scope.fixed === 'last') {
                                classList.push('fd-table__cell--fixed-last');
                            }
                        }
                        if (scope.activable) {
                            classList.push('fd-table__cell--activable');
                        }
                        if (scope.hoverable) {
                            classList.push('fd-table__cell--hoverable');
                        }
                        return classList.join(' ');
                    };

                    if (element.closest('tbody').length > 0) {
                        element[0].setAttribute('scope', 'row');
                    } else if (element.closest('thead').length > 0) {
                        element[0].setAttribute('scope', 'col');
                    }
                }
            },
            template: `<th ng-class="getClasses()" ng-transclude></th>`
        }
    }]).directive('fdTableCell', ['classNames', function (classNames) {
        /**
         * contentType: String - The type of the inner element. Could be one of 'checkbox', 'statusIndicator' or 'any' (default value)
         * fitContent: Boolean - Sets width to fit the cell content
         * activable: Boolean - Displays the cell as active when clicked. Defaults to 'false'
         * hoverable: Boolean - Highlights the cell on hover. Defaults to 'false'
         * navigated: Boolean - Displays the cell as navigated. Defaults to 'false'
         * noData: Boolean - Displays empty row
         * statusIndicator: String - the type of the status indicator. Could be one of 'valid', 'warning', 'error', 'information' or 'default' (default value)
         * nestingLevel: Number - The row nesting level (starting from 1) for tables with row groups 
         */
        return {
            restrict: 'A',
            transclude: true,
            replace: true,
            require: '?^fdTableGroup',
            scope: {
                contentType: '@',
                fitContent: '<',
                activable: '<',
                hoverable: '<',
                navigated: '<',
                noData: '<',
                statusIndicator: '@',
                nestingLevel: '<'
            },
            link: function (scope, element, attrs, tableGroupCtrl) {
                scope.getClasses = () => classNames('fd-table__cell', {
                    'fd-table__cell--no-data': scope.noData,
                    'fd-table__cell--checkbox': scope.contentType === 'checkbox',
                    'fd-table__cell--status-indicator': scope.contentType === 'statusIndicator',
                    [`fd-table__cell--status-indicator--${scope.statusIndicator}`]: scope.statusIndicator,
                    'fd-table__cell--fit-content': scope.fitContent,
                    'fd-table__cell--activable': scope.activable,
                    'fd-table__cell--hoverable': scope.hoverable,
                    'fd-table__cell--navigated': scope.navigated
                });

                if (scope.nestingLevel) {
                    element[0].setAttribute('data-nesting-level', scope.nestingLevel);
                    if (tableGroupCtrl) {
                        let rowEl = element.parent()[0];
                        tableGroupCtrl.addRow(rowEl, scope.nestingLevel);

                        scope.$on('$destroy', function () {
                            tableGroupCtrl.removeRow(rowEl);
                        });
                    }
                }
            },
            template: `<td ng-class="getClasses()" ng-attr-colspan="{{ noData ? '100%' : undefined }}"  ng-transclude></td>`
        }
    }]).directive('fdTableGroup', [function () {
        return {
            restrict: 'A',
            controller: ['$scope', '$element', function ($scope, $element) {
                $element.addClass('fd-table--group');

                const findRowIndex = (rowElement) => rows.findIndex(r => r.element === rowElement);
                let rows = [];

                this.addRow = function (rowElement, nestingLevel) {
                    let index = $(rowElement).index();
                    rows.splice(index, 0, {
                        element: rowElement,
                        nestingLevel
                    });
                };

                this.removeRow = function (rowElement) {
                    let index = findRowIndex(rowElement);
                    if (index >= 0) {
                        rows.splice(index, 1);
                    }
                }

                this.addGroupRow = function (rowElement, nestingLevel, expanded) {
                    let index = $(rowElement).index();
                    rows.splice(index, 0, {
                        groupRow: true,
                        element: rowElement,
                        nestingLevel,
                        expanded
                    });
                };

                this.setGroupRowExpanded = function (rowElement, expanded) {
                    let index = findRowIndex(rowElement);
                    if (index >= 0) {
                        rows[index].expanded = expanded;
                    }
                }

                this.shouldHideRow = function (rowElement) {
                    let index = findRowIndex(rowElement);
                    if (index >= 0) {
                        let currentRow = rows[index];

                        if (currentRow.nestingLevel === 1)
                            return false;

                        for (let i = index - 1; i >= 0; i--) {
                            let row = rows[i];

                            if (row.groupRow && row.nestingLevel < currentRow.nestingLevel && !row.expanded) {
                                return true;
                            }

                            if (row.nestingLevel === 1)
                                break;
                        }
                    }

                    return false;
                }

                this.isRowExpanded = function (rowElement) {
                    let index = findRowIndex(rowElement);
                    if (index >= 0) {
                        let row = rows[index];
                        return row.groupRow && row.expanded;
                    }

                    return false;
                }
            }]
        };
    }]).directive('fdTableGroupCell', ['classNames', function (classNames) {
        /**
         * nestingLevel: Number - The row nesting level (starting from 1) for tables with row groups 
         * expanded: Boolean - Whether the row group is expanded or not
         */
        return {
            restrict: 'A',
            transclude: true,
            replace: true,
            scope: {
                nestingLevel: '<',
                expanded: '<'
            },
            require: '^fdTableGroup',
            link: function (scope, element, attrs, tableGroupCtrl) {
                let rowEl = element.parent()[0];
                tableGroupCtrl.addGroupRow(rowEl, scope.nestingLevel, scope.expanded);

                scope.getClasses = () => classNames('fd-table__expand', {
                    'fd-table__expand--open': scope.expanded
                });

                scope.toggleExpanded = function () {
                    scope.expanded = !scope.expanded;
                };

                scope.$watch('expanded', function () {
                    tableGroupCtrl.setGroupRowExpanded(element.parent()[0], scope.expanded);
                });

                if (scope.nestingLevel) {
                    element[0].setAttribute('data-nesting-level', scope.nestingLevel);
                }

                scope.$on('$destroy', function () {
                    tableGroupCtrl.removeRow(rowEl);
                });
            },
            template: `<td class="fd-table__cell fd-table__cell--group fd-table__cell--expand" colspan="100%" ng-click="toggleExpanded()">
                <span ng-class="getClasses()"></span>
                <span class="fd-table__text--no-wrap" ng-transclude></span>
            </td>`
        }
    }]).directive('fdTableIcon', ['classNames', function (classNames) {
        /**
         * navigation: Boolean - Whether or not this icon is for a list navigation item.
         * glyph: String - Icon class.
         */
        return {
            restrict: 'A',
            replace: true,
            scope: {
                navigation: '<',
                glyph: '@'
            },
            link: function (scope) {
                scope.getClasses = () => classNames('fd-table__icon', scope.glyph, {
                    'fd-table__icon--navigation': scope.navigation
                });
            },
            template: `<i ng-class="getClasses()" role="presentation"></i>`
        }
    }]).directive('fdToolbar', [function () {
        /**
         * dgType: String - The type of the toolbar. One of 'transparent', 'auto', 'info' or 'solid' (default value)
         * dgSize: String - The size of the toolbar. One of 'cozy' or 'compact' (default value)
         * hasTitle: Boolean - Should be used whenever a title is required.
         * noBottomBorder: Boolean - Removes the bottom border of the toolbar
         * active: Boolean - Enables active and hover states
         */
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            scope: {
                dgType: '@',
                dgSize: '@',
                hasTitle: '<',
                noBottomBorder: '<',
                active: '<'
            },
            link: function (scope) {
                scope.getClasses = function () {
                    let classList = ['fd-toolbar'];

                    switch (scope.dgType) {
                        case 'transparent':
                            classList.push('fd-toolbar--transparent');
                            break;
                        case 'auto':
                            classList.push('fd-toolbar--auto');
                            break;
                        case 'info':
                            classList.push('fd-toolbar--info');
                            break;
                        case 'solid':
                            classList.push('fd-toolbar--solid');
                            break;
                    }

                    if (scope.hasTitle) {
                        classList.push('fd-toolbar--title');
                    }

                    if (scope.noBottomBorder) {
                        classList.push('fd-toolbar--clear');
                    }

                    if (scope.active) {
                        classList.push('fd-toolbar--active');
                    }

                    if (scope.dgSize === 'cozy') {
                        classList.push('fd-toolbar--cozy');
                    }

                    return classList.join(' ');
                };
            },
            template: '<div ng-class="getClasses()" ng-transclude></div>'
        }
    }]).directive('fdToolbarSpacer', [function () {
        /**
         * fixedWidth: Number|String - The fixed with of the spacer. Could be any valid css width value or number in pixels.  
         */
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            scope: {
                fixedWidth: '@'
            },
            link: function (scope) {
                scope.getClasses = function () {
                    let classList = ['fd-toolbar__spacer'];

                    if (scope.fixedWidth !== undefined) {
                        classList.push('fd-toolbar__spacer--fixed');
                    }

                    return classList.join(' ');
                };

                scope.getStyles = function () {
                    if (scope.fixedWidth !== undefined) {
                        let width = scope.fixedWidth;
                        return { width: Number.isFinite(width) ? `${width}px` : width };
                    }
                }
            },
            template: '<div ng-class="getClasses()" ng-style="getStyles()" ng-transclude></div>'
        }
    }]).directive('fdToolbarSeparator', [function () {
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            template: '<span class="fd-toolbar__separator" ng-transclude></span>'
        }
    }]).directive('fdToolbarOverflow', [function () {
        return {
            restrict: 'EA',
            transclude: true,
            template: `<fd-popover>
				<fd-popover-control>
					<fd-button compact="true" glyph="sap-icon--overflow" dg-type="transparent" aria-label="Toolbar overflow"></fd-button>
				</fd-popover-control>
				<fd-popover-body dg-align="right">
					<div class="fd-toolbar__overflow" ng-transclude></div>
				</fd-popover-body>
			</fd-popover>`
        }
    }]).directive('fdToolbarOverflowLabel', [function () {
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            template: '<label class="fd-label fd-toolbar__overflow-label" ng-transclude></label>'
        }
    }]).directive('fdList', [function () {
        /**
         * compact: Boolean - Display the list in compact size mode
         * noBorder: Boolean - Removes the list borders
         * listType: String - One of 'selection', 'navigation' or 'navigation-indication'
         * fixedHeight: String|Number|Boolean - If true it expects the height to be specified explicitly (by css class or style). If number it will be treated as pixels otherwise it must be a valid css height value.
         * byline: Boolean - Whether the list is byline or standard
         */
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            scope: {
                compact: '<',
                noBorder: '<',
                listType: '@',
                fixedHeight: '@',
                byline: '<',
                hasMessage: '<',
                dropdownMode: '<'
            },
            link: function (scope) {
                scope.getClasses = function () {
                    let classList = ['fd-list'];

                    if (scope.compact) {
                        classList.push('fd-list--compact');
                    }

                    if (scope.byline) {
                        classList.push('fd-list--byline');
                    }

                    if (scope.noBorder) {
                        classList.push('fd-list--no-border');
                    }

                    if (scope.hasMessage) {
                        classList.push('fd-list--has-message');
                    }

                    if (scope.dropdownMode) {
                        classList.push('fd-list--dropdown');
                    }

                    switch (scope.listType) {
                        case 'navigation-indication':
                            classList.push('fd-list--navigation-indication');
                        case 'navigation':
                            classList.push('fd-list--navigation');
                            break;
                        case 'selection':
                            classList.push('fd-list--selection');
                            break;
                    }

                    if (parseHeight(scope.fixedHeight)) {
                        classList.push('fd-list__infinite-scroll');
                    }

                    return classList.join(' ');
                }

                scope.getStyles = function () {
                    let height = parseHeight(scope.fixedHeight);
                    if (height && typeof height === 'string') {
                        return { height };
                    }
                }

                scope.getRole = function () {
                    return scope.listType === 'selection' ? 'listbox' : 'list';
                }

                const parseHeight = function (height) {
                    if (Number.isFinite(height)) {
                        return `${height}px`;
                    }
                    return height === 'true' ? true :
                        height === 'false' ? false : height;
                };
            },
            template: `<ul ng-class="getClasses()" ng-style="getStyles()" role="{{getRole()}}" ng-transclude>`
        }
    }]).directive('fdListItem', [function () {
        /**
         * interactive: Boolean - Makes the list item look interactive (clickable)
         * inactive: Boolean - Makes the list item look inactive (non-clickable)
         * dgSelected: Boolean - Selects the list item. Should be used with 'selection' lists
         */
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            scope: {
                interactive: '<',
                inactive: '<',
                dgSelected: '<'
            },
            controller: ['$scope', '$element', '$attrs', function ($scope, $element, $attrs) {

                this.addClass = function (className) {
                    $element.addClass(className);
                }

                this.setRole = function (role) {
                    $element.attr('role', role);
                }

                if (!$attrs.role)
                    this.setRole('listitem');

                $scope.getClasses = function () {
                    let classList = ['fd-list__item'];

                    if ($scope.interactive) {
                        classList.push('fd-list__item--interractive');
                    }
                    if ($scope.inactive) {
                        classList.push('fd-list__item--inactive');
                    }
                    if ($scope.dgSelected) {
                        classList.push('is-selected');
                    }

                    return classList.join(' ');
                }

                $scope.$watch('dgSelected', function () {
                    if ($scope.dgSelected) {
                        $element[0].setAttribute('aria-selected', 'true');
                    } else {
                        $element[0].removeAttribute('aria-selected');
                    }
                })
            }],
            template: `<li ng-class="getClasses()" ng-transclude></li>`
        }
    }]).directive('fdListTitle', [function () {
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            template: '<span class="fd-list__title" ng-transclude></span>'
        }
    }]).directive('fdListSecondary', [function () {
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            template: '<span class="fd-list__secondary" ng-transclude></span>'
        }
    }]).directive('fdListMessage', [function () {
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            scope: {
                state: "@"
            },
            link: function (scope) {
                const states = ['success', 'error', 'warning', 'information'];
                if (scope.state && !states.includes(scope.state)) {
                    console.error(`fd-list-message error: 'state' must be one of: ${states.join(', ')}`);
                }

                scope.getClasses = function () {
                    let classList = ['fd-list__message'];

                    if (scope.state && states.includes(scope.state)) {
                        classList.push(`fd-list__message--${scope.state}`);
                    }
                    return classList;
                }
            },
            template: '<span ng-class="getClasses()" ng-transclude></span>'
        }
    }]).directive('fdListButton', [function () {
        return {
            restrict: 'A',
            link: function (scope, element) {
                element.addClass('fd-list__button');
            }
        }
    }]).directive('fdListLink', [function () {
        /**
         * navigationIndicator: Boolean - Displays an arrow to indicate that the item is navigable (Should be used with 'navigation-indication' lists)
         * navigated: Boolean - Displays the list item as navigated
         * dgSelected: Boolean - Selects the list item. Should be used with 'navigation' and 'navigation-indication' lists
         */
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            require: '^fdListItem',
            scope: {
                navigationIndicator: '<',
                navigated: '<',
                dgSelected: '<'
            },
            link: function (scope, element, attrs, listItemCtrl) {
                listItemCtrl.addClass('fd-list__item--link');

                scope.getClasses = function () {
                    let classList = ['fd-list__link'];

                    if (scope.navigationIndicator) {
                        classList.push('fd-list__link--navigation-indicator');
                    }

                    if (scope.navigated) {
                        classList.push('is-navigated');
                    }

                    if (scope.dgSelected) {
                        classList.push('is-selected');
                    }
                    return classList;
                }
            },
            template: '<a tabindex="0" ng-class="getClasses()" ng-transclude></a>'
        }
    }]).directive('fdListIcon', [function () {
        /**
         * glyph: String - Icon class.
         */
        return {
            restrict: 'EA',
            replace: true,
            scope: {
                glyph: '@'
            },
            link: function (scope) {
                if (!scope.glyph) {
                    console.error('fd-list-icon error: You should provide glpyh icon using the "glyph" attribute');
                }

                scope.getClasses = function () {
                    let classList = ['fd-list__icon'];
                    if (scope.glyph) {
                        classList.push(scope.glyph);
                    }
                    return classList.join(' ');
                }
            },
            template: '<i role="presentation" ng-class="getClasses()"></i>'
        }
    }]).directive('fdListActionItem', [function () {
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            template: `<li role="listitem" class="fd-list__item fd-list__item--action">
                <button class="fd-list__title" ng-transclude></button>
            </li>`
        }
    }]).directive('fdListFormItem', [function () {
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            require: '?^fdListItem',
            link: function (scope, element, attrs, listItemCtrl) {
                if (listItemCtrl)
                    listItemCtrl.setRole('option');
            },
            template: '<div class="fd-form-item fd-list__form-item" ng-transclude></div>'
        }
    }]).directive('fdListContent', [function () {
        /**
         * itemTitle: String - list item title
         * itemTitleId: String - list item title id
         * contentWrap: String - Allows the byline text to wrap
         * titleWrap: String - Allows the title text to wrap
         */
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            scope: {
                itemTitle: '@',
                itemTitleId: '@',
                contentWrap: '<',
                titleWrap: '<'
            },
            controller: ['$scope', '$element', function ($scope, $element) {
                this.addClass = function (className) {
                    $element.children().last().addClass(className);
                }

                $scope.getBylineClasses = function () {
                    let classList = ['fd-list__byline'];
                    if ($scope.contentWrap) {
                        classList.push('fd-list__byline--wrap');
                    }
                    return classList.join(' ');
                }

                $scope.getTitleClasses = function () {
                    let classList = ['fd-list__title'];
                    if ($scope.titleWrap) {
                        classList.push('fd-list__title--wrap');
                    }
                    return classList.join(' ');
                }

                if ($scope.itemTitleId) {
                    $element.children().first().attr('id', $scope.itemTitleId);
                }
            }],
            template: `<div class="fd-list__content">
                <div ng-class="getTitleClasses()">{{itemTitle}}</div>
                <div ng-class="getBylineClasses()" ng-transclude></div>
            </div>`
        }
    }]).directive('fdListGroupHeader', [function () {
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            template: `<li role="listitem" class="fd-list__group-header" ng-transclude></li>`
        }
    }]).directive('fdListByline', [function () {
        /**
         * dgAlign: String - One of 'left' or 'right'
         * contentWrap: String - Allows the byline text to wrap. Relevant to left aligned content only
         * semanticStatus: String - One of 'neutral', 'positive', 'negative', 'critical' or 'informative'. Relevant to right aligned content only
         */
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            scope: {
                dgAlign: '@',
                contentWrap: '<',
                semanticStatus: '@'
            },
            require: '^fdListContent',
            link: function (scope, element, attrs, ctrl) {
                ctrl.addClass('fd-list__byline--2-col');

                const semanticStatuses = ['neutral', 'positive', 'negative', 'critical', 'informative'];

                if (scope.semanticStatus && !semanticStatuses.includes(scope.semanticStatus)) {
                    console.error(`fd-list-byline error: semantic-status must be one of: ${semanticStatuses.join(', ')}`);
                }

                if (scope.dgAlign !== 'left' && scope.dgAlign !== 'right') {
                    console.error(`fd-list-byline error: 'dg-align' must be 'left' or 'right' `);
                }

                scope.getClasses = function () {
                    let classList = [];
                    switch (scope.dgAlign) {
                        case 'left':
                            classList.push('fd-list__byline-left');
                            if (scope.contentWrap) {
                                classList.push('fd-list__byline-left--wrap');
                            }
                            break;
                        case 'right':
                            classList.push('fd-list__byline-right');
                            if (semanticStatuses.includes(scope.semanticStatus)) {
                                classList.push(`fd-list__byline-right--${scope.semanticStatus}`);
                            }
                            break;
                    }

                    return classList.join(' ');
                }
            },
            template: `<div ng-class="getClasses()" ng-transclude></div>`
        }
    }]).directive('fdListThumbnail', [function () {
        /**
         * glyph: String - Icon class.
         * imageUrl: String - Path to the thumbnail image
         */
        return {
            restrict: 'EA',
            replace: true,
            scope: {
                glyph: '@',
                imageUrl: '@'
            },
            link: function (scope) {
                if (!scope.glyph && !scope.imageUrl) {
                    console.error('fd-list-thumbnail error: You should provide either glpyh icon or image');
                }

                scope.getClasses = function () {
                    let classList = ['fd-list__thumbnail'];
                    if (scope.imageUrl) {
                        classList.push('fd-image--s');
                    }
                    return classList.join(' ');
                }

                scope.getStyles = function () {
                    if (scope.imageUrl) {
                        return {
                            backgroundImage: `url('${scope.imageUrl}')`,
                            backgroundSize: 'cover'
                        }
                    }
                }
            },
            template: `<span ng-class="getClasses()" ng-style="getStyles()">
                <i ng-if="glyph" role="presentation" ng-class="glyph"></i>
            </span>`
        }
    }]).directive('fdObjectStatus', ['classNames', function (classNames) {
        /**
         * status: String - One of 'negative', 'critical', 'positive' or 'informative'
         * glyph: String - Icon class.
         * text: String - Object status text.
         * clickable: Boolean - For SPAN elements only
         * inverted: Boolean - Inverts the background color
         * indication: Boolean - Applies generic indication color. Must be a number between 1 and 8 inclusive
         * large: Boolean - Increases the size
         */
        return {
            restrict: 'A',
            replace: false,
            scope: {
                status: '@',
                glyph: '@',
                text: '@',
                clickable: '<',
                inverted: '<',
                indication: '<',
                large: '<'
            },
            controller: ['$scope', '$element', function (scope, element) {
                const statuses = ['negative', 'critical', 'positive', 'informative'];

                this.setIsUploadCollection = function () {
                    scope.isUploadCollection = true;
                    element.addClass('fd-upload-collection__status-group-item');
                }

                scope.getIconClasses = () => classNames('fd-object-status__icon', scope.glyph);
                scope.getTextClasses = () => classNames('fd-object-status__text', {
                    'fd-upload-collection__status-group-item-text': scope.isUploadCollection
                })

                element.addClass('fd-object-status');

                scope.$watch('status', function (newStatus, oldStatus) {
                    if (newStatus && !statuses.includes(newStatus)) {
                        console.error(`fd-object-status error: 'status' must be one of: ${statuses.join(', ')}`);
                    }

                    if (oldStatus) {
                        element.removeClass(`fd-object-status--${oldStatus}`);
                    }

                    if (statuses.includes(newStatus)) {
                        element.addClass(`fd-object-status--${newStatus}`);
                    }
                });

                scope.$watch('clickable', function () {
                    const isLink = element[0].tagName === 'A';
                    if (scope.clickable || isLink) {
                        element.addClass(`fd-object-status--link`);
                        if (!isLink)
                            element[0].setAttribute('role', 'button');
                    } else {
                        element.removeClass(`fd-object-status--link`);
                        element[0].removeAttribute('role');
                    }
                });

                scope.$watch('inverted', function () {
                    if (scope.inverted) {
                        element.addClass('fd-object-status--inverted');
                    } else {
                        element.removeClass('fd-object-status--inverted');
                    }
                });

                scope.$watch('large', function () {
                    if (scope.large) {
                        element.addClass('fd-object-status--large');
                    } else {
                        element.removeClass('fd-object-status--large');
                    }
                });

                scope.$watch('indication', function (indication, oldIndication) {
                    if (oldIndication) {
                        element.removeClass(`fd-object-status--indication-${oldIndication}`);
                    }

                    if (indication && (indication < 1 || indication > 8)) {
                        console.error(`fd-object-status error: 'indication' must be a number between 1 and 8 inclusive`);
                        return;
                    }

                    element.addClass(`fd-object-status--indication-${indication}`);
                });
            }],
            template: `<i ng-if="glyph" ng-class="getIconClasses()" role="presentation"></i>
                       <span ng-if="text" ng-class="getTextClasses()">{{text}}</span>`
        }
    }]).directive('fdSelect', ['uuid', function (uuid) {
        /**
         * dgSize: String - The size of the select. One of 'compact' or 'large'. 
         * dgDisabled: Boolean - Disable the select
         * ngModel: Any - The value of the currently selected item
         * state: String - Optional semantic state. Could be one of 'success', 'error', 'warning' or 'information'
         * message: String - Optional text displayed within the dropdown list.
         * dgPlaceholder: String - Short hint displayed when no item is selected yet.
         * dropdownFill: Boolean - Adjusts the popover body that wraps the dropdown to match the text length
         * labelId: String - The id of the label element if present (Necessary for aria-labelledby)
         * dropdownFixed: Boolean - Dropdown css position will be fixed, allowing for use in dialogs.
         * placement: String - Placement of the dropdown. It can be any of 'top', 'bottom', 'right', and then can be suffixed with '-start' or '-end'. Deafult is 'bottom-start'. Incompatible with dropdownFixed.
         */
        return {
            restrict: 'EA',
            replace: true,
            transclude: true,
            require: '?ngModel',
            scope: {
                dgSize: '@',
                dgDisabled: '<',
                selectedValue: '=?',
                state: '@',
                message: '@',
                dgPlaceholder: '@',
                dropdownFill: '<',
                labelId: '@',
                dropdownFixed: '@',
                placement: '@',
            },
            link: function (scope, element, attrs, ngModel) {
                if (ngModel) {
                    scope.$watch('selectedValue', function (value) {
                        ngModel.$setViewValue(value);
                        ngModel.$validate();
                    });

                    ngModel.$render = function () {
                        scope.selectedValue = ngModel.$viewValue;
                    }
                }
            },
            controller: ['$scope', '$element', function ($scope, $element) {
                $scope.items = [];
                $scope.bodyExpanded = false;
                $scope.buttonId = `select-btn-${uuid.generate()}`;
                $scope.textId = `select-text-${uuid.generate()}`;
                $scope.bodyId = `select-body-${uuid.generate()}`;

                const states = ['success', 'error', 'warning', 'information'];
                if ($scope.state && !states.includes($scope.state)) {
                    console.error(`fd-select error: 'state' must be one of: ${states.join(', ')}`);
                }

                $scope.getClasses = function () {
                    let classList = ['fd-select'];

                    if ($scope.dgSize === 'compact') {
                        classList.push('fd-select--compact');
                    }

                    return classList.join(' ');
                }

                $scope.getControlClasses = function () {
                    let classList = ['fd-select__control'];

                    if ($scope.state) classList.push(`is-${$scope.state}`);

                    return classList.join(' ');
                }

                $scope.getPopoverBodyClasses = function () {
                    let classList = ['fd-popover__body', 'fd-popover__body--no-arrow', 'fd-popover__body--dropdown', 'fd-scrollbar', 'dg-menu__sublist--overflow'];

                    if ($scope.dropdownFill) {
                        classList.push('fd-popover__body--dropdown-fill');
                    }
                    if ($scope.placement && $scope.dropdownFixed !== 'true') {
                        switch ($scope.placement) {
                            case 'bottom-end':
                                classList.push('dg-popover--align-bottom-end');
                                break;
                            case 'top':
                            case 'top-start':
                                classList.push('dg-popover--align-top-start');
                                break;
                            case 'top-end':
                                classList.push('dg-popover--align-top-end');
                                break;
                            case 'right':
                            case 'right-start':
                                classList.push('dg-popover--align-right-start');
                                break;
                            case 'right-end':
                                classList.push('dg-popover--align-right-end');
                                break;
                            default:
                                break;
                        }
                    }

                    return classList.join(' ');
                }


                $scope.getListClasses = function () {
                    let classList = ['fd-list', 'fd-list--dropdown'];

                    if ($scope.message) {
                        classList.push('fd-list--has-message');
                    }

                    switch ($scope.dgSize) {
                        case 'compact':
                            classList.push('fd-list--compact');
                            break;
                        case 'large':
                            classList.push('fd-list--large-dropdown');
                            break;
                    }

                    return classList.join(' ');
                }

                $scope.getListMessageClasses = function () {
                    let classList = ['fd-list__message'];

                    if ($scope.state) {
                        classList.push(`fd-list__message--${$scope.state}`);
                    }

                    return classList.join(' ');
                }

                $scope.getFormMessageClasses = function () {
                    let classList = ['fd-form-message', 'fd-form-message--static'];

                    if ($scope.state) {
                        classList.push(`fd-form-message--${$scope.state}`);
                    }

                    return classList.join(' ');
                }

                $scope.onControllClick = function ($event) {
                    $scope.bodyExpanded = !$scope.bodyExpanded;
                    $event.currentTarget.focus();
                }

                $scope.closeDropdown = function () {
                    $scope.bodyExpanded = false;
                }

                $scope.getSelectedItem = function () {
                    if ($scope.selectedValue === undefined || $scope.selectedValue === null)
                        return null;

                    let index = $scope.items.findIndex(x => x.value === $scope.selectedValue);
                    return index >= 0 ? $scope.items[index] : null;
                }

                $scope.getSelectedItemText = function () {
                    const selectedItem = $scope.getSelectedItem();
                    return selectedItem ? selectedItem.text : $scope.dgPlaceholder || '';
                }

                $scope.getSelectedItemId = function () {
                    const selectedItem = $scope.getSelectedItem();
                    return selectedItem ? selectedItem.optionId : '';
                }

                this.addItem = function (item) {
                    $scope.items.push(item);
                }

                this.removeItem = function (item) {
                    let index = $scope.items.findIndex(x => x.optionId === item.optionId);
                    if (index >= 0)
                        $scope.items.splice(index, 1);
                }

                this.getSelectedValue = function () {
                    return $scope.selectedValue;
                }

                this.selectItem = function (item) {
                    $scope.selectedValue = item.value;
                    $scope.closeDropdown();
                }

                $scope.getStyle = function () {
                    if ($scope.dropdownFixed === 'true') {
                        let pos = $element[0].getBoundingClientRect();
                        return {
                            transition: 'none',
                            transform: 'none',
                            position: 'fixed',
                            top: `${pos.bottom}px`,
                            left: `${pos.left}px`,
                        };
                    } else return {};
                };

                $element.on('focusout', function (e) {
                    if (!e.relatedTarget || !$element[0].contains(e.relatedTarget)) {
                        $scope.$apply($scope.closeDropdown);
                    }
                });
            }],
            template: `<div class="fd-popover">
                <div class="fd-popover__control" aria-disabled="{{ !!dgDisabled }}">
                    <div ng-class="getClasses()">
                        <button id="{{ buttonId }}" ng-class="getControlClasses()" ng-click="onControllClick($event)" aria-labelledby="{{ [labelId, textId].join(' ') }}" aria-expanded="{{ bodyExpanded }}" aria-haspopup="listbox" aria-disabled="{{ !!dgDisabled }}">
                            <span id="{{ textId }}" class="fd-select__text-content">{{ getSelectedItemText() }}</span>
                            <span class="fd-button fd-button--transparent fd-select__button">
                                <i class="sap-icon--slim-arrow-down"></i>
                            </span>
                        </button>
                    </div>
                </div>
                <div id="{{ bodyId }}" aria-hidden="{{ !bodyExpanded }}" ng-class="getPopoverBodyClasses()" ng-style="getStyle()">
                    <div ng-if="message" aria-live="assertive" ng-class="getListMessageClasses()" role="alert">{{ message }}</div>
                    <ul ng-class="getListClasses()" aria-activedescendant="{{ getSelectedItemId() }}" aria-labelledby="{{ labelId }}" role="listbox" ng-transclude></ul>
                </div>
                <div ng-if="message" class="fd-popover__body fd-popover__body--no-arrow" aria-hidden="{{ bodyExpanded }}">
                    <span ng-class="getFormMessageClasses()">{{ message }}</span>
                </div>
            </div>`
        }
    }]).directive('fdOption', ['uuid', function (uuid) {
        /**
         * text: String - Primary text of the select option
         * secondaryText: String - Right alligned secondary text
         * value: Any - Option value
         * glyph: String - Option icon class
         * noWrap: String - Prevents primary text wrapping
         */
        return {
            restrict: 'EA',
            replace: true,
            transclude: true,
            scope: {
                text: '@',
                secondaryText: '@',
                value: '<',
                glyph: '@',
                noWrap: '<'
            },
            require: '^fdSelect',
            link: function (scope, element, attrs, selectCtrl) {
                scope.optionId = `select-option-${uuid.generate()}`;

                scope.isSelected = function () {
                    return selectCtrl.getSelectedValue() === scope.value;
                }

                scope.selectItem = function () {
                    selectCtrl.selectItem(scope);
                }

                scope.getClasses = function () {
                    let classList = ['fd-list__item'];

                    if (scope.isSelected()) {
                        classList.push('is-selected');
                    }

                    return classList.join(' ');
                }

                scope.getTitleClasses = function () {
                    let classList = ['fd-list__title'];

                    if (scope.noWrap) {
                        classList.push('fd-list__title--no-wrap');
                    }

                    return classList.join(' ');
                }

                scope.getIconClasses = function () {
                    let classList = ['fd-list__icon'];

                    if (scope.glyph) {
                        classList.push(scope.glyph);
                    }

                    return classList.join(' ');
                }

                selectCtrl.addItem(scope);

                scope.$on('$destroy', function () {
                    selectCtrl.removeItem(scope);
                });
            },
            template: `<li id="{{ optionId }}" ng-class="getClasses()" role="option" aria-selected="{{ isSelected() }}" ng-click="selectItem()">
                <i ng-if="glyph" role="presentation" ng-class="getIconClasses()"></i>
                <span ng-class="getTitleClasses()">{{ text }}</span>
                <span ng-if="secondaryText" class="fd-list__secondary">{{ secondaryText }}</span>
            </li>`
        }
    }]).directive('fdPagination', ['uuid', function (uuid) {
        /**
         * totalItems: Number - The total number of the items
         * itemsPerPage: Number - The number of the items per page
         * currentPage: Number - The number of the current page (starting from 1).
         * compact: Boolean - Pagination buttons size
         * displayTotalItems: Boolean - Whether to display the total number of items
         * itemsPerPageOptions: Array<Number> - The options for items per page dropdown. If not specified the dropdown will not be displayed
         * pageChange: Function - Callback called when the page has changed. Args: (pageNumber : Number)
         * itemsPerPageChange: Function - Callback called when the 'itemsPerPage' dropdown selection has changed: Args: (itemsPerPage : Number)
         * itemsPerPagePlacement: String - Placement of the dropdown for items per page. See 'placement' on fdSelect.
         */
        return {
            restrict: 'EA',
            replace: true,
            scope: {
                totalItems: '<',
                itemsPerPage: '=?',
                currentPage: '=?',
                compact: '<',
                displayTotalItems: '<',
                itemsPerPageOptions: '<',
                pageChange: '&',
                itemsPerPageChange: '&',
                itemsPerPagePlacement: '@',
            },
            link: function (scope) {
                const maxButtonsInShortMode = 9; // must be an odd number (min 5)
                const maxInnerButtonsInShortMode = maxButtonsInShortMode - 4; //excluding left and right arrow buttons and first and last number buttons

                scope.totalItems = scope.totalItems || 0;
                scope.itemsPerPage = scope.itemsPerPage || 20;
                scope.currentPage = scope.currentPage || 1;
                scope.currentPageInput = scope.currentPage;

                scope.itemsPerPageLabelId = `pag-perpage-label-${uuid.generate()}`;
                scope.currentPageLabelId = `pag-page-label-${uuid.generate()}`;
                scope.currentPageOfLabelId = `pag-of-label-${uuid.generate()}`;

                scope.isShortMode = function () {
                    return scope.getPageCount() <= maxButtonsInShortMode;
                }

                scope.isCurrentPageValid = function (pageNumber) {
                    return pageNumber >= 1 && pageNumber <= scope.getPageCount();
                }

                scope.changePage = function () {
                    scope.gotoPage(scope.currentPageInput);
                }

                scope.onCurrentPageInputChange = function () {
                    scope.currentPageInputState = scope.isCurrentPageValid(scope.currentPageInput) ? null : 'error';
                }

                scope.onCurrentPageInputBlur = function () {
                    if (scope.currentPageInput != scope.currentPage) {
                        scope.currentPageInput = scope.currentPage;
                        scope.currentPageInputState = null;
                    }
                }

                scope.gotoPage = function (pageNumber) {
                    if (scope.isCurrentPageValid(pageNumber)) {
                        scope.currentPage = pageNumber;
                        scope.currentPageInput = pageNumber;

                        scope.pageChange && scope.pageChange({ pageNumber });
                    }
                }

                scope.gotoFirstPage = function () {
                    scope.gotoPage(1);
                }

                scope.gotoLastPage = function () {
                    scope.gotoPage(scope.getPageCount());
                }

                scope.gotoPrevPage = function () {
                    scope.gotoPage(scope.currentPage - 1);
                }

                scope.gotoNextPage = function () {
                    scope.gotoPage(scope.currentPage + 1);
                }

                scope.getPageCount = function () {
                    return Math.ceil(scope.totalItems / scope.itemsPerPage);
                }

                scope.isPrevButtonEnabled = function () {
                    return scope.currentPage > 1;
                }

                scope.isNextButtonEnabled = function () {
                    return scope.currentPage < scope.getPageCount();
                }

                scope.hasStartEllipsys = function () {
                    return scope.getPageCount() > maxButtonsInShortMode && scope.currentPage > Math.ceil(maxButtonsInShortMode / 2);
                }

                scope.hasEndEllipsys = function () {
                    return scope.getPageCount() > maxButtonsInShortMode && scope.currentPage <= scope.getPageCount() - Math.ceil(maxButtonsInShortMode / 2);
                }

                scope.showEllipsys = function (index, length) {
                    return (index === 0 && scope.hasStartEllipsys()) || (index === length - 2 && scope.hasEndEllipsys());
                }

                scope.getPageNumbers = function () {
                    let count = scope.getPageCount();
                    const numbers = [1];
                    if (count > 2) {
                        const hasStartEllipsys = scope.hasStartEllipsys();
                        const hasEndEllipsys = scope.hasEndEllipsys();
                        let startNumber, endNumber;

                        if (hasStartEllipsys && hasEndEllipsys) {
                            const offset = Math.ceil(maxInnerButtonsInShortMode / 2) - 1;
                            startNumber = scope.currentPage - offset;
                            endNumber = scope.currentPage + offset;

                        } else if (hasStartEllipsys && !hasEndEllipsys) {
                            endNumber = count - 1;
                            startNumber = endNumber - maxInnerButtonsInShortMode;

                        } else if (!hasStartEllipsys && hasEndEllipsys) {
                            startNumber = 2;
                            endNumber = startNumber + maxInnerButtonsInShortMode;

                        } else {
                            startNumber = 2;
                            endNumber = count - 1
                        }

                        for (let i = startNumber; i <= endNumber; i++) {
                            numbers.push(i);
                        }
                    }
                    if (count > 1) numbers.push(count);

                    return numbers;
                }

                scope.getClasses = function () {
                    let classList = ['fd-pagination'];

                    if (scope.isShortMode()) {
                        classList.push('fd-pagination--short');
                    }

                    return classList.join(' ');
                }

                scope.getNumberButtonClasses = function (pageNumber) {
                    let classList = ['fd-button', 'fd-button--transparent', 'fd-pagination__link'];

                    if (pageNumber === scope.currentPage) {
                        classList.push('is-active');
                    }

                    if (scope.compact) {
                        classList.push('fd-button--compact');
                    }

                    return classList.join(' ');
                }

                scope.getArrowButtonClassess = function () {
                    let classList = ['fd-button', 'fd-button--transparent', 'fd-pagination__button'];

                    if (scope.compact) {
                        classList.push('fd-button--compact');
                    }

                    return classList.join(' ');
                }

                scope.getNumberButtonAriaLabel = function (pageNumber) {
                    return pageNumber === scope.currentPage ? `Current Page, Page ${pageNumber}` : `Goto page ${pageNumber}`;
                }

                scope.getCurrentPageInputAriaLabelledBy = function () {
                    return [scope.currentPageLabelId, scope.currentPageOfLabelId].join(' ');
                }

                scope.getTotal = function () {
                    return `${scope.totalItems} Results`;
                }

                scope.$watch('itemsPerPage', function (newVal, oldVal) {
                    if (newVal !== oldVal) {
                        if (scope.itemsPerPageChange)
                            scope.itemsPerPageChange({ itemsPerPage: scope.itemsPerPage });
                    }

                    const pageCount = scope.getPageCount();
                    if (scope.currentPage > pageCount) {
                        scope.gotoPage(pageCount);
                    }
                });
            },
            template: `<div ng-class="getClasses()">
                <div ng-if="itemsPerPageOptions" class="fd-pagination__per-page">
                    <label class="fd-form-label fd-pagination__per-page-label" id="{{ itemsPerPageLabelId }}">Results per page: </label>
                    <fd-select selected-value="$parent.itemsPerPage" dg-size="{{ compact ? 'compact' : null }}" label-id="{{ itemsPerPageLabelId }}" placement="{{ itemsPerPagePlacement }}">
                        <fd-option ng-repeat="option in itemsPerPageOptions" text="{{ option }}" value="option"></fd-option>
                    </fd-select>
                </div>
                <nav class="fd-pagination__nav" role="navigation">
                    <a href="javascript:void(0)" ng-class="getArrowButtonClassess()" class="fd-pagination__button--mobile" aria-label="First page" aria-disabled="{{ !isPrevButtonEnabled() }}" ng-click="gotoFirstPage()">
                        <i class="sap-icon sap-icon--media-rewind"></i>
                    </a>
                    <a href="javascript:void(0)" ng-class="getArrowButtonClassess()" aria-label="Previous page" aria-disabled="{{ !isPrevButtonEnabled() }}" ng-click="gotoPrevPage()">
                        <i class="sap-icon sap-icon--navigation-left-arrow"></i>
                    </a>

                    <a ng-if="pageNumber !== currentPage || isShortMode()" ng-repeat-start="pageNumber in pageNumbers = getPageNumbers()" href="javascript:void(0)" ng-class="getNumberButtonClasses(pageNumber)" aria-label="{{ getNumberButtonAriaLabel(pageNumber) }}" aria-current="{{ currentPage === pageNumber }}" ng-click="gotoPage(pageNumber)">{{ pageNumber }}</a>
                    <label ng-if="pageNumber === currentPage" id="{{ currentPageLabelId }}" class="fd-form-label fd-pagination__label" aria-label="Page input, Current page, Page {currentPage}">Page:</label>
                    <fd-input ng-if="pageNumber === currentPage" aria-labelledby="{{ getCurrentPageInputAriaLabelledBy() }}" class="fd-pagination__input" type="number" min="1" max="{{ getPageCount() }}" compact="{{ compact }}" ng-required state="{{ currentPageInputState }}" ng-model="$parent.$parent.currentPageInput" ng-keydown="$event.keyCode === 13 && changePage()" ng-blur="onCurrentPageInputBlur()" ng-change="onCurrentPageInputChange()"></fd-input>
                    <label ng-if="pageNumber === currentPage" id="{{ currentPageOfLabelId }}" class="fd-form-label fd-pagination__label">of {{ getPageCount() }}</label>
                    <span ng-if="showEllipsys($index, pageNumbers.length)" ng-repeat-end class="fd-pagination__more" role="presentation"></span>

                    <a href="javascript:void(0)" ng-class="getArrowButtonClassess()" aria-label="Next page" aria-disabled="{{ !isNextButtonEnabled() }}" ng-click="gotoNextPage()">
                        <i class="sap-icon sap-icon--navigation-right-arrow"></i>
                    </a>
                    <a href="javascript:void(0)" ng-class="getArrowButtonClassess()" class="fd-pagination__button--mobile" aria-label="Last page" aria-disabled="{{ !isNextButtonEnabled() }}" ng-click="gotoLastPage()">
                        <i class="sap-icon sap-icon--media-forward"></i>
                    </a>
                </nav>
                <div ng-if="displayTotalItems" class="fd-pagination__total">
                    <span class="fd-form-label fd-pagination__total-label">{{ getTotal() }}</span>
                </div>
            </div>`
        }
    }]).directive('fdBar', ['classNames', function (classNames) {
        /**
         * barDesign: String - Whether the Bar component is used as a header, subheader, header-with-subheader, footer or floating-footer. Types available: 'header','subheader','header-with-subheader','footer','floating-footer'
         * cozy: Boolean - Whether to apply cozy mode to the Bar.
         * inPage: Boolean - Whether the Bar component is used in Page Layout.
         * size: String - The size of the Page in Page responsive design. Available sizes: 's' | 'm_l' | 'xl'
         */
        return {
            restrict: 'EA',
            replace: true,
            transclude: true,
            scope: {
                barDesign: '@',
                cozy: '<',
                inPage: '<',
                size: '@'
            },
            link: function (scope) {
                const barDesigns = ['header', 'subheader', 'header-with-subheader', 'footer', 'floating-footer'];
                const sizes = ['s', 'm_l', 'xl'];

                if (scope.barDesign && !barDesigns.includes(scope.barDesign)) {
                    console.error(`fd-bar error: 'bar-design' must be one of: ${barDesigns.join(', ')}`);
                }

                if (scope.size && !sizes.includes(scope.size)) {
                    console.error(`fd-bar error: 'size' must be one of: ${sizes.join(', ')}`);
                }

                scope.getClasses = () => classNames('fd-bar', {
                    [`fd-bar--${scope.barDesign}`]: barDesigns.includes(scope.barDesign),
                    'fd-bar--cozy': scope.cozy,
                    'fd-bar--page': scope.inPage,
                    [`fd-bar--page-${scope.size}`]: sizes.includes(scope.size)
                });
            },
            template: `<div ng-class="getClasses()" ng-transclude></div>`
        }
    }]).directive('fdBarLeft', [function () {
        return {
            restrict: 'EA',
            replace: true,
            transclude: true,
            template: `<div class="fd-bar__left" ng-transclude></div>`
        }
    }]).directive('fdBarMiddle', [function () {
        return {
            restrict: 'EA',
            replace: true,
            transclude: true,
            template: `<div class="fd-bar__middle" ng-transclude></div>`
        }
    }]).directive('fdBarRight', [function () {
        return {
            restrict: 'EA',
            replace: true,
            transclude: true,
            template: `<div class="fd-bar__right" ng-transclude></div>`
        }
    }]).directive('fdBarElement', [function () {
        /**
         * fullWidth: Boolean - Whether the element should take the whole width of the container.
         * isTitle: Boolean - Whether the element is title.
         */
        return {
            restrict: 'EA',
            replace: true,
            transclude: true,
            scope: {
                fullWidth: '<',
                isTitle: '<'
            },
            link: function (scope) {
                scope.getClasses = function () {
                    let classList = ['fd-bar__element'];

                    if (scope.isTitle) {
                        classList.push('fd-bar__element--title');
                    }

                    if (scope.fullWidth) {
                        classList.push('fd-bar__element--full-width');
                    }

                    return classList.join(' ');
                }
            },
            template: `<div ng-class="getClasses()" ng-transclude></div>`
        }
    }]).directive('fdTitle', [function () {
        /**
         * headerSize: Boolean - If specified overrides the size of the heading element. Must be a number between 1 and 6 inclusive
         * dgWrap: Boolean - Whether or not the title should wrap
         */
        return {
            restrict: 'A',
            scope: {
                headerSize: '<',
                dgWrap: '<'
            },
            link: function (scope, element) {
                element.addClass('fd-title');

                if (scope.headerSize) {
                    if (scope.headerSize >= 1 && scope.headerSize <= 6)
                        element.addClass(`fd-title--h${scope.headerSize}`);
                    else
                        console.error(`fd-title error: 'header-size' must be a number between 1 and 6 inclusive`);
                }

                if (scope.dgWrap) {
                    element.addClass(`fd-title--wrap`);
                }
            }
        }
    }]).directive('fdComboboxInput', ['uuid', 'classNames', '$window', function (uuid, classNames, $window) {
        /**
         * dropdownItems: Array[{ text: String, secondaryText: String, value: Any }] - Items to be filtered in the search input.
         * ngModel: Any|Array[Any] - The value of the selected item. If multiSelect is set to true this must be an array 
         * dgPlaceholder: String - Placeholder of the search input
         * compact: Boolean - Whether the search input should be displayed in compact mode
         * dgDisabled: Boolean - Whether the search input is disabled
         * state: String - Optional semantic state. Could be one of 'success', 'error', 'warning' or 'information'
         * message: String - Optional text displayed within the dropdown list
         * inputId: String - Id attribute for input element inside Combobox component
         * dgAriaLabel: String - Aria-label for Combobox
         * multiSelect: Boolean - When true the combobox allows selecting multiple items,
         * maxBodyHeight: Number - Maximum body height in pixels before it starts scrolling. Default is the height of the window.
         */
        return {
            restrict: 'EA',
            replace: true,
            require: '?ngModel',
            scope: {
                dropdownItems: '<',
                dgPlaceholder: '@',
                compact: '<',
                dgDisabled: '<',
                state: '@',
                message: '@',
                inputId: '@',
                dgAriaLabel: '@',
                multiSelect: '<',
                maxBodyHeight: '@'
            },
            link: function (scope, element, attrs, ngModel) {

                if (ngModel) {
                    const onSelectedValueChanged = function (value) {
                        if (!angular.equals(value, ngModel.$viewValue)) {
                            ngModel.$setViewValue(scope.multiSelect ? [...value] : value);
                        }
                        ngModel.$validate();
                    }

                    if (scope.multiSelect) {
                        scope.$watchCollection('selectedValue', onSelectedValueChanged);
                    } else {
                        scope.$watch('selectedValue', onSelectedValueChanged);
                    }

                    ngModel.$isEmpty = function (value) {
                        return !value || (scope.multiSelect && value.length === 0);
                    }

                    ngModel.$render = function () {
                        let selectedValue = ngModel.$viewValue;

                        if (!scope.multiSelect) {
                            const selectedItem = scope.dropdownItems.find(x => x.value === selectedValue);
                            scope.search.term = selectedItem ? selectedItem.text : '';
                            scope.clearFilter();
                        } else {
                            if (selectedValue === undefined) {
                                selectedValue = [];
                            } else if (!Array.isArray(selectedValue)) {
                                console.error(`fd-combobox-input error: When multi-select is true 'selected-value' must be an array`);
                                selectedValue = [];
                            } else {
                                selectedValue = [...selectedValue];
                            }
                        }

                        scope.selectedValue = selectedValue;
                    }

                    // we have to add an extra watch since ngModel doesn't work well with arrays - it
                    // doesn't trigger rendering if only an item in the array changes.

                    if (scope.multiSelect) {
                        // we have to do it on each watch since ngModel watches reference, but
                        // we need to work of an array, so we need to see if anything was inserted/removed
                        var lastView, lastViewRef = NaN;
                        scope.$watch(function selectMultipleWatch() {
                            if (lastViewRef === ngModel.$viewValue && !angular.equals(lastView, ngModel.$viewValue)) {
                                lastView = [...ngModel.$viewValue];
                                ngModel.$render();
                            }
                            lastViewRef = ngModel.$viewValue;
                        });
                    }
                }

                scope.search = { term: '' };

                scope.dropdownItems = scope.dropdownItems || [];
                scope.filteredDropdownItems = scope.dropdownItems;
                scope.bodyId = `cb-body-${uuid.generate()}`;
                scope.checkboxIds = {};

                scope.onControllClick = function () {
                    scope.bodyExpanded = !scope.bodyExpanded;
                    if (scope.bodyExpanded) {
                        element.find('input').focus();
                    }
                }

                scope.closeDropdown = function () {
                    scope.bodyExpanded = false;
                }

                scope.openDropdown = function () {
                    scope.bodyExpanded = true;
                }

                scope.isBodyExpanded = function () {
                    return scope.bodyExpanded && scope.filteredDropdownItems.length > 0;
                }

                scope.onInputChange = function () {
                    scope.filterValues();

                    if (!scope.multiSelect) {
                        const item = scope.dropdownItems.find(x => x.text.toLowerCase() === scope.search.term.toLowerCase());
                        scope.selectedValue = item ? item.value : null;
                    }
                }

                scope.filterValues = function () {
                    if (scope.search.term) {
                        scope.filteredDropdownItems = scope.dropdownItems.filter(x => x.text.toLowerCase().startsWith(scope.search.term.toLowerCase()));
                    } else {
                        scope.filteredDropdownItems = scope.dropdownItems;
                    }
                }

                scope.clearFilter = function () {
                    scope.filteredDropdownItems = scope.dropdownItems;
                }

                scope.onKeyDown = function (event) {
                    const ARROW_UP = 38;
                    const ARROW_DOWN = 40;
                    if (event.keyCode === ARROW_UP || event.keyCode === ARROW_DOWN) {
                        const inputEl = element.find('input');
                        const elements = [inputEl[0], ...element.find('.fd-popover__body li')];
                        const index = elements.findIndex(x => x === event.target);
                        if (index === -1) return;

                        if (event.keyCode === ARROW_DOWN) {
                            if (index < elements.length - 1) {
                                elements[index + 1].focus();
                                event.preventDefault();
                            }
                        } else if (event.keyCode === ARROW_UP) {
                            if (index > 0) {
                                elements[index - 1].focus();
                                event.preventDefault();
                            }
                        }
                    }
                }

                scope.onSearchKeyDown = function (event) {
                    switch (event.key) {
                        case 'Backspace':
                            if (scope.search.term.length === 0 && scope.selectedValue.length)
                                scope.selectedValue.splice(-1, 1);
                            break;
                        case 'Enter':
                            if (scope.search.term.length && scope.filteredDropdownItems.length) {
                                scope.addItemToSelection(scope.filteredDropdownItems[0]);
                                scope.search.term = '';
                                scope.clearFilter();
                            }
                            break;
                    }
                }

                scope.removeItemFromSelection = function (item) {
                    const itemIndex = scope.selectedValue.indexOf(item.value);
                    if (itemIndex >= 0)
                        scope.selectedValue.splice(itemIndex, 1);
                }

                scope.addItemToSelection = function (item) {
                    const itemIndex = scope.selectedValue.indexOf(item.value);
                    if (itemIndex === -1)
                        scope.selectedValue.push(item.value);
                }

                scope.onItemClick = function (item) {
                    if (scope.multiSelect) {
                        const itemIndex = scope.selectedValue.indexOf(item.value);
                        if (itemIndex >= 0)
                            scope.selectedValue.splice(itemIndex, 1);
                        else
                            scope.selectedValue.push(item.value);
                    } else {
                        scope.selectedValue = item.value;
                        scope.search.term = item.text;
                        scope.clearFilter();
                    }

                    if (!scope.multiSelect)
                        scope.closeDropdown();
                }

                scope.isSelected = function (item) {
                    if (scope.multiSelect) {
                        return scope.selectedValue.includes(item.value);
                    } else {
                        return item.value === scope.selectedValue;
                    }
                }

                scope.getHighlightedText = function (value) {
                    return scope.shouldRendedHighlightedText(value) ? value.substring(0, scope.search.term.length) : null;
                }

                scope.shouldRendedHighlightedText = function (value) {
                    return value.toLowerCase().startsWith(scope.search.term.toLowerCase()) && value.length > scope.search.term.length;
                }

                scope.getLabel = function (value) {
                    return scope.shouldRendedHighlightedText(value) ? value.substring(scope.search.term.length) : value;
                }

                scope.isDisabled = function () {
                    return scope.dgDisabled ? true : undefined;
                }

                scope.getListClasses = function () {
                    return classNames({
                        'fd-list--multi-input': scope.multiSelect
                    });
                }

                scope.getCheckboxId = function (value) {
                    let id = scope.checkboxIds[value];
                    if (!id) scope.checkboxIds[value] = id = `cb-checkbox-${uuid.generate()}`;
                    return id;
                }

                scope.getSelectedItems = function () {
                    return scope.selectedValue.map(value => scope.dropdownItems.find(item => item.value === value));
                }

                scope.onTokenClick = function (item) {
                    if (scope.bodyExpanded) {
                        element.find('input').focus();
                    }
                    scope.removeItemFromSelection(item);
                }

                element.on('focusout', function (e) {
                    if (!scope.bodyExpanded)
                        return;

                    if (!e.relatedTarget || !element[0].contains(e.relatedTarget)) {
                        scope.$apply(scope.closeDropdown);
                    }
                });

                scope.$watchCollection('dropdownItems', function (items) {
                    if (items === undefined || items === null)
                        scope.dropdownItems = [];

                    scope.filteredDropdownItems = items || [];
                });

                scope.setDefault = function () {
                    let rect = element[0].getBoundingClientRect();
                    scope.defaultHeight = $window.innerHeight - rect.bottom;
                };
                scope.setDefault();

                $window.addEventListener('resize', function () {
                    scope.$apply(function () { scope.setDefault() });
                });
            },
            template: `<div class="fd-popover" ng-keydown="onKeyDown($event)">
                <div class="fd-popover__control" ng-attr-disabled="{{ isDisabled() }}" ng-attr-aria-disabled="{{isDisabled() }}" aria-expanded="{{ isBodyExpanded() }}" aria-haspopup="true" aria-controls="{{ bodyId }}">
                    <fd-input-group class="fd-input-group--control" dg-disabled="{{isDisabled() }}" state="{{ state }}">
                        <fd-tokenizer ng-if="multiSelect" compact="compact">
                            <fd-token ng-repeat="item in getSelectedItems()" close-clicked="onTokenClick(item)" compact="compact" dg-text="{{item.text}}" close-button-aria-label="unselect option: {{item.text}}" tabindex="0"></fd-token>
                            <fd-token-indicator></fd-token-indicator>
                            <fd-input ng-attr-id="{{ inputId }}" type="text" class="fd-tokenizer__input" autocomplete="off" placeholder="{{ dgPlaceholder }}" in-group="true" compact="{{ compact }}" ng-focus="openDropdown()" ng-change="onInputChange()" ng-model="search.term" ng-keydown="onSearchKeyDown($event)"></fd-input>
                        </fd-tokenizer>
                        <fd-input ng-if="!multiSelect" ng-attr-id="{{ inputId }}" type="text" autocomplete="off" placeholder="{{ dgPlaceholder }}" in-group="true" compact="{{ compact }}" ng-focus="openDropdown()" ng-change="onInputChange()" ng-model="search.term"></fd-input>
                        <fd-input-group-addon has-button="true" compact="{{ compact }}">
                            <fd-button class="fd-select__button" in-group="true" compact="{{ compact }}" glyph="sap-icon--navigation-down-arrow" dg-type="transparent" state="{{ isBodyExpanded() ? 'expanded' : '' }}" ng-click="onControllClick()"
                                ng-attr-aria-label="{{ dgAriaLabel }}"
                                aria-controls="{{ bodyId }}"
                                aria-haspopup="true"></fd-button>
                        </fd-input-group-addon>
                    </fd-input-group>
                </div>
                <div ng-if="!dgDisabled" id="{{ bodyId }}" class="fd-popover__body fd-popover__body--no-arrow fd-popover__body--dropdown fd-popover__body--dropdown-fill" aria-hidden="{{ !isBodyExpanded() }}" ng-attr-aria-label="{{ dgAriaLabel }}">
                    <div class="fd-popover__wrapper fd-scrollbar" style="max-height:{{ maxBodyHeight || defaultHeight }}px;">
                        <fd-list-message ng-if="message" state="{{ state }}">{{ message }}</fd-list-message>
                        <fd-list class="{{getListClasses()}}" dropdown-mode="true" compact="compact" has-message="!!message">
                            <fd-list-item ng-repeat="item in filteredDropdownItems" role="option" tabindex="0" dg-selected="isSelected(item)" ng-click="onItemClick(item)">
                                <fd-list-form-item ng-if="multiSelect">
                                    <fd-checkbox id="{{getCheckboxId(item.value)}}" compact="compact" ng-checked="isSelected(item)">
                                    </fd-checkbox>
                                    <fd-checkbox-label empty="true" compact="compact" for="{{getCheckboxId(item.value)}}" ng-click="$event.preventDefault()"></fd-checkbox-label>
                                </fd-list-form-item>
                                <fd-list-title>
                                    <span ng-if="shouldRendedHighlightedText(item.text)" class="fd-list__bold">{{ getHighlightedText(item.text) }}</span>{{ getLabel(item.text) }}
                                </fd-list-title>
                                <fd-list-seconday ng-if="item.secondaryText">{{ item.secondaryText }}</fd-list-seconday>
                            </fd-list-item>
                        </fd-list>
                    </div>
                </div>
            </div>`
        }
    }]).directive('fdCard', ['classNames', function (classNames) {
        /**
         * cardType: String - cardType can be 'object' | 'standard' | 'list' | 'table' to indicate what card's type it belongs to
         * compact: Boolean - Whether to apply compact mode
         */
        return {
            restrict: 'EA',
            replace: true,
            transclude: true,
            scope: {
                cardType: '@',
                compact: '<'
            },
            link: function (scope, element) {
                scope.getClasses = () => classNames('fd-card', {
                    'fd-card--object': scope.cardType === 'object',
                    'fd-card--table': scope.cardType === 'table',
                    'fd-card--compact': scope.compact
                });
            },
            template: `<div ng-class="getClasses()" role="region" ng-transclude></div>`
        }
    }]).directive('fdCardHeader', ['classNames', function (classNames) {
        /**
         * interactive: Boolean - Whether card header is interactive. Defaults to true 
         */
        return {
            restrict: 'EA',
            replace: true,
            transclude: {
                'title': 'fdCardTitle',
                'subtitle': '?fdCardSubtitle',
                'status': '?fdCardStatus',
                'avatar': '?fdAvatar'
            },
            scope: {
                interactive: '<'
            },
            link: function (scope, element, attributes, controller, $transclude) {
                if (scope.interactive === undefined)
                    scope.interactive = true;

                const avatarEl = element.find('.fd-avatar');
                avatarEl.addClass('fd-card__avatar');

                scope.isSubtitleFilled = () => { return $transclude.isSlotFilled('fdCardSubtitle') };

                scope.getClasses = () => classNames('fd-card__header', {
                    'fd-card__header--non-interactive': !scope.interactive
                });
            },
            template: `<a ng-class="getClasses()">
                <ng-transclude ng-transclude-slot="avatar"></ng-transclude>
                <div class="fd-card__header-text">
                    <div class="fd-card__title-area">
                        <ng-transclude ng-transclude-slot="title"></ng-transclude>
                        <ng-transclude ng-transclude-slot="status"></ng-transclude>
                    </div>
                    <div ng-if="isSubtitleFilled()" class="fd-card__subtitle-area" ng-transclude="subtitle">
                    </div>
                </div>
            </a>`
        }
    }]).directive('fdCardTitle', [function () {
        return {
            restrict: 'EA',
            replace: false,
            transclude: true,
            template: `<div class="fd-card__title" ng-transclude></div>`
        }
    }]).directive('fdCardSubtitle', [function () {
        return {
            restrict: 'EA',
            replace: true,
            transclude: true,
            template: `<div class="fd-card__subtitle" ng-transclude></div>`
        }
    }]).directive('fdCardStatus', ['classNames', function (classNames) {
        /**
         * status: String - One of 'negative', 'critical', 'positive' or 'informative'
         * dgType: String - Could be 'counter' or 'status' (default value)
         */
        return {
            restrict: 'EA',
            replace: true,
            transclude: true,
            scope: {
                status: '@'
            },
            link: function (scope) {
                const statuses = ['negative', 'critical', 'positive', 'informative'];

                scope.getClasses = () => classNames('fd-object-status', {
                    [`fd-object-status--${scope.status}`]: scope.status && statuses.includes(scope.status),
                    'fd-card__counter': scope.dgType === 'counter'
                });
            },
            template: `<span ng-class="getClasses()" ng-transclude></span>`
        }
    }]).directive('fdCardContent', [function () {
        return {
            restrict: 'EA',
            replace: true,
            transclude: true,
            template: `<div class="fd-card__content" role="group" ng-transclude></div>`
        }
    }]).directive('fdBadge', [function () {
        return {
            restrict: 'EA',
            replace: true,
            transclude: true,
            template: `<div class="fd-badge" ng-transclude></div>`
        }
    }]).directive('fdWizard', [function () {
        /**
         * currentStep: Number - Wizard current step starting from 1
         * completedSteps: Number - The number of the completed steps
         * dgSize: String - When specified adds horizontal paddings to the content. Could be one of: 'sm', 'md', 'lg', 'xl'
         */
        return {
            restrict: 'EA',
            replace: true,
            transclude: true,
            scope: {
                currentStep: '<',
                completedSteps: '<',
                dgSize: '@',
            },
            controller: ['$scope', function ($scope) {
                $scope.steps = [];
                const validSizes = ['sm', 'md', 'lg', 'xl'];
                if ($scope.dgSize && !validSizes.includes($scope.dgSize)) {
                    console.error(`fd-wizard error: 'dg-size' must be one of: ${validSizes.join(', ')}`);
                }

                this.addStep = function (step) {
                    $scope.steps.push(step);
                };

                this.removeStep = function (step) {
                    let index = $scope.steps.indexOf(step);
                    if (index >= 0)
                        $scope.steps.splice(index, 1);
                }

                this.getSteps = function () {
                    return $scope.steps;
                }

                this.onStepClick = function (index) {
                    $scope.steps[index].stepClick({ step: index + 1 });
                }

                this.isStepCompleted = function (step) {
                    let index = $scope.steps.indexOf(step);
                    return index + 1 <= $scope.completedSteps;
                }

                this.isStepUpcoming = function (step) {
                    let index = $scope.steps.indexOf(step);
                    return index + 1 > $scope.currentStep;
                }

                this.isStepUpcomingIncompleted = function (step) {
                    let index = $scope.steps.indexOf(step);
                    return index + 1 > $scope.completedSteps && index + 1 > $scope.currentStep;
                }

                this.isStepCurrent = function (step) {
                    let index = $scope.steps.indexOf(step);
                    return index + 1 === $scope.currentStep;
                }

                this.hasCurrentStep = function () {
                    return $scope.currentStep >= 1 && $scope.currentStep <= $scope.steps.length;
                }

                this.allStepsCompleted = function () {
                    return $scope.completedSteps >= $scope.steps.length;
                }

                this.getSize = function () {
                    return $scope.dgSize;
                }

                this.isValidSize = function (size) {
                    return validSizes.includes(size);
                }

                this.getValidSizes = function () {
                    return validSizes;
                }
            }],
            template: `<section class="fd-wizard" ng-transclude></section>`
        }
    }]).directive('fdWizardNavigation', ['classNames', function (classNames) {
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            require: '^^fdWizard',
            link: function (scope, element, attrs, wizCtrl) {

                scope.onStepClick = function (step) {
                    wizCtrl.onStepClick(step);
                }

                scope.getSteps = function () {
                    return wizCtrl.getSteps();
                }
                scope.getProgressBarClasses = () => classNames('fd-wizard__progress-bar', {
                    [`fd-wizard__progress-bar--${wizCtrl.getSize()}`]: wizCtrl.isValidSize(wizCtrl.getSize())
                });
                scope.getConnectorClasses = (step) => classNames('fd-wizard__connector', {
                    'fd-wizard__connector--active': wizCtrl.isStepCompleted(step)
                });
                scope.getLabelContainerClasses = (step) => classNames('fd-wizard__label-container', {
                    'fd-wizard__label-container--optional': !!step.optionalLabel
                });
                scope.getStepClasses = (step) => classNames('fd-wizard__step', {
                    'fd-wizard__step--upcoming': wizCtrl.isStepUpcoming(step),
                    'fd-wizard__step--current': wizCtrl.isStepCurrent(step),
                    'fd-wizard__step--completed': wizCtrl.isStepCompleted(step),
                    'fd-wizard__step--no-label': step.noLabel
                });
                scope.getAriaDisabled = (step) => wizCtrl.isStepUpcomingIncompleted(step) ? 'true' : undefined;
                scope.getAriaCurrent = (step) => wizCtrl.isStepCurrent(step) ? 'step' : undefined;
                scope.getIndicatorLabel = (step) => !step.indicatorGlyph ? step.indicatorLabel : undefined;
            },
            template: `<nav class="fd-wizard__navigation">
                <ul ng-class="getProgressBarClasses()">
                    <li ng-repeat="step in getSteps()" ng-class="getStepClasses(step)">
                        <div class="fd-wizard__step-wrapper">
                            <a ng-click="onStepClick($index)" class="fd-wizard__step-container" tabindex="0" aria-label="{{ step.dgLabel }}" ng-attr-aria-disabled="{{getAriaDisabled(step)}}" ng-attr-aria-current="{{getAriaCurrent(step) }}">
                                <span class="fd-wizard__step-indicator">{{ getIndicatorLabel(step) }}
                                    <i ng-if="step.indicatorGlyph" class="fd-wizard__icon {{step.indicatorGlyph}}" role="presentation"></i>
                                </span>
                                <div ng-class="getLabelContainerClasses(step)">
                                    <span class="fd-wizard__label">{{ step.dgLabel }}</span>
                                    <span ng-if="step.optionalLabel" class="fd-wizard__optional-text">{{ step.optionalLabel }}</span>
                                </div>
                            </a>
                            <span ng-if="!$last" ng-class="getConnectorClasses(step)"></span>
                        </div>
                    </li>
                </ul>
            </nav>`
        };
    }]).directive('fdWizardContent', ['classNames', function (classNames) {
        /**
         * dgBackground: String - When specified applies specific background. Could be one of: 'solid', 'list', 'transparent'
         * dgSize: String - When specified adds horizontal paddings to the content. Could be one of: 'sm', 'md', 'lg', 'xl'
         */
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            require: '^fdWizard',
            scope: {
                dgBackground: '@',
                dgSize: '@',
            },
            link: function (scope, element, attrs, wizCtrl) {
                const validBackgrounds = ['solid', 'list', 'transparent'];
                if (scope.dgSize && !wizCtrl.isValidSize(scope.dgSize)) {
                    console.error(`fd-wizard-content error: 'dg-size' must be one of: ${wizCtrl.getValidSizes().join(', ')}`);
                }
                if (scope.dgBackground && !validBackgrounds.includes(scope.dgBackground)) {
                    console.error(`fd-wizard-content error: 'dgBackground' must be one of: ${validBackgrounds.join(', ')}`);
                }

                scope.getClasses = () => classNames('fd-wizard__content', {
                    [`fd-wizard__content--${scope.dgSize}`]: wizCtrl.isValidSize(scope.dgSize),
                    [`fd-wizard__content--${scope.dgBackground}`]: validBackgrounds.includes(scope.dgBackground)
                });
            },
            template: `<section ng-class="getClasses()" ng-transclude></section>`
        }
    }]).directive('fdWizardStep', ['classNames', function (classNames) {
        /**
         * dgLabel: String - The step label
         * dgSize: String - When specified adds horizontal paddings to the content. Could be one of: 'sm', 'md', 'lg', 'xl'
         * optionalLabel: String - An optional text displayed below the label
         * indicatorGlyph: String - Indicator icon class/classes. When specified overrides the 'indicatorLabel' attribute
         * indicatorLabel: String - Indicator label
         * noLabel: Boolean - When true hides the step label
         * stepClick: Function - Callback called when the step (indicator + label) has been clicked
         */
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            require: '^fdWizard',
            scope: {
                dgLabel: '@',
                dgSize: '@',
                optionalLabel: '@',
                indicatorGlyph: '@',
                indicatorLabel: '@',
                noLabel: '<',
                stepClick: '&',
            },
            link: function (scope, element, attrs, wizCtrl) {
                if (scope.dgSize && !wizCtrl.isValidSize(scope.dgSize)) {
                    console.error(`fd-wizard-content error: 'dg-size' must be one of: ${wizCtrl.getValidSizes().join(', ')}`);
                }
                wizCtrl.addStep(scope);

                scope.isStepCurrent = function () {
                    return wizCtrl.isStepCurrent(scope);
                }

                scope.onNextClick = function () {
                    wizCtrl.gotoNextStep();
                }

                scope.getClasses = () => classNames('fd-wizard__step-content-container', {
                    [`fd-wizard__step-content-container--${scope.dgSize}`]: wizCtrl.isValidSize(scope.dgSize),
                });

                scope.$on('$destroy', function () {
                    wizCtrl.removeStep(scope);
                });
            },
            template: `<div ng-show="isStepCurrent()" ng-class="getClasses()" ng-transclude></div>`
        };
    }]).directive('fdWizardSummary', ['classNames', function (classNames) {
        /**
         * dgSize: String - When specified adds horizontal paddings to the content. Could be one of: 'sm', 'md', 'lg', 'xl'
         */
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            require: '^fdWizard',
            scope: {
                dgSize: '@',
            },
            link: function (scope, element, attrs, wizCtrl) {
                if (scope.dgSize && !wizCtrl.isValidSize(scope.dgSize)) {
                    console.error(`fd-wizard-content error: 'dg-size' must be one of: ${wizCtrl.getValidSizes().join(', ')}`);
                }
                scope.allStepsCompleted = function () {
                    return wizCtrl.allStepsCompleted() && !wizCtrl.hasCurrentStep();
                }
                scope.getClasses = () => classNames('fd-wizard__step-content-container', {
                    [`fd-wizard__step-content-container--${scope.dgSize}`]: wizCtrl.isValidSize(scope.dgSize),
                });
            },
            template: `<div ng-show="allStepsCompleted()" ng-class="getClasses()" ng-transclude></div>`
        };
    }]).directive('fdWizardNextStep', [function () {
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            template: `<div class="fd-wizard__next-step" ng-transclude></div>`
        }
    }]).directive('fdPanel', [function () {
        /**
         * expanded: Boolean - Whether the panel is expanded or not
         * fixed: Boolean - Whether the panel is expandable or not
         * compact: Boolean - Panel size
         * expandedChange: Function - A callback called when the Expand button is clicked
         */
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            scope: {
                expanded: '<',
                fixed: '<',
                compact: '<',
                expandedChange: '&'
            },
            controller: ['$scope', 'classNames', function ($scope, classNames) {
                $scope.expanded = !!$scope.expanded;

                this.isFixed = () => $scope.fixed;
                this.isExpanded = () => $scope.expanded;
                this.isCompact = () => $scope.compact;
                this.getContentId = () => $scope.contentId;
                this.getTitleId = () => $scope.titleId;

                this.setContentId = (id) => {
                    $scope.contentId = id;
                }

                this.setTitleId = (id) => {
                    $scope.titleId = id;
                }

                this.toggleExpanded = function () {
                    $scope.expanded = !$scope.expanded;

                    if ($scope.expandedChange) {
                        $scope.expandedChange({ expanded: $scope.expanded });
                    }
                }

                $scope.getClasses = () => classNames('fd-panel', {
                    'fd-panel--compact': $scope.compact,
                    'fd-panel--fixed': $scope.fixed
                });
            }],
            template: `<div ng-class="getClasses()" ng-transclude></div>`
        }
    }]).directive('fdPanelHeader', [function () {
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            template: `<div class="fd-panel__header" ng-transclude></div>`
        }
    }]).directive('fdPanelExpand', [function () {
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            require: '^^fdPanel',
            link: function (scope, element, attrs, panelCtrl) {
                scope.isFixed = () => panelCtrl.isFixed();
                scope.isCompact = () => panelCtrl.isCompact();
                scope.isExpanded = () => panelCtrl.isExpanded();
                scope.getContentId = () => panelCtrl.getContentId();
                scope.getTitleId = () => panelCtrl.getTitleId();
                scope.toggleExpanded = function () {
                    panelCtrl.toggleExpanded();
                }
                scope.getExpandButtonIcon = function () {
                    return panelCtrl.isExpanded() ? 'sap-icon--slim-arrow-down' : 'sap-icon--slim-arrow-right';
                }
            },
            template: `<div ng-show="!isFixed()" class="fd-panel__expand">
                <fd-button ng-click="toggleExpanded()" glyph="{{ getExpandButtonIcon() }}" dg-type="transparent" compact="{{ isCompact() || 'false' }}" class="fd-panel__button" 
                    aria-haspopup="true" aria-expanded="{{ isExpanded() }}" aria-controls="{{ getContentId() }}" aria-labelledby="{{ getTitleId() }}" 
                    aria-label="expand/collapse panel"></fd-button>
            </div>`
        }
    }]).directive('fdPanelTitle', ['uuid', function (uuid) {
        return {
            restrict: 'A',
            require: '^^fdPanel',
            link: function (scope, element, attrs, panelCtrl) {
                element.addClass('fd-panel__title');

                let id = attrs.id;
                if (!id) {
                    id = `pt-${uuid.generate()}`;
                    element[0].setAttribute('id', id);
                }

                panelCtrl.setTitleId(id);
            }
        }
    }]).directive('fdPanelContent', ['uuid', function (uuid) {
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            require: '^fdPanel',
            link: function (scope, element, attrs, panelCtrl) {
                scope.isHidden = function () {
                    return !panelCtrl.isFixed() && !panelCtrl.isExpanded();
                }

                let id = attrs.id;
                if (!id) {
                    id = `ph-${uuid.generate()}`;
                    element[0].setAttribute('id', id);
                }

                panelCtrl.setContentId(id);
            },
            template: `<div role="region" class="fd-panel__content" aria-hidden="{{ isHidden() }}" ng-transclude></div>`
        }
    }]).directive('fdMessagePage', ['classNames', function (classNames) {
        /**
         * glyph: String - Icon class
         */
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            scope: {
                glyph: '@'
            },
            link: function (scope) {
                if (!scope.glyph) {
                    console.error('fd-message-page error: You should provide glpyh icon using the "glyph" attribute');
                }

                scope.getIconClasses = () => classNames(scope.glyph, 'fd-message-page__icon');
            },
            template: `<div class="fd-message-page">
                <div class="fd-message-page__container">
                    <div class="fd-message-page__icon-container">
                        <i role="presentation" ng-class="getIconClasses()"></i>
                    </div>
                    <div role="status" aria-live="polite" class="fd-message-page__content" ng-transclude></div>
                </div>
            </div>`
        }
    }]).directive('fdMessagePageTitle', [function () {
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            template: `<div class="fd-message-page__title" ng-transclude></div>`
        }
    }]).directive('fdMessagePageSubtitle', [function () {
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            template: `<div class="fd-message-page__subtitle" ng-transclude></div>`
        }
    }]).directive('fdMessagePageActions', [function () {
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            template: `<div class="fd-message-page__actions" ng-transclude></div>`
        }
    }]).directive('fdMessagePageMore', [function () {
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            template: `<div class="fd-message-page__more" ng-transclude></div>`
        }
    }]).directive('fdUploadCollection', ['classNames', function (classNames) {
        /**
         * small: Boolean - Whether or not this is the small upload collection.
         * selection: Boolean - Whether or not this upload collection supports selection.
         */
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            scope: {
                small: '<',
                selection: '<'
            },
            link: function (scope) {
                scope.getClassNames = () => classNames('fd-list', 'fd-list--byline', 'fd-upload-collection', {
                    'fd-upload-collection--sm': scope.small,
                    'fd-list--selection': scope.selection
                });
            },
            template: `<ul ng-class="getClassNames()" role="list" ng-transclude></ul>`
        }
    }]).directive('fdUploadCollectionItem', ['classNames', function (classNames) {
        /**
         * dgSelected: Boolean - Whether or not this item is selected
         * fileName: Boolean - The name of the file, not including the type extension.
         * extension: Boolean - The file type extension.
         * fileNameChanged: Function - Event emitted when the user changes a file name. Args: (fileName : String)
         * deleteClicked: Function - Event emitted when presses the delete button.
         */
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            scope: {
                dgSelected: '<',
                fileName: '@',
                extension: '@',
                fileNameChanged: '&',
                deleteClicked: '&'
            },
            controller: ['$scope', function ($scope) {
                $scope.editing = false;
                this.getFileName = function () {
                    return $scope.fileName;
                }
                this.getEditedFileName = function () {
                    return $scope.editedFileName;
                }
                this.getExtension = function () {
                    return $scope.extension;
                }
                this.isEditing = function () {
                    return $scope.editing;
                }
                this.setEditing = function (editing) {
                    $scope.editing = editing;
                    $scope.editedFileName = $scope.fileName;
                }
                this.fileNameChanged = function (fileName) {
                    $scope.editedFileName = fileName;
                }
                this.applyFilenameChange = function () {
                    $scope.fileName = $scope.editedFileName;
                    this.setEditing(false);

                    if ($scope.fileNameChanged)
                        $scope.fileNameChanged({ fileName: $scope.fileName });
                }

                this.deleteItem = function () {
                    if ($scope.deleteClicked)
                        $scope.deleteClicked();
                }

                $scope.getClassNames = () => classNames('fd-list__item', 'fd-upload-collection__item', {
                    'is-selected': $scope.dgSelected
                });
                $scope.getAriaSelected = () => $scope.dgSelected ? 'true' : undefined;
            }],
            template: `<li role="listitem" tabindex="0" ng-class="getClassNames()" ng-attr-aria-selected="{{ getAriaSelected() }}" ng-transclude></li>`
        }
    }]).directive('fdUploadCollectionItemContent', [function () {
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            template: `<div class="fd-list__content" ng-transclude><div>`
        }
    }]).directive('fdUploadCollectionTitleContainer', [function () {
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            template: `<div class="fd-upload-collection__title-container" ng-transclude></div>`
        }
    }]).directive('fdUploadCollectionTitle', [function () {
        return {
            restrict: 'EA',
            replace: true,
            require: '^^fdUploadCollectionItem',
            link: function (scope, element, attr, itemCtrl) {
                scope.getTitle = () => `${itemCtrl.getFileName()}.${itemCtrl.getExtension()}`;
                scope.isEditing = () => itemCtrl.isEditing();
            },
            template: `<span ng-if="!isEditing()" class="fd-list__title fd-upload-collection__title">{{ getTitle() }}</span>`
        }
    }]).directive('fdUploadCollectionFormItem', [function () {
        return {
            restrict: 'EA',
            replace: true,
            require: '^^fdUploadCollectionItem',
            link: function (scope, element, attr, itemCtrl) {
                let editing;
                scope.file = { name: itemCtrl.getFileName() };
                scope.getExtension = () => itemCtrl.getExtension();
                scope.isEditing = () => {
                    if (editing !== itemCtrl.isEditing()) {
                        scope.file.name = itemCtrl.getFileName();
                        editing = itemCtrl.isEditing();
                    }
                    return editing;
                }
                scope.onFileNameChange = () => itemCtrl.fileNameChanged(scope.file.name);
                scope.getInputState = () => scope.file.name ? null : 'error';
            },
            template: `<div ng-if="isEditing()" class="fd-upload-collection__form-item">
                <fd-input type="text" placeholder="Filename" state="{{ getInputState() }}" ng-required ng-model="file.name" ng-change="onFileNameChange()" style="pointer-events: all"></fd-input>
                <span class="fd-upload-collection__extension">.{{ getExtension() }}</span>
            </div>`
        }
    }]).directive('fdUploadCollectionDescription', [function () {
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            template: `<div class="fd-upload-collection__description" ng-transclude><div>`
        }
    }]).directive('fdUploadCollectionTextSeparator', [function () {
        return {
            restrict: 'EA',
            replace: true,
            template: `<span class="fd-upload-collection__text-separator"></span>`
        }
    }]).directive('fdUploadCollectionStatusGroup', [function () {
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            template: `<div class="fd-upload-collection__status-group" ng-transclude><div>`
        }
    }]).directive('fdUploadCollectionStatusItem', [function () {
        return {
            restrict: 'A',
            require: 'fdObjectStatus',
            link: function (scope, element, attr, objectStatusCtrl) {
                objectStatusCtrl.setIsUploadCollection();
            }
        }
    }]).directive('fdUploadCollectionButtonGroup', [function () {
        return {
            restrict: 'EA',
            replace: true,
            require: '^fdUploadCollectionItem',
            link: function (scope, element, attr, itemCtrl) {
                scope.isEditing = () => itemCtrl.isEditing();
                scope.editClick = (e) => {
                    e.stopPropagation();

                    itemCtrl.setEditing(true);
                }
                scope.cancelClick = (e) => {
                    e.stopPropagation();

                    itemCtrl.setEditing(false);
                }
                scope.deleteClick = (e) => {
                    e.stopPropagation();

                    itemCtrl.deleteItem();
                }
                scope.okClick = (e) => {
                    e.stopPropagation();

                    itemCtrl.applyFilenameChange();
                }
                scope.getOkButtonState = () => itemCtrl.getEditedFileName() ? undefined : 'disabled';
            },
            template: `<div class="fd-upload-collection__button-group">
                <fd-button ng-if="!isEditing()" aria-label="Edit" dg-type="transparent" glyph="sap-icon--edit" ng-click="editClick($event)"></fd-button>
				<fd-button ng-if="!isEditing()" aria-label="Delete" dg-type="transparent" glyph="sap-icon--decline" ng-click="deleteClick($event)"></fd-button>
                <fd-button ng-if="isEditing()" dg-label="Ok" state="{{ getOkButtonState() }}" dg-type="transparent" ng-click="okClick($event)"></fd-button>
				<fd-button ng-if="isEditing()" dg-label="Cancel" dg-type="transparent" ng-click="cancelClick($event)"></fd-button>
            <div>`
        }
    }]).directive('fdTokenizer', [function () {
        /**
         * compact: Boolean - Whether the tokenizer is compact
         * focusable: Boolean - Whether tokenizer should have fake focus indicator, when input is focused inside
         * scrollable: Boolean - Whether tokenizer is scrollable. When false displays 'more' indicator with the number of the hidden tokens
         */
        return {
            restrict: 'EA',
            transclude: true,
            replace: true,
            scope: {
                compact: '<',
                focusable: '<',
                scrollable: '<'
            },
            controller: ['$scope', '$element', 'classNames', function ($scope, $element, classNames) {
                $scope.tokenElements = [];
                $scope.numberOfVisibleTokens = 0;

                const tokenElementWidths = new Map();
                const scrollable = !!$scope.scrollable;
                let tokenizerRO, tokensRO;

                function findNumberOfVisibleTokens() {
                    let width = 0;
                    const indicatorWidth = 50;
                    const inputMinWidth = getInputMinWidth();
                    const containerWidth = $element.width();
                    const availableWidth = containerWidth - inputMinWidth - indicatorWidth;
                    for (let i = 0; i < $scope.tokenElements.length; i++) {
                        let el = $scope.tokenElements[i];
                        width += tokenElementWidths.get(el[0]);
                        if (availableWidth - width < 0) {
                            $scope.numberOfVisibleTokens = i;
                            return;
                        }
                    }
                    $scope.numberOfVisibleTokens = $scope.tokenElements.length;
                }

                function getInputMinWidth() {
                    const input = $element.find('.fd-tokenizer__input');
                    const minWidth = input.css('min-width');
                    return minWidth ? parseInt(minWidth, 10) : 0;
                }

                this.addToken = function (tokenElement) {
                    $scope.tokenElements.push(tokenElement);

                    if (!scrollable) {
                        const el = tokenElement[0];
                        tokenElementWidths.set(el, tokenElement.outerWidth(true));
                        tokensRO.observe(el);
                        findNumberOfVisibleTokens();
                    }
                }

                this.removeToken = function (tokenElement) {
                    const index = $scope.tokenElements.indexOf(tokenElement);
                    if (index >= 0) {
                        $scope.tokenElements.splice(index, 1);

                        if (!scrollable) {
                            const el = tokenElement[0];
                            tokenElementWidths.delete(el);
                            tokensRO.unobserve(el);
                            findNumberOfVisibleTokens();
                        }
                    }
                }

                this.isTokenVisible = function (tokenElement) {
                    if (scrollable)
                        return true;

                    const index = $scope.tokenElements.indexOf(tokenElement);
                    return index < $scope.numberOfVisibleTokens;
                }

                this.getNumberOfHiddenTokens = function () {
                    if (scrollable)
                        return 0;

                    const count = $scope.tokenElements.length - $scope.numberOfVisibleTokens;
                    return count < 0 ? 0 : count;
                }

                $scope.getClasses = () => classNames('fd-tokenizer', {
                    'fd-tokenizer--compact': $scope.compact,
                    'is-focus': $scope.focusable,
                    'fd-tokenizer--scrollable': $scope.scrollable
                });

                if (!scrollable) {
                    tokenizerRO = new ResizeObserver(() => {
                        $scope.$apply(findNumberOfVisibleTokens);
                    });

                    tokenizerRO.observe($element[0]);

                    tokensRO = new ResizeObserver(entries => {
                        for (let entry of entries) {
                            if (entry.contentRect.width > 0)
                                tokenElementWidths.set(entry.target, $(entry.target).outerWidth(true));
                        }
                        $scope.$apply(findNumberOfVisibleTokens);
                    });

                    $scope.$on('$destroy', function () {
                        tokenizerRO.disconnect();
                        tokensRO.disconnect();
                    });
                }
            }],
            template: `<div ng-class="getClasses()">
            <div class="fd-tokenizer__inner" tabindex="0" ng-transclude></div>
        </div>`
        }
    }]).directive('fdToken', ['classNames', function (classNames) {
        /**
         * dgText: String - The text of the token
         * compact: Boolean - Whether the token is compact.
         * dgReadOnly: Boolean - Whether the token is read-only.
         * closeButtonAriaLabel: String - Aria label for the close button
         * closeClicked: Function - A callback called when the close button has been clicked 
         */
        return {
            restrict: 'EA',
            replace: true,
            require: '?^fdTokenizer',
            scope: {
                dgText: '@',
                compact: '<',
                dgReadOnly: '<',
                closeButtonAriaLabel: '@',
                closeClicked: '&'
            },
            link: function (scope, element, attr, tokenizerCtrl) {
                if (tokenizerCtrl) {
                    tokenizerCtrl.addToken(element);

                    scope.$on('$destroy', function () {
                        tokenizerCtrl.removeToken(element);
                    });
                }

                scope.getClasses = () => classNames('fd-token', {
                    'fd-token--compact': scope.compact,
                    'fd-token--readonly': scope.dgReadOnly
                })

                scope.isVisible = () => tokenizerCtrl ? tokenizerCtrl.isTokenVisible(element) : true;
            },
            template: `<span ng-show="isVisible()" ng-class="getClasses()" role="button">
            <span class="fd-token__text">{{dgText}}</span>
            <button ng-if="!dgReadOnly" class="fd-token__close" aria-label="{{closeButtonAriaLabel}}" ng-click="closeClicked()"></button>
        </span>`
        }
    }]).directive('fdTokenIndicator', [function () {
        return {
            restrict: 'EA',
            replace: true,
            require: '^fdTokenizer',
            link: function (scope, element, attr, tokenizerCtrl) {
                scope.getNumberOfHiddenTokens = () => tokenizerCtrl.getNumberOfHiddenTokens();
                scope.getText = () => `${tokenizerCtrl.getNumberOfHiddenTokens()} more`;
            },
            template: `<span ng-if="getNumberOfHiddenTokens() > 0" class="fd-tokenizer__indicator">{{ getText() }}</span>`
        }
    }]).directive('fdToolHeader', ['classNames', function (classNames) {
        /**
         * hasMenu: Boolean - If the toolbar will contain a hamburger menu.
         * dgSize: String - Manually set the horizontal paddings of the tool header. Possible options are 'sm', 'md', 'lg' and 'xl'.
         * responsive: Boolean - Automatically adjust the horizontal paddings.
         */
        return {
            restrict: 'E',
            transclude: true,
            replace: true,
            scope: {
                hasMenu: '<',
                dgSize: '@',
                responsive: '<'
            },
            link: function (scope) {
                scope.getClasses = () => classNames('fd-tool-header', {
                    'fd-tool-header--menu': scope.hasMenu,
                    'fd-tool-header--responsive-paddings': scope.responsive,
                    [`fd-tool-header--${scope.dgSize}`]: scope.dgSize,
                });
            },
            template: `<div ng-class="getClasses()" ng-transclude></div>`
        }
    }]).directive('fdToolHeaderGroup', [function () {
        return {
            restrict: 'A',
            link: {
                pre: function (scope, element) {
                    element.addClass('fd-tool-header__group');
                }
            },
        }
    }]).directive('fdToolHeaderElement', [function () {
        return {
            restrict: 'A',
            link: {
                pre: function (scope, element) {
                    element.addClass('fd-tool-header__element');
                }
            },
        }
    }]).directive('fdToolHeaderButton', [function () {
        return {
            restrict: 'A',
            link: {
                pre: function (scope, element) {
                    element.addClass('fd-tool-header__button');
                }
            },
        }
    }]).directive('fdToolHeaderTitle', [function () {
        return {
            restrict: 'A',
            link: {
                pre: function (scope, element) {
                    element.addClass('fd-tool-header__title');
                }
            },
        }
    }]).directive('fdToolHeaderLogo', [function () {
        return {
            restrict: 'A',
            link: {
                pre: function (scope, element) {
                    element.addClass('fd-tool-header__logo');
                }
            },
        }
    }]).directive('fdVerticalNav', ['classNames', function (classNames) {
        /**
         * condensed: Boolean - Condensed navigation mode.
         * canScroll: Boolean - Enable/disable scroll navigation support. Default is false.
         */
        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            scope: {
                condensed: '<?',
                canScroll: '<?',
            },
            link: function (scope) {
                scope.getClasses = function () {
                    return classNames('fd-vertical-nav', {
                        'fd-vertical-nav--condensed': scope.condensed === true,
                        'fd-vertical-nav--overflow': scope.canScroll === true,
                        'fd-scrollbar': scope.canScroll === true,
                    });
                };
            },
            template: '<div ng-class="getClasses()" ng-transclude></div>'
        }
    }]).directive('fdVerticalNavMainSection', [function () {
        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            link: {
                pre: function (scope, element, attrs) {
                    if (!attrs.hasOwnProperty('ariaLabel'))
                        console.error('fdVerticalNavMainSection error: You must set the "aria-label" attribute');
                },
            },
            template: '<nav class="fd-vertical-nav__main-navigation" ng-transclude></nav>'
        }
    }]).directive('fdVerticalNavUtilitySection', [function () {
        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            link: {
                pre: function (scope, element, attrs) {
                    if (!attrs.hasOwnProperty('ariaLabel'))
                        console.error('fdVerticalNavUtilitySection error: You must set the "aria-label" attribute');
                },
            },
            template: '<nav class="fd-vertical-nav__utility-section" ng-transclude></nav>'
        }
    }]).directive('fdListNavigationItem', ['classNames', function (classNames) {
        /**
         * indicated: Boolean - Navigation item is indicated.
         * condensed: Boolean - Condensed navigation mode.
         * expandable: Boolean - The item can expand.
         * isExpanded: Boolean - If the item is expanded or not.
         */
        return {
            restrict: 'E',
            transclude: true,
            replace: true,
            scope: {
                indicated: '<?',
                condensed: '<?',
                expandable: '<?',
                isExpanded: '<?',
            },
            link: {
                post: function (scope) {
                    scope.getClasses = () => classNames('fd-list__navigation-item', {
                        'fd-list__navigation-item--condensed': scope.condensed === true,
                        'fd-list__navigation-item--indicated': scope.indicated === true,
                        'fd-list__navigation-item--expandable': scope.expandable === true,
                        'is-expanded': scope.isExpanded === true,
                    });
                },
            },
            template: '<li ng-class="getClasses()" tabindex="0" ng-transclude></li>'
        }
    }]).directive('fdListNavigationItemPopover', ['classNames', function (classNames) {
        /**
         * isExpanded: Boolean - If the popover is expanded or not.
         */
        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            scope: {
                level: '<',
                isExpanded: '<',
            },
            link: {
                post: function (scope) {
                    scope.getClasses = () => classNames('fd-popover__body', 'fd-popover__body--no-arrow', {
                        'fd-list__navigation-item-popover--first-level': scope.level === 1,
                        'fd-list__navigation-item-popover--second-level': scope.level === 2,
                    });
                },
            },
            template: '<div ng-class="getClasses()" ng-show="isExpanded" ng-transclude></div>'
        }
    }]).directive('fdListNavigationItemText', [function () {
        return {
            restrict: 'A',
            link: {
                pre: function (scope, element) {
                    element.addClass('fd-list__navigation-item-text');
                }
            },
        }
    }]).directive('fdListNavigationItemText', [function () {
        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            template: '<span class="fd-list__navigation-item-text" ng-transclude></span>'
        }
    }]).directive('fdListNavigationItemIndicator', [function () {
        return {
            restrict: 'E',
            replace: true,
            template: '<span class="fd-list__navigation-item-indicator"></span>'
        }
    }]).directive('fdListNavigationItemArrow', ['classNames', function (classNames) {
        /**
         * isExpanded: Boolean - If the item is expanded or not.
         */
        return {
            restrict: 'E',
            replace: true,
            scope: {
                isExpanded: '<?',
            },
            link: {
                pre: function (scope, element, attrs) {
                    if (!attrs.hasOwnProperty('ariaLabel'))
                        console.error('fdListNavigationItemArrow error: You must set the "aria-label" attribute');
                },
                post: function (scope) {
                    scope.getClasses = () => classNames('fd-list__navigation-item-arrow', {
                        'is-expanded': scope.isExpanded === true,
                        'sap-icon--navigation-down-arrow': scope.isExpanded === true,
                        'sap-icon--navigation-right-arrow': scope.isExpanded !== true,
                    });
                },
            },
            template: `<button ng-class="getClasses()"></button>`
        }
    }]).directive('fdListNavigationItemIcon', ['classNames', function (classNames) {
        /**
         * glyph: String - Icon class. For example 'sap-icon--home'.
         * svgPath: String - The src path to your svg image.
         * iconSize: String - The size of the item icon. Possible options are include 'lg' and none.
         */
        return {
            restrict: 'E',
            replace: true,
            scope: {
                glyph: '@?',
                svgPath: '@?',
                iconSize: '@?',
            },
            link: function (scope) {
                if (!scope.glyph && !scope.svgPath) {
                    console.error('fd-list-navigation-item-icon error: You must provide a glpyh or an svg icon');
                }
                scope.getClasses = () => classNames('fd-list__navigation-item-icon', scope.glyph, {
                    'dg-nav-item--lg': scope.iconSize === 'lg',
                    'dg-nav-item--svg': scope.svgPath,
                });
            },
            template: '<i role="presentation" ng-class="getClasses()"><ng-include ng-if="svgPath" src="svgPath"></ng-include></i>'
        }
    }]).directive('fdListNavigationGroupHeader', [function () {
        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            template: '<li role="listitem" class="fd-list__group-header fd-vertical-nav__group-header"><span class="fd-list__title" ng-transclude></span></li>'
        }
    }]);