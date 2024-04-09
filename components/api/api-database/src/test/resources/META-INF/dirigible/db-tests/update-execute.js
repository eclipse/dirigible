
var query = require('db/query');
var update = require('db/update');
var assertTrue = require('test/assert').assertTrue;

update.execute("CREATE TABLE U (A INT, B VARCHAR(10))");
update.execute("INSERT INTO U VALUES (1, 'ABC')");
update.execute("INSERT INTO U VALUES (2, 'DEF')");

var sql = "SELECT COUNT(*) AS C FROM U";
var resultset = query.execute(sql);

console.log(JSON.stringify(resultset));

update.execute("DROP TABLE U");

assertTrue(((resultset !== null) && (resultset !== undefined)));