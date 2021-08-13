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
/**
 *
 * Main lit-html module.
 *
 * Main exports:
 *
 * -  [[html]]
 * -  [[svg]]
 * -  [[render]]
 *
 * @module lit-html
 * @preferred
 */
/**
 * Do not remove this comment; it keeps typedoc from misplacing the module
 * docs.
 */
import { defaultTemplateProcessor } from './lib/default-template-processor.js';
import { SVGTemplateResult, TemplateResult } from './lib/template-result.js';
export { DefaultTemplateProcessor, defaultTemplateProcessor } from './lib/default-template-processor.js';
export { directive, isDirective } from './lib/directive.js';
// TODO(justinfagnani): remove line when we get NodePart moving methods
export { removeNodes, reparentNodes } from './lib/dom.js';
export { noChange, nothing } from './lib/part.js';
export { AttributeCommitter, AttributePart, BooleanAttributePart, EventPart, isIterable, isPrimitive, NodePart, PropertyCommitter, PropertyPart } from './lib/parts.js';
export { parts, render } from './lib/render.js';
export { templateCaches, templateFactory } from './lib/template-factory.js';
export { TemplateInstance } from './lib/template-instance.js';
export { SVGTemplateResult, TemplateResult } from './lib/template-result.js';
export { createMarker, isTemplatePartActive, Template } from './lib/template.js';
// IMPORTANT: do not change the property name or the assignment expression.
// This line will be used in regexes to search for lit-html usage.
// TODO(justinfagnani): inject version number at build time
(window['litHtmlVersions'] || (window['litHtmlVersions'] = [])).push('1.1.2');
/**
 * Interprets a template literal as an HTML template that can efficiently
 * render to and update a container.
 */
export const html = (strings, ...values) => new TemplateResult(strings, values, 'html', defaultTemplateProcessor);
/**
 * Interprets a template literal as an SVG template that can efficiently
 * render to and update a container.
 */
export const svg = (strings, ...values) => new SVGTemplateResult(strings, values, 'svg', defaultTemplateProcessor);
//# sourceMappingURL=lit-html.js.map