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
class LayoutHub extends MessageHubApi {
    /**
     * Returns a list of the currently opened views.
     * @param {object} [params] - Custom parameters.
     */ // @ts-ignore
    getOpenedViews({ params } = {}) {
        const callbackTopic = `platform.layout.views.opened.${new Date().valueOf()}`;
        this.postMessage({
            topic: 'platform.layout.views.opened',
            data: {
                topic: callbackTopic,
                params: params,
            },
        });
        return new Promise((resolve) => {
            const callbackListener = this.addMessageListener({
                topic: callbackTopic,
                handler: (data) => {
                    this.removeMessageListener(callbackListener);
                    resolve(data);
                }
            });
        });
    }

    /**
     * Triggered when a list of opened views is requested.
     * @param handler - Callback function.
     * @returns - A reference to the listener. In order to remove/disable the listener, you need to use this reference and pass it to the 'removeMessageListener' function.
     */
    onGetOpenedViews(handler) {
        return this.addMessageListener({ topic: 'platform.layout.views.opened', handler: handler });
    }

    /**
     * Returns true if a view is opened.
     * @param {string} id - View id.
     * @param {object} [params] - Custom parameters.
     */ // @ts-ignore
    isViewOpen({ id, params } = {}) {
        const callbackTopic = `platform.layout.views.is-open.${new Date().valueOf()}`;
        this.postMessage({
            topic: 'platform.layout.views.is-open',
            data: {
                topic: callbackTopic,
                id: id,
                params: params,
            },
        });
        return new Promise((resolve) => {
            const callbackListener = this.addMessageListener({
                topic: callbackTopic,
                handler: (data) => {
                    this.removeMessageListener(callbackListener);
                    resolve(data);
                }
            });
        });
    }

    /**
     * Triggered when a check for an opened view is requested.
     * @param handler - Callback function.
     * @returns - A reference to the listener. In order to remove/disable the listener, you need to use this reference and pass it to the 'removeMessageListener' function.
     */
    onIsViewOpen(handler) {
        return this.addMessageListener({ topic: 'platform.layout.views.is-open', handler: handler });
    }

    /**
     * Opens a view inside the layout. If the view is already opened, it will become visible.
     * @param {string} id - View id.
     * @param {object} [params] - Custom parameters that will be set to the view's data-parameters attribute.
     */ // @ts-ignore
    openView({ id, params } = {}) {
        this.postMessage({
            topic: 'platform.layout.view.open',
            data: {
                id: id,
                params: params,
            }
        });
    }

    /**
     * Triggered when a view should be opened.
     * @param handler - Callback function.
     * @returns - A reference to the listener. In order to remove/disable the listener, you need to use this reference and pass it to the 'removeMessageListener' function.
     */
    onOpenView(handler) {
        return this.addMessageListener({ topic: 'platform.layout.view.open', handler: handler });
    }

    /**
     * Shows a perspective inside the shell.
     * @param {string} id - Perspective id.
     * @param {object} [params] - Extra parameters.
     */ // @ts-ignore
    showPerspective({ id, params } = {}) {
        this.postMessage({
            topic: 'platform.shell.perspective.show',
            data: {
                id: id,
                params: params,
            }
        });
    }

    /**
     * Triggered when a perspective should be shown.
     * @param handler - Callback function.
     * @returns - A reference to the listener. In order to remove/disable the listener, you need to use this reference and pass it to the 'removeMessageListener' function.
     */
    onShowPerspective(handler) {
        return this.addMessageListener({ topic: 'platform.shell.perspective.show', handler: handler });
    }

    /**
     * Tells a view that it should gain focus from the inside.
     * @param {string} id - View id.
     * @param {object} [params] - Custom parameters that will be set to the view's data-parameters attribute.
     */
    focusView({ id, params } = {}) {
        this.postMessage({
            topic: 'platform.layout.view.focus',
            data: {
                id: id,
                params: params,
            }
        });
    }

    /**
     * Triggered when a view should gain focus.
     * @param handler - Callback function.
     * @returns - A reference to the listener. In order to remove/disable the listener, you need to use this reference and pass it to the 'removeMessageListener' function.
     */
    onFocusView(handler) {
        return this.addMessageListener({ topic: 'platform.layout.view.focus', handler: handler });
    }

    /**
     * Closes a view inside the layout.
     * @param {string} id - View id.
     * @param {object} [params] - Custom parameters.
     */ // @ts-ignore
    closeView({ id, params } = {}) {
        this.postMessage({
            topic: 'platform.layout.view.close',
            data: {
                id: id,
                params: params,
            }
        });
    }

    /**
     * Triggered when a view should be closed.
     * @param handler - Callback function.
     * @returns - A reference to the listener. In order to remove/disable the listener, you need to use this reference and pass it to the 'removeMessageListener' function.
     */
    onCloseView(handler) {
        return this.addMessageListener({ topic: 'platform.layout.view.close', handler: handler });
    }

    /**
     * Closes all other views inside the layout.
     * @param {string} id - View id.
     * @param {object} [params] - Custom parameters.
     */ // @ts-ignore
    closeOtherViews({ id, params } = {}) {
        this.postMessage({
            topic: 'platform.layout.view.close.others',
            data: {
                id: id,
                params: params,
            }
        });
    }

    /**
     * Triggered when other views should be closed.
     * @param handler - Callback function.
     * @returns - A reference to the listener. In order to remove/disable the listener, you need to use this reference and pass it to the 'removeMessageListener' function.
     */
    onCloseOtherViews(handler) {
        return this.addMessageListener({ topic: 'platform.layout.view.close.others', handler: handler });
    }

    /**
     * Closes all views inside the layout.
     * @param {object} [params] - Custom parameters.
     */ // @ts-ignore
    closeAllViews({ params } = {}) {
        this.postMessage({
            topic: 'platform.layout.view.close.all',
            data: {
                params: params
            }
        });
    }

    /**
     * Triggered when all views should be closed.
     * @param handler - Callback function.
     * @returns - A reference to the listener. In order to remove/disable the listener, you need to use this reference and pass it to the 'removeMessageListener' function.
     */
    onCloseAllViews(handler) {
        return this.addMessageListener({ topic: 'platform.layout.view.close.all', handler: handler });
    }

    /**
     * Marks a view in the layout as dirty.
     * @param {string} id - View id.
     * @param {boolean} dirty - Dirty state.
     * @param {object} [params] - Custom parameters.
     */ // @ts-ignore
    setViewDirty({ id, dirty, params } = {}) {
        this.postMessage({
            topic: 'platform.layout.view.dirty',
            data: {
                id: id,
                dirty: dirty,
                params: params,
            }
        });
    }

    /**
     * Triggered when a vie's dirty state should be changed.
     * @param handler - Callback function.
     * @returns - A reference to the listener. In order to remove/disable the listener, you need to use this reference and pass it to the 'removeMessageListener' function.
     */
    onViewDirty(handler) {
        return this.addMessageListener({ topic: 'platform.layout.view.dirty', handler: handler });
    }
}