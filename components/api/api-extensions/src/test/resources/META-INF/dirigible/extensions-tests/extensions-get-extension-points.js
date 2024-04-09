
var extensions = require('extensions/extensions');
var assertEquals = require('test/assert').assertEquals;

var result = extensions.getExtensionPoints();

assertEquals(result[0], "test_extpoint1");
