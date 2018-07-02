var apiTemplate = require('template-application-angular/template/api/template');
var dataTemplate = require('template-application-angular/template/data/template');
var extensionTemplate = require('template-application-angular/template/extension/template');
var uiTemplate = require('template-application-angular/template/ui/template');

exports.getTemplate = function(parameters) {
	return {
		'name': 'Full-stack Application (AngularJS)',
		'description': 'Full-stack Application with a Database Schema, a set of REST Services and an AngularJS User Interfaces',
		'model':'true',
		'sources': getSources(parameters),
		'parameters': [{
			'name': 'extensionName',
			'label': 'Extension',
			'placeholder': 'Extension name'
		}, {
			'name': 'launchpadName',
			'label': 'Launchpad',
			'placeholder': 'Launchpad project name',
			'ui': {
				'hide': {
					'property': 'includeLaunchpad',
					'value': true
				}
			}
		}, {
			'name': 'includeLaunchpad',
			'label': 'Embedded',
			'type': 'checkbox'
		}, {
			'name': 'title',
			'label': 'Title',
			'placeholder': 'Launchpad title',
			'ui': {
				'hide': {
					'property': 'includeLaunchpad',
					'value': false
				}
			}
		}, {
			'name': 'subTitle',
			'label': 'Sub-title',
			'placeholder': 'Launchpad sub-title',
			'ui': {
				'hide': {
					'property': 'includeLaunchpad',
					'value': false
				}
			}
		}, {
			'name': 'description',
			'label': 'Description',
			'placeholder': 'Launchpad description',
			'ui': {
				'hide': {
					'property': 'includeLaunchpad',
					'value': false
				}
			}
		}, {
			'name': 'brand',
			'label': 'Brand',
			'placeholder': 'Brand'
		}]
	};
};

function getSources(parameters) {
	var sources = [];
	sources = sources.concat(apiTemplate.getSources(parameters));
	sources = sources.concat(dataTemplate.getSources(parameters));
	sources = sources.concat(extensionTemplate.getSources(parameters));
	sources = sources.concat(uiTemplate.getSources(parameters));
	return sources;
}
