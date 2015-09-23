/* globals $ */
/* eslint-env node, dirigible */

var entityLib = require('entity');
var entity${User}_books = require('${user}_books/${user}_books_lib');

handleRequest();

function handleRequest() {
	
	// get method type
	var method = $.getRequest().getMethod();
	method = method.toUpperCase();
	
	//get primary keys (one primary key is supported!)
	var idParameter = entity${User}_books.getPrimaryKey();
	
	// retrieve the id as parameter if exist 
	var id = $.getXssUtils().escapeSql($.getRequest().getParameter(idParameter));
	var count = $.getXssUtils().escapeSql($.getRequest().getParameter('count'));
	var metadata = $.getXssUtils().escapeSql($.getRequest().getParameter('metadata'));
	var sort = $.getXssUtils().escapeSql($.getRequest().getParameter('sort'));
	var limit = $.getXssUtils().escapeSql($.getRequest().getParameter('limit'));
	var offset = $.getXssUtils().escapeSql($.getRequest().getParameter('offset'));
	var desc = $.getXssUtils().escapeSql($.getRequest().getParameter('desc'));
	
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
			if(entityLib.isInputParameterValid(idParameter)){
				entity${User}_books.delete${User}_books(id);
			}
		} else {
			entityLib.printError($.getResponse().SC_BAD_REQUEST, 1, "Invalid HTTP Method");
		}
	}
	
	// flush and close the response
	$.getResponse().getWriter().flush();
	$.getResponse().getWriter().close();
}
