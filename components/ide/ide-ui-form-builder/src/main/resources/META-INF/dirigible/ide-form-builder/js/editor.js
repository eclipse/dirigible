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
const editorView = angular.module('app', ['ideUI', 'ideView', 'ideWorkspace', 'codeEditor']);
editorView.config(["messageHubProvider", function (messageHubProvider) {
    messageHubProvider.eventIdPrefix = 'formEditor';
}]);
editorView.controller('DesignerController', ['$scope', '$window', '$document', '$timeout', '$compile', '$http', 'uuid', 'messageHub', 'ViewParameters', 'workspaceApi', function ($scope, $window, $document, $timeout, $compile, $http, uuid, messageHub, ViewParameters, workspaceApi) {
    $scope.selectedTab = 'designer';
    $scope.isFileChanged = false;
    $scope.state = {
        isBusy: true,
        error: false,
        busyText: "Loading...",
        canSave: true,
        preview: false,
    };

    $scope.forms = {
        formProperties: {},
    };

    $scope.selectedCtrlId = undefined;
    $scope.selectedContainerId = undefined;

    $scope.formModel = [];
    $scope.formData = {
        feeds: [],
        scripts: [],
        code: ''
    };

    angular.element($window).bind("focus", function () {
        messageHub.setFocusedEditor($scope.dataParameters.file);
        messageHub.setStatusCaret('');
    });

    $scope.builderComponents = [
        {
            id: 'fb-controls',
            label: 'Controls',
            items: [
                {
                    controlId: 'header',
                    label: 'Header',
                    icon: "sap-icon--heading-1",
                    description: 'Text header',
                    template: `<div class="fb-control-wrapper" ng-click="showProps($event)" data-id="{{id}}"><h1 fd-title header-size="props.size.value">{{props.title.value}}</h1></div>`,
                    props: {
                        title: {
                            type: 'text',
                            label: 'Label',
                            value: 'Title',
                            required: true
                        },
                        size: {
                            type: 'number',
                            label: 'Size',
                            value: 1,
                            max: 6,
                            min: 1,
                            step: 1,
                            required: true
                        }
                    }
                },
                {
                    controlId: 'button',
                    label: 'Button',
                    icon: 'sap-icon--border',
                    description: 'Button',
                    template: `<div class="fb-control-wrapper" ng-click="showProps($event)" data-id="{{id}}"><fd-button class="{{props.sizeToText.value ? 'dg-float-right' : 'dg-full-width'}}" type="{{props.isSubmit.value ? 'submit' : 'button'}}" dg-label="{{props.label.value}}" dg-type="{{props.type.value}}" ng-click="submit()"></fd-button></div>`,
                    props: {
                        label: {
                            type: 'text',
                            label: 'Label',
                            value: 'Button',
                            placeholder: 'Button label',
                            required: true
                        },
                        type: {
                            type: 'dropdown',
                            label: 'Button type',
                            value: '',
                            items: [
                                {
                                    label: "Default",
                                    value: '',
                                },
                                {
                                    label: "Emphasized",
                                    value: 'emphasized',
                                },
                                {
                                    label: "Ghost",
                                    value: 'ghost',
                                },
                                {
                                    label: "Positive",
                                    value: 'positive',
                                },
                                {
                                    label: "Negative",
                                    value: 'negative',
                                },
                                {
                                    label: "Attention",
                                    value: 'attention',
                                },
                                {
                                    label: "Transparent",
                                    value: 'transparent',
                                }
                            ]
                        },
                        sizeToText: {
                            type: 'checkbox',
                            label: 'Size to text',
                            value: false,
                        },
                        isSubmit: {
                            type: 'checkbox',
                            label: 'Submits form',
                            value: true,
                        },
                        callback: {
                            type: 'text',
                            label: 'Callback function',
                            value: '',
                            placeholder: 'callbackFn()',
                        },
                    }
                },
                {
                    controlId: 'input-textfield',
                    label: 'Text Field',
                    icon: "sap-icon--edit",
                    description: 'Input field',
                    template: `<div class="fb-control-wrapper" ng-click="showProps($event)" data-id="{{id}}"><fd-form-item horizontal="props.horizontal.value">
                        <fd-form-label dg-colon="true" dg-required="props.required.value" for="{{props.id.value}}">
                            {{ props.label.value }}
                        </fd-form-label>
                        <fd-form-input-message-group dg-inactive="{{props.errorState.invalid}}">
                            <fd-input id="{{props.id.value}}" type="text" placeholder="{{props.placeholder.value}}"
                                state="{'error' : props.errorState.invalid }" name="{{props.id.value}}" ng-required="props.required.value">
                            </fd-input>
                            <fd-form-message dg-type="error">{{props.errorState.value || 'Incorrect input'}}</fd-form-message>
                        </fd-form-input-message-group>
                    </fd-form-item></div>`,
                    props: {
                        id: {
                            type: 'text',
                            label: 'ID',
                            placeholder: 'Form Item ID',
                            value: '',
                            required: true,
                        },
                        label: {
                            type: 'text',
                            label: 'Label',
                            value: 'Input',
                            placeholder: 'Input label',
                            required: false,
                        },
                        horizontal: {
                            type: 'checkbox',
                            label: 'Is horizontal',
                            value: false,
                        },
                        placeholder: {
                            type: 'text',
                            label: 'Placeholder',
                            value: '',
                            placeholder: 'Input placeholder',
                        },
                        type: {
                            type: 'dropdown',
                            label: 'Input type',
                            value: 'text',
                            required: true,
                            items: [
                                {
                                    label: "Text",
                                    value: 'text',
                                },
                                {
                                    label: "Email",
                                    value: 'email',
                                },
                                {
                                    label: "Password",
                                    value: 'password',
                                },
                                {
                                    label: "URL",
                                    value: 'URL',
                                }
                            ],
                        },
                        model: {
                            type: 'text',
                            label: 'Model',
                            value: '',
                            placeholder: 'inputVar',
                        },
                        required: {
                            type: 'checkbox',
                            label: 'Is required',
                            value: true,
                        },
                        minLength: {
                            type: 'number',
                            label: 'Minimum length',
                            value: 0,
                            min: 0,
                            step: 1,
                            required: false,
                        },
                        maxLength: {
                            type: 'number',
                            label: 'Maximum length',
                            value: -1,
                            min: -1,
                            step: 1,
                            required: false,
                        },
                        validationRegex: {
                            type: 'text',
                            label: 'Regex validation',
                            value: '',
                            placeholder: '^[^/]*$',
                            required: false,
                        },
                        errorState: {
                            type: 'text',
                            label: 'Error state popover message',
                            value: 'Incorrect input',
                            invalid: false,
                            placeholder: 'Input label',
                            required: false,
                        },
                    },
                },
                {
                    controlId: 'input-textarea',
                    label: 'Text Area',
                    icon: "sap-icon--edit",
                    description: 'Text Area',
                    template: `<div class="fb-control-wrapper" ng-click="showProps($event)" data-id="{{id}}"><fd-form-item horizontal="props.horizontal.value">
                        <fd-form-label dg-colon="true" dg-required="props.required.value" for="{{props.id.value}}">
                            {{ props.label.value }}
                        </fd-form-label>
                        <fd-form-input-message-group dg-inactive="{{props.errorState.invalid}}">
                            <fd-textarea id="{{props.id.value}}" type="text" placeholder="{{props.placeholder.value}}"
                                state="{'error' : props.errorState.invalid }" name="{{props.id.value}}" ng-required="props.required.value">
                            </fd-textarea>
                            <fd-form-message dg-type="error">{{props.errorState.value || 'Incorrect input'}}</fd-form-message>
                        </fd-form-input-message-group>
                    </fd-form-item></div>`,
                    props: {
                        id: {
                            type: 'text',
                            label: 'ID',
                            placeholder: 'Form Item ID',
                            value: '',
                            required: true,
                        },
                        label: {
                            type: 'text',
                            label: 'Label',
                            value: 'Input',
                            placeholder: 'Input label',
                            required: false,
                        },
                        horizontal: {
                            type: 'checkbox',
                            label: 'Is horizontal',
                            value: false,
                        },
                        placeholder: {
                            type: 'text',
                            label: 'Placeholder',
                            value: '',
                            placeholder: 'Input placeholder',
                        },
                        type: {
                            type: 'dropdown',
                            label: 'Input type',
                            value: 'text',
                            required: true,
                            items: [
                                {
                                    label: "Text",
                                    value: 'text',
                                },
                                {
                                    label: "Email",
                                    value: 'email',
                                },
                                {
                                    label: "Password",
                                    value: 'password',
                                },
                                {
                                    label: "URL",
                                    value: 'URL',
                                }
                            ],
                        },
                        model: {
                            type: 'text',
                            label: 'Model',
                            value: '',
                            placeholder: '',
                        },
                        required: {
                            type: 'checkbox',
                            label: 'Is required',
                            value: true,
                        },
                        minLength: {
                            type: 'number',
                            label: 'Minimum length',
                            value: 0,
                            min: 0,
                            step: 1,
                            required: false,
                        },
                        maxLength: {
                            type: 'number',
                            label: 'Maximum length',
                            value: -1,
                            min: -1,
                            step: 1,
                            required: false,
                        },
                        validationRegex: {
                            type: 'text',
                            label: 'Regex validation',
                            value: '',
                            placeholder: 'Regular expression',
                            required: false,
                        },
                        errorState: {
                            type: 'text',
                            label: 'Error state popover message',
                            value: 'Incorrect input',
                            invalid: false,
                            placeholder: 'Input label',
                            required: false,
                        },
                    },
                },
                {
                    controlId: 'input-time',
                    label: 'Time Field',
                    icon: "sap-icon--in-progress",
                    description: 'Time input',
                    template: `<div class="fb-control-wrapper" ng-click="showProps($event)" data-id="{{id}}"><fd-form-item horizontal="props.horizontal.value">
                    <fd-form-label dg-colon="true" dg-required="props.required.value" for="{{props.id.value}}">
                        {{ props.label.value }}
                    </fd-form-label>
                    <fd-input id="{{props.id.value}}" name="{{props.id.value}}" ng-required="props.required.value" type="time" value="13:30">
	                </fd-input></fd-form-item></div>`,
                    props: {
                        id: {
                            type: 'text',
                            label: 'ID',
                            placeholder: 'Form Item ID',
                            value: '',
                            required: true,
                        },
                        label: {
                            type: 'text',
                            label: 'Label',
                            value: 'Input',
                            placeholder: 'Input label',
                            required: false,
                        },
                        horizontal: {
                            type: 'checkbox',
                            label: 'Is horizontal',
                            value: false,
                        },
                        model: {
                            type: 'text',
                            label: 'Model',
                            value: '',
                            placeholder: 'inputVar',
                        },
                        required: {
                            type: 'checkbox',
                            label: 'Is required',
                            value: true,
                        },
                    },
                },
                {
                    controlId: 'input-date',
                    label: 'Date Field',
                    icon: "sap-icon--calendar",
                    description: 'Date input',
                    template: `<div class="fb-control-wrapper" ng-click="showProps($event)" data-id="{{id}}"><fd-form-item horizontal="props.horizontal.value">
                    <fd-form-label dg-colon="true" dg-required="props.required.value" for="{{props.id.value}}">
                        {{ props.label.value }}
                    </fd-form-label>
                    <fd-input id="{{props.id.value}}" name="{{props.id.value}}" ng-required="props.required.value" type="{{props.type.value}}">
	                </fd-input></fd-form-item></div>`,
                    props: {
                        id: {
                            type: 'text',
                            label: 'ID',
                            placeholder: 'Form Item ID',
                            value: '',
                            required: true,
                        },
                        label: {
                            type: 'text',
                            label: 'Label',
                            value: 'Input',
                            placeholder: 'Input label',
                            required: false,
                        },
                        horizontal: {
                            type: 'checkbox',
                            label: 'Is horizontal',
                            value: false,
                        },
                        type: {
                            type: 'dropdown',
                            label: 'Input type',
                            value: 'date',
                            required: true,
                            items: [
                                {
                                    label: 'Date',
                                    value: 'date',
                                },
                                {
                                    label: 'Datetime',
                                    value: 'datetime-local',
                                }
                            ],
                        },
                        model: {
                            type: 'text',
                            label: 'Model',
                            value: '',
                            placeholder: 'inputVar',
                        },
                        required: {
                            type: 'checkbox',
                            label: 'Is required',
                            value: true,
                        },
                    },
                },
                {
                    controlId: 'input-number',
                    label: 'Number Field',
                    icon: "sap-icon--number-sign",
                    description: 'Stepped number input',
                    template: `<div class="fb-control-wrapper" ng-click="showProps($event)" data-id="{{id}}"><fd-form-item horizontal="props.horizontal.value">
						<fd-form-label dg-colon="true" dg-required="props.required.value" for="{{props.id.value}}">{{ props.label.value }}</fd-form-label>
						<fd-step-input dg-id="{{props.id.value}}" name="{{props.id.value}}" ng-model="props.minNum.value" placeholder="{{props.placeholder.value}}" dg-required="props.required.value">
						</fd-step-input>
					</fd-form-item></div>`,
                    props: {
                        id: {
                            type: 'text',
                            label: 'ID',
                            placeholder: 'Form Item ID',
                            value: '',
                            required: true,
                        },
                        label: {
                            type: 'text',
                            label: 'Label',
                            value: 'Input',
                            placeholder: 'Input label',
                            required: false,
                        },
                        horizontal: {
                            type: 'checkbox',
                            label: 'Is horizontal',
                            value: false,
                        },
                        placeholder: {
                            type: 'text',
                            label: 'Placeholder',
                            value: '',
                            placeholder: 'Input placeholder',
                        },
                        model: {
                            type: 'text',
                            label: 'Model',
                            value: '',
                            placeholder: 'inputVar',
                        },
                        required: {
                            type: 'checkbox',
                            label: 'Is required',
                            value: true,
                        },
                        minNum: {
                            type: 'number',
                            label: 'Minimum number',
                            value: 0,
                            min: 0,
                            step: 1,
                            required: false,
                        },
                        maxNum: {
                            type: 'number',
                            label: 'Maximum number',
                            value: -1,
                            min: -1,
                            step: 1,
                            required: false,
                        },
                        step: {
                            type: 'number',
                            label: 'Step number',
                            value: 1,
                            step: 1,
                            required: false,
                        },
                    },
                },
                {
                    controlId: 'input-color',
                    label: 'Color',
                    icon: "sap-icon--color-fill",
                    description: 'Color input',
                    template: `<div class="fb-control-wrapper" ng-click="showProps($event)" data-id="{{id}}"><fd-form-item horizontal="props.horizontal.value">
                    <fd-form-label dg-colon="true" dg-required="props.required.value" for="{{props.id.value}}">
                        {{ props.label.value }}
                    </fd-form-label>
                    <fd-input id="{{props.id.value}}" name="{{props.id.value}}" ng-required="props.required.value" type="color" value="#ffbe6f">
	                </fd-input></fd-form-item></div>`,
                    props: {
                        id: {
                            type: 'text',
                            label: 'ID',
                            placeholder: 'Form Item ID',
                            value: '',
                            required: true,
                        },
                        label: {
                            type: 'text',
                            label: 'Label',
                            value: 'Input',
                            placeholder: 'Input label',
                            required: false,
                        },
                        horizontal: {
                            type: 'checkbox',
                            label: 'Is horizontal',
                            value: false,
                        },
                        model: {
                            type: 'text',
                            label: 'Model',
                            value: '',
                            placeholder: 'inputVar',
                        },
                        required: {
                            type: 'checkbox',
                            label: 'Is required',
                            value: true,
                        },
                    },
                },
                {
                    controlId: 'input-select',
                    label: 'Dropdown',
                    icon: "sap-icon--down",
                    description: 'Dropdown selection',
                    template: `<div class="fb-control-wrapper" ng-click="showProps($event)" data-id="{{id}}"><fd-form-item horizontal="props.horizontal.value">
                        <fd-form-label dg-colon="true" dg-required="props.required.value"
                            for="{{props.id.value}}">
                            {{ props.label.value }}
                        </fd-form-label>
                        <fd-select dg-placeholder="{{props.placeholder.value}}" label-id="{{ props.id.value }}"
                            ng-required="props.required.value" ng-model="props.dropdown.defaultValue" dropdown-fixed="true">
                            <fd-option text="{{ menuItem.label }}" value="menuItem.value"
                                ng-repeat="menuItem in props.dropdown.options track by $index">
                            </fd-option>
                        </fd-select>
                    </fd-form-item></div>`,
                    props: {
                        id: {
                            type: 'text',
                            label: 'ID',
                            placeholder: 'Form Item ID',
                            value: '',
                            required: true,
                        },
                        label: {
                            type: 'text',
                            label: 'Label',
                            value: 'Input',
                            placeholder: 'Input label',
                            required: false,
                        },
                        horizontal: {
                            type: 'checkbox',
                            label: 'Is horizontal',
                            value: false,
                        },
                        model: {
                            type: 'text',
                            label: 'Model',
                            value: '',
                            placeholder: '',
                        },
                        options: {
                            type: 'text',
                            label: 'Options',
                            value: '',
                            placeholder: '',
                        },
                        optionLabel: {
                            type: 'text',
                            label: 'Option label key',
                            value: 'label',
                            placeholder: 'label',
                        },
                        optionValue: {
                            type: 'text',
                            label: 'Option value key',
                            value: 'value',
                            placeholder: 'value',
                        },
                        // dropdown: {
                        //     type: 'list',
                        //     label: 'Options',
                        //     defaultValue: 'item1',
                        //     value: [
                        //         { label: 'Item 1', value: 'item1' },
                        //         { label: 'Item 2', value: 'item2' }
                        //     ]
                        // },
                        required: {
                            type: 'checkbox',
                            label: 'Is required',
                            value: true,
                        },
                    },
                },
                {
                    controlId: 'input-checkbox',
                    label: 'Checkbox',
                    icon: "sap-icon--complete",
                    description: 'description',
                    template: `<div class="fb-control-wrapper" ng-click="showProps($event)" data-id="{{id}}"><fd-form-item>
                        <fd-checkbox id="{{ props.id.value }}"></fd-checkbox>
                        <fd-checkbox-label for="{{ props.id.value }}">{{ props.label.value }}</fd-checkbox-label>
                    </fd-form-item></div>`,
                    props: {
                        id: {
                            type: 'text',
                            label: 'ID',
                            placeholder: 'Form Item ID',
                            value: '',
                            required: true
                        },
                        label: {
                            type: 'text',
                            label: 'Label',
                            value: 'Checkbox',
                            placeholder: '',
                            required: true
                        },
                        model: {
                            type: 'text',
                            label: 'Model',
                            value: '',
                            placeholder: 'modelVar',
                        },
                    },
                },
                {
                    controlId: 'input-radio',
                    label: 'Radio',
                    icon: "sap-icon--record",
                    description: 'Radio select',
                    template: `<div class="fb-control-wrapper" ng-click="showProps($event)" data-id="{{id}}"><fd-form-group dg-header="{{props.name.value}}">
                    <fd-form-item ng-repeat="option in props.options.value track by $index">
                        <fd-radio id="{{ props.id.value + $index }}" name="{{ props.id.value }}" ng-model="props.options.defaultValue" ng-value="option.value" ng-required="props.required.value"></fd-radio>
                        <fd-radio-label for="{{ props.id.value + $index }}">{{option.label}}</fd-radio-label>
                    </fd-form-item>
                    </fd-form-group></div>`,
                    // template: `<div class="fb-control-wrapper" ng-click="showProps($event)" data-id="{{id}}"><fd-form-group dg-header="{{props.name.value}}">
                    // <fd-form-item>
                    //     <fd-radio id="{{ props.id.value + 1 }}" name="{{ props.id.value }}" ng-model="props.model.value" ng-required="props.required.value"></fd-radio>
                    //     <fd-radio-label for="{{ props.id.value + 1 }}">Radio Option 1</fd-radio-label>
                    // </fd-form-item>
                    // <fd-form-item>
                    //     <fd-radio id="{{ props.id.value + 2 }}" name="{{ props.id.value }}" ng-model="props.model.value" ng-required="props.required.value"></fd-radio>
                    //     <fd-radio-label for="{{ props.id.value + 2 }}">Radio Option 2</fd-radio-label>
                    // </fd-form-item>
                    // </fd-form-group></div>`,
                    props: {
                        id: {
                            type: 'text',
                            label: 'Name',
                            placeholder: 'The name of the radio button(s)',
                            value: '',
                            required: true
                        },
                        name: {
                            type: 'text',
                            label: 'Group title',
                            value: 'Radio group',
                            placeholder: '',
                        },
                        model: {
                            type: 'text',
                            label: 'Model',
                            value: '',
                            placeholder: 'selectedOptionValue',
                        },
                        // options: {
                        //     type: 'text',
                        //     label: 'Options array name',
                        //     value: '',
                        //     placeholder: 'radioOptions',
                        // },
                        // optionLabel: {
                        //     type: 'text',
                        //     label: 'Option label key',
                        //     value: 'label',
                        //     placeholder: 'label',
                        // },
                        // optionValue: {
                        //     type: 'text',
                        //     label: 'Option value key',
                        //     value: 'value',
                        //     placeholder: 'value',
                        // },
                        options: {
                            type: 'list',
                            label: 'Options',
                            defaultValue: '',
                            value: [
                                { label: "Item 1", value: "item1" },
                                { label: "Item 2", value: "item2" }
                            ]
                        },
                        required: {
                            type: 'checkbox',
                            label: 'Is required',
                            value: true,
                        },
                    },
                },
                {
                    controlId: 'paragraph',
                    label: 'Paragraph',
                    icon: "sap-icon--text-align-left",
                    description: 'Paragraph',
                    template: `<div class="fb-control-wrapper" ng-click="showProps($event)" data-id="{{id}}"><p class="fd-text" ng-class="{'dg-pre-wrap' : props.format.value}">{{props.text.value}}</p></div>`,
                    props: {
                        format: {
                            type: 'checkbox',
                            label: 'Preserve formatting',
                            value: true,
                        },
                        text: {
                            type: 'textarea',
                            label: 'Text',
                            value: `Multiline\nParagraph\nText`,
                        },
                        model: {
                            type: 'text',
                            label: 'Model',
                            value: '',
                            placeholder: '',
                        },
                    },
                },
            ]
        },
        {
            id: 'fb-containers',
            label: 'Containers',
            items: [
                {
                    controlId: 'container-vbox',
                    label: 'Vertical Box',
                    icon: 'sap-icon--screen-split-two',
                    iconRotate: true,
                    description: 'Vertical box container',
                    template: `<div id="{{id}}" class="dg-vbox" data-type="container" ng-click="showActions(id, $event)"></div>`,
                    children: []
                },
                {
                    controlId: 'container-hbox',
                    label: 'Horizintal Box',
                    icon: 'sap-icon--screen-split-two',
                    description: 'Horizintal box container',
                    template: `<div id="{{id}}" class="dg-hbox" data-type="container" ng-click="showActions(id, $event)"></div>`,
                    children: []
                },
            ]
        }
    ];

    $scope.contextMenuContent = function (element) {
        let items = [];
        const type = element.getAttribute('data-type');
        if (type && type === 'container-main') {
            items.push({
                id: "clearForm",
                label: "Clear form",
            });
        } else {
            const isContainer = (type && type === 'container');
            let controlId;
            if (isContainer) controlId = element.id;
            else controlId = element.getAttribute('data-id')
            items.push({
                id: "deleteControl",
                label: "Delete",
                data: {
                    isContainer: isContainer,
                    controlId: controlId,
                }
            });
        }
        if (items.length) {
            return {
                callbackTopic: "formEditor.contextmenu",
                items: items
            }
        } else return;
    };

    $scope.insertInModel = function (model, control, containerId, index) {
        for (let i = 0; i < model.length; i++) {
            if (model[i].controlId.startsWith('container')) {
                if (model[i].$scope.id === containerId) {
                    if (index) model[i].children.splice(index, 0, control);
                    else model[i].children.push(control);
                    return true;
                } else {
                    if ($scope.insertInModel(model[i].children, control, containerId, index)) break;
                }
            }
        }
    };

    $scope.moveInModel = function (model, control, containerId, insertIndex) {
        if (containerId === 'formContainer') {
            model.splice(insertIndex, 0, control);
            return true;
        }
        for (let i = 0; i < model.length; i++) {
            if (model[i].controlId.startsWith('container')) {
                if (model[i].$scope.id === containerId) {
                    model[i].children.splice(insertIndex, 0, control);
                    return true;
                } else {
                    if ($scope.moveInModel(model[i].children, control, containerId, insertIndex)) break;
                }
            }
        }
    };

    $scope.popFromModel = function (model, controlId) {
        if (controlId) {
            for (let i = 0; i < model.length; i++) {
                if (model[i].$scope.id === controlId) {
                    return model.splice(i, 1)[0];
                } else if (model[i].controlId.startsWith('container')) {
                    const control = $scope.popFromModel(model[i].children, controlId);
                    if (control) return control;
                }
            }
        }
        return;
    };

    function addFormItem(event) {
        const parentIndex = event.item.getAttribute('data-pindex');
        const controlIndex = event.item.getAttribute('data-cindex');
        if (event.from.getAttribute('data-type') === 'componentPanel') {
            const control = JSON.parse(JSON.stringify($scope.builderComponents[parentIndex].items[controlIndex]));
            control.$scope = $scope.$new(true);
            control.$scope.props = control.props;
            if (event.to.id !== 'formContainer') {
                $scope.insertInModel($scope.formModel, control, event.to.id, event.newIndex);
            } else {
                $scope.formModel.splice(event.newIndex, 0, control);
            }
            let isContainer = false;
            if (control.controlId.startsWith('container')) {
                isContainer = true;
                control.$scope.id = `c${uuid.generate()}`;
                control.$scope.showActions = $scope.showActions;
            } else {
                control.$scope.id = `w${uuid.generate()}`;
                if (control.$scope.props.id)
                    control.$scope.props.id.value = `i${uuid.generate()}`;
                control.$scope.showProps = $scope.showProps;
            }
            const element = $compile(control.template)(control.$scope)[0];
            $(event.item).replaceWith(element);
            if (isContainer) createSublist(element, control.$scope.id);
        } else {
            const control = $scope.popFromModel($scope.formModel, event.item.getAttribute('data-id'));
            if (control) {
                $scope.moveInModel($scope.formModel, control, event.to.id, event.newIndex);
            } else {
                messageHub.showAlertError('Move error', 'There was an error while attempting to move the control in the data model. Control was not found.');
            }
        }
        $scope.$apply(() => $scope.fileChanged())
    }

    function createSublist(element, groupId) {
        Sortable.create(element, {
            group: {
                name: groupId,
                put: true
            },
            animation: 200,
            onAdd: addFormItem,
            onUpdate: addFormItem,
        });
    }

    function removeSelection() {
        const control = $document[0].querySelector(`div.fb-control-wrapper[data-id=${$scope.selectedCtrlId}]`);
        control.classList.remove('is-selected');
    }

    $scope.initControlGroup = function (gid) {
        Sortable.create($document[0].getElementById(gid), {
            sort: false,
            group: {
                name: gid,
                pull: 'clone',
                put: false,
            },
            animation: 200
        });
    };

    $scope.togglePreview = function () {
        if ($scope.selectedCtrlId || $scope.selectedContainerId) {
            removeSelection();
            $scope.selectedCtrlId = undefined;
            $scope.selectedContainerId = undefined;
        }
        $scope.state.preview = !$scope.state.preview;
    };

    $scope.showActions = function (controlId, event) {
        if ($scope.state.canSave) {
            if (event.target.id && event.target.id === controlId) {
                event.stopPropagation();
                $scope.selectedCtrlId = undefined;
                $scope.selectedContainerId = controlId;
            } else $scope.selectedContainerId = undefined;
        }
    };

    $scope.showProps = function (event) {
        if ($scope.state.canSave) {
            $scope.selectedContainerId = undefined;
            if ($scope.selectedCtrlId) {
                removeSelection();
            }
            event.target.classList.add('is-selected');
            $scope.selectedCtrlId = event.target.getAttribute('data-id');
        }
    };

    $scope.getProps = function (model, controlId) {
        if (controlId) {
            for (let i = 0; i < model.length; i++) {
                if (model[i].controlId.startsWith('container')) {
                    const props = $scope.getProps(model[i].children, controlId);
                    if (props) return props;
                } else if (model[i].$scope.id === controlId) {
                    return model[i].$scope.props;
                }
            }
        }
        return;
    };

    $scope.addListItem = function (options) {
        messageHub.showFormDialog(
            "formEditorAddListItem",
            "Add item",
            [
                {
                    id: 'aliLabel',
                    type: 'input',
                    label: 'Label',
                    minlength: 1,
                    required: true,
                    placeholder: '',
                    value: '',
                },
                {
                    id: 'aliValue',
                    type: 'input',
                    label: 'Value',
                    inputRules: {
                        patterns: ['^\\S*$'],
                    },
                    minlength: 1,
                    required: true,
                    placeholder: '',
                    value: '',
                }
            ],
            [{
                id: "b1",
                type: "emphasized",
                label: "Add",
                whenValid: true
            },
            {
                id: "b2",
                type: "transparent",
                label: "Cancel",
            }],
            "formEditor.dialogs.addListItem",
            "Adding..."
        );

        const handler = messageHub.onDidReceiveMessage(
            'dialogs.addListItem',
            function (msg) {
                if (msg.data.buttonId === "b1") {
                    $scope.$apply(function () {
                        options.push({
                            label: msg.data.formData[0].value,
                            value: msg.data.formData[1].value
                        });
                        $scope.fileChanged();
                    });
                }
                messageHub.hideFormDialog('formEditorAddListItem');
                messageHub.unsubscribe(handler);
            }
        );
    };

    $scope.editListItem = function (listItem) {
        messageHub.showFormDialog(
            "formEditorEditListItem",
            "Edit item",
            [
                {
                    id: 'eliLabel',
                    type: 'input',
                    label: 'Label',
                    minlength: 1,
                    required: true,
                    placeholder: '',
                    value: listItem.label,
                },
                {
                    id: 'eliValue',
                    type: 'input',
                    label: 'Value',
                    inputRules: {
                        patterns: ['^\\S*$'],
                    },
                    minlength: 1,
                    required: true,
                    placeholder: '',
                    value: listItem.value,
                }
            ],
            [{
                id: "b1",
                type: "emphasized",
                label: "Save",
                whenValid: true
            },
            {
                id: "b2",
                type: "transparent",
                label: "Cancel",
            }],
            "formEditor.dialogs.editListItem",
            "Updating..."
        );

        const handler = messageHub.onDidReceiveMessage(
            'dialogs.editListItem',
            function (msg) {
                if (msg.data.buttonId === "b1") {
                    $scope.$apply(function () {
                        listItem.label = msg.data.formData[0].value;
                        listItem.value = msg.data.formData[1].value;
                    });
                    $scope.fileChanged();
                }
                messageHub.hideFormDialog('formEditorEditListItem');
                messageHub.unsubscribe(handler);
            }
        );
    };

    $scope.deleteListItem = function (options, index) {
        options.splice(index, 1);
        $scope.fileChanged();
    };

    $scope.addFeed = function () {
        messageHub.showFormDialog(
            "formEditorAddFeed",
            "Add feed",
            [
                {
                    id: 'afName',
                    type: 'input',
                    label: 'Name',
                    minlength: 1,
                    required: true,
                    placeholder: '',
                    value: '',
                },
                {
                    id: 'afUrl',
                    type: 'input',
                    label: 'URL',
                    minlength: 1,
                    required: true,
                    placeholder: '',
                    value: '',
                }
            ],
            [{
                id: "b1",
                type: "emphasized",
                label: "Add",
                whenValid: true
            },
            {
                id: "b2",
                type: "transparent",
                label: "Cancel",
            }],
            "formEditor.dialogs.addFeed",
            "Adding..."
        );

        const handler = messageHub.onDidReceiveMessage(
            'dialogs.addFeed',
            function (msg) {
                if (msg.data.buttonId === "b1") {
                    $scope.$apply(function () {
                        $scope.formData.feeds.push({
                            name: msg.data.formData[0].value,
                            url: msg.data.formData[1].value
                        });
                        $scope.fileChanged();
                    });
                }
                messageHub.hideFormDialog('formEditorAddFeed');
                messageHub.unsubscribe(handler);
            }
        );
    };

    $scope.editFeed = function (feed) {
        messageHub.showFormDialog(
            "formEditorEditFeed",
            "Edit item",
            [
                {
                    id: 'afName',
                    type: 'input',
                    label: 'Name',
                    minlength: 1,
                    required: true,
                    placeholder: '',
                    value: feed.name,
                },
                {
                    id: 'afUrl',
                    type: 'input',
                    label: 'URL',
                    minlength: 1,
                    required: true,
                    placeholder: '',
                    value: feed.url,
                }
            ],
            [{
                id: "b1",
                type: "emphasized",
                label: "Save",
                whenValid: true
            },
            {
                id: "b2",
                type: "transparent",
                label: "Cancel",
            }],
            "formEditor.dialogs.editFeed",
            "Updating..."
        );

        const handler = messageHub.onDidReceiveMessage(
            'dialogs.editFeed',
            function (msg) {
                if (msg.data.buttonId === "b1") {
                    $scope.$apply(function () {
                        feed.name = msg.data.formData[0].value;
                        feed.url = msg.data.formData[1].value;
                        $scope.fileChanged();
                    });
                }
                messageHub.hideFormDialog('formEditorEditFeed');
                messageHub.unsubscribe(handler);
            }
        );
    };

    $scope.deleteFeed = function (index) {
        $scope.formData.feeds.splice(index, 1);
    };

    $scope.addScript = function () {
        messageHub.showFormDialog(
            "formEditorAddScript",
            "Add script",
            [
                {
                    id: 'asName',
                    type: 'input',
                    label: 'Name',
                    minlength: 1,
                    required: true,
                    placeholder: '',
                    value: '',
                },
                {
                    id: 'asUrl',
                    type: 'input',
                    label: 'URL',
                    minlength: 1,
                    required: true,
                    placeholder: '',
                    value: '',
                }
            ],
            [{
                id: "b1",
                type: "emphasized",
                label: "Add",
                whenValid: true
            },
            {
                id: "b2",
                type: "transparent",
                label: "Cancel",
            }],
            "formEditor.dialogs.addScript",
            "Adding..."
        );

        const handler = messageHub.onDidReceiveMessage(
            'dialogs.addScript',
            function (msg) {
                if (msg.data.buttonId === "b1") {
                    $scope.$apply(function () {
                        $scope.formData.scripts.push({
                            name: msg.data.formData[0].value,
                            url: msg.data.formData[1].value
                        });
                        $scope.fileChanged();
                    });
                }
                messageHub.hideFormDialog('formEditorAddScript');
                messageHub.unsubscribe(handler);
            }
        );
    };

    $scope.editScript = function (script) {
        messageHub.showFormDialog(
            "formEditorEditScript",
            "Edit script",
            [
                {
                    id: 'asName',
                    type: 'input',
                    label: 'Name',
                    minlength: 1,
                    required: true,
                    placeholder: '',
                    value: script.name,
                },
                {
                    id: 'asUrl',
                    type: 'input',
                    label: 'URL',
                    minlength: 1,
                    required: true,
                    placeholder: '',
                    value: script.url,
                }
            ],
            [{
                id: "b1",
                type: "emphasized",
                label: "Save",
                whenValid: true
            },
            {
                id: "b2",
                type: "transparent",
                label: "Cancel",
            }],
            "formEditor.dialogs.editScript",
            "Updating..."
        );

        const handler = messageHub.onDidReceiveMessage(
            'dialogs.editScript',
            function (msg) {
                if (msg.data.buttonId === "b1") {
                    $scope.$apply(function () {
                        script.name = msg.data.formData[0].value;
                        script.url = msg.data.formData[1].value;
                        $scope.fileChanged();
                    });
                }
                messageHub.hideFormDialog('formEditorEditScript');
                messageHub.unsubscribe(handler);
            }
        );
    };

    $scope.deleteScript = function (index) {
        $scope.formData.scripts.splice(index, 1);
    };

    $scope.isFormValid = function () {
        if ($scope.forms.formProperties) {
            if ($scope.forms.formProperties.$valid === true || $scope.forms.formProperties.$valid === undefined) {
                $scope.state.canSave = true;
                return true;
            } else {
                $scope.state.canSave = false;
                return false;
            }
        }
        $scope.state.canSave = true;
        return true;
    };

    $scope.switchTab = function (tabId) {
        $scope.selectedTab = tabId;
    };

    $scope.createDomFromJson = function (model, containerId) {
        for (let i = 0; i < model.length; i++) {
            let control;
            if (model[i].controlId.startsWith('container')) {
                for (let c = 0; c < $scope.builderComponents[1].items.length; c++) {
                    if ($scope.builderComponents[1].items[c].controlId === model[i].controlId) {
                        control = JSON.parse(JSON.stringify($scope.builderComponents[1].items[c]));
                        control.$scope = $scope.$new(true);
                        control.$scope.id = `c${uuid.generate()}`;
                        control.$scope.showActions = $scope.showActions;
                        for (const key in control.$scope.props) {
                            control.$scope.props[key].value = model[i][key];
                        }
                        break;
                    }
                }
            } else {
                for (let c = 0; c < $scope.builderComponents[0].items.length; c++) {
                    if ($scope.builderComponents[0].items[c].controlId === model[i].controlId) {
                        control = JSON.parse(JSON.stringify($scope.builderComponents[0].items[c]));
                        control.$scope = $scope.$new(true);
                        control.$scope.id = `w${uuid.generate()}`;
                        control.$scope.showProps = $scope.showProps;
                        control.$scope.props = control.props;
                        for (const key in control.$scope.props) {
                            control.$scope.props[key].value = model[i][key];
                        }
                        break;
                    }
                }
            }
            $timeout(function () {
                if (containerId !== undefined) {
                    $scope.insertInModel($scope.formModel, control, containerId);
                    const element = angular.element($document[0].querySelector(`#${containerId}`));
                    element.append($compile(control.template)(control.$scope)[0]);
                    element.ready(function () {
                        createSublist(document.querySelector(`#${containerId}`), containerId);
                    });
                } else {
                    $scope.formModel.push(control);
                    angular.element($document[0].querySelector(`#formContainer`)).append($compile(control.template)(control.$scope)[0]);
                }
                if (control.controlId.startsWith('container') && model[i].children.length > 0) {
                    $scope.createDomFromJson(model[i].children, control.$scope.id);
                }
            }, 0);
        }
    };

    $scope.loadFileContents = function () {
        workspaceApi.loadContent('', $scope.dataParameters.file).then(function (response) {
            if (response.status === 200) {
                if (response.data.hasOwnProperty('feeds')) {
                    $scope.formData.feeds = response.data.feeds;
                }
                if (response.data.hasOwnProperty('code')) {
                    $scope.formData.code = response.data.code;
                }
                if (response.data.hasOwnProperty('scripts')) {
                    $scope.formData.scripts = response.data.scripts;
                }
                if (response.data.hasOwnProperty('form')) {
                    $scope.createDomFromJson(response.data.form);
                }
                $scope.$apply(function () {
                    $scope.state.isBusy = false;
                });
            } else {
                $scope.$apply(function () {
                    $scope.state.error = true;
                    $scope.errorMessage = "There was a problem with loading the file";
                    $scope.state.isBusy = false;
                });
            }
        });
    };

    $scope.createFormJson = function (model) {
        let formJson = [];
        for (let i = 0; i < model.length; i++) {
            if (model[i].controlId.startsWith('container')) {
                formJson.push({
                    controlId: model[i].controlId,
                    children: $scope.createFormJson(model[i].children)
                });
            } else {
                const controlObj = {
                    controlId: model[i].controlId,
                };
                for (const key in model[i].$scope.props) {
                    //@ts-ignore
                    controlObj[key] = model[i].$scope.props[key].value;
                }
                formJson.push(controlObj);
            }
        }
        return formJson;
    };

    function saveContents(text) {
        workspaceApi.saveContent('', $scope.dataParameters.file, text).then(function (response) {
            if (response.status === 200) {
                messageHub.announceFileSaved({
                    name: $scope.dataParameters.file.substring($scope.dataParameters.file.lastIndexOf('/') + 1),
                    path: $scope.dataParameters.file.substring($scope.dataParameters.file.indexOf('/', 1)),
                    contentType: $scope.dataParameters.contentType,
                    workspace: $scope.dataParameters.file.substring(1, $scope.dataParameters.file.indexOf('/', 1)),
                });
                messageHub.setStatusMessage(`File '${$scope.dataParameters.file}' saved`);
                messageHub.setEditorDirty($scope.dataParameters.file, false);
                $scope.$apply(function () {
                    $scope.state.isBusy = false;
                    $scope.isFileChanged = false;
                });
            } else {
                console.error(`Error saving '${$scope.dataParameters.file}'`);
                messageHub.setStatusError(`Error saving '${$scope.dataParameters.file}'`);
                messageHub.showAlertError('Error while saving the file', 'Please look at the console for more information');
                $scope.$apply(function () {
                    $scope.state.isBusy = false;
                });
            }
        });
    }

    $scope.save = function () {
        if ($scope.state.canSave && $scope.isFileChanged) {
            $scope.state.isBusy = true;
            const formFile = {
                feeds: $scope.formData.feeds,
                scripts: $scope.formData.scripts,
                code: $scope.formData.code,
                form: $scope.createFormJson($scope.formModel)
            };
            saveContents(JSON.stringify(formFile));
        }
    };

    $scope.fileChanged = function () {
        if (!$scope.isFileChanged) {
            $scope.isFileChanged = true;
            messageHub.setEditorDirty($scope.dataParameters.file, $scope.isFileChanged);
        }
    };

    $scope.deleteControlFromModel = function (id, model) {
        for (let i = 0; i < model.length; i++) {
            if (model[i].controlId.startsWith('container') && model[i].$scope.id !== id) {
                if ($scope.deleteControlFromModel(id, model[i].children)) return true;
            } else if (model[i].$scope.id === id) {
                model.splice(i, 1);
                return true;
            }
        }
        return false;
    };

    $scope.deleteControl = function (id, isContainer = false) {
        if (!id) {
            messageHub.showAlertError('Delete error', 'Received an empty ID');
        } else if (!$scope.deleteControlFromModel(id, $scope.formModel)) {
            console.error(`Could not delete control with internal ID '${id}'`);
            messageHub.showAlertError('Delete error', `Could not delete control from model with internal ID '${id}'`);
        } else {
            $scope.fileChanged();
            $scope.selectedCtrlId = undefined;
            $scope.selectedContainerId = undefined;
            let control;
            if (isContainer) control = $document[0].querySelector(`#${id}`);
            else control = $document[0].querySelector(`[data-id=${id}]`);
            if (control) {
                angular.element(control).remove();
            } else {
                messageHub.showAlertError('Delete error', `Could not delete control from UI with internal ID '${id}'`);
            }
        }
    };

    $scope.deleteSelected = function () {
        if ($scope.selectedCtrlId) {
            $scope.deleteControl($scope.selectedCtrlId);
        } else if ($scope.selectedContainerId) {
            $scope.deleteControl($scope.selectedContainerId, true);
        }
    }

    messageHub.onDidReceiveMessage(
        'contextmenu',
        function (msg) {
            if (msg.data.itemId === 'clearForm') {
                const control = $document[0].querySelector('#formContainer');
                control.textContent = '';
                $scope.$apply(function () {
                    $scope.formModel.length = 0;
                    $scope.fileChanged();
                });
            } else if (msg.data.itemId === 'deleteControl') {
                if (msg.data.data.isContainer) $scope.deleteControl(msg.data.data.controlId, true);
                else $scope.deleteControl(msg.data.data.controlId);
            }
        }
    );

    messageHub.onEditorFocusGain(function (msg) {
        if (msg.resourcePath === $scope.dataParameters.file) messageHub.setStatusCaret('');
    });

    messageHub.onEditorReloadParameters(
        function (event) {
            $scope.$apply(() => {
                if (event.resourcePath === $scope.dataParameters.file) {
                    $scope.dataParameters = ViewParameters.get();
                    // $scope.loadFileContents(); // TODO: Make dynamic data reload possible
                }
            });
        }
    );

    $scope.dataParameters = ViewParameters.get();
    if (!$scope.dataParameters.hasOwnProperty('file')) {
        $scope.state.error = true;
        $scope.errorMessage = "The 'file' data parameter is missing.";
    } else {
        angular.element($document[0]).ready(function () {
            $scope.$apply(function () {
                $scope.loadFileContents();
            });
            const formContainer = $document[0].getElementById("formContainer");
            Sortable.create(formContainer, {
                group: {
                    name: 'formContainer',
                    put: true
                },
                animation: 200,
                onAdd: addFormItem,
                onUpdate: addFormItem,
            });
        });
    }
}]);