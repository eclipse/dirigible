
var digest = require('utils/digest');
var assertTrue = require('test/assert').assertTrue;

var input = [41, 42, 43];
var result = digest.md5(input);

console.log(JSON.stringify(result));

assertTrue(result.length === 16 && result[0] === -15);
