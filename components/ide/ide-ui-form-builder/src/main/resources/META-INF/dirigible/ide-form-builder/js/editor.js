/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2024 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
const editorView = angular.module('app', ['ideUI', 'ideView', 'ideWorkspace', 'codeEditor']);

editorView.controller('DesignerController', ['$scope', '$window', '$document', '$timeout', '$compile', 'uuid', 'messageHub', 'ViewParameters', 'workspaceApi', function ($scope, $window, $document, $timeout, $compile, uuid, messageHub, ViewParameters, workspaceApi) {
    let csrfToken;
    $scope.selectedTab = 'designer';
    $scope.isFileChanged = false;
    $scope.state = {
        isBusy: true,
        error: false,
        busyText: "Loading...",
        canSave: true,
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

    $scope.builderComponents = {
        'Controls': [
            {
                type: 'header',
                label: 'Header',
                icon: "sap-icon--heading-1",
                description: 'Text header',
                template: `<h1 fd-title header-size="props.size.value">{{props.title.value}}</h1>`,
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
                type: 'button',
                label: 'Button',
                icon: 'sap-icon--border',
                description: 'Button',
                template: `<fd-form-group class="dg-float-right"><fd-button type="{{props.isSubmit.value ? 'submit' : 'button'}}" dg-label="{{props.label.value}}" dg-type="{{props.type.value}}" ng-click="submit()"></fd-button></fd-form-group>`,
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
                    isSubmit: {
                        type: 'checkbox',
                        label: 'Submits form',
                        value: true,
                    },
                    callback: {
                        type: 'text',
                        label: 'Callback function name',
                        value: '',
                        placeholder: 'callback',
                    },
                }
            },
            {
                type: 'input-textfield',
                label: 'Text Field',
                icon: "sap-icon--edit",
                description: 'Input field',
                template: `<fd-form-item horizontal="props.horizontal.value">
                    <fd-form-label dg-colon="true" dg-required="props.required.value" for="{{props.id.value}}">
                        {{ props.label.value }}
                    </fd-form-label>
                    <fd-form-input-message-group dg-inactive="{{props.errorState.invalid}}">
                        <fd-input id="{{props.id.value}}" type="text" placeholder="{{props.placeholder.value}}"
                            state="{'error' : props.errorState.invalid }" name="{{props.id.value}}" ng-required="props.required.value"
                            ng-model="scopeVars.model" ng-trim="false" ng-minlength="props.minLength.value || 0"
                            ng-maxlength="props.maxLength.value || -1" dg-input-rules="props.validationRegex.value ? { patterns: [props.validationRegex.value] } : {}"
                            ng-change="props.errorState.invalid = !formModel[props.id.value].$valid">
                        </fd-input>
                        <fd-form-message dg-type="error">{{props.errorState.value || 'Incorrect input'}}</fd-form-message>
                    </fd-form-input-message-group>
                </fd-form-item>`,
                props: {
                    id: {
                        type: 'text',
                        label: 'Form item ID',
                        placeholder: 'The id of the input used inside the form',
                        value: '',
                        required: true,
                    },
                    label: {
                        type: 'text',
                        label: 'Label',
                        value: 'Input',
                        placeholder: 'Input label',
                        required: true,
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
                type: 'input-textarea',
                label: 'Text Area',
                icon: "sap-icon--edit",
                description: 'Text Area',
                template: `<fd-form-item horizontal="props.horizontal.value">
                    <fd-form-label dg-colon="true" dg-required="props.required.value" for="{{props.id.value}}">
                        {{ props.label.value }}
                    </fd-form-label>
                    <fd-form-input-message-group dg-inactive="{{props.errorState.invalid}}">
                        <fd-textarea id="{{props.id.value}}" type="text" placeholder="{{props.placeholder.value}}"
                            state="{'error' : props.errorState.invalid }" name="{{props.id.value}}" ng-required="props.required.value"
                            ng-model="scopeVars.model" ng-trim="false" ng-minlength="props.minLength.value || 0"
                            ng-maxlength="props.maxLength.value || -1" dg-input-rules="props.validationRegex.value ? { patterns: [props.validationRegex.value] } : {}"
                            ng-change="props.errorState.invalid = !formModel[props.id.value].$valid">
                        </fd-textarea>
                        <fd-form-message dg-type="error">{{props.errorState.value || 'Incorrect input'}}</fd-form-message>
                    </fd-form-input-message-group>
                </fd-form-item>`,
                props: {
                    id: {
                        type: 'text',
                        label: 'Form item ID',
                        placeholder: 'The id of the input used inside the form',
                        value: '',
                        required: true,
                    },
                    label: {
                        type: 'text',
                        label: 'Label',
                        value: 'Input',
                        placeholder: 'Input label',
                        required: true,
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
                type: 'input-select',
                label: 'Dropdown',
                icon: "sap-icon--down",
                description: 'Dropdown selection',
                template: `<fd-form-item horizontal="props.horizontal.value">
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
                </fd-form-item>`,
                props: {
                    id: {
                        type: 'text',
                        label: 'Form item ID',
                        placeholder: 'The id of the dropdown used inside the form',
                        value: '',
                        required: true,
                    },
                    label: {
                        type: 'text',
                        label: 'Label',
                        value: 'Input',
                        placeholder: 'Input label',
                        required: true,
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
                    //     options: [
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
                type: 'input-checkbox',
                label: 'Checkbox',
                icon: "sap-icon--complete",
                description: 'description',
                template: `<fd-form-item>
                    <fd-checkbox id="{{ props.id.value }}" ng-model="scopeVars.model">
                    </fd-checkbox>
                    <fd-checkbox-label for="{{ props.id.value }}">{{ props.label.value }}
                    </fd-checkbox-label>
                </fd-form-item>`,
                props: {
                    id: {
                        type: 'text',
                        label: 'Form item ID',
                        placeholder: 'The id of the input used inside the form',
                        value: '',
                        required: true
                    },
                    label: {
                        type: 'text',
                        label: 'Label',
                        value: 'Input',
                        placeholder: 'Input label',
                        required: true
                    },
                    model: {
                        type: 'text',
                        label: 'Model',
                        value: '',
                        placeholder: '',
                    },
                },
            },
            {
                type: 'input-radio',
                label: 'Radio',
                icon: "sap-icon--record",
                description: 'Radio select',
                template: `<fd-form-item ng-repeat="option in props.options.options track by $index">
                    <fd-radio id="{{ props.id.value + $index }}" name="{{ props.id.value }}" ng-model="props.options.defaultValue" ng-value="option.value" ng-required="props.required.value"></fd-radio>
                    <fd-radio-label for="{{ props.id.value + $index }}">{{option.label}}</fd-radio-label>
                </fd-form-item>`,
                props: {
                    id: {
                        type: 'text',
                        label: 'Radio group name',
                        placeholder: 'The name of the radio button(s)',
                        value: '',
                        required: true
                    },
                    label: {
                        type: 'text',
                        label: 'Label',
                        value: 'Input',
                        placeholder: 'Input label',
                        required: true
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
                    // options: {
                    //     type: 'list',
                    //     label: 'Options',
                    //     defaultValue: '',
                    //     options: [
                    //         { label: "Item 1", value: "item1" },
                    //         { label: "Item 2", value: "item2" }
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
                type: 'paragraph',
                label: 'Paragraph',
                icon: "sap-icon--text-align-left",
                description: 'Paragraph',
                template: `<p class="fd-text" ng-class="{'dg-pre-wrap' : props.format.value}">{{props.text.value}}</p>`,
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
                        required: true
                    },
                    model: {
                        type: 'text',
                        label: 'Model',
                        value: '',
                        placeholder: '',
                    },
                },
            },
        ],
        'Containers': [
            {
                type: 'container-vbox',
                label: 'Vertical Box',
                icon: 'sap-icon--screen-split-two',
                iconRotate: true,
                description: 'Vertical box container',
                template: `<div id="{{id}}" class="dg-vbox" data-type="container" ng-click="showActions(id, $event)"></div>`,
                children: []
            },
            {
                type: 'container-hbox',
                label: 'Horizintal Box',
                icon: 'sap-icon--screen-split-two',
                description: 'Horizintal box container',
                template: `<div id="{{id}}" class="dg-hbox" data-type="container" ng-click="showActions(id, $event)"></div>`,
                children: []
            },
        ]
    };

    $scope.allowDrop = function (event) {
        event.preventDefault();
    };

    $scope.drag = function (event, control) {
        event.originalEvent.dataTransfer.setData('text/plain', JSON.stringify(control));
    };

    $scope.insertInModel = function (model, control, containerId) {
        for (let i = 0; i < model.length; i++) {
            if (model[i].type.startsWith('container')) {
                if (model[i].$scope.id === containerId) {
                    model[i].children.push(control);
                    return true;
                } else {
                    if ($scope.insertInModel(model[i].children, control, containerId)) break;
                }
            }
        }
    };

    $scope.drop = function (event) {
        event.stopPropagation();
        $scope.selectedCtrlId = undefined;
        $scope.selectedContainerId = undefined;
        const control = JSON.parse(event.originalEvent.dataTransfer.getData('text/plain'));
        control.$scope = $scope.$new(true);
        control.$scope.id = `c${uuid.generate()}`;
        control.$scope.props = control.props;
        if (control.type.startsWith('input')) {
            control.$scope.props.id.value = `i${uuid.generate()}`;
        }
        if (control.type.startsWith('container')) {
            control.$scope.showActions = $scope.showActions;
        } else {
            control.$scope.showProps = $scope.showProps;
            control.template = `<div id="{{id}}" class="fb-control-wrapper" data-type="wrapper" tabindex="0" ng-click="showProps(id)">${control.template}</div>`;
        }
        if (event.target.getAttribute('data-type') === 'container') {
            const containerId = event.target.getAttribute('id');
            $scope.insertInModel($scope.formModel, control, containerId);
            event.target.appendChild($compile(control.template)(control.$scope)[0]);
        } else if (event.target.getAttribute('data-type') === 'wrapper') {
            const parentId = event.target.parentElement.getAttribute('id');
            if (parentId) {
                $scope.insertInModel($scope.formModel, control, parentId);
            } else {
                $scope.formModel.push(control);
            }
            event.target.parentElement.appendChild($compile(control.template)(control.$scope)[0]);
        } else {
            $scope.formModel.push(control);
            event.target.appendChild($compile(control.template)(control.$scope)[0]);
        }
        $scope.fileChanged();
    };

    $scope.createDomFromJson = function (model, containerId) {
        for (let i = 0; i < model.length; i++) {
            let control;
            if (model[i].controlType.startsWith('container')) {
                for (let c = 0; c < $scope.builderComponents.Containers.length; c++) {
                    if ($scope.builderComponents.Containers[c].type === model[i].controlType) {
                        control = JSON.parse(JSON.stringify($scope.builderComponents.Containers[c]));
                        control.$scope = $scope.$new(true);
                        control.$scope.id = `c${uuid.generate()}`;
                        control.$scope.showActions = $scope.showActions;
                        break;
                    }
                }
            } else {
                for (let c = 0; c < $scope.builderComponents.Controls.length; c++) {
                    if ($scope.builderComponents.Controls[c].type === model[i].controlType) {
                        control = JSON.parse(JSON.stringify($scope.builderComponents.Controls[c]));
                        control.$scope = $scope.$new(true);
                        control.$scope.id = `c${uuid.generate()}`;
                        control.$scope.showProps = $scope.showProps;
                        control.template = `<div id="{{id}}" class="fb-control-wrapper" data-type="wrapper" tabindex="0" ng-click="showProps(id)">${control.template}</div>`;
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
                    angular.element($document[0].querySelector(`#${containerId}`)).append($compile(control.template)(control.$scope)[0]);
                } else {
                    $scope.formModel.push(control);
                    angular.element($document[0].querySelector(`#formContainer`)).append($compile(control.template)(control.$scope)[0]);
                }
                if (control.type.startsWith('container') && model[i].children.length > 0) {
                    $scope.createDomFromJson(model[i].children, control.$scope.id);
                }
            }, 0);
        }
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

    $scope.showProps = function (controlId) {
        if ($scope.state.canSave) {
            $scope.selectedCtrlId = controlId;
        }
    };

    $scope.getProps = function (model, controlId) {
        if (controlId) {
            for (let i = 0; i < model.length; i++) {
                if (model[i].type.startsWith('container')) {
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
            'formEditor.dialogs.addListItem',
            function (msg) {
                if (msg.data.buttonId === "b1") {
                    $scope.$apply(function () {
                        $scope.fileChanged();
                        options.push({
                            label: msg.data.formData[0].value,
                            value: msg.data.formData[1].value
                        });
                    });
                }
                messageHub.hideFormDialog('formEditorAddListItem');
                messageHub.unsubscribe(handler);
            },
            true
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
            'formEditor.dialogs.editListItem',
            function (msg) {
                if (msg.data.buttonId === "b1") {
                    $scope.$apply(function () {
                        $scope.fileChanged();
                        listItem.label = msg.data.formData[0].value;
                        listItem.value = msg.data.formData[1].value;
                    });
                }
                messageHub.hideFormDialog('formEditorEditListItem');
                messageHub.unsubscribe(handler);
            },
            true
        );
    };

    $scope.deleteListItem = function (options, index) {
        options.splice(index, 1);
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
            'formEditor.dialogs.addFeed',
            function (msg) {
                if (msg.data.buttonId === "b1") {
                    $scope.$apply(function () {
                        $scope.fileChanged();
                        $scope.formData.feeds.push({
                            name: msg.data.formData[0].value,
                            url: msg.data.formData[1].value
                        });
                    });
                }
                messageHub.hideFormDialog('formEditorAddFeed');
                messageHub.unsubscribe(handler);
            },
            true
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
            'formEditor.dialogs.editFeed',
            function (msg) {
                if (msg.data.buttonId === "b1") {
                    $scope.$apply(function () {
                        $scope.fileChanged();
                        feed.name = msg.data.formData[0].value;
                        feed.url = msg.data.formData[1].value;
                    });
                }
                messageHub.hideFormDialog('formEditorEditFeed');
                messageHub.unsubscribe(handler);
            },
            true
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
            'formEditor.dialogs.addScript',
            function (msg) {
                if (msg.data.buttonId === "b1") {
                    $scope.$apply(function () {
                        $scope.fileChanged();
                        $scope.formData.scripts.push({
                            name: msg.data.formData[0].value,
                            url: msg.data.formData[1].value
                        });
                    });
                }
                messageHub.hideFormDialog('formEditorAddScript');
                messageHub.unsubscribe(handler);
            },
            true
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
            'formEditor.dialogs.editFeed',
            function (msg) {
                if (msg.data.buttonId === "b1") {
                    $scope.$apply(function () {
                        $scope.fileChanged();
                        feed.name = msg.data.formData[0].value;
                        feed.url = msg.data.formData[1].value;
                    });
                }
                messageHub.hideFormDialog('formEditorEditFeed');
                messageHub.unsubscribe(handler);
            },
            true
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

    $scope.loadFileContents = function () {
        workspaceApi.loadContent('', $scope.dataParameters.file).then(function (response) {
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
        });
    };

    $scope.createFormJson = function (model) {
        let formJson = [];
        for (let i = 0; i < model.length; i++) {
            if (model[i].type.startsWith('container')) {
                formJson.push({
                    controlType: model[i].type,
                    children: $scope.createFormJson(model[i].children)
                });
            } else {
                const controlObj = {
                    controlType: model[i].type,
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
        let xhr = new XMLHttpRequest();
        xhr.open('PUT', '/services/ide/workspaces' + $scope.dataParameters.file);
        xhr.setRequestHeader('X-Requested-With', 'Fetch');
        xhr.setRequestHeader('X-CSRF-Token', csrfToken);
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4) {
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
            }
        };
        xhr.onerror = function (error) {
            console.error(`Error saving '${$scope.dataParameters.file}'`, error);
            messageHub.setStatusError(`Error saving '${$scope.dataParameters.file}'`);
            messageHub.showAlertError('Error while saving the file', 'Please look at the console for more information');
            $scope.$apply(function () {
                $scope.state.isBusy = false;
            });
        };
        xhr.send(text);
    }

    $scope.save = function () {
        if ($scope.state.canSave && $scope.isFileChanged) {
            $scope.state.isBusy = true;
            let formFile = {
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
            if (model[i].type.startsWith('container') && model[i].$scope.id !== id) {
                if ($scope.deleteControlFromModel(id, model[i].children)) return true;
            } else if (model[i].$scope.id === id) {
                model.splice(i, 1);
                return true;
            }
        }
        return false;
    };

    $scope.deleteControl = function (id) {
        if (!$scope.deleteControlFromModel(id, $scope.formModel)) {
            console.error(`Could not delete control with ID '${id}'`); // TODO: Show UI error
        } else {
            $scope.fileChanged();
            $scope.selectedCtrlId = undefined;
            $scope.selectedContainerId = undefined;
            angular.element($document[0].querySelector(`#${id}`)).remove();
        }
    };

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
        });
    }
}]);