/* globals $ */
/* eslint-env node, dirigible */

var request = require("net/http/request");
var response = require("net/http/response");
var database = require("db/database");
var xss = require("utils/xss");

var datasource = database.getDatasource();

// create entity by parsing JSON object from request body
exports.create${User}_books = function() {
    var input = request.readInputText();
    var requestBody = JSON.parse(input);
    var connection = datasource.getConnection();
    try {
        var sql = "INSERT INTO ${USER}_BOOKS (";
        sql += "BOOKID";
        sql += ",";
        sql += "BOOKISBN";
        sql += ",";
        sql += "BOOKTITLE";
        sql += ",";
        sql += "BOOKAUTHOR";
        sql += ",";
        sql += "BOOKEDITOR";
        sql += ",";
        sql += "BOOKPUBLISHER";
        sql += ",";
        sql += "BOOKFORMAT";
        sql += ",";
        sql += "BOOKPUBLICATIONDATE";
        sql += ",";
        sql += "BOOKPRICE";
        sql += ") VALUES ("; 
        sql += "?";
        sql += ",";
        sql += "?";
        sql += ",";
        sql += "?";
        sql += ",";
        sql += "?";
        sql += ",";
        sql += "?";
        sql += ",";
        sql += "?";
        sql += ",";
        sql += "?";
        sql += ",";
        sql += "?";
        sql += ",";
        sql += "?";
        sql += ")";

        var statement = connection.prepareStatement(sql);
        var i = 0;
        var id = datasource.getSequence('${USER}_BOOKS_BOOKID').next();
        statement.setInt(++i, id);
        statement.setString(++i, requestBody.bookisbn);
        statement.setString(++i, requestBody.booktitle);
        statement.setString(++i, requestBody.bookauthor);
        statement.setString(++i, requestBody.bookeditor);
        statement.setString(++i, requestBody.bookpublisher);
        statement.setString(++i, requestBody.bookformat);
        if (requestBody.bookpublicationdate !== null) {
            var js_date_bookpublicationdate =  new Date(Date.parse(requestBody.bookpublicationdate));
            statement.setDate(++i, js_date_bookpublicationdate);
        } else {
            statement.setDate(++i, null);
        }
        statement.setDouble(++i, requestBody.bookprice);
        statement.executeUpdate();
		response.println(id);
        return id;
    } catch(e) {
        var errorCode = response.BAD_REQUEST;
        exports.printError(errorCode, errorCode, e.message, sql);
    } finally {
        connection.close();
    }
    return -1;
};

// read single entity by id and print as JSON object to response
exports.read${User}_booksEntity = function(id) {
    var connection = datasource.getConnection();
    try {
        var result;
        var sql = "SELECT * FROM ${USER}_BOOKS WHERE " + exports.pkToSQL();
        var statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        
        var resultSet = statement.executeQuery();
        if (resultSet.next()) {
            result = createEntity(resultSet);
        } else {
        	exports.printError(response.NOT_FOUND, 1, "Record with id: " + id + " does not exist.", sql);
        }
        var jsonResponse = JSON.stringify(result, null, 2);
        response.println(jsonResponse);
    } catch(e){
        var errorCode = response.BAD_REQUEST;
        exports.printError(errorCode, errorCode, e.message, sql);
    } finally {
        connection.close();
    }
};

// read all entities and print them as JSON array to response
exports.read${User}_booksList = function(limit, offset, sort, desc) {
    var connection = datasource.getConnection();
    try {
        var result = [];
        var sql = "SELECT ";
        if (limit !== null && offset !== null) {
            sql += " " + datasource.getPaging().genTopAndStart(limit, offset);
        }
        sql += " * FROM ${USER}_BOOKS";
        if (sort !== null) {
            sql += " ORDER BY " + sort;
        }
        if (sort !== null && desc !== null) {
            sql += " DESC ";
        }
        if (limit !== null && offset !== null) {
            sql += " " + datasource.getPaging().genLimitAndOffset(limit, offset);
        }
        var statement = connection.prepareStatement(sql);
        var resultSet = statement.executeQuery();
        while (resultSet.next()) {
            result.push(createEntity(resultSet));
        }
        var jsonResponse = JSON.stringify(result, null, 2);
        response.println(jsonResponse);
    } catch(e){
        var errorCode = response.BAD_REQUEST;
        exports.printError(errorCode, errorCode, e.message, sql);
    } finally {
        connection.close();
    }
};

