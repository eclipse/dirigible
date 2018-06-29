exports.getSources = function(parameters) {
    var sources = [];
    sources = sources.concat(getMaster(parameters));
    sources = sources.concat(getDetails(parameters));
    return sources;
};

function getMaster(parameters) {
    return [{
        'location': '/template-application-angular/ui/perspectives/views/master-list/index.html.template', 
        'action': 'generate',
        'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/index.html',
        'collection': 'uiListMasterModels',
        'engine': 'velocity'
    }, {
        'location': '/template-application-angular/ui/perspectives/views/master-list/extensions/view.js.template', 
        'action': 'generate',
        'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/extensions/view.js',
        'collection': 'uiListMasterModels'
    }, {
        'location': '/template-application-angular/ui/perspectives/views/master-list/master/index.html.template', 
        'action': 'generate',
        'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/master/index.html',
        'collection': 'uiListMasterModels',
        'engine': 'velocity'
    }, {
        'location': '/template-application-angular/ui/perspectives/views/master-list/master/controller.js.template', 
        'action': 'generate',
        'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/master/controller.js',
        'collection': 'uiListMasterModels',
        'engine': 'velocity'
    }, {
        'location': '/template-application-angular/ui/perspectives/views/master-list/master/extensions/view.js.template', 
        'action': 'generate',
        'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/master/extensions/view.js',
        'collection': 'uiListMasterModels'
    }];
}

function getDetails(parameters) {
    return [{
        'location': '/template-application-angular/ui/perspectives/views/master-list/details/index.html.template', 
        'action': 'generate',
        'rename': 'ui/{{perspectiveName}}/views/master/details/{{fileName}}/index.html',
        'collection': 'uiListDetailsModels',
        'engine': 'velocity'
    }, {
        'location': '/template-application-angular/ui/perspectives/views/master-list/details/controller.js.template', 
        'action': 'generate',
        'rename': 'ui/{{perspectiveName}}/views/master/details/{{fileName}}/controller.js',
        'collection': 'uiListDetailsModels',
        'engine': 'velocity'
    }, {
        'location': '/template-application-angular/ui/perspectives/views/master-list/details/extensions/view.js.template', 
        'action': 'generate',
        'rename': 'ui/{{perspectiveName}}/views/master/details/{{fileName}}/extensions/view.js',
        'collection': 'uiListDetailsModels'
    }];
}