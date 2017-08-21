/* eslint-env node, dirigible */

var database = require('db/v3/database');

database.update("CREATE TABLE T (A INT, B VARCHAR(10))");
database.update("INSERT INTO T VALUES (1, 'ABC')");
database.update("INSERT INTO T VALUES (2, 'DEF')");

var sql = "SELECT COUNT(*) AS C FROM T";
var resultset = database.query(sql);

console.log(JSON.stringify(resultset));

database.update("DROP TABLE T");

((resultset !== null) && (resultset !== undefined));