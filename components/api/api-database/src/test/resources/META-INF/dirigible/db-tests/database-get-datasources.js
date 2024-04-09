
var database = require('db/database');
var assertTrue = require('test/assert').assertTrue;

var datasources = database.getDataSources();

console.log(JSON.stringify(datasources));

assertTrue(((datasources !== null) && (datasources !== undefined)));