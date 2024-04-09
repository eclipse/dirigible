
var digest = require('utils/digest');
var assertTrue = require('test/assert').assertTrue;

var input = [41, 42, 43];
var result = digest.sha512(input);

console.log(JSON.stringify(result));

assertTrue(result.length === 64 && result[0] === 123);
