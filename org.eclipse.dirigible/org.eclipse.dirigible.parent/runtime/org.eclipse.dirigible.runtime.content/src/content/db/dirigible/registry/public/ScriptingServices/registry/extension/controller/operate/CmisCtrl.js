/*globals registryApp, $ controllers*/

controllers.controller('CmisCtrl', ['$scope', '$http', 'FileUploader', function($scope, $http, FileUploader) {
	var folderUrl = '../../js-secured/ext_registry_cmis_explorer/folder.js';
	$scope.paths = [];
	$scope.docsUrl = '../../js-secured/ext_registry_cmis_explorer/document.js';
	
	function getFolder(folderId){
		var requestUrl = folderUrl;
		if(folderId != null){
			requestUrl += '?id=' + folderId;
		}
		
		return $http.get(requestUrl);
	};
	
	function addFolderToPaths(folder){
		var nextIndex = $scope.paths.length;
		$scope.paths.push({index: nextIndex, name: folder.name, id: folder.id});
	}
	
	function refreshFolder(){
		getFolder($scope.folder.id)
		.success(function(data){
			$scope.folder = data;
		});
	}
	
	function setUploaderFolder(folderId){
		$scope.uploader.url = $scope.docsUrl + '?id=' + folderId;
	}
	
	function setCurrentFolder(folderData){
		$scope.folder = folderData;
		setUploaderFolder($scope.folder.id);
	};
	
	function openErrorModal(titleText, bodyText){
		$("#errorModal .modal-header #title-text").text(titleText);
     	$("#errorModal .modal-body #body-text").text(bodyText);
     	$('#errorModal').modal('show');
	};
	
	getFolder()
	.success(function(data){
		setCurrentFolder(data);
		addFolderToPaths(data);
	});
	
	$scope.handleExplorerClick = function(cmisObject){
		if (cmisObject.type === "cmis:folder" && !$scope.inDeleteSession){
			getFolder(cmisObject.id)
			.success(function(data){
				setCurrentFolder(data);
				addFolderToPaths(data);
			});
		}
	};
	
	$scope.crumbsChanged = function(path){
		getFolder(path.id)
		.success(function(data){
			setCurrentFolder(data);
			$scope.paths.splice(path.index + 1);
		});
	};
	
	$scope.createFolder = function(newFolderName){
		var postData = { parentFolderId: $scope.folder.id, name: newFolderName };
		$http.post(folderUrl, postData)
		.success(function(){
			$scope.newFolderName = undefined;
			$('#newFolderModal').modal('hide');
			refreshFolder();
		})
		.error(function(data){
			$('#newFolderModal').modal('hide');
			openErrorModal("Failed to create folder", data.err.message);
		});
	};
	
	$scope.enterDeleteSession = function(){
		$scope.inDeleteSession = true; 
	};
	
	$scope.exitDeleteSession = function(){
		$scope.inDeleteSession = false;
	};
	
	$scope.deleteItems = function(){
		var idsToDelete = []
		for (var i in $scope.itemsToDelete)
			idsToDelete.push($scope.itemsToDelete[i].id);
		
		$http({ url: $scope.docsUrl, 
                method: 'DELETE', 
                data: idsToDelete, 
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
		$('#confirmDeleteModal').modal('show');
	};
	
	$scope.handleDeleteButton = function(){
		var itemsToDelete = [];
		for (var i in $scope.folder.children)
			if ($scope.folder.children[i].selected === true)
				itemsToDelete.push({name: $scope.folder.children[i].name, id: $scope.folder.children[i].id});
				
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
	
	$scope.renameItem = function(itemId, newName){
		$http({ url: $scope.docsUrl, 
                method: 'PUT', 
                data: {id: itemId, name: newName }, 
                headers: {"Content-Type": "application/json;charset=utf-8"}
        }).success(function() {
        	$('#renameModal').modal('hide');
            refreshFolder();
        }).error(function(error) {
        	$('#renameModal').modal('hide');
        	var title = "Failed to rename item" + $scope.itemToDelete.name;
            openErrorModal(title, error.err.message);
        });
	}
	
	$scope.hoverIn = function(){
        this.hoverEdit = true;
    };

    $scope.hoverOut = function(){
        this.hoverEdit = false;
    };
	
	// FILE UPLOADER
	
    var uploader = $scope.uploader = new FileUploader({
        url: '../../js-secured/ext_registry_cmis_explorer/document'
    });

    // UPLOADER FILTERS

    uploader.filters.push({
        name: 'customFilter',
        fn: function(item /*{File|FileLikeObject}*/, options) {
            return this.queue.length < 10;
        }
    });

    // UPLOADER CALLBACKS

    uploader.onWhenAddingFileFailed = function(item /*{File|FileLikeObject}*/, filter, options) {
//        console.info('onWhenAddingFileFailed', item, filter, options);
    };
    uploader.onAfterAddingFile = function(fileItem) {
//        console.info('onAfterAddingFile', fileItem);
    };
    uploader.onAfterAddingAll = function(addedFileItems) {
//        console.info('onAfterAddingAll', addedFileItems);
    };
    uploader.onBeforeUploadItem = function(item) {
//        console.info('onBeforeUploadItem', item);
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

//    console.info('uploader', uploader);
}]);
