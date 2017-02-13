/* globals $ */
/* eslint-env node, dirigible */

var database = require('db/database');
var user = require('net/http/user');

var datasource = database.getDatasource();

// Return a single entity for the current user
exports.get = function() {
	var entity = null;
    var connection = datasource.getConnection();
    try {
        var sql = 'SELECT * FROM IAM_USERS WHERE USER_USERNAME = ?';
        var statement = connection.prepareStatement(sql);
        statement.setString(1, user.getName());

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
    return exports.get();
};

// Update an entity by Id
exports.update = function(entity) {
    var connection = datasource.getConnection();
    try {
        var sql = 'UPDATE IAM_USERS SET USER_PASSWORD = ?,USER_FIRSTNAME = ?,USER_LASTNAME = ? WHERE USER_ID = ?';
        var statement = connection.prepareStatement(sql);
        var i = 0;
        statement.setString(++i, entity.password);
        statement.setString(++i, entity.firstName);
        statement.setString(++i, entity.lastname);
        var id = entity.id;
        statement.setInt(++i, id);
        statement.executeUpdate();
    } finally {
        connection.close();
    }
};

// Returns the metadata for the entity
exports.metadata = function() {
	var metadata = {
		name: 'iam_users',
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
			name: 'password',
			type: 'string'
		},
		{
			name: 'firstName',
			type: 'string'
		},
		{
			name: 'lastName',
			type: 'string'
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
	result.id = resultSet.getInt('USER_ID');
    result.username = resultSet.getString('USER_USERNAME');
    result.firstName = resultSet.getString('USER_FIRSTNAME');
    result.lastName = resultSet.getString('USER_LASTNAME');
    if (resultSet.getTimestamp('USER_CREATED_AT') !== null) {
        result.createdAt = new Date(resultSet.getTimestamp('USER_CREATED_AT').getTime());
    } else {
        result.createdAt = null;
    }
    result.createdBy = resultSet.getString('USER_CREATED_BY');
    return result;
}

