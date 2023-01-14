let response = require("http/response");
response.println('{"token": ""}');
response.flush();
response.close();