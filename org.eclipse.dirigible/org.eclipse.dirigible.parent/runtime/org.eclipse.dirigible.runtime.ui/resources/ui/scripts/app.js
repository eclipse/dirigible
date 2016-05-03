var fileApp = angular.module('fileApp', ['ngRoute', 'defaultServices', 'workspaceServices',
  'menuControllers', 'defaultControllers', 'workspaceControllers', 'angularFileUpload'
]);

fileApp.config(function($routeProvider) {
  $routeProvider
    .when('/home', {
      controller: 'HomeCtrl',
      templateUrl: 'templates/home/home.html'
    }).when('/home/develop', {
        controller: 'DevelopCtrl',
        templateUrl: 'templates/home/develop/develop.html'
    }).when('/home/discover', {
        controller: 'DiscoverCtrl',
        templateUrl: 'templates/home/discover/discover.html'
    }).when('/home/operate', {
        controller: 'OperateCtrl',
        templateUrl: 'templates/home/operate/operate.html'
    }).when('/workspace', {
      controller: 'WorkspaceCtrl',
      templateUrl: 'templates/workspace/workspace.html'
    }).when('/content', {
        controller: 'ContentCtrl',
        templateUrl: 'templates/content/content.html'
    }).when('/content/import', {
      controller: 'ImportCtrl',
      templateUrl: 'templates/content/import/import.html'
    }).when('/content/clone', {
      controller: 'CloneCtrl',
      templateUrl: 'templates/content/import/import.html'
    }).when('/content/project', {
      controller: 'ProjectCtrl',
      templateUrl: 'templates/content/import/import.html'
    }).when('/web/content', {
      controller: 'WebContentCtrl',
      templateUrl: 'templates/web/content/content.html'
    }).when('/web/wiki', {
      controller: 'WebWikiCtrl',
      templateUrl: 'templates/web/wiki/wiki.html'
    }).when('/mobile', {
        controller: 'MobileCtrl',
        templateUrl: 'templates/mobile/mobile.html'
    }).when('/scripting/javascript', {
      controller: 'JavaScriptCtrl',
      templateUrl: 'templates/scripting/javascript/javascript.html'
//    }).when('/scripting/groovy', {
//      controller: 'GroovyCtrl',
//      templateUrl: 'templates/scripting/groovy/groovy.html'
//    }).when('/scripting/java', {
//      controller: 'JavaCtrl',
//      templateUrl: 'templates/scripting/java/java.html'
    }).when('/scripting/command', {
      controller: 'CommandCtrl',
      templateUrl: 'templates/scripting/command/command.html'
    }).when('/scripting/sql', {
        controller: 'SQLCtrl',
        templateUrl: 'templates/scripting/sql/sql.html'
    }).when('/scripting/tests', {
      controller: 'TestsCtrl',
      templateUrl: 'templates/scripting/tests/tests.html'
//    }).when('/scripting/ruby', {
//      controller: 'RubyCtrl',
//      templateUrl: 'templates/scripting/ruby/ruby.html'
//    }).when('/routes', {
//      controller: 'RoutesCtrl',
//      templateUrl: 'templates/routes/routes.html'
    }).when('/integration/flow', {
      controller: 'FlowCtrl',
      templateUrl: 'templates/integration/flows/flows.html'
    }).when('/integration/job', {
      controller: 'JobCtrl',
      templateUrl: 'templates/integration/jobs/jobs.html'
    }).when('/integration/listener', {
        controller: 'ListenerCtrl',
        templateUrl: 'templates/integration/listeners/listeners.html'
    }).when('/monitoring', {
      controller: 'MonitoringCtrl',
      templateUrl: 'templates/monitoring/monitoring.html'
    }).when('/monitoring/manage', {
      controller: 'MonitoringManageCtrl',
      templateUrl: 'templates/monitoring/manage/manage.html'
    }).when('/monitoring/hits', {
      templateUrl: 'templates/monitoring/hits/hits.html'
    }).when('/monitoring/response', {
      templateUrl: 'templates/monitoring/response/response.html'
    }).when('/monitoring/memory', {
      templateUrl: 'templates/monitoring/memory/memory.html'
    }).when('/monitoring/acclog', {
      controller: 'MonitoringAccessCtrl',
      templateUrl: 'templates/monitoring/acclog/acclog.html'
    }).when('/monitoring/logging', {
      templateUrl: 'templates/monitoring/logging/logging.html'
    }).when('/monitoring/log-console', {
        templateUrl: 'templates/monitoring/logging/log-console.html'
    }).when('/monitoring/log', {
        templateUrl: 'templates/monitoring/logging/log.html'
    }).otherwise({
      redirectTo: '/home'
    });
}).controller('ImportCtrl', function($scope, FileUploader) {
  $scope.pageHeader = 'Import Registry Content';
  $scope.exportTitle = 'Export Registry Content';
  $scope.exportUrl = '../export';
  $scope.exportButtonText = 'Download Zipped Registry Content';
  $scope.overrideContent = false;

  $scope.uploader = new FileUploader({
    url: '../import?override=false'
  });

  $scope.$watch('overrideContent', function (newVal) {
    $scope.uploader.url = '../import?override=' + newVal;
    for (var i=0; i<$scope.uploader.queue.length; i++) {
      $scope.uploader.queue[i].url = $scope.uploader.url;
    }
  });

  $scope.uploader.filters.push({
    name: 'onlyZip',
    fn: function(item) {
      return item.name.lastIndexOf(".zip") === item.name.length - 4;
    }
  });
}).controller('CloneCtrl', function($scope, FileUploader) {
  $scope.pageHeader = 'Import Cloned Content';
  $scope.exportTitle = 'Export Cloned Content';
  $scope.exportUrl = '../clone-export';
  $scope.exportButtonText = 'Download Zipped Cloned Content';
  $scope.overrideContent = false;

  var uploader = $scope.uploader = new FileUploader({
    url: '../clone-import?reset=false'
  });

  $scope.$watch('overrideContent', function (newVal) {
    $scope.uploader.url = '../clone-import?reset=' + newVal;
    for (var i=0; i<$scope.uploader.queue.length; i++) {
      $scope.uploader.queue[i].url = $scope.uploader.url;
    }
  });

  uploader.filters.push({
    name: 'onlyZip',
    fn: function(item) {
      return item.name.lastIndexOf('.zip') === item.name.length - 4;
    }
  });
}).controller('ProjectCtrl', function($scope, FileUploader) {
	  $scope.pageHeader = 'Import Project Content';
	  $scope.overrideContent = false;

	  var uploader = $scope.uploader = new FileUploader({
	    url: '../project-import?reset=false'
	  });

	  $scope.$watch('overrideContent', function (newVal) {
	    $scope.uploader.url = '../project-import?reset=' + newVal;
	    for (var i=0; i<$scope.uploader.queue.length; i++) {
	      $scope.uploader.queue[i].url = $scope.uploader.url;
	    }
	  });

	  uploader.filters.push({
	    name: 'onlyZip',
	    fn: function(item) {
	      return item.name.lastIndexOf('.zip') === item.name.length - 4;
	    }
	  });
	});

