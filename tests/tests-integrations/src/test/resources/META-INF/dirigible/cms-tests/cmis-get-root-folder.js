
var cmis = require('cms/cmis');
var assertTrue = require('test/assert').assertTrue;

var session = cmis.getSession();

var result = session.getRootFolder();

assertTrue(result !== null && result !== undefined);
