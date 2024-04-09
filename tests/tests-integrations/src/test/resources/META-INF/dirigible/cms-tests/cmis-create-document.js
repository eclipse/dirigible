
var cmis = require('cms/cmis');
var streams = require('io/streams');
var assertTrue = require('test/assert').assertTrue;

var session = cmis.getSession();

var rootFolder = session.getRootFolder();

var inputStream = streams.createByteArrayInputStream([101,102,103,104]);
var contentStream = session.getObjectFactory().createContentStream('test1.txt', 4, 'text/plain', inputStream);
var properties = {};
properties[cmis.OBJECT_TYPE_ID] = cmis.OBJECT_TYPE_DOCUMENT;
properties[cmis.NAME] = 'test1.txt';

var result = rootFolder.createDocument(properties, contentStream, cmis.VERSIONING_STATE_MAJOR);

assertTrue(result !== null && result !== undefined);
