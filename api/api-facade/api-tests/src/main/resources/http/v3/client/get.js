var client = require('http/v3/client');

var result = client.get('https://raw.githubusercontent.com/eclipse/dirigible/master/NOTICE.txt');

console.log(JSON.stringify(result));

((result !== null) && (result !== undefined));
