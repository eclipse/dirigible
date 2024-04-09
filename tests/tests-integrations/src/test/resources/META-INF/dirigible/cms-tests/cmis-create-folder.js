
var cmis = require('cms/cmis');
var assertTrue = require('test/assert').assertTrue;

var session = cmis.getSession();

var rootFolder = session.getRootFolder();

var properties = {};
properties[cmis.OBJECT_TYPE_ID] = cmis.OBJECT_TYPE_FOLDER;
properties[cmis.NAME] = 'test1';
var result = rootFolder.createFolder(properties);

assertTrue(result !== null && result !== undefined);