var menuControllers = angular.module('menuControllers', []);

menuControllers.controller('MenuCtrl', ['$scope', '$http',
  function($scope, $http) {
    $http.get('menu').success(function(data) {
      $scope.menus = data;
    });
  }
]);

menuControllers.controller('UserCtrl', ['$scope', '$http',
  function($scope, $http) {
    $scope.name = "Unknown";
    $http.get('../op?user').success(function(data) {
      $scope.name = data;
    });
  }
]);


menuControllers.controller('HomeCtrl', ['$scope',
    function($scope) {
      $scope.homeData = [{
        image: 'edit',
        color: 'blue',
        path: '#/home/develop',
        title: 'Develop',
        description: "Development Toolkits",
        newTab: true
      }, {
        image: "search",
        color: 'green',
        path: "#/home/discover",
        title: "Discover",
        description: "Service Endpoints"
      }, {
        image: "wrench",
        color: 'orange',
        path: "#/home/operate",
        title: "Operate",
        description: "Lifecycle Management"
      }, {
        image: "area-chart",
        color: 'red',
        path: "#/monitoring",
        title: "Monitor",
        description: "Basic Metrics"
      }];
    }
  ]);





menuControllers.controller('DevelopCtrl', ['$scope',
    function($scope) {
      $scope.developData = [{
        image: 'laptop',
        color: 'blue',
        path: '../index.html',
        title: 'Web IDE',
        description: "Development Toolkit",
        newTab: true
      }, {
        image: "mobile",
        color: 'lblue',
        path: "#/workspace",
        title: "Light IDE",
        description: "Lightweight Development"
      }, {
          image: "desktop",
          color: 'lila',
          path: "http://download.eclipse.org/dirigible/drops/M20160119-1919/p2/rcp/",
          title: "Desktop IDE",
          description: "Eclipse Plugins"
        }];      
    }
  ]);


