/*
 * Copyright (c) 2010-2024 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
class StatusBarApi extends MessageHubApi {
    /**
     * Shows a message in the status bar.
     * @param {string} message - Message text.
     */
    showMessage(message) {
        this.postMessage({
            topic: 'platform.shell.status.message',
            data: message
        });
    }

    /**
     * Shows an error message in the status bar.
     * @param {string} message - Message text.
     */
    showError(message) {
        this.postMessage({
            topic: 'platform.shell.status.error',
            data: message
        });
    }

    /**
     * Shows a busy indicator in the status bar.
     * @param {string} text - Status text for the busy indicator.
     */
    showBusy(text) {
        this.postMessage({
            topic: 'platform.shell.status.busy',
            data: text
        });
    }

    /**
     * Hides the busy indicator.
     */
    hideBusy() {
        this.triggerEvent('platform.shell.status.busy');
    }

    /**
     * Shows a text label at the end of status bar.
     * @param {string} label - Label text.
     */
    showLabel(label) {
        this.postMessage({
            topic: 'platform.shell.status.label',
            data: label
        });
    }
}