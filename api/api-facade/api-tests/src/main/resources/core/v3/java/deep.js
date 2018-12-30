var _java = require('core/v3/java');

var process = _java.instantiate('org.eclipse.dirigible.api.v3.test.Process', []);

console.log('Process: ' + process.uuid);

_java.invoke(process.uuid, 'setName', ['process1']);

var task = _java.invoke(process.uuid, 'createTask', ['task1'], true);

console.log('Task: ' + task.uuid);

var result = _java.invoke(task.uuid, 'getName', []);

console.log('Task.name: ' + result);

var exists = _java.invoke(process.uuid, 'existsTask', [task.uuid]);

console.log('Task exists?: ' + exists);

result == 'task1';
