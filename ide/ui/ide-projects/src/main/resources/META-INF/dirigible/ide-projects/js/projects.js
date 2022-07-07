/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
let projectsView = angular.module('projects', ['ideUI', 'ideView', 'ideEditors', 'ideWorkspace', 'idePublisher', 'ideTemplates', 'ideGenerate', 'ideTransport']);

projectsView.controller('ProjectsViewController', [
    '$scope',
    'messageHub',
    'workspaceApi',
    'Editors',
    'publisherApi',
    'templatesApi',
    'generateApi',
    'transportApi',
    function (
        $scope,
        messageHub,
        workspaceApi,
        Editors,
        publisherApi,
        templatesApi,
        generateApi,
        transportApi,
    ) {
        $scope.searchVisible = false;
        $scope.searchField = { text: '' };
        $scope.workspaceNames = [];
        $scope.menuTemplates = [];
        $scope.genericTemplates = [];
        $scope.modelTemplates = [];
        $scope.modelTemplateExtensions = [];
        $scope.gmodel = {
            templateId: '',
            project: '',
            model: '',
            parameters: {},
        };
        $scope.duplicateProjectData = {};
        $scope.imageFileExts = ['ico', 'bmp', 'png', 'jpg', 'jpeg', 'gif', 'svg'];
        $scope.modelFileExts = ['extension', 'extensionpoint', 'edm', 'model', 'dsm', 'schema', 'bpmn', 'job', 'listener', 'websocket', 'roles', 'constraints', 'table', 'view'];

        $scope.selectedWorkspace = JSON.parse(localStorage.getItem('DIRIGIBLE.workspace') || '{}');
        if (!$scope.selectedWorkspace.name) {
            $scope.selectedWorkspace = { name: 'workspace' }; // Default
            saveSelectedWorkspace();
        }

        $scope.projects = [];

        $scope.jstreeWidget = angular.element('#dgProjects');
        $scope.spinnerObj = {
            text: "Loading...",
            type: "spinner",
            li_attr: { spinner: true },
        };
        $scope.jstreeConfig = {
            core: {
                check_callback: true,
                themes: {
                    name: "fiori",
                    variant: "compact",
                },
                data: function (node, cb) {
                    cb($scope.projects);
                },
            },
            search: {
                case_sensitive: false,
            },
            plugins: ["wholerow", "dnd", "search", "state", "types", "indicator"],
            dnd: {
                large_drop_target: true,
                large_drag_target: true,
                is_draggable: function (nodes) {
                    for (let i = 0; i < nodes.length; i++) {
                        if (nodes[i].type === 'project') return false;
                    }
                    return true;
                },
            },
            state: { key: 'ide-projects' },
            types: {
                '#': {
                    valid_children: ["project"]
                },
                "default": {
                    icon: "sap-icon--question-mark",
                    valid_children: [],
                },
                file: {
                    icon: "jstree-file",
                    valid_children: [],
                },
                folder: {
                    icon: "jstree-folder",
                    valid_children: ['folder', 'file', 'spinner'],
                },
                project: {
                    icon: "jstree-project",
                    valid_children: ['folder', 'file', 'spinner'],
                },
                spinner: {
                    icon: "jstree-spinner",
                    valid_children: [],
                },
            },
        };

        $scope.jstreeWidget.on('select_node.jstree', function (event, data) {
            if (data.event && data.event.type === 'click' && data.node.type === 'file') {
                messageHub.announceFileSelected({
                    name: data.node.text,
                    path: data.node.data.path,
                    contentType: data.node.data.contentType,
                    workspace: data.node.data.workspace,
                });
            }
        });

        $scope.jstreeWidget.on('dblclick.jstree', function (event) {
            let node = $scope.jstreeWidget.jstree(true).get_node(event.target);
            if (node.type === 'file') {
                openFile(node);
            }
        });

        $scope.jstreeWidget.on('rename_node.jstree', function (event, data) {
            if (!data.node.state.failedRename) {
                if (data.old !== data.text) {
                    let validate = new RegExp('^[^/]+$');
                    if (!validate.test(data.text)) {
                        data.node.state.failedRename = true;
                        $scope.jstreeWidget.jstree(true).rename_node(data.node, data.old);
                        messageHub.setStatusError(`Invalid name '${data.text}'. Reverted.`);
                    } else {
                        $scope.jstreeWidget.jstree(true).disable_node(data.node);
                        let parent = $scope.jstreeWidget.jstree(true).get_node(data.node.parent);
                        let spinnerId = showSpinner(parent);
                        workspaceApi.rename(
                            data.old,
                            data.text,
                            data.node.data.path,
                            data.node.data.workspace
                        ).then(function (response) {
                            if (response.status === 201) {
                                let guessedPath = `${data.node.data.path.substring(data.node.data.path.length - data.old.length, 0)}${data.text}`; // Temporary until back-end is fixed
                                if (data.node.type === "file") {
                                    workspaceApi.getMetadataByPath(data.node.data.workspace, guessedPath).then(function (metadata) {
                                        if (metadata.status === 200) {
                                            messageHub.closeEditor(`/${data.node.data.workspace}${data.node.data.path}`);
                                            data.node.text = metadata.data.name;
                                            data.node.data.path = metadata.data.path.substring($scope.selectedWorkspace.name.length + 1, metadata.data.path.length);
                                            data.node.data.contentType = metadata.data.contentType;
                                            data.node.state.status = metadata.data.status;
                                            data.node.icon = getFileIcon(metadata.data.name);
                                            $scope.jstreeWidget.jstree(true).redraw_node(data.node);
                                        } else {
                                            data.node.state.failedRename = true;
                                            $scope.jstreeWidget.jstree(true).rename_node(data.node, data.old);
                                            messageHub.setStatusError(`Unable to rename '${data.old}'. Reverted.`);
                                        }
                                    });
                                } else {
                                    for (let i = 0; i < data.node.children_d.length; i++) {
                                        let child = $scope.jstreeWidget.jstree(true).get_node(data.node.children_d[i]);
                                        child.data.path = guessedPath + child.data.path.substring(data.node.data.path.length);
                                    }
                                    data.node.data.path = guessedPath;
                                }
                            } else {
                                data.node.state.failedRename = true;
                                $scope.jstreeWidget.jstree(true).rename_node(data.node, data.old);
                                messageHub.setStatusError(`Unable to rename '${data.old}'. Reverted.`);
                            }
                            hideSpinner(spinnerId);
                            $scope.jstreeWidget.jstree(true).enable_node(data.node);
                        });
                    }
                }
            } else delete data.node.state.failedRename;
        });

        $scope.jstreeWidget.on('copy_node.jstree', function (event, copyObj) {
            if (!copyObj.node.state.failedCopy) {
                $scope.jstreeWidget.jstree(true).hide_node(copyObj.node);
                let parent = $scope.jstreeWidget.jstree(true).get_node(copyObj.parent);
                let spinnerId = showSpinner(parent);
                let workspace;
                let path;
                workspace = parent.data.workspace;
                path = (parent.data.path.endsWith('/') ? parent.data.path : parent.data.path + '/');
                copyObj.node.data = {
                    path: path + copyObj.node.text,
                    contentType: copyObj.original.data.contentType,
                    workspace: workspace,
                };
                workspaceApi.copy(
                    copyObj.original.data.path,
                    path,
                    copyObj.original.data.workspace,
                    workspace,
                ).then(function (response) {
                    if (response.status === 201) {
                        for (let i = 0; i < parent.children.length; i++) { // Temp solution
                            let node = $scope.jstreeWidget.jstree(true).get_node(parent.children[i]);
                            if (node.text === copyObj.node.text && node.id !== copyObj.node.id) {
                                $scope.reloadWorkspace();
                                return;
                            }
                        }
                        $scope.jstreeWidget.jstree(true).show_node(copyObj.node);
                    } else {
                        copyObj.node.state.failedCopy = true;
                        messageHub.setStatusError(`Unable to copy '${copyObj.node.text}'.`);
                        $scope.jstreeWidget.jstree(true).delete_node(copyObj.node);
                    }
                    hideSpinner(spinnerId);
                });
            } else delete copyObj.node.state.failedCopy;
        });

        $scope.jstreeWidget.on('move_node.jstree', function (event, moveObj) {
            if (!moveObj.node.state.failedMove) {
                let parent = $scope.jstreeWidget.jstree(true).get_node(moveObj.parent);
                for (let i = 0; i < parent.children.length; i++) { // Temp solution
                    let node = $scope.jstreeWidget.jstree(true).get_node(parent.children[i]);
                    if (node.text === moveObj.node.text && node.id !== moveObj.node.id) {
                        moveObj.node.state.failedMove = true;
                        $scope.jstreeWidget.jstree(true).move_node(
                            moveObj.node,
                            $scope.jstreeWidget.jstree(true).get_node(moveObj.old_parent),
                            moveObj.old_position,
                        );
                        messageHub.showAlertError('Could not move file', 'The destination contains a file with the same name.');
                        return;
                    }
                }
                $scope.jstreeWidget.jstree(true).hide_node(moveObj.node);
                let spinnerId = showSpinner(parent);
                workspaceApi.move(
                    moveObj.node.data.path,
                    (parent.data.path.endsWith('/') ? parent.data.path : parent.data.path + '/') + moveObj.node.text,
                    moveObj.node.data.workspace,
                ).then(function (response) {
                    if (response.status === 201) {
                        moveObj.node.data.path = (parent.data.path.endsWith('/') ? parent.data.path : parent.data.path + '/') + moveObj.node.text;
                        moveObj.node.data.workspace = parent.data.workspace;
                        $scope.jstreeWidget.jstree(true).show_node(moveObj.node);
                    } else {
                        moveObj.node.state.failedMove = true;
                        messageHub.setStatusError(`Unable to move '${moveObj.node.text}'.`);
                        $scope.jstreeWidget.jstree(true).move_node(
                            moveObj.node,
                            $scope.jstreeWidget.jstree(true).get_node(moveObj.old_parent),
                            moveObj.old_position,
                        );
                    }
                    hideSpinner(spinnerId);
                });
            } else delete moveObj.node.state.failedMove;
        });

        function setMenuTemplateItems(parent, menuObj, workspace, nodePath, childrenIds) {
            let priorityTemplates = true;
            let childExtensions = {};
            for (let i = 0; i < childrenIds.length; i++) {
                let child = $scope.jstreeWidget.jstree(true).get_node(childrenIds[i]);
                if (child.type === 'file') {
                    let lastIndex = child.text.lastIndexOf('.');
                    if (lastIndex !== -1) childExtensions[child.text.substring(lastIndex + 1)] = true;
                }
            }
            for (let i = 0; i < $scope.menuTemplates.length; i++) {
                let item = {
                    id: $scope.menuTemplates[i].id,
                    label: $scope.menuTemplates[i].label,
                };
                if ($scope.menuTemplates[i].oncePerFolder) {
                    if (childExtensions[$scope.menuTemplates[i].extension]) {
                        item.isDisabled = true;
                    }
                }
                if (!item.isDisabled) {
                    item.data = {
                        parent: parent,
                        name: `New File.${$scope.menuTemplates[i].extension}`,
                        path: nodePath,
                        content: $scope.menuTemplates[i].data,
                        workspace: workspace,
                    };
                }
                if (priorityTemplates && !$scope.menuTemplates[i].hasOwnProperty('order')) {
                    item.divider = true;
                    priorityTemplates = false;
                }
                menuObj.items[0].items.push(item);
            }
            menuObj.items[0].items[2].divider = true;
        }

        function getProjectNode(parents) {
            for (let i = 0; i < parents.length; i++) {
                if (parents[i] !== '#') {
                    let parent = $scope.jstreeWidget.jstree(true).get_node(parents[i]);
                    if (parent.type === 'project') {
                        return parent;
                    }
                }
            }
        }

        $scope.contextMenuContent = function (element) {
            if ($scope.jstreeWidget[0].contains(element)) {
                let id;
                if (element.tagName !== "LI") {
                    let closest = element.closest("li");
                    if (closest) id = closest.id;
                    else return {
                        callbackTopic: "projects.tree.contextmenu",
                        items: [
                            {
                                id: "newProject",
                                label: "New Project",
                                icon: "sap-icon--create",
                            },
                            {
                                id: "publishAll",
                                label: "Publish All",
                                icon: "sap-icon--arrow-top",
                                divider: true,
                            },
                            {
                                id: "unpublishAll",
                                label: "Unpublish All",
                                icon: "sap-icon--arrow-bottom",
                            },
                            {
                                id: "exportProjects",
                                label: "Export all",
                                icon: "sap-icon--download-from-cloud",
                                divider: true,
                            }
                        ]
                    }
                } else {
                    id = element.id;
                }
                if (id) {
                    let node = $scope.jstreeWidget.jstree(true).get_node(id);
                    let newSubmenu = {
                        id: "new",
                        label: "New",
                        icon: "sap-icon--create",
                        items: [
                            {
                                id: "file",
                                label: "File",
                                data: {
                                    workspace: node.data.workspace,
                                    path: node.data.path,
                                    parent: node.id
                                },
                            },
                            {
                                id: "folder",
                                label: "Folder",
                                data: {
                                    workspace: node.data.workspace,
                                    path: node.data.path,
                                    parent: node.id
                                },
                            }
                        ]
                    };
                    let cutObj = {
                        id: "cut",
                        label: "Cut",
                        shortcut: "Ctrl+X",
                        divider: true,
                        icon: "sap-icon--scissors",
                        data: node,
                    };
                    let copyObj = {
                        id: "copy",
                        label: "Copy",
                        shortcut: "Ctrl+C",
                        icon: "sap-icon--copy",
                        data: node,
                    };
                    let pasteObj = {
                        id: "paste",
                        label: "Paste",
                        shortcut: "Ctrl+V",
                        icon: "sap-icon--paste",
                        isDisabled: !$scope.jstreeWidget.jstree(true).can_paste(),
                        data: node,
                    };
                    let renameObj = {
                        id: "rename",
                        label: "Rename",
                        shortcut: "F2",
                        divider: true,
                        icon: "sap-icon--edit",
                        data: node,
                    };
                    let deleteObj = {
                        id: "delete",
                        label: "Delete",
                        shortcut: "Del",
                        icon: "sap-icon--delete",
                        data: node,
                    };
                    let publishObj = {
                        id: "publish",
                        label: "Publish",
                        divider: true,
                        icon: "sap-icon--arrow-top",
                        data: `/${node.data.workspace}${node.data.path}`,
                    };
                    let unpublishObj = {
                        id: "unpublish",
                        label: "Unpublish",
                        icon: "sap-icon--arrow-bottom",
                        data: `/${node.data.workspace}${node.data.path}`,
                    };
                    let generateObj = {
                        id: "generateGeneric",
                        label: "Generate",
                        icon: "sap-icon--create",
                        divider: true,
                        data: node,
                    };
                    // let importObj = {
                    //     id: "import",
                    //     label: "Import",
                    //     icon: "sap-icon--attachment",
                    //     divider: true,
                    //     data: node,
                    // };
                    let importZipObj = {
                        id: "importZip",
                        label: "Import from zip",
                        icon: "sap-icon--attachment-zip-file",
                        divider: true,
                        data: node,
                    };
                    if (node.type === 'project') {
                        let menuObj = {
                            callbackTopic: 'projects.tree.contextmenu',
                            items: [
                                newSubmenu,
                                {
                                    id: "duplicateProject",
                                    label: "Duplicate",
                                    divider: true,
                                    icon: "sap-icon--duplicate",
                                    data: node,
                                },
                                pasteObj,
                                renameObj,
                                deleteObj,
                                publishObj,
                                unpublishObj,
                            ]
                        };
                        if ($scope.genericTemplates.length) {
                            menuObj.items.push(generateObj);
                            setMenuTemplateItems(node.id, menuObj, node.data.workspace, node.data.path, node.children);
                        }
                        // menuObj.items.push(importObj);
                        menuObj.items.push(importZipObj);
                        menuObj.items.push({
                            id: "exportProject",
                            label: "Export",
                            icon: "sap-icon--download-from-cloud",
                            divider: true,
                            data: node,
                        });
                        return menuObj;
                    } else if (node.type === "folder") {
                        let menuObj = {
                            callbackTopic: "projects.tree.contextmenu",
                            items: [
                                newSubmenu,
                                cutObj,
                                copyObj,
                                pasteObj,
                                renameObj,
                                deleteObj,
                                publishObj,
                                unpublishObj,
                                generateObj,
                                // importObj,
                                importZipObj,
                            ]
                        };
                        setMenuTemplateItems(node.id, menuObj, node.data.workspace, node.data.path, node.children);
                        return menuObj;
                    } else if (node.type === "file") {
                        let menuObj = {
                            callbackTopic: "projects.tree.contextmenu",
                            items: [
                                {
                                    id: "open",
                                    label: "Open",
                                    icon: "sap-icon--action",
                                    data: node,
                                },
                                {
                                    id: "openWith",
                                    label: "Open With",
                                    icon: "sap-icon--action",
                                    items: getEditorsForType(node)
                                },
                                cutObj,
                                copyObj,
                                renameObj,
                                deleteObj,
                                publishObj,
                                unpublishObj,
                            ]
                        };
                        if ($scope.modelTemplates.length && $scope.modelTemplateExtensions.includes(getFileExtension(node.text))) {
                            let genObj = {
                                id: "generateModel",
                                label: "Generate",
                                icon: "sap-icon--create",
                                divider: true,
                                data: undefined,
                                isDisabled: false,
                            };
                            if (node.parents.length > 2) {
                                genObj.isDisabled = true;
                            } else {
                                genObj.data = node;
                            }
                            menuObj.items.push(genObj);
                        }
                        return menuObj;
                    }
                }
                return;
            } else return;
        };

        $scope.toggleSearch = function () {
            $scope.searchField.text = '';
            $scope.jstreeWidget.jstree(true).clear_search();
            $scope.searchVisible = !$scope.searchVisible;
        };

        $scope.isSelectedWorkspace = function (name) {
            if ($scope.selectedWorkspace.name === name) return true;
            return false;
        };

        $scope.reloadWorkspaceList = function () {
            workspaceApi.listWorkspaceNames().then(function (response) {
                if (response.status === 200)
                    $scope.workspaceNames = response.data;
                else messageHub.setStatusError('Unable to load workspace list');
            });
        };

        $scope.reloadWorkspace = function (setConfig = false) {
            $scope.projects.length = 0;
            workspaceApi.load($scope.selectedWorkspace.name).then(function (response) {
                if (response.status === 200) {
                    for (let i = 0; i < response.data.projects.length; i++) {
                        let project = {
                            text: response.data.projects[i].name,
                            type: response.data.projects[i].type,
                            data: {
                                git: response.data.projects[i].git,
                                gitName: response.data.projects[i].gitName,
                                path: response.data.projects[i].path.substring(response.data.path.length, response.data.projects[i].path.length), // Back-end should not include workspase name in path
                                workspace: response.data.name,
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
                    if (setConfig) $scope.jstreeWidget.jstree($scope.jstreeConfig);
                    else $scope.jstreeWidget.jstree(true).refresh();
                } else {
                    messageHub.setStatusError('Unable to load workspace data');
                }
            });
        };

        $scope.loadTemplates = function () {
            $scope.menuTemplates.length = 0;
            $scope.genericTemplates.length = 0;
            $scope.modelTemplates.length = 0;
            $scope.modelTemplateExtensions.length = 0;
            templatesApi.menuTemplates().then(function (response) {
                if (response.status === 200) {
                    $scope.menuTemplates = response.data
                    for (let i = 0; i < response.data.length; i++) {
                        if (response.data[i].isModel) {
                            $scope.modelFileExts.push(response.data[i].extension);
                        } else if (response.data[i].isImage) {
                            $scope.imageFileExts.push(response.data[i].extension);
                        }
                    }
                } else messageHub.setStatusError('Unable to load menu template list');
            });
            templatesApi.listTemplates().then(function (response) {
                if (response.status === 200) {
                    for (let i = 0; i < response.data.length; i++) {
                        if (response.data[i].hasOwnProperty('extension')) {
                            $scope.modelTemplates.push(response.data[i]);
                            $scope.modelTemplateExtensions.push(response.data[i].extension);
                        } else {
                            $scope.genericTemplates.push(response.data[i]);
                        }
                    }
                } else messageHub.setStatusError('Unable to load template list');
            });
        };

        $scope.publishAll = function () {
            messageHub.showStatusBusy("Publishing projects...");
            publisherApi.publish(`/${$scope.selectedWorkspace.name}/*`).then(function (response) {
                messageHub.hideStatusBusy();
                if (response.status !== 201)
                    messageHub.setStatusError(`Unable to publish projects in '${$scope.selectedWorkspace.name}'`);
                else messageHub.setStatusMessage(`Published all projects in '${$scope.selectedWorkspace.name}'`);
            });
        };

        $scope.unpublishAll = function () {
            messageHub.showStatusBusy("Unpublishing projects...");
            publisherApi.unpublish(`/${$scope.selectedWorkspace.name}/*`).then(function (response) {
                messageHub.hideStatusBusy();
                if (response.status !== 201)
                    messageHub.setStatusError(`Unable to unpublish projects in '${$scope.selectedWorkspace.name}'`);
                else messageHub.setStatusMessage(`Unpublished all projects in '${$scope.selectedWorkspace.name}'`);
            });
        };

        $scope.publish = function (path, workspace) {
            messageHub.showStatusBusy(`Publishing '${path}'...`);
            publisherApi.publish(path, workspace).then(function (response) {
                messageHub.hideStatusBusy();
                if (response.status !== 201)
                    messageHub.setStatusError(`Unable to publish '${path}'`);
                else messageHub.setStatusMessage(`Published '${path}'`);
            });
        };

        $scope.switchWorkspace = function (workspace) {
            if ($scope.selectedWorkspace.name !== workspace) {
                $scope.selectedWorkspace.name = workspace;
                saveSelectedWorkspace();
                $scope.reloadWorkspace();
            }
        };

        $scope.saveAll = function () {
            messageHub.triggerEvent('editor.file.save.all', true);
        };

        $scope.exportProjects = function () {
            transportApi.exportProject($scope.selectedWorkspace.name, '*');
        };

        $scope.linkProject = function () {
            messageHub.showFormDialog(
                'linkProjectForm',
                'Link project',
                [
                    {
                        id: "pgfi1",
                        type: "input",
                        label: "Name",
                        required: true,
                        placeholder: "project name",
                        pattern: '^[^/]*$',
                    },
                    {
                        id: "pgfi2",
                        type: "input",
                        label: "Path",
                        required: true,
                        placeholder: "/absolute/path/to/project",
                    },
                ],
                [{
                    id: 'b1',
                    type: 'emphasized',
                    label: 'Create',
                    whenValid: true,
                },
                {
                    id: 'b2',
                    type: 'transparent',
                    label: 'Cancel',
                }],
                'projects.link.project',
                'Linking...',
            );
        };

        $scope.createProject = function () {
            messageHub.showFormDialog(
                'createProjectForm',
                'Create project',
                [
                    {
                        id: "pgfi1",
                        type: "input",
                        label: "Name",
                        required: true,
                        placeholder: "project name",
                        pattern: '^[^/]*$',
                    },
                ],
                [{
                    id: 'b1',
                    type: 'emphasized',
                    label: 'Create',
                    whenValid: true,
                },
                {
                    id: 'b2',
                    type: 'transparent',
                    label: 'Cancel',
                }],
                'projects.create.project',
                'Creating...',
            );
        };

        $scope.duplicateProject = function (node) {
            let title = 'Duplicate project';
            let projectName = '';
            let workspaces = [];
            for (let i = 0; i < $scope.workspaceNames.length; i++) {
                workspaces.push({
                    label: $scope.workspaceNames[i],
                    value: $scope.workspaceNames[i],
                });
            }
            let dialogItems = [{
                id: 'pgfd1',
                type: 'dropdown',
                label: 'Duplicate in workspace',
                required: true,
                value: $scope.selectedWorkspace.name,
                items: workspaces,
            }];
            if (!node) {
                let projectNames = [];
                let root = $scope.jstreeWidget.jstree(true).get_node('#');
                for (let i = 0; i < root.children.length; i++) {
                    let name = $scope.jstreeWidget.jstree(true).get_text(root.children[i])
                    projectNames.push({
                        label: name,
                        value: name,
                    });
                }
                dialogItems.push({
                    id: 'pgfd2',
                    type: 'dropdown',
                    label: 'Project',
                    required: true,
                    value: '',
                    items: projectNames,
                });
            } else {
                projectName = `${node.text} 2`;
                $scope.duplicateProjectData.originalPath = node.data.path;
                $scope.duplicateProjectData.originalWorkspace = node.data.workspace;
                title = `Duplicate project '${node.text}'`;
            }
            dialogItems.push({
                id: "pgfi1",
                type: "input",
                label: "Duplicated project name",
                required: true,
                placeholder: "project name",
                pattern: '^[^/]*$',
                value: projectName,
            });
            messageHub.showFormDialog(
                'duplicateProjectForm',
                title,
                dialogItems,
                [{
                    id: 'b1',
                    type: 'emphasized',
                    label: 'Duplicate',
                    whenValid: true,
                },
                {
                    id: 'b2',
                    type: 'transparent',
                    label: 'Cancel',
                }],
                'projects.duplicate.project',
                'Duplicating...',
            );
        }

        $scope.createWorkspace = function () {
            messageHub.showFormDialog(
                'createWorkspaceForm',
                'Create workspace',
                [
                    {
                        id: "pgfi1",
                        type: "input",
                        label: "Name",
                        required: true,
                        placeholder: "workspace name",
                        pattern: '^[^/]*$',
                    },
                ],
                [{
                    id: 'b1',
                    type: 'emphasized',
                    label: 'Create',
                    whenValid: true,
                },
                {
                    id: 'b2',
                    type: 'transparent',
                    label: 'Cancel',
                }],
                'projects.create.workspace',
                'Creating...',
            );
        };

        $scope.deleteWorkspace = function () {
            if ($scope.selectedWorkspace.name !== 'workspace') {
                workspaceApi.deleteWorkspace($scope.selectedWorkspace.name).then(function (response) {
                    if (response.status === 204) {
                        $scope.switchWorkspace('workspace');
                        $scope.reloadWorkspaceList();
                        messageHub.announceWorkspacesModified();
                    } else {
                        messageHub.setStatusError(`Unable to delete workspace '${$scope.selectedWorkspace.name}'`);
                    }
                });
            }
        };

        let to = 0;
        $scope.search = function () {
            if (to) { clearTimeout(to); }
            to = setTimeout(function () {
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
                        path: children[i].path.substring($scope.selectedWorkspace.name.length + 1, children[i].path.length), // Back-end should not include workspase name in path
                        workspace: $scope.selectedWorkspace.name,
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
            let ext = getFileExtension(fileName);
            let icon;
            if (ext === 'js' || ext === 'mjs' || ext === 'xsjs' || ext === 'ts' || ext === 'json') {
                icon = "sap-icon--syntax";
            } else if (ext === 'css' || ext === 'less' || ext === 'scss') {
                icon = "sap-icon--number-sign";
            } else if (ext === 'txt') {
                icon = "sap-icon--text";
            } else if (ext === 'pdf') {
                icon = "sap-icon--pdf-attachment";
            } else if ($scope.imageFileExts.indexOf(ext) !== -1) {
                icon = "sap-icon--picture";
            } else if ($scope.modelFileExts.indexOf(ext) !== -1) {
                icon = "sap-icon--document-text";
            } else {
                icon = 'jstree-file';
            }
            return icon;
        }

        function getEditorsForType(node) {
            let editors = [{
                id: 'openWith',
                label: Editors.defaultEditor.label,
                data: {
                    node: node,
                    editorId: Editors.defaultEditor.id,
                }
            }];
            let editorsForContentType = Editors.editorsForContentType;
            if (Object.keys(editorsForContentType).indexOf(node.data.contentType) > -1) {
                for (let i = 0; i < editorsForContentType[node.data.contentType].length; i++) {
                    if (editorsForContentType[node.data.contentType][i].id !== Editors.defaultEditor.id)
                        editors.push({
                            id: 'openWith',
                            label: editorsForContentType[node.data.contentType][i].label,
                            data: {
                                node: node,
                                editorId: editorsForContentType[node.data.contentType][i].id,
                            }
                        });
                }
            }
            return editors;
        }

        function openFile(node, editor = undefined) {
            let parent = node;
            let extraArgs;
            for (let i = 0; i < node.parents.length - 1; i++) {
                parent = $scope.jstreeWidget.jstree(true).get_node(parent.parent);
            }
            if (parent.data.git) {
                extraArgs = { gitName: parent.data.gitName };
            }
            messageHub.openEditor(
                `/${node.data.workspace}${node.data.path}`,
                node.text,
                node.data.contentType,
                editor,
                extraArgs
            );
        }

        function saveSelectedWorkspace() {
            localStorage.setItem('DIRIGIBLE.workspace', JSON.stringify($scope.selectedWorkspace));
        }

        function createFile(parent, name, workspace, path, content = '') {
            workspaceApi.createNode(name, `/${workspace}${path}`, false, content).then(function (response) {
                if (response.status === 201) {
                    workspaceApi.getMetadata(response.data).then(function (metadata) {
                        if (metadata.status === 200) {
                            $scope.jstreeWidget.jstree(true).create_node(
                                parent,
                                {
                                    text: metadata.data.name,
                                    type: 'file',
                                    state: {
                                        status: metadata.data.status
                                    },
                                    icon: getFileIcon(metadata.data.name),
                                    data: {
                                        path: metadata.data.path.substring($scope.selectedWorkspace.name.length + 1, metadata.data.path.length),
                                        workspace: $scope.selectedWorkspace.name,
                                        contentType: metadata.data.contentType,
                                    }
                                },
                            );
                        } else {
                            messageHub.showAlertError('Could not create file', `There was an error while creating '${name}'`);
                        }
                    });
                } else {
                    messageHub.showAlertError('Could not create file', `There was an error while creating '${name}'`);
                }
            });
        }

        function createFolder(parent, name, workspace, path) {
            workspaceApi.createNode(name, `/${workspace}${path}`, true).then(function (response) {
                if (response.status === 201) {
                    $scope.jstreeWidget.jstree(true).create_node(
                        parent,
                        {
                            text: name,
                            type: "folder",
                            data: {
                                path: (path.endsWith('/') ? path : path + '/') + name,
                                workspace: workspace,
                            }
                        },
                    );
                }
            });
        }

        // Temp
        // $scope.test = function () {
        //     messageHub.postMessage('projects.tree.select', { filePath: '/ide/index.html' }, true);
        // };

        messageHub.onFileSaved(function (data) {
            const { topic, ...fileDescriptor } = data;
            publisherApi.publish(`/${fileDescriptor.workspace}${fileDescriptor.path}`).then(function (response) {
                if (response.status !== 201)
                    messageHub.setStatusError(`Unable to publish '${fileDescriptor.path}'`);
                else
                    messageHub.announcePublish(fileDescriptor);
            });
            // Temp solution until we fix the back-end API
            if (data.status) {
                let objects = $scope.jstreeWidget.jstree(true).get_json(
                    '#',
                    {
                        no_li_attr: true,
                        no_a_attr: true,
                        flat: true
                    }
                );
                for (let i = 0; i < objects.length; i++) {
                    if (objects[i].data.path === data.path) {
                        let node = $scope.jstreeWidget.jstree(true).get_node(objects[i]);
                        if (data.status === 'modified')
                            node.state.status = 'M';
                        else node.state.status = undefined;
                        $scope.jstreeWidget.jstree(true).redraw_node(node.id);
                        break;
                    }
                }
            }
        });

        messageHub.onWorkspaceChanged(function (workspace) {
            if (workspace.data.name === $scope.selectedWorkspace.name)
                $scope.reloadWorkspace();
            if (workspace.data.publish) {
                if (workspace.data.publish.workspace) {
                    $scope.publish(`/${workspace.data.name}/*`);
                } else if (workspace.data.publish.path) {
                    $scope.publish(workspace.data.publish.path, workspace.data.name);
                }
            }
        });

        messageHub.onDidReceiveMessage(
            'projects.export.all',
            function () {
                $scope.exportProjects();
            },
            true
        );

        messageHub.onDidReceiveMessage(
            'projects.tree.select',
            function (msg) {
                let objects = $scope.jstreeWidget.jstree(true).get_json(
                    '#',
                    {
                        no_state: true,
                        no_li_attr: true,
                        no_a_attr: true,
                        flat: true
                    }
                );
                for (let i = 0; i < objects.length; i++) {
                    if (objects[i].data.path === msg.data.filePath) {
                        $scope.jstreeWidget.jstree(true).select_node(objects[i]);
                        break;
                    }
                }
            },
            true
        );

        messageHub.onDidReceiveMessage(
            'projects.create.workspace',
            function (msg) {
                if (msg.data.buttonId === "b1") {
                    workspaceApi.createWorkspace(msg.data.formData[0].value).then(function (response) {
                        messageHub.hideFormDialog('createWorkspaceForm');
                        if (response.status !== 201) {
                            messageHub.showAlertError(
                                'Failed to create workspace',
                                `An unexpected error has occurred while trying create a workspace named '${msg.data.formData[0].value}'`
                            );
                            messageHub.setStatusError(`Unable to create workspace '${msg.data.formData[0].value}'`);
                        } else {
                            $scope.reloadWorkspaceList();
                            messageHub.setStatusMessage(`Created workspace '${msg.data.formData[0].value}'`);
                            messageHub.announceWorkspacesModified();
                        }
                    });
                } else messageHub.hideFormDialog('createWorkspaceForm');
            },
            true
        );

        messageHub.onDidReceiveMessage(
            'projects.duplicate.project',
            function (msg) {
                if (msg.data.buttonId === "b1") {
                    let originalPath;
                    let originalWorkspace;
                    let duplicatePath;
                    if (msg.data.formData[1].type === 'dropdown') {
                        let root = $scope.jstreeWidget.jstree(true).get_node('#');
                        for (let i = 0; i < root.children.length; i++) {
                            let child = $scope.jstreeWidget.jstree(true).get_node(root.children[i]);
                            if (child.text === msg.data.formData[1].value) {
                                originalPath = child.data.path;
                                originalWorkspace = child.data.workspace;
                                break;
                            }
                        }
                        duplicatePath = `/${msg.data.formData[2].value}`;
                    } else {
                        originalWorkspace = $scope.duplicateProjectData.originalWorkspace
                        originalPath = $scope.duplicateProjectData.originalPath;
                        duplicatePath = `/${msg.data.formData[1].value}`;
                    }
                    workspaceApi.copy(
                        originalPath,
                        duplicatePath,
                        originalWorkspace,
                        msg.data.formData[0].value,
                    ).then(function (response) {
                        if (response.status === 201) {
                            if (msg.data.formData[0].value !== $scope.selectedWorkspace.name)
                                $scope.reloadWorkspace(); // Temp
                            messageHub.setStatusMessage(`Duplicated '${originalPath}'`);
                        } else {
                            messageHub.setStatusError(`Unable to duplicate '${originalPath}'`);
                            messageHub.showAlertError(
                                'Failed to duplicate project',
                                `An unexpected error has occurred while trying duplicate '${originalPath}'`,
                            );
                        }
                        messageHub.hideFormDialog('duplicateProjectForm');
                    });
                } else messageHub.hideFormDialog('duplicateProjectForm');
            },
            true
        );

        messageHub.onDidReceiveMessage(
            'projects.create.project',
            function (msg) {
                if (msg.data.isMenu) {
                    $scope.createProject();
                } else if (msg.data.buttonId === "b1") {
                    workspaceApi.createProject($scope.selectedWorkspace.name, msg.data.formData[0].value).then(function (response) {
                        messageHub.hideFormDialog('createProjectForm');
                        if (response.status !== 201) {
                            messageHub.showAlertError(
                                'Failed to create project',
                                `An unexpected error has occurred while trying create a project named '${msg.data.formData[0].value}'`
                            );
                            messageHub.setStatusError(`Unable to create project '${msg.data.formData[0].value}'`);
                        } else {
                            $scope.reloadWorkspace();
                            messageHub.setStatusMessage(`Created project '${msg.data.formData[0].value}'`);
                        }
                    });
                } else messageHub.hideFormDialog('createProjectForm');
            },
            true
        );

        messageHub.onDidReceiveMessage(
            'projects.link.project',
            function (msg) {
                if (msg.data.isMenu) {
                    $scope.linkProject();
                } else if (msg.data.buttonId === "b1") {
                    workspaceApi.linkProject($scope.selectedWorkspace.name, msg.data.formData[0].value, msg.data.formData[1].value).then(function (response) {
                        messageHub.hideFormDialog('linkProjectForm');
                        if (response.status !== 201) {
                            messageHub.showAlertError(
                                'Failed to link project',
                                `An unexpected error has occurred while trying to link project '${msg.data.formData[0].value}'`
                            );
                            messageHub.setStatusError(`Unable to link project '${msg.data.formData[0].value}'`);
                        } else {
                            $scope.reloadWorkspace();
                            messageHub.setStatusMessage(`Linked project '${msg.data.formData[0].value}'`);
                        }
                    });
                } else messageHub.hideFormDialog('linkProjectForm');
            },
            true
        );

        messageHub.onDidReceiveMessage(
            'projects.generate.generic',
            function (msg) {
                if (msg.data.buttonId === "b1") {
                    let template;
                    for (let i = 0; i < $scope.genericTemplates.length; i++) {
                        if ($scope.genericTemplates[i].id === msg.data.formData[0].value) {
                            template = $scope.genericTemplates[i];
                        }
                    }
                    generateApi.generateFromTemplate(
                        $scope.selectedWorkspace.name,
                        msg.data.formData[1].value,
                        msg.data.formData[2].value,
                        template.id,
                        template.parameters
                    ).then(function (response) {
                        if (response.status === 201) {
                            messageHub.setStatusMessage('Successfully generated from template.');
                            $scope.reloadWorkspace();
                        } else {
                            messageHub.showAlertError(
                                'Failed to generate from template',
                                `An unexpected error has occurred while trying generate from template '${template.name}'`
                            );
                            messageHub.setStatusError(`Unable to generate from template '${template.name}'`);
                        }
                    });
                    messageHub.hideFormDialog('projectGenerateForm1');
                } else messageHub.hideFormDialog('projectGenerateForm1');
            },
            true
        );

        messageHub.onDidReceiveMessage(
            'projects.generate.model',
            function (msg) {
                if (msg.data.buttonId === "b1") {
                    if ($scope.gmodel.model === '') {
                        $scope.gmodel.templateId = msg.data.formData[0].value;
                        $scope.gmodel.project = msg.data.formData[1].value;
                        $scope.gmodel.model = msg.data.formData[2].value;
                        let formData = [];
                        for (let i = 0; i < $scope.modelTemplates.length; i++) {
                            if ($scope.modelTemplates[i].id === $scope.gmodel.templateId) {
                                for (let j = 0; j < $scope.modelTemplates[i].parameters.length; j++) {
                                    let formItem = {
                                        id: $scope.modelTemplates[i].parameters[j].name,
                                        type: ($scope.modelTemplates[i].parameters[j].type === 'checkbox' ? 'checkbox' : 'input'),
                                        label: $scope.modelTemplates[i].parameters[j].label,
                                        required: $scope.modelTemplates[i].parameters[j].required || true,
                                        placeholder: $scope.modelTemplates[i].parameters[j].placeholder,
                                        value: $scope.modelTemplates[i].parameters[j].value || ($scope.modelTemplates[i].parameters[j].type === 'checkbox' ? false : ''),
                                    };
                                    if ($scope.modelTemplates[i].parameters[j].hasOwnProperty('ui')) {
                                        if ($scope.modelTemplates[i].parameters[j].ui.hasOwnProperty('hide')) {
                                            formItem.visibility = {
                                                hidden: false,
                                                id: $scope.modelTemplates[i].parameters[j].ui.hide.property,
                                                value: $scope.modelTemplates[i].parameters[j].ui.hide.value,
                                            };
                                        } else {
                                            // TODO
                                        }
                                    }
                                    formData.push(formItem);
                                }
                                break;
                            }
                        }
                        messageHub.updateFormDialog(
                            "projectGenerateForm2",
                            formData,
                            "Generating...",
                        );
                    } else {
                        $scope.gmodel.parameters = {};
                        for (let i = 0; i < msg.data.formData.length; i++) {
                            if (msg.data.formData[i].value)
                                $scope.gmodel.parameters[msg.data.formData[i].id] = msg.data.formData[i].value;
                        }
                        generateApi.generateFromModel(
                            $scope.selectedWorkspace.name,
                            $scope.gmodel.project,
                            $scope.gmodel.model,
                            $scope.gmodel.templateId,
                            $scope.gmodel.parameters
                        ).then(function (response) {
                            messageHub.hideFormDialog("projectGenerateForm2");
                            if (response.status !== 201) {
                                messageHub.showAlertError(
                                    'Failed to generate from model',
                                    `An unexpected error has occurred while trying generate from model '${$scope.gmodel.model}'`
                                );
                                messageHub.setStatusError(`Unable to generate from model '${$scope.gmodel.model}'`);
                            } else {
                                messageHub.setStatusMessage(`Generated from model '${$scope.gmodel.model}'`);
                            }
                            $scope.reloadWorkspace();
                            for (let key in $scope.gmodel.parameters) {
                                delete $scope.gmodel.parameters[key];
                            }
                            $scope.gmodel.model = '';
                        });
                    }
                } else {
                    messageHub.hideFormDialog('projectGenerateForm2');
                    $scope.gmodel.model = '';
                }
            },
            true
        );

        messageHub.onDidReceiveMessage(
            'projects.tree.contextmenu',
            function (msg) {
                if (msg.data.itemId === 'open') {
                    openFile(msg.data.data);
                } else if (msg.data.itemId === 'openWith') {
                    openFile(msg.data.data.node, msg.data.data.editorId);
                } else if (msg.data.itemId === 'file') {
                    createFile(msg.data.data.parent, 'New File', msg.data.data.workspace, msg.data.data.path);
                } else if (msg.data.itemId === 'folder') {
                    createFolder(msg.data.data.parent, 'New Folder', msg.data.data.workspace, msg.data.data.path);
                } else if (msg.data.itemId === 'rename') {
                    $scope.jstreeWidget.jstree(true).edit(msg.data.data);
                } else if (msg.data.itemId === 'delete') {
                    if (msg.data.data.type === 'project') {
                        publisherApi.unpublish(msg.data.data.data.path, msg.data.data.data.workspace).then(function (unpublish) {
                            if (unpublish.status !== 201) {
                                messageHub.setStatusError(`Unable to unpublish '${msg.data.data.text}'.`);
                            } else {
                                workspaceApi.deleteProject(msg.data.data.data.workspace, msg.data.data.text).then(function (response) {
                                    if (response.status !== 204) {
                                        messageHub.setStatusError(`Unable to delete '${msg.data.data.text}'.`);
                                    } else {
                                        $scope.jstreeWidget.jstree(true).delete_node(msg.data.data);
                                        messageHub.setStatusMessage(`Deleted and unpublished '${msg.data.data.text}'.`);
                                    }
                                });
                            }
                        });
                    } else {
                        publisherApi.unpublish(msg.data.data.data.path, msg.data.data.data.workspace).then(function (unpublish) {
                            if (unpublish.status !== 201) {
                                messageHub.setStatusError(`Unable to unpublish '${msg.data.data.text}'.`);
                            } else {
                                workspaceApi.remove(msg.data.data.data.workspace + msg.data.data.data.path).then(function (response) {
                                    if (response.status !== 204) {
                                        messageHub.setStatusError(`Unable to delete '${msg.data.data.text}'.`);
                                    } else {
                                        $scope.jstreeWidget.jstree(true).delete_node(msg.data.data);
                                        messageHub.setStatusMessage(`Deleted and unpublished '${msg.data.data.text}'.`);
                                    }
                                });
                            }
                        });
                    }
                } else if (msg.data.itemId === 'cut') {
                    $scope.jstreeWidget.jstree(true).cut(msg.data.data);
                } else if (msg.data.itemId === 'copy') {
                    $scope.jstreeWidget.jstree(true).copy(msg.data.data);
                } else if (msg.data.itemId === 'paste') {
                    $scope.jstreeWidget.jstree(true).paste(msg.data.data);
                } else if (msg.data.itemId === 'newProject') {
                    $scope.createProject();
                } else if (msg.data.itemId === 'duplicateProject') {
                    $scope.duplicateProject(msg.data.data);
                } else if (msg.data.itemId === 'exportProjects') {
                    $scope.exportProjects();
                } else if (msg.data.itemId === 'exportProject') {
                    transportApi.exportProject(msg.data.data.data.workspace, msg.data.data.text);
                } else if (msg.data.itemId === 'import') {
                    messageHub.showDialogWindow(
                        "import",
                        {
                            importType: 'file',
                            uploadPath: msg.data.data.data.path,
                            workspace: msg.data.data.data.workspace,
                        }
                    );
                } else if (msg.data.itemId === 'importZip') {
                    messageHub.showDialogWindow(
                        "import",
                        {
                            uploadPath: msg.data.data.data.path,
                            workspace: msg.data.data.data.workspace,
                        }
                    );
                } else if (msg.data.itemId === 'unpublishAll') {
                    $scope.unpublishAll();
                } else if (msg.data.itemId === 'publishAll') {
                    $scope.publishAll();
                } else if (msg.data.itemId === 'publish') {
                    publisherApi.publish(msg.data.data).then(function (response) {
                        if (response.status !== 201)
                            messageHub.setStatusError(`Unable to publish '${msg.data.data}'`);
                        else
                            messageHub.announcePublish();
                    });
                } else if (msg.data.itemId === 'unpublish') {
                    publisherApi.unpublish(msg.data.data).then(function (response) {
                        if (response.status !== 201)
                            messageHub.setStatusError(`Unable to unpublish '${msg.data.data}'`);
                        else
                            messageHub.announceUnpublish();
                    });
                } else if (msg.data.itemId === 'unpublishAll') {
                    $scope.unpublishAll();
                } else if (msg.data.itemId.startsWith('generate')) {
                    let project;
                    let projectNames = [];
                    let templateItems = [];
                    let root = $scope.jstreeWidget.jstree(true).get_node('#');
                    for (let i = 0; i < root.children.length; i++) {
                        let name = $scope.jstreeWidget.jstree(true).get_text(root.children[i])
                        projectNames.push({
                            label: name,
                            value: name,
                        });
                    }
                    if (msg.data.itemId === 'generateGeneric') {
                        let generatePath;
                        for (let i = 0; i < $scope.genericTemplates.length; i++) {
                            templateItems.push({
                                label: $scope.genericTemplates[i].name,
                                value: $scope.genericTemplates[i].id,
                            });
                        }
                        if (msg.data.data.type !== 'project') {
                            let pnode = getProjectNode(msg.data.data.parents);
                            project = pnode.text;
                            generatePath = msg.data.data.data.path.substring(pnode.text.length + 1);
                            if (generatePath.endsWith('/')) generatePath += 'filename';
                            else generatePath += '/filename';
                        } else {
                            project = msg.data.data.text;
                            generatePath = '/filename';
                        }
                        messageHub.showFormDialog(
                            'projectGenerateForm1',
                            'Generate from template',
                            [
                                {
                                    id: 'pgfd1',
                                    type: 'dropdown',
                                    label: 'Choose template',
                                    required: true,
                                    value: '',
                                    items: templateItems,
                                },
                                {
                                    id: 'pgfd2',
                                    type: 'dropdown',
                                    label: 'Choose project',
                                    required: true,
                                    value: project,
                                    items: projectNames,
                                },
                                {
                                    id: "pgfi1",
                                    type: "input",
                                    label: "File path in project",
                                    required: true,
                                    placeholder: "/path/file",
                                    value: generatePath,
                                },
                            ],
                            [{
                                id: 'b1',
                                type: 'emphasized',
                                label: 'OK',
                                whenValid: true,
                            },
                            {
                                id: 'b2',
                                type: 'transparent',
                                label: 'Cancel',
                            }],
                            'projects.generate.generic',
                            'Generating...',
                        );
                    } else if (msg.data.itemId === 'generateModel') {
                        let pnode = getProjectNode(msg.data.data.parents);
                        project = pnode.text;
                        let ext = getFileExtension(msg.data.data.text);
                        for (let i = 0; i < $scope.modelTemplates.length; i++) {
                            if ($scope.modelTemplates[i].extension === ext) {
                                templateItems.push({
                                    label: $scope.modelTemplates[i].name,
                                    value: $scope.modelTemplates[i].id,
                                });
                            }
                        }
                        messageHub.showFormDialog(
                            'projectGenerateForm2',
                            'Generate from template',
                            [
                                {
                                    id: 'pgfd1',
                                    type: 'dropdown',
                                    label: 'Choose template',
                                    required: true,
                                    value: '',
                                    items: templateItems,
                                },
                                {
                                    id: 'pgfd2',
                                    type: 'dropdown',
                                    label: 'Choose project',
                                    required: true,
                                    value: project,
                                    items: projectNames,
                                },
                                {
                                    id: "pgfi1",
                                    type: "input",
                                    label: "Model (must be in the root of the project)",
                                    required: true,
                                    pattern: '^[^/]*$',
                                    placeholder: "file.model",
                                    value: msg.data.data.data.path.substring(project.length + 2),
                                },
                            ],
                            [{
                                id: 'b1',
                                type: 'emphasized',
                                label: 'OK',
                                whenValid: true,
                            },
                            {
                                id: 'b2',
                                type: 'transparent',
                                label: 'Cancel',
                            }],
                            'projects.generate.model',
                            'Loading parameters...',
                        );
                    }
                } else {
                    createFile(
                        msg.data.data.parent,
                        msg.data.data.name,
                        msg.data.data.workspace,
                        msg.data.data.path,
                        msg.data.data.content
                    );
                }
            },
            true
        );

        // Initialization
        $scope.reloadWorkspace(true);
        $scope.reloadWorkspaceList();
        $scope.loadTemplates();
    }]);