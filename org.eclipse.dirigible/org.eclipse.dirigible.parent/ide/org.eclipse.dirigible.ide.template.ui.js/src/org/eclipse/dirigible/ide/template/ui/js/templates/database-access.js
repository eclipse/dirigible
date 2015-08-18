/* globals $ */
/* eslint-env node */

var systemLib = require('system');

var count;
var connection = $.getDatasource().getConnection();
try {
    var statement = connection.createStatement();
    var rs = statement.executeQuery('SELECT COUNT(*) FROM ${tableName}');
    while (rs.next()) {
        count = rs.getInt(1);
    }
    systemLib.println('count: '  + count);
} finally {
    connection.close();
}

$.getResponse().getWriter().println(count);
$.getResponse().getWriter().flush();
$.getResponse().getWriter().close();