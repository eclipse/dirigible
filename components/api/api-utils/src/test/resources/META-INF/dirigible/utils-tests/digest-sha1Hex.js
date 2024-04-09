
var digest = require('utils/digest');
var assertEquals = require('test/assert').assertEquals;

var input = [61, 62, 63];
var result = digest.sha1Hex(input);

console.log(result);

assertEquals(result, '3b543c8b5ddc61fe39de1e5a3aece34082b12777');
