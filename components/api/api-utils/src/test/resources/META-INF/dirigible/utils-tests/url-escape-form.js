
var url = require('utils/url');
var assertEquals = require('test/assert').assertEquals;

var input = 'http://www.test.com?var1=abc123&var2=123 456&var3=стойност';
var result = url.escapeForm(input);
console.log(result);
assertEquals(result, 'http%3A%2F%2Fwww.test.com%3Fvar1%3Dabc123%26var2%3D123+456%26var3%3D%D1%81%D1%82%D0%BE%D0%B9%D0%BD%D0%BE%D1%81%D1%82');
