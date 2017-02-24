/* globals $ */
/* eslint-env node, dirigible */

var orm = {
    dbName: "${tableName}",
    properties: [
#foreach ($tableColumn in $tableColumns)
		{
			name: '$tableColumn.name.toLowerCase()',
			dbName: '$tableColumn.name.toUpperCase()',
#if ($tableColumn.key)
			id: true,
#end
#if ($tableColumn.size)
			size: $size,
#end
#if ($tableColumn.unique)
			unique: $unique,
#end
#if ($tableColumn.required)
			required: $required,
#end
#if ($tableColumn.type == $INTEGER)
			type: 'Int'
#elseif ($tableColumn.type == $VARCHAR)
    		type: 'String',
    		size: 255
#elseif ($tableColumn.type == $CHAR)
			type: 'String',
			size: 20
#elseif ($tableColumn.type == $BIGINT)
			type: 'Long'
#elseif ($tableColumn.type == $SMALLINT)
			type: 'Short'
#elseif ($tableColumn.type == $FLOAT)
			type: 'Float'
#elseif ($tableColumn.type == $DOUBLE)
			type: 'Double'
#elseif ($tableColumn.type == $DATE)
			type: 'Date'
#elseif ($tableColumn.type == $TIME)
			type: 'Time'
#elseif ($tableColumn.type == $TIMESTAMP)
			type: 'Timestamp'
#else
 			type: 'Unknown'
#end
		},
#end
    ]
#if ($associations)
    ,
    associationSets: {
#foreach ($association in $associations)
		"$association.name": {
#if ($association.multiplicity == $MANY_TO_MANY)
			"associationType": 'many-to-many',
#if ($association.key)
			"key": "$association.key",
#end			
#if ($association.joinKey)
			"joinKey": "$association.joinKey",
#end			
#if ($association.daoJoinPath)
			"daoJoin": require('${packageName}/dao/${association.daoJoin}Dao').get,
#end
#if ($association.daoNPath)
			"daoN": require('${packageName}/dao/${association.daoN}Dao').get,
#end
#elseif ($association.multiplicity == $ONE_TO_MANY)
			"associationType": 'one-to-many',
			"dao": require('${packageName}/dao/${association.dao}Dao').get,
#if ($association.key)
			"key": "$association.key",
#end
#if ($association.joinKey)
			"joinKey": "$association.joinKey",
#end
#end
		},
#end
    }
#end   
};

exports.get = function() {
	var dao = require('daoism/dao').get(orm);
	return dao;
};

