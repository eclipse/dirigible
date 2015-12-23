/* globals $ */
/* eslint-env node, dirigible */

var ioLib = require('io');
var entityLib = require('entity');

// create entity by parsing JSON object from request body
exports.create${entityName} = function() {
    var input = ioLib.read($\.getRequest().getInputStream());
    var requestBody = JSON.parse(input);
    var connection = $\.getDatasource().getConnection();
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
        var id = $\.getDatabaseUtils().getNext('${tableName}_${tableColumn.getName()}');
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
            statement.setDate(++i, $\.getDatabaseUtils().createDate(js_date_${tableColumn.getName().toLowerCase()}.getTime()));
        } else {
            statement.setDate(++i, null);
        }
#elseif ($tableColumn.getType() == $TIME)
        if (requestBody.${tableColumn.getName().toLowerCase()} !== null) {
            var js_date_${tableColumn.getName().toLowerCase()} =  new Date(Date.parse(requestBody.${tableColumn.getName().toLowerCase()})); 
            statement.setTime(++i, $\.getDatabaseUtils().createTime(js_date_${tableColumn.getName().toLowerCase()}.getTime()));
        } else {
            statement.setTime(++i, null);
        }
#elseif ($tableColumn.getType() == $TIMESTAMP)
        if (requestBody.${tableColumn.getName().toLowerCase()} !== null) {
            var js_date_${tableColumn.getName().toLowerCase()} =  new Date(Date.parse(requestBody.${tableColumn.getName().toLowerCase()}));
            statement.setTimestamp(++i, $\.getDatabaseUtils().createTimestamp(js_date_${tableColumn.getName().toLowerCase()}.getTime()));
        } else {
            statement.setTimestamp(++i, null);
        }
#else
    // not supported type: requestBody.${tableColumn.getName().toLowerCase()}
#end
#end
#end
        statement.executeUpdate();
		$\.getResponse().getWriter().println(id);
        return id;
    } catch(e) {
        var errorCode = $\.getResponse().SC_BAD_REQUEST;
        entityLib.printError(errorCode, errorCode, e.message);
    } finally {
        connection.close();
    }
    return -1;
};

// read single entity by id and print as JSON object to response
exports.read${entityName}Entity = function(id) {
    var connection = $\.getDatasource().getConnection();
    try {
        var result;
        var statement = connection.prepareStatement("SELECT * FROM ${tableName} WHERE " + exports.pkToSQL());
        statement.setInt(1, id);
        
        var resultSet = statement.executeQuery();
        if (resultSet.next()) {
            result = createEntity(resultSet);
        } else {
        	entityLib.printError($\.getResponse().SC_NOT_FOUND, 1, "Record with id: " + id + " does not exist.");
        }
        var jsonResponse = JSON.stringify(result, null, 2);
        $\.getResponse().getWriter().println(jsonResponse);
    } catch(e){
        var errorCode = $\.getResponse().SC_BAD_REQUEST;
        entityLib.printError(errorCode, errorCode, e.message);
    } finally {
        connection.close();
    }
};

// read all entities and print them as JSON array to response
exports.read${entityName}List = function(limit, offset, sort, desc) {
    var connection = $\.getDatasource().getConnection();
    try {
        var result = [];
        var sql = "SELECT ";
        if (limit !== null && offset !== null) {
            sql += " " + $\.getDatabaseUtils().createTopAndStart(limit, offset);
        }
        sql += " * FROM ${tableName}";
        if (sort !== null) {
            sql += " ORDER BY " + sort;
        }
        if (sort !== null && desc !== null) {
            sql += " DESC ";
        }
        if (limit !== null && offset !== null) {
            sql += " " + $\.getDatabaseUtils().createLimitAndOffset(limit, offset);
        }
        var statement = connection.prepareStatement(sql);
        var resultSet = statement.executeQuery();
        while (resultSet.next()) {
            result.push(createEntity(resultSet));
        }
        var jsonResponse = JSON.stringify(result, null, 2);
        $\.getResponse().getWriter().println(jsonResponse);
    } catch(e){
        var errorCode = $\.getResponse().SC_BAD_REQUEST;
        entityLib.printError(errorCode, errorCode, e.message);
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
    var input = ioLib.read($\.getRequest().getInputStream());
    var responseBody = JSON.parse(input);
    var connection = $\.getDatasource().getConnection();
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
            statement.setDate(++i, $\.getDatabaseUtils().createDate(js_date_${tableColumn.getName().toLowerCase()}.getTime()));
        } else {
            statement.setDate(++i, null);
        }
#elseif ($tableColumn.getType() == $TIME)
        if (responseBody.${tableColumn.getName().toLowerCase()} !== null) {
            var js_date_${tableColumn.getName().toLowerCase()} =  new Date(Date.parse(responseBody.${tableColumn.getName().toLowerCase()})); 
            statement.setTime(++i, $\.getDatabaseUtils().createTime(js_date_${tableColumn.getName().toLowerCase()}.getTime()));
        } else {
            statement.setTime(++i, null);
        }
#elseif ($tableColumn.getType() == $TIMESTAMP)
        if (responseBody.${tableColumn.getName().toLowerCase()} !== null) {
            var js_date_${tableColumn.getName().toLowerCase()} =  new Date(Date.parse(responseBody.${tableColumn.getName().toLowerCase()}));
            statement.setTimestamp(++i, $\.getDatabaseUtils().createTimestamp(js_date_${tableColumn.getName().toLowerCase()}.getTime()));
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
		$\.getResponse().getWriter().println(id);
    } catch(e){
        var errorCode = $\.getResponse().SC_BAD_REQUEST;
        entityLib.printError(errorCode, errorCode, e.message);
    } finally {
        connection.close();
    }
};

// delete entity
exports.delete${entityName} = function(id) {
    var connection = $\.getDatasource().getConnection();
    try {
        var statement = connection.prepareStatement("DELETE FROM ${tableName} WHERE " + exports.pkToSQL());
        statement.setString(1, id);
        statement.executeUpdate();
        $\.getResponse().getWriter().println(id);
    } catch(e){
        var errorCode = $\.getResponse().SC_BAD_REQUEST;
        entityLib.printError(errorCode, errorCode, e.message);
    } finally {
        connection.close();
    }
};

exports.count${entityName} = function() {
    var count = 0;
    var connection = $\.getDatasource().getConnection();
    try {
        var statement = connection.createStatement();
        var rs = statement.executeQuery('SELECT COUNT(*) FROM ${tableName}');
        if (rs.next()) {
            count = rs.getInt(1);
        }
    } catch(e){
        var errorCode = $\.getResponse().SC_BAD_REQUEST;
        entityLib.printError(errorCode, errorCode, e.message);
    } finally {
        connection.close();
    }
    $\.getResponse().getWriter().println(count);
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

	$\.getResponse().getWriter().println(JSON.stringify(entityMetadata));
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
        throw $\.getExceptionUtils().createException("There is no primary key");
    } else if(result.length > 1) {
        throw $\.getExceptionUtils().createException("More than one Primary Key is not supported.");
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