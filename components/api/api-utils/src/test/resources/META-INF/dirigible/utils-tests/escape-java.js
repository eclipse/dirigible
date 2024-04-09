
var escape = require('utils/escape');
var assertEquals = require('test/assert').assertEquals;

var input = 'java \t characters \n';
var result = escape.escapeJava(input);

assertEquals(result, 'java \\t characters \\n');
