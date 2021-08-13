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
import { Range, SelectionRange } from '../cssLanguageTypes.js';
import { NodeType } from '../parser/cssNodes.js';
export function getSelectionRanges(document, positions, stylesheet) {
    function getSelectionRange(position) {
        var applicableRanges = getApplicableRanges(position);
        var current = undefined;
        for (var index = applicableRanges.length - 1; index >= 0; index--) {
            current = SelectionRange.create(Range.create(document.positionAt(applicableRanges[index][0]), document.positionAt(applicableRanges[index][1])), current);
        }
        if (!current) {
            current = SelectionRange.create(Range.create(position, position));
        }
        return current;
    }
    return positions.map(getSelectionRange);
    function getApplicableRanges(position) {
        var currNode = stylesheet.findChildAtOffset(document.offsetAt(position), true);
        if (!currNode) {
            return [];
        }
        var result = [];
        while (currNode) {
            if (currNode.parent &&
                currNode.offset === currNode.parent.offset &&
                currNode.end === currNode.parent.end) {
                currNode = currNode.parent;
                continue;
            }
            if (currNode.type === NodeType.Declarations) {
                result.push([currNode.offset + 1, currNode.end - 1]);
            }
            else {
                result.push([currNode.offset, currNode.end]);
            }
            currNode = currNode.parent;
        }
        return result;
    }
}
