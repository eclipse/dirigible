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
import { TemplateInstance } from '../lib/template-instance.js';
import { directive, NodePart, reparentNodes, TemplateResult } from '../lit-html.js';
const templateCaches = new WeakMap();
/**
 * Enables fast switching between multiple templates by caching the DOM nodes
 * and TemplateInstances produced by the templates.
 *
 * Example:
 *
 * ```
 * let checked = false;
 *
 * html`
 *   ${cache(checked ? html`input is checked` : html`input is not checked`)}
 * `
 * ```
 */
export const cache = directive((value) => (part) => {
    if (!(part instanceof NodePart)) {
        throw new Error('cache can only be used in text bindings');
    }
    let templateCache = templateCaches.get(part);
    if (templateCache === undefined) {
        templateCache = new WeakMap();
        templateCaches.set(part, templateCache);
    }
    const previousValue = part.value;
    // First, can we update the current TemplateInstance, or do we need to move
    // the current nodes into the cache?
    if (previousValue instanceof TemplateInstance) {
        if (value instanceof TemplateResult &&
            previousValue.template === part.options.templateFactory(value)) {
            // Same Template, just trigger an update of the TemplateInstance
            part.setValue(value);
            return;
        }
        else {
            // Not the same Template, move the nodes from the DOM into the cache.
            let cachedTemplate = templateCache.get(previousValue.template);
            if (cachedTemplate === undefined) {
                cachedTemplate = {
                    instance: previousValue,
                    nodes: document.createDocumentFragment(),
                };
                templateCache.set(previousValue.template, cachedTemplate);
            }
            reparentNodes(cachedTemplate.nodes, part.startNode.nextSibling, part.endNode);
        }
    }
    // Next, can we reuse nodes from the cache?
    if (value instanceof TemplateResult) {
        const template = part.options.templateFactory(value);
        const cachedTemplate = templateCache.get(template);
        if (cachedTemplate !== undefined) {
            // Move nodes out of cache
            part.setValue(cachedTemplate.nodes);
            part.commit();
            // Set the Part value to the TemplateInstance so it'll update it.
            part.value = cachedTemplate.instance;
        }
    }
    part.setValue(value);
});
//# sourceMappingURL=cache.js.map