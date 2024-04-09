
var context = require('core/context');
var assertEquals = require('test/assert').assertEquals;

context.set('name1', 'value1');
var result = context.get('name1');

assertEquals(result, 'value1');
