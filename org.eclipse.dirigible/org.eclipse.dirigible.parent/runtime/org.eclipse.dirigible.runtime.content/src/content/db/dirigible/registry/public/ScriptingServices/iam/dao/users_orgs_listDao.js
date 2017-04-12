/* globals $ */
/* eslint-env node, dirigible */

var database = require('db/database');
var datasource = database.getDatasource();

// Return all entities
exports.list = function(limit, offset, sort, desc, username) {
    var result = [];
    var connection = datasource.getConnection();
    try {
        var sql = 'SELECT ';
        if (limit !== null && offset !== null) {
            sql += ' ' + datasource.getPaging().genTopAndStart(limit, offset);
        }
        sql += ' * FROM IAM_USERS_ORGS_VIEW';
        if (username) {
        	sql += ' WHERE USERORG_USERNAME=?';
    	}

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
        if (username) {
        	statement.setString(1, username);
    	}
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
exports.count = function(username) {
    var count = 0;
    var connection = datasource.getConnection();
    try {
    	var sql = 'SELECT COUNT(*) FROM IAM_USERS_ORGS_VIEW';
    	if (username) {
        	sql += ' WHERE USERORG_USERNAME=?';
    	}
        var statement = connection.prepareStatement(sql);
        if (username) {
        	statement.setString(1, username);
    	}
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
		name: 'iam_users_orgs_view',
		type: 'object',
		properties: [
		{
			name: 'id',
			type: 'integer'
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
			name: 'org_name',
			type: 'string'
		},
		{
			name: 'position_id',
			type: 'integer'
		},
		{
			name: 'position_name',
			type: 'string'
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
    result.org_name = resultSet.getString('ORG_NAME');
	result.position_id = resultSet.getInt('USERORG_POSITION_ID');
    result.position_name = resultSet.getString('POSITION_NAME');
    return result;
}

