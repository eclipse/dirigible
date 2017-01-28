/* globals $ */
/* eslint-env node, dirigible */

var database = require('db/database');
var user = require('net/http/user');

var datasource = database.getDatasource();

// Create an entity
exports.create = function(entity) {
    var connection = datasource.getConnection();
    try {
        var sql = 'INSERT INTO IAM_ROLES (ROLE_ID,ROLE_ROLENAME,ROLE_DESCRIPTION,ROLE_CREATED_AT,ROLE_CREATED_BY) VALUES (?,?,?,?,?)';
        var statement = connection.prepareStatement(sql);
        var i = 0;
        var id = datasource.getSequence('IAM_ROLES_ROLE_ID').next();
        statement.setInt(++i, id);
        statement.setString(++i, entity.role_rolename);
        statement.setString(++i, entity.role_description);
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
        var sql = 'SELECT * FROM IAM_ROLES WHERE ROLE_ID = ?';
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
        sql += ' * FROM IAM_ROLES';
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
        var sql = 'UPDATE IAM_ROLES SET ROLE_ROLENAME = ?,ROLE_DESCRIPTION = ? WHERE ROLE_ID = ?';
        var statement = connection.prepareStatement(sql);
        var i = 0;
        statement.setString(++i, entity.role_rolename);
        statement.setString(++i, entity.role_description);
        var id = entity.role_id;
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
    	var sql = 'DELETE FROM IAM_ROLES WHERE ROLE_ID = ?';
        var statement = connection.prepareStatement(sql);
        statement.setString(1, entity.role_id);
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
    	var sql = 'SELECT COUNT(*) FROM IAM_ROLES';
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
		name: 'iam_roles',
		type: 'object',
		properties: [
		{
			name: 'role_id',
			type: 'integer',
			key: 'true',
			required: 'true'
		},
		{
			name: 'role_rolename',
			type: 'string'
		},
		{
			name: 'role_description',
			type: 'string'
		},
		{
			name: 'role_created_at',
			type: 'timestamp'
		},
		{
			name: 'role_created_by',
			type: 'string'
		},
		]
	};
	return metadata;
};

// Create an entity as JSON object from ResultSet current Row
function createEntity(resultSet) {
    var result = {};
	result.role_id = resultSet.getInt('ROLE_ID');
    result.role_rolename = resultSet.getString('ROLE_ROLENAME');
    result.role_description = resultSet.getString('ROLE_DESCRIPTION');
    if (resultSet.getTimestamp('ROLE_CREATED_AT') !== null) {
        result.role_created_at = new Date(resultSet.getTimestamp('ROLE_CREATED_AT').getTime());
    } else {
        result.role_created_at = null;
    }
    result.role_created_by = resultSet.getString('ROLE_CREATED_BY');
    return result;
}

