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