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
angular.module('idePublisher', [])
    .provider('publisherApi', function PublisherApiProvider() {
        this.publisherServiceUrl = '/services/v4/ide/publisher/request';
        this.$get = ['$http', function publisherApiFactory($http) {

            let publish = function (resourcePath) {
                let url = new UriBuilder().path(this.publisherServiceUrl.split('/')).path(resourcePath.split('/')).build();
                return $http.post(url, {}, {
                    headers: {
                        "Dirigible-Editor": "Publish"
                    }
                }).then(function successCallback(response) {
                    return { status: response.status, data: response.data };
                }, function errorCallback(response) {
                    console.error('Publisher service:', response);
                    return { status: response.status };
                });
            }.bind(this);

            let unpublish = function (resourcePath) {
                let url = new UriBuilder().path(this.publisherServiceUrl.split('/')).path(resourcePath.split('/')).build();
                return $http.delete(url, {
                    headers: {
                        "Dirigible-Editor": "Publish"
                    }
                }).then(function successCallback(response) {
                    return { status: response.status, data: response.data };
                }, function errorCallback(response) {
                    console.error('Publisher service:', response);
                    return { status: response.status };
                });
            }.bind(this);

            return {
                publish: publish,
                unpublish: unpublish,
            };
        }];
    });