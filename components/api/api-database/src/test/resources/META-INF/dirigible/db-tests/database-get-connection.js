
import { Database as database } from 'sdk/db/database';
import { Update as update } from 'sdk/db/update';
import { Assert } from 'test/assert';

update.execute("CREATE TABLE T (A INT, B VARCHAR(10))");
update.execute("INSERT INTO T VALUES (1, 'ABC')");
update.execute("INSERT INTO T VALUES (2, 'DEF')");

var sql = "SELECT * FROM T WHERE A = ?";

var value;
var connection = database.getConnection();
try {
	var statement = connection.prepareStatement(sql);
	try {
		statement.setInt(1, 2);
		var resultset = statement.executeQuery();
		try {
			while (resultset.next()) {
				var value = resultset.getString('B');
				console.log('B: ' + value);
			}
		} finally {
			resultset.close();
		}
	} finally {
		statement.close();
	}
} finally {
	connection.close();
}

update.execute("DROP TABLE T");

Assert.assertTrue(value == 'DEF');