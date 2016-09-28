/*eslint-env node */

exports.getItem = function() {
	var item = {
	    "name": "Help",
	    "subMenu": [{
	        "name": "Help Contents",
	        "link": "http://help.dirigible.io"
	    }, {
	        "name": "Samples",
	        "link": "http://samples.dirigible.io"
	    }, {
	        "name": "Forum",
	        "link": "http://forum.dirigible.io"
	    }, {
	        "name": "Bugzilla",
	        "link": "http://bugs.dirigible.io"
	    }, {
	        "name": "Mailing List",
	        "link": "http://mail.dirigible.io"
	    }, {
	        "name": "About",
	        "link": "http://www.dirigible.io"
	    }]
	};
	return item;
};

exports.getOrder = function() {
	return 4;
};
