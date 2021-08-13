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
import { coalesce } from '../../../base/common/arrays.js';
import { CancellationToken } from '../../../base/common/cancellation.js';
import { onUnexpectedExternalError } from '../../../base/common/errors.js';
import { registerModelAndPositionCommand } from '../../browser/editorExtensions.js';
import { HoverProviderRegistry } from '../../common/modes.js';
export function getHover(model, position, token) {
    var supports = HoverProviderRegistry.ordered(model);
    var promises = supports.map(function (support) {
        return Promise.resolve(support.provideHover(model, position, token)).then(function (hover) {
            return hover && isValid(hover) ? hover : undefined;
        }, function (err) {
            onUnexpectedExternalError(err);
            return undefined;
        });
    });
    return Promise.all(promises).then(coalesce);
}
registerModelAndPositionCommand('_executeHoverProvider', function (model, position) { return getHover(model, position, CancellationToken.None); });
function isValid(result) {
    var hasRange = (typeof result.range !== 'undefined');
    var hasHtmlContent = typeof result.contents !== 'undefined' && result.contents && result.contents.length > 0;
    return hasRange && hasHtmlContent;
}
