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
	
	//get primary keys (one primary key is supported!)
	var idParameter = entity${entityName}.getPrimaryKey();
	
	// retrieve the id as parameter if exist 
	var id = $\.getXssUtils().escapeSql($\.getRequest().getParameter(idParameter));
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
	
	if(!entityLib.hasConflictingParameters(id, count, metadata)) {
		// switch based on method type
		if ((method === 'POST')) {
			// create
			entity${entityName}.create${entityName}();
		} else if ((method === 'GET')) {
			// read
			if (id) {
				entity${entityName}.read${entityName}Entity(id);
			} else if (count !== null) {
				entity${entityName}.count${entityName}();
			} else if (metadata !== null) {
				entity${entityName}.metadata${entityName}();
			} else {
				entity${entityName}.read${entityName}List(limit, offset, sort, desc);
			}
		} else if ((method === 'PUT')) {
			// update
			entity${entityName}.update${entityName}();    
		} else if ((method === 'DELETE')) {
			// delete
			if(entityLib.isInputParameterValid(idParameter)){
				entity${entityName}.delete${entityName}(id);
			}
		} else {
			entityLib.printError($\.getResponse().SC_BAD_REQUEST, 1, "Invalid HTTP Method", method);
		}
	}
	
	// flush and close the response
	$\.getResponse().getWriter().flush();
	$\.getResponse().getWriter().close();
}