/*
 * Copyright (c) 2024 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
const workbench = angular.module('workbench', ['platformView', 'platformLayout', 'blimpKit']);
workbench.controller('WorkbenchController', ($scope) => {
    const contextMenuApi = new ContextMenuApi();
    const workspaceApi = new WorkspaceApi();
    let rightClickTabId;

    $scope.layoutConfig = {
        views: ['welcome', 'projects', 'import', 'search', 'properties', 'console', 'preview', 'problems'],
        viewSettings: {},
        layoutSettings: {
            hideCenterPane: false,
            leftPaneMinSize: 355
        },
    };

    $scope.showContextMenu = (event) => {
        event.preventDefault();
        if (event.target.tagName !== 'LI') {
            let closest = event.target.closest('li');
            if (closest && closest.hasAttribute('tab-id') && closest.hasAttribute('data-file-path')) {
                rightClickTabId = closest.getAttribute('data-file-path');
            } else return;
        } else {
            if (event.target.hasAttribute('tab-id') && event.target.hasAttribute('data-file-path')) {
                rightClickTabId = event.target.getAttribute('data-file-path');
            } else return;
        }
        contextMenuApi.showContextMenu({
            ariaLabel: 'editor tab contextmenu',
            posX: event.clientX,
            posY: event.clientY,
            icons: false,
            items: [
                {
                    id: 'close',
                    label: 'Close',
                },
                {
                    id: 'closeOthers',
                    label: 'Close Others',
                },
                {
                    id: 'closeAll',
                    label: 'Close All',
                    separator: true,
                },
                {
                    id: 'reveal',
                    label: 'Reveal in Projects',
                }
            ]
        }).then((id) => {
            const path = rightClickTabId.substring(rightClickTabId.indexOf('/', rightClickTabId.indexOf('/') + 1), rightClickTabId.length);
            const workspace = rightClickTabId.substring(1, rightClickTabId.length - path.length);
            if (id === 'reveal') {
                contextMenuApi.postMessage({ topic: 'projects.tree.select', data: { filePath: rightClickTabId } });
            } else if (id === 'close') {
                workspaceApi.closeFile({
                    path: path,
                    workspace: workspace,
                });
            } else if (id === 'closeOthers') {
                workspaceApi.closeFile({
                    path: path,
                    workspace: workspace,
                    params: { closeOthers: true }
                });
            } else if (id === 'closeAll') {
                workspaceApi.closeAllFiles();
            }
        });
    };
});