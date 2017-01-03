/* globals $ */
/* eslint-env node, dirigible */

var response = require('net/http/response');

var message = 'Hello World!';

// Print in the system output
console.info(message);

response.setContentType('text/html; charset=UTF-8');
response.setCharacterEncoding('UTF-8');

// Print in the response
response.println(message);

response.flush();
response.close();
