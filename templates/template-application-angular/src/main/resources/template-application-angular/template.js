exports.getTemplate = function() {
	return {
		'name': 'Full-stack Application (AngularJS)',
		'description': 'Full-stack Application with a Database Schema, a set of REST Services and an AngularJS User Interfaces',
		'model':'true',
		'sources': [
		{
			'_section': 'API',
			'location': '/template-application-angular/api/http.js.template', 
			'action': 'copy',
			'rename': 'api/http.js',
		}, {
			'_section': 'API',
			'location': '/template-application-angular/api/entity.js.template', 
			'action': 'generate',
			'rename': 'api/{{fileName}}.js',
			'collection': 'models',
			'engine': 'velocity'
		},



		{
			'_section': 'Data',
			'location': '/template-application-angular/data/application.schema.template', 
			'action': 'generate',
			'rename': 'data/{{fileNameBase}}.schema'
		}, {
			'_section': 'Data',
			'location': '/template-application-angular/data/dao/entity.js.template', 
			'action': 'generate',
			'rename': 'data/dao/{{fileName}}.js',
			'collection': 'models',
			'engine': 'velocity'
		},



		{
			'_section': 'Extensions',
			'location': '/template-application-angular/ui/perspectives/extensions/perspective/perspective.extension.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/extensions/perspective/perspective.extension',
			'collection': 'uiPerspectives'
		}, {
			'_section': 'Extensions',
			'location': '/template-application-angular/ui/perspectives/extensions/perspective/perspective.js.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/extensions/perspective/perspective.js',
			'engine': 'velocity',
			'collection': 'uiPerspectives'
		}, {
			'_section': 'Extensions',
			'location': '/template-application-angular/ui/perspectives/views/manage/extensions/view.extensionpoint.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/extensions/view.extensionpoint',
			'collection': 'uiPerspectives'
		},



		{
			'_section': 'Extensions',
			'location': '/template-application-angular/ui/perspectives/extensions/tile/tile.extension.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/{{name}}/extensions/tile/tile.extension',
			'collection': 'uiPrimaryModels'
		}, {
			'_section': 'Extensions',
			'location': '/template-application-angular/ui/perspectives/extensions/tile/tile.js.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/{{name}}/extensions/tile/tile.js',
			'engine': 'velocity',
			'collection': 'uiPrimaryModels'
		},



		{
			'_section': 'Extensions',
			'location': '/template-application-angular/ui/perspectives/views/manage/extensions/entity.extension.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/view.extension',
			'collection': 'uiManageModels'
		}, {
			'_section': 'Extensions',
			'location': '/template-application-angular/ui/perspectives/views/list/extensions/entity.extension.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/view.extension',
			'collection': 'uiListModels'
		}, {
			'_section': 'Extensions',
			'location': '/template-application-angular/ui/perspectives/views/master-list/extensions/entity-view.extensionpoint.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/master/extensions/view.extensionpoint',
			'collection': 'uiListMasterModels'
		}, {
			'_section': 'Extensions',
			'location': '/template-application-angular/ui/perspectives/views/master-list/extensions/entity.extension.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/extensions/view.extension',
			'collection': 'uiListMasterModels'
		}, {
			'_section': 'Extensions',
			'location': '/template-application-angular/ui/perspectives/views/master-list/master/extensions/entity-view-master.extension.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/master/extensions/view-master.extension',
			'collection': 'uiListMasterModels'
		}, {
			'_section': 'Extensions',
			'location': '/template-application-angular/ui/perspectives/views/master-list/details/extensions/entity-view-detail.extension.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/master/details/{{fileName}}/extensions/view-detail.extension',
			'collection': 'uiListDetailsModels',
			'engine': 'velocity'
		}, {
			'_section': 'Extensions',
			'location': '/template-application-angular/ui/perspectives/views/master-manage/extensions/entity-view.extensionpoint.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/master/extensions/view.extensionpoint',
			'collection': 'uiManageMasterModels'
		}, {
			'_section': 'Extensions',
			'location': '/template-application-angular/ui/perspectives/views/master-manage/extensions/entity.extension.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/extensions/view.extension',
			'collection': 'uiManageMasterModels'
		}, {
			'_section': 'Extensions',
			'location': '/template-application-angular/ui/perspectives/views/master-manage/master/extensions/entity-view-master.extension.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/master/extensions/view-master.extension',
			'collection': 'uiManageMasterModels'
		}, {
			'_section': 'Extensions',
			'location': '/template-application-angular/ui/perspectives/views/master-manage/details/extensions/entity-view-detail.extension.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/master/details/{{fileName}}/extensions/view-detail.extension',
			'collection': 'uiManageDetailsModels',
			'engine': 'velocity'
		}, {
			'_section': 'Extensions',
			'location': '/template-application-angular/extensions/menu.extensionpoint.template', 
			'action': 'generate',
			'rename': 'extensions/menu.extensionpoint'
		},



		{
			'_section': 'UI - Manage Models',
			'location': '/template-application-angular/ui/perspectives/views/manage/index.html.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/index.html',
			'collection': 'uiManageModels',
			'engine': 'velocity'
		}, {
			'_section': 'UI - Manage Models',
			'location': '/template-application-angular/ui/perspectives/views/manage/controller.js.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/controller.js',
			'collection': 'uiManageModels',
			'engine': 'velocity'
		}, {
			'_section': 'UI - Manage Models',
			'location': '/template-application-angular/ui/perspectives/views/manage/extensions/view.js.template', 
			'action': 'generate',
			'collection': 'uiManageModels',
			'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/view.js'
		}, {
			'_section': 'UI - Manage Models',
			'location': '/template-application-angular/ui/perspectives/views/manage/extensions/menu/item.extension.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/menu/item.extension',
			'collection': 'uiManageModels'
		}, {
			'_section': 'UI - Manage Models',
			'location': '/template-application-angular/ui/perspectives/views/manage/extensions/menu/item.js.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/menu/item.js',
			'collection': 'uiManageModels'
		},



		{
			'_section': 'UI - List Models',
			'location': '/template-application-angular/ui/perspectives/views/list/index.html.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/index.html',
			'collection': 'uiListModels',
			'engine': 'velocity'
		}, {
			'_section': 'UI - List Models',
			'location': '/template-application-angular/ui/perspectives/views/list/controller.js.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/controller.js',
			'collection': 'uiListModels',
			'engine': 'velocity'
		}, {
			'_section': 'UI - List Models',
			'location': '/template-application-angular/ui/perspectives/views/list/extensions/view.js.template', 
			'action': 'generate',
			'collection': 'uiListModels',
			'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/view.js'
		}, {
			'_section': 'UI - List Models',
			'location': '/template-application-angular/ui/perspectives/views/list/extensions/menu/item.extension.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/menu/item.extension',
			'collection': 'uiListModels'
		}, {
			'_section': 'UI - List Models',
			'location': '/template-application-angular/ui/perspectives/views/list/extensions/menu/item.js.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/menu/item.js',
			'collection': 'uiListModels'
		},



		{
			'_section': 'UI - List Master Models',
			'location': '/template-application-angular/ui/perspectives/views/master-list/index.html.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/index.html',
			'collection': 'uiListMasterModels',
		}, {
			'_section': 'UI - List Master Models',
			'location': '/template-application-angular/ui/perspectives/views/master-list/extensions/view.js.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/extensions/view.js',
			'collection': 'uiListMasterModels'
		}, {
			'_section': 'UI - List Master Models',
			'location': '/template-application-angular/ui/perspectives/views/master-list/master/index.html.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/master/index.html',
			'collection': 'uiListMasterModels',
			'engine': 'velocity'
		}, {
			'_section': 'UI - List Master Models',
			'location': '/template-application-angular/ui/perspectives/views/master-list/master/controller.js.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/master/controller.js',
			'collection': 'uiListMasterModels',
			'engine': 'velocity'
		}, {
			'_section': 'UI - List Master Models',
			'location': '/template-application-angular/ui/perspectives/views/master-list/master/extensions/view.js.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/master/extensions/view.js',
			'collection': 'uiListMasterModels'
		},



		{
			'_section': 'UI - List Details Models',
			'location': '/template-application-angular/ui/perspectives/views/master-list/details/index.html.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/master/details/{{fileName}}/index.html',
			'collection': 'uiListDetailsModels',
			'engine': 'velocity'
		}, {
			'_section': 'UI - List Details Models',
			'location': '/template-application-angular/ui/perspectives/views/master-list/details/controller.js.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/master/details/{{fileName}}/controller.js',
			'collection': 'uiListDetailsModels',
			'engine': 'velocity'
		}, {
			'_section': 'UI - List Details Models',
			'location': '/template-application-angular/ui/perspectives/views/master-list/details/extensions/view.js.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/master/details/{{fileName}}/extensions/view.js',
			'collection': 'uiListDetailsModels'
		},



		{
			'_section': 'UI - Manage Master Models',
			'location': '/template-application-angular/ui/perspectives/views/master-manage/index.html.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/index.html',
			'collection': 'uiManageMasterModels',
		}, {
			'_section': 'UI - Manage Master Models',
			'location': '/template-application-angular/ui/perspectives/views/master-manage/extensions/view.js.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/extensions/view.js',
			'collection': 'uiManageMasterModels'
		}, {
			'_section': 'UI - Manage Master Models',
			'location': '/template-application-angular/ui/perspectives/views/master-manage/master/index.html.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/master/index.html',
			'collection': 'uiManageMasterModels',
			'engine': 'velocity'
		}, {
			'_section': 'UI - Manage Master Models',
			'location': '/template-application-angular/ui/perspectives/views/master-manage/master/controller.js.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/master/controller.js',
			'collection': 'uiManageMasterModels',
			'engine': 'velocity'
		}, {
			'_section': 'UI - Manage Master Models',
			'location': '/template-application-angular/ui/perspectives/views/master-manage/master/extensions/view.js.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/master/extensions/view.js',
			'collection': 'uiManageMasterModels'
		},



		{
			'_section': 'UI - Manage Details Models',
			'location': '/template-application-angular/ui/perspectives/views/master-manage/details/index.html.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/master/details/{{fileName}}/index.html',
			'collection': 'uiManageDetailsModels',
			'engine': 'velocity'
		}, {
			'_section': 'UI - Manage Details Models',
			'location': '/template-application-angular/ui/perspectives/views/master-manage/details/controller.js.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/master/details/{{fileName}}/controller.js',
			'collection': 'uiManageDetailsModels',
			'engine': 'velocity'
		}, {
			'_section': 'UI - Manage Details Models',
			'location': '/template-application-angular/ui/perspectives/views/master-manage/details/extensions/view.js.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/views/master/details/{{fileName}}/extensions/view.js',
			'collection': 'uiManageDetailsModels'
		},



		{
			'_section': 'UI - Index.html',
			'location': '/template-application-angular/ui/perspectives/index.html.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/index.html',
			'collection': 'uiPerspectives'
		}],
		'parameters': [{
			'name': 'extensionName',
			'label': 'Extension Name'
		}, {
			'name': 'launchpadName',
			'label': 'Launchpad Name'
		}, {
			'name': 'brand',
			'label': 'Brand'
		}]
	};
};