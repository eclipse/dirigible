angular.module('ideView', ['ngResource', 'ideTheming'])
    .constant('view', (typeof viewData != 'undefined') ? viewData : editorData)
    .factory('baseHttpInterceptor', function () {
        let csrfToken = null;
        return {
            request: function (config) {
                config.headers['X-Requested-With'] = 'Fetch';
                config.headers['X-CSRF-Token'] = csrfToken ? csrfToken : 'Fetch';
                return config;
            },
            response: function (response) {
                let token = response.headers()['x-csrf-token'];
                if (token) {
                    csrfToken = token;
                    uploader.headers['X-CSRF-Token'] = csrfToken;
                }
                return response;
            }
        };
    })
    .config(['$httpProvider', function ($httpProvider) {
        $httpProvider.interceptors.push('baseHttpInterceptor');
    }])
    .directive('dgViewTitle', ['view', function (view) {
        return {
            restrict: 'A',
            transclude: false,
            replace: true,
            link: function (scope) {
                scope.label = view.label;
            },
            template: '<title>{{label}}</title>'
        };
    }]);