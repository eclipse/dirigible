/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
let editorView = angular.module('csvim-editor', []);

editorView.factory('$messageHub', [function () {
    let messageHub = new FramesMessageHub();
    let message = function (evtName, data) {
        messageHub.post({ data: data }, evtName);
    };
    let on = function (topic, callback) {
        messageHub.subscribe(callback, topic);
    };
    return {
        message: message,
        on: on
    };
}]);

editorView.directive('allowedSymbols', () => {
    return {
        restrict: 'A',
        require: 'ngModel',
        scope: {
            regex: '@allowedSymbols'
        },
        link: (scope, element, attrs, controller) => {
            controller.$validators.forbiddenName = value => {
                if (!value) {
                    return true;
                }
                let correct = RegExp(scope.regex, 'gm').test(value);
                if (correct) {
                    element.removeClass("error-input");
                } else {
                    element.addClass('error-input');
                }
                if (attrs.hasOwnProperty("id") && attrs["id"] === "filepath") {
                    scope.$parent.fileExists = true;
                }
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
                let correct = RegExp(scope.regex, 'gm').test(value);
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
                scope.$parent.setSaveEnabled(correct);
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
    var csrfToken;
    var contents;
    $scope.fileExists = true;
    $scope.saveEnabled = true;
    $scope.editEnabled = false;
    $scope.dataEmpty = true;
    $scope.dataLoaded = false;
    $scope.csvimData = [];
    $scope.activeItemId = 0;
    $scope.delimiterList = [',', '\\t', '|', ';', '#'];
    $scope.quoteCharList = ["'", "\""];

    $scope.openFile = function () {
        if ($scope.checkResource($scope.csvimData[$scope.activeItemId].file)) {
            let msg = {
                "file": {
                    "name": $scope.csvimData[$scope.activeItemId].name,
                    "path": $scope.csvimData[$scope.activeItemId].file,
                    "type": "file",
                    "contentType": "text/csv",
                    "label": $scope.csvimData[$scope.activeItemId].name
                },
                "extraArgs": {
                    "header": $scope.csvimData[$scope.activeItemId].header,
                    "delimiter": $scope.csvimData[$scope.activeItemId].delimField,
                    "quotechar": $scope.csvimData[$scope.activeItemId].delimEnclosing
                }
            };
            $messageHub.message('ide-core.openEditor', msg);
        }
    };

    $scope.setSaveEnabled = function (enabled) {
        $scope.saveEnabled = enabled;
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
        $scope.setEditEnabled(false);
        $scope.fileExists = true;
        $scope.activeItemId = id;
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
        $messageHub.message('editor.file.dirty', $scope.file);
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
            xhr.open('HEAD', `../../../../../../services/v4/ide/workspaces${resourcePath}`, false);
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
        return value;
    }

    function loadFileContents() {
        let searchParams = new URLSearchParams(window.location.search);
        $scope.file = searchParams.get('file');
        if ($scope.file) {
            $http.get('../../../../../../services/v4/ide/workspaces' + $scope.file)
                .then(function (response) {
                    contents = response.data;
                    if (!contents) contents = [];
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
                            console.error("Loading file:", response.data.error.message);
                        }
                    }
                });
        } else {
            console.error('file parameter is not present in the URL');
        }
    }

    function saveContents(text) {
        console.log('Save called...');
        if ($scope.file) {
            let xhr = new XMLHttpRequest();
            xhr.open('PUT', '../../../../../../services/v4/ide/workspaces' + $scope.file);
            xhr.setRequestHeader('X-Requested-With', 'Fetch');
            xhr.setRequestHeader('X-CSRF-Token', csrfToken);
            xhr.onreadystatechange = function () {
                if (xhr.readyState === 4) {
                    console.log('file saved: ' + $scope.file);
                }
            };
            xhr.send(text);
            contents = text;
            isFileChanged = false;
            $messageHub.message('editor.file.saved', $scope.file);
            $messageHub.message('status.message', 'File [' + $scope.file + '] saved.');
        } else {
            console.error('file parameter is not present in the request');
        }
    }

    function checkPlatform() {
        let platform = window.navigator.platform;
        let macosPlatforms = ['Macintosh', 'MacIntel', 'MacPPC', 'Mac68K', 'darwin', 'Mac', 'mac', 'macOS'];
        if (macosPlatforms.indexOf(platform) !== -1) isMac = true;
    }

    checkPlatform();
    loadFileContents();

}]);