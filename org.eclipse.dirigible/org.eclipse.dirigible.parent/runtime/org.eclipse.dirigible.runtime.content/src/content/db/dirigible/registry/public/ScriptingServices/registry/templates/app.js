/*eslint-env browser, jquery*/
/*globals angular*/

var registryApp = angular.module('registryApp', ['ngRoute', 'defaultServices', 'workspaceServices',
  'menuControllers', 'defaultControllers', 'workspaceControllers', 'angularFileUpload'
]);

angular.module('workspaceServices', ['ngResource']).factory('FilesSearch', ['$resource', function($resource) {
	return $resource('../searchw');
}]);

var defaultServices = angular.module('defaultServices', ['ngResource']);
var controllers = angular.module('defaultControllers', []);
var menuControllers = angular.module('menuControllers', []);
var workspaceControllers = angular.module('workspaceControllers', []);

defaultServices.factory('FilesSearch', ['$resource',
  function($resource) {
    return $resource('../search');
  }
]);

controllers.controller('DefaultListCtrl', ['$scope', '$sce', 'FilesSearch',

  function($scope, $sce, FilesSearch) {
    /*    if (!$routeParams.pathName) {
      $routeParams.pathName = 'content';
    }*/
    var backupRoot;
    var timeOutDelay;

    $scope.caseSensitive = false;
    $scope.mainError = undefined;
    $scope.searchError = undefined;
    $scope.search = undefined;

    if ($scope.objectContent) {
      $scope.restService.get({}, function(data) {
        $scope.mainError = undefined;
        backupRoot = $scope.selected = data;
        $scope.paths = [data];
      }, onError);
    } else {
      $scope.restService.query({}, onArrayQuery, onError);
    }

    $scope.change = function(newData) {
      if (!newData.folder) {
        $scope.iframeSrc = newData.path;
      } else if (newData.files) {
        $scope.selected = newData;
        $scope.paths.push(newData);
      }
    };

    $scope.copyFile = function(file) {
      window.prompt("Copy to clipboard: Ctrl+C, Enter", file.path);
    };

    $scope.crumbsChanged = function(path) {
      var inx = this.paths.indexOf(path);
      $scope.paths.splice(inx + 1);
      $scope.selected = this.paths[inx];
    };

    $scope.securedUrl = function(src) {
      return $sce.trustAsResourceUrl(src);
    };

    $scope.$watch('search', function(newVal, oldVal) {
      if (!oldVal) {
        return;
      }
      if (!newVal) {
        $scope.searchError = undefined;
        $scope.selected = backupRoot;
        return;
      }
      clearTimeout(timeOutDelay);
      timeOutDelay = setTimeout(function() {
        FilesSearch.query({
          q: newVal
        }, onArrayQuery, function(er) {
          $scope.searchError = er;
        });
      }, 300);
    });

    function onArrayQuery(data) {
      $scope.searchError = undefined;
      $scope.paths = undefined;
      $scope.selected = {
        files: data
      };
    }

    function onError(er) {
      $scope.mainError = er;
    }
  }
]);

controllers.controller('MonitoringAccessCtrl', function($scope, $resource) {
  $resource('../../acclog').query({}, function(data) {
    $scope.logs = data;
  });
});

menuControllers.controller('UserCtrl', ['$scope', '$http',
	function($scope, $http) {
		$scope.name = "Unknown";
		$http.get('../../op?user').success(function(data) {
			$scope.name = data;
		});
	}
]);
