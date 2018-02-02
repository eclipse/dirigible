/* eslint-env node, dirigible */

var java = require('core/v3/java');

var uuid = java.call('org.eclipse.dirigible.api.v3.utils.UuidFacade', 'random', []);

uuid !== null;
