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
let editorView = angular.module('csvim-editor', []);

editorView.factory('$messageHub', [function () {
    let messageHub = new FramesMessageHub();
    let announceAlert = function (title, message, type) {
        messageHub.post({
            data: {
                title: title,
                message: message,
                type: type
            }
        }, 'ide.alert');
    };
    let announceAlertError = function (title, message) {
        announceAlert(title, message, "error");
    };
    let message = function (evtName, data) {
        messageHub.post({ data: data }, evtName);
    };
    // Temp thing
    let post = function (data, evtName) {
        messageHub.post(data, evtName);
    };
    let on = function (topic, callback) {
        messageHub.subscribe(callback, topic);
    };
    return {
        announceAlert: announceAlert,
        announceAlertError: announceAlertError,
        message: message,
        post: post,
        on: on,
    };
}]);

editorView.directive('validateInput', () => {
    return {
        restrict: 'A',
        require: 'ngModel',
        scope: {
            regex: '@validateInput'
        },
        link: (scope, element, attrs, controller) => {
            controller.$validators.forbiddenName = value => {
                let correct = false;
                if (value) {
                    correct = RegExp(scope.regex, 'g').test(value);
                }
                if (attrs.hasOwnProperty("id")) {
                    if (attrs["id"] === "table") scope.$parent.showTableError(!correct);
                    else if (attrs["id"] === "schema") {
                        if (value === '' || value === null || value === undefined) {
                            scope.$parent.showSchemaError(false);
                            correct = true;
                        } else scope.$parent.showSchemaError(!correct);
                    } else if (attrs["id"] === "filepath") {
                        scope.$parent.fileExists = true;
                        scope.$parent.showFilepathError(!correct);
                    }
                }
                if (correct) element.removeClass("error-input");
                else element.addClass('error-input');
                scope.$parent.setSaveEnabled(correct);
                return correct;
            };
        }
    };
});

