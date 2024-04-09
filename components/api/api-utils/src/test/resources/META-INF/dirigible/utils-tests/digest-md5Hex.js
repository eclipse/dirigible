
var digest = require('utils/digest');
var assertEquals = require('test/assert').assertEquals;

var input = 'ABC';
var result = digest.md5Hex(input);

console.log(result);

assertEquals(result, '902fbdd2b1df0c4f70b4a5d23525e932');
