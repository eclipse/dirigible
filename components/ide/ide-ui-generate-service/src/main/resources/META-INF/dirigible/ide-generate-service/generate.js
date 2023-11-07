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
angular.module('ideGenerate', [])
    .provider('generateApi', function GenerateApiProvider() {
        this.generateServiceUrl = '/services/ide/generate';
        this.generateModelServiceUrl = '/services/js/ide-generate-service/generate.mjs';
        this.$get = ['$http', function generateApiFactory($http) {
	
            let generateFromTemplate = function (workspace, project, file, template, parameters = []) {
                let url = new UriBuilder().path(this.generateServiceUrl.split('/')).path('file').path(workspace).path(project).path(file.split('/')).build();
                if (parameters.length === 0) {
					parameters = {"__empty": ""};
				}
                return $http.post(url, { "template": template, "parameters": parameters })
                    .then(function successCallback(response) {
                        return { status: response.status, data: response.data };
                    }, function errorCallback(response) {
                        console.error('Publisher service:', response);
                        return { status: response.status };
                    });
            }.bind(this);

            let generateFromModel = function (workspace, project, file, template, parameters = []) {
                let url = new UriBuilder().path(this.generateModelServiceUrl.split('/')).path('model').path(workspace).path(project).build();
                url = `${url}?path=${file.split('/')}`;
                if (parameters.length === 0) {
					parameters = {"__empty": ""};
				}
                return $http.post(url, { "template": template, "parameters": parameters, "model": file })
                    .then(function successCallback(response) {
                        return { status: response.status, data: response.data };
                    }, function errorCallback(response) {
                        console.error('Publisher service:', response);
                        return { status: response.status };
                    });
            }.bind(this);
            
            let isEnabled = function() {
				return true;
			}.bind(this);

            return {
                generateFromTemplate: generateFromTemplate,
                generateFromModel: generateFromModel,
                isEnabled: isEnabled
            };
        }];
    });