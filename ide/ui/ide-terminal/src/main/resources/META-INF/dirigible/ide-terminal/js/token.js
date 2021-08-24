var response = require("http/v4/response");

response.println('{"token": ""}');
response.flush();
response.close();
