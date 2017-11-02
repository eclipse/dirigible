/* eslint-env node, dirigible */

var _java = require('core/v3/java');

var arrayList = _java.instantiate('java.util.ArrayList', []);

_java.invoke(arrayList.uuid, 'add', [null]);

var result = _java.invoke(arrayList.uuid, 'get', [0]);

console.log('>>>>>>>>>>> ' + result);

result == null;

true;
