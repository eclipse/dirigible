var session = require('http/v3/session');

session.setAttribute('attr1', 'value1');

JSON.stringify(session.getAttributeNames()) === '["attr1"]';

