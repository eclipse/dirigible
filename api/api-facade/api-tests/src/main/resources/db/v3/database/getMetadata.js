var database = require('db/v3/database');

var metadata = database.getMetadata();

console.log(JSON.stringify(metadata));

((metadata !== null) && (metadata !== undefined));