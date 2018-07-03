exports.getSources = function(parameters) {
	console.error('Report Table get Sources!!!');
	return [{
		'location': '/template-application-angular/ui/perspectives/views/report/table/index.html.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/index.html',
		'engine': 'velocity',
		'collection': 'uiReportTableModels'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/report/table/controller.js.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/controller.js',
		'engine': 'velocity',
		'collection': 'uiReportTableModels'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/report/table/extensions/view.js.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/view.js',
		'collection': 'uiReportTableModels'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/report/table/extensions/menu/item.extension.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/menu/item.extension',
		'collection': 'uiReportTableModels'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/report/table/extensions/menu/item.js.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/menu/item.js',
		'collection': 'uiReportTableModels'
	}];
};