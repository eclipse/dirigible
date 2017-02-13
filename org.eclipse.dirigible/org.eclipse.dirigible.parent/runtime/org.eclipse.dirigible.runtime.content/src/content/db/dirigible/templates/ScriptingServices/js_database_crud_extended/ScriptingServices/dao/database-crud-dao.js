/* globals $ */
/* eslint-env node, dirigible */

var database = require('db/database');
var datasource = database.getDatasource();
var ${fileNameNoExtension}DaoExtensionsUtils = require('${packageName}/utils/${fileNameNoExtension}DaoExtensionUtils');

// Create an entity
exports.create = function(entity) {
    var connection = datasource.getConnection();
    try {
        var sql = 'INSERT INTO ${tableName} (##
#foreach ($tableColumn in $tableColumns)#if($velocityCount > 1),#end${tableColumn.name.toUpperCase()}#end) VALUES (##
#foreach ($tableColumn in $tableColumns)#if ($velocityCount > 1),#end?#end)';
        var statement = connection.prepareStatement(sql);
        var i = 0;
#foreach ($tableColumn in $tableColumns)
#if ($tableColumn.key)
        var id = datasource.getSequence('${tableName}_${tableColumn.name}').next();
        statement.setInt(++i, id);
#else    
#if ($tableColumn.type == $INTEGER)
        statement.setInt(++i, entity.${tableColumn.name.toLowerCase()});
#elseif ($tableColumn.type == $VARCHAR)
        statement.setString(++i, entity.${tableColumn.name.toLowerCase()});
#elseif ($tableColumn.type == $CHAR)
        statement.setString(++i, entity.${tableColumn.name.toLowerCase()});
#elseif ($tableColumn.type == $BIGINT)
        statement.setLong(++i, entity.${tableColumn.name.toLowerCase()});
#elseif ($tableColumn.type == $SMALLINT)
        statement.setShort(++i, entity.${tableColumn.name.toLowerCase()});
#elseif ($tableColumn.type == $FLOAT)
        statement.setFloat(++i, entity.${tableColumn.name.toLowerCase()});
#elseif ($tableColumn.type == $DOUBLE)
        statement.setDouble(++i, entity.${tableColumn.name.toLowerCase()});
#elseif ($tableColumn.type == $DATE)
        if (entity.${tableColumn.name.toLowerCase()} !== null) {
            var js_date_${tableColumn.name.toLowerCase()} =  new Date(Date.parse(entity.${tableColumn.name.toLowerCase()}));
            statement.setDate(++i, js_date_${tableColumn.name.toLowerCase()});
        } else {
            statement.setDate(++i, null);
        }
#elseif ($tableColumn.type == $TIME)
        if (entity.${tableColumn.name.toLowerCase()} !== null) {
            var js_date_${tableColumn.name.toLowerCase()} =  new Date(Date.parse(entity.${tableColumn.name.toLowerCase()})); 
            statement.setTime(++i, js_date_${tableColumn.name.toLowerCase()});
        } else {
            statement.setTime(++i, null);
        }
#elseif ($tableColumn.type == $TIMESTAMP)
        if (entity.${tableColumn.name.toLowerCase()} !== null) {
            var js_date_${tableColumn.name.toLowerCase()} =  new Date(Date.parse(entity.${tableColumn.name.toLowerCase()}));
            statement.setTimestamp(++i, js_date_${tableColumn.name.toLowerCase()});
        } else {
            statement.setTimestamp(++i, null);
        }
#else
    // not supported type: requestBody.${tableColumn.name.toLowerCase()}
#end
#end
#end
		${fileNameNoExtension}DaoExtensionsUtils.beforeCreate(connection, entity);
        statement.executeUpdate();
        ${fileNameNoExtension}DaoExtensionsUtils.afterCreate(connection, entity);
    	return id;
    } finally {
        connection.close();
    }
};

// Return a single entity by Id
exports.get = function(id) {
	var entity = null;
    var connection = datasource.getConnection();
    try {
        var sql = 'SELECT * FROM ${tableName} WHERE #foreach ($tableColumn in $tableColumns)#if ($tableColumn.key)${tableColumn.name}#end#end = ?';
        var statement = connection.prepareStatement(sql);
        statement.setInt(1, id);

        var resultSet = statement.executeQuery();
        if (resultSet.next()) {
            entity = createEntity(resultSet);
        }
    } finally {
        connection.close();
    }
    return entity;
};

// Return all entities
exports.list = function(limit, offset, sort, desc) {
    var result = [];
    var connection = datasource.getConnection();
    try {
        var sql = 'SELECT ';
        if (limit !== null && offset !== null) {
            sql += ' ' + datasource.getPaging().genTopAndStart(limit, offset);
        }
        sql += ' * FROM ${tableName}';
        if (sort !== null) {
            sql += ' ORDER BY ' + sort;
        }
        if (sort !== null && desc !== null) {
            sql += ' DESC ';
        }
        if (limit !== null && offset !== null) {
            sql += ' ' + datasource.getPaging().genLimitAndOffset(limit, offset);
        }
        var statement = connection.prepareStatement(sql);
        var resultSet = statement.executeQuery();
        while (resultSet.next()) {
            result.push(createEntity(resultSet));
        }
    } finally {
        connection.close();
    }
    return result;
};

