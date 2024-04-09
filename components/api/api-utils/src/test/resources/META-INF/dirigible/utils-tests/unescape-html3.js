
var escape = require('utils/escape');
var assertEquals = require('test/assert').assertEquals;

var input = '&quot;&lt;&gt;';
var result = escape.unescapeHtml3(input);

assertEquals(result, '"<>');
