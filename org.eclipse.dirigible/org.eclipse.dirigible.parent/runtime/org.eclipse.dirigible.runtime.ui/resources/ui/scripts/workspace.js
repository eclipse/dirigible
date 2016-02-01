angular.module('workspaceServices', ['ngResource']).factory('FilesSearch', ['$resource', function($resource) {
	return $resource('../searchw');
}]);

var workspaceControllers = angular.module('workspaceControllers', []);
workspaceControllers.controller('WorkspaceListCtrl', ['$scope', '$sce', 'FilesSearch', '$http', function($scope, $sce, FilesSearch, $http) {

    var backupRoot;
    var timeOutDelay;

    $scope.caseSensitive = false;
    $scope.mainError = undefined;
    $scope.searchError = undefined;
    $scope.search = undefined;

    $scope.mapping = {
    		"application/javascript": ["js"],
			"application/json": ["json", "odata", "ws", "table", "view", "entity", "menu", "access", "extensionpoint", "extension", "command", "flow", "job"],
			"application/xml": ["xml", "xsd", "wsdl", "xsl", "xslt", "routes"],
			"text/html": ["html"],
			"text/x-java-source": ["java"],
			"text/css": ["css"],
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
    		$http.get(newData.path, {
    			transformResponse: [function (data) {
    				return data;
    			}]
    		}).success(function(response) {
    			setText(response, $scope.getModeModule(newData.path));
        		$scope.path = newData.path;
    		}).error(function(response) {
    			$scope.mainError = "Error loading " + newData.path;
    		});
    	} else if (newData.files) {
    		$scope.selected = newData;
    		$scope.paths.push(newData);
    		$scope.editor = null;
    	}
    };

    $scope.getModeModule = function(resourcePath) {
    	var m = resourcePath.match(/(.*)[\/\\]([^\/\\]+)\.(\w+)$/);
		var extension = m && m.length>3 && m[3] ? m[3] : "txt";

		var modules = Object.keys($scope.mapping);
		for (var i in modules) {
			if ($scope.mapping[modules[i]].indexOf(extension) > -1) {
				return modules[i];
			}
		} 
		return "text";
    };

    $scope.copyFile = function(file) {
      window.prompt("Copy to clipboard: Ctrl+C, Enter", file.path);
    };

    $scope.crumbsChanged = function(path) {
    	var inx = this.paths.indexOf(path);
    	$scope.paths.splice(inx + 1);
    	$scope.selected = this.paths[inx];
    	$scope.editor = null;
    };

    $scope.securedUrl = function(src) {
    	return $sce.trustAsResourceUrl(src);
    };

    $scope.$watch('search', function(newVal, oldVal) {
    	if (oldVal && newVal) {
    		clearTimeout(timeOutDelay);
    		timeOutDelay = setTimeout(function() {
    			FilesSearch.query({
    				q: newVal
    			}, onArrayQuery, function(er) {
    				$scope.searchError = er;
    			});
    		}, 300);
    	} else if (!newVal) {
    		$scope.searchError = undefined;
    		$scope.selected = backupRoot;
    	}
	});

    $scope.saveCalled = function() {
    	$http.put($scope.path, getText()).success(function(response) {
    		onSuccess($scope.path + " saved successfully");
    	}).error(function(response) {
			onError("Error saving " + $scope.path + "\n" + response);
    	});
    };

    $scope.publishCalled = function() {
    	$http.put($scope.path, getText()).success(function(response) {
    		onSuccess($scope.path + " saved successfully");

    		var publishPath = $scope.path;
    		publishPath = publishPath.replace("/workspace", "");
    		publishPath = "/publish" + publishPath;

    		$http.post(publishPath).success(function(response) {
        		onSuccess(publishPath + " published successfully");
    		}).error(function(response) {
    			onError("Error publishing " + publishPath + "\n" + response);
    		});
    	}).error(function(response) {
    		onError("Error saving " + $scope.path + "\n" + response);
    	});
    };

    function onArrayQuery(data) {
    	$scope.searchError = undefined;
    	$scope.paths = undefined;
    	$scope.selected = {
    			files: data
    	};
    }

    function onSuccess(message) {
    	console.log(message);
    	$scope.successfullMessage = message;
    	$scope.mainError = null;
    	$("#successMessageAlert").fadeIn();
    	setTimeout(function() {
    		$("#successMessageAlert").fadeOut();
    	}, 1500);
    	dirtyChanged(false);
    }

    function onError(error) {
    	console.error(error);
    	$scope.successfullMessage = null;
    	$scope.mainError = error;
    }

    function createEditor(content, contentType) {
    	require.config({waitSeconds: 0});
        require(["../orion/code_edit/built-codeEdit", "orion/keyBinding"], function(widget, mKeyBinding) {
            var codeEdit = new widget();
            $scope.editor = {};
            codeEdit.create({
            	parent: "editor",
            	contentType: contentType,
            	contents: content
            }).then(function(editorViewer) {
            	$scope.editor = editorViewer.editor;
            	var savedText = content;
            	var isDirty = false;
            	$scope.editor.getTextView().setKeyBinding(new mKeyBinding.KeyBinding("s", true), "save");
            	$scope.editor.getTextView().setKeyBinding(new mKeyBinding.KeyBinding("p", true), "toggleZoomRuler");

            	editorViewer.editor.getTextView().setAction("save", function(){ //$NON-NLS-0$
            		isDirty = false;
            		$scope.saveCalled();
            		return true;
            	});

            	editorViewer.editor.getTextView().setAction("toggleZoomRuler", function(){ //$NON-NLS-0$
            		isDirty = false;
            		$scope.publishCalled();
            		return true;
            	});

            	$scope.editor.addEventListener("DirtyChanged", function(event) {
            		var newText = $scope.editor.getText();
            		if (savedText !== newText && !isDirty) {
            			isDirty = true;
            			dirtyChanged(true);
            		} else if (savedText === newText && isDirty) {
            			isDirty = false;
            			dirtyChanged(false);
            		}
            	});

    	        // explicitly set the read only mode for empty files
    	        $scope.editor.getTextView()._readonly = false;
            });
        });
    }

    function getText() {
        return $scope.editor.getText();
    }

    function setText(text, mode) {
    	createEditor(text, mode);
    }

    function dirtyChanged(value) {
    	$scope.isDirty = value;
    }
    
    $scope.isFullscreen = false;
    
    $scope.toggleFullscreen = function() {
      $scope.isFullscreen=!$scope.isFullscreen;
    }
}]);

workspaceControllers.controller('WorkspaceCtrl', function($scope, $resource) {
  $scope.objectContent = true;
  $scope.restService = $resource('../workspace');
});
