/* globals $ */
/* eslint-env node, dirigible */

var request = require("net/http/request");
var response = require("net/http/response");
var database = require("db/database");
var xss = require("utils/xss");

var datasource = database.getDatasource();

// create entity by parsing JSON object from request body
exports.create${entityName} = function() {
    var input = request.readInputText();
    var requestBody = JSON.parse(input);
    var connection = datasource.getConnection();
    try {
        var sql = "INSERT INTO ${tableName} (";
#foreach ($tableColumn in $tableColumns)
#if ($velocityCount > 1)
        sql += ",";
#end
        sql += "${tableColumn.getName()}";
#end
        sql += ") VALUES ("; 
#foreach ($tableColumn in $tableColumns)
#if ($velocityCount > 1)
        sql += ",";
#end
        sql += "?";
#end
        sql += ")";

        var statement = connection.prepareStatement(sql);
        var i = 0;
#foreach ($tableColumn in $tableColumns)
#if ($tableColumn.isKey())
        var id = datasource.getSequence('${tableName}_${tableColumn.getName()}').next();
        statement.setInt(++i, id);
#else    
#if ($tableColumn.getType() == $INTEGER)
        statement.setInt(++i, requestBody.${tableColumn.getName().toLowerCase()});
#elseif ($tableColumn.getType() == $VARCHAR)
        statement.setString(++i, requestBody.${tableColumn.getName().toLowerCase()});
#elseif ($tableColumn.getType() == $CHAR)
        statement.setString(++i, requestBody.${tableColumn.getName().toLowerCase()});
#elseif ($tableColumn.getType() == $BIGINT)
        statement.setLong(++i, requestBody.${tableColumn.getName().toLowerCase()});
#elseif ($tableColumn.getType() == $SMALLINT)
        statement.setShort(++i, requestBody.${tableColumn.getName().toLowerCase()});
#elseif ($tableColumn.getType() == $FLOAT)
        statement.setFloat(++i, requestBody.${tableColumn.getName().toLowerCase()});
#elseif ($tableColumn.getType() == $DOUBLE)
        statement.setDouble(++i, requestBody.${tableColumn.getName().toLowerCase()});
#elseif ($tableColumn.getType() == $DATE)
        if (requestBody.${tableColumn.getName().toLowerCase()} !== null) {
            var js_date_${tableColumn.getName().toLowerCase()} =  new Date(Date.parse(requestBody.${tableColumn.getName().toLowerCase()}));
            statement.setDate(++i, js_date_${tableColumn.getName().toLowerCase()});
        } else {
            statement.setDate(++i, null);
        }
#elseif ($tableColumn.getType() == $TIME)
        if (requestBody.${tableColumn.getName().toLowerCase()} !== null) {
            var js_date_${tableColumn.getName().toLowerCase()} =  new Date(Date.parse(requestBody.${tableColumn.getName().toLowerCase()})); 
            statement.setTime(++i, js_date_${tableColumn.getName().toLowerCase()});
        } else {
            statement.setTime(++i, null);
        }
#elseif ($tableColumn.getType() == $TIMESTAMP)
        if (requestBody.${tableColumn.getName().toLowerCase()} !== null) {
            var js_date_${tableColumn.getName().toLowerCase()} =  new Date(Date.parse(requestBody.${tableColumn.getName().toLowerCase()}));
            statement.setTimestamp(++i, js_date_${tableColumn.getName().toLowerCase()});
        } else {
            statement.setTimestamp(++i, null);
        }
#else
    // not supported type: requestBody.${tableColumn.getName().toLowerCase()}
#end
#end
#end
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
exports.read${entityName}Entity = function(id) {
    var connection = datasource.getConnection();
    try {
        var result;
        var sql = "SELECT * FROM ${tableName} WHERE " + exports.pkToSQL();
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
exports.read${entityName}List = function(limit, offset, sort, desc) {
    var connection = datasource.getConnection();
    try {
        var result = [];
        var sql = "SELECT ";
        if (limit !== null && offset !== null) {
            sql += " " + datasource.getPaging().genTopAndStart(limit, offset);
        }
        sql += " * FROM ${tableName}";
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
#foreach ($tableColumn in $tableColumns)
#if ($tableColumn.getType() == $INTEGER)
	result.${tableColumn.getName().toLowerCase()} = resultSet.getInt("${tableColumn.getName()}");
#elseif ($tableColumn.getType() == $VARCHAR)
    result.${tableColumn.getName().toLowerCase()} = resultSet.getString("${tableColumn.getName()}");
#elseif ($tableColumn.getType() == $CHAR)
    result.${tableColumn.getName().toLowerCase()} = resultSet.getString("${tableColumn.getName()}");
#elseif ($tableColumn.getType() == $BIGINT)
    result.${tableColumn.getName().toLowerCase()} = resultSet.getLong("${tableColumn.getName()}");
#elseif ($tableColumn.getType() == $SMALLINT)
    result.${tableColumn.getName().toLowerCase()} = resultSet.getShort("${tableColumn.getName()}");
#elseif ($tableColumn.getType() == $FLOAT)
    result.${tableColumn.getName().toLowerCase()} = resultSet.getFloat("${tableColumn.getName()}");
#elseif ($tableColumn.getType() == $DOUBLE)
    result.${tableColumn.getName().toLowerCase()} = resultSet.getDouble("${tableColumn.getName()}");
#elseif ($tableColumn.getType() == $DATE) 
    if (resultSet.getDate("${tableColumn.getName()}") !== null) {
		result.${tableColumn.getName().toLowerCase()} = convertToDateString(new Date(resultSet.getDate("${tableColumn.getName()}").getTime()));
    } else {
        result.${tableColumn.getName().toLowerCase()} = null;
    }
#elseif ($tableColumn.getType() == $TIME)
    if (resultSet.getTime("${tableColumn.getName()}") !== null) {
        result.${tableColumn.getName().toLowerCase()} = new Date(resultSet.getTime("${tableColumn.getName()}").getTime()).toTimeString();
    } else {
        result.${tableColumn.getName().toLowerCase()} = null;
    }
#elseif ($tableColumn.getType() == $TIMESTAMP)
    if (resultSet.getTimestamp("${tableColumn.getName()}") !== null) {
        result.${tableColumn.getName().toLowerCase()} = new Date(resultSet.getTimestamp("${tableColumn.getName()}").getTime());
    } else {
        result.${tableColumn.getName().toLowerCase()} = null;
    }
#else
    // not supported type: ${tableColumn.getName()}
#end
#end
    return result;
}

function convertToDateString(date) {
    var fullYear = date.getFullYear();
    var month = date.getMonth() < 10 ? "0" + date.getMonth() : date.getMonth();
    var dateOfMonth = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();
    return fullYear + "/" + month + "/" + dateOfMonth;
}

// update entity by id
exports.update${entityName} = function() {
    var input = request.readInputText();
    var responseBody = JSON.parse(input);
    var connection = datasource.getConnection();
    try {
        var sql = "UPDATE ${tableName} SET ";
#foreach ($tableColumn in $tableColumnsWithoutKeys)
#if ($velocityCount > 1)
        sql += ",";
#end
        sql += "${tableColumn.getName()} = ?";
#end
#foreach ($tableColumn in $tableColumns)
#if ($tableColumn.isKey())
        sql += " WHERE ${tableColumn.getName()} = ?";
#end
#end
        var statement = connection.prepareStatement(sql);
        var i = 0;
#foreach ($tableColumn in $tableColumnsWithoutKeys)
#if ($tableColumn.getType() == $INTEGER)
        statement.setInt(++i, responseBody.${tableColumn.getName().toLowerCase()});
#elseif ($tableColumn.getType() == $VARCHAR)
        statement.setString(++i, responseBody.${tableColumn.getName().toLowerCase()});
#elseif ($tableColumn.getType() == $CHAR)
        statement.setString(++i, responseBody.${tableColumn.getName().toLowerCase()});
#elseif ($tableColumn.getType() == $BIGINT)
        statement.setLong(++i, responseBody.${tableColumn.getName().toLowerCase()});
#elseif ($tableColumn.getType() == $SMALLINT)
        statement.setShort(++i, responseBody.${tableColumn.getName().toLowerCase()});
#elseif ($tableColumn.getType() == $FLOAT)
        statement.setFloat(++i, responseBody.${tableColumn.getName().toLowerCase()});
#elseif ($tableColumn.getType() == $DOUBLE)
        statement.setDouble(++i, responseBody.${tableColumn.getName().toLowerCase()});
#elseif ($tableColumn.getType() == $DATE)
        if (responseBody.${tableColumn.getName().toLowerCase()} !== null) {
            var js_date_${tableColumn.getName().toLowerCase()} =  new Date(Date.parse(responseBody.${tableColumn.getName().toLowerCase()}));
            statement.setDate(++i, js_date_${tableColumn.getName().toLowerCase()});
        } else {
            statement.setDate(++i, null);
        }
#elseif ($tableColumn.getType() == $TIME)
        if (responseBody.${tableColumn.getName().toLowerCase()} !== null) {
            var js_date_${tableColumn.getName().toLowerCase()} =  new Date(Date.parse(responseBody.${tableColumn.getName().toLowerCase()})); 
            statement.setTime(++i, js_date_${tableColumn.getName().toLowerCase()});
        } else {
            statement.setTime(++i, null);
        }
#elseif ($tableColumn.getType() == $TIMESTAMP)
        if (responseBody.${tableColumn.getName().toLowerCase()} !== null) {
            var js_date_${tableColumn.getName().toLowerCase()} =  new Date(Date.parse(responseBody.${tableColumn.getName().toLowerCase()}));
            statement.setTimestamp(++i, js_date_${tableColumn.getName().toLowerCase()});
        } else {
            statement.setTimestamp(++i, null);
        }
#else
    // not supported type: responseBody.${tableColumn.getName().toLowerCase()}
#end
#end
#foreach ($tableColumn in $tableColumns)
#if ($tableColumn.isKey())
        var id = responseBody.${tableColumn.getName().toLowerCase()};
        statement.setInt(++i, id);
#end
#end
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
exports.delete${entityName} = function(id) {
    var connection = datasource.getConnection();
    try {
    	var sql = "DELETE FROM ${tableName} WHERE " + exports.pkToSQL();
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

exports.count${entityName} = function() {
    var count = 0;
    var connection = datasource.getConnection();
    try {
    	var sql = 'SELECT COUNT(*) FROM ${tableName}';
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

exports.metadata${entityName} = function() {
	var entityMetadata = {
		name: '${tableName.toLowerCase()}',
		type: 'object',
		properties: []
	};
	
#foreach ($tableColumn in $tableColumns)
	var property${tableColumn.getName().toLowerCase()} = {
		name: '$tableColumn.getName().toLowerCase()',
#if ($tableColumn.getType() == $INTEGER && $tableColumn.isKey())
		type: 'integer',
#elseif ($tableColumn.getType() == $INTEGER)
		type: 'integer'
#elseif ($tableColumn.getType() == $VARCHAR && $tableColumn.isKey())
    	type: 'string',
#elseif ($tableColumn.getType() == $VARCHAR)
		type: 'string'
#elseif ($tableColumn.getType() == $CHAR && $tableColumn.isKey())
		type: 'string',
#elseif ($tableColumn.getType() == $CHAR)
		type: 'string'
#elseif ($tableColumn.getType() == $BIGINT && $tableColumn.isKey())
		type: 'bigint',
#elseif ($tableColumn.getType() == $BIGINT)
		type: 'bigint'
#elseif ($tableColumn.getType() == $SMALLINT && $tableColumn.isKey())
		type: 'smallint',
#elseif ($tableColumn.getType() == $SMALLINT)
		type: 'smallint'
#elseif ($tableColumn.getType() == $FLOAT && $tableColumn.isKey())
		type: 'float',
#elseif ($tableColumn.getType() == $FLOAT)
		type: 'float'
#elseif ($tableColumn.getType() == $DOUBLE && $tableColumn.isKey())
		type: 'double',
#elseif ($tableColumn.getType() == $DOUBLE)
		type: 'double'
#elseif ($tableColumn.getType() == $DATE && $tableColumn.isKey())
		type: 'date',
#elseif ($tableColumn.getType() == $DATE)
		type: 'date'
#elseif ($tableColumn.getType() == $TIME && $tableColumn.isKey())
		type: 'time',
#elseif ($tableColumn.getType() == $TIME)
		type: 'time'
#elseif ($tableColumn.getType() == $TIMESTAMP && $tableColumn.isKey())
		type: 'timestamp',
#elseif ($tableColumn.getType() == $TIMESTAMP)
		type: 'timestamp'
#else
 		type: 'unknown',
#end
#if ($tableColumn.isKey())
	key: 'true',
	required: 'true'
#end
	};
    entityMetadata.properties.push(property${tableColumn.getName().toLowerCase()});

#end

	response.println(JSON.stringify(entityMetadata));
};

exports.getPrimaryKeys = function() {
    var result = [];
    var i = 0;
#foreach ($tableColumn in $tableColumns)
#if ($tableColumn.isKey())
    result[i++] = '$tableColumn.getName()';
#end
#end
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
