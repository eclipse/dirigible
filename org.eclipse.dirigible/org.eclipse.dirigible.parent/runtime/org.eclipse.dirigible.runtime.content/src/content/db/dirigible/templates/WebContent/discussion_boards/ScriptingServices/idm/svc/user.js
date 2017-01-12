/* globals $ */
/* eslint-env node, dirigible */
(function(){
"use strict";
	
	var arester = require("arestme/arester");
	var userDAO = require("idm/lib/user_dao");
	
	var User = arester.asRestAPI(userDAO);
	User.prototype.logger.ctx = "User Svc";
	
	User.prototype.cfg[""].get = {
		handler: function(context, io){
			var self = this;
			var offset = context.queryParams.offset || 0;
			var limit = context.queryParams.limit || 100;
			var sort = context.queryParams.sort;
			var order = context.queryParams.order;			
			var expanded = context.queryParams.expanded;
			var username = context.queryParams.username;
			
		    try{
				var entities = this.dao.list.apply(self, [limit, offset, sort, order, expanded, username]);
		        var jsonResponse = JSON.stringify(entities, null, 2);
		    	io.response.println(jsonResponse);
			} catch(e) {
	    	    var errorCode = io.response.INTERNAL_SERVER_ERROR ;
	    	    self.logger.error(errorCode, e.message, e.errContext);
	        	self.sendError(io, errorCode, errorCode, e.message, e.errContext);
	        	throw e;
			}			
		}
	};
			
	function unescapePath(path){
		return path.replace(/\\/g, '');
	}
	
	User.prototype.cfg["$current"] = {
		get: {
			handler: function(context, io){
				var self = this;
			    try{
			    	var userLib = require('net/http/user');
			    	
			    	var documentPath = unescapePath(userLib.getName());
			    	try{ 							
						var documentLib = require('docs_explorer/lib/document_lib');
						var document = documentLib.getDocument(documentPath);
						if(!document.getName())
							documentPath = undefined;
					} catch(docerr){
						documentPath = undefined;						
					}
					var avatarurl = documentPath?'/services/js/idm/svc/user.js/$pics/'+ documentPath:undefined;
			    	
			    	var currentUser = {
			    		uname: userLib.getName(),
			    		avatarUrl: avatarurl
			    	};
			    
			        var jsonResponse = JSON.stringify(currentUser, null, 2);
			    	io.response.println(jsonResponse);
				} catch(e) {
		    	    var errorCode = io.response.INTERNAL_SERVER_ERROR ;
		    	    self.logger.error(errorCode, e.message, e.errContext);
		        	self.sendError(io, errorCode, errorCode, e.message, e.errContext);
		        	throw e;
				}			
			}
		}
	};
	
	User.prototype.cfg["$pics/{userid}"] = {		
		get: {
			handler: function(context, io){
				var self = this;
				try{
					var userid = context.pathParams.userid;
					var documentPath = '/'+userid;
					if (documentPath)
						documentPath = unescapePath(documentPath);
					var documentLib = require('docs_explorer/lib/document_lib');
					var document = documentLib.getDocument(documentPath);
					var contentStream = documentLib.getDocumentStream(document);
					var contentType = contentStream.getInternalObject().getMimeType();
					io.response.setContentType(contentType);
					io.response.writeStream(contentStream.getStream());
				} catch(e) {
		    	    var errorCode = io.response.INTERNAL_SERVER_ERROR ;
		    	    self.logger.error(errorCode, e.message, e.errContext);
		        	self.sendError(io, errorCode, errorCode, e.message, e.errContext);
		        	throw e;
				}					
			}
		},
		post: {
			handler: function(context, io){
				var self = this;
				var userid = context.pathParams.userid;
				var userLib = require('net/http/user');
				if(!userLib.isInRole('owner') && userLib.getName() !== userid){
 					self.logger.error('403', 'Unauthorized for access');
		        	self.sendError(io, 403, 403, "Unauthorized");
		        	return;
				}

				var documentLib = require('docs_explorer/lib/document_lib');
				var folderLib = require('docs_explorer/lib/folder_lib');
			    try{
	
					var upload = require('net/http/upload');
					var result = [];
					if (upload.isMultipartContent()) {
						var documents = upload.parseRequest();
						if(documents && documents.length){
							var folder = folderLib.getFolder('/');
							documents[0].name = userid;
							result.push(documentLib.uploadDocument(folder, documents[0]));
						}
					}
			        var jsonResponse = JSON.stringify(result , null, 2);
			    	io.response.println(jsonResponse);
				} catch(e) {
		    	    var errorCode = io.response.INTERNAL_SERVER_ERROR ;
		    	    self.logger.error(errorCode, e.message, e.errContext);
		        	self.sendError(io, errorCode, errorCode, e.message, e.errContext);
		        	throw e;
				}			
			}
		}		
	};

	var user = new User(userDAO);
	
	var request = require("net/http/request");
	var response = require("net/http/response");
		
	user.service(request, response);
		
})();
