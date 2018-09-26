var response = require('http/v3/response');
response.setContentType("application/json");
response.println(JSON.stringify({
	"username": require('security/v3/user').getName()
}, null, 2));
response.flush();
response.close();