menuControllers.controller('DiscoverCtrl', ['$scope',
    function($scope) {
      $scope.discoverData = [{
        image: "search",
        color: 'green',
        path: "#/content",
        title: "Registry",
        description: "Browse Registry Content"
      }, {
        image: "globe",
        color: 'yellow',
        path: "#/web/content",
        title: "Web",
        description: "Browse Applications User Interfaces"
      }, {
        image: "book",
        color: 'yellow',
        path: "#/web/wiki",
        title: "Wiki",
        description: "Browse Applications Documentation"
      }, {
        image: "mobile-phone",
        color: 'blue',
        path: "#/mobile",
        title: "Mobile",
        description: "Native Mobile Applications"
      }, {
        image: "file-code-o",
        color: 'lblue',
        path: "#/scripting/javascript",
        title: "JavaScript",
        description: "JavaScript Services Endpoints"
      }, {
//        image: "coffee",
//        color: 'lblue',
//        path: "#/scripting/java",
//        title: "Java",
//        description: "Java Services Endpoints"
//      }, {
        image: "database",
        color: 'lblue',
        path: "#/scripting/sql",
        title: "SQL",
        description: "SQL Services Endpoints"
      }, {
        image: "gear",
        color: 'lblue',
        path: "#/scripting/command",
        title: "Command",
        description: "Command Services Endpoints"
      }, {
        image: "caret-square-o-right",
        color: 'orange',
        path: "#/integration/flow",
        title: "Flows",
        description: "Flow Integration Services"
      }, {
        image: "clock-o",
        color: 'orange',
        path: "#/integration/job",
        title: "Jobs",
        description: "Job Integration Services"
      }, {
    	image: "phone",
	    color: 'orange',
	    path: "#/integration/listener",
	    title: "Listeners",
	    description: "Listener Integration Services"
	  }, {
        image: "flash",
        color: 'purple',
        path: "#/scripting/tests",
        title: "Tests",
        description: "Test Cases Endpoints"
      }];
    }
  ]);


menuControllers.controller('OperateCtrl', ['$scope',
    function($scope) {
      $scope.operateData = [{
        image: "truck",
        color: 'blue',
        path: "#/content/import",
        title: "Transport",
        description: "Transport Registry Content"
      }, {
        image: "toggle-on",
        color: 'green',
        path: "#/content/clone",
        title: "Clone",
        description: "Clone Instance"
      }, {
        image: "sign-in",
        color: 'yellow',
        path: "#/content/project",
        title: "Import",
        description: "Import Project"
      }];
    }
  ]);



menuControllers.controller('MonitoringCtrl', ['$scope',
  function($scope) {
    $scope.monitoringData = [{
      image: "wrench",
      color: 'blue',
      path: "#/monitoring/manage",
      title: "Configure",
      description: "Configure locations"
    }, {
      image: "bar-chart",
      color: 'green',
      path: "#/monitoring/hits",
      title: "Hits",
      description: "Hit count statistics"
    }, {
      image: "hourglass-o",
      color: 'orange',
      path: "#/monitoring/response",
      title: "Response",
      description: "Response time statistics"
    }, {
        image: "line-chart",
        color: 'red',
        path: "#/monitoring/memory",
        title: "Memory",
        description: "Memory statistics"
    }, {
      image: "ticket",
      color: 'lila',
      path: "#/monitoring/acclog",
      title: "Access Log",
      description: "Access Log"
    }, {
      image: "film",
      color: 'lblue',
      path: "#/monitoring/logging",
      title: "Applications Log",
      description: "Applications Log"
    }];
  }
]);

menuControllers.controller('MonitoringManageCtrl', ['$scope', '$http',
  function($scope, $http) {
    var accessLogUrl = "../acclog";
    $scope.locations = null;
    $scope.newLocation;

    loadData();

    function loadData() {
      $http.get(accessLogUrl + "/locations").success(function(result) {
        $scope.locations = result;
      }).error(function(data) {
        alert('Could not fetch access log data!');
      });
    }

    $scope.remove = function(location) {
      $http.delete(accessLogUrl + location)
        .success(function(result) {
          loadData();
        }).error(function(data) {
          alert('Error while removing location!');
        });
    };

    $scope.addNewLocation = function() {
      $http.post(accessLogUrl + $scope.newLocation).success(function(result) {
        loadData();
      }).error(function(data) {
        alert('Unable to add location ' + '"' + $scope.newLocation + '"' +
          '\nLocation must be in "project/index.html" format!');
      });
    };
  }
]);
