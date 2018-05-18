exports.getTemplate = function() {
	return {
		'name': 'Full-stack Application (AngularJS)',
		'description': 'Full-stack Application with a Database Schema, a set of REST Services and an AngularJS User Interfaces',
		'model':'true',
		'sources': [
		{
			'_section': 'API',
			'location': '/template-application-angular/api/http.js.template', 
			'action': 'copy',
			'rename': 'api/http.js',
		}, {
			'_section': 'API',
			'location': '/template-application-angular/api/entity.js.template', 
			'action': 'generate',
			'rename': 'api/{{fileName}}.js',
			'collection': 'models',
			'engine': 'velocity'
		},
		
		
		
		{
			'_section': 'Data',
			'location': '/template-application-angular/data/application.schema.template', 
			'action': 'generate',
			'rename': 'data/{{fileNameBase}}.schema'
		}, {
			'_section': 'Data',
			'location': '/template-application-angular/data/dao/entity.js.template', 
			'action': 'generate',
			'rename': 'data/dao/{{fileName}}.js',
			'collection': 'models',
			'engine': 'velocity'
		},
		
		
		
		{
			'_section': 'Extensions',
			'location': '/template-application-angular/extensions/perspective/perspective.extension.template', 
			'action': 'generate',
			'rename': 'extensions/perspective/perspective.extension'
		}, {
			'_section': 'Extensions',
			'location': '/template-application-angular/extensions/perspective/perspective.js.template', 
			'action': 'generate',
			'rename': 'extensions/perspective/perspective.js'
		}, {
			'_section': 'Extensions',
			'location': '/template-application-angular/extensions/tiles/tiles.extension.template', 
			'action': 'generate',
			'rename': 'extensions/tiles/tiles.extension'
		}, {
			'_section': 'Extensions',
			'location': '/template-application-angular/extensions/tiles/tiles.js.template', 
			'action': 'generate',
			'rename': 'extensions/tiles/tiles.js'
		}, {
			'_section': 'Extensions',
			'location': '/template-application-angular/extensions/menu.extensionpoint.template', 
			'action': 'generate',
			'rename': 'extensions/menu.extensionpoint'
		}, {
			'_section': 'Extensions',
			'location': '/template-application-angular/extensions/views/view.extensionpoint.template', 
			'action': 'generate',
			'rename': 'extensions/views/view.extensionpoint'
		}, {
			'_section': 'Extensions',
			'location': '/template-application-angular/extensions/views/entity.extension.template', 
			'action': 'generate',
			'rename': 'extensions/views/{{fileName}}.extension',
			'collection': 'models'
		},
		
		
		
		{
			'_section': 'UI - Manage Models',
			'location': '/template-application-angular/views/manage/index.html.template', 
			'action': 'generate',
			'rename': 'views/{{fileName}}/index.html',
			'collection': 'uiManageModels',
			'engine': 'velocity'
		}, {
			'_section': 'UI - Manage Models',
			'location': '/template-application-angular/views/manage/controller.js.template', 
			'action': 'generate',
			'rename': 'views/{{fileName}}/controller.js',
			'collection': 'uiManageModels',
			'engine': 'velocity'
		}, {
			'_section': 'UI - Manage Models',
			'location': '/template-application-angular/views/manage/view.js.template', 
			'action': 'generate',
			'collection': 'uiManageModels',
			'rename': 'views/{{fileName}}/view.js'
		}, {
			'_section': 'UI - Manage Models',
			'location': '/template-application-angular/views/manage/menu/item.extension.template', 
			'action': 'generate',
			'rename': 'views/{{fileName}}/menu/item.extension',
			'collection': 'uiManageModels'
		}, {
			'_section': 'UI - Manage Models',
			'location': '/template-application-angular/views/manage/menu/item.js.template', 
			'action': 'generate',
			'rename': 'views/{{fileName}}/menu/item.js',
			'collection': 'uiManageModels'
		},
		
		
		
		{
			'_section': 'UI - Index.html',
			'location': '/template-application-angular/index.html.template', 
			'action': 'generate',
			'rename': 'index.html'
		}],
		'parameters': [{
			'name': 'extensionName',
			'label': 'Extension Name'
		}, {
			'name': 'launchpadName',
			'label': 'Launchpad Name'
		}, {
			'name': 'brand',
			'label': 'Brand'
		}]
	};
};