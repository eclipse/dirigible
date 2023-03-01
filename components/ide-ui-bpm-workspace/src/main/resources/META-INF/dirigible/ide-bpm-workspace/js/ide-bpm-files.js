/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
let ideBpmFilesView = angular.module('ide-bpm-files', ['ideUI', 'ideView']);

ideBpmFilesView.config(["messageHubProvider", function (messageHubProvider) {
    messageHubProvider.eventIdPrefix = 'IDEBPMWorkspace';
}]);

ideBpmFilesView.controller('IDEBpmFilesViewController', ['$scope', 'messageHub', function ($scope, messageHub) {
    $scope.searchVisible = false;
    $scope.searchField = { text: '' };
    $scope.jstreeWidget = angular.element('#bpmFiles');

    let to = 0;
    $scope.search = function () {
        if (to) { clearTimeout(to); }
        to = setTimeout(function () {
            $scope.jstreeWidget.jstree(true).search($scope.searchField.text);
        }, 250);
    };

    $scope.reload = function () { // Doesn't do anything useful
        $scope.jstreeWidget.jstree(true).refresh();
    };

    $scope.toggleSearch = function () {
        $scope.searchField.text = '';
        $scope.jstreeWidget.jstree(true).clear_search();
        $scope.searchVisible = !$scope.searchVisible;
    };

    fetch("/services/ide/bpm/bpm-processes/keys")
        .then((response) => response.json())
        .then((data) => {
            $scope.jstreeWidget.jstree({ // JSTree should NOT be initialized here
                core: {
                    check_callback: true,
                    themes: {
                        name: "fiori",
                        variant: "compact",
                    },
                    data: data.map(processKey => ({ text: processKey, type: "file" })),
                },
                plugins: ["wholerow", "dnd", "search", "state", "types", "indicator"],
                dnd: {
                    large_drop_target: true,
                    large_drag_target: true,
                    is_draggable: function (nodes) {
                        for (let i = 0; i < nodes.length; i++) {
                            if (nodes[i].type === 'project') return false;
                        }
                        return true;
                    },
                },
                state: { "key": "ide-bpm-workspace" },
                types: {
                    '#': {
                        valid_children: ["project"]
                    },
                    "default": {
                        icon: "sap-icon--question-mark",
                        valid_children: [],
                    },
                    file: {
                        icon: "jstree-file",
                        valid_children: [],
                    },
                    folder: {
                        icon: "jstree-folder",
                        valid_children: ['folder', 'file', 'spinner'],
                    },
                    project: {
                        icon: "jstree-project",
                        valid_children: ['folder', 'file', 'spinner'],
                    },
                    spinner: {
                        icon: "jstree-spinner",
                        valid_children: [],
                    },
                },
            }).on("select_node.jstree", function (e, data) {
                messageHub.postMessage('image-viewer.image', { filename: data.node.text });
            });
        });
}]);