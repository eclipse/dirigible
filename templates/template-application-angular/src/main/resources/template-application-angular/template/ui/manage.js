exports.getSources = function(parameters) {
    return [{
        'location': '/template-application-angular/ui/perspectives/views/manage/index.html.template', 
        'action': 'generate',
        'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/index.html',
        'collection': 'uiManageModels',
        'engine': 'velocity'
    }, {
        'location': '/template-application-angular/ui/perspectives/views/manage/controller.js.template', 
        'action': 'generate',
        'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/controller.js',
        'collection': 'uiManageModels',
        'engine': 'velocity'
    }, {
        'location': '/template-application-angular/ui/perspectives/views/manage/extensions/view.js.template', 
        'action': 'generate',
        'collection': 'uiManageModels',
        'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/view.js'
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