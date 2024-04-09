
var client = require('http/client');
var assertTrue = require('test/assert').assertTrue;

var result = client.get('https://raw.githubusercontent.com/eclipse/dirigible/master/NOTICE.txt', {'binary': true});

console.log(JSON.stringify(result));

assertTrue((result !== null) && (result !== undefined));
