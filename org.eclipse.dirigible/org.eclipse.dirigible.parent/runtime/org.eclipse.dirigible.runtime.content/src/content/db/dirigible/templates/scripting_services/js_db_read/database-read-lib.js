/* globals $ */
/* eslint-env node, dirigible */

var request = require("net/http/request");
var response = require("net/http/response");
var database = require("db/database");

var datasource = database.getDatasource();

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
        exports.printError(errorCode, errorCode, e.message);
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
        exports.printError(errorCode, errorCode, e.message);
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
    var param = request.getParameter(paramName);
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


