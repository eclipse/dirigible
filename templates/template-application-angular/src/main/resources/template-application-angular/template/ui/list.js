exports.getSources = function(parameters) {
	return [{
		'location': '/template-application-angular/ui/perspectives/views/list/index.html.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/index.html',
		'engine': 'velocity',
		'collection': 'uiListModels'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/list/controller.js.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/controller.js',
		'engine': 'velocity',
		'collection': 'uiListModels'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/list/extensions/view.js.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/view.js',
		'collection': 'uiListModels'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/list/extensions/view.extension.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/view.extension',
		'collection': 'uiListModels'
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