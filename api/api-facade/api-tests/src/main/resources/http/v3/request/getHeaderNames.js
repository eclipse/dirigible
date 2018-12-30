var request = require('http/v3/request');

JSON.stringify(request.getHeaderNames()) === '["header1","header2"]';

