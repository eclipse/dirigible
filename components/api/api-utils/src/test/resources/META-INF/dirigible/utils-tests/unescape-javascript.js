
var escape = require('utils/escape');
var assertEquals = require('test/assert').assertEquals;

var input = 'javascript \\t characters \\n';
var result = escape.unescapeJavascript(input);

assertEquals(result, 'javascript \t characters \n');
