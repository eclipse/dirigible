/*globals angular, $ */
angular
.module('app', ['angularFileUpload'])
.controller('DocServiceCtrl', ['$scope', '$http', 'FileUploader', function($scope, $http, FileUploader) {
	var rootPath = '../../js-secured/profile';
	$scope.createDocPath = rootPath + '/avatar.js';
	
	function openErrorModal(titleText, bodyText){
		$("#errorModal .modal-header #title-text").text(titleText);
     	$("#errorModal .modal-body #body-text").text(bodyText);
     	$('#errorModal').modal('show');
	};
	
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
    	// refreshFolder();
//        console.info('onCompleteItem', fileItem, response, status, headers);
    };
    uploader.onCompleteAll = function() {
//        console.info('onCompleteAll');
    };

	
}]);
