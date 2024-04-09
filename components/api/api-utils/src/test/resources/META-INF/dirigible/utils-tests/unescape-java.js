
var escape = require('utils/escape');
var assertEquals = require('test/assert').assertEquals;

var input = 'java \\t characters \\n';
var result = escape.unescapeJava(input);

assertEquals(result, 'java \t characters \n');
