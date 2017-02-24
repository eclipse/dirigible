#set ( $dollar = "$")

/*globals angular */
var app = angular.module('cockpit', ['ngRoute', 'ng', 'defaultControllers']);

var controllers = angular.module('defaultControllers', []);

app.config(function(${dollar}routeProvider) {
	${dollar}routeProvider.when('/home', {
		templateUrl: 'templates/home.html'
	})
	+++_ROUTES_PLACEHOLDER_+++
	.otherwise({
		redirectTo: '/home'
	});
});

controllers.controller('CockpitCtrl', ['${dollar}scope', '${dollar}location', '${dollar}http',
	function(${dollar}scope, ${dollar}location, ${dollar}http) {
		const API_SIDEBAR = '../../js/${packageName}/api/sidebar.js';
		const API_MENU = '../../js/${packageName}/api/menu.js';

		${dollar}scope.location = ${dollar}location;

		${dollar}http.get(API_SIDEBAR).success(function (data) {
			${dollar}scope.sidebar = data;
			loadPageFromPath(${dollar}scope.sidebar);
		});

		${dollar}http.get(API_MENU).success(function (data) {
			${dollar}scope.menu = data;
			loadPageFromPath(${dollar}scope.menu);
		});

		${dollar}scope.setView = function(view) {
			${dollar}scope.view = view;
		};

		${dollar}scope.setSidebarView = function() {
			${dollar}scope.setView('sidebar.html');
		};

		function loadPageFromPath(data) {
			for (var i = 0; i < data.length; i ++) {
				if (data[i].path === ${dollar}scope.location.path()) {
					${dollar}scope.setView(data[i].link);
				}
			}
		}
	}
]);
