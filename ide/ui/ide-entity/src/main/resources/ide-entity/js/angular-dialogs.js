angular.module('ui.entity-data.modeler', ['ngAnimate', 'ngSanitize', 'ui.bootstrap']);
angular.module('ui.entity-data.modeler').controller('ModelerCtrl', function ($uibModal, $log, $document, $scope) {
	var ctrl = this;
	ctrl.$scope = $scope;

	ctrl.animationsEnabled = true;

	ctrl.layoutTypes = [
		{"key":"MANAGE","label":"Manage Entities"},
		{"key":"LIST","label":"List Entities"},
		{"key":"MANAGE_MASTER","label":"Manage Master Entities"},
		{"key":"LIST_MASTER","label":"List Master Entities"},
		{"key":"MANAGE_DETAILS","label":"Manage Details Entities"},
		{"key":"LIST_DETAILS","label":"List Details Entities"}
	];
	
	ctrl.dataTypes = [
		{"key":"VARCHAR","label":"VARCHAR"},
		{"key":"CHAR","label":"CHAR"},
		{"key":"DATE","label":"DATE"},
		{"key":"TIME","label":"TIME"},
		{"key":"TIMESTAMP","label":"TIMESTAMP"},
		{"key":"INTEGER","label":"INTEGER"},
		{"key":"TINYINT","label":"TINYINT"},
		{"key":"BIGINT","label":"BIGINT"},
		{"key":"SMALLINT","label":"SMALLINT"},
		{"key":"REAL","label":"REAL"},
		{"key":"DOUBLE","label":"DOUBLE"},
		{"key":"BOOLEAN","label":"BOOLEAN"},
		{"key":"BLOB","label":"BLOB"},
		{"key":"DECIMAL","label":"DECIMAL"},
		{"key":"BIT","label":"BIT"}
	];
	
	ctrl.widgetTypes = [
		{"key":"TEXTBOX","label":"Text Box"},
		{"key":"TEXTAREA","label":"Text Area"},
		{"key":"DATE","label":"Date Picker"},
		{"key":"DROPDOWN","label":"Drop Down"},
		{"key":"LOOKUPDIALOG","label":"Lookup Dialog"},
		{"key":"NUMBER","label":"Number"},
		{"key":"COLOR","label":"Color"},
		{"key":"DATETIME-LOCAL","label":"Datetime Local"},
		{"key":"EMAIL","label":"e-mail"},
		{"key":"MONTH","label":"Month"},
		{"key":"RANGE","label":"Range"},
		{"key":"SEARCH","label":"Search"},
		{"key":"TEL","label":"Telephone"},
		{"key":"TIME","label":"Time"},
		{"key":"URL","label":"URL"},
		{"key":"WEEK","label":"Week"}
	];
	
	ctrl.relationshipTypes = [
		{"key":"ASSOCIATION","label":"Association"},
		{"key":"AGGREGATION","label":"Aggregation"},
		{"key":"COMPOSITION","label":"Composition"}
	];
	
	ctrl.relationshipCardinalities = [
		{"key":"1_1","label":"one-to-one"},
		{"key":"1_n","label":"one-to-many"}
	];
	
	ctrl.isPrimaryTypes = [
		{"key":"true","label":"Primary Entity"},
		{"key":"false","label":"Dependent Entity"}
	];
	
	ctrl.isMajorTypes = [
		{"key":"true","label":"Show in table header"},
		{"key":"false","label":"Show in form only"}
	];

	// Save Entity's properties
	ctrl.okEntityProperties = function() {
		var clone = $scope.$parent.cell.value.clone();
		$scope.$parent.graph.model.setValue($scope.$parent.cell, clone);
	};
	
	// Save Property's properties
	ctrl.okPropertyProperties = function() {
		var clone = $scope.$parent.cell.value.clone();
		$scope.$parent.graph.model.setValue($scope.$parent.cell, clone);
	};
	
	
	// Save Connector's properties
	ctrl.okConnectorProperties = function() {
		var clone = $scope.$parent.cell.source.value.clone();
		$scope.$parent.graph.model.setValue($scope.$parent.cell.source, clone);
		
		var connector = new Connector();
		connector.name = $scope.$parent.cell.source.value.relationshipName;
		$scope.$parent.graph.model.setValue($scope.$parent.cell, connector);
	};
  
  
});
