angular.module('entityApi', [])
    .provider('entityApi', function EntityApiProvider() {
        this.baseUrl = '';
        this.$get = ['$http', function workspaceApiFactory($http) {

            let count = function (entityId) {
                let url = `${this.baseUrl}/count${entityId ? `/${entityId}` : ''}`;
                return $http.get(url, { headers: { 'describe': 'application/json' } })
                    .then(function successCallback(response) {
                        return { status: response.status, data: response.data };
                    }, function errorCallback(response) {
                        console.error('Entity Service:', response);
                        return { status: response.status, message: response.data ? response.data.message : '' };
                    });
            }.bind(this);

            let list = function (offset, limit) {
                let url = this.baseUrl;
                if (offset != null && limit != null) {
                    url = `${url}?$offset=${offset}&$limit=${limit}`;
                }
                return $http.get(url, { headers: { 'describe': 'application/json' } })
                    .then(function successCallback(response) {
                        return { status: response.status, data: response.data };
                    }, function errorCallback(response) {
                        console.error('Entity service:', response);
                        return { status: response.status, message: response.data ? response.data.message : '' };
                    });
            }.bind(this);

            let filter = function (query, offset, limit) {
                let url = `${this.baseUrl}?${query}&$offset=${offset}&$limit=${limit}`;
                return $http.get(url, { headers: { 'describe': 'application/json' } })
                    .then(function successCallback(response) {
                        return { status: response.status, data: response.data };
                    }, function errorCallback(response) {
                        console.error('Entity service:', response);
                        return { status: response.status, message: response.data ? response.data.message : '' };
                    });
            }.bind(this);

            let create = function (entity) {
                let url = this.baseUrl;
                let body = JSON.stringify(entity);
                return $http.post(url, body)
                    .then(function (response) {
                        return { status: response.status, data: response.data };
                    }, function (response) {
                        console.error('Entity service:', response);
                        return { status: response.status, message: response.data ? response.data.message : '' };
                    });
            }.bind(this);

            let update = function (id, entity) {
                let url = `${this.baseUrl}/${id}`;
                let body = JSON.stringify(entity);
                return $http.put(url, body)
                    .then(function (response) {
                        return { status: response.status, data: response.data };
                    }, function (response) {
                        console.error('Entity service:', response);
                        return { status: response.status, message: response.data ? response.data.message : '' };
                    });
            }.bind(this);

            let deleteEntity = function (id) {
                let url = `${this.baseUrl}/${id}`;
                return $http.delete(url, { headers: { 'describe': 'application/json' } })
                    .then(function successCallback(response) {
                        return { status: response.status, data: response.data };
                    }, function errorCallback(response) {
                        console.error('Entity service:', response);
                        return { status: response.status, message: response.data ? response.data.message : '' };
                    });
            }.bind(this);

            return {
                count: count,
                list: list,
                filter: filter,
                create: create,
                update: update,
                'delete': deleteEntity
            };
        }];
    })