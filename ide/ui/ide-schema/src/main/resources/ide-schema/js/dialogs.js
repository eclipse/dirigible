
function showAlert(title, message, $scope) {
	$scope.$parent.alertTitle = title;
	$scope.$parent.alertStatus = 'warning';
	$scope.$parent.alertMessage = message;
	$scope.$apply();
	$('#alertOpen').click();
}

function showInfo(title, message, $scope) {
	$scope.$parent.infoTitle = title;
	$scope.$parent.infoMessage = message;
	$scope.$apply();
	$('#infoOpen').click();
}
		