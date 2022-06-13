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
    }]).directive('dgContextmenu', ['messageHub', '$window', function (messageHub, $window) {
        return {
            restrict: 'A',
            replace: false,
            scope: {
                callback: '&dgContextmenu',
                includedElements: '=',
                excludedElements: '=',
            },
            link: function (scope, element) {
                scope.callback = scope.callback();
                element.on('contextmenu', function (event) {
                    if (scope.includedElements) {
                        let isIncluded = false;
                        if (scope.includedElements.ids && scope.includedElements.ids.includes(event.target.id)) isIncluded = true;
                        if (!isIncluded && scope.includedElements.classes) {
                            for (let i = 0; i < scope.includedElements.classes.length; i++) {
                                if (event.target.classList.contains(scope.includedElements.classes[i]))
                                    isIncluded = true;
                            }
                        }
                        if (!isIncluded && scope.includedElements.types && scope.includedElements.types.includes(event.target.tagName)) isIncluded = true;
                        if (!isIncluded) return;
                    } else if (scope.excludedElements) {
                        if (scope.excludedElements.ids && scope.excludedElements.ids.includes(event.target.id)) return;
                        if (scope.excludedElements.classes) {
                            for (let i = 0; i < scope.excludedElements.classes.length; i++) {
                                if (event.target.classList.contains(scope.excludedElements.classes[i])) return;
                            }
                        }
                        if (scope.excludedElements.types && scope.excludedElements.types.includes(event.target.tagName)) return;
                    }
                    event.preventDefault();
                    let menu = scope.callback(event.target);
                    if (menu) {
                        let posX;
                        let posY;
                        if ($window.frameElement) {
                            let frame = $window.frameElement.getBoundingClientRect();
                            posX = frame.x + event.clientX;
                            posY = frame.y + event.clientY;
                        } else {
                            posX = event.clientX;
                            posY = event.clientY;
                        }
                        messageHub.postMessage(
                            'ide-contextmenu.open',
                            {
                                posX: posX,
                                posY: posY,
                                callbackTopic: menu.callbackTopic,
                                items: menu.items
                            },
                            true
                        );
                    }
                });
            }
        };
    }]).directive('dgViewTitle', ['view', function (view) {
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