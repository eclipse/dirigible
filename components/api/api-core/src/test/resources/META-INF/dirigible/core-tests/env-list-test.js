
var env = require('core/env');
var assertTrue = require('test/assert').assertTrue;

var result = env.list();

assertTrue(result !== undefined && result !== null);
