exports.getSources = function(parameters) {
    return [{
        'location': '/template-application-angular/ui/perspectives/index.html.template', 
        'action': 'generate',
        'rename': 'ui/{{perspectiveName}}/index.html',
        'engine': 'velocity',
        'collection': 'uiPerspectives'
    }];
};