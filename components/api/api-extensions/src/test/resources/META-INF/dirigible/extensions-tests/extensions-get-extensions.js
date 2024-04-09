
var extensions = require('extensions/extensions');
var assertEquals = require('test/assert').assertEquals;

var result = extensions.getExtensions('test_extpoint1');

assertEquals(result[0], "/test_ext_module1");
