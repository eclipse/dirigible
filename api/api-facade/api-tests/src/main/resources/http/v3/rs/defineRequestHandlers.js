const LOGGER = require('log/logging').getLogger('http.rs.tests');

var rs = require('http/v3/rs').get();

rs.addResourceHandler("", "get", function(ctx, io){
	io.response.println('OK');
}, undefined, undefined, function(){
	LOGGER.info('Handling resource with GET');
})
rs.addResourceHandler("", "put", function(ctx, io){
	io.response.println('OK');
}, undefined, undefined, function(){
	LOGGER.info('Handling resource with PUT');
})
rs.addResourceHandler("", "post", function(ctx, io){
	io.response.println('OK');
}, undefined, undefined, function(){
	LOGGER.info('Handling resource with POST');
})
rs.addResourceHandler("", "delete", function(ctx, io){
	io.response.println('OK');
}, undefined, undefined, function(){
	LOGGER.info('Handling resource with DELETE');
})
rs.addResourceHandler("a/{param1}/{param2}", "get", function(ctx, io){
	LOGGER.info('Handling resource with GET and parameterized path');
	io.response.println('OK');
}, undefined, undefined, function(){
	LOGGER.info('Handling resource with GET and parameterized path');
})
.service();