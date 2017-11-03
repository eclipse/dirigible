/* eslint-env node, dirigible */

var query = require('db/v3/query');
var response = require('http/v3/response');


var sql = 'select * from DIRIGIBLE_EXTENSIONS where EXTENSION_EXTENSIONPOINT_NAME = ?';

var resultset = query.execute(sql, ['ide-template']);

response.println(JSON.stringify(resultset));

response.flush();
response.close();
