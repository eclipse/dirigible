/* globals $ */
/* eslint-env node, dirigible */

var database = require('db/database');
var response = require('net/http/response');

response.setContentType("application/json; charset=UTF-8");
response.setCharacterEncoding("UTF-8");

var count;
var connection = database.getConnection();
try {
    var statement = connection.createStatement();
    var rs = statement.executeQuery('SELECT COUNT(*) FROM ${tableName}');
    while (rs.next()) {
        count = rs.getInt(1);
    }
    console.log('count: '  + count);
} finally {
    connection.close();
}

response.println(count);
response.flush();
response.close();