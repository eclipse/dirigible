angular.module('entityApi', [])
    .provider('entityApi', function EntityApiProvider() {
        this.baseUrl = '';
        this.$get = ['$http', function workspaceApiFactory($http) {

            const count = function (idOrFilter) {
                let url = `${this.baseUrl}/count`;
                if (idOrFilter != null && typeof idOrFilter === 'object') {
                    const query = Object.keys(idOrFilter).map(e => idOrFilter[e] ? `${e}=${idOrFilter[e]}` : null).filter(e => e !== null).join('&');
                    if (query) {
                        url = `${this.baseUrl}/count?${query}`;
                    }
                } else if (idOrFilter != null) {
                    url = `${this.baseUrl}/count/${idOrFilter}`;
                }
                return $http.get(url, { headers: { 'describe': 'application/json' } })
                    .then(function successCallback(response) {
                        return { status: response.status, data: response.data };
                    }, function errorCallback(response) {
                        console.error('Entity Service:', response);
                        return { status: response.status, message: response.data ? response.data.message : '' };
                    });
            }.bind(this);

            const list = function (offsetOrFilter, limit) {
                let url = this.baseUrl;
                if (offsetOrFilter != null && typeof offsetOrFilter === 'object') {
                    const query = Object.keys(offsetOrFilter).map(e => offsetOrFilter[e] ? `${e}=${offsetOrFilter[e]}` : null).filter(e => e !== null).join('&');
                    if (query) {
                        url = `${this.baseUrl}?${query}`;
                    }
                } else if (offsetOrFilter != null && limit != null) {
                    url = `${url}?$offset=${offsetOrFilter}&$limit=${limit}`;
                }
                return $http.get(url, { headers: { 'describe': 'application/json' } })
                    .then(function successCallback(response) {
                        return { status: response.status, data: response.data };
                    }, function errorCallback(response) {
                        console.error('Entity service:', response);
                        return { status: response.status, message: response.data ? response.data.message : '' };
                    });
            }.bind(this);

            const filter = function (query, offset, limit) {
                const url = `${this.baseUrl}?${query}&$offset=${offset}&$limit=${limit}`;
                return $http.get(url, { headers: { 'describe': 'application/json' } })
                    .then(function successCallback(response) {
                        return { status: response.status, data: response.data };
                    }, function errorCallback(response) {
                        console.error('Entity service:', response);
                        return { status: response.status, message: response.data ? response.data.message : '' };
                    });
            }.bind(this);

            const search = function (entity) {
                const url = `${this.baseUrl}/search`;
                const body = JSON.stringify(entity);
                return $http.post(url, body)
                    .then(function (response) {
                        return { status: response.status, data: response.data };
                    }, function (response) {
                        console.error('Entity service:', response);
                        return { status: response.status, message: response.data ? response.data.message : '' };
                    });
            }.bind(this);

            const create = function (entity) {
                const url = this.baseUrl;
                const body = JSON.stringify(entity);
                return $http.post(url, body)
                    .then(function (response) {
                        return { status: response.status, data: response.data };
                    }, function (response) {
                        console.error('Entity service:', response);
                        return { status: response.status, message: response.data ? response.data.message : '' };
                    });
            }.bind(this);

            const update = function (id, entity) {
                const url = `${this.baseUrl}/${id}`;
                const body = JSON.stringify(entity);
                return $http.put(url, body)
                    .then(function (response) {
                        return { status: response.status, data: response.data };
                    }, function (response) {
                        console.error('Entity service:', response);
                        return { status: response.status, message: response.data ? response.data.message : '' };
                    });
            }.bind(this);

            const deleteEntity = function (id) {
                const url = `${this.baseUrl}/${id}`;
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
                search: search,
                create: create,
                update: update,
                'delete': deleteEntity
            };
        }];
    })