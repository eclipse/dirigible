var assert = require('assert');

assert.assertTrue('invalid true', true);
assert.assertEquals('invalid equal', 'a', 'a');
assert.assertNull('invalid null', null);
assert.assertNotNull('invalid not null', 'a');
//assert.fail('just fail');

response.getWriter().println('OK');

