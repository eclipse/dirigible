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
'use strict';
/**
 * Takes a sorted array and a function p. The array is sorted in such a way that all elements where p(x) is false
 * are located before all elements where p(x) is true.
 * @returns the least x for which p(x) is true or array.length if no element fullfills the given function.
 */
export function findFirst(array, p) {
    var low = 0, high = array.length;
    if (high === 0) {
        return 0; // no children
    }
    while (low < high) {
        var mid = Math.floor((low + high) / 2);
        if (p(array[mid])) {
            high = mid;
        }
        else {
            low = mid + 1;
        }
    }
    return low;
}
export function includes(array, item) {
    return array.indexOf(item) !== -1;
}
export function union() {
    var arrays = [];
    for (var _i = 0; _i < arguments.length; _i++) {
        arrays[_i] = arguments[_i];
    }
    var result = [];
    for (var _a = 0, arrays_1 = arrays; _a < arrays_1.length; _a++) {
        var array = arrays_1[_a];
        for (var _b = 0, array_1 = array; _b < array_1.length; _b++) {
            var item = array_1[_b];
            if (!includes(result, item)) {
                result.push(item);
            }
        }
    }
    return result;
}
