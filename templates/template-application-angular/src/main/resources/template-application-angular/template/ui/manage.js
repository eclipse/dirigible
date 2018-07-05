exports.getSources = function(parameters) {
    return [{
        'location': '/template-application-angular/ui/perspectives/views/manage/index.html.template', 
        'action': 'generate',
        'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/index.html',
        'engine': 'velocity',
        'collection': 'uiManageModels'
    }, {
        'location': '/template-application-angular/ui/perspectives/views/manage/controller.js.template', 
        'action': 'generate',
        'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/controller.js',
        'engine': 'velocity',
        'collection': 'uiManageModels'
    }, {
        'location': '/template-application-angular/ui/perspectives/views/manage/extensions/view.js.template', 
        'action': 'generate',
        'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/view.js',
        'collection': 'uiManageModels'
    }, {
		'location': '/template-application-angular/ui/perspectives/views/manage/extensions/view.extension.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/view.extension',
		'collection': 'uiManageModels'
	}, {
        'location': '/template-application-angular/ui/perspectives/views/manage/extensions/menu/item.extension.template', 
        'action': 'generate',
        'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/menu/item.extension',
        'collection': 'uiManageModels'
    }, {
        'location': '/template-application-angular/ui/perspectives/views/manage/extensions/menu/item.js.template', 
        'action': 'generate',
        'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/menu/item.js',
        'collection': 'uiManageModels'
    }];
};