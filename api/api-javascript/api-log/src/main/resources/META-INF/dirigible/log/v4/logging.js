/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
exports.getLogger = function (loggerName) {

    //"at obj.f2 (:29:9)" - length 8
    //"at :36:4"		  - length 8
    //"at Error (native)" - length 6
    const v8_stack_el_regex = /^\s*at ?(.*?) ((.*?)\s*(?:\()?(?:(.*?))(?::(\d+))?(?::(\d+))(?:\))?|\((native)\))$/;
    var parseIntoStackTraceElementsV8 = function (stack) {
        var lines = stack.split("\n");
        var stackLineIdx = 0;
        lines = lines.map(function (line) {
            if (stackLineIdx > 0 || line.trim().length < 1) {
                var _segmenets = line.trim().match(v8_stack_el_regex);
                if (_segmenets === null)
                    return;
                //resolve object and function if specified
                var _obj, _m;
                if (_segmenets[1]) {
                    var _idx = _segmenets[1].lastIndexOf('.');
                    if (_idx < 0)
                        _m = _segmenets[1];
                    else {
                        _m = _segmenets[1].substring(_idx + 1, _segmenets[1].length);
                        _obj = _segmenets[1].substring(0, _segmenets[1].length - _m.length - 1);
                    }
                }
                return {
                    fileName: _segmenets.length < 8 ? _segmenets[3] : _segmenets[4],
                    lineNumber: _segmenets.length < 8 ? _segmenets[4] : _segmenets[5],
                    declaringClass: _obj || "?",
                    methodName: _m || "?"
                };
            }
            stackLineIdx++;
            return;
        }).filter(function (el) {
            return el !== undefined;
        });
        return lines;
    };

    //at prj1/svc/a.js:28 (anonymous)
    //at prj1/svc/a.js:36
    const rhino_stack_el_regex = /^\s*at (.*?) ?(.*?)(?::(\d+))?(?::(\d+))?\s*(\((.*?)\))?\s*$/;
    var parseIntoStackTraceElementsRhino = function (stack) {
        var lines = stack.split("\n");
        lines = lines.map(function (line) {
            if (line.trim().length < 1)
                return;
            var _lineSegments = line.match(rhino_stack_el_regex);
            return {
                fileName: _lineSegments[2],
                lineNumber: _lineSegments[3],
                declaringClass: "?",
                methodName: _lineSegments[6] || '?'
            };
        }).filter(function (el) {
            return el !== undefined;
        });
        return lines;

    };

    var parseIntoStackTraceElementsNashorn = function () {
        //TODO
    }

    var resolveError = function (err) {
        var stack = [];
        if (__engine === 'v8') {
            stack = parseIntoStackTraceElementsV8(err.stack);
        } else if (__engine === 'rhino' && err.stack) {
            stack = parseIntoStackTraceElementsRhino(err.stack);
        }
        return {
            message: err.message,
            stack: stack
        };
    };

    return {
        setLevel: function (level) {
            org.eclipse.dirigible.api.v3.log.LogFacade.setLevel(loggerName, level);
            return this;
        },
        log: function (msg, level) {
            var args = Array.prototype.slice.call(arguments);
            var msgParameters = [];
            var errObjectJson = null;
            if (args.length > 2) {
                if (args[2] instanceof Error) {
                    var errObject = resolveError(args[2]);
                    if (errObject) {
                        errObjectJson = JSON.stringify(errObject);
                    }
                }
                var sliceIndex = errObjectJson ? 3 : 2;
                msgParameters = args.slice(sliceIndex).map(function (param) {
                    return typeof param === 'object' ? JSON.stringify(param) : param;
                });
            }
            org.eclipse.dirigible.api.v3.log.LogFacade.log(loggerName, level, msg, JSON.stringify(msgParameters), errObjectJson);
        },
        debug: function (msg) {
            var args = Array.prototype.slice.call(arguments);
            args.splice(1, 0, 'DEBUG');//insert DEBUG on second position in arguments array
            this.log.apply(this, args);
        },
        info: function (msg) {
            var args = Array.prototype.slice.call(arguments);
            args.splice(1, 0, 'INFO');//insert INFO on second position in arguments array
            this.log.apply(this, args);
        },
        trace: function (msg) {
            var args = Array.prototype.slice.call(arguments);
            args.splice(1, 0, 'TRACE');//insert TRACE on second position in arguments array
            this.log.apply(this, args);
        },
        warn: function (msg) {
            var args = Array.prototype.slice.call(arguments);
            args.splice(1, 0, 'WARN');//insert WARN on second position in arguments array
            this.log.apply(this, args);
        },
        error: function (msg) {
            var args = Array.prototype.slice.call(arguments);
            args.splice(1, 0, 'ERROR');//insert ERROR on second position in arguments array
            this.log.apply(this, args);
        }
    };
};