//create entity as JSON object from ResultSet current Row
function createEntity(resultSet) {
    var result = {};
	result.bookid = resultSet.getInt("BOOKID");
    result.bookisbn = resultSet.getString("BOOKISBN");
    result.booktitle = resultSet.getString("BOOKTITLE");
    result.bookauthor = resultSet.getString("BOOKAUTHOR");
    result.bookeditor = resultSet.getString("BOOKEDITOR");
    result.bookpublisher = resultSet.getString("BOOKPUBLISHER");
    result.bookformat = resultSet.getString("BOOKFORMAT");
    if (resultSet.getDate("BOOKPUBLICATIONDATE") !== null) {
		result.bookpublicationdate = convertToDateString(new Date(resultSet.getDate("BOOKPUBLICATIONDATE").getTime()));
    } else {
        result.bookpublicationdate = null;
    }
    result.bookprice = resultSet.getDouble("BOOKPRICE");
    return result;
}

function convertToDateString(date) {
    var fullYear = date.getFullYear();
    var month = date.getMonth() < 10 ? "0" + date.getMonth() : date.getMonth();
    var dateOfMonth = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();
    return fullYear + "/" + month + "/" + dateOfMonth;
}

// update entity by id
exports.update${User}_books = function() {
    var input = request.readInputText();
    var responseBody = JSON.parse(input);
    var connection = datasource.getConnection();
    try {
        var sql = "UPDATE ${USER}_BOOKS SET ";
        sql += "BOOKISBN = ?";
        sql += ",";
        sql += "BOOKTITLE = ?";
        sql += ",";
        sql += "BOOKAUTHOR = ?";
        sql += ",";
        sql += "BOOKEDITOR = ?";
        sql += ",";
        sql += "BOOKPUBLISHER = ?";
        sql += ",";
        sql += "BOOKFORMAT = ?";
        sql += ",";
        sql += "BOOKPUBLICATIONDATE = ?";
        sql += ",";
        sql += "BOOKPRICE = ?";
        sql += " WHERE BOOKID = ?";
        var statement = connection.prepareStatement(sql);
        var i = 0;
        statement.setString(++i, responseBody.bookisbn);
        statement.setString(++i, responseBody.booktitle);
        statement.setString(++i, responseBody.bookauthor);
        statement.setString(++i, responseBody.bookeditor);
        statement.setString(++i, responseBody.bookpublisher);
        statement.setString(++i, responseBody.bookformat);
        if (responseBody.bookpublicationdate !== null) {
            var js_date_bookpublicationdate =  new Date(Date.parse(responseBody.bookpublicationdate));
            statement.setDate(++i, js_date_bookpublicationdate);
        } else {
            statement.setDate(++i, null);
        }
        statement.setDouble(++i, responseBody.bookprice);
        var id = responseBody.bookid;
        statement.setInt(++i, id);
        statement.executeUpdate();
		response.println(id);
    } catch(e){
        var errorCode = response.BAD_REQUEST;
        exports.printError(errorCode, errorCode, e.message, sql);
    } finally {
        connection.close();
    }
};

// delete entity
exports.delete${User}_books = function(id) {
    var connection = datasource.getConnection();
    try {
    	var sql = "DELETE FROM ${USER}_BOOKS WHERE " + exports.pkToSQL();
        var statement = connection.prepareStatement(sql);
        statement.setString(1, id);
        statement.executeUpdate();
        response.println(id);
    } catch(e){
        var errorCode = response.BAD_REQUEST;
        exports.printError(errorCode, errorCode, e.message, sql);
    } finally {
        connection.close();
    }
};

