var database = require('db/v3/database');

var datasources = database.getDataSources();

console.log(JSON.stringify(datasources));

((datasources !== null) && (datasources !== undefined));