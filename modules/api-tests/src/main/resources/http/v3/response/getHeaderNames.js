/* eslint-env node, dirigible */

var response = require('http/v3/response');

JSON.stringify(response.getHeaderNames()) === '["header1","header2"]';

