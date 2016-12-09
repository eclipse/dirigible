/*globals controllers */
/*eslint-env browser */

controllers.controller('CloneCtrl', function($scope, FileUploader) {
	$scope.pageHeader = 'Import Cloned Content';
  	$scope.exportTitle = 'Export Cloned Content';
  	$scope.exportUrl = '../../clone-export';
  	$scope.exportButtonText = 'Download Zipped Cloned Content';
  	$scope.overrideContent = false;

  	var uploader = $scope.uploader = new FileUploader({
    	url: '../../clone-import?reset=false'
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
});
