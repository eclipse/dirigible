exports.getSources = function(parameters) {
	return [{
		'location': '/template-application-angular/ui/perspectives/views/list/index.html.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/index.html',
		'collection': 'uiListModels',
		'engine': 'velocity'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/list/controller.js.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/controller.js',
		'collection': 'uiListModels',
		'engine': 'velocity'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/list/extensions/view.js.template', 
		'action': 'generate',
		'collection': 'uiListModels',
		'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/view.js'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/list/extensions/menu/item.extension.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/menu/item.extension',
		'collection': 'uiListModels'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/list/extensions/menu/item.js.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/menu/item.js',
		'collection': 'uiListModels'
	}];
};