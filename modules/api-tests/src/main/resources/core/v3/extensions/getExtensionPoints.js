/* eslint-env node, dirigible */

var extensions = require('core/v3/extensions');

var result = extensions.getExtensionPoints();

result !== undefined && result !== null;
