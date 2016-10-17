/* globals $ */
/* eslint-env node, dirigible */

var database = require('db/database');
var datasource = database.getDatasource();

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
#if ($tableColumn.getType() == $INTEGER)
	result.${tableColumn.getName().toLowerCase()} = resultSet.getInt('${tableColumn.getName()}');
#elseif ($tableColumn.getType() == $VARCHAR)
    result.${tableColumn.getName().toLowerCase()} = resultSet.getString('${tableColumn.getName()}');
#elseif ($tableColumn.getType() == $CHAR)
    result.${tableColumn.getName().toLowerCase()} = resultSet.getString('${tableColumn.getName()}');
#elseif ($tableColumn.getType() == $BIGINT)
    result.${tableColumn.getName().toLowerCase()} = resultSet.getLong('${tableColumn.getName()}');
#elseif ($tableColumn.getType() == $SMALLINT)
    result.${tableColumn.getName().toLowerCase()} = resultSet.getShort('${tableColumn.getName()}');
#elseif ($tableColumn.getType() == $FLOAT)
    result.${tableColumn.getName().toLowerCase()} = resultSet.getFloat('${tableColumn.getName()}');
#elseif ($tableColumn.getType() == $DOUBLE)
    result.${tableColumn.getName().toLowerCase()} = resultSet.getDouble('${tableColumn.getName()}');
#elseif ($tableColumn.getType() == $DATE)
    if (resultSet.getDate('${tableColumn.getName()}') !== null) {
		result.${tableColumn.getName().toLowerCase()} = convertToDateString(new Date(resultSet.getDate('${tableColumn.getName()}').getTime()));
    } else {
        result.${tableColumn.getName().toLowerCase()} = null;
    }
#elseif ($tableColumn.getType() == $TIME)
    if (resultSet.getTime('${tableColumn.getName()}') !== null) {
        result.${tableColumn.getName().toLowerCase()} = new Date(resultSet.getTime('${tableColumn.getName()}').getTime()).toTimeString();
    } else {
        result.${tableColumn.getName().toLowerCase()} = null;
    }
#elseif ($tableColumn.getType() == $TIMESTAMP)
    if (resultSet.getTimestamp('${tableColumn.getName()}') !== null) {
        result.${tableColumn.getName().toLowerCase()} = new Date(resultSet.getTimestamp('${tableColumn.getName()}').getTime());
    } else {
        result.${tableColumn.getName().toLowerCase()} = null;
    }
#else
    // not supported type: ${tableColumn.getName()}
#end
#end
    return result;
}

#foreach ($tableColumn in $tableColumns)
#if ($tableColumn.getType() == $DATE)
function convertToDateString(date) {
    var fullYear = date.getFullYear();
    var month = date.getMonth() < 10 ? '0' + date.getMonth() : date.getMonth();
    var dateOfMonth = date.getDate() < 10 ? '0' + date.getDate() : date.getDate();
    return fullYear + '/' + month + '/' + dateOfMonth;
}
#end
#end