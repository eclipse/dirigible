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
const projectsView = angular.module('projects', ['blimpKit', 'platformView', 'platformShortcuts', 'platformEditors', 'WorkspaceService', 'PublisherService', 'TemplatesService', 'GenerateService', 'TransportService', 'ActionsService']);
projectsView.constant('StatusBarAPI', new StatusBarApi());
projectsView.constant('DialogAPI', new DialogApi());
projectsView.constant('WorkspaceAPI', new WorkspaceApi());
projectsView.constant('ContextMenuAPI', new ContextMenuApi());
projectsView.controller('ProjectsViewController', function (
    $scope,
    $document,
    StatusBarAPI,
    DialogAPI,
    WorkspaceAPI,
    ContextMenuAPI,
    WorkspaceService,
    Editors,
    PublisherService,
    TemplatesService,
    GenerateService,
    TransportService,
    ActionsService,
    clientOS,
    ButtonStates) {
    $scope.state = {
        isBusy: true,
        error: false,
        busyText: 'Loading...',
    };
    const inMacOS = clientOS.isMac();
    $scope.searchVisible = false;
    $scope.searchField = { text: '' };
    $scope.workspaceNames = [];
    $scope.menuTemplates = [];
    $scope.genericTemplates = [];
    $scope.modelTemplates = [];
    $scope.modelTemplateExtensions = [];
    $scope.gmodel = {
        nodeParents: [],
        templateId: '',
        project: '',
        model: '',
        parameters: {},
    };
    $scope.newNodeData = {
        parent: '',
        path: '',
        content: '',
    };
    $scope.actionData = {
        workspace: '',
        project: '',
        action: '',
    };
    $scope.duplicateProjectData = {};
    $scope.imageFileExts = ['ico', 'bmp', 'png', 'jpg', 'jpeg', 'gif', 'svg'];
    $scope.modelFileExts = ['extension', 'extensionpoint', 'edm', 'model', 'dsm', 'schema', 'bpmn', 'job', 'listener', 'websocket', 'roles', 'constraints', 'table', 'view'];

    $scope.selectedWorkspace = WorkspaceService.getCurrentWorkspace();

    $scope.projects = [];

    $scope.jstreeWidget = angular.element('#pvtree');
    $scope.dndCancel = false;
    $scope.spinnerObj = {
        text: 'Loading...',
        type: 'spinner',
        li_attr: { spinner: true },
    };
    $scope.jstreeConfig = {
        core: {
            check_callback: (operation) => {
                if (operation == 'move_node' && $scope.dndCancel) return false;
                return true;
            },
            themes: {
                name: 'fiori',
                variant: 'compact',
            },
            data: (node, cb) => {
                if (node.id === '#') cb($scope.projects);
                else WorkspaceService.loadContent(node.data.path).then((response) => {
                    if (response.status === 200) {
                        let children;
                        if (response.data.folders && response.data.files) {
                            children = processChildren(response.data.folders.concat(response.data.files));
                        } else if (response.data.folders) {
                            children = processChildren(response.data.folders);
                        } else if (response.data.files) {
                            children = processChildren(response.data.files);
                        }
                        cb(children);
                    } else {
                        StatusBarAPI.showError(`There was an error while refreshing the contents of '${node.data.path}'`);
                        console.error(response);
                        $scope.reloadWorkspace();
                    }
                });
            },
            keyboard: { // We have to have this in order to disable the default behavior.
                'enter': () => { },
                'f2': () => { },
            }
        },
        search: {
            case_sensitive: false,
        },
        plugins: ['wholerow', 'dnd', 'search', 'state', 'types', 'indicator'],
        dnd: {
            touch: 'selected',
            copy: false,
            blank_space_drop: false,
            large_drop_target: true,
            large_drag_target: true,
            is_draggable: (nodes) => {
                for (let i = 0; i < nodes.length; i++) {
                    if (nodes[i].type === 'project') return false;
                }
                return true;
            }
        },
        state: { key: `${brandingInfo.keyPrefix}.view-projects.state` },
        types: {
            '#': {
                valid_children: ['project']
            },
            'default': {
                icon: 'sap-icon--question-mark',
                valid_children: [],
            },
            file: {
                icon: 'jstree-file',
                valid_children: [],
            },
            folder: {
                icon: 'jstree-folder',
                valid_children: ['folder', 'file', 'spinner'],
            },
            project: {
                icon: 'jstree-project',
                valid_children: ['folder', 'file', 'spinner'],
            },
            spinner: {
                icon: 'jstree-spinner',
                valid_children: [],
            },
        },
    };

    $scope.keyboardShortcuts = (keySet, event) => {
        event.preventDefault();
        let nodes;
        switch (keySet) {
            case 'enter':
                const focused = $scope.jstreeWidget.jstree(true).get_node(document.activeElement);
                if (focused && !focused.state.selected) {
                    $scope.jstreeWidget.jstree(true).deselect_all();
                    $scope.jstreeWidget.jstree(true).select_node(focused);
                    openSelected([focused]);
                } else openSelected();
                break;
            case 'shift+enter':
                const toSelect = $scope.jstreeWidget.jstree(true).get_node(document.activeElement);
                if (toSelect && !toSelect.state.selected) $scope.jstreeWidget.jstree(true).select_node(toSelect);
                else $scope.jstreeWidget.jstree(true).deselect_node(toSelect);
                break;
            case 'f2':
                nodes = $scope.jstreeWidget.jstree(true).get_selected(true);
                if (nodes.length === 1) openRenameDialog(angular.copy(nodes[0]));
                break;
            case 'delete':
            case 'meta+backspace':
                nodes = $scope.jstreeWidget.jstree(true).get_top_selected(true);
                openDeleteDialog(nodes);
                break;
            case 'ctrl+f':
                $scope.$apply(() => $scope.toggleSearch());
                break;
            case 'ctrl+c':
                $scope.jstreeWidget.jstree(true).copy($scope.jstreeWidget.jstree(true).get_top_selected(true));
                break;
            case 'ctrl+x':
                $scope.jstreeWidget.jstree(true).cut($scope.jstreeWidget.jstree(true).get_top_selected(true));
                break;
            case 'ctrl+v':
                nodes = $scope.jstreeWidget.jstree(true).get_selected(true);
                if (nodes.length === 1 && $scope.jstreeWidget.jstree(true).can_paste()) {
                    if (nodes[0].type === 'folder' || nodes[0].type === 'project') {
                        $scope.jstreeWidget.jstree(true).paste(nodes[0]);
                    }
                }
                break;
            default:
                break;
        }
    };

    $scope.jstreeWidget.on('select_node.jstree', (_event, data) => {
        if (data.event && data.event.type === 'click' && data.node.type === 'file') {
            WorkspaceAPI.announceFileSelected({
                path: data.node.data.path,
                contentType: data.node.data.contentType,
                params: { workspace: $scope.selectedWorkspace.name },
            });
        }
    });

    $scope.jstreeWidget.on('dblclick.jstree', (event) => {
        const node = $scope.jstreeWidget.jstree(true).get_node(event.target);
        if (node.type === 'file') openFile(node);
    });

    $scope.jstreeWidget.on('paste.jstree', (_event, pasteObj) => {
        const parent = $scope.jstreeWidget.jstree(true).get_node(pasteObj.parent);
        const spinnerId = showSpinner(parent);
        const targetPath = (parent.data.path.endsWith('/') ? parent.data.path : parent.data.path + '/');
        let pasteArray = [];
        for (let i = 0; i < pasteObj.node.length; i++) {
            if (pasteObj.node[i].data.path === targetPath) return;
            pasteArray.push(pasteObj.node[i].data.path);
        }
        function onResponse(response) {
            if (response.status === 200) { // Move
                WorkspaceAPI.getCurrentlyOpenedFiles().then((result) => {
                    for (let r = 0; r < response.data.length; r++) {
                        for (let f = 0; f < result.length; f++) {
                            if (result[f].startsWith(response.data[r].from)) {
                                WorkspaceAPI.announceFileMoved({
                                    newPath: result[f].replace(response.data[r].from, response.data[r].to),
                                    oldPath: result[f],
                                });
                            }
                        }
                    }
                });
                $scope.jstreeWidget.jstree(true).load_node(parent);
            } else if (response.status === 201) { // Copy
                $scope.jstreeWidget.jstree(true).load_node(parent);
            } else {
                console.error(response);
                $scope.reloadWorkspace();
                if (pasteObj.mode !== 'copy_node' && pasteObj.node.length > 1)
                    StatusBarAPI.showError(`Unable to move ${pasteObj.node.length} objects.`);
                else if (pasteObj.mode !== 'copy_node' && pasteObj.node.length === 1)
                    StatusBarAPI.showError(`Unable to move '${pasteObj.node[0].text}'.`);
                if (pasteObj.mode === 'copy_node' && pasteObj.node.length > 1)
                    StatusBarAPI.showError(`Unable to copy ${pasteObj.node.length} objects.`);
                else if (pasteObj.mode === 'copy_node' && pasteObj.node.length === 1)
                    StatusBarAPI.showError(`Unable to copy '${pasteObj.node[0].text}'.`);
            }
            hideSpinner(spinnerId);
        }
        if (pasteObj.mode === 'copy_node') {
            WorkspaceService.copy(pasteArray, targetPath).then(onResponse);
        } else {
            for (let pIndex = 0; pIndex < pasteObj.node.length; pIndex++) { // Temp solution
                for (let i = 0; i < parent.children.length; i++) {
                    const node = $scope.jstreeWidget.jstree(true).get_node(parent.children[i]);
                    if (node.text === pasteObj.node[pIndex].text && node.id !== pasteObj.node[pIndex].id) {
                        DialogAPI.showAlert({
                            title: 'Could not move',
                            message: 'The destination contains a file/folder with the same name.',
                            type: AlertTypes.Error,
                        });
                        $scope.reloadWorkspace();
                        return;
                    }
                }
            }
            WorkspaceService.move(
                pasteArray,
                pasteArray.length === 1 ? targetPath + pasteObj.node[0].text : targetPath,
            ).then(onResponse);
        }
    });

    $(document).bind('dnd_stop.vakata', (_e, data) => { //Triggered on drag complete
        const target = $scope.jstreeWidget.jstree(true).get_node(data.event.target);
        for (let i = 0; i < data.data.nodes.length; i++) {
            if (!target || target.children.includes(data.data.nodes[i])) {
                $scope.dndCancel = true;
                return;
            }
        }
        $scope.dndCancel = false;
        if (!data.data.nodes.includes(target.id)) {
            $scope.jstreeWidget.jstree(true).cut(data.data.nodes);
            $scope.jstreeWidget.jstree(true).paste(target);
            $scope.jstreeWidget.jstree(true).delete_node(data.data.nodes);
        } else $scope.dndCancel = true;
    });

    function setMenuTemplateItems(parent, menuItems) {
        let priorityTemplates = true;
        let childExtensions = {};
        const children = getChildrenNames(parent, 'file');
        for (let i = 0; i < children.length; i++) {
            let lastIndex = children[i].lastIndexOf('.');
            if (lastIndex !== -1) childExtensions[children[i].substring(lastIndex + 1)] = true;
        }
        for (let i = 0; i < $scope.menuTemplates.length; i++) {
            let item = {
                id: $scope.menuTemplates[i].id,
                label: $scope.menuTemplates[i].label,
            };
            if ($scope.menuTemplates[i].oncePerFolder) {
                if (childExtensions[$scope.menuTemplates[i].extension]) {
                    item.disabled = true;
                }
            }
            if (priorityTemplates && !$scope.menuTemplates[i].hasOwnProperty('order')) {
                item.separator = true;
                priorityTemplates = false;
            }
            menuItems[0].items.push(item);
        }
    }

    function getProjectNode(parents) {
        for (let i = 0; i < parents.length; i++) {
            if (parents[i] !== '#') {
                const parent = $scope.jstreeWidget.jstree(true).get_node(parents[i]);
                if (parent.type === 'project') {
                    return parent;
                }
            }
        }
    }

    function getChildrenNames(node, type = '') {
        const root = $scope.jstreeWidget.jstree(true).get_node(node);
        const names = [];
        if (type) {
            for (let i = 0; i < root.children.length; i++) {
                let child = $scope.jstreeWidget.jstree(true).get_node(root.children[i]);
                if (child.type === type) names.push(child.text);
            }
        } else {
            for (let i = 0; i < root.children.length; i++) {
                names.push($scope.jstreeWidget.jstree(true).get_text(root.children[i]));
            }
        }
        return names;
    }

    const contextMenuNodes = [];

    $scope.showContextMenu = (event) => {
        contextMenuNodes.length = 0;
        const items = [];
        if ($scope.jstreeWidget[0].contains(event.target)) {
            event.preventDefault();
            let id;
            if (event.target.tagName !== 'LI') {
                const closest = event.target.closest('li');
                if (closest) {
                    id = closest.id;
                } else {
                    items.push({
                        id: 'newProject',
                        label: 'New Project',
                        leftIconClass: 'sap-icon--create',
                        separator: true,
                    });
                    if (PublisherService.isEnabled()) {
                        items.push({
                            id: 'publishAll',
                            label: 'Publish All',
                            leftIconClass: 'sap-icon--arrow-top',

                        });
                        items.push({
                            id: 'unpublishAll',
                            label: 'Unpublish All',
                            leftIconClass: 'sap-icon--arrow-bottom',
                            separator: true,
                        });
                    }
                    items.push({
                        id: 'exportProjects',
                        label: 'Export all',
                        leftIconClass: 'sap-icon--download-from-cloud',
                    });
                }
            } else {
                id = event.target.id;
            }
            if (id) {
                const node = $scope.jstreeWidget.jstree(true).get_node(id);
                if (!node.state.selected) {
                    $scope.jstreeWidget.jstree(true).deselect_all();
                    $scope.jstreeWidget.jstree(true).select_node(node, false, true);
                }
                const nodes = $scope.jstreeWidget.jstree(true).get_selected(true);
                contextMenuNodes.push(...nodes);
                const newSubmenu = {
                    id: 'new',
                    label: 'New',
                    iconClass: 'sap-icon--create',
                    items: [{
                        id: 'file',
                        label: 'File',
                    }, {
                        id: 'folder',
                        label: 'Folder',
                        separator: true,
                    }],
                    separator: true,
                };
                const cutObj = {
                    id: 'cut',
                    label: 'Cut',
                    shortcut: inMacOS ? '⌘X' : 'Ctrl+X',
                    leftIconClass: 'sap-icon--scissors',
                };
                const copyObj = {
                    id: 'copy',
                    label: 'Copy',
                    shortcut: inMacOS ? '⌘C' : 'Ctrl+C',
                    leftIconClass: 'sap-icon--copy',
                    separator: true,
                };
                const pasteObj = {
                    id: 'paste',
                    label: 'Paste',
                    shortcut: inMacOS ? '⌘V' : 'Ctrl+V',
                    leftIconClass: 'sap-icon--paste',
                    disabled: !$scope.jstreeWidget.jstree(true).can_paste() || nodes.length > 1,
                    separator: true,
                };
                const renameObj = {
                    id: 'rename',
                    label: 'Rename',
                    shortcut: 'F2',
                    leftIconClass: 'sap-icon--edit',
                    disabled: nodes.length > 1,
                };
                const deleteObj = {
                    id: 'delete',
                    label: (nodes.length > 1) ? `Delete ${nodes.length} items` : 'Delete',
                    shortcut: inMacOS ? '⌘⌫' : 'Del',
                    leftIconClass: 'sap-icon--delete',
                    separator: true,
                };
                let publishObj;
                let unpublishObj;
                if (PublisherService.isEnabled()) {
                    publishObj = {
                        id: 'publish',
                        label: 'Publish',
                        leftIconClass: 'sap-icon--arrow-top',
                    };
                    unpublishObj = {
                        id: 'unpublish',
                        label: 'Unpublish',
                        leftIconClass: 'sap-icon--arrow-bottom',
                        separator: true,
                    };
                }
                let generateObj;
                if (nodes.length === 1 && GenerateService.isEnabled()) {
                    generateObj = {
                        id: 'generateGeneric',
                        label: 'Generate',
                        leftIconClass: 'sap-icon-TNT--operations',
                        separator: true,
                    };
                }
                const importObj = {
                    id: 'import',
                    label: 'Import',
                    leftIconClass: 'sap-icon--attachment',
                };
                const importZipObj = {
                    id: 'importZip',
                    label: 'Import from zip',
                    leftIconClass: 'sap-icon--attachment-zip-file',
                    separator: true,
                };
                if (node.type === 'project') {
                    if (nodes.length === 1) items.push(
                        newSubmenu,
                        {
                            id: 'duplicateProject',
                            label: 'Duplicate',
                            leftIconClass: 'sap-icon--duplicate',
                        },
                        pasteObj,
                        renameObj,
                        deleteObj,
                    );
                    else items.push(
                        deleteObj,
                    );
                    if (PublisherService.isEnabled()) {
                        items.push(publishObj);
                        items.push(unpublishObj);
                    }
                    if (nodes.length === 1 && generateObj && $scope.menuTemplates.length) {
                        items.push(generateObj);
                        setMenuTemplateItems(node.id, items);
                    }

                    if (nodes.length === 1) {
                        items.push(importObj);
                        items.push(importZipObj);
                        items.push({
                            id: 'exportProject',
                            label: 'Export',
                            leftIconClass: 'sap-icon--download-from-cloud',
                            separator: true,
                        });
                    }
                    if (nodes.length === 1 && ActionsService.isEnabled()) {
                        items.push({
                            id: 'actionsProject',
                            label: 'Actions',
                            leftIconClass: 'sap-icon--media-play',
                            separator: true,
                        });
                    }
                } else if (node.type === 'folder') {
                    items.push(
                        newSubmenu,
                        cutObj,
                        copyObj,
                        pasteObj,
                        renameObj,
                        deleteObj,
                        generateObj,
                        importObj,
                        importZipObj,
                    );
                    if (PublisherService.isEnabled()) {
                        items.push(publishObj);
                        items.push(unpublishObj);
                    }
                    setMenuTemplateItems(node.id, items);
                } else if (node.type === 'file') {
                    items.push(
                        {
                            id: 'open',
                            label: 'Open',
                            leftIconClass: 'sap-icon--action',
                            separator: nodes.length > 1,
                        },
                        cutObj,
                        copyObj,
                        renameObj,
                        deleteObj,
                    );
                    if (nodes.length <= 1) {
                        items.splice(1, 0, {
                            id: 'openWith',
                            label: 'Open With',
                            iconClass: 'sap-icon--action',
                            items: getEditorsForType(node),
                            separator: true,
                        });
                    }
                    if (PublisherService.isEnabled()) {
                        items.push(publishObj);
                        items.push(unpublishObj);
                    }
                    if (generateObj && GenerateService.isEnabled()) {
                        const fileExt = getFileExtension(node.text);
                        if (fileExt === 'gen') {
                            let regenObj = {
                                id: 'regenerateModel',
                                label: 'Regenerate',
                                leftIconClass: 'sap-icon--refresh',
                                separator: true,
                                disabled: false,
                            };
                            if (node.parents.length > 2) {
                                regenObj.disabled = true;
                            } else {
                                regenObj.data = node;
                            }
                            items.push(regenObj);
                        }
                        else if ($scope.modelTemplates.length && $scope.modelTemplateExtensions.includes(fileExt)) {
                            let genObj = {
                                id: 'generateModel',
                                label: 'Generate',
                                leftIconClass: 'sap-icon-TNT--operations',
                                separator: true,
                                disabled: false,
                            };
                            if (node.parents.length > 2) {
                                genObj.disabled = true;
                            } else {
                                genObj.data = node;
                            }
                            items.push(genObj);
                        }
                    }
                }
            }
            ContextMenuAPI.showContextMenu({
                ariaLabel: 'projects view contextmenu',
                posX: event.clientX,
                posY: event.clientY,
                icons: true,
                items: items
            }).then((id) => {
                if (id) {
                    if (id === 'open') {
                        if (contextMenuNodes.length > 1) {
                            openSelected(contextMenuNodes);
                        } else openFile(contextMenuNodes[0]);
                    } else if (id.startsWith('openWith')) {
                        const editorId = id.slice(9);
                        openFile(contextMenuNodes[0], editorId);
                    } else if (id === 'file') {
                        $scope.newNodeData.parent = contextMenuNodes[0].id;
                        $scope.newNodeData.path = contextMenuNodes[0].data.path;
                        $scope.newNodeData.content = '';
                        openNewFileDialog();
                    } else if (id === 'folder') {
                        $scope.newNodeData.parent = contextMenuNodes[0].id;
                        $scope.newNodeData.path = contextMenuNodes[0].data.path;
                        openNewFolderDialog();
                    } else if (id === 'cut') {
                        $scope.jstreeWidget.jstree(true).cut($scope.jstreeWidget.jstree(true).get_top_selected(true));
                    } else if (id === 'copy') {
                        $scope.jstreeWidget.jstree(true).copy($scope.jstreeWidget.jstree(true).get_top_selected(true));
                    } else if (id === 'paste') {
                        $scope.jstreeWidget.jstree(true).paste(contextMenuNodes[0]);
                    } else if (id === 'newProject') $scope.createProject();
                    else if (id === 'duplicateProject') {
                        $scope.duplicateProject(contextMenuNodes[0]);
                    } else if (id === 'rename') {
                        openRenameDialog(angular.copy(contextMenuNodes[0]));
                    } else if (id === 'delete') {
                        openDeleteDialog(contextMenuNodes);
                    } else if (id === 'publish') {
                        for (let i = 0; i < contextMenuNodes.length; i++) {
                            publish(contextMenuNodes[i].data.path, {
                                name: contextMenuNodes[i].text,
                                path: contextMenuNodes[i].data.path,
                                type: contextMenuNodes[i].type,
                            });
                        }
                    } else if (id === 'publishAll') {
                        $scope.publishAll();
                    } else if (id === 'unpublish') {
                        for (let i = 0; i < contextMenuNodes.length; i++) {
                            unpublish(contextMenuNodes[i].data.path, {
                                name: contextMenuNodes[i].text,
                                path: contextMenuNodes[i].data.path,
                                type: contextMenuNodes[i].type,
                            });
                        }
                    } else if (id === 'unpublishAll') {
                        unpublishAll();
                    } else if (id === 'import' || id === 'importZip') {
                        DialogAPI.showWindow({
                            hasHeader: true,
                            id: 'importWindow',
                            params: {
                                importType: id !== 'importZip' ? 'file' : 'zip',
                                uploadPath: contextMenuNodes[0].data.path,
                                projectsViewId: contextMenuNodes[0].id,
                                workspace: $scope.selectedWorkspace.name,
                            }
                        });
                    } else if (id === 'exportProjects') {
                        $scope.exportProjects();
                    } else if (id === 'exportProject') {
                        TransportService.exportProject($scope.selectedWorkspace.name, contextMenuNodes[0].text);
                    } else if (id === 'actionsProject') {
                        $scope.actionData.project = contextMenuNodes[0].text;
                        $scope.actionData.workspace = $scope.selectedWorkspace.name;
                        DialogAPI.showFormDialog({
                            title: 'Enter the action to execute',
                            form: {
                                'fdti1': {
                                    label: 'Action name',
                                    controlType: 'input',
                                    type: 'text',
                                    submitOnEnter: true,
                                    focus: true,
                                    required: true
                                }
                            },
                            submitLabel: 'Execute',
                            cancelLabel: 'Cancel'
                        }).then((form) => {
                            if (form) {
                                executeAction($scope.actionData.workspace, $scope.actionData.project, form['fdti1']);
                            }
                        }, (error) => {
                            console.error(error);
                        });
                    } else if (id === 'regenerateModel') {
                        DialogAPI.showBusyDialog('Regenerating...');
                        WorkspaceService.loadContent(contextMenuNodes[0].data.path).then((response) => {
                            if (response.status === 200) {
                                $scope.gmodel.nodeParents = contextMenuNodes[0].parents;
                                $scope.gmodel.project = response.data.projectName;
                                $scope.gmodel.model = response.data.filePath;
                                let { models, perspectives, templateId, filePath, workspaceName, projectName, ...params } = response.data;
                                $scope.gmodel.parameters = params;
                                if (!response.data.templateId) {
                                    DialogAPI.closeBusyDialog();
                                    DialogAPI.showFormDialog({
                                        title: 'Choose template',
                                        form: {
                                            'pgfd1': {
                                                label: 'Template',
                                                placeholder: 'Select template',
                                                controlType: 'input',
                                                type: 'text',
                                                required: true,
                                                options: getModelTemplates('model'),
                                            }
                                        },
                                        submitLabel: 'Regenerate',
                                        cancelLabel: 'Cancel'
                                    }).then((form) => {
                                        if (form) {
                                            $scope.gmodel.templateId = form['pgfd1'];
                                            DialogAPI.showBusyDialog('Regenerating from model...');
                                            generateModel(true);
                                        }
                                    }, (error) => {
                                        console.error(error);
                                    });
                                } else {
                                    $scope.gmodel.templateId = response.data.templateId;
                                    DialogAPI.showBusyDialog('Regenerating from model...');
                                    generateModel(true);
                                }
                            } else {
                                DialogAPI.closeBusyDialog();
                                DialogAPI.showAlert({
                                    title: 'Unable to load file',
                                    message: 'There was an error while loading the file. See the log for more information.',
                                    type: AlertTypes.Error,
                                    preformatted: false,
                                });
                                console.error(response);
                            }
                        });
                    } else if (id.startsWith('generate')) {
                        let project;
                        let projectNames = [];
                        let root = $scope.jstreeWidget.jstree(true).get_node('#');
                        for (let i = 0; i < root.children.length; i++) {
                            let name = $scope.jstreeWidget.jstree(true).get_text(root.children[i])
                            projectNames.push({
                                label: name,
                                value: name,
                            });
                        }
                        if (id === 'generateGeneric') {
                            let generatePath;
                            const nodeParents = contextMenuNodes[0].parents;
                            const templateItems = getGenericTemplates();
                            if (contextMenuNodes[0].type !== 'project') {
                                const pnode = getProjectNode(contextMenuNodes[0].parents);
                                project = pnode.text;
                                generatePath = contextMenuNodes[0].data.path.substring(pnode.text.length + 1);
                                if (generatePath.endsWith('/')) generatePath += 'filename';
                                else generatePath += '/filename';
                            } else {
                                project = contextMenuNodes[0].text;
                                generatePath = '/filename';
                            }
                            DialogAPI.showFormDialog({
                                title: 'Generate from template',
                                form: {
                                    'pgfd1': {
                                        label: 'Choose template',
                                        placeholder: 'Templates',
                                        controlType: 'dropdown',
                                        options: templateItems,
                                        required: true,
                                    },
                                    'pgfd2': {
                                        label: 'Choose project',
                                        placeholder: 'Projects',
                                        controlType: 'dropdown',
                                        value: project,
                                        options: projectNames,
                                        required: true,
                                    },
                                    'pgfi1': {
                                        label: 'File path in project',
                                        controlType: 'input',
                                        type: 'text',
                                        placeholder: '/path/file',
                                        value: generatePath,
                                        submitOnEnter: true,
                                        required: true
                                    },
                                },
                                submitLabel: 'Generate',
                                cancelLabel: 'Cancel'
                            }).then((form) => {
                                if (form) {
                                    let template;
                                    for (let i = 0; i < $scope.genericTemplates.length; i++) {
                                        if ($scope.genericTemplates[i].id === form['pgfd1']) {
                                            template = $scope.genericTemplates[i];
                                            break;
                                        }
                                    }
                                    GenerateService.generateFromTemplate(
                                        $scope.selectedWorkspace.name,
                                        form['pgfd2'],
                                        form['pgfi1'],
                                        template.id,
                                        template.parameters
                                    ).then((response) => {
                                        if (response.status === 201) {
                                            StatusBarAPI.showMessage('Successfully generated from template.');
                                            $scope.jstreeWidget.jstree(true).refresh_node(getProjectNode(nodeParents));
                                        } else {
                                            DialogAPI.showAlert({
                                                title: 'Failed to generate from template',
                                                message: `An unexpected error has occurred while trying generate from template '${template.name}'`,
                                                type: AlertTypes.Error,
                                                preformatted: false,
                                            });
                                            StatusBarAPI.showError(`Unable to generate from template '${template.name}'`);
                                        }
                                    });
                                }
                            }, (error) => {
                                console.error(error);
                                DialogAPI.showAlert({
                                    title: 'Failed to generate from template',
                                    message: 'An unexpected error has occurred.',
                                    type: AlertTypes.Error,
                                    preformatted: false,
                                });
                            });
                        } else if (id === 'generateModel') {
                            let pnode = getProjectNode(contextMenuNodes[0].parents);
                            project = pnode.text;
                            const templateItems = getModelTemplates(getFileExtension(contextMenuNodes[0].text));
                            DialogAPI.showFormDialog({
                                title: 'Generate from template',
                                form: {
                                    'pgfd1': {
                                        label: 'Choose template',
                                        placeholder: 'Templates',
                                        controlType: 'dropdown',
                                        options: templateItems,
                                        required: true,
                                    },
                                    'pgfd2': {
                                        label: 'Choose project',
                                        placeholder: 'Projects',
                                        controlType: 'dropdown',
                                        value: project,
                                        options: projectNames,
                                        required: true,
                                    },
                                    'pgfi1': {
                                        label: 'Model (must be in the root of the project)',
                                        controlType: 'input',
                                        type: 'text',
                                        placeholder: '/path/file',
                                        inputRules: {
                                            // excluded: [], // TODO
                                            patterns: ['^[^/:]*$'],
                                        },
                                        value: contextMenuNodes[0].data.path.substring(project.length + 2),
                                        submitOnEnter: true,
                                        required: true
                                    },
                                },
                                submitLabel: 'Generate',
                                cancelLabel: 'Cancel'
                            }).then((form) => generateFromModelHandler(form), (error) => generateFromModelErrorHandler(error));
                        }
                    } else {
                        let name;
                        let nameless;
                        let staticName;
                        let extension;
                        let content = '';
                        for (let i = 0; i < $scope.menuTemplates.length; i++) {
                            if ($scope.menuTemplates[i].id === id) {
                                name = $scope.menuTemplates[i].name;
                                content = $scope.menuTemplates[i].data || '';
                                extension = $scope.menuTemplates[i].extension;
                                nameless = $scope.menuTemplates[i].nameless || false;
                                staticName = $scope.menuTemplates[i].staticName || false;

                            }
                        }
                        if (nameless) {
                            createFile(
                                contextMenuNodes[0].id,
                                `.${extension}`,
                                contextMenuNodes[0].data.path,
                                content
                            );
                        } else {
                            let excludedNames = getChildrenNames(contextMenuNodes[0].id, 'file');
                            let i = 1;
                            if (extension) {
                                name = `${name}.${extension}`;
                                while (excludedNames.includes(name)) {
                                    name = `${name} ${i++}.${extension}`;
                                }
                            } else {
                                while (excludedNames.includes(name)) {
                                    name = `${name} ${i++}`;
                                }
                            }
                            if (staticName) {
                                createFile(
                                    contextMenuNodes[0].id,
                                    name,
                                    contextMenuNodes[0].data.path,
                                    content
                                );
                            } else {
                                $scope.newNodeData.parent = contextMenuNodes[0].id;
                                $scope.newNodeData.path = contextMenuNodes[0].data.path;
                                $scope.newNodeData.content = content;
                                openNewFileDialog(excludedNames, name);
                            }
                        }
                    }
                }
            });
        }
    };

    function generateFromModelHandler(form) {
        if (form) {
            DialogAPI.showBusyDialog('Generating...');
            if ($scope.gmodel.model === '') {
                $scope.gmodel.templateId = form['pgfd1'];
                $scope.gmodel.project = form['pgfd2'];
                $scope.gmodel.model = form['pgfi1'];
                const newForm = {};
                for (let i = 0; i < $scope.modelTemplates.length; i++) {
                    if ($scope.modelTemplates[i].id === $scope.gmodel.templateId) {
                        for (let j = 0; j < $scope.modelTemplates[i].parameters.length; j++) {
                            newForm[$scope.modelTemplates[i].parameters[j].name] = {
                                controlType: ($scope.modelTemplates[i].parameters[j].type === 'checkbox' ? 'checkbox' : 'input'),
                                type: 'text',
                                label: $scope.modelTemplates[i].parameters[j].label,
                                required: $scope.modelTemplates[i].parameters[j].required ?? true,
                                placeholder: $scope.modelTemplates[i].parameters[j].placeholder,
                                value: $scope.modelTemplates[i].parameters[j].value || ($scope.modelTemplates[i].parameters[j].type === 'checkbox' ? false : ''),
                            };
                            if ($scope.modelTemplates[i].parameters[j].hasOwnProperty('ui')) {
                                if ($scope.modelTemplates[i].parameters[j].ui.hasOwnProperty('hide')) {
                                    newForm[$scope.modelTemplates[i].parameters[j].name].hiddenOn = {
                                        key: $scope.modelTemplates[i].parameters[j].ui.hide.property,
                                        value: $scope.modelTemplates[i].parameters[j].ui.hide.value,
                                    };
                                }
                                // else {
                                //     newForm[$scope.modelTemplates[i].parameters[j].name].visibleOn = {
                                //         key: $scope.modelTemplates[i].parameters[j].ui.hide.property,
                                //         value: $scope.modelTemplates[i].parameters[j].ui.hide.value,
                                //     };
                                // }
                            }
                        }
                        break;
                    }
                }
                if (Object.keys(newForm).length > 0) {
                    DialogAPI.closeBusyDialog();
                    DialogAPI.showFormDialog({
                        title: 'Generate from template',
                        form: newForm,
                        submitLabel: 'Generate',
                        cancelLabel: 'Cancel'
                    }).then((form) => generateFromModelHandler(form), (error) => generateFromModelErrorHandler(error))
                } else {
                    generateModel();
                }
            } else {
                $scope.gmodel.parameters = {};
                const keys = Object.keys(form);
                for (let i = 0; i < keys.length; i++) {
                    if (form[keys[i]]) $scope.gmodel.parameters[keys[i]] = form[keys[i]];
                }
                generateModel();
            }
        } else {
            $scope.gmodel.model = '';
        }
    }

    function generateFromModelErrorHandler(error) {
        console.error(error);
        DialogAPI.showAlert({
            title: 'Failed to generate from template',
            message: 'An unexpected error has occurred.',
            type: AlertTypes.Error,
            preformatted: false,
        });
    }

    $scope.toggleSearch = () => {
        $scope.searchField.text = '';
        $scope.jstreeWidget.jstree(true).clear_search();
        $scope.searchVisible = !$scope.searchVisible;
    };

    $scope.isSelectedWorkspace = (name) => {
        if ($scope.selectedWorkspace.name === name) return true;
        return false;
    };

    $scope.reloadWorkspaceList = () => {
        WorkspaceService.listWorkspaceNames().then((response) => {
            if (response.status === 200) {
                $scope.workspaceNames = response.data;
                $scope.state.error = false;
                $scope.reloadWorkspace(true);
                $scope.loadTemplates();
            } else {
                $scope.state.error = true;
                $scope.errorMessage = 'Unable to load workspace list.';
                StatusBarAPI.showError('Unable to load workspace list');
            }
        });
    };

    $scope.reloadWorkspace = (setConfig = false) => {
        $scope.projects.length = 0;
        $scope.state.isBusy = true;
        WorkspaceService.list($scope.selectedWorkspace.name).then((response) => {
            if (response.status === 200) {
                for (let i = 0; i < response.data.projects.length; i++) {
                    let project = {
                        text: response.data.projects[i].name,
                        type: response.data.projects[i].type,
                        data: {
                            git: response.data.projects[i].git,
                            gitName: response.data.projects[i].gitName,
                            path: response.data.projects[i].path,
                        },
                        li_attr: { git: response.data.projects[i].git },
                    };
                    if (response.data.projects[i].folders && response.data.projects[i].files) {
                        project['children'] = processChildren(response.data.projects[i].folders.concat(response.data.projects[i].files));
                    } else if (response.data.projects[i].folders) {
                        project['children'] = processChildren(response.data.projects[i].folders);
                    } else if (response.data.projects[i].files) {
                        project['children'] = processChildren(response.data.projects[i].files);
                    }
                    $scope.projects.push(project);
                }
                $scope.state.isBusy = false;
                if (setConfig) $scope.jstreeWidget.jstree($scope.jstreeConfig);
                else $scope.jstreeWidget.jstree(true).refresh();
            } else {
                $scope.state.isBusy = false;
                $scope.state.error = true;
                $scope.errorMessage = 'Unable to load workspace data.';
                StatusBarAPI.showError('Unable to load workspace data');
            }
        });
    };

    $scope.loadTemplates = () => {
        $scope.menuTemplates.length = 0;
        $scope.genericTemplates.length = 0;
        $scope.modelTemplates.length = 0;
        $scope.modelTemplateExtensions.length = 0;
        TemplatesService.menuTemplates().then((response) => {
            if (response.status === 200) {
                $scope.menuTemplates = response.data
                for (let i = 0; i < response.data.length; i++) {
                    if (response.data[i].isModel) {
                        $scope.modelFileExts.push(response.data[i].extension);
                    } else if (response.data[i].isImage) {
                        $scope.imageFileExts.push(response.data[i].extension);
                    }
                }
            } else StatusBarAPI.showError('Unable to load menu template list');
        });
        TemplatesService.listTemplates().then((response) => {
            if (response.status === 200) {
                for (let i = 0; i < response.data.length; i++) {
                    if (response.data[i].hasOwnProperty('extension')) {
                        $scope.modelTemplates.push(response.data[i]);
                        $scope.modelTemplateExtensions.push(response.data[i].extension);
                    } else {
                        $scope.genericTemplates.push(response.data[i]);
                    }
                }
            } else StatusBarAPI.showError('Unable to load template list');
        });
    };

    $scope.publishAll = () => {
        StatusBarAPI.showBusy('Publishing projects...');
        PublisherService.publish(`/${$scope.selectedWorkspace.name}/*`).then((response) => {
            if (response.status !== 200)
                StatusBarAPI.showError(`Unable to publish projects in '${$scope.selectedWorkspace.name}'`);
            else StatusBarAPI.showMessage(`Published all projects in '${$scope.selectedWorkspace.name}'`);
            StatusBarAPI.hideBusy();
            WorkspaceAPI.announcePublished({
                path: `/${$scope.selectedWorkspace.name}`
            });
        });
    };

    function unpublishAll() {
        StatusBarAPI.showBusy('Unpublishing projects...');
        PublisherService.unpublish(`/${$scope.selectedWorkspace.name}/*`).then((response) => {
            if (response.status !== 200)
                StatusBarAPI.showError(`Unable to unpublish projects in '${$scope.selectedWorkspace.name}'`);
            else StatusBarAPI.showMessage(`Unpublished all projects in '${$scope.selectedWorkspace.name}'`);
            StatusBarAPI.hideBusy();
            WorkspaceAPI.announceUnpublished({
                path: `/${$scope.selectedWorkspace.name}`
            });
        });
    };

    function publish(path, fileDescriptor, callback) {
        StatusBarAPI.showBusy(`Publishing '${path}'...`);
        PublisherService.publish(path).then((response) => {
            if (response.status !== 200) {
                StatusBarAPI.showError(`Unable to publish '${path}'`);
            } else {
                StatusBarAPI.showMessage(`Published '${path}'`);
                if (callback) callback();
            }
            StatusBarAPI.hideBusy();
            if (fileDescriptor) WorkspaceAPI.announcePublished(fileDescriptor);
        });
    };

    function unpublish(path, fileDescriptor, callback) {
        StatusBarAPI.showBusy(`Unpublishing '${path}'...`);
        PublisherService.unpublish(path).then((response) => {
            if (response.status !== 200) {
                StatusBarAPI.showError(`Unable to unpublish '${path}'`);
            } else {
                StatusBarAPI.showMessage(`Unpublished '${path}'`);
                if (callback) callback();
            }
            StatusBarAPI.hideBusy();
            if (fileDescriptor) WorkspaceAPI.announceUnpublished(fileDescriptor);
        });
    };

    $scope.switchWorkspace = (workspace) => {
        if ($scope.selectedWorkspace.name !== workspace) {
            $scope.selectedWorkspace.name = workspace;
            WorkspaceService.setWorkspace(workspace);
            $scope.reloadWorkspace();
        }
    };

    $scope.isPublishEnabled = () => PublisherService.isEnabled();

    $scope.saveAll = () => WorkspaceAPI.saveAll();

    function deleteFileFolder(path, nodeId, type) {
        WorkspaceService.remove(path).then((response) => {
            if (response.status !== 204) {
                StatusBarAPI.showMessage(`Unable to delete '${path}'.`);
            } else {
                StatusBarAPI.showMessage(`Deleted '${path}'.`);
                if (type === 'file') WorkspaceAPI.announceFileDeleted({
                    path: path,
                });
                else WorkspaceAPI.announceFolderDeleted({
                    path: path,
                });
                $scope.jstreeWidget.jstree(true).delete_node(nodeId);
            }
        });
    };

    function deleteProject(workspace, project, nodeId) {
        WorkspaceService.deleteProject(workspace, project).then((response) => {
            if (response.status !== 204) {
                StatusBarAPI.showError(`Unable to delete '${project}'.`);
            } else {
                StatusBarAPI.showMessage(`Deleted '${project}'.`);
                WorkspaceAPI.announceProjectDeleted({ project: project, workspace: workspace });
                $scope.jstreeWidget.jstree(true).delete_node(nodeId);
            }
        });
    };

    $scope.exportProjects = () => {
        TransportService.exportProject($scope.selectedWorkspace.name, '*');
    };

    $scope.createProject = () => {
        DialogAPI.showFormDialog({
            title: 'Create project',
            form: {
                'pgfi1': {
                    label: 'Name',
                    controlType: 'input',
                    type: 'text',
                    placeholder: 'project name',
                    required: true,
                    submitOnEnter: true,
                    focus: true,
                    inputRules: {
                        excluded: getChildrenNames('#'),
                        patterns: ['^[^/:]*$'],
                    },
                }
            },
            submitLabel: 'Create',
            cancelLabel: 'Cancel',
        }).then((form) => {
            if (form) {
                DialogAPI.showBusyDialog('Creating...');
                WorkspaceService.createProject($scope.selectedWorkspace.name, form['pgfi1']).then((response) => {
                    DialogAPI.closeBusyDialog();
                    if (response.status !== 201) {
                        DialogAPI.showAlert({
                            title: 'Failed to create project',
                            message: `An unexpected error has occurred while trying create a project named '${form['pgfi1']}'`,
                            type: AlertTypes.Error,
                            preformatted: false,
                        });
                        StatusBarAPI.showError(`Unable to create project '${form['pgfi1']}'`);
                    } else {
                        StatusBarAPI.showMessage(`Created project '${form['pgfi1']}'`);
                        $scope.reloadWorkspace();
                    }
                });
            }
        }, (error) => {
            console.error(error);
            DialogAPI.showAlert({
                title: 'Failed to create project',
                message: `An unexpected error has occurred while trying create a project named '${form['pgfi1']}'`,
                type: AlertTypes.Error,
                preformatted: false,
            });
            StatusBarAPI.showError(`Unable to create project '${form['pgfi1']}'`);
        });
    };

    $scope.duplicateProject = (node) => {
        let title = 'Duplicate project';
        let projectName = '';
        const workspaces = [];
        for (let i = 0; i < $scope.workspaceNames.length; i++) {
            workspaces.push({
                label: $scope.workspaceNames[i],
                value: $scope.workspaceNames[i],
            });
        }
        let formItems = {
            'pgfd1': {
                controlType: 'dropdown',
                label: 'Duplicate in workspace',
                required: true,
                value: $scope.selectedWorkspace.name,
                options: workspaces,
            }
        };
        if (!node) {
            let projectNames = [];
            let root = $scope.jstreeWidget.jstree(true).get_node('#');
            for (let i = 0; i < root.children.length; i++) {
                const name = $scope.jstreeWidget.jstree(true).get_text(root.children[i])
                projectNames.push({
                    label: name,
                    value: name,
                });
            }
            formItems['pgfd2'] = {
                controlType: 'dropdown',
                label: 'Project',
                required: true,
                options: projectNames,
            };
        } else {
            projectName = `${node.text} 2`;
            $scope.duplicateProjectData.originalPath = node.data.path;
            title = `Duplicate project '${node.text}'`;
        }
        formItems['pgfi1'] = {
            controlType: 'input',
            submitOnEnter: true,
            focus: true,
            label: 'New project name',
            required: true,
            placeholder: 'project name',
            inputRules: {
                excluded: getChildrenNames('#'),
                patterns: ['^[^/:]*$'],
            },
            value: projectName,
        };
        DialogAPI.showFormDialog({
            title: title,
            form: formItems,
            submitLabel: 'Duplicate',
            cancelLabel: 'Cancel',
        }).then((form) => {
            if (form) {
                DialogAPI.showBusyDialog('Duplicating...');
                let originalPath;
                let duplicatePath = `/${form['pgfd1']}/${form['pgfi1']}`;
                if (Object.prototype.hasOwnProperty.call(form, 'pgfd2')) {
                    let root = $scope.jstreeWidget.jstree(true).get_node('#');
                    for (let i = 0; i < root.children.length; i++) {
                        const child = $scope.jstreeWidget.jstree(true).get_node(root.children[i]);
                        if (child.text === form['pgfd2']) {
                            originalPath = child.data.path;
                            break;
                        }
                    }
                } else {
                    originalPath = $scope.duplicateProjectData.originalPath;
                }
                WorkspaceService.copy(originalPath, duplicatePath).then((response) => {
                    DialogAPI.closeBusyDialog();
                    if (response.status === 201) {
                        if (form['pgfd1'] === $scope.selectedWorkspace.name)
                            $scope.reloadWorkspace(); // Temporary solution
                        StatusBarAPI.showMessage(`Duplicated '${originalPath}'`);
                    } else {
                        StatusBarAPI.showError(`Unable to duplicate '${originalPath}'`);
                        DialogAPI.showAlert({
                            title: 'Failed to duplicate project',
                            message: `An unexpected error has occurred while trying duplicate '${originalPath}'`,
                            type: AlertTypes.Error,
                            preformatted: false,
                        });
                    }
                });
            }
        });
    };

    $scope.createWorkspace = () => {
        DialogAPI.showFormDialog({
            title: 'Create workspace',
            form: {
                'pgfi1': {
                    label: 'Name',
                    controlType: 'input',
                    type: 'text',
                    placeholder: 'workspace name',
                    required: true,
                    submitOnEnter: true,
                    focus: true,
                    inputRules: {
                        excluded: $scope.workspaceNames,
                        patterns: ['^[^/:]*$'],
                    },
                }
            },
            submitLabel: 'Create',
            cancelLabel: 'Cancel',
        }).then((form) => {
            if (form) {
                DialogAPI.showBusyDialog('Creating...');
                WorkspaceService.createWorkspace(form['pgfi1']).then((response) => {
                    DialogAPI.closeBusyDialog();
                    if (response.status !== 201) {
                        DialogAPI.showAlert({
                            title: 'Failed to create workspace',
                            message: `An unexpected error has occurred while trying create a workspace named '${form['pgfi1']}'`,
                            type: AlertTypes.Error,
                            preformatted: false,
                        });
                        StatusBarAPI.showError(`Unable to create workspace '${form['pgfi1']}'`);
                    } else {
                        $scope.reloadWorkspaceList();
                        StatusBarAPI.showMessage(`Created workspace '${form['pgfi1']}'`);
                        WorkspaceAPI.announceWorkspaceCreated({
                            workspace: form['pgfi1']
                        });
                    }
                });
            }
        }, (error) => {
            console.error(error);
            DialogAPI.showAlert({
                title: 'Failed to create workspace',
                message: `An unexpected error has occurred while trying create a workspace named '${form['pgfi1']}'`,
                type: AlertTypes.Error,
                preformatted: false,
            });
            StatusBarAPI.showError(`Unable to create workspace '${form['pgfi1']}'`);
        });
    };

    $scope.deleteWorkspace = () => {
        if ($scope.selectedWorkspace.name !== 'workspace') {
            DialogAPI.showDialog({
                title: 'Delete workspace?',
                message: `Are you sure you want to delete workspace "${$scope.selectedWorkspace.name}"? This action cannot be undone.`,
                buttons: [
                    { id: 'yes', label: 'Yes', state: ButtonStates.Emphasized },
                    { id: 'no', label: 'No' }
                ]
            }).then((buttonId) => {
                if (buttonId === 'yes') {
                    WorkspaceService.deleteWorkspace($scope.selectedWorkspace.name).then((response) => {
                        if (response.status === 204) {
                            WorkspaceAPI.announceWorkspaceDeleted($scope.selectedWorkspace.name);
                            $scope.switchWorkspace('workspace');
                            $scope.reloadWorkspaceList();
                        } else {
                            StatusBarAPI.showError(`Unable to delete workspace '${$scope.selectedWorkspace.name}'`);
                        }
                    });
                }
            }, (error) => {
                StatusBarAPI.showMessage(`An error occurred - ${error}`);
            });
        }
    };

    let to = 0;
    $scope.search = (event) => {
        if (to) { clearTimeout(to); }
        if (event.originalEvent.key === "Escape") {
            $scope.toggleSearch();
            return;
        }
        to = setTimeout(() => {
            $scope.jstreeWidget.jstree(true).search($scope.searchField.text);
        }, 250);
    };

    function showSpinner(parent) {
        return $scope.jstreeWidget.jstree(true).create_node(parent, $scope.spinnerObj, 0);
    }

    function hideSpinner(spinnerId) {
        $scope.jstreeWidget.jstree(true).delete_node($scope.jstreeWidget.jstree(true).get_node(spinnerId));
    }

    function processChildren(children) {
        let treeChildren = [];
        for (let i = 0; i < children.length; i++) {
            let child = {
                text: children[i].name,
                type: children[i].type,
                state: {
                    status: children[i].status
                },
                data: {
                    path: children[i].path,
                }
            };
            if (children[i].type === 'file') {
                child.data.contentType = children[i].contentType;
                let icon = getFileIcon(children[i].name);
                if (icon) child.icon = icon;
            }
            if (children[i].folders && children[i].files) {
                child['children'] = processChildren(children[i].folders.concat(children[i].files));
            } else if (children[i].folders) {
                child['children'] = processChildren(children[i].folders);
            } else if (children[i].files) {
                child['children'] = processChildren(children[i].files);
            }
            treeChildren.push(child);
        }
        return treeChildren;
    }

    function getFileExtension(fileName) {
        return fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length).toLowerCase();
    }

    function getFileIcon(fileName) {
        const ext = getFileExtension(fileName);
        let icon;
        if (ext === 'js' || ext === 'mjs' || ext === 'xsjs' || ext === 'ts' || ext === 'tsx' || ext === 'py' || ext === 'json') {
            icon = 'sap-icon--syntax';
        } else if (ext === 'css' || ext === 'less' || ext === 'scss') {
            icon = 'sap-icon--number-sign';
        } else if (ext === 'txt') {
            icon = 'sap-icon--text';
        } else if (ext === 'pdf') {
            icon = 'sap-icon--pdf-attachment';
        } else if (ext === 'md') {
            icon = 'sap-icon--information';
        } else if (ext === 'access') {
            icon = 'sap-icon--locked';
        } else if (ext === 'zip') {
            icon = 'sap-icon--attachment-zip-file';
        } else if (ext === 'extensionpoint') {
            icon = 'sap-icon--puzzle';
        } else if ($scope.imageFileExts.indexOf(ext) !== -1) {
            icon = 'sap-icon--picture';
        } else if ($scope.modelFileExts.indexOf(ext) !== -1) {
            icon = 'sap-icon--document-text';
        } else {
            icon = 'jstree-file';
        }
        return icon;
    }

    function getEditorsForType(node) {
        const editors = [{
            id: `openWith-${Editors.defaultEditor.id}`,
            label: Editors.defaultEditor.label,
        }];
        const editorsForContentType = Editors.editorsForContentType;
        if (Object.keys(editorsForContentType).indexOf(node.data.contentType) > -1) {
            for (let i = 0; i < editorsForContentType[node.data.contentType].length; i++) {
                if (editorsForContentType[node.data.contentType][i].id !== Editors.defaultEditor.id)
                    editors.push({
                        id: `openWith-${editorsForContentType[node.data.contentType][i].id}`,
                        label: editorsForContentType[node.data.contentType][i].label,
                    });
            }
        }
        return editors;
    }

    function openSelected(nodes) {
        if (!nodes) nodes = $scope.jstreeWidget.jstree(true).get_selected(true)
        for (let i = 0; i < nodes.length; i++) {
            if (nodes[i].type === 'file') {
                openFile(nodes[i]);
            } else if (nodes[i].type === 'folder' || nodes[i].type === 'project') {
                if (nodes[i].state.opened) $scope.jstreeWidget.jstree(true).close_node(nodes[i]);
                else $scope.jstreeWidget.jstree(true).open_node(nodes[i]);
            }
        }
    }

    function openFile(node, editor = undefined) {
        let parent = node;
        let extraArgs = { gitName: undefined };
        for (let i = 0; i < node.parents.length - 1; i++) {
            parent = $scope.jstreeWidget.jstree(true).get_node(parent.parent);
        }
        if (parent.data.git) {
            extraArgs.gitName = parent.data.gitName;
        }
        WorkspaceAPI.openFile({
            path: node.data.path,
            contentType: node.data.contentType,
            editorId: editor,
            params: extraArgs,
        });
    }

    function createFile(parent, name, path, content = '') {
        const alertBody = {
            title: 'Could not create a file',
            message: `There was an error while creating '${name}'`,
            type: AlertTypes.Error,
            preformatted: false,
        };
        WorkspaceService.createFile(name, path, content).then((response) => {
            if (response.status === 201) {
                WorkspaceService.getMetadataByUrl(response.data).then((metadata) => {
                    if (metadata.status === 200) {
                        $scope.jstreeWidget.jstree(true).deselect_all(true);
                        $scope.jstreeWidget.jstree(true).select_node(
                            $scope.jstreeWidget.jstree(true).create_node(parent, {
                                text: metadata.data.name,
                                type: 'file',
                                state: {
                                    status: metadata.data.status
                                },
                                icon: getFileIcon(metadata.data.name),
                                data: {
                                    path: metadata.data.path,
                                    contentType: metadata.data.contentType,
                                }
                            })
                        );
                    } else DialogAPI.showAlert(alertBody);
                });
            } else DialogAPI.showAlert(alertBody);
        });
    }

    function createFolder(parent, name, path) {
        WorkspaceService.createFolder(name, path).then((response) => {
            if (response.status === 201) {
                $scope.jstreeWidget.jstree(true).deselect_all(true);
                $scope.jstreeWidget.jstree(true).select_node(
                    $scope.jstreeWidget.jstree(true).create_node(
                        parent,
                        {
                            text: name,
                            type: 'folder',
                            data: {
                                path: (path.endsWith('/') ? path : path + '/') + name,
                            }
                        },
                    )
                );
            } else DialogAPI.showAlert({
                title: 'Could not create a folder',
                message: `There was an error while creating '${name}'`,
                type: AlertTypes.Error,
                preformatted: false,
            });
        });
    }

    function executeAction(workspace, project, name) {
        ActionsService.executeAction(workspace, project, name).then((response) => {
            if (response.status === 200) DialogAPI.showAlert({
                title: 'Execute action',
                message: `Action '${name}' executed successfully`,
                type: AlertTypes.Information,
                preformatted: false,
            });
            else DialogAPI.showAlert({
                title: 'Execute action',
                message: `There was an error while executing action '${name}'`,
                type: AlertTypes.Error,
                preformatted: false,
            });
        });
    }

    function openNewFileDialog(excluded, value) {
        DialogAPI.showFormDialog({
            title: 'Create a new file',
            form: {
                'fdti1': {
                    label: 'Name',
                    controlType: 'input',
                    type: 'text',
                    placeholder: 'new file.txt',
                    inputRules: {
                        excluded: excluded ? excluded : getChildrenNames($scope.newNodeData.parent, 'file'),
                        patterns: ['^[^/:]*$'],
                    },
                    value: value ? value : undefined,
                    submitOnEnter: true,
                    focus: true,
                    required: true
                },
            },
            submitLabel: 'Create',
            cancelLabel: 'Cancel'
        }).then((form) => {
            if (form) {
                createFile($scope.newNodeData.parent, form['fdti1'], $scope.newNodeData.path, $scope.newNodeData.content);
                $scope.newNodeData.content = '';
            }
        }, (error) => {
            console.error(error);
            DialogAPI.showAlert({
                title: 'Create file error',
                message: 'There was an error while processing the new file data.',
                type: AlertTypes.Error,
                preformatted: false,
            });
        });
    }

    function openNewFolderDialog() {
        DialogAPI.showFormDialog({
            title: 'Create new folder',
            form: {
                'fdti1': {
                    label: 'Name',
                    controlType: 'input',
                    type: 'text',
                    inputRules: {
                        excluded: getChildrenNames($scope.newNodeData.parent, 'folder'),
                        patterns: ['^[^/:]*$'],
                    },
                    submitOnEnter: true,
                    focus: true,
                    required: true
                }
            },
            submitLabel: 'Create',
            cancelLabel: 'Cancel'
        }).then((form) => {
            if (form) {
                createFolder($scope.newNodeData.parent, form['fdti1'], $scope.newNodeData.path);
            }
        }, (error) => {
            console.error(error);
        });
    }

    function openRenameDialog(renameNode) {
        DialogAPI.showFormDialog({
            title: `Rename ${renameNode.type}`,
            form: {
                'fdti1': {
                    label: 'Name',
                    controlType: 'input',
                    type: 'text',
                    inputRules: {
                        excluded: getChildrenNames(renameNode.parent, 'file'),
                        patterns: ['^[^/:]*$'],
                    },
                    value: renameNode.text,
                    submitOnEnter: true,
                    focus: true,
                    required: true
                },
            },
            submitLabel: 'Rename',
            cancelLabel: 'Cancel'
        }).then((form) => {
            if (form) {
                WorkspaceService.rename(
                    renameNode.text,
                    form['fdti1'],
                    renameNode.data.path.substring(renameNode.data.path.length - renameNode.text.length, 0),
                ).then((response) => {
                    if (response.status === 200) {
                        const node = $scope.jstreeWidget.jstree(true).get_node(renameNode);
                        const newPath = response.data[0].to;
                        if (renameNode.type === 'file') {
                            WorkspaceService.getMetadata(newPath).then((metadata) => {
                                if (metadata.status === 200) {
                                    node.text = metadata.data.name;
                                    node.data.path = metadata.data.path;
                                    node.data.contentType = metadata.data.contentType;
                                    node.state.status = metadata.data.status;
                                    node.icon = getFileIcon(metadata.data.name);
                                    WorkspaceAPI.announceFileRenamed({
                                        oldPath: renameNode.data.path,
                                        newPath: node.data.path,
                                        contentType: node.data.contentType,
                                    });
                                    $scope.jstreeWidget.jstree(true).redraw_node(node);
                                } else {
                                    DialogAPI.showAlert({
                                        title: 'Rename file error',
                                        message: `Unable to rename '${renameNode.text}'.`,
                                        type: AlertTypes.Error,
                                        preformatted: false,
                                    });
                                }
                            });
                        } else {
                            WorkspaceAPI.getCurrentlyOpenedFiles(renameNode.data.path).then((result) => {
                                for (let i = 0; i < result.length; i++) {
                                    const updatedPath = result[i].replace(renameNode.data.path, newPath);
                                    if (updatedPath !== result[i])
                                        WorkspaceAPI.announceFileMoved({
                                            newPath: result[i].replace(renameNode.data.path, newPath),
                                            oldPath: result[i],
                                        });
                                }
                            });
                            for (let i = 0; i < renameNode.children_d.length; i++) {
                                const child = $scope.jstreeWidget.jstree(true).get_node(renameNode.children_d[i]);
                                child.data.path = newPath + child.data.path.substring(renameNode.data.path.length);
                            }
                            node.text = form['fdti1'];
                            node.data.path = newPath;
                            $scope.jstreeWidget.jstree(true).redraw_node(node);
                        }
                    } else DialogAPI.showAlert({
                        title: 'Rename file error',
                        message: `Unable to rename '${renameNode.text}'.`,
                        type: AlertTypes.Error,
                        preformatted: false,
                    });
                });
            }
        }, (error) => {
            console.error(error);
            DialogAPI.showAlert({
                title: 'Rename file error',
                message: 'There was an error while processing the new file name.',
                type: AlertTypes.Error,
                preformatted: false,
            });
        });
    }

    function openDeleteDialog(selected) {
        DialogAPI.showDialog({
            title: (selected.length > 1) ? `Delete ${selected.length} items?` : `Delete '${selected[0].text}'?`,
            message: 'This action cannot be undone. It is recommended that you unpublish and delete.',
            buttons: [
                { id: 'b1', label: 'Delete', state: ButtonStates.Negative },
                { id: 'b2', label: 'Delete & Unpublish', state: ButtonStates.Emphasized },
                { id: 'b3', label: 'Cancel', state: ButtonStates.Transparent },
            ]
        }).then((buttonId) => {
            function deleteNode(node) {
                if (node.type === 'project') {
                    deleteProject($scope.selectedWorkspace.name, node.text, node.id);
                } else {
                    deleteFileFolder(node.data.path, node.id, node.type);
                }
            };
            if (buttonId === 'b1') {
                for (let i = 0; i < selected.length; i++) {
                    deleteNode(selected[i]);
                }
            } else if (buttonId === 'b2') {
                for (let i = 0; i < selected.length; i++) {
                    unpublish(selected[i].data.path, selected[i], () => {
                        deleteNode(selected[i]);
                    });
                }
            }
        }, (error) => {
            console.error(error);
            DialogAPI.showAlert({
                title: 'Delete error',
                message: (selected.length > 1) ? `Error while deleting ${selected.length} files.` : `Error while deleting '${selected[0].text}'.`,
                type: AlertTypes.Error,
                preformatted: false,
            });
        });
    }

    function getGenericTemplates() {
        const templateItems = [];
        for (let i = 0; i < $scope.genericTemplates.length; i++) {
            templateItems.push({
                label: $scope.genericTemplates[i].name,
                value: $scope.genericTemplates[i].id,
            });
        }
        return templateItems;
    }

    function getModelTemplates(extension) {
        const templateItems = [];
        for (let i = 0; i < $scope.modelTemplates.length; i++) {
            if ($scope.modelTemplates[i].extension === extension) {
                templateItems.push({
                    label: $scope.modelTemplates[i].name,
                    value: $scope.modelTemplates[i].id,
                });
            }
        }
        return templateItems;
    }

    function generateModel(isRegenerating = false) {
        GenerateService.generateFromModel(
            $scope.selectedWorkspace.name,
            $scope.gmodel.project,
            $scope.gmodel.model,
            $scope.gmodel.templateId,
            $scope.gmodel.parameters
        ).then((response) => {
            DialogAPI.closeBusyDialog();
            if (response.status !== 201) {
                DialogAPI.showAlert({
                    title: 'Failed to generate from model',
                    message: `An unexpected error has occurred while trying generate from model '${$scope.gmodel.model}'`,
                    type: AlertTypes.Error,
                    preformatted: false,
                });
            } else StatusBarAPI.showMessage(`Generated from model '${$scope.gmodel.model}'`);
            if (isRegenerating && $scope.gmodel.nodeParents.length) {
                $scope.jstreeWidget.jstree(true).refresh_node(getProjectNode($scope.gmodel.nodeParents));
            } else $scope.reloadWorkspace();
            for (let key in $scope.gmodel.parameters) {
                delete $scope.gmodel.parameters[key];
            }
            $scope.gmodel.model = '';
            $scope.gmodel.project = '';
            $scope.gmodel.templateId = '';
        });
    }

    WorkspaceAPI.onFileSaved((fileData) => {
        publish(fileData.path, fileData);
        if (fileData.status) {
            const instance = $scope.jstreeWidget.jstree(true);
            for (let item in instance._model.data) { // Uses the unofficial '_model' property but this is A LOT faster then using 'get_json()'
                if (item !== '#' && instance._model.data[item].data.path === fileData.path) {
                    for (let i = 0; i < instance._model.data[item].parents.length; i++) {
                        if (instance._model.data[item].parents[i] !== '#') {
                            if (instance._model.data[instance._model.data[item].parents[i]].type === 'project') {
                                if (instance._model.data[instance._model.data[item].parents[i]].li_attr.git) {
                                    if (fileData.status === 'modified')
                                        instance._model.data[item].state.status = 'M';
                                    else instance._model.data[item].state.status = undefined;
                                    instance.redraw_node(instance._model.data[item], false, false, false, fileData.status === 'modified');
                                }
                                return;
                            }
                        }
                    }
                    break;
                }
            }
        }
    });

    WorkspaceAPI.onWorkspaceChanged((changed) => {
        if (changed.workspace === $scope.selectedWorkspace.name) {
            if (changed.params.projectsViewId) {
                $scope.jstreeWidget.jstree(true).refresh_node(changed.params.projectsViewId);
            } else $scope.reloadWorkspace();
        }
        if (changed.params.publish) {
            if (changed.workspace && changed.params.publish.path) {
                publish(changed.params.publish.path, changed.workspace);
            } else if (changed.workspace) {
                publish(`/${changed.workspace}/*`);
            }
        }
    });

    WorkspaceAPI.addMessageListener({
        topic: 'projects.tree.refresh',
        handler: (msg) => {
            if (msg.workspace === $scope.selectedWorkspace.name) {
                if (msg.partial) {
                    const instance = $scope.jstreeWidget.jstree(true);
                    for (let item in instance._model.data) {
                        if (item !== '#' && instance._model.data[item].data.path === `/${msg.project}`) {
                            instance.refresh_node(item);
                        }
                    }
                } else $scope.reloadWorkspace();
            }
        },
    });

    WorkspaceAPI.addMessageListener({
        topic: 'projects.export.all',
        handler: () => {
            $scope.exportProjects();
        },
    });

    WorkspaceAPI.addMessageListener({
        topic: 'projects.tree.select',
        handler: (msg) => {
            if (msg.filePath.startsWith(`/${$scope.selectedWorkspace.name}/`)) {
                const instance = $scope.jstreeWidget.jstree(true);
                if (typeof instance._model !== 'undefined')
                    for (let item in instance._model.data) { // Uses the unofficial '_model' property but this is A LOT faster then using 'get_json()'
                        if (item !== '#' && instance._model.data[item].data.path === msg.filePath) {
                            instance.deselect_all();
                            instance._open_to(item).focus();
                            instance.select_node(item);
                            const objNode = instance.get_node(item, true);
                            objNode[0].scrollIntoView({ behavior: 'smooth', block: 'center' });
                            break;
                        }
                    }
            }
        },
    });

    angular.element($document[0]).ready(() => {
        $scope.reloadWorkspaceList();
    });
});