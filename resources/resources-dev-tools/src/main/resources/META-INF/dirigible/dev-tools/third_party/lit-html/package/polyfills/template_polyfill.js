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
import { removeNodes, reparentNodes } from '../lib/dom.js';
/**
 * A lightweight <template> polyfill that supports minimum features to cover
 * lit-html use cases. It provides an alternate route in case <template> is not
 * natively supported.
 * Please note that nested template, cloning template node and innerHTML getter
 * do NOT work with this polyfill.
 * If it can not fullfill your requirement, please consider using the full
 * polyfill: https://github.com/webcomponents/template
 */
export const initTemplatePolyfill = (forced = false) => {
    // Minimal polyfills (like this one) may provide only a subset of Template's
    // functionality. So, we explicitly check that at least content is present to
    // prevent installing patching with multiple polyfills, which might happen if
    // multiple versions of lit-html were included on a page.
    if (!forced && 'content' in document.createElement('template')) {
        return;
    }
    const contentDoc = document.implementation.createHTMLDocument('template');
    const body = contentDoc.body;
    const descriptor = {
        enumerable: true,
        configurable: true,
    };
    const upgrade = (template) => {
        const content = contentDoc.createDocumentFragment();
        Object.defineProperties(template, {
            content: Object.assign({}, descriptor, { get() {
                    return content;
                } }),
            innerHTML: Object.assign({}, descriptor, { set: function (text) {
                    body.innerHTML = text;
                    removeNodes(content, content.firstChild);
                    reparentNodes(content, body.firstChild);
                } }),
        });
    };
    const capturedCreateElement = Document.prototype.createElement;
    Document.prototype.createElement = function createElement(tagName, options) {
        const el = capturedCreateElement.call(this, tagName, options);
        if (el.tagName === 'TEMPLATE') {
            upgrade(el);
        }
        return el;
    };
};
//# sourceMappingURL=template_polyfill.js.map