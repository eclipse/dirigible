exports.getSources = function(parameters) {
	var sources = [];
	sources = sources.concat(getMaster(parameters));
	sources = sources.concat(getDetails(parameters));
	return sources;
};

function getMaster(parameters) {
	return [{
		'location': '/template-application-angular/ui/perspectives/views/master-manage/index.html.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/index.html',
		'collection': 'uiManageMasterModels',
		'engine': 'velocity'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/master-manage/extensions/view.js.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/extensions/view.js',
		'collection': 'uiManageMasterModels'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/master-manage/master/index.html.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/master/index.html',
		'collection': 'uiManageMasterModels',
		'engine': 'velocity'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/master-manage/master/controller.js.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/master/controller.js',
		'collection': 'uiManageMasterModels',
		'engine': 'velocity'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/master-manage/master/extensions/view.js.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/master/extensions/view.js',
		'collection': 'uiManageMasterModels'
	}];
}

function getDetails(parameters) {
	return [{
		'location': '/template-application-angular/ui/perspectives/views/master-manage/details/index.html.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/master/details/{{fileName}}/index.html',
		'collection': 'uiManageDetailsModels',
		'engine': 'velocity'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/master-manage/details/controller.js.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/master/details/{{fileName}}/controller.js',
		'collection': 'uiManageDetailsModels',
		'engine': 'velocity'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/master-manage/details/extensions/view.js.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/master/details/{{fileName}}/extensions/view.js',
		'collection': 'uiManageDetailsModels'
	}];
}