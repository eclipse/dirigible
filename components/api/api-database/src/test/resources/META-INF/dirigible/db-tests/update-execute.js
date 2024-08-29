import { Query as query } from 'sdk/db/query';
import { Update as update } from 'sdk/db/update';
import { Assert } from 'test/assert';

update.execute("CREATE TABLE U (A INT, B VARCHAR(10))");
update.execute("INSERT INTO U VALUES (1, 'ABC')");
update.execute("INSERT INTO U VALUES (2, 'DEF')");

var sql = "SELECT COUNT(*) AS C FROM U";
var resultset = query.execute(sql);

console.log(JSON.stringify(resultset));

update.execute("DROP TABLE U");

Assert.assertTrue(((resultset !== null) && (resultset !== undefined)));