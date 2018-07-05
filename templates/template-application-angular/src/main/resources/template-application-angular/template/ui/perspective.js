exports.getSources = function(parameters) {
    return [{
        'location': '/template-application-angular/ui/perspectives/index.html.template', 
        'action': 'generate',
        'rename': 'ui/{{perspectiveName}}/index.html',
        'engine': 'velocity',
        'collection': 'uiPerspectives'
    }, {
		'location': '/template-application-angular/ui/perspectives/extensions/perspective/perspective.extension.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/extensions/perspective/perspective.extension',
		'engine': 'velocity',
		'collection': 'uiPerspectives'
	}, {
		'location': '/template-application-angular/ui/perspectives/extensions/perspective/perspective.js.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/extensions/perspective/perspective.js',
		'engine': 'velocity',
		'collection': 'uiPerspectives'
	}, {
		'location': '/template-application-angular/ui/perspectives/extensions/view.extensionpoint.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/extensions/view.extensionpoint',
		'engine': 'velocity',
		'collection': 'uiPerspectives'
	}];
};