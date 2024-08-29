
import { Database as database } from 'sdk/db/database';
import { Assert } from 'test/assert';

var metadata = database.getMetadata();

console.log(JSON.stringify(metadata));

Assert.assertTrue((metadata !== null) && (metadata !== undefined));