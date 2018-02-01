var cmis = require('cms/v3/cmis');

var session = cmis.getSession();

var rootFolder = session.getRootFolder();

var result = rootFolder.getChildren();

result !== null && result !== undefined;
