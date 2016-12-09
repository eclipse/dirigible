/* eslint-env node, dirigible */

exports.getMenuItem = function() {
	return {
	    'name': 'Help',
	    'subMenu': [{
	        'name': 'Help Contents',
	        'link': 'http://help.dirigible.io',
	        'newTab': true
	    }, {
	        'name': 'Samples',
	        'link': 'http://samples.dirigible.io',
	        'newTab': true
	    }, {
	        'name': 'Forum',
	        'link': 'http://forum.dirigible.io',
	        'newTab': true
	    }, {
	        'name': 'Bugzilla',
	        'link': 'http://bugs.dirigible.io',
	        'newTab': true
	    }, {
	        'name': 'Mailing List',
	        'link': 'http://mail.dirigible.io',
	        'newTab': true
	    }, {
	        'name': 'About',
	        'link': 'http://www.dirigible.io',
	        'newTab': true
	    }]
	};
};