exports.count${User}_books = function() {
    var count = 0;
    var connection = datasource.getConnection();
    try {
    	var sql = 'SELECT COUNT(*) FROM ${USER}_BOOKS';
        var statement = connection.prepareStatement(sql);
        var rs = statement.executeQuery();
        if (rs.next()) {
            count = rs.getInt(1);
        }
    } catch(e){
        var errorCode = response.BAD_REQUEST;
        exports.printError(errorCode, errorCode, e.message, sql);
    } finally {
        connection.close();
    }
    response.println(count);
};

exports.metadata${User}_books = function() {
	var entityMetadata = {
		name: '${user}_books',
		type: 'object',
		properties: []
	};
	
	var propertybookid = {
		name: 'bookid',
		type: 'integer',
	key: 'true',
	required: 'true'
	};
    entityMetadata.properties.push(propertybookid);

	var propertybookisbn = {
		name: 'bookisbn',
		type: 'string'
	};
    entityMetadata.properties.push(propertybookisbn);

	var propertybooktitle = {
		name: 'booktitle',
		type: 'string'
	};
    entityMetadata.properties.push(propertybooktitle);

	var propertybookauthor = {
		name: 'bookauthor',
		type: 'string'
	};
    entityMetadata.properties.push(propertybookauthor);

	var propertybookeditor = {
		name: 'bookeditor',
		type: 'string'
	};
    entityMetadata.properties.push(propertybookeditor);

	var propertybookpublisher = {
		name: 'bookpublisher',
		type: 'string'
	};
    entityMetadata.properties.push(propertybookpublisher);

	var propertybookformat = {
		name: 'bookformat',
		type: 'string'
	};
    entityMetadata.properties.push(propertybookformat);

	var propertybookpublicationdate = {
		name: 'bookpublicationdate',
		type: 'date'
	};
    entityMetadata.properties.push(propertybookpublicationdate);

	var propertybookprice = {
		name: 'bookprice',
		type: 'double'
	};
    entityMetadata.properties.push(propertybookprice);


	response.println(JSON.stringify(entityMetadata));
};

exports.getPrimaryKeys = function() {
    var result = [];
    var i = 0;
    result[i++] = 'BOOKID';
    if (result === 0) {
        throw new Error("There is no primary key");
    } else if(result.length > 1) {
        throw new Error("More than one Primary Key is not supported.");
    }
    return result;
};

exports.getPrimaryKey = function() {
	return exports.getPrimaryKeys()[0].toLowerCase();
};

exports.pkToSQL = function() {
    var pks = exports.getPrimaryKeys();
    return pks[0] + " = ?";
};

exports.hasConflictingParameters = function(id, count, metadata) {
    if(id !== null && count !== null){
    	exports.printError(response.EXPECTATION_FAILED, 1, "Expectation failed: conflicting parameters - id, count");
        return true;
    }
    if(id !== null && metadata !== null){
    	exports.printError(response.EXPECTATION_FAILED, 2, "Expectation failed: conflicting parameters - id, metadata");
        return true;
    }
    return false;
};

// check whether the parameter exists 
exports.isInputParameterValid = function(paramName) {
	var param = xss.escapeSql(request.getAttribute("path"));
	if (!param) {
		param = xss.escapeSql(request.getParameter(paramName));
	}
    if(param === null || param === undefined){
    	exports.printError(response.PRECONDITION_FAILED, 3, "Expected parameter is missing: " + paramName);
        return false;
    }
    return true;
};

// print error
exports.printError = function(httpCode, errCode, errMessage, errContext) {
    var body = {'err': {'code': errCode, 'message': errMessage}};
    response.setStatus(httpCode);
    response.setHeader("Content-Type", "application/json");
    response.print(JSON.stringify(body));
    console.error(JSON.stringify(body));
    if (errContext !== null) {
    	console.error(JSON.stringify(errContext));
    }
};
