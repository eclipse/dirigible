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
import { startsWithIgnoreCase } from './strings.js';
import { sep, posix } from './path.js';
/**
 * Takes a Windows OS path and changes backward slashes to forward slashes.
 * This should only be done for OS paths from Windows (or user provided paths potentially from Windows).
 * Using it on a Linux or MaxOS path might change it.
 */
export function toSlashes(osPath) {
    return osPath.replace(/[\\/]/g, posix.sep);
}
export function isEqualOrParent(path, candidate, ignoreCase, separator) {
    if (separator === void 0) { separator = sep; }
    if (path === candidate) {
        return true;
    }
    if (!path || !candidate) {
        return false;
    }
    if (candidate.length > path.length) {
        return false;
    }
    if (ignoreCase) {
        var beginsWith = startsWithIgnoreCase(path, candidate);
        if (!beginsWith) {
            return false;
        }
        if (candidate.length === path.length) {
            return true; // same path, different casing
        }
        var sepOffset = candidate.length;
        if (candidate.charAt(candidate.length - 1) === separator) {
            sepOffset--; // adjust the expected sep offset in case our candidate already ends in separator character
        }
        return path.charAt(sepOffset) === separator;
    }
    if (candidate.charAt(candidate.length - 1) !== separator) {
        candidate += separator;
    }
    return path.indexOf(candidate) === 0;
}
export function isWindowsDriveLetter(char0) {
    return char0 >= 65 /* A */ && char0 <= 90 /* Z */ || char0 >= 97 /* a */ && char0 <= 122 /* z */;
}
