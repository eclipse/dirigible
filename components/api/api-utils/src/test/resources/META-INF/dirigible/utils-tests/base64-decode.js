
var base64 = require('utils/base64');
var assertTrue = require('test/assert').assertTrue;

var input = 'PT4/';
var result = base64.decode(input);

console.log('decoded: ' + result);

assertTrue(result[0] === 61 && result[1] === 62 && result[2] === 63);
