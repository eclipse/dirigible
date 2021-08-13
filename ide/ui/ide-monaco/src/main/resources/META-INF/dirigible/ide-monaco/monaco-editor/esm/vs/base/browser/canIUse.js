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
import * as browser from './browser.js';
import * as platform from '../common/platform.js';
/**
 * Browser feature we can support in current platform, browser and environment.
 */
export var BrowserFeatures = {
    clipboard: {
        writeText: (platform.isNative
            || (document.queryCommandSupported && document.queryCommandSupported('copy'))
            || !!(navigator && navigator.clipboard && navigator.clipboard.writeText)),
        readText: (platform.isNative
            || !!(navigator && navigator.clipboard && navigator.clipboard.readText)),
        richText: (function () {
            if (browser.isIE) {
                return false;
            }
            if (browser.isEdge) {
                var index = navigator.userAgent.indexOf('Edge/');
                var version = parseInt(navigator.userAgent.substring(index + 5, navigator.userAgent.indexOf('.', index)), 10);
                if (!version || (version >= 12 && version <= 16)) {
                    return false;
                }
            }
            return true;
        })()
    },
    keyboard: (function () {
        if (platform.isNative || browser.isStandalone) {
            return 0 /* Always */;
        }
        if (navigator.keyboard || browser.isSafari) {
            return 1 /* FullScreen */;
        }
        return 2 /* None */;
    })(),
    touch: 'ontouchstart' in window || navigator.maxTouchPoints > 0 || window.navigator.msMaxTouchPoints > 0,
    pointerEvents: window.PointerEvent && ('ontouchstart' in window || window.navigator.maxTouchPoints > 0 || navigator.maxTouchPoints > 0 || window.navigator.msMaxTouchPoints > 0)
};