// Update an entity by Id
exports.update = function(entity) {
    var connection = datasource.getConnection();
    try {
        var sql = 'UPDATE ${tableName} SET ##
#foreach ($tableColumn in $tableColumns)#if($addComma == true),#end #if($tableColumn.key == false)#set($addComma = true)${tableColumn.name} = ?#end#end
#foreach ($tableColumn in $tableColumns)#if ($tableColumn.key) WHERE ${tableColumn.name} = ?';
#end
#end
        var statement = connection.prepareStatement(sql);
        var i = 0;
#foreach ($tableColumn in $tableColumns)
#if ($tableColumn.key == false)
#if ($tableColumn.type == $INTEGER)
        statement.setInt(++i, entity.${tableColumn.name.toLowerCase()});
#elseif ($tableColumn.type == $VARCHAR)
        statement.setString(++i, entity.${tableColumn.name.toLowerCase()});
#elseif ($tableColumn.type == $CHAR)
        statement.setString(++i, entity.${tableColumn.name.toLowerCase()});
#elseif ($tableColumn.type == $BIGINT)
        statement.setLong(++i, entity.${tableColumn.name.toLowerCase()});
#elseif ($tableColumn.type == $SMALLINT)
        statement.setShort(++i, entity.${tableColumn.name.toLowerCase()});
#elseif ($tableColumn.type == $FLOAT)
        statement.setFloat(++i, entity.${tableColumn.name.toLowerCase()});
#elseif ($tableColumn.type == $DOUBLE)
        statement.setDouble(++i, entity.${tableColumn.name.toLowerCase()});
#elseif ($tableColumn.type == $DATE)
        if (entity.${tableColumn.name.toLowerCase()} !== null) {
            var js_date_${tableColumn.name.toLowerCase()} =  new Date(Date.parse(entity.${tableColumn.name.toLowerCase()}));
            statement.setDate(++i, js_date_${tableColumn.name.toLowerCase()});
        } else {
            statement.setDate(++i, null);
        }
#elseif ($tableColumn.type == $TIME)
        if (entity.${tableColumn.name.toLowerCase()} !== null) {
            var js_date_${tableColumn.name.toLowerCase()} =  new Date(Date.parse(entity.${tableColumn.name.toLowerCase()})); 
            statement.setTime(++i, js_date_${tableColumn.name.toLowerCase()});
        } else {
            statement.setTime(++i, null);
        }
#elseif ($tableColumn.type == $TIMESTAMP)
        if (entity.${tableColumn.name.toLowerCase()} !== null) {
            var js_date_${tableColumn.name.toLowerCase()} =  new Date(Date.parse(entity.${tableColumn.name.toLowerCase()}));
            statement.setTimestamp(++i, js_date_${tableColumn.name.toLowerCase()});
        } else {
            statement.setTimestamp(++i, null);
        }
#else
    // not supported type: responseBody.${tableColumn.name.toLowerCase()}
#end
#end
#end
#foreach ($tableColumn in $tableColumns)
#if ($tableColumn.key)
        statement.setInt(++i, entity.${tableColumn.name.toLowerCase()});
#end
#end
		${fileNameNoExtension}DaoExtensionsUtils.beforeUpdate(connection, entity);
        statement.executeUpdate();
        ${fileNameNoExtension}DaoExtensionsUtils.afterUpdate(connection, entity);
    } finally {
        connection.close();
    }
};

