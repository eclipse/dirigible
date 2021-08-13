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
import { Event as BaseEvent, Emitter } from '../common/event.js';
export var domEvent = function (element, type, useCapture) {
    var fn = function (e) { return emitter.fire(e); };
    var emitter = new Emitter({
        onFirstListenerAdd: function () {
            element.addEventListener(type, fn, useCapture);
        },
        onLastListenerRemove: function () {
            element.removeEventListener(type, fn, useCapture);
        }
    });
    return emitter.event;
};
export function stop(event) {
    return BaseEvent.map(event, function (e) {
        e.preventDefault();
        e.stopPropagation();
        return e;
    });
}
