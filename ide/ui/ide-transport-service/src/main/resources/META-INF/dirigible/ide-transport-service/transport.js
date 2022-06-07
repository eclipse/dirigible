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
angular.module('ideTransport', [])
    .provider('transportApi', function TransportApiProvider() {
        this.transportServiceUrl = '/services/v4/transport';
        this.$get = ['$http', '$window', function transportApiFactory($http, $window) {

            let exportProject = function (workspace, projectName) {
                if (!workspace) throw Error("Transport API: You must provide a workspace name");
                if (!projectName) throw Error("Transport API: You must provide a project name");
                let url = new UriBuilder().path(this.transportServiceUrl.split('/')).path('project').path(workspace).path(projectName).build();
                $window.open(url, '_blank');
            }.bind(this);

            let getProjectImportUrl = function () {
                return new UriBuilder().path(this.transportServiceUrl.split('/')).path('project').build();
            }.bind(this);

            return {
                exportProject: exportProject,
                getProjectImportUrl: getProjectImportUrl,
            };
        }];
    });