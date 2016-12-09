/*globals controllers */
/*eslint-env browser */

controllers.controller('ProjectCtrl', function($scope, FileUploader) {
	$scope.pageHeader = 'Import Project Content';
	$scope.overrideContent = false;

	var uploader = $scope.uploader = new FileUploader({
		url: '../../project-import?reset=false'
	});

	$scope.$watch('overrideContent', function (newVal) {
		$scope.uploader.url = '../../project-import?reset=' + newVal;
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