/* globals $ */
/* eslint-env node, dirigible */

var response = require('net/http/response');

response.setContentType("text/html; charset=UTF-8");
response.setCharacterEncoding("UTF-8");
response.println("Hello World!");
response.flush();
response.close();
