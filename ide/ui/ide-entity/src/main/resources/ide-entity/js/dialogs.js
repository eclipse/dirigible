
function showAlert(title, message, $scope) {
	$scope.$parent.alertTitle = 'Drop';
	$scope.$parent.alertStatus = 'warning';
	$scope.$parent.alertMessage = 'Target Entity must have a Primary Key';
	$scope.$apply();
	$('#alertOpen').click();
}
