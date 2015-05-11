var systemLib = require('system');
var ioLib = require('io');
var entityLib = require('entity');

// create entity by parsing JSON object from request body
exports.create${entityName} = function() {
    var input = ioLib.read(request.getReader());
    var message = JSON.parse(input);
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
        var id = db.getNext('${tableName}_${tableColumn.getName()}');
        statement.setInt(++i, id);
#else    
#if ($tableColumn.getType() == $INTEGER)
        statement.setInt(++i, message.${tableColumn.getName().toLowerCase()});
#elseif ($tableColumn.getType() == $VARCHAR)
        statement.setString(++i, message.${tableColumn.getName().toLowerCase()});
#elseif ($tableColumn.getType() == $CHAR)
        statement.setString(++i, message.${tableColumn.getName().toLowerCase()});
#elseif ($tableColumn.getType() == $BIGINT)
        statement.setLong(++i, message.${tableColumn.getName().toLowerCase()});
#elseif ($tableColumn.getType() == $SMALLINT)
        statement.setShort(++i, message.${tableColumn.getName().toLowerCase()});
#elseif ($tableColumn.getType() == $FLOAT)
        statement.setFloat(++i, message.${tableColumn.getName().toLowerCase()});
#elseif ($tableColumn.getType() == $DOUBLE)
        statement.setDouble(++i, message.${tableColumn.getName().toLowerCase()});
#elseif ($tableColumn.getType() == $DATE)
        if (message.${tableColumn.getName().toLowerCase()} !== null) {
            var js_date_${tableColumn.getName().toLowerCase()} =  new Date(Date.parse(message.${tableColumn.getName().toLowerCase()}));
            statement.setDate(++i, new java.sql.Date(js_date_${tableColumn.getName().toLowerCase()}.getTime() + js_date_${tableColumn.getName().toLowerCase()}.getTimezoneOffset()*60*1000));
        } else {
            statement.setDate(++i, null);
        }
#elseif ($tableColumn.getType() == $TIME)
        if (message.${tableColumn.getName().toLowerCase()} !== null) {
            var js_date_${tableColumn.getName().toLowerCase()} =  new Date(Date.parse(message.${tableColumn.getName().toLowerCase()})); 
            statement.setTime(++i, new java.sql.Time(js_date_${tableColumn.getName().toLowerCase()}.getTime() + js_date_${tableColumn.getName().toLowerCase()}.getTimezoneOffset()*60*1000));
        } else {
            statement.setTime(++i, null);
        }
#elseif ($tableColumn.getType() == $TIMESTAMP)
        if (message.${tableColumn.getName().toLowerCase()} !== null) {
            var js_date_${tableColumn.getName().toLowerCase()} =  new Date(Date.parse(message.${tableColumn.getName().toLowerCase()}));
            statement.setTimestamp(++i, new java.sql.Timestamp(js_date_${tableColumn.getName().toLowerCase()}.getTime() + js_date_${tableColumn.getName().toLowerCase()}.getTimezoneOffset()*60*1000));
        } else {
            statement.setTimestamp(++i, null);
        }
#else
    // not supported type: message.${tableColumn.getName().toLowerCase()}
#end
#end
#end
        statement.executeUpdate();
        response.getWriter().println(id);
        return id;
    } catch(e) {
        var errorCode = javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
        entityLib.printError(errorCode, errorCode, e.message);
    } finally {
        connection.close();
    }
    return -1;
};

// read single entity by id and print as JSON object to response
exports.read${entityName}Entity = function(id) {
    var connection = datasource.getConnection();
    try {
        var result = "";
        var sql = "SELECT * FROM ${tableName} WHERE " + pkToSQL();
        var statement = connection.prepareStatement(sql);
        statement.setString(1, id);
        
        var resultSet = statement.executeQuery();
        var value;
        while (resultSet.next()) {
            result = createEntity(resultSet);
        }
        if(result.length === 0){
            entityLib.printError(javax.servlet.http.HttpServletResponse.SC_NOT_FOUND, 1, "Record with id: " + id + " does not exist.");
        }
        var text = JSON.stringify(result, null, 2);
        response.getWriter().println(text);
    } catch(e){
        var errorCode = javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
        entityLib.printError(errorCode, errorCode, e.message);
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
            sql += " " + db.createTopAndStart(limit, offset);
        }
        sql += " * FROM ${tableName}";
        if (sort !== null) {
            sql += " ORDER BY " + sort;
        }
        if (sort !== null && desc !== null) {
            sql += " DESC ";
        }
        if (limit !== null && offset !== null) {
            sql += " " + db.createLimitAndOffset(limit, offset);
        }
        var statement = connection.prepareStatement(sql);
        var resultSet = statement.executeQuery();
        var value;
        while (resultSet.next()) {
            result.push(createEntity(resultSet));
        }
        var text = JSON.stringify(result, null, 2);
        response.getWriter().println(text);
    } catch(e){
        var errorCode = javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
        entityLib.printError(errorCode, errorCode, e.message);
    } finally {
        connection.close();
    }
};

