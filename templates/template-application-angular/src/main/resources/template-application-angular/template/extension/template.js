exports.getSources = function(parameters) {
	var sources = [];
	sources = sources.concat(getPerspectives(parameters));
	sources = sources.concat(getPrimaryModels(parameters));
	sources = sources.concat(getListModels(parameters));
	sources = sources.concat(getManageModels(parameters));
	sources = sources.concat(getListMasterDetailModels(parameters));
	sources = sources.concat(getManageMasterDetailModels(parameters));
	sources = sources.concat(getMenu(parameters));
	sources = sources.concat(getLaunchpad(parameters));
	return sources;
};

function getPerspectives(parameters) {
	return [{
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
		'location': '/template-application-angular/ui/perspectives/views/manage/extensions/view.extensionpoint.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/extensions/view.extensionpoint',
		'collection': 'uiPerspectives'
	}];
}

function getPrimaryModels(parameters) {
	return [{
		'location': '/template-application-angular/ui/perspectives/extensions/tile/tile.extension.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{name}}/extensions/tile/tile.extension',
		'collection': 'uiPrimaryModels'
	}, {
		'location': '/template-application-angular/ui/perspectives/extensions/tile/tile.js.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{name}}/extensions/tile/tile.js',
		'engine': 'velocity',
		'collection': 'uiPrimaryModels'
	}];
}

function getListModels(parameters) {
	return [{
		'location': '/template-application-angular/ui/perspectives/views/list/extensions/entity.extension.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/view.extension',
		'collection': 'uiListModels'
	}];
}

function getManageModels(parameters) {
	return [{
		'location': '/template-application-angular/ui/perspectives/views/manage/extensions/entity.extension.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/view.extension',
		'collection': 'uiManageModels'
	}];
}

function getListMasterDetailModels(parameters) {
	return [{
		'location': '/template-application-angular/ui/perspectives/views/master-list/extensions/entity-view.extensionpoint.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/master/extensions/view.extensionpoint',
		'collection': 'uiListMasterModels'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/master-list/extensions/entity.extension.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/extensions/view.extension',
		'collection': 'uiListMasterModels'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/master-list/master/extensions/entity-view-master.extension.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/master/extensions/view-master.extension',
		'collection': 'uiListMasterModels'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/master-list/details/extensions/entity-view-detail.extension.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/master/details/{{fileName}}/extensions/view-detail.extension',
		'collection': 'uiListDetailsModels',
		'engine': 'velocity'
	}];
}

function getManageMasterDetailModels(parameters) {
	return [{
		'location': '/template-application-angular/ui/perspectives/views/master-manage/extensions/entity-view.extensionpoint.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/master/extensions/view.extensionpoint',
		'collection': 'uiManageMasterModels'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/master-manage/extensions/entity.extension.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/extensions/view.extension',
		'collection': 'uiManageMasterModels'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/master-manage/master/extensions/entity-view-master.extension.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/master/extensions/view-master.extension',
		'collection': 'uiManageMasterModels'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/master-manage/details/extensions/entity-view-detail.extension.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/master/details/{{fileName}}/extensions/view-detail.extension',
		'collection': 'uiManageDetailsModels',
		'engine': 'velocity'
	}];
}

function getMenu(parameters) {
	return [{
		'location': '/template-application-angular/ui/extensions/menu.extensionpoint.template', 
		'action': 'generate',
		'rename': 'ui/extensions/menu.extensionpoint'
	}];
}

function getLaunchpad(parameters) {
	var sources = [];
	if (parameters && parameters.includeLaunchpad) {
		sources = [{
			'location': '/template-application-angular/ui/extensions/perspective.extensionpoint.template', 
			'action': 'generate',
			'rename': 'ui/extensions/perspective.extensionpoint'
		}, {
			'location': '/template-application-angular/ui/extensions/perspective.extension.template', 
			'action': 'generate',
			'rename': 'ui/extensions/perspective.extension'
		}, {
			'location': '/template-application-angular/ui/extensions/perspective.js.template', 
			'action': 'generate',
			'rename': 'ui/extensions/perspective.js'
		}, {
			'location': '/template-application-angular/ui/extensions/tiles.extensionpoint.template', 
			'action': 'generate',
			'rename': 'ui/extensions/tiles.extensionpoint'
		}];
	}
	return sources;
}