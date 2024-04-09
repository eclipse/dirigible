
var cmis = require('cms/cmis');
var assertTrue = require('test/assert').assertTrue;

var session = cmis.getSession();

var rootFolder = session.getRootFolder();

var result = rootFolder.getChildren();

assertTrue(result !== null && result !== undefined);
