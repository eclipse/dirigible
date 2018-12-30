var uuid = require('utils/v3/uuid');

var generated = uuid.random();

console.log(generated);

uuid.validate(generated);


