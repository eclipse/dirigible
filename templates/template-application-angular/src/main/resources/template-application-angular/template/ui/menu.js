exports.getSources = function(parameters) {
	return [{
		'location': '/template-application-angular/ui/extensions/menu.extensionpoint.template', 
		'action': 'generate',
		'rename': 'ui/extensions/menu.extensionpoint'
	}];
};