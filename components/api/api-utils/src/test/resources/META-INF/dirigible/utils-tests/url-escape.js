
var url = require('utils/url');
var assertEquals = require('test/assert').assertEquals;

var input = 'http://www.test.com?var1=abc123&var2=123 456&var3=стойност';
var result = url.escape(input);
console.log(result);
assertEquals(result, 'http://www.test.com?var1=abc123&var2=123%20456&var3=%D1%81%D1%82%D0%BE%D0%B9%D0%BD%D0%BE%D1%81%D1%82');
