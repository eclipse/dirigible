/* globals $ */
/* eslint-env node, dirigible */

var entityLib = require('entity');
var entity${entityName} = require('${projectName}/${fileNameNoExtension}_lib');

handleRequest();

function handleRequest() {
	
	$\.getResponse().setContentType("application/json; charset=UTF-8");
	$\.getResponse().setCharacterEncoding("UTF-8");
	
	// get method type
	var method = $\.getRequest().getMethod();
	method = method.toUpperCase();
	
	// retrieve the id as parameter if exist 
	var count = $\.getXssUtils().escapeSql($\.getRequest().getParameter('count'));
	var metadata = $\.getXssUtils().escapeSql($\.getRequest().getParameter('metadata'));
	var sort = $\.getXssUtils().escapeSql($\.getRequest().getParameter('sort'));
	var limit = $\.getXssUtils().escapeSql($\.getRequest().getParameter('limit'));
	var offset = $\.getXssUtils().escapeSql($\.getRequest().getParameter('offset'));
	var desc = $\.getXssUtils().escapeSql($\.getRequest().getParameter('desc'));
	
	if (limit === null) {
		limit = 100;
	}
	if (offset === null) {
		offset = 0;
	}
	
	if(!entityLib.hasConflictingParameters(null, count, metadata)) {
		// switch based on method type
		if ((method === 'GET')) {
			// read
			if (count !== null) {
				entity${entityName}.count${entityName}();
			} else if (metadata !== null) {
				entity${entityName}.metadata${entityName}();
			} else {
				entity${entityName}.read${entityName}List(limit, offset, sort, desc);
			}
		} else {
			// create, update, delete
			entityLib.printError($\.getResponse().SC_METHOD_NOT_ALLOWED, 1, "Method not allowed"); 
		}
	}
	
	// flush and close the response
	$\.getResponse().getWriter().flush();
	$\.getResponse().getWriter().close();
}