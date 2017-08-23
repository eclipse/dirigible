/* eslint-env node, dirigible */

var extensions = require('core/v3/extensions');

var result = extensions.getExtensions('test_extpoint1');

result[0] == "/test_ext_module1";
