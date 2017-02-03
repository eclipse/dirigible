/*globals angular, $ */
angular
.module('app', ['angularFileUpload'])
.controller('DocServiceCtrl', ['$scope', '$http', 'FileUploader', function($scope, $http, FileUploader) {
	var rootPath = '../../../js-secured/docs_explorer';
	var managePath = rootPath + '/manage';
	var createPath = managePath + '/create';
	$scope.createDocPath = createPath + '/document';
	var zipUploadPath = createPath + '/zip';
	var createFolderPath = createPath + '/folder';
	var updatePath = managePath + '/update';
	var renamePath = updatePath + '/rename';
	$scope.removePath = managePath + '/remove';
	
	var readPath = rootPath + '/read';
	var readDocPath = readPath + '/document';
	$scope.downloadPath = readDocPath + '/download';
	$scope.previewPath = readDocPath + '/preview';
	var readFolderPath = readPath + '/folder';
	var listFolderPath = readFolderPath + '/list';
	$scope.downloadZipPath = readFolderPath + '/zip';

	$scope.breadcrumbs = new Breadcrumbs();
	
	function getFolder(folderPath){
		var requestUrl = listFolderPath;
		if(folderPath){
			requestUrl += '?path=' + folderPath;
		}
		
		return $http.get(requestUrl);
	};
	
	function refreshFolder(){
		getFolder($scope.folder.path)
		.success(function(data){
			$scope.folder = data;
		});
	}
	
	function setUploaderFolder(folderPath){
		$scope.uploader.url = $scope.createDocPath + '?path=' + folderPath;
	}
	
	function setCurrentFolder(folderData){
		$scope.folder = folderData;
		$scope.breadcrumbs.parse(folderData.path);
		setUploaderFolder($scope.folder.path);
	};
	
	function openErrorModal(titleText, bodyText){
		$("#errorModal .modal-header #title-text").text(titleText);
     	$("#errorModal .modal-body #body-text").text(bodyText);
     	$('#errorModal').modal('show');
	};
	
	$scope.getFullPath = function(itemName){
		return $scope.folder.path + '/' + itemName;
	}
	
	getFolder()
	.success(function(data){
		setCurrentFolder(data);
	});
	
	$scope.handleExplorerClick = function(cmisObject){
		if (cmisObject.type === "cmis:folder" && !$scope.inDeleteSession){
			getFolder($scope.getFullPath(cmisObject.name))
			.success(function(data){
				setCurrentFolder(data);
			});
		}
	};
	
	$scope.crumbsChanged = function(entry){
		getFolder(entry.path)
		.success(function(data){
			setCurrentFolder(data);
		});
	};
	
	$scope.createFolder = function(newFolderName){
		var postData = { parentFolder: $scope.folder.path, name: newFolderName };
		$http.post(createFolderPath, postData)
		.success(function(){
			$('#newFolderModal').modal('toggle');
			refreshFolder();
		})
		.error(function(data){
			$('#newFolderModal').modal('toggle');
			openErrorModal("Failed to create folder", data.err.message);
		});
		$scope.newFolderName = undefined;

	};
	
	$scope.enterDeleteSession = function(){
		$scope.inDeleteSession = true; 
	};
	
	$scope.exitDeleteSession = function(){
		$scope.inDeleteSession = false;
	};
	
	$scope.deleteItems = function(forceDelete){
		var pathsToDelete = $scope.itemsToDelete.map(function(item){return $scope.getFullPath(item.name);});
		var url = $scope.removePath + (forceDelete ? "?force=true" : "");
		$http({ url: url, 
                method: 'DELETE', 
                data: pathsToDelete, 
                headers: {"Content-Type": "application/json;charset=utf-8"}
        }).success(function() {
            $scope.inDeleteSession = false;
            refreshFolder();
        }).error(function(error) {
        	$scope.inDeleteSession = false;
            openErrorModal("Failed to delete items", error.err.message);
        });
	
		$scope.inDeleteSession = false;
	};
	
	$scope.handleSingleDelete = function($event, item){
		$event.stopPropagation();
		$scope.itemsToDelete = [item];
		$scope.forceDelete = false;
		$('#confirmDeleteModal').modal('show');
	};
	
	$scope.handleDeleteButton = function(){
		var itemsToDelete = $scope.folder.children
			.filter(function(child){ return child.selected === true})
			.map(function(child){ return { name: child.name, path: child.path } });
				
		if (itemsToDelete.length > 0){
			$scope.itemsToDelete = itemsToDelete;
			$('#confirmDeleteModal').modal('show');		
		} else {
			$scope.inDeleteSession = false;
		}
	};
	
	$scope.handleRenameButton = function($event, item){
		$event.stopPropagation();
		$scope.itemToRename = item;
		$('#renameModal').modal('show');
	};
	
	$scope.renameItem = function(itemName, newName){
		$http({ url: renamePath, 
                method: 'PUT', 
                data: { path: $scope.getFullPath(itemName), name: newName },
                headers: {"Content-Type": "application/json;charset=utf-8"}
        }).success(function() {
        	$('#renameModal').modal('toggle');
            refreshFolder();
        }).error(function(error) {
        	$('#renameModal').modal('toggle');
        	var title = "Failed to rename item" + $scope.itemToRename.name;
            openErrorModal(title, error.err.message);
        });
	}
	
	// FILE UPLOADER
	
    var uploader = $scope.uploader = new FileUploader({
        url: $scope.createDocPath
    });

    // UPLOADER FILTERS

    uploader.filters.push({
        name: 'customFilter',
        fn: function(item /*{File|FileLikeObject}*/, options) {
            return this.queue.length < 100;
        }
    });

    // UPLOADER CALLBACKS

    uploader.onWhenAddingFileFailed = function(item /*{File|FileLikeObject}*/, filter, options) {
//        console.info('onWhenAddingFileFailed', item, filter, options);
    };
    uploader.onAfterAddingFile = function(fileItem) {
    	
    };
    uploader.onAfterAddingAll = function(addedFileItems) {
//        console.info('onAfterAddingAll', addedFileItems);
    };
    uploader.onBeforeUploadItem = function(item) {
//        console.info('onBeforeUploadItem', item);
		if ($scope.unpackZips && item.file.name.endsWith(".zip")) {
			item.url = zipUploadPath + "?path=" + $scope.folder.path;
		}
    };
    uploader.onProgressItem = function(fileItem, progress) {
//        console.info('onProgressItem', fileItem, progress);
    };
    uploader.onProgressAll = function(progress) {
//        console.info('onProgressAll', progress);
    };
    uploader.onSuccessItem = function(fileItem, response, status, headers) {
//        console.info('onSuccessItem', fileItem, response, status, headers);
    };
    uploader.onErrorItem = function(fileItem, response, status, headers) {
//        console.info('onErrorItem', fileItem, response, status, headers);
        var title = "Failed to uplaod item";
        openErrorModal(title, response.err.message);
    };
    uploader.onCancelItem = function(fileItem, response, status, headers) {
//        console.info('onCancelItem', fileItem, response, status, headers);
    };
    uploader.onCompleteItem = function(fileItem, response, status, headers) {
    	refreshFolder();
//        console.info('onCompleteItem', fileItem, response, status, headers);
    };
    uploader.onCompleteAll = function() {
//        console.info('onCompleteAll');
    };

	function Breadcrumbs() {
		this.crumbs = [];
	};

	Breadcrumbs.prototype.parse = function(path){
		var folders = path.split("/");
		var crumbs = [];
		for (var i in folders){
			var index = +i + 1;
			var crumbPath = folders.slice(0, index).join("/");
			var crumb = { name: folders[i], path: crumbPath };
			crumbs.push(crumb);
		}
		crumbs[0].name = 'root';
		crumbs[0].path = '/';
		
		this.crumbs = crumbs;
	};
}]);