//create entity as JSON object from ResultSet current Row
function createEntity(resultSet, data) {
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
		result.${tableColumn.getName().toLowerCase()} = convertToDateString(new Date(resultSet.getDate("${tableColumn.getName()}").getTime() - resultSet.getDate("${tableColumn.getName()}").getTimezoneOffset()*60*1000));
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
        result.${tableColumn.getName().toLowerCase()} = new Date(resultSet.getTimestamp("${tableColumn.getName()}").getTime() - resultSet.getDate("${tableColumn.getName()}").getTimezoneOffset()*60*1000);
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
    var input = ioLib.read(request.getReader());
    var message = JSON.parse(input);
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
        statement.setInt(++i, message.${tableColumn.getName().toLowerCase()});
#elseif ($tableColumn.getType() == $VARCHAR)
        statement.setString(++i, message.${tableColumn.getName().toLowerCase()});
#elseif ($tableColumn.getType() == $CHAR)
        statement.setString(++i, message.${tableColumn.getName().toLowerCase()});
#elseif ($tableColumn.getType() == $BIGINT)
        statement.setLong(++i, message.${tableColumn.getName().toLowerCase()});
#elseif ($tableColumn.getType() == $SMALLINT)
        statement.setShort(++i, message.${tableColumn.getName().toLowerCase()});
#elseif ($tableColumn.getType() == $FLOAT)
        statement.setFloat(++i, message.${tableColumn.getName().toLowerCase()});
#elseif ($tableColumn.getType() == $DOUBLE)
        statement.setDouble(++i, message.${tableColumn.getName().toLowerCase()});
#elseif ($tableColumn.getType() == $DATE)
        if (message.${tableColumn.getName().toLowerCase()} !== null) {
            var js_date =  new Date(Date.parse(message.${tableColumn.getName().toLowerCase()}));
            statement.setDate(++i, new java.sql.Date(js_date_${tableColumn.getName().toLowerCase()}.getTime() + js_date_${tableColumn.getName().toLowerCase()}.getTimezoneOffset()*60*1000));
        } else {
            statement.setDate(++i, null);
        }
#elseif ($tableColumn.getType() == $TIME)
        if (message.${tableColumn.getName().toLowerCase()} !== null) {
            var js_date_${tableColumn.getName().toLowerCase()} =  new Date(Date.parse(message.${tableColumn.getName().toLowerCase()})); 
            statement.setTime(++i, new java.sql.Time(js_date_${tableColumn.getName().toLowerCase()}.getTime() + js_date_${tableColumn.getName().toLowerCase()}.getTimezoneOffset()*60*1000));
        } else {
            statement.setTime(++i, null);
        }
#elseif ($tableColumn.getType() == $TIMESTAMP)
        if (message.${tableColumn.getName().toLowerCase()} !== null) {
            var js_date_${tableColumn.getName().toLowerCase()} =  new Date(Date.parse(message.${tableColumn.getName().toLowerCase()}));
            statement.setTimestamp(++i, new java.sql.Timestamp(js_date_${tableColumn.getName().toLowerCase()}.getTime() + js_date_${tableColumn.getName().toLowerCase()}.getTimezoneOffset()*60*1000));
        } else {
            statement.setTimestamp(++i, null);
        }
#else
    // not supported type: message.${tableColumn.getName().toLowerCase()}
#end
#end
        var id = "";
#foreach ($tableColumn in $tableColumns)
#if ($tableColumn.isKey())
        id = message.${tableColumn.getName().toLowerCase()};
        statement.setInt(++i, id);
#end
#end
        statement.executeUpdate();
        response.getWriter().println(id);
    } catch(e){
        var errorCode = javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
        entityLib.printError(errorCode, errorCode, e.message);
    } finally {
        connection.close();
    }
};

// delete entity
exports.delete${entityName} = function(id) {
    var connection = datasource.getConnection();
    try {
        var sql = "DELETE FROM ${tableName} WHERE "+pkToSQL();
        var statement = connection.prepareStatement(sql);
        statement.setString(1, id);
        var resultSet = statement.executeUpdate();
        response.getWriter().println(id);
    } catch(e){
        var errorCode = javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
        entityLib.printError(errorCode, errorCode, e.message);
    } finally {
        connection.close();
    }
};

