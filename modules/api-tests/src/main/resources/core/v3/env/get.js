/* eslint-env node, dirigible */

var env = require('core/v3/env');

var result = env.get('SHELL');

result !== undefined && result !== null;
