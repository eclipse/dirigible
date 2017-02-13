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
        statement.setString(++i, entity.username);
        statement.setString(++i, entity.rolename);
        statement.setShort(++i, entity.state);
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
        statement.setString(++i, entity.username);
        statement.setString(++i, entity.rolename);
        statement.setShort(++i, entity.state);
        var id = entity.id;
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
        statement.setString(1, entity.id);
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
			name: 'id',
			type: 'integer',
			key: 'true',
			required: 'true'
		},
		{
			name: 'username',
			type: 'string'
		},
		{
			name: 'rolename',
			type: 'string'
		},
		{
			name: 'state',
			type: 'smallint'
		},
		{
			name: 'createdAt',
			type: 'timestamp'
		},
		{
			name: 'createdBy',
			type: 'string'
		},
		]
	};
	return metadata;
};

// Create an entity as JSON object from ResultSet current Row
function createEntity(resultSet) {
    var result = {};
	result.id = resultSet.getInt('ASSIGN_ID');
    result.username = resultSet.getString('ASSIGN_USERNAME');
    result.rolename = resultSet.getString('ASSIGN_ROLENAME');
    result.state = resultSet.getShort('ASSIGN_STATE');
    if (resultSet.getTimestamp('ASSIGN_CREATED_AT') !== null) {
        result.createdAt = new Date(resultSet.getTimestamp('ASSIGN_CREATED_AT').getTime());
    } else {
        result.createdAt = null;
    }
    result.createdBy = resultSet.getString('ASSIGN_CREATED_BY');
    return result;
}

