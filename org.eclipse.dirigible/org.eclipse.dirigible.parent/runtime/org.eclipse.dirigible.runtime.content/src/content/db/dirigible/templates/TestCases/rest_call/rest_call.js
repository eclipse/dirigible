/* globals $ */
/* eslint-env node, dirigible */

var http = require('net/http/client');
var response = require('net/http/response');

var options = {
    method: 'GET', // default
    host: '<service_endpoint_host>',
    port: 80,
    path: '<service_endpoint_path>',
    binary: false 
};

var httpResponse = http.request(options);

response.println(httpResponse.statusMessage);
response.println(httpResponse.data);
response.flush();
response.close();

