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
angular.module('ideWorkspace', [])
    .provider('workspaceApi', function WorkspaceApiProvider() {
        this.workspacesServiceUrl = '/services/v8/ide/workspaces/';
        this.workspaceManagerServiceUrl = '/services/v8/ide/workspace';
        this.workspaceSearchServiceUrl = '/services/v8/ide/workspace-search';
        this.$get = ['$http', function workspaceApiFactory($http) {
            let setWorkspace = function (workspaceName) {
                if (workspaceName !== undefined && !(typeof workspaceName === 'string'))
                    throw Error("setWorkspace: workspaceName must be an string");
                localStorage.setItem('DIRIGIBLE.workspace', JSON.stringify({ name: workspaceName }));
            };

            let listWorkspaceNames = function () {
                return $http.get(this.workspacesServiceUrl)
                    .then(function successCallback(response) {
                        return { status: response.status, data: response.data };
                    }, function errorCallback(response) {
                        console.error('Workspace service:', response);
                        return { status: response.status };
                    });
            }.bind(this);

            let load = function (resourcePath) {
                let url = new UriBuilder().path(this.workspacesServiceUrl.split('/')).path(resourcePath.split('/')).build();
                return $http.get(url, { headers: { 'describe': 'application/json' } })
                    .then(function successCallback(response) {
                        return { status: response.status, data: response.data };
                    }, function errorCallback(response) {
                        console.error('Workspace service:', response);
                        return { status: response.status };
                    });
            }.bind(this);

            let getMetadata = function (resourceUrl) {
                return $http.get(resourceUrl, { headers: { 'describe': 'application/json' } })
                    .then(function successCallback(response) {
                        return { status: response.status, data: response.data };
                    }, function errorCallback(response) {
                        console.error('Workspace service:', response);
                        return { status: response.status };
                    });
            }

            let getMetadataByPath = function (workspace, path) {
                let resourceUrl = new UriBuilder().path(this.workspacesServiceUrl.split('/')).path(workspace).path(path.split('/')).build();
                return getMetadata(resourceUrl);
            }.bind(this);

            let rename = function (oldName, newName, path, workspaceName) {
                let pathSegments = path.split('/');
                pathSegments = pathSegments.slice(1);
                if (pathSegments.length >= 1) {
                    let url = new UriBuilder().path(this.workspaceManagerServiceUrl.split('/')).path(workspaceName).path('rename').build();
                    return $http.post(url, {
                        source: new UriBuilder().path(pathSegments).path(oldName).build(),
                        target: new UriBuilder().path(pathSegments).path(newName).build()
                    }).then(function successCallback(response) {
                        return { status: response.status, data: response.data };
                    }, function errorCallback(response) {
                        console.error('Workspace service:', response);
                        return { status: response.status };
                    });
                }
            }.bind(this);

            let remove = function (filepath) {
                let url = new UriBuilder().path(this.workspacesServiceUrl.split('/')).path(filepath.split('/')).build();
                return $http.delete(url, { headers: { 'Dirigible-Editor': 'Workspace' } })
                    .then(function successCallback(response) {
                        return { status: response.status, data: response.data };
                    }, function errorCallback(response) {
                        console.error('Workspace service:', response);
                        return { status: response.status };
                    });
            }.bind(this);

            let copy = function (sourcePath, targetPath, sourceWorkspace, targetWorkspace) {
                let url = new UriBuilder().path(this.workspaceManagerServiceUrl.split('/')).path(targetWorkspace).path('copy').build();
                return $http.post(url, {
                    sourceWorkspace: sourceWorkspace,
                    source: sourcePath,
                    targetWorkspace: targetWorkspace,
                    target: (targetPath.endsWith('/') ? targetPath : targetPath + '/'),
                }).then(function successCallback(response) {
                    return { status: response.status, data: response.data };
                }, function errorCallback(response) {
                    console.error('Workspace service:', response);
                    return { status: response.status };
                });
            }.bind(this);

            let move = function (sourcePath, targetPath, sourceWorkspace, targetWorkspace) {
                // TODO: Move to another workspace
                let url = new UriBuilder().path(this.workspaceManagerServiceUrl.split('/')).path(sourceWorkspace).path('move').build();
                return $http.post(url, {
                    source: sourcePath,
                    target: targetPath,
                }).then(function successCallback(response) {
                    return { status: response.status, data: response.data };
                }, function errorCallback(response) {
                    console.error('Workspace service:', response);
                    return { status: response.status };
                });
            }.bind(this);

            let createNode = function (name, targetPath, isDirectory, content = '') {
                let url = new UriBuilder().path((this.workspacesServiceUrl + targetPath).split('/')).path(name).build();
                if (isDirectory)
                    url += "/";
                return $http.post(url, content, { headers: { 'Dirigible-Editor': 'Workspace', 'Content-Type': 'plain/text' } })
                    .then(function successCallback(response) {
                        return {
                            status: response.status,
                            data: response.config.url,
                        };
                    }, function errorCallback(response) {
                        console.error('Workspace service:', response);
                        return { status: response.status };
                    });
            }.bind(this);

            let createWorkspace = function (workspace) {
                let url = new UriBuilder().path(this.workspacesServiceUrl.split('/')).path(workspace).build();
                return $http.post(url, {})
                    .then(function successCallback(response) {
                        return { status: response.status, data: response.data };
                    }, function errorCallback(response) {
                        console.error('Workspace service:', response);
                        return { status: response.status };
                    });
            }.bind(this);

            let deleteWorkspace = function (workspace) {
                let url = new UriBuilder().path(this.workspacesServiceUrl.split('/')).path(workspace).build();
                return $http.delete(url, { headers: { 'Dirigible-Editor': 'Workspace' } })
                    .then(function successCallback(response) {
                        return { status: response.status, data: response.data };
                    }, function errorCallback(response) {
                        console.error('Workspace service:', response);
                        return { status: response.status };
                    });
            }.bind(this);

            let createProject = function (workspace, projectName) {
                let url = new UriBuilder().path(this.workspacesServiceUrl.split('/')).path(workspace).path(projectName).build();
                return $http.post(url, {})
                    .then(function successCallback(response) {
                        return { status: response.status, data: response.data };
                    }, function errorCallback(response) {
                        console.error('Workspace service:', response);
                        return { status: response.status };
                    });
            }.bind(this);

            let linkProject = function (workspace, projectName, path) {
                let url = new UriBuilder().path(this.workspaceManagerServiceUrl.split('/')).path(workspace).path('linkProject').build();
                return $http.post(url, {
                    source: projectName,
                    target: path
                }).then(function successCallback(response) {
                    return { status: response.status, data: response.data };
                }, function errorCallback(response) {
                    console.error('Workspace service:', response);
                    return { status: response.status };
                });
            }.bind(this);

            let deleteProject = function (workspace, projectName) {
                let url = new UriBuilder().path(this.workspacesServiceUrl.split('/')).path(workspace).path(projectName).build();
                return $http.delete(url, { headers: { 'Dirigible-Editor': 'Workspace' } })
                    .then(function successCallback(response) {
                        return { status: response.status, data: response.data };
                    }, function errorCallback(response) {
                        console.error('Workspace service:', response);
                        return { status: response.status };
                    });
            }.bind(this);

            let search = function (workspace, resourcePath, searchTerm) {
                let url = new UriBuilder().path(this.workspaceSearchServiceUrl.split('/')).path(workspace).path(resourcePath.split('/')).build();
                return $http.post(url, searchTerm)
                    .then(function successCallback(response) {
                        return { status: response.status, data: response.data };
                    }, function errorCallback(response) {
                        console.error('Workspace service:', response);
                        return { status: response.status };
                    });
            }.bind(this);

            return {
                setWorkspace: setWorkspace,
                listWorkspaceNames: listWorkspaceNames,
                load: load,
                getMetadata: getMetadata,
                getMetadataByPath: getMetadataByPath,
                rename: rename,
                remove: remove,
                copy: copy,
                move: move,
                createNode: createNode,
                createWorkspace: createWorkspace,
                deleteWorkspace: deleteWorkspace,
                createProject: createProject,
                linkProject: linkProject,
                deleteProject: deleteProject,
                search: search,
            };
        }];
    });