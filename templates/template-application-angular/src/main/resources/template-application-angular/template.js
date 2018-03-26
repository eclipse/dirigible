exports.getTemplate = function() {
	return {
		'name': 'Full-stack Application (AngularJS)',
		'description': 'Full-stack Application with a Database Schema, a set of REST Services and an AngularJS User Interfaces',
		'model':'true',
		'sources': [{
			'location': '/template-application-angular/api/application.js.template', 
			'action': 'generate',
			'rename': 'api/{{fileName}}.js',
			'collection': 'dataModels'
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
			'rename': 'data/{{fileName}}.schema'
		}, {
			'location': '/template-application-angular/extensions/menu/application.extension.template', 
			'action': 'generate',
			'rename': 'extensions/menu/{{fileName}}.extension',
			'collection': 'dataModels'
		}, {
			'location': '/template-application-angular/extensions/menu/application.js.template', 
			'action': 'generate',
			'rename': 'extensions/menu/{{fileName}}.js',
			'collection': 'dataModels'
		}, {
			'location': '/template-application-angular/extensions/perspective/application.extension.template', 
			'action': 'generate',
			'rename': 'extensions/perspective/{{fileName}}.extension',
			'collection': 'dataModels'
		}, {
			'location': '/template-application-angular/extensions/perspective/application.js.template', 
			'action': 'generate',
			'rename': 'extensions/perspective/{{fileName}}.js',
			'collection': 'dataModels'
		}, {
			'location': '/template-application-angular/extensions/point/menu.extensionpoint.template', 
			'action': 'generate',
			'rename': 'extensions/point/{{fileName}}.extensionpoint',
			'collection': 'dataModels'
		}, {
			'location': '/template-application-angular/extensions/point/perspective.extensionpoint.template', 
			'action': 'generate',
			'rename': 'extensions/point/{{fileName}}.extensionpoint',
			'collection': 'dataModels'
		}, {
			'location': '/template-application-angular/extensions/point/view.extensionpoint.template', 
			'action': 'generate',
			'rename': 'extensions/point/{{fileName}}.extensionpoint',
			'collection': 'dataModels'
		}, {
			'location': '/template-application-angular/extensions/view/application.extension.template', 
			'action': 'generate',
			'rename': 'extensions/view/{{fileName}}.extension',
			'collection': 'dataModels'
		}, {
			'location': '/template-application-angular/extensions/view/application.js.template', 
			'action': 'generate',
			'rename': 'extensions/view/{{fileName}}.js',
			'collection': 'dataModels'
		}, {
			'location': '/template-application-angular/ui/index.html.template', 
			'action': 'generate',
			'rename': 'ui/index.html',
			'start' : '[[',
			'end' : ']]',
			'type': 'ui-index'
		}, {
			'location': '/template-application-angular/ui/manage/index.html.template', 
			'action': 'generate',
			'rename': 'ui/{{fileName}}/index.html',
			'start' : '[[',
			'end' : ']]',
			'collection': 'uiManageModels'
		}, {
			'location': '/template-application-angular/ui/manage/manage.html.template', 
			'action': 'generate',
			'rename': 'ui/{{fileName}}/manage.html',
			'start' : '[[',
			'end' : ']]',
			'collection': 'uiManageModels'
		}, {
			'location': '/template-application-angular/ui/list.html.template', 
			'action': 'generate',
			'rename': 'ui/{{fileName}}.html',
			'start' : '[[',
			'end' : ']]',
			'collection': 'uiListModels'
		}, {
			'location': '/template-application-angular/ui/display.html.template', 
			'action': 'generate',
			'rename': 'ui/{{fileName}}.html',
			'start' : '[[',
			'end' : ']]',
			'collection': 'uiDisplayModels'
		}],
		'parameters': [{
			'name': 'extensionName',
			'label': 'Extension Name'
		}]
	};
};