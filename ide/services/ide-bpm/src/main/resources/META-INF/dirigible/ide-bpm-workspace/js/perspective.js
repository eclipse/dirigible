/*
 * Copyright (c) 2022 codbex or an codbex affiliate company and contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 codbex or an codbex affiliate company and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
let IDEBPMWorkspacePerspective = angular.module("IDEBPMWorkspace", ["ngResource", "ideLayout", "ideUI"]);

IDEBPMWorkspacePerspective.config(["messageHubProvider", function (messageHubProvider) {
    messageHubProvider.eventIdPrefix = 'IDEBPMWorkspace';
}]);

// Initialize controller
IDEBPMWorkspacePerspective.controller("ExampleViewController", ["$scope", "messageHub", function ($scope, messageHub) {

    $scope.formItems = [
        {
            id: "fdti1",
            type: "input",
            label: "Test input 1",
            required: true,
            placeholder: "test placeholder",
            minlength: 3,
            maxlength: 6,
            inputRules: {
                excluded: ['excludedword'],
                patterns: ['^[^/]*$'],
            },
            value: '',
            visibility: {
                hidden: true,
                id: "fdtd1",
                value: "secondItem",
            },
        },
        {
            id: "fdti2",
            type: "input",
            label: "Test input 2",
            placeholder: "test placeholder",
            value: 'IDEBPMWorkspace',
        },
        {
            id: "fdtc1",
            type: "checkbox",
            label: "Test checkbox 1",
            value: false,
            visibility: {
                hidden: false,
                id: "fdti1",
                value: "test1",
            }
        },
        {
            id: "fdtd1",
            type: "dropdown",
            label: "Test dropdown",
            required: true,
            value: undefined,
            items: [
                {
                    label: "First item",
                    value: 0,
                },
                {
                    label: "Second item",
                    value: 1,
                },
                {
                    label: "Third item",
                    value: 2,
                }
            ]
        },
        {
            id: "fdtr1",
            type: "radio",
            required: true,
            value: '',
            items: [
                {
                    id: "rsi1",
                    label: "First radio",
                    value: "firstRadio",
                },
                {
                    id: "rsi2",
                    label: "Second radio",
                    value: "secondRadio",
                },
                {
                    id: "rsi3",
                    label: "Third radio",
                    value: "thirdRadio",
                }
            ]
        },
    ];

    $scope.contextMenuContent = function (element) {
        return {
            callbackTopic: "IDEBPMWorkspace.contextmenu",
            items: [
                {
                    id: "new",
                    label: "New",
                    icon: "sap-icon--create",
                    items: [
                        {
                            id: "tab",
                            label: "Tab"
                        },
                    ]
                },
                {
                    id: "other",
                    label: "Other",
                    divider: true,
                    icon: "sap-icon--question-mark"
                }
            ]
        }
    };

    messageHub.onDidReceiveMessage(
        "contextmenu",
        function (msg) {
            if (msg.data == "other") {
                messageHub.showAlertSuccess(
                    "Success",
                    "You have selected the other option!"
                );
            } else {
                messageHub.showAlertInfo(
                    "Nothing will happen",
                    "This is just a demo after all."
                );
            }
        }
    );

    messageHub.onDidReceiveMessage(
        "IDEBPMWorkspace.busy.closed",
        function () {
            messageHub.setStatusMessage('Busy dialog closed by the user.');
        },
        true
    );

    $scope.layoutModel = {
        // Array of view ids
        views: ["ide-bpm-files-view", "ide-user-tasks"],
        viewSettings: {
            "ide-bpm-files-view": { isClosable: true },
            "ide-user-tasks": { isClosable: true }
        },
        layoutSettings: {
            hideEditorsPane: false
        },
        events: {
            "IDEBPMWorkspace.alert.info": function (msg) {
                console.info(msg.data.message);
            }
        }
    };
}]);