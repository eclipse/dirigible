/* eslint-env node, dirigible */

var query = require('db/v3/query');
var update = require('db/v3/update');

update.execute("CREATE TABLE T (A INT, B VARCHAR(10))");
update.execute("INSERT INTO T VALUES (1, 'ABC')");
update.execute("INSERT INTO T VALUES (2, 'DEF')");

var sql = "SELECT * FROM T WHERE A = ?";
var resultset = query.execute(sql, [1]);

console.log(JSON.stringify(resultset));

update.execute("DROP TABLE T");

((resultset !== null) && (resultset !== undefined));