// Delete an entity
exports.delete = function(entity) {
    var connection = datasource.getConnection();
    try {
    	var sql = 'DELETE FROM ${tableName} WHERE #foreach ($tableColumn in $tableColumns)#if ($tableColumn.key)${tableColumn.name}#end#end = ?';
        var statement = connection.prepareStatement(sql);
        statement.setString(1, entity.#foreach ($tableColumn in $tableColumns)#if ($tableColumn.key)${tableColumn.name.toLowerCase()}#end#end);
        ${fileNameNoExtension}DaoExtensionsUtils.beforeDelete(connection, entity);
        statement.executeUpdate();
        ${fileNameNoExtension}DaoExtensionsUtils.afterDelete(connection, entity);
    } finally {
        connection.close();
    }
};

// Return the entities count
exports.count = function() {
    var count = 0;
    var connection = datasource.getConnection();
    try {
    	var sql = 'SELECT COUNT(*) FROM ${tableName}';
        var statement = connection.prepareStatement(sql);
        var rs = statement.executeQuery();
        if (rs.next()) {
            count = rs.getInt(1);
        }
    } finally {
        connection.close();
    }
    return count;
};

// Returns the metadata for the entity
exports.metadata = function() {
	var metadata = {
		name: '${tableName.toLowerCase()}',
		type: 'object',
		properties: [
#foreach ($tableColumn in $tableColumns)
		{
			name: '$tableColumn.name.toLowerCase()',
#if ($tableColumn.type == $INTEGER && $tableColumn.key)
			type: 'integer',
#elseif ($tableColumn.type == $INTEGER)
			type: 'integer'
#elseif ($tableColumn.type == $VARCHAR && $tableColumn.key)
    		type: 'string',
#elseif ($tableColumn.type == $VARCHAR)
			type: 'string'
#elseif ($tableColumn.type == $CHAR && $tableColumn.key)
			type: 'string',
#elseif ($tableColumn.type == $CHAR)
			type: 'string'
#elseif ($tableColumn.type == $BIGINT && $tableColumn.key)
			type: 'bigint',
#elseif ($tableColumn.type == $BIGINT)
			type: 'bigint'
#elseif ($tableColumn.type == $SMALLINT && $tableColumn.key)
			type: 'smallint',
#elseif ($tableColumn.type == $SMALLINT)
			type: 'smallint'
#elseif ($tableColumn.type == $FLOAT && $tableColumn.key)
			type: 'float',
#elseif ($tableColumn.type == $FLOAT)
			type: 'float'
#elseif ($tableColumn.type == $DOUBLE && $tableColumn.key)
			type: 'double',
#elseif ($tableColumn.type == $DOUBLE)
			type: 'double'
#elseif ($tableColumn.type == $DATE && $tableColumn.key)
			type: 'date',
#elseif ($tableColumn.type == $DATE)
			type: 'date'
#elseif ($tableColumn.type == $TIME && $tableColumn.key)
			type: 'time',
#elseif ($tableColumn.type == $TIME)
			type: 'time'
#elseif ($tableColumn.type == $TIMESTAMP && $tableColumn.key)
			type: 'timestamp',
#elseif ($tableColumn.type == $TIMESTAMP)
			type: 'timestamp'
#else
 			type: 'unknown',
#end
#if ($tableColumn.key)
			key: 'true',
			required: 'true'
#end
		},
#end
		]
	};
	return metadata;
};

// Create an entity as JSON object from ResultSet current Row
function createEntity(resultSet) {
    var result = {};
#foreach ($tableColumn in $tableColumns)
#if ($tableColumn.type == $INTEGER)
	result.${tableColumn.name.toLowerCase()} = resultSet.getInt('${tableColumn.name}');
#elseif ($tableColumn.type == $VARCHAR)
    result.${tableColumn.name.toLowerCase()} = resultSet.getString('${tableColumn.name}');
#elseif ($tableColumn.type == $CHAR)
    result.${tableColumn.name.toLowerCase()} = resultSet.getString('${tableColumn.name}');
#elseif ($tableColumn.type == $BIGINT)
    result.${tableColumn.name.toLowerCase()} = resultSet.getLong('${tableColumn.name}');
#elseif ($tableColumn.type == $SMALLINT)
    result.${tableColumn.name.toLowerCase()} = resultSet.getShort('${tableColumn.name}');
#elseif ($tableColumn.type == $FLOAT)
    result.${tableColumn.name.toLowerCase()} = resultSet.getFloat('${tableColumn.name}');
#elseif ($tableColumn.type == $DOUBLE)
    result.${tableColumn.name.toLowerCase()} = resultSet.getDouble('${tableColumn.name}');
#elseif ($tableColumn.type == $DATE)
    if (resultSet.getDate('${tableColumn.name}') !== null) {
		result.${tableColumn.name.toLowerCase()} = convertToDateString(new Date(resultSet.getDate('${tableColumn.name}').getTime()));
    } else {
        result.${tableColumn.name.toLowerCase()} = null;
    }
#elseif ($tableColumn.type == $TIME)
    if (resultSet.getTime('${tableColumn.name}') !== null) {
        result.${tableColumn.name.toLowerCase()} = new Date(resultSet.getTime('${tableColumn.name}').getTime()).toTimeString();
    } else {
        result.${tableColumn.name.toLowerCase()} = null;
    }
#elseif ($tableColumn.type == $TIMESTAMP)
    if (resultSet.getTimestamp('${tableColumn.name}') !== null) {
        result.${tableColumn.name.toLowerCase()} = new Date(resultSet.getTimestamp('${tableColumn.name}').getTime());
    } else {
        result.${tableColumn.name.toLowerCase()} = null;
    }
#else
    // not supported type: ${tableColumn.name}
#end
#end
    return result;
}

#foreach ($tableColumn in $tableColumns)
#if ($tableColumn.type == $DATE)
function convertToDateString(date) {
    var fullYear = date.getFullYear();
    var month = date.getMonth() < 10 ? '0' + date.getMonth() : date.getMonth();
    var dateOfMonth = date.getDate() < 10 ? '0' + date.getDate() : date.getDate();
    return fullYear + '/' + month + '/' + dateOfMonth;
}
#end
#end
