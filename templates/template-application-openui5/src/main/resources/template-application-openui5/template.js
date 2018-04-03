exports.getTemplate = function() {
	return {
		'name': 'Full-stack Application (OpenUI5)',
		'description': 'Full-stack Application with a Database Schema, a set of REST Services and an OpenUI5 User Interfaces',
		'model':'true',
		'sources': [{
			'location': '/template-application-openui5/api/application.js.template', 
			'action': 'generate',
			'rename': 'api/{{fileName}}.js',
			'collection': 'dataModels'
		}, {
			'location': '/template-application-openui5/api/shell/perspectives.js.template', 
			'action': 'generate',
			'rename': 'api/shell/perspectives.js'
		}, {
			'location': '/template-application-openui5/data/application.schema.template', 
			'action': 'generate',
			'rename': 'data/{{fileNameBase}}.schema'
		}, {
			'location': '/template-application-openui5/extensions/perspective/application.extension.template', 
			'action': 'generate',
			'rename': 'extensions/perspective/home.extension'
		}, {
			'location': '/template-application-openui5/extensions/perspective/application.js.template', 
			'action': 'generate',
			'rename': 'extensions/perspective/home.js'
		}, {
			'location': '/template-application-openui5/index.html.template', 
			'action': 'generate',
			'rename': 'index.html'
		}, {
			'location': '/template-application-openui5/ui/view/Navigation.view.xml.template', 
			'action': 'generate',
			'rename': 'ui/view/Navigation.view.xml'
		}, {
			'location': '/template-application-openui5/ui/view/View.view.xml.template', 
			'action': 'generate',
			'rename': 'ui/view/{{fileName}}.view.xml',
			'start': '[[',
			'end': ']]',
			'collection': 'uiManageModels'
		},  {
			'location': '/template-application-openui5/ui/view/AddEditDialog.fragment.xml.template', 
			'action': 'generate',
			'rename': 'ui/view/AddEditDialog{{fileName}}.fragment.xml',
			'start': '[[',
			'end': ']]',
			'collection': 'uiManageModels'
		}, {
			'location': '/template-application-openui5/ui/controller/Navigation.controller.js.template', 
			'action': 'generate',
			'rename': 'ui/controller/Navigation.controller.js'
		}, {
			'location': '/template-application-openui5/ui/controller/View.controller.js.template', 
			'action': 'generate',
			'rename': 'ui/controller/{{fileName}}.controller.js',
			'start': '[[',
			'end': ']]',
			'collection': 'uiManageModels'
		}, {
			'location': '/template-application-openui5/ui/Component.js.template', 
			'action': 'generate',
			'rename': 'ui/Component.js'
		}, {
			'location': '/template-application-openui5/ui/manifest.json.template', 
			'action': 'generate',
			'rename': 'ui/manifest.json'
		}, {
			'location': '/template-application-openui5/ui/model/models.js.template', 
			'action': 'generate',
			'rename': 'ui/model/models.js'
		}, {
			'location': '/template-application-openui5/ui/model/entity.json.template', 
			'action': 'generate',
			'rename': 'ui/model/entity.json'
		}],
		'parameters': [{
			'name': 'extensionName',
			'label': 'Extension Name'
		}, {
			'name': 'brand',
			'label': 'Brand'
		}]
	};
};