exports.count${entityName} = function() {
    var count = 0;
    var connection = datasource.getConnection();
    try {
        var statement = connection.createStatement();
        var rs = statement.executeQuery('SELECT COUNT(*) FROM ${tableName}');
        while (rs.next()) {
            count = rs.getInt(1);
        }
    } catch(e){
        var errorCode = javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
        entityLib.printError(errorCode, errorCode, e.message);
    } finally {
        connection.close();
    }
    response.getWriter().println(count);
};

exports.metadata${entityName} = function() {
	var entityMetadata = {};
	entityMetadata.name = '${tableName.toLowerCase()}';
	entityMetadata.type = 'object';
	entityMetadata.properties = [];
	
#foreach ($tableColumn in $tableColumns)
	var property${tableColumn.getName().toLowerCase()} = {};
	property${tableColumn.getName().toLowerCase()}.name = '$tableColumn.getName().toLowerCase()';
#if ($tableColumn.getType() == $INTEGER)
	property${tableColumn.getName().toLowerCase()}.type = 'integer';
#elseif ($tableColumn.getType() == $VARCHAR)
    property${tableColumn.getName().toLowerCase()}.type = 'string';
#elseif ($tableColumn.getType() == $CHAR)
	property${tableColumn.getName().toLowerCase()}.type = 'string';
#elseif ($tableColumn.getType() == $BIGINT)
	property${tableColumn.getName().toLowerCase()}.type = 'bigint';
#elseif ($tableColumn.getType() == $SMALLINT)
	property${tableColumn.getName().toLowerCase()}.type = 'smallint';
#elseif ($tableColumn.getType() == $FLOAT)
	property${tableColumn.getName().toLowerCase()}.type = 'float';
#elseif ($tableColumn.getType() == $DOUBLE)
    property${tableColumn.getName().toLowerCase()}.type = 'double';
#elseif ($tableColumn.getType() == $DATE)
    property${tableColumn.getName().toLowerCase()}.type = 'date';
#elseif ($tableColumn.getType() == $TIME)
    property${tableColumn.getName().toLowerCase()}.type = 'time';
#elseif ($tableColumn.getType() == $TIMESTAMP)
    property${tableColumn.getName().toLowerCase()}.type = 'timestamp';
#else
    property${tableColumn.getName().toLowerCase()}.type = 'unknown';
#end
#if ($tableColumn.isKey())
	property${tableColumn.getName().toLowerCase()}.key = 'true';
	property${tableColumn.getName().toLowerCase()}.required = 'true';
#end
    entityMetadata.properties.push(property${tableColumn.getName().toLowerCase()});

#end

    response.getWriter().println(JSON.stringify(entityMetadata));
};

function getPrimaryKeys(){
    var result = [];
    var i = 0;
#foreach ($tableColumn in $tableColumns)
#if ($tableColumn.isKey())
    result[i++] = '$tableColumn.getName()';
#end
#end
    if (result === 0) {
        throw new Exception("There is no primary key");
    } else if(result.length > 1) {
        throw new Exception("More than one Primary Key is not supported.");
    }
    return result;
}

function getPrimaryKey(){
	return getPrimaryKeys()[0].toLowerCase();
}

function pkToSQL(){
    var pks = getPrimaryKeys();
    return pks[0] + " = ?";
}

exports.process${entityName} = function() {
	
	// get method type
	var method = request.getMethod();
	method = method.toUpperCase();
	
	//get primary keys (one primary key is supported!)
	var idParameter = getPrimaryKey();
	
	// retrieve the id as parameter if exist 
	var id = xss.escapeSql(request.getParameter(idParameter));
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
	
	if(!entityLib.hasConflictingParameters(id, count, metadata)) {
		// switch based on method type
		if ((method === 'POST')) {
			// create
			exports.create${entityName}();
		} else if ((method === 'GET')) {
			// read
			if (id) {
				exports.read${entityName}Entity(id);
			} else if (count !== null) {
				exports.count${entityName}();
			} else if (metadata !== null) {
				exports.metadata${entityName}();
			} else {
				exports.read${entityName}List(limit, offset, sort, desc);
			}
		} else if ((method === 'PUT')) {
			// update
			exports.update${entityName}();    
		} else if ((method === 'DELETE')) {
			// delete
			if(entityLib.isInputParameterValid(idParameter)){
				exports.delete${entityName}(id);
			}
		} else {
			entityLib.printError(javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST, 1, "Invalid HTTP Method");
		}
	}
	
	// flush and close the response
	response.getWriter().flush();
	response.getWriter().close();
};
