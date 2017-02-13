/* globals $ */
/* eslint-env node, dirigible */

var database = require('db/database');
var user = require('net/http/user');

var datasource = database.getDatasource();

// Create an entity
exports.create = function(entity) {
    var connection = datasource.getConnection();
    try {
        var sql = 'INSERT INTO IAM_USERS (USER_ID,USER_USERNAME,USER_PASSWORD,USER_FIRSTNAME,USER_LASTNAME,USER_CREATED_AT,USER_CREATED_BY) VALUES (?,?,?,?,?,?,?)';
        var statement = connection.prepareStatement(sql);
        var i = 0;
        var id = datasource.getSequence('IAM_USERS_USER_ID').next();
        statement.setInt(++i, id);
        statement.setString(++i, entity.username);
        statement.setString(++i, entity.password);
        statement.setString(++i, entity.firstName);
        statement.setString(++i, entity.lastName);
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
        var sql = 'SELECT * FROM IAM_USERS WHERE USER_ID = ?';
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
        sql += ' * FROM IAM_USERS';
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
        var sql = 'UPDATE IAM_USERS SET USER_USERNAME = ?,USER_PASSWORD = ?,USER_FIRSTNAME = ?,USER_LASTNAME = ? WHERE USER_ID = ?';
        var statement = connection.prepareStatement(sql);
        var i = 0;
        statement.setString(++i, entity.username);
        statement.setString(++i, entity.password);
        statement.setString(++i, entity.firstName);
        statement.setString(++i, entity.lastName);
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
    	var sql = 'DELETE FROM IAM_USERS WHERE USER_ID = ?';
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
    	var sql = 'SELECT COUNT(*) FROM IAM_USERS';
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

