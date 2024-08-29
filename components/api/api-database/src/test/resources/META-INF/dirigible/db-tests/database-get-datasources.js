
import { Database as database } from 'sdk/db/database';
import { Assert } from 'test/assert';

var datasources = database.getDataSources();

console.log(JSON.stringify(datasources));

Assert.assertTrue(((datasources !== null) && (datasources !== undefined)));