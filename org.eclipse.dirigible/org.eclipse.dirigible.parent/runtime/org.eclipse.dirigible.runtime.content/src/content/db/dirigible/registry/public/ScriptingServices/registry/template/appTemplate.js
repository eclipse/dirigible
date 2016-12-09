/*eslint-env browser, jquery*/
/*globals angular*/

var app = angular.module('registry', ['ngRoute', 'defaultServices', 'workspaceServices', 'defaultControllers', 'angularFileUpload']);

angular.module('workspaceServices', ['ngResource']).factory('FilesSearch', ['$resource', function($resource) {
	return $resource('../searchw');
}]);

angular.module('defaultServices', ['ngResource']).factory('FilesSearch', ['$resource', function($resource) {
    return $resource('../search');
}]);

var controllers = angular.module('defaultControllers', []);

app.config(function($routeProvider) {

	$routeProvider
${routes}
.otherwise({
	redirectTo: '/home'
});
});

${controllers}
