var uuid = require('utils/modules').getUuid();

var generated = uuid.random();

console.log(generated);

uuid.validate(generated);


