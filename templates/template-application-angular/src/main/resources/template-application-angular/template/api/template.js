exports.getSources = function(parameters) {
	var sources = [];
	sources = sources.concat(getApi(parameters));
	sources = sources.concat(getLaunchpadApi(parameters));
	return sources;
};

function getApi(parameters) {
	return [{
		'location': '/template-application-angular/api/http.js.template', 
		'action': 'copy',
		'rename': 'api/http.js',
	}, {
		'location': '/template-application-angular/api/entity.js.template', 
		'action': 'generate',
		'rename': 'api/{{perspectiveName}}/{{fileName}}.js',
		'collection': 'models',
		'engine': 'velocity'
	}];
}

function getLaunchpadApi(parameters) {
	var sources = [];
	if (parameters && parameters.includeLaunchpad) {
		sources = [{
			'location': '/template-application-angular/api/launchpad/menu.js.template', 
			'action': 'generate',
			'rename': 'api/launchpad/menu.js'
		}, {
			'location': '/template-application-angular/api/launchpad/perspectives.js.template', 
			'action': 'generate',
			'rename': 'api/launchpad/perspectives.js'
		}, {
			'location': '/template-application-angular/api/launchpad/tiles.js.template', 
			'action': 'generate',
			'rename': 'api/launchpad/tiles.js'
		}, {
			'location': '/template-application-angular/api/launchpad/views.js.template', 
			'action': 'generate',
			'rename': 'api/launchpad/views.js'
		}];
	}
	return sources;
}