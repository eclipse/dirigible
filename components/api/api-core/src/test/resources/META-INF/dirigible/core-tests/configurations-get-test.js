
var configurations = require('core/configurations');
var assertEquals = require('test/assert').assertEquals;

configurations.set('name1', 'value1');
var result = configurations.get('name1');

assertEquals(result, 'value1');
