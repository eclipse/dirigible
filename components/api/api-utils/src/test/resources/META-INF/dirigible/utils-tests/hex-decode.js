
var hex = require('utils/hex');
var assertTrue = require('test/assert').assertTrue;

var input = '414243';
var result = hex.decode(input);

console.log('decoded: ' + result);

assertTrue(result[0] === 65 &&
result[1] === 66 &&
result[2] === 67)
