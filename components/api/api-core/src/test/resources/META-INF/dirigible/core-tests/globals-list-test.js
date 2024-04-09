
var globals = require('core/globals');
var assertTrue = require('test/assert').assertTrue;

var result = globals.list();

assertTrue(result !== undefined && result !== null, "Result of globals.list() is undefined or null");
