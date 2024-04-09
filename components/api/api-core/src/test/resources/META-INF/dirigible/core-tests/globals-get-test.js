
var globals = require('core/globals');
var assertEquals = require('test/assert').assertEquals;

globals.set("name1", "value1");
var result = globals.get('name1');

assertEquals(result, 'value1');