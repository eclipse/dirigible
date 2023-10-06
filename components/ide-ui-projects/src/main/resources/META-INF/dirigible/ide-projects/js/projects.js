/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
let projectsView = angular.module('projects', ['ideUI', 'ideView', 'ideEditors', 'ideWorkspace', 'idePublisher', 'ideTemplates', 'ideGenerate', 'ideTransport', 'ideActions']);

projectsView.controller('ProjectsViewController', [
    '$scope',
    'messageHub',
    'workspaceApi',
    'Editors',
    'publisherApi',
    'templatesApi',
    'generateApi',
    'transportApi',
    'actionsApi',
    function (
        $scope,
        messageHub,
        workspaceApi,
        Editors,
        publisherApi,
        templatesApi,
        generateApi,
        transportApi,
        actionsApi,
    ) {
        $scope.state = {
            isBusy: true,
            error: false,
            busyText: "Loading...",
        };
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
        $scope.newNodeData = {
            parent: '',
            workspace: '',
            path: '',
            content: '',
        };
        $scope.actionData = {
            workspace: '',
            project: '',
            action: '',
        };
        $scope.renameNodeData;
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
                keyboard: {
                    'enter': function (e) {
                        e.type = "dblclick";
                        $(e.currentTarget).trigger(e);
                    },
                    'f2': function (e) {
                        e.preventDefault();
                        $scope.renameNodeData = $scope.jstreeWidget.jstree(true).get_node(e.target.id);
                        openRenameDialog();
                    },
                    'delete': function () {
                        let nodes = $scope.jstreeWidget.jstree(true).get_selected(true);
                        if (nodes.length === 1) openDeleteDialog(nodes[0]);
                        else if (nodes.length > 1) openDeleteDialog(nodes);
                    },
                    'ctrl+c': function (e) {
                        $scope.jstreeWidget.jstree(true).copy($scope.jstreeWidget.jstree(true).get_node(e.target.id));
                    },
                    'ctrl+x': function (e) {
                        $scope.jstreeWidget.jstree(true).cut($scope.jstreeWidget.jstree(true).get_node(e.target.id));
                    },
                    'ctrl+v': function (e) {
                        if ($scope.jstreeWidget.jstree(true).can_paste()) {
                            let parent = $scope.jstreeWidget.jstree(true).get_node(e.target.id);
                            if (parent.type === 'folder' || parent.type === 'project') {
                                $scope.jstreeWidget.jstree(true).paste(parent);
                            }
                        }
                    },
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
                let workspace = parent.data.workspace;
                let spinnerId = showSpinner(parent);
                workspaceApi.move(
                    moveObj.node.data.path,
                    (parent.data.path.endsWith('/') ? parent.data.path : parent.data.path + '/') + moveObj.node.text,
                    moveObj.node.data.workspace,
                    workspace
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

        function setMenuTemplateItems(parent, menuObj, workspace, nodePath) {
            let priorityTemplates = true;
            let childExtensions = {};
            let children = getChildrenNames(parent, 'file');
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
                        item.isDisabled = true;
                    }
                }
                if (!item.isDisabled) {
                    item.data = {
                        parent: parent,
                        extension: $scope.menuTemplates[i].extension,
                        path: nodePath,
                        content: $scope.menuTemplates[i].data,
                        workspace: workspace,
                        name: $scope.menuTemplates[i].name,
                        staticName: $scope.menuTemplates[i].staticName || false,
                        nameless: $scope.menuTemplates[i].nameless || false,
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

        function getChildrenNames(node, type = '') {
            let root = $scope.jstreeWidget.jstree(true).get_node(node);
            let names = [];
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

        $scope.contextMenuContent = function (element) {
            if ($scope.jstreeWidget[0].contains(element)) {
                let id;
                if (element.tagName !== "LI") {
                    let closest = element.closest("li");
                    if (closest) {
						id = closest.id;
					} else {
						let items = [];
						items.push({
	                            id: "newProject",
	                            label: "New Project",
	                            icon: "sap-icon--create",
	                        });
	                    if (publisherApi.isEnabled()) {
		                    items.push({
		                            id: "publishAll",
		                            label: "Publish All",
		                            icon: "sap-icon--arrow-top",
		                            divider: true,
		                        });
		                    items.push({
		                            id: "unpublishAll",
		                            label: "Unpublish All",
		                            icon: "sap-icon--arrow-bottom",
		                        });
	                    }
	                    items.push({
	                            id: "exportProjects",
	                            label: "Export all",
	                            icon: "sap-icon--download-from-cloud",
	                            divider: true,
	                        });
						return {
	                        callbackTopic: "projects.tree.contextmenu",
	                        items: items
	                    }
                    }
                } else {
                    id = element.id;
                }
                if (id) {
                    let node = $scope.jstreeWidget.jstree(true).get_node(id);
                    if (!node.state.selected) {
                        $scope.jstreeWidget.jstree(true).deselect_all();
                        $scope.jstreeWidget.jstree(true).select_node(node, false, true);
                    }
                    let nodes = $scope.jstreeWidget.jstree(true).get_selected(false);
                    if (nodes.length === 1) nodes.length = 0;
                    let newSubmenu = {
                        id: "new",
                        label: "New",
                        icon: "sap-icon--create",
                        items: [{
                            id: "file",
                            label: "File",
                            data: {
                                workspace: node.data.workspace,
                                path: node.data.path,
                                parent: node.id
                            },
                        }, {
                            id: "folder",
                            label: "Folder",
                            data: {
                                workspace: node.data.workspace,
                                path: node.data.path,
                                parent: node.id
                            },
                        }]
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
                        label: (nodes.length) ? `Delete ${nodes.length} items` : "Delete",
                        shortcut: "Del",
                        icon: "sap-icon--delete",
                        data: (nodes.length) ? nodes : node,
                    };
                    let publishObj;
                    let unpublishObj;
                    if (publisherApi.isEnabled()) {
	                    publishObj = {
	                        id: "publish",
	                        label: "Publish",
	                        divider: true,
	                        icon: "sap-icon--arrow-top",
	                        data: `/${node.data.workspace}${node.data.path}`,
	                    };
	                    unpublishObj = {
	                        id: "unpublish",
	                        label: "Unpublish",
	                        icon: "sap-icon--arrow-bottom",
	                        data: `/${node.data.workspace}${node.data.path}`,
	                    };
                    }
                    let generateObj;
                    if (generateApi.isEnabled()) {
	                    generateObj = {
	                        id: "generateGeneric",
	                        label: "Generate",
	                        icon: "sap-icon-TNT--operations",
	                        divider: true,
	                        data: node,
	                    };
                    }
                    let importObj = {
                        id: "import",
                        label: "Import",
                        icon: "sap-icon--attachment",
                        divider: true,
                        data: node,
                    };
                    let importZipObj = {
                        id: "importZip",
                        label: "Import from zip",
                        icon: "sap-icon--attachment-zip-file",
                        data: node,
                    };
                    if (node.type === 'project') {
						let items = [
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
                            ]
						if (publisherApi.isEnabled()) {
							items.push(publishObj);
							items.push(unpublishObj);
						}
                        let menuObj = {
                            callbackTopic: 'projects.tree.contextmenu',
                            items: items
                        };
                        if ($scope.menuTemplates.length && generateApi.isEnabled()) {
                            menuObj.items.push(generateObj);
                            setMenuTemplateItems(node.id, menuObj, node.data.workspace, node.data.path);
                        }
                        menuObj.items.push(importObj);
                        menuObj.items.push(importZipObj);
                        menuObj.items.push({
                            id: "exportProject",
                            label: "Export",
                            icon: "sap-icon--download-from-cloud",
                            divider: true,
                            data: node,
                        });
                        if (actionsApi.isEnabled()) {
	                        menuObj.items.push({
	                            id: "actionsProject",
	                            label: "Actions",
	                            icon: "sap-icon--media-play",
	                            divider: true,
	                            data: node,
	                        });
                        }
                        return menuObj;
                    } else if (node.type === "folder") {
						let items = [
                                newSubmenu,
                                cutObj,
                                copyObj,
                                pasteObj,
                                renameObj,
                                deleteObj,
                                generateObj,
                                importObj,
                                importZipObj,
                            ]
						if (publisherApi.isEnabled()) {
							items.push(publishObj);
							items.push(unpublishObj);
						}
                        let menuObj = {
                            callbackTopic: "projects.tree.contextmenu",
                            items: items
                        };
                        setMenuTemplateItems(node.id, menuObj, node.data.workspace, node.data.path);
                        return menuObj;
                    } else if (node.type === "file") {
						let items = [
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
                            ]
						if (publisherApi.isEnabled()) {
							items.push(publishObj);
							items.push(unpublishObj);
						}
                        let menuObj = {
                            callbackTopic: "projects.tree.contextmenu",
                            items: items
                        };
                        if (generateApi.isEnabled()) {
	                        const fileExt = getFileExtension(node.text);
	                        if (fileExt === 'gen') {
	                            let regenObj = {
	                                id: "regenerateModel",
	                                label: "Regenerate",
	                                icon: "sap-icon--refresh",
	                                divider: true,
	                                data: undefined,
	                                isDisabled: false,
	                            };
	                            if (node.parents.length > 2) {
	                                regenObj.isDisabled = true;
	                            } else {
	                                regenObj.data = node;
	                            }
	                            menuObj.items.push(regenObj);
	                        }
	                        else if ($scope.modelTemplates.length && $scope.modelTemplateExtensions.includes(fileExt)) {
	                            let genObj = {
	                                id: "generateModel",
	                                label: "Generate",
	                                icon: "sap-icon-TNT--operations",
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
                if (response.status === 200) {
                    $scope.workspaceNames = response.data;
                    $scope.state.error = false;
                } else {
                    $scope.state.error = true;
                    $scope.errorMessage = "Unable to load workspace list.";
                    messageHub.setStatusError('Unable to load workspace list');
                }
            });
        };

        $scope.reloadWorkspace = function (setConfig = false) {
            $scope.projects.length = 0;
            $scope.state.isBusy = true;
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
                    $scope.state.isBusy = false;
                    if (setConfig) $scope.jstreeWidget.jstree($scope.jstreeConfig);
                    else $scope.jstreeWidget.jstree(true).refresh();
                } else {
                    $scope.state.isBusy = false;
                    $scope.state.error = true;
                    $scope.errorMessage = "Unable to load workspace data.";
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
                if (response.status !== 200)
                    messageHub.setStatusError(`Unable to publish projects in '${$scope.selectedWorkspace.name}'`);
                else messageHub.setStatusMessage(`Published all projects in '${$scope.selectedWorkspace.name}'`);
                messageHub.announcePublish();
            });

        };

        $scope.unpublishAll = function () {
            messageHub.showStatusBusy("Unpublishing projects...");
            publisherApi.unpublish(`/${$scope.selectedWorkspace.name}/*`).then(function (response) {
                messageHub.hideStatusBusy();
                if (response.status !== 200)
                    messageHub.setStatusError(`Unable to unpublish projects in '${$scope.selectedWorkspace.name}'`);
                else messageHub.setStatusMessage(`Unpublished all projects in '${$scope.selectedWorkspace.name}'`);
                messageHub.announceUnpublish();
            });
        };

        $scope.publish = function (path, workspace, callback) {
            messageHub.showStatusBusy(`Publishing '${path}'...`);
            publisherApi.publish(path, workspace).then(function (response) {
                messageHub.hideStatusBusy();
                if (response.status !== 200) {
                    messageHub.setStatusError(`Unable to publish '${path}'`);
                } else {
                    messageHub.setStatusMessage(`Published '${path}'`);
                    if (callback) callback();
                }
            });
        };

        $scope.unpublish = function (path, workspace, callback) {
            messageHub.showStatusBusy(`Unpublishing '${path}'...`);
            publisherApi.unpublish(path, workspace).then(function (response) {
                messageHub.hideStatusBusy();
                if (response.status !== 200) {
                    messageHub.setStatusError(`Unable to unpublish '${path}'`);
                } else {
                    messageHub.setStatusMessage(`Unpublished '${path}'`);
                    if (callback) callback();
                }
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

        $scope.deleteFileFolder = function (workspace, path, nodeId) {
            workspaceApi.remove(workspace + path).then(function (response) {
                if (response.status !== 204) {
                    messageHub.setStatusError(`Unable to delete '${path}'.`);
                } else {
                    messageHub.setStatusMessage(`Deleted '${path}'.`);
                    $scope.jstreeWidget.jstree(true).delete_node(nodeId);
                }
            });
        };

        $scope.deleteProject = function (workspace, project, nodeId) {
            workspaceApi.deleteProject(workspace, project).then(function (response) {
                if (response.status !== 204) {
                    messageHub.setStatusError(`Unable to delete '${project}'.`);
                } else {
                    messageHub.setStatusMessage(`Deleted '${project}'.`);
                    $scope.jstreeWidget.jstree(true).delete_node(nodeId);
                }
            });
        };

        $scope.exportProjects = function () {
            transportApi.exportProject($scope.selectedWorkspace.name, '*');
        };

        $scope.linkProject = function () {
            messageHub.showFormDialog(
                'linkProjectForm',
                'Link project',
                [{
                    id: "pgfi1",
                    type: "input",
                    label: "Name",
                    required: true,
                    placeholder: "project name",
                    inputRules: {
                        excluded: getChildrenNames('#'),
                        patterns: ['^[^/:]*$'],
                    },
                }, {
                    id: "pgfi2",
                    type: "input",
                    label: "Path",
                    required: true,
                    placeholder: "/absolute/path/to/project",
                }],
                [{
                    id: 'b1',
                    type: 'emphasized',
                    label: 'Create',
                    whenValid: true,
                }, {
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
                        submitOnEnterId: "b1",
                        label: "Name",
                        required: true,
                        placeholder: "project name",
                        inputRules: {
                            excluded: getChildrenNames('#'),
                            patterns: ['^[^/:]*$'],
                        },
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
                submitOnEnterId: "b1",
                label: "Duplicated project name",
                required: true,
                placeholder: "project name",
                inputRules: {
                    excluded: getChildrenNames('#'),
                    patterns: ['^[^/:]*$'],
                },
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
                }, {
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
                [{
                    id: "pgfi1",
                    type: "input",
                    submitOnEnterId: "b1",
                    label: "Name",
                    required: true,
                    placeholder: "workspace name",
                    inputRules: {
                        excluded: $scope.workspaceNames,
                        patterns: ['^[^/:]*$'],
                    },
                }],
                [{
                    id: 'b1',
                    type: 'emphasized',
                    label: 'Create',
                    whenValid: true,
                }, {
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
                messageHub.showDialogAsync(
                    'Delete workspace?',
                    `Are you sure you want to delete workspace "${$scope.selectedWorkspace.name}"? This action cannot be undone.`,
                    [{
                        id: "b1",
                        type: "emphasized",
                        label: "Yes",
                    }, {
                        id: "b3",
                        type: "normal",
                        label: "No",
                    }],
                ).then(function (msg) {
                    if (msg.data === "b1") {
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
            let extraArgs = { gitName: undefined, workspaceName: node.data.workspace };
            for (let i = 0; i < node.parents.length - 1; i++) {
                parent = $scope.jstreeWidget.jstree(true).get_node(parent.parent);
            }
            if (parent.data.git) {
                extraArgs.gitName = parent.data.gitName;
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
                            $scope.jstreeWidget.jstree(true).deselect_all(true);
                            $scope.jstreeWidget.jstree(true).select_node(
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
                                )
                            );
                        } else {
                            messageHub.showAlertError('Could not create a file', `There was an error while creating '${name}'`);
                        }
                    });
                } else {
                    messageHub.showAlertError('Could not create a file', `There was an error while creating '${name}'`);
                }
            });
        }

        function createFolder(parent, name, workspace, path) {
            workspaceApi.createNode(name, `/${workspace}${path}`, true).then(function (response) {
                if (response.status === 201) {
                    $scope.jstreeWidget.jstree(true).deselect_all(true);
                    $scope.jstreeWidget.jstree(true).select_node(
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
                        )
                    );
                } else {
                    messageHub.showAlertError('Could not create a folder', `There was an error while creating '${name}'`);
                }
            });
        }
        
        function executeAction(workspace, project, name) {
            actionsApi.executeAction(workspace, project, name).then(function (response) {
                if (response.status === 200) {
                    messageHub.showAlertInfo('Execute action', `Action '${name}' executed successfully`);
                } else {
                    messageHub.showAlertError('Execute action', `There was an error while executing action '${name}'`);
                }
            });
        }

        function openNewFileDialog() {
            messageHub.showFormDialog(
                "projectsNewFileForm",
                "Create a new file",
                [{
                    id: "fdti1",
                    type: "input",
                    submitOnEnterId: "b1",
                    label: "Name",
                    required: true,
                    inputRules: {
                        excluded: getChildrenNames($scope.newNodeData.parent, 'file'),
                        patterns: ['^[^/:]*$'],
                    },
                    value: '',
                }],
                [{
                    id: "b1",
                    type: "emphasized",
                    label: "Create",
                    whenValid: true
                }, {
                    id: "b2",
                    type: "transparent",
                    label: "Cancel",
                }],
                "projects.formDialog.create.file",
                "Creating..."
            );
        }

        function openRenameDialog() {
            messageHub.showFormDialog(
                "projectsRenameForm",
                `Rename ${$scope.renameNodeData.type}`,
                [{
                    id: "fdti1",
                    type: "input",
                    submitOnEnterId: "b1",
                    label: "Name",
                    required: true,
                    inputRules: {
                        excluded: getChildrenNames($scope.renameNodeData.parent, 'file'),
                        patterns: ['^[^/:]*$'],
                    },
                    value: $scope.renameNodeData.text,
                }],
                [{
                    id: "b1",
                    type: "emphasized",
                    label: "Rename",
                    whenValid: true
                }, {
                    id: "b2",
                    type: "transparent",
                    label: "Cancel",
                }],
                "projects.formDialog.rename",
                "Renameing..."
            );
        }

        function openDeleteDialog(selected) {
            let isMultiple = Array.isArray(selected);
            messageHub.showDialogAsync(
                (isMultiple) ? `Delete ${selected.length} items?` : `Delete '${selected.text}'?`,
                'This action cannot be undone. It is recommended that you unpublish and delete.',
                [{
                    id: 'b1',
                    type: 'negative',
                    label: 'Delete',
                }, {
                    id: 'b2',
                    type: 'emphasized',
                    label: 'Delete & Unpublish',
                }, {
                    id: 'b3',
                    type: 'transparent',
                    label: 'Cancel',
                }],
            ).then(function (dialogResponse) {
                function deleteNode(node) {
                    if (node.type === 'project') {
                        $scope.deleteProject(node.data.workspace, node.text, node.id);
                    } else {
                        $scope.deleteFileFolder(node.data.workspace, node.data.path, node.id);
                    }
                };
                if (dialogResponse.data === 'b1') {
                    if (isMultiple) {
                        for (let i = 0; i < selected.length; i++) {
                            let node = $scope.jstreeWidget.jstree(true).get_node(selected[i]);
                            deleteNode(node);
                        }
                    } else {
                        deleteNode(selected);
                    }
                } else if (dialogResponse.data === 'b2') {
                    if (isMultiple) {
                        for (let i = 0; i < selected.length; i++) {
                            let node = $scope.jstreeWidget.jstree(true).get_node(selected[i]);
                            $scope.unpublish(node.data.path, node.data.workspace, function () {
                                deleteNode(node);
                            });
                        }
                    } else {
                        $scope.unpublish(selected.data.path, selected.data.workspace, function () {
                            deleteNode(selected);
                        });
                    }
                }
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
            generateApi.generateFromModel(
                $scope.selectedWorkspace.name,
                $scope.gmodel.project,
                $scope.gmodel.model,
                $scope.gmodel.templateId,
                $scope.gmodel.parameters
            ).then(function (response) {
                if (isRegenerating) messageHub.hideLoadingDialog('projectsRegenerateModel');
                else messageHub.hideFormDialog("projectGenerateForm2");
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
                $scope.gmodel.project = '';
                $scope.gmodel.templateId = '';
            });
        }

        // Temp
        // $scope.test = function () {
        //     messageHub.postMessage('projects.tree.select', { filePath: '/ide/index.html' }, true);
        // };

        messageHub.onFileSaved(function (data) {
            const { topic, ...fileDescriptor } = data;
            publisherApi.publish(`/${fileDescriptor.workspace}${fileDescriptor.path}`).then(function (response) {
                if (response.status !== 200)
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
            'projects.tree.refresh',
            function (msg) {
                if (msg.data.name === $scope.selectedWorkspace.name) {
                    $scope.reloadWorkspace();
                }
            },
            true
        );

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
                            if (msg.data.formData[0].value === $scope.selectedWorkspace.name)
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
                            break;
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
                        if (formData.length > 0) {
                            messageHub.updateFormDialog(
                                "projectGenerateForm2",
                                formData,
                                "Generating...",
                            );
                        } else {
                            generateModel();
                        }
                    } else {
                        $scope.gmodel.parameters = {};
                        for (let i = 0; i < msg.data.formData.length; i++) {
                            if (msg.data.formData[i].value)
                                $scope.gmodel.parameters[msg.data.formData[i].id] = msg.data.formData[i].value;
                        }
                        generateModel();
                    }
                } else {
                    messageHub.hideFormDialog('projectGenerateForm2');
                    $scope.gmodel.model = '';
                }
            },
            true
        );

        messageHub.onDidReceiveMessage(
            'projects.regenerate.template',
            function (msg) {
                if (msg.data.buttonId === "b1") {
                    $scope.gmodel.templateId = msg.data.formData[0].value;
                    messageHub.hideFormDialog('projectRegenerateChooseTemplate');
                    messageHub.showLoadingDialog('projectsRegenerateModel', 'Regenerating', 'Regenerating from model');
                    generateModel(true);
                } else messageHub.hideFormDialog('projectRegenerateChooseTemplate');
            },
            true
        );

        messageHub.onDidReceiveMessage(
            "projects.formDialog.create.file",
            function (msg) {
                if (msg.data.buttonId === "b1") {
                    createFile($scope.newNodeData.parent, msg.data.formData[0].value, $scope.newNodeData.workspace, $scope.newNodeData.path, $scope.newNodeData.content);
                    $scope.newNodeData.content = '';
                }
                messageHub.hideFormDialog("projectsNewFileForm");
            },
            true
        );

        messageHub.onDidReceiveMessage(
            "projects.formDialog.create.folder",
            function (msg) {
                if (msg.data.buttonId === "b1") {
                    createFolder($scope.newNodeData.parent, msg.data.formData[0].value, $scope.newNodeData.workspace, $scope.newNodeData.path);
                }
                messageHub.hideFormDialog("projectsNewFolderForm");
            },
            true
        );
        
        messageHub.onDidReceiveMessage(
            "projects.formDialog.action.execute",
            function (msg) {
                if (msg.data.buttonId === "b1") {
                    executeAction($scope.actionData.workspace, $scope.actionData.project, msg.data.formData[0].value);
                }
                messageHub.hideFormDialog("projectsExecuteActionForm");
            },
            true
        );

        messageHub.onDidReceiveMessage(
            "projects.formDialog.rename",
            function (msg) {
                if (msg.data.buttonId === "b1") {
                    workspaceApi.rename(
                        $scope.renameNodeData.text,
                        msg.data.formData[0].value,
                        $scope.renameNodeData.data.path.substring($scope.renameNodeData.data.path.length - $scope.renameNodeData.text.length, 0),
                        $scope.renameNodeData.data.workspace
                    ).then(function (response) {
                        if (response.status === 201) {
                            let guessedPath = `${$scope.renameNodeData.data.path.substring($scope.renameNodeData.data.path.length - $scope.renameNodeData.text.length, 0)}${msg.data.formData[0].value}`; // Temporary until back-end is fixed
                            let node = $scope.jstreeWidget.jstree(true).get_node($scope.renameNodeData);
                            if ($scope.renameNodeData.type === "file") {
                                workspaceApi.getMetadataByPath($scope.renameNodeData.data.workspace, guessedPath).then(function (metadata) {
                                    if (metadata.status === 200) {
                                        messageHub.closeEditor(`/${$scope.renameNodeData.data.workspace}${$scope.renameNodeData.data.path}`);
                                        node.text = metadata.data.name;
                                        node.data.path = metadata.data.path.substring($scope.selectedWorkspace.name.length + 1, metadata.data.path.length);
                                        node.data.contentType = metadata.data.contentType;
                                        node.state.status = metadata.data.status;
                                        node.icon = getFileIcon(metadata.data.name);
                                        $scope.jstreeWidget.jstree(true).redraw_node(node);
                                        messageHub.announceFileRenamed({
                                            oldName: $scope.renameNodeData.text,
                                            name: node.text,
                                            oldPath: $scope.renameNodeData.data.path,
                                            path: node.data.path,
                                            contentType: node.data.contentType,
                                            workspace: node.data.workspace,
                                        });
                                    } else {
                                        messageHub.setStatusError(`Unable to rename '${$scope.renameNodeData.text}'.`);
                                    }
                                });
                            } else {
                                for (let i = 0; i < $scope.renameNodeData.children_d.length; i++) {
                                    let child = $scope.jstreeWidget.jstree(true).get_node($scope.renameNodeData.children_d[i]);
                                    messageHub.closeEditor(`/${child.data.workspace}${child.data.path}`);
                                    child.data.path = guessedPath + child.data.path.substring($scope.renameNodeData.data.path.length);
                                }
                                node.text = msg.data.formData[0].value;
                                node.data.path = guessedPath;
                                $scope.jstreeWidget.jstree(true).redraw_node(node);
                            }
                        } else {
                            messageHub.setStatusError(`Unable to rename '${$scope.renameNodeData.text}'.`);
                        }
                        messageHub.hideFormDialog("projectsRenameForm");
                    });
                } else {
                    messageHub.hideFormDialog("projectsRenameForm");
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
                    $scope.newNodeData.parent = msg.data.data.parent;
                    $scope.newNodeData.workspace = msg.data.data.workspace;
                    $scope.newNodeData.path = msg.data.data.path;
                    $scope.newNodeData.content = '';
                    openNewFileDialog();
                } else if (msg.data.itemId === 'folder') {
                    $scope.newNodeData.parent = msg.data.data.parent;
                    $scope.newNodeData.workspace = msg.data.data.workspace;
                    $scope.newNodeData.path = msg.data.data.path;
                    messageHub.showFormDialog(
                        "projectsNewFolderForm",
                        "Create new folder",
                        [{
                            id: "fdti1",
                            type: "input",
                            submitOnEnterId: "b1",
                            label: "Name",
                            required: true,
                            inputRules: {
                                excluded: getChildrenNames(msg.data.data.parent, 'folder'),
                                patterns: ['^[^/:]*$'],
                            },
                            value: '',
                        }],
                        [{
                            id: "b1",
                            type: "emphasized",
                            label: "Create",
                            whenValid: true
                        },
                        {
                            id: "b2",
                            type: "transparent",
                            label: "Cancel",
                        }],
                        "projects.formDialog.create.folder",
                        "Creating..."
                    );
                } else if (msg.data.itemId === 'rename') {
                    $scope.renameNodeData = msg.data.data;
                    openRenameDialog();
                } else if (msg.data.itemId === 'delete') {
                    openDeleteDialog(msg.data.data)
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
                } else if (msg.data.itemId === 'actionsProject') {
                    $scope.actionData.workspace = msg.data.data.data.workspace;
                    $scope.actionData.project = msg.data.data.text;
                    messageHub.showFormDialog(
                        "projectsExecuteActionForm",
                        "Enter the action to execute",
                        [{
                            id: "fdti1",
                            type: "input",
                            submitOnEnterId: "b1",
                            label: "Name",
                            required: true,
                            inputRules: '',
                            value: '',
                        }],
                        [{
                            id: "b1",
                            type: "emphasized",
                            label: "Execute",
                            whenValid: true
                        },
                        {
                            id: "b2",
                            type: "transparent",
                            label: "Cancel",
                        }],
                        "projects.formDialog.action.execute",
                        "Executing..."
                    );
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
                        if (response.status !== 200)
                            messageHub.setStatusError(`Unable to publish '${msg.data.data}'`);
                        else
                            messageHub.announcePublish();
                    });
                } else if (msg.data.itemId === 'unpublish') {
                    publisherApi.unpublish(msg.data.data).then(function (response) {
                        if (response.status !== 200)
                            messageHub.setStatusError(`Unable to unpublish '${msg.data.data}'`);
                        else
                            messageHub.announceUnpublish();
                    });
                } else if (msg.data.itemId === 'unpublishAll') {
                    $scope.unpublishAll();
                } else if (msg.data.itemId === 'regenerateModel') {
                    messageHub.showLoadingDialog('projectsRegenerateModel', 'Regenerating', 'Loading data');
                    workspaceApi.loadContent(msg.data.data.data.workspace, msg.data.data.data.path).then(function (response) {
                        if (response.status === 200) {
                            $scope.gmodel.project = response.data.projectName;
                            $scope.gmodel.model = response.data.filePath;
                            let { models, perspectives, templateId, filePath, workspaceName, projectName, ...params } = response.data;
                            $scope.gmodel.parameters = params;
                            if (!response.data.templateId) {
                                messageHub.hideLoadingDialog('projectsRegenerateModel');
                                messageHub.showFormDialog(
                                    'projectRegenerateChooseTemplate',
                                    'Choose template',
                                    [{
                                        id: 'pgfd1',
                                        type: 'dropdown',
                                        label: 'Choose template',
                                        required: true,
                                        value: '',
                                        items: getModelTemplates('model'),
                                    }],
                                    [{
                                        id: 'b1',
                                        type: 'emphasized',
                                        label: 'OK',
                                        whenValid: true,
                                    }, {
                                        id: 'b2',
                                        type: 'transparent',
                                        label: 'Cancel',
                                    }],
                                    'projects.regenerate.template',
                                    'Setting template...',
                                );
                            } else {
                                $scope.gmodel.templateId = response.data.templateId;
                                messageHub.updateLoadingDialog('projectsRegenerateModel', 'Regenerating from model');
                                generateModel(true);
                            }
                        } else {
                            messageHub.hideLoadingDialog('projectsRegenerateModel');
                            messageHub.showAlertError('Unable to load file', 'There was an error while loading the file. See the log for more information.');
                            console.error(response);
                        }
                    });
                } else if (msg.data.itemId.startsWith('generate')) {
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
                    if (msg.data.itemId === 'generateGeneric') {
                        let generatePath;
                        const templateItems = getGenericTemplates();
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
                            [{
                                id: 'pgfd1',
                                type: 'dropdown',
                                label: 'Choose template',
                                required: true,
                                value: '',
                                items: templateItems,
                            }, {
                                id: 'pgfd2',
                                type: 'dropdown',
                                label: 'Choose project',
                                required: true,
                                value: project,
                                items: projectNames,
                            }, {
                                id: "pgfi1",
                                type: "input",
                                submitOnEnterId: "b1",
                                label: "File path in project",
                                required: true,
                                placeholder: "/path/file",
                                value: generatePath,
                            }],
                            [{
                                id: 'b1',
                                type: 'emphasized',
                                label: 'OK',
                                whenValid: true,
                            }, {
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
                        const templateItems = getModelTemplates(getFileExtension(msg.data.data.text))
                        messageHub.showFormDialog(
                            'projectGenerateForm2',
                            'Generate from template',
                            [{
                                id: 'pgfd1',
                                type: 'dropdown',
                                label: 'Choose template',
                                required: true,
                                value: '',
                                items: templateItems,
                            }, {
                                id: 'pgfd2',
                                type: 'dropdown',
                                label: 'Choose project',
                                required: true,
                                value: project,
                                items: projectNames,
                            }, {
                                id: "pgfi1",
                                type: "input",
                                submitOnEnterId: "b1",
                                label: "Model (must be in the root of the project)",
                                required: true,
                                inputRules: {
                                    // excluded: [], //TODO
                                    patterns: ['^[^/:]*$'],
                                },
                                placeholder: "file.model",
                                value: msg.data.data.data.path.substring(project.length + 2),
                            }],
                            [{
                                id: 'b1',
                                type: 'emphasized',
                                label: 'OK',
                                whenValid: true,
                            }, {
                                id: 'b2',
                                type: 'transparent',
                                label: 'Cancel',
                            }],
                            'projects.generate.model',
                            'Loading parameters...',
                        );
                    }
                } else {
                    if (msg.data.data.nameless) {
                        createFile(
                            msg.data.data.parent,
                            `.${msg.data.data.extension}`,
                            msg.data.data.workspace,
                            msg.data.data.path,
                            msg.data.data.content
                        );
                    } else {
                        let name;
                        let excludedNames = getChildrenNames(msg.data.data.parent, 'file');
                        let i = 1;
                        if (msg.data.data.extension) {
                            name = `${msg.data.data.name}.${msg.data.data.extension}`;
                            while (excludedNames.includes(name)) {
                                name = `${msg.data.data.name} ${i++}.${msg.data.data.extension}`;
                            }
                        } else {
                            name = msg.data.data.name;
                            while (excludedNames.includes(name)) {
                                name = `${msg.data.data.name} ${i++}`;
                            }
                        }
                        if (msg.data.data.staticName) {
                            createFile(
                                msg.data.data.parent,
                                name,
                                msg.data.data.workspace,
                                msg.data.data.path,
                                msg.data.data.content
                            );
                        } else {
                            $scope.newNodeData.parent = msg.data.data.parent;
                            $scope.newNodeData.workspace = msg.data.data.workspace;
                            $scope.newNodeData.path = msg.data.data.path;
                            $scope.newNodeData.content = msg.data.data.content;
                            messageHub.showFormDialog(
                                "projectsNewFileForm",
                                "Create a new file",
                                [{
                                    id: "fdti1",
                                    type: "input",
                                    submitOnEnterId: "b1",
                                    label: "Name",
                                    required: true,
                                    inputRules: {
                                        excluded: excludedNames,
                                        patterns: ['^[^/:]*$'],
                                    },
                                    value: name,
                                }],
                                [{
                                    id: "b1",
                                    type: "emphasized",
                                    label: "Create",
                                    whenValid: true
                                }, {
                                    id: "b2",
                                    type: "transparent",
                                    label: "Cancel",
                                }],
                                "projects.formDialog.create.file",
                                "Creating..."
                            );
                        }
                    }
                }
            },
            true
        );

        // Initialization
        $scope.init = function () {
            $scope.state.error = false;
            $scope.state.isBusy = true;
            $scope.reloadWorkspaceList();
            $scope.reloadWorkspace(true);
            $scope.loadTemplates();
        };
        $scope.init();
    }]);