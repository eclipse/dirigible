/* eslint-env node, dirigible */

var extensions = require('core/v3/extensions');

var result = extensions.getExtensions('ide-menu');

result !== undefined && result !== null;
