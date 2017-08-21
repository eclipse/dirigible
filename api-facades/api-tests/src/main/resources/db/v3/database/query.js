/* eslint-env node, dirigible */

var database = require('db/v3/database');

database.update("CREATE TABLE T (A INT, B VARCHAR(10))");
database.update("INSERT INTO T VALUES (1, 'ABC')");
database.update("INSERT INTO T VALUES (2, 'DEF')");

var sql = "SELECT * FROM T WHERE A = ?";
var resultset = database.query(sql, [1]);

console.log(JSON.stringify(resultset));

database.update("DROP TABLE T");

((resultset !== null) && (resultset !== undefined));