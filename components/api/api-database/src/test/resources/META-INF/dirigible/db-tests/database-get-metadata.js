
var database = require('db/database');
var assertTrue = require('test/assert').assertTrue;

var metadata = database.getMetadata();

console.log(JSON.stringify(metadata));

assertTrue((metadata !== null) && (metadata !== undefined));