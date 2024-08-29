import { Query as query } from 'sdk/db/query';
import { Update as update } from 'sdk/db/update';
import { Assert } from 'test/assert';

update.execute("CREATE TABLE Q (A INT, B VARCHAR(10))");
update.execute("INSERT INTO Q VALUES (1, 'ABC')");
update.execute("INSERT INTO Q VALUES (2, 'DEF')");

var sql = "SELECT * FROM Q WHERE A = ?";
var resultset = query.execute(sql, [1]);

console.log(JSON.stringify(resultset));

update.execute("DROP TABLE Q");

Assert.assertTrue((resultset !== null) && (resultset !== undefined));
