/* globals $ */
/* eslint-env node, dirigible */

var database = require('db/database');
var user = require('net/http/user');

var datasource = database.getDatasource();

// Create an entity
exports.create = function(entity) {
    var connection = datasource.getConnection();
    try {
        var sql = 'INSERT INTO IAM_ASSIGN (ASSIGN_ID,ASSIGN_USERNAME,ASSIGN_ROLENAME,ASSIGN_STATE,ASSIGN_CREATED_AT,ASSIGN_CREATED_BY) VALUES (?,?,?,?,?,?)';
        var statement = connection.prepareStatement(sql);
        var i = 0;
        var id = datasource.getSequence('IAM_ASSIGN_ASSIGN_ID').next();
        statement.setInt(++i, id);
        statement.setString(++i, entity.assign_username);
        statement.setString(++i, entity.assign_rolename);
        statement.setShort(++i, entity.assign_state);
        statement.setTimestamp(++i, new Date());
        statement.setString(++i, user.getName());
        statement.executeUpdate();
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
        var sql = 'SELECT * FROM IAM_ASSIGN WHERE ASSIGN_ID = ?';
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
        sql += ' * FROM IAM_ASSIGN';
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
        var sql = 'UPDATE IAM_ASSIGN SET ASSIGN_USERNAME = ?,ASSIGN_ROLENAME = ?,ASSIGN_STATE = ? WHERE ASSIGN_ID = ?';
        var statement = connection.prepareStatement(sql);
        var i = 0;
        statement.setString(++i, entity.assign_username);
        statement.setString(++i, entity.assign_rolename);
        statement.setShort(++i, entity.assign_state);
        var id = entity.assign_id;
        statement.setInt(++i, id);
        statement.executeUpdate();
    } finally {
        connection.close();
    }
};

// Delete an entity
exports.delete = function(entity) {
    var connection = datasource.getConnection();
    try {
    	var sql = 'DELETE FROM IAM_ASSIGN WHERE ASSIGN_ID = ?';
        var statement = connection.prepareStatement(sql);
        statement.setString(1, entity.assign_id);
        statement.executeUpdate();
    } finally {
        connection.close();
    }
};

// Return the entities count
exports.count = function() {
    var count = 0;
    var connection = datasource.getConnection();
    try {
    	var sql = 'SELECT COUNT(*) FROM IAM_ASSIGN';
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
		name: 'iam_assign',
		type: 'object',
		properties: [
		{
			name: 'assign_id',
			type: 'integer',
			key: 'true',
			required: 'true'
		},
		{
			name: 'assign_username',
			type: 'string'
		},
		{
			name: 'assign_rolename',
			type: 'string'
		},
		{
			name: 'assign_state',
			type: 'smallint'
		},
		{
			name: 'assign_created_at',
			type: 'timestamp'
		},
		{
			name: 'assign_created_by',
			type: 'string'
		},
		]
	};
	return metadata;
};

// Create an entity as JSON object from ResultSet current Row
function createEntity(resultSet) {
    var result = {};
	result.assign_id = resultSet.getInt('ASSIGN_ID');
    result.assign_username = resultSet.getString('ASSIGN_USERNAME');
    result.assign_rolename = resultSet.getString('ASSIGN_ROLENAME');
    result.assign_state = resultSet.getShort('ASSIGN_STATE');
    if (resultSet.getTimestamp('ASSIGN_CREATED_AT') !== null) {
        result.assign_created_at = new Date(resultSet.getTimestamp('ASSIGN_CREATED_AT').getTime());
    } else {
        result.assign_created_at = null;
    }
    result.assign_created_by = resultSet.getString('ASSIGN_CREATED_BY');
    return result;
}

