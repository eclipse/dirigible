/*globals angular, $ */
angular
.module('import', ['angularFileUpload'])
.factory('$messageHub', [function(){
	var messageHub = new FramesMessageHub();	
	var message = function(evtName, data){
		messageHub.post({data: data}, 'properties.' + evtName);
	};
	var on = function(topic, callback){
		messageHub.subscribe(callback, topic);
	};
	return {
		message: message,
		on: on
	};
}])
.controller('ImportController', ['$scope', '$http', 'FileUploader', '$messageHub', function($scope, $http, FileUploader, $messageHub) {
	
	$scope.TRANSPORT_PROJECT_URL = "/services/v3/transport/project";
	$scope.WORKSPACES_URL = "/services/v3/ide/workspaces";
	
	var url = $scope.WORKSPACES_URL;
	$http.get(url)
			.then(function(response){
				var workspaceNames = response.data;
				$scope.workspaces = workspaceNames;
				if($scope.workspaces[0]) {
					$scope.selectedWs = $scope.workspaces[0];
				} 
			});

	// FILE UPLOADER
	
    var uploader = $scope.uploader = new FileUploader({
        url: $scope.TRANSPORT_PROJECT_URL
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
		item.url =  $scope.TRANSPORT_PROJECT_URL + "/" + $scope.selectedWs;
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
        alert(response.err.message);
    };
    uploader.onCancelItem = function(fileItem, response, status, headers) {
//        console.info('onCancelItem', fileItem, response, status, headers);
    };
    uploader.onCompleteItem = function(fileItem, response, status, headers) {
    	//refreshFolder();
//        console.info('onCompleteItem', fileItem, response, status, headers);
    };
    uploader.onCompleteAll = function() {
//        console.info('onCompleteAll');
    };

		
	$messageHub.on('workbench.theme.changed', function(msg){
		var themeUrl = msg.data;
	
		$('a[href="/services/v3/core/theme/ide.css"]').remove();
		$('<link href="/services/v3/core/theme/ide.css" rel="stylesheet" />').appendTo('head');
		
		$('#theme-stylesheet').remove();
		$('<link id="theme-stylesheet" href="'+themeUrl +'" rel="stylesheet" />').appendTo('head');
	}.bind(this));
	
}]);