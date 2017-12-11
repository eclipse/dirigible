var cmis = require('cms/v3/cmis');

var session = cmis.getSession();

var result = session.getRootFolder();

result !== null && result !== undefined;
