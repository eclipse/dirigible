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
export function getSpaceCnt(str, tabSize) {
    var spacesCnt = 0;
    for (var i = 0; i < str.length; i++) {
        if (str.charAt(i) === '\t') {
            spacesCnt += tabSize;
        }
        else {
            spacesCnt++;
        }
    }
    return spacesCnt;
}
export function generateIndent(spacesCnt, tabSize, insertSpaces) {
    spacesCnt = spacesCnt < 0 ? 0 : spacesCnt;
    var result = '';
    if (!insertSpaces) {
        var tabsCnt = Math.floor(spacesCnt / tabSize);
        spacesCnt = spacesCnt % tabSize;
        for (var i = 0; i < tabsCnt; i++) {
            result += '\t';
        }
    }
    for (var i = 0; i < spacesCnt; i++) {
        result += ' ';
    }
    return result;
}
