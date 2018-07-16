exports.getSources = function(parameters) {
	return [{
		'location': '/template-application-angular/ui/perspectives/views/report/bar/index.html.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/index.html',
		'engine': 'velocity',
		'collection': 'uiReportBarsModels'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/report/bar/controller.js.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/controller.js',
		'engine': 'velocity',
		'collection': 'uiReportBarsModels'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/report/bar/extensions/view.js.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/view.js',
		'collection': 'uiReportBarsModels'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/report/bar/extensions/view.extension.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/view.extension',
		'collection': 'uiReportBarsModels'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/report/bar/extensions/menu/item.extension.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/menu/item.extension',
		'collection': 'uiReportBarsModels'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/report/bar/extensions/menu/item.js.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/menu/item.js',
		'collection': 'uiReportBarsModels'
	}];
};
