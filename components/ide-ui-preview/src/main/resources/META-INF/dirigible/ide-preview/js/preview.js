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
const previewView = angular.module('preview', ['ideUI', 'ideView']);

previewView.config(["messageHubProvider", function (messageHubProvider) {
    messageHubProvider.eventIdPrefix = 'preview-view';
}]);

const defaultParameters = `{
  "container": "layout",
  "perspectiveId": "workbench"
}`;

previewView.controller('PreviewController', ['$scope', 'messageHub', function ($scope, messageHub) {
    $scope.customizeLabel = "Set custom view parameters";
    $scope.reloadLabel = "Reload";
    $scope.backLabel = "Go back";
    $scope.forwardLabel = "Go forward";
    $scope.urlLocked = localStorage.getItem('DIRIGIBLE.preview.urlLocked') === 'true';
    $scope.customParameters = localStorage.getItem('DIRIGIBLE.preview.customParameters');
    if (!$scope.customParameters) {
        $scope.customParameters = defaultParameters;
        localStorage.setItem('DIRIGIBLE.preview.customParameters', defaultParameters);
    }
    $scope.iframe = document.getElementById('preview-iframe');
    $scope.history = {
        idx: -1,
        state: []
    };

    $scope.urlLockedToggle = function () {
        $scope.urlLocked = !$scope.urlLocked;
        localStorage.setItem('DIRIGIBLE.preview.urlLocked', `${$scope.urlLocked}`);
    };

    $scope.reload = function () {
        let iframeDocument = $scope.iframe.contentDocument || $scope.contentWindow.document;
        if (iframeDocument) {
            iframeDocument.location.reload(true);
        }
    };

    $scope.getCurrentUrl = function () {
        return $scope.history.state[$scope.history.idx];
    };

    $scope.hasBack = function () {
        return $scope.history.idx > 0;
    };

    $scope.hasForward = function () {
        return $scope.history.idx < $scope.history.state.length - 1;
    };

    $scope.goBack = function () {
        if ($scope.hasBack()) {
            const url = $scope.history.state[--$scope.history.idx];
            $scope.replaceLocationUrl(url);
        }
    };

    $scope.goForward = function () {
        if ($scope.hasForward()) {
            const url = $scope.history.state[++$scope.history.idx];
            $scope.replaceLocationUrl(url);
        }
    };

    $scope.gotoUrl = function (url, shouldReload = true) {
        const currentUrl = $scope.getCurrentUrl();
        if (currentUrl && currentUrl === url) {
            if (shouldReload)
                $scope.reload();
            return;
        };

        if ($scope.history.idx >= 0)
            $scope.history.state.length = $scope.history.idx + 1;

        $scope.history.state.push(url);
        $scope.history.idx++;

        $scope.replaceLocationUrl(url);
    };

    $scope.replaceLocationUrl = function (url) {
        $scope.previewUrl = url;
        $scope.iframe.contentWindow.location.replace(url);
    };

    $scope.inputUrlKeyUp = function (e) {
        switch (e.key) {
            case 'Escape': // cancel url edit
                const currentUrl = $scope.getCurrentUrl();
                $scope.previewUrl = currentUrl || '';
                break;
            case 'Enter':
                if ($scope.previewUrl) {
                    $scope.gotoUrl($scope.previewUrl);
                }
                break;
        }
    };

    $scope.makeUrlFromPath = function (resourcePath) {
        let url = window.location.protocol + '//' + window.location.host + window.location.pathname.substring(window.location.pathname.indexOf('/web/'), 0);
        let type = resourcePath.substring(resourcePath.lastIndexOf('.') + 1);
        let isOData = resourcePath.endsWith(".odata");
        if (isOData) {
            url = window.location.protocol + '//' + window.location.host + "/odata/v2/";
        } else {
            switch (type) {
                case 'rhino':
                    url += '/rhino';
                    break;
                case 'nashorn':
                    url += '/nashorn';
                    break;
                case 'v8':
                    url += '/v8';
                    break;
                case 'graalvm':
                    url += '/graalvm';
                    break;
                case 'ts':
                    url += '/ts';
                    break;
                case 'js':
                    url += '/js';
                    break;
                case 'mjs':
                    url += '/js';
                    break;
                case 'ts':
                    url += '/js';
                    break;
                case 'xsjs':
                    url += '/xsjs';
                    break;
                case 'md':
                    url += '/wiki';
                    break;
                case 'command':
                    url += '/command';
                    break;
                case 'xsodata':
                    url += '/web';
                    break;
                case 'edm':
                case 'dsm':
                case 'bpmn':
                case 'job':
                case 'xsjob':
                case 'calculationview':
                case 'websocket':
                case 'hdi':
                case 'hdbtable':
                case 'hdbstructurе':
                case 'hdbview':
                case 'hdbtablefunction':
                case 'hdbprocedure':
                case 'hdbschema':
                case 'hdbsynonym':
                case 'hdbdd':
                case 'hdbsequence':
                case 'hdbcalculationview':
                case 'xsaccess':
                case 'xsprivileges':
                case 'xshttpdest':
                case 'listener':
                case 'extensionpoint':
                case 'extension':
                case 'table':
                case 'view':
                case 'access':
                case 'roles':
                case 'sh':
                case 'csv':
                case 'csvim':
                case 'hdbti':
                case 'camel':
                case 'form':
                    return;
                default:
                    url += '/web';
            }
            url += resourcePath;
        }
        return url;
    };

    $scope.customizeParameters = function () {
        messageHub.showFormDialog(
            "previewCustomizeDataParameters",
            "Set custom ViewParameters",
            [{
                id: "pdpta",
                type: "textarea",
                label: "JSON data",
                placeholder: defaultParameters,
                rows: 10,
                value: $scope.customParameters,
            }],
            [{
                id: "b1",
                type: "emphasized",
                label: "Apply",
            },
            {
                id: "b2",
                type: "transparent",
                label: "Cancel",
            }],
            "preview.formDialog.data-parameters",
            "Applying data...",
            "",
            "Data will be passed to the 'data-parameters' iframe attribute (ViewParameters)."
        );
    };

    messageHub.onDidReceiveMessage(
        "preview.formDialog.data-parameters",
        function (msg) {
            if (msg.data.buttonId === "b1") {
                let customData;
                try {
                    customData = JSON.parse(msg.data.formData[0].value)
                } catch (error) {
                    console.error(error);
                    msg.data.formData[0].error = true;
                    msg.data.formData[0].errorMsg = "Input is not a valid JSON.";
                }
                if (msg.data.formData[0].error) {
                    messageHub.updateFormDialog(
                        "previewCustomizeDataParameters",
                        msg.data.formData,
                        "Applying data...",
                        "Input is not a valid JSON"
                    );
                } else {
                    $scope.customParameters = JSON.stringify(customData, null, 2);
                    localStorage.setItem('DIRIGIBLE.preview.customParameters', $scope.customParameters);
                    messageHub.hideFormDialog("previewCustomizeDataParameters");
                    $scope.reload();
                }
            } else {
                messageHub.hideFormDialog("previewCustomizeDataParameters");
            }
        },
        true
    );

    messageHub.onFileSelected((fileDescriptor) => {
        if ($scope.urlLocked)
            return;

        let url = $scope.makeUrlFromPath(fileDescriptor.path);
        if (url) {
            $scope.gotoUrl(url, false);
            $scope.$apply();
        }
    });

    messageHub.onPublish((fileDescriptor) => {
        if ($scope.urlLocked)
            return;

        if (fileDescriptor.path) {
            let url = $scope.makeUrlFromPath(fileDescriptor.path);
            if (url) {
                $scope.gotoUrl(url);
                $scope.$apply();
            }
        } else {
            $scope.reload();
            $scope.$apply();
        }
    });

    messageHub.onUnpublish((fileDescriptor) => {
        if ($scope.urlLocked)
            return;

        if (fileDescriptor.path) {
            let url = $scope.makeUrlFromPath(fileDescriptor.path);
            if (url) {
                $scope.gotoUrl(url);
                $scope.$apply();
            }
        } else {
            $scope.reload();
            $scope.$apply();
        }
    });
}]);
