var httpClient = require('http/v3/client');
var response = require('http/v3/response');

var httpResponse = httpClient.get('http://services.odata.org/V4/Northwind/Northwind.svc/');

response.println(httpResponse.text);
response.flush();
response.close();