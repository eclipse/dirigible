/* globals $ */
/* eslint-env node, dirigible */

var entity${User}_books = require('${user}_books/${user}_books_lib');
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
	var idParameter = entity${User}_books.getPrimaryKey();
	
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
	
	if(!entity${User}_books.hasConflictingParameters(id, count, metadata)) {
		// switch based on method type
		if ((method === 'POST')) {
			// create
			entity${User}_books.create${User}_books();
		} else if ((method === 'GET')) {
			// read
			if (id) {
				entity${User}_books.read${User}_booksEntity(id);
			} else if (count !== null) {
				entity${User}_books.count${User}_books();
			} else if (metadata !== null) {
				entity${User}_books.metadata${User}_books();
			} else {
				entity${User}_books.read${User}_booksList(limit, offset, sort, desc);
			}
		} else if ((method === 'PUT')) {
			// update
			entity${User}_books.update${User}_books();    
		} else if ((method === 'DELETE')) {
			// delete
			if(entity${User}_books.isInputParameterValid(idParameter)){
				entity${User}_books.delete${User}_books(id);
			}
		} else {
			entity${User}_books.printError(response.BAD_REQUEST, 4, "Invalid HTTP Method", method);
		}
	}
	
	// flush and close the response
	response.flush();
	response.close();
}
