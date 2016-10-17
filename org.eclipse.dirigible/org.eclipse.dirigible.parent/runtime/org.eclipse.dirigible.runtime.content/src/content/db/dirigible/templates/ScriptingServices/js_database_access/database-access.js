/* globals $ */
/* eslint-env node, dirigible */

var database = require('db/database');
var response = require('net/http/response');

var datasource = database.getDatasource();

response.setContentType('application/json; charset=UTF-8');
response.setCharacterEncoding('UTF-8');

var count;
var connection = datasource.getConnection();
try {
    var statement = connection.prepareStatement('SELECT COUNT(*) FROM ${tableName}');
    var rs = statement.executeQuery();
    while (rs.next()) {
        count = rs.getInt(1);
    }
    console.log('Count: '  + count);
} finally {
    connection.close();
}

response.println(count);
response.flush();
response.close();