editorView.directive('uniqueField', () => {
    return {
        restrict: 'A',
        require: 'ngModel',
        scope: {
            regex: '@uniqueField'
        },
        link: (scope, element, attrs, controller) => {
            controller.$validators.forbiddenName = value => {
                let unique = true;
                let correct = RegExp(scope.regex, 'g').test(value);
                if (correct) {
                    if ("index" in attrs) {
                        for (let i = 0; i < scope.$parent.csvimData[scope.$parent.activeItemId].keys.length; i++) {
                            if (i != attrs.index) {
                                if (value === scope.$parent.csvimData[scope.$parent.activeItemId].keys[i].column) {
                                    unique = false;
                                    break;
                                }
                            }
                        }
                    } else if ("kindex" in attrs && "vindex" in attrs) {
                        for (let i = 0; i < scope.$parent.csvimData[scope.$parent.activeItemId].keys[attrs.kindex].values.length; i++) {
                            if (i != attrs.vindex) {
                                if (value === scope.$parent.$parent.csvimData[scope.$parent.activeItemId].keys[attrs.kindex].values[i]) {
                                    unique = false;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (correct && unique) {
                    element.removeClass("error-input");
                } else {
                    element.addClass('error-input');
                }
                scope.$parent.setSaveEnabled(correct && unique);
                return unique;
            };
        }
    };
});

editorView.controller('CsvimViewController', ['$scope', '$http', '$messageHub', '$window', function ($scope, $http, $messageHub, $window) {
    let isFileChanged = false;
    const ctrlKey = 17;
    let ctrlDown = false;
    let isMac = false;
    let workspace = 'workspace'; // This needs to be replace with an API.
    let csrfToken;
    $scope.schemaError = {
        hasError: false,
        msg: 'Schema can only contain letters (a-z, A-Z), numbers (0-9), hyphens ("-"), dots ("."), underscores ("_"), and dollar signs ("$")'
    };
    $scope.tableError = {
        hasError: false,
        msg: 'Table can only contain letters (a-z, A-Z), numbers (0-9), hyphens ("-"), dots ("."), underscores ("_"), and dollar signs ("$"). Two colons ("::") are permitted only when table name contains schema ("schemaName::tableName").'
    };
    $scope.filepathError = {
        hasError: false,
        msg: 'Path can only contain letters (a-z, A-Z), numbers (0-9), hyphens ("-"), forward slashes ("/"), dots ("."), underscores ("_"), and dollar signs ("$")'
    };
    $scope.fileExists = true;
    $scope.saveEnabled = true;
    $scope.editEnabled = false;
    $scope.dataEmpty = true;
    $scope.dataLoaded = false;
    $scope.csvimData = [];
    $scope.activeItemId = 0;
    $scope.delimiterList = [',', '\\t', '|', ';', '#'];
    $scope.quoteCharList = ["'", "\"", "#"];

    angular.element($window).bind("focus", function () {
        $messageHub.post({ data: { file: $scope.file } }, 'editor.focus.gained');
        $messageHub.post({ text: '' }, 'ide.status.caret');
    });

    $scope.openFile = function () {
        if ($scope.checkResource($scope.csvimData[$scope.activeItemId].file)) {
            $messageHub.post({
                resourcePath: `/${workspace}${$scope.csvimData[$scope.activeItemId].file}`,
                resourceLabel: $scope.csvimData[$scope.activeItemId].name,
                contentType: "text/csv",
                extraArgs: {
                    "header": $scope.csvimData[$scope.activeItemId].header,
                    "delimiter": $scope.csvimData[$scope.activeItemId].delimField,
                    "quotechar": $scope.csvimData[$scope.activeItemId].delimEnclosing
                },
            }, 'ide-core.openEditor');
        }
    };

    $scope.showTableError = function (hasError) {
        $scope.tableError.hasError = hasError;
    };

    $scope.showSchemaError = function (hasError) {
        $scope.schemaError.hasError = hasError;
    };

    $scope.showFilepathError = function (hasError) {
        $scope.filepathError.hasError = hasError;
    };

    $scope.inputsHaveErrors = function () {
        let inputs = document.getElementsByClassName("form-control");
        for (let i = 0; i < inputs.length; i++) {
            if (inputs[i].classList.contains('error-input')) return true;
        }
        return false;
    };

    $scope.setSaveEnabled = function (enabled) {
        if (enabled && !$scope.inputsHaveErrors()) $scope.saveEnabled = true;
        else $scope.saveEnabled = false;
    };

    $scope.setEditEnabled = function (enabled) {
        if (enabled != undefined) {
            $scope.editEnabled = enabled;
        } else {
            $scope.editEnabled = !$scope.editEnabled;
        }
    };

    $scope.addNew = function () {
        let newCsv = {
            "name": "Untitled",
            "visible": true,
            "table": "",
            "schema": "",
            "file": "",
            "header": false,
            "useHeaderNames": false,
            "delimField": ";",
            "delimEnclosing": "\"",
            "distinguishEmptyFromNull": true,
            "keys": []
        };
        // Clean search bar
        $scope.filesSearch = "";
        $scope.filterFiles();
        $scope.csvimData.push(newCsv);
        $scope.activeItemId = $scope.csvimData.length - 1;
        $scope.dataEmpty = false;
        $scope.setEditEnabled(false);
        $scope.fileChanged();
    };

    $scope.getFileName = function (str, canBeEmpty = true) {
        if (canBeEmpty) {
            return str.split('\\').pop().split('/').pop();
        }
        let title = str.split('\\').pop().split('/').pop();
        if (title) return title;
        else return "Untitled";
    };

    $scope.fileSelected = function (id) {
        if (!$scope.inputsHaveErrors()) {
            $scope.setEditEnabled(false);
            $scope.fileExists = true;
            $scope.activeItemId = id;
        }
    };

    $scope.isDelimiterSupported = function (delimiter) {
        return $scope.delimiterList.includes(delimiter);
    };

    $scope.isQuoteCharSupported = function (quoteChar) {
        return $scope.quoteCharList.includes(quoteChar);
    };

    $scope.delimiterChanged = function (delimiter) {
        $scope.csvimData[$scope.activeItemId].delimField = delimiter;
        $scope.fileChanged();
    };

    $scope.quoteCharChanged = function (quoteChar) {
        $scope.csvimData[$scope.activeItemId].delimEnclosing = quoteChar;
        $scope.fileChanged();
    };

    $scope.addValueToKey = function (column) {
        let entry_num = 1;
        for (let i = 0; i < $scope.csvimData[$scope.activeItemId].keys.length; i++) {
            if ($scope.csvimData[$scope.activeItemId].keys[i].column === column) {
                for (let k = 0; k < $scope.csvimData[$scope.activeItemId].keys[i].values.length; k++) {
                    let num = getNumber(
                        $scope.csvimData[$scope.activeItemId].keys[i].values[k].replace("NEW_ENTRY_", '')
                    );
                    if (!isNaN(num) && num >= entry_num) {
                        entry_num = num + 1;
                    }
                }
                $scope.csvimData[$scope.activeItemId].keys[i].values.push(`NEW_ENTRY_${entry_num}`);
                break;
            }
        }
        $scope.fileChanged();
    };

    $scope.removeValueFromKey = function (columnIndex, valueIndex) {
        $scope.csvimData[$scope.activeItemId].keys[columnIndex].values.splice(valueIndex, 1);
        $scope.fileChanged();
    };

    $scope.addKeyColumn = function () {
        let num = 1;
        for (let i = 0; i < $scope.csvimData[$scope.activeItemId].keys.length; i++) {
            if ($scope.csvimData[$scope.activeItemId].keys[i].column === `NEW_ENTRY_${num}`) {
                num++;
            }
        }
        $scope.csvimData[$scope.activeItemId].keys.push(
            {
                "column": `NEW_ENTRY_${num}`,
                "values": []
            }
        );
        $scope.fileChanged();
    };

    $scope.removeKeyColumn = function (index) {
        $scope.csvimData[$scope.activeItemId].keys.splice(index, 1);
        $scope.fileChanged();
    };

    $scope.save = function () {
        if (isFileChanged && $scope.saveEnabled) {
            $scope.checkResource($scope.csvimData[$scope.activeItemId].file);
            $scope.csvimData[$scope.activeItemId].name = $scope.getFileName($scope.csvimData[$scope.activeItemId].file, false);
            saveContents(JSON.stringify($scope.csvimData, cleanForOutput, 2));
        }
    };

    $scope.deleteFile = function () {
        // Clean search bar
        $scope.csvimData.splice($scope.activeItemId, 1);
        $scope.setEditEnabled(false);
        $scope.fileExists = true;
        if ($scope.csvimData.length > 0) {
            $scope.dataEmpty = false;
            $scope.activeItemId = $scope.csvimData.length - 1;
        } else {
            $scope.dataEmpty = true;
            $scope.activeItemId = 0;
        }
        $scope.fileChanged();
    };

    $scope.filterFiles = function () {
        if ($scope.filesSearch) {
            for (let i = 0; i < $scope.csvimData.length; i++) {
                if ($scope.csvimData[i].name.toLowerCase().includes($scope.filesSearch.toLowerCase())) {
                    $scope.csvimData[i].visible = true;
                } else {
                    $scope.csvimData[i].visible = false;
                }
            }
        } else {
            for (let i = 0; i < $scope.csvimData.length; i++) {
                $scope.csvimData[i].visible = true;
            }
        }
    };

    $scope.fileChanged = function () {
        isFileChanged = true;
        $messageHub.post({ resourcePath: $scope.file, isDirty: isFileChanged }, 'ide-core.setEditorDirty');
    };

    $scope.keyDownFunc = function ($event) {
        if (
            ctrlDown &&
            String.fromCharCode($event.which).toLowerCase() == 's'
        ) {
            $event.preventDefault();
            if (isFileChanged)
                $scope.save();
        }
    };

    angular.element($window).bind("keyup", function (/*$event*/) {
        ctrlDown = false;
    });

    angular.element($window).bind("keydown", function ($event) {
        if (isMac && "metaKey" in $event && $event.metaKey)
            ctrlDown = true;
        else if ($event.keyCode == ctrlKey)
            ctrlDown = true;
    });

    $scope.checkResource = function (resourcePath) {
        if (resourcePath != "") {
            let xhr = new XMLHttpRequest();
            xhr.open('HEAD', `/services/v4/ide/workspaces/${workspace}${resourcePath}`, false);
            xhr.setRequestHeader('X-CSRF-Token', 'Fetch');
            xhr.send();
            if (xhr.status === 200) {
                csrfToken = xhr.getResponseHeader("x-csrf-token");
                $scope.fileExists = true;
            } else {
                $scope.fileExists = false;
            }
        } else {
            $scope.fileExists = false;
        }
        return $scope.fileExists;
    };

    function getNumber(str) {
        if (typeof str != "string") return NaN;
        let strNum = parseFloat(str);
        // use type coercion to parse the _entirety_ of the string (`parseFloat` alone does not do this) and ensure strings of whitespace fail
        let isNumber = !isNaN(str) && !isNaN(strNum);
        if (isNumber) return strNum;
        else return NaN;
    }

    /**
     * Used for removing some keys from the object before turning it into a string.
     */
    function cleanForOutput(key, value) {
        if (key === "name" || key === "visible") {
            return undefined;
        }
        if (key === "schema" && value === "") {
            return undefined;
        }
        return value;
    }

    function getViewParameters() {
        if (window.frameElement.hasAttribute("data-parameters")) {
            let params = JSON.parse(window.frameElement.getAttribute("data-parameters"));
            $scope.file = params["file"];
        } else {
            let searchParams = new URLSearchParams(window.location.search);
            $scope.file = searchParams.get('file');
        }
    }

    function loadFileContents() {
        getViewParameters();
        if ($scope.file) {
            $http.get('/services/v4/ide/workspaces' + $scope.file)
                .then(function (response) {
                    let contents = response.data;
                    if (!contents || !Array.isArray(contents)) {
                        contents = [];
                    }
                    $scope.csvimData = contents;
                    for (let i = 0; i < $scope.csvimData.length; i++) {
                        $scope.csvimData[i]["name"] = $scope.getFileName($scope.csvimData[i].file, false);
                        $scope.csvimData[i]["visible"] = true;
                    }
                    $scope.activeItemId = 0;
                    if ($scope.csvimData.length > 0) {
                        $scope.dataEmpty = false;
                    } else {
                        $scope.dataEmpty = true;
                    }
                    $scope.dataLoaded = true;
                }, function (response) {
                    if (response.data) {
                        if ("error" in response.data) {
                            $messageHub.post({
                                message: `Error loading '${$scope.file}'`
                            }, 'ide.status.error');
                            console.error("Loading file:", response.data.error.message);
                        }
                    } else {
                        $messageHub.announceAlertError(
                            "Error while loading the file",
                            "Please look at the console for more information"
                        );
                        console.error(response);
                    }
                });
        } else {
            console.error("CSVIM Editor: file parameter is missing");
        }
    }

    function saveContents(text) {
        if ($scope.file) {
            let xhr = new XMLHttpRequest();
            xhr.open('PUT', '/services/v4/ide/workspaces' + $scope.file);
            xhr.setRequestHeader('X-Requested-With', 'Fetch');
            xhr.setRequestHeader('X-CSRF-Token', csrfToken);
            xhr.onreadystatechange = function () {
                if (xhr.readyState === 4) {
                    $messageHub.post({
                        name: $scope.file.substring($scope.file.lastIndexOf('/') + 1),
                        path: $scope.file.substring($scope.file.indexOf('/', 1)),
                        contentType: 'application/json+csvim', // TODO: Take this from data-parameters
                        workspace: $scope.file.substring(1, $scope.file.indexOf('/', 1)),
                    }, 'ide.file.saved');
                    $messageHub.post({ message: `File '${$scope.file}' saved` }, 'ide.status.message');
                    $messageHub.post({ resourcePath: $scope.file, isDirty: false }, 'ide-core.setEditorDirty');
                }
            };
            xhr.onerror = function (error) {
                console.error(`Error saving '${$scope.file}'`, error);
                $messageHub.post({
                    message: `Error saving '${$scope.file}'`
                }, 'ide.status.error');
                $messageHub.announceAlertError(
                    "Error while saving the file",
                    "Please look at the console for more information"
                );
            };
            xhr.send(text);
            isFileChanged = false;
        } else {
            console.error("CSVIM Editor: file parameter is missing");
        }
    }

    function checkPlatform() {
        let platform = window.navigator.platform; // This needs improvement
        let macosPlatforms = ['Macintosh', 'MacIntel', 'MacPPC', 'Mac68K', 'darwin', 'Mac', 'mac', 'macOS'];
        if (macosPlatforms.indexOf(platform) !== -1) isMac = true;
    }

    function getCurrentWorkspace() { // This needs to be replaced with an API
        let storedWorkspace = JSON.parse(localStorage.getItem('DIRIGIBLE.workspace') || '{}');
        if ('name' in storedWorkspace) workspace = storedWorkspace.name;
        else workspace = 'workspace';
    }

    $messageHub.on(
        "editor.file.save.all",
        function () {
            if (isFileChanged) {
                $scope.save();
            }
        },
    );

    $messageHub.on(
        "editor.file.save",
        function (msg) {
            let file = msg.data && typeof msg.data === 'object' && msg.data.file;
            if (file && file === $scope.file && isFileChanged)
                $scope.save();
        },
    );

    getCurrentWorkspace();
    checkPlatform();
    loadFileContents();

}]);