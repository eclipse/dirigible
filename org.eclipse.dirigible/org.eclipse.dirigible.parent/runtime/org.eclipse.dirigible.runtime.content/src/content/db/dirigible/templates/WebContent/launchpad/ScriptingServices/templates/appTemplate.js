#set ( $dollar = "$")

/*globals angular */
var app = angular.module('launchpad', ['ngRoute', 'ng', 'defaultControllers']);

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

controllers.controller('LaunchpadCtrl', ['${dollar}scope', '${dollar}location', '${dollar}http',
	function(${dollar}scope, ${dollar}location, ${dollar}http) {
		const API_HOME = '../../js/${packageName}/api/home.js';
		const API_MENU = '../../js/${packageName}/api/menu.js';

		${dollar}scope.location = ${dollar}location;

		${dollar}http.get(API_HOME).success(function (data) {
			${dollar}scope.homeData = data;
			loadPageFromPath(${dollar}scope.homeData);
		});

		${dollar}http.get(API_MENU).success(function (data) {
			${dollar}scope.menu = data;
			loadPageFromPath(${dollar}scope.menu);
		});

		${dollar}scope.setView = function(view) {
			${dollar}scope.view = view;
		};

		${dollar}scope.setHomeView = function() {
			${dollar}scope.setView('home.html');
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
