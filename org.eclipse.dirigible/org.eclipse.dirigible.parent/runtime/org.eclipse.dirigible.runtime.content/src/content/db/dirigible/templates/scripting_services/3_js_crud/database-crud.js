/* globals $ */
/* eslint-env node, dirigible */

var entity${entityName} = require('${packageName}/${fileNameNoExtension}_lib');
var request = require("net/http/request");
var response = require("net/http/response");
var xss = require("utils/xss");

handleRequest();

function handleRequest() {
	
	response.setContentType("application/json; charset=UTF-8");
	response.setCharacterEncoding("UTF-8");
	
	// get method type
	var method = request.getMethod();
	method = method.toUpperCase();
	
	//get primary keys (one primary key is supported!)
	var idParameter = entity${entityName}.getPrimaryKey();
	
	// retrieve the id as parameter if exist 
	var id = xss.escapeSql(request.getAttribute("path"));
	if (!id) {
		id = xss.escapeSql(request.getParameter(idParameter));
	}
	var count = xss.escapeSql(request.getParameter('count'));
	var metadata = xss.escapeSql(request.getParameter('metadata'));
	var sort = xss.escapeSql(request.getParameter('sort'));
	var limit = xss.escapeSql(request.getParameter('limit'));
	var offset = xss.escapeSql(request.getParameter('offset'));
	var desc = xss.escapeSql(request.getParameter('desc'));
	
	if (limit === null) {
		limit = 100;
	}
	if (offset === null) {
		offset = 0;
	}
	
	if(!entity${entityName}.hasConflictingParameters(id, count, metadata)) {
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
			if(entity${entityName}.isInputParameterValid(idParameter)){
				entity${entityName}.delete${entityName}(id);
			}
		} else {
			entity${entityName}.printError(response.BAD_REQUEST, 4, "Invalid HTTP Method", method);
		}
	}
	
	// flush and close the response
	response.flush();
	response.close();
}