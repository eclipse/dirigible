var context = require('core/v3/context');

context.set('name1', 'value1');
var result = context.get('name1');

result === 'value1';
