exports.getSources = function(parameters) {
    var sources = [];
    if (parameters && parameters.includeLaunchpad) {
        sources = [{
			'location': '/template-application-angular/index.html.template', 
			'action': 'generate',
			'rename': 'index.html',
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
			'location': '/template-application-angular/resources/templates/tiles.html.template', 
			'action': 'copy',
			'rename': 'resources/templates/tiles.html'
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
		}];
    }
    return sources;
};