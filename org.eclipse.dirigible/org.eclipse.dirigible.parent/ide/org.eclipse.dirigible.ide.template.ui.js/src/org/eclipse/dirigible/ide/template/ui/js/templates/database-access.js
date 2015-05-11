var systemLib = require('system');

var count;
var connection = datasource.getConnection();
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

response.getWriter().println(count);
response.getWriter().flush();
response.getWriter().close();