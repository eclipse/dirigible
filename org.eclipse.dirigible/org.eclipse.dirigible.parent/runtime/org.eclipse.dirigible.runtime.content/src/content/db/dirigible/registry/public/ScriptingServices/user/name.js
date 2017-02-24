/* eslint-env node, dirigible */

var user = require('net/http/user');
var response = require("net/http/response");

response.setContentType("text/plain");
response.println(user.getName());
response.flush();
response.close();
