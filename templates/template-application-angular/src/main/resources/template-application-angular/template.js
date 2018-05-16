exports.getTemplate = function() {
	return {
		'name': 'Full-stack Application (AngularJS)',
		'description': 'Full-stack Application with a Database Schema, a set of REST Services and an AngularJS User Interfaces',
		'model':'true',
		'sources': [{
			'location': '/template-application-angular/api/http.js.template', 
			'action': 'copy',
			'rename': 'api/http.js',
		}, {
			'location': '/template-application-angular/api/application.js.template', 
			'action': 'generate',
			'rename': 'api/{{fileName}}.js',
			'collection': 'models',
			'engine': 'velocity'
		}, {
			'location': '/template-application-angular/api/shell/menu.js.template', 
			'action': 'generate',
			'rename': 'api/shell/menu.js'
		}, {
			'location': '/template-application-angular/api/shell/perspectives.js.template', 
			'action': 'generate',
			'rename': 'api/shell/perspectives.js'
		}, {
			'location': '/template-application-angular/api/shell/views.js.template', 
			'action': 'generate',
			'rename': 'api/shell/views.js'
		}, {
			'location': '/template-application-angular/data/application.schema.template', 
			'action': 'generate',
			'rename': 'data/{{fileNameBase}}.schema'
		}, {
			'location': '/template-application-angular/data/dao/entity.js.template', 
			'action': 'generate',
			'rename': 'data/dao/{{fileName}}.js',
			'collection': 'models',
			'engine': 'velocity'
		}, {
			'location': '/template-application-angular/extensions/perspective/application.extension.template', 
			'action': 'generate',
			'rename': 'extensions/perspective/home.extension'
		}, {
			'location': '/template-application-angular/extensions/perspective/application.js.template', 
			'action': 'generate',
			'rename': 'extensions/perspective/home.js'
		}, {
			'location': '/template-application-angular/extensions/point/menu.extensionpoint.template', 
			'action': 'generate',
			'rename': 'extensions/point/menu.extensionpoint'
		}, {
			'location': '/template-application-angular/extensions/point/perspective.extensionpoint.template', 
			'action': 'generate',
			'rename': 'extensions/point/perspective.extensionpoint'
		}, {
			'location': '/template-application-angular/extensions/point/view.extensionpoint.template', 
			'action': 'generate',
			'rename': 'extensions/point/view.extensionpoint'
		}, {
			'location': '/template-application-angular/extensions/views/entity.extension.template', 
			'action': 'generate',
			'rename': 'extensions/views/{{fileName}}.extension',
			'collection': 'models'
		}, {
			'location': '/template-application-angular/views/manage/index.html.template', 
			'action': 'generate',
			'rename': 'views/{{fileName}}/index.html',
			'collection': 'uiManageModels',
			'engine': 'velocity'
		}, {
			'location': '/template-application-angular/views/manage/controller.js.template', 
			'action': 'generate',
			'rename': 'views/{{fileName}}/controller.js',
			'collection': 'uiManageModels',
			'engine': 'velocity'
		}, {
			'location': '/template-application-angular/views/manage/view.js.template', 
			'action': 'generate',
			'collection': 'models',
			'rename': 'views/{{fileName}}/view.js'
		}, {
			'location': '/template-application-angular/views/manage/menu/item.extension.template', 
			'action': 'generate',
			'rename': 'views/{{fileName}}/menu/item.extension',
			'collection': 'models'
		}, {
			'location': '/template-application-angular/views/manage/menu/item.js.template', 
			'action': 'generate',
			'rename': 'views/{{fileName}}/menu/item.js',
			'collection': 'models'
		}, {
			'location': '/template-application-angular/views/list.html.template', 
			'action': 'generate',
			'rename': 'views/{{fileName}}.html',
			'start' : '[[',
			'end' : ']]',
			'collection': 'uiListModels'
		}, {
			'location': '/template-application-angular/views/display.html.template', 
			'action': 'generate',
			'rename': 'views/{{fileName}}.html',
			'start' : '[[',
			'end' : ']]',
			'collection': 'uiDisplayModels'
		}, {
			'location': '/template-application-angular/resources/templates/menu.html.template', 
			'action': 'generate',
			'start' : '[[',
			'end' : ']]',
			'rename': 'resources/templates/menu.html'
		}, {
			'location': '/template-application-angular/resources/templates/sidebar.html.template', 
			'action': 'copy',
			'rename': 'resources/templates/sidebar.html'
		}, {
			'location': '/template-application-angular/resources/js/message-hub.js.template', 
			'action': 'copy',
			'rename': 'resources/js/message-hub.js'
		}, {
			'location': '/template-application-angular/resources/js/ui-bootstrap-tpls-0.14.3.min.js.template', 
			'action': 'copy',
			'rename': 'resources/js/ui-bootstrap-tpls-0.14.3.min.js'
		}, {
			'location': '/template-application-angular/resources/js/ui-core-ng-modules.js.template', 
			'action': 'generate',
			'rename': 'resources/js/ui-core-ng-modules.js'
		}, {
			'location': '/template-application-angular/resources/js/ui-layout.js.template', 
			'action': 'generate',
			'rename': 'resources/js/ui-layout.js'
		}, {
			'location': '/template-application-angular/index.html.template', 
			'action': 'generate',
			'rename': 'index.html'
		}],
		'parameters': [{
			'name': 'extensionName',
			'label': 'Extension Name'
		}, {
			'name': 'brand',
			'label': 'Brand'
		}]
	};
};