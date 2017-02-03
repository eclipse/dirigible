
/*globals angular */
var app = angular.module('launchpad', ['ngRoute', 'ng', 'defaultControllers']);

var controllers = angular.module('defaultControllers', []);

app.config(function($routeProvider) {
	$routeProvider.when('/home', {
		templateUrl: 'templates/home.html'
	})
	+++_ROUTES_PLACEHOLDER_+++
	.otherwise({
		redirectTo: '/home'
	});
});

controllers.controller('LaunchpadCtrl', ['$scope', '$location', '$http',
	function($scope, $location, $http) {
		const API_HOME = '../../js/iam/api/home.js';
		const API_MENU = '../../js/iam/api/menu.js';

		$scope.location = $location;

		$http.get(API_HOME).success(function (data) {
			$scope.homeData = data;
			loadPageFromPath($scope.homeData);
		});

		$http.get(API_MENU).success(function (data) {
			$scope.menu = data;
			loadPageFromPath($scope.menu);
		});

		$scope.setView = function(view) {
			$scope.view = view;
		};

		$scope.setHomeView = function() {
			$scope.setView('home.html');
		};

		function loadPageFromPath(data) {
			for (var i = 0; i < data.length; i ++) {
				if (data[i].path === $scope.location.path()) {
					$scope.setView(data[i].link);
				}
			}
		}
	}
]);
