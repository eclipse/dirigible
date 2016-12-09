/*globals controllers */
/*eslint-env browser */

controllers.controller('TransportCtrl', function($scope, FileUploader) {
	$scope.pageHeader = 'Import Registry Content';
  	$scope.exportTitle = 'Export Registry Content';
  	$scope.exportUrl = '../../export';
  	$scope.exportButtonText = 'Download Zipped Registry Content';
  	$scope.overrideContent = false;

  	$scope.uploader = new FileUploader({
  		url: '../../import?override=false'
  	});

	$scope.$watch('overrideContent', function (newVal) {
    	$scope.uploader.url = '../../import?override=' + newVal;
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
});