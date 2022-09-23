angular.module('ideView', ['ngResource', 'ideTheming'])
    .constant('view', (typeof viewData != 'undefined') ? viewData : (typeof editorData != 'undefined' ? editorData : ''))
    .constant('extensionPoint', {})
    .factory('Views', ['$resource', "extensionPoint", function ($resource, extensionPoint) {
        let cachedViews;
        let get = function () {
            if (cachedViews) {
                return cachedViews;
            } else {
                let url = '/services/v4/js/resources-core/services/views.js';
                if (extensionPoint && extensionPoint.views) {
                    url = `${url}?extensionPoint=${extensionPoint.views}`;
                }
                return $resource(url).query().$promise
                    .then(function (data) {
                        data = data.map(function (v) {
                            if (!v.id) {
                                console.error(`Views: view '${v.label || 'undefined'}' does not have an id`);
                                return;
                            }
                            if (!v.label) {
                                console.error(`Views: view '${v.id}' does not have a label`);
                                return;
                            }
                            if (!v.link) {
                                console.error(`Views: view '${v.id}' does not have a link`);
                                return;
                            }
                            v.factory = v.factory || 'frame';
                            v.settings = {
                                path: v.link,
                                loadType: (v.lazyLoad ? 'lazy' : 'eager'),
                            };
                            v.region = v.region || 'left';
                            return v;
                        });
                        cachedViews = data;
                        return data;
                    });
            }
        };

        return {
            get: get
        };
    }])
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
    .service('ViewParameters', ['$window', function ($window) {
        return {
            get: function () {
                if ($window.frameElement.hasAttribute("data-parameters")) {
                    return JSON.parse($window.frameElement.getAttribute("data-parameters"));
                }
                return {};
            }
        };
    }])
    .directive('embeddedView', ['Views', 'view', function (Views, view) {
        return {
            restrict: 'E',
            transclude: false,
            replace: true,
            scope: {
                viewId: '@',
                params: '<',
            },
            link: {
                pre: function (scope) {
                    if (scope.params !== undefined && !(typeof scope.params === 'object' && !Array.isArray(scope.params) && scope.params !== null))
                        throw Error("embeddedView: view-parameters must be an object");
                    Views.get().then(function (views) {
                        const embeddedView = views.find(v => v.id === scope.viewId);
                        if (embeddedView) {
                            scope.path = embeddedView.settings.path;
                            scope.loadType = embeddedView.settings.loadType;
                            scope.parameters = {
                                container: 'embedded',
                                viewId: view.id
                            };
                            if (embeddedView.params) {
                                scope.parameters = {
                                    ...scope.parameters,
                                    ...embeddedView.params,
                                };
                            }
                            if (scope.params) {
                                scope.parameters = {
                                    ...scope.parameters,
                                    ...scope.params,
                                }
                            }
                        } else {
                            throw Error(`embeddedView: view with id '${scope.viewId}' not found`);
                        }
                    });

                    scope.getParams = function () {
                        return JSON.stringify(scope.parameters);
                    };
                },
            },
            template: '<iframe loading="{{loadType}}" ng-src="{{path}}" data-parameters="{{getParams()}}"></iframe>'
        }
    }])
    .directive('dgContextmenu', ['messageHub', '$window', function (messageHub, $window) {
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
                if (!view)
                    throw Error("dgViewTitle: Not in view, missing data");
                scope.label = view.label;
            },
            template: '<title>{{label}}</title>'
        };
    }]);