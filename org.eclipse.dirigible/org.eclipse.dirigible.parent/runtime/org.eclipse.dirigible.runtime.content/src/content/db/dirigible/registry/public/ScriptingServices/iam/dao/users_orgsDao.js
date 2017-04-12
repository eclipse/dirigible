/* globals $ */
/* eslint-env node, dirigible */

var database = require('db/database');
var datasource = database.getDatasource();

// Create an entity
exports.create = function(entity) {
    var connection = datasource.getConnection();
    try {
        var sql = 'INSERT INTO IAM_USERS_ORGS (USERORG_ID,USERORG_USERNAME,USERORG_ORG_ID,USERORG_POSITION_ID) VALUES (?,?,?,?)';
        var statement = connection.prepareStatement(sql);
        var i = 0;
        var id = datasource.getSequence('IAM_USERS_ORGS_USERORG_ID').next();
        statement.setInt(++i, id);
        statement.setString(++i, entity.username);
        statement.setInt(++i, entity.org_id);
        statement.setInt(++i, entity.position_id);
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
        var sql = 'SELECT * FROM IAM_USERS_ORGS WHERE USERORG_ID = ?';
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
        sql += ' * FROM IAM_USERS_ORGS';
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
        var sql = 'UPDATE IAM_USERS_ORGS SET USERORG_USERNAME = ?,USERORG_ORG_ID = ?,USERORG_POSITION_ID = ? WHERE USERORG_ID = ?';
        var statement = connection.prepareStatement(sql);
        var i = 0;
        statement.setString(++i, entity.username);
        statement.setInt(++i, entity.org_id);
        statement.setInt(++i, entity.position_id);
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
    	var sql = 'DELETE FROM IAM_USERS_ORGS WHERE USERORG_ID = ?';
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
    	var sql = 'SELECT COUNT(*) FROM IAM_USERS_ORGS';
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
		name: 'iam_users_orgs',
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
			name: 'org_id',
			type: 'integer'
		},
		{
			name: 'position_id',
			type: 'integer'
		},
		]
	};
	return metadata;
};

// Create an entity as JSON object from ResultSet current Row
function createEntity(resultSet) {
    var result = {};
	result.id = resultSet.getInt('USERORG_ID');
    result.username = resultSet.getString('USERORG_USERNAME');
	result.org_id = resultSet.getInt('USERORG_ORG_ID');
	result.position_id = resultSet.getInt('USERORG_POSITION_ID');
    return result;
}

