var workspaceServices = angular.module('workspaceServices', ['ngResource']);

workspaceServices.factory('FilesSearch', ['$resource',
  function($resource) {
    return $resource('../search');
  }
]);


var workspaceControllers = angular.module('workspaceControllers', []);
workspaceControllers.controller('WorkspaceListCtrl', ['$scope', '$sce', 'FilesSearch', '$http', 

  function($scope, $sce, FilesSearch, $http) {
    var backupRoot;
    var timeOutDelay;

    $scope.caseSensitive = false;
    $scope.mainError = undefined;
    $scope.searchError = undefined;
    $scope.search = undefined;
    
    $scope.mapping = {
			"application/javascript": ["js"],
			//"sql": ["sql"],
			"application/json": ["json", "odata", "ws", "table", "view", "entity", "menu", "access", "extensionpoint", "extension", "command", "flow", "job"],
			"application/xml": ["xml", "xsd", "wsdl", "xsl", "xslt", "routes"],
			"text/html": ["html"],
			"text/x-java-source": ["java"],
			"text/css": ["css"],
			//"markdown": ["markdown", "mdown", "mkdn", "md", "mkd", "mdwn"],
			//"textile": ["textile"],
			"text/plain": ["txt"]
		};

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
        
        $http({
        	  method: 'GET',
        	  url: newData.path,
        	  transformResponse: [function (data) {
        	      return data;
        	  }]
        	}).then(function successCallback(response) {
        		setText(response.data, $scope.getModeModule(newData.path));
        		http = $http;
        		path = newData.path;
        	  }, function errorCallback(response) {
        		  $scope.mainError = "Error loading " + newData.path;
        	  });
        
        
      } else if (newData.files) {
        $scope.selected = newData;
        $scope.paths.push(newData);
      }
    };
    
    $scope.getModeModule = function(resourcePath){
		var m = resourcePath.match(/(.*)[\/\\]([^\/\\]+)\.(\w+)$/);
		var ext;
		if(m && m.length>3 && m[3])
			ext = m[3];
		else
			ext = "txt";
		var modules = Object.keys($scope.mapping);
		var module;
		for(var i in modules){
			if($scope.mapping[modules[i]].indexOf(ext)>-1){
				module = modules[i];
				return module;
			}
		} 
		return "text";//default
    };

    $scope.copyFile = function(file) {
      window.prompt("Copy to clipboard: Ctrl+C, Enter", file.path);
    };

    $scope.crumbsChanged = function(path) {
      var inx = this.paths.indexOf(path);
      $scope.paths.splice(inx + 1);
      $scope.selected = this.paths[inx];
    }

    $scope.securedUrl = function(src) {
      return $sce.trustAsResourceUrl(src);
    }

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


workspaceControllers.controller('WorkspaceCtrl', function($scope, $resource) {
  $scope.objectContent = true;
  $scope.restService = $resource('../workspace');
});
