/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
(function(angular){
"use strict";
	angular.module('discussion-boards')
	.directive('ckEditor', ['$ckeditor', function($ckeditor) {
	  return {
	    require: '?ngModel',
	    link: function(scope, elm, attr, ngModel) {
	    
	      var ck = $ckeditor.replace(elm[0], {
		    	toolbar: [
					{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ], items: [ 'Format', 'Bold', 'Italic', 'Strike', '-', 'RemoveFormat' ] },
					{ name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi' ], items: [ 'NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'Blockquote' ] },					
					{ name: 'clipboard', groups: [ 'clipboard', 'undo' ], items: [ 'Undo', 'Redo' ] },
					{ name: 'editing', groups: [ 'find', 'selection', 'spellchecker' ], items: [ 'Scayt' ] },
					{ name: 'links', items: [ 'Link', 'Unlink' ] },
					{ name: 'insert', items: [ 'Image', 'Table', 'HorizontalRule', 'SpecialChar' ] },
					{ name: 'styles', items: [ 'Styles' ] },					
					{ name: 'document', groups: [ 'mode', 'document', 'doctools' ], items: [ 'Source' ] },					
				],
				toolbarGroups: [
					{ name: 'clipboard', groups: [ 'clipboard', 'undo' ] },
					{ name: 'editing', groups: [ 'find', 'selection', 'spellchecker' ] },
					{ name: 'links' },
					{ name: 'insert' },
					{ name: 'forms' },
					{ name: 'tools' },
					{ name: 'document', groups: [ 'mode', 'document', 'doctools' ] },
					{ name: 'others' },
					'/',
					{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
					{ name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi' ] },
					{ name: 'styles' },
					{ name: 'colors' }
				]				
	    	});
	
	      if (!ngModel) return;
	
	      ck.on('pasteState', function() {
	        scope.$apply(function() {
	          ngModel.$setViewValue(ck.getData());
	        });
	      });
	
	      ngModel.$render = function(value) {
	        ck.setData(ngModel.$viewValue);
	      };
	    }
	  };
	}]);	
})(angular);