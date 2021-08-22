/*
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
angular.module('ui.schema.modeler', ['ngAnimate', 'ngSanitize', 'ui.bootstrap']);
angular.module('ui.schema.modeler').controller('ModelerCtrl', function ($uibModal, $log, $document, $scope) {
	var ctrl = this;
	ctrl.$scope = $scope;

	ctrl.animationsEnabled = true;

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
	
	// Save Entity's properties
	ctrl.okTableProperties = function() {
		var clone = $scope.$parent.cell.value.clone();
		$scope.$parent.graph.model.setValue($scope.$parent.cell, clone);
	};
	
	// Save Column's properties
	ctrl.okColumnProperties = function() {
		var clone = $scope.$parent.cell.value.clone();
		$scope.$parent.graph.model.setValue($scope.$parent.cell, clone);
	};
	
	// Save SQL Column's properties
	ctrl.okSQLColumnProperties = function() {
		var clone = $scope.$parent.cell.value.clone();
		$scope.$parent.graph.model.setValue($scope.$parent.cell, clone);
	};
	
	main(document.getElementById('graphContainer'),
			document.getElementById('outlineContainer'),
		 	document.getElementById('toolbarContainer'),
			document.getElementById('sidebarContainer'),
			document.getElementById('statusContainer'));

});
