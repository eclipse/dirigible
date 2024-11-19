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
const welcome = angular.module('welcome', ['blimpKit', 'platformView', 'PublisherService', 'WorkspaceService', 'TemplatesService']);
welcome.filter('startFrom', () => {
    return (input, start) => {
        start = +start;
        if (input) return input.slice(start);
        return 0;
    };
});
welcome.controller('WelcomeController', ($scope, $http, PublisherService, WorkspaceService, TemplatesService) => {
    const dialogApi = new DialogApi();
    const statusBarApi = new StatusBarApi();
    const workspaceApi = new WorkspaceApi();
    $scope.templates = [];
    const workspaces = [];
    const selectedWorkspace = WorkspaceService.getCurrentWorkspace();
    $scope.filteredTemplates = [];
    $scope.search = {
        text: '',
        applyFilter: () => {
            const searchText = $scope.search.text.toLowerCase();
            $scope.filteredTemplates = searchText ? $scope.templates.filter(t => t.name.toLowerCase().includes(searchText)) : $scope.templates;
            $scope.currentPage = 1;
        }
    };
    $scope.currentPage = 1;
    $scope.pageSize = 6;

    const getTemplateFormItems = (template) => {
        let form = {
            'workspace': {
                controlType: 'dropdown',
                label: 'Workspace',
                required: true,
                value: selectedWorkspace.name,
                options: workspaces,
            },
            'projectName': {
                controlType: 'input',
                type: 'text',
                label: 'Project',
                placeholder: 'Project name',
                required: true,
            },
            'fileName': {
                controlType: 'input',
                type: 'text',
                label: 'File',
                placeholder: 'file name',
                required: true,
            },
        };
        for (let i = 0; i < template.length; i++) {
            form[`param_${p.name}`] = {
                controlType: 'input',
                type: 'text',
                label: template[i].label,
                require: true,
            }
        }
        return form;
    };

    TemplatesService.listTemplates().then((response) => {
        if (response.status === 200) {
            $scope.templates.push(...response.data.filter(value => !value.extension));
            $scope.search.applyFilter();
        } else dialogApi.showAlert({
            title: 'Template API error',
            message: 'Unable to load template list',
            type: AlertTypes.Error,
        });
    });

    WorkspaceService.listWorkspaceNames().then((response) => {
        if (response.status === 200) {
            workspaces.push(...response.data.map(x => ({ label: x, value: x })));
        } else dialogApi.showAlert({
            title: 'Workspace API error',
            message: 'Unable to load workspace list',
            type: AlertTypes.Error,
        });
    });

    $scope.openCreateProjectDialog = (template) => {
        $scope.selectedTemplate = template;
        dialogApi.showFormDialog({
            title: 'Create from template',
            subheader: template.name,
            form: getTemplateFormItems(template),
            submitLabel: 'Create',
            cancelLabel: 'Cancel'
        }).then(async (form) => {
            if (form) {
                dialogApi.showBusyDialog('Creating...');
                try {
                    await createFromTemplate(form);
                } catch (ex) {
                    console.error(ex);
                    dialogApi.showAlert({
                        title: 'Failed to create project',
                        message: ex.message,
                        type: AlertTypes.Error,
                    });
                    statusBarApi.showError('Failed to create project');
                } finally {
                    statusBarApi.showMessage(`Created new project in '${form.workspace}'`);
                    dialogApi.closeBusyDialog();
                }
            }
        }, (error) => {
            console.error(error);
        });
    };

    const createFromTemplate = (formData) => {
        if (!$scope.selectedTemplate)
            return Promise.reject(new Error('No template selected'));

        const projectName = formData.projectName;
        const workspace = formData.workspace;
        const fileName = formData.fileName;
        const parameters = [];
        for (const [key, value] of Object.entries(formData)) {
            let param;
            if (key.startsWith('param_')) {
                param[key.substring(6)] = value;
                parameters.push(param);
            }
        }

        const url = `/services/ide/generate/file/${workspace}/${projectName}/${fileName}`;

        return new Promise((resolve, reject) => {
            $http.post(url, { template: $scope.selectedTemplate.id, parameters })
                .then(response => {
                    workspaceApi.announceWorkspaceChanged({ workspace: workspace, params: { publish: { path: `/${projectName}` } } });
                    if (PublisherService.isEnabled()) {
                        PublisherService.publish(`/${workspace}/${projectName}`).then((response) => {
                            if (response.status !== 200)
                                statusBarApi.showError(`Unable to publish project '${projectName}'`);
                            workspaceApi.announcePublished({ path: `/${projectName}`, workspace: workspace });
                        });
                    }
                    resolve(response.data);
                }).catch(ex => {
                    reject(ex);
                });
        });
    };
});