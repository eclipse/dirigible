var assertLib = require('assert');
var ioLib = require('io');

var httpClient = http.createHttpClient();
var httpGet = http.createGet('<input the target URL of the service here>');

var answer = httpClient.execute(httpGet);
// sample response:  {"field1":"test field"}
var entity = answer.getEntity();
var content;
var reply;
var toBeChecked;
try {
    content = ioLib.read(entity.getContent());
    reply = JSON.parse(content);
    toBeChecked = reply.field1;
    http.consume(entity);
} finally {
    entity.releaseConnection();
}

assertLib.assertNotNull('reply is null', reply);
assertLib.assertNotNull('reply.field1 is null', reply.field1);
assertLib.assertEquals('check field is not correct', toBeChecked, 'test field');

