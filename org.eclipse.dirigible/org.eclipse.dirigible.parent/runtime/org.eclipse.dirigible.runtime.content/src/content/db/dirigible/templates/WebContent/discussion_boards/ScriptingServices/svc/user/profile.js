/* globals $ */
/* eslint-env node, dirigible */
"use strict";
var session = require('net/http/session');
var HttpController = require('arestme/http').HttpController;
new HttpController()
.addResourceHandler("logout", "get", 
	function(context, io){
		session.invalidate();
		io.response.setStatus(io.response.OK);
	}).service();
