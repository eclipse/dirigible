/* globals $ */
/* eslint-env node, dirigible */
(function(){
"use strict";

	var xss = require("utils/xss"); 

	var $log = function(){
		this.ctx = 'Service',
		this.error = function(errCode, errMessage, errContext){
			var ctxSegment = this.ctx!==undefined?'['+this.ctx+']: ':'';
			var errCodeSegment = errCode!==undefined?'['+errCode+']: ':'';
			console.error(ctxSegment + errCodeSegment + errMessage);
		    if (errContext !== undefined && errContext !== null) {
		    	console.error(JSON.stringify(errContext));
		    }
		};
		this.info = function(message){
			var ctxSegment = this.ctx!==undefined?'['+this.ctx+']: ':'';
			console.info(ctxSegment + message);
		};
		return this;
	};
	
	function RestApi(){
		var self = this;
		
		var parseIntStrict = function (value) {
		  if(/^(\-|\+)?([0-9]+|Infinity)$/.test(value))
		    return Number(value);
		  return NaN;
		};
		
		this.logger = new $log();

		this.sendError = function(io, httpCode, errCode, errMessage, errContext, contentType) {
			if(['text/html'].indexOf(contentType)){
				io.response.sendError(httpCode, errMessage);	
			} else {
			    var body = {'err': {'code': errCode, 'message': errMessage}};
			   	io.response.setHeader("Content-Type", (contentType || "application/json"));
			    io.response.print(body);
			}		    
		};
	
		var create = function(context, io){
			var input = io.request.readInputText();
		    var entity = JSON.parse(input);
		    if(this.onEntityInsert){
		    	this.onEntityInsert(entity);
		    }
		    try{
				entity[this.dao.getPrimaryKey()] = this.dao.insert(entity, context.queryParams.cascaded);
				io.response.setStatus(io.response.OK);
				io.response.setHeader('Location', $.getRequest().getRequestURL().toString() + '/' + entity[this.dao.getPrimaryKey()]);
			} catch(e) {
	    	    var errorCode = io.response.INTERNAL_SERVER_ERROR;
	    	    self.logger.error(errorCode, e.message, e.errContext);
	        	self.sendError(io, errorCode, errorCode, e.message, e.errContext);
	        	throw e;
			}		
		};
		
		var remove = function(context, io){
			var id = context.pathParams.id;
			var cascaded = context.queryParams.cascaded;			
		 	try{
				this.dao.remove(id, cascaded);
				io.response.setStatus(io.response.NO_CONTENT);
			} catch(e) {
	    	    var errorCode = io.response.INTERNAL_SERVER_ERROR;
	    	    self.logger.error(errorCode, e.message, e.errContext);
	        	self.sendError(io, errorCode, errorCode, e.message, e.errContext);
	        	throw e;
			}
		};
		
		var update = function(context, io){
			var id = context.pathParams.id;
			var cascaded = context.queryParams.cascaded;
			var input = io.request.readInputText();
		    var item = JSON.parse(input);
		    //check for potential mismatch in path id and id in input
		    if(this.onEntityUpdate){
		    	this.onEntityUpdate(item);
		    }
		    try{
				item[this.dao.getPrimaryKey()] = this.dao.update(item, cascaded);
				io.response.setStatus(io.response.NO_CONTENT);
			} catch(e) {
	    	    var errorCode = io.response.INTERNAL_SERVER_ERROR ;
	    	    self.logger.error(errorCode, e.message, e.errContext);
	        	self.sendError(io, errorCode, errorCode, e.message, e.errContext);
	        	throw e;
			}
		};
		
		var get = function(context, io){
			var id = context.pathParams.id;
			var expanded = context.queryParams.expanded;
			var select = context.queryParams.select;
			if(!select && expanded !== undefined && expanded){
				if(this.dao.associations || this.dao.orm.associationSets)
					select = Object.keys(this.dao.associations).join(',');
				else
					select = expanded;//old style daos
			}
			if(select){
				select = String(new java.lang.String(""+select));
				select = select.split(',').map(function(sel){
					return sel.trim();
				});
			}
			//id is mandatory parameter and an integer
			if(id === undefined || isNaN(parseIntStrict(id))) {
				self.sendError(io, io.response.BAD_REQUEST, 1, "Invallid id parameter: " + id);
				return;
			}
	
		    try{
				var item = this.dao.find.apply(this.dao.ctx || this.dao, [id, select]);
				if(!item){
					self.logger.error(1, "Record with id: " + id + " does not exist.");
	        		self.sendError(io, io.response.NOT_FOUND, 1, "Record with id: " + id + " does not exist.");
	        		return;
				}
				var jsonResponse = JSON.stringify(item, null, 2);
				io.response.setContentType("application/json; charset=UTF-8");//TODO: read this from context as defined in config
		        io.response.println(jsonResponse);
			} catch(e) {
	    	    var errorCode = io.response.INTERNAL_SERVER_ERROR ;
				self.logger.error(errorCode, e.message, e.errContext);		    	    
	        	self.sendError(io, errorCode, errorCode, e.message, e.errContext);
	        	throw e;
			}	
		};
		
		var validateQueryInputs = function(context, io){
			var offset = context.queryParams.offset;
			if (offset === undefined || offset === null) {
				context.queryParams.offset = 0;
			} else if(isNaN(parseIntStrict(offset)) || offset < 0) {
				self.logger.error(1, "Invallid offset parameter: " + offset + ". Must be a positive integer.");				
				self.sendError(io, io.response.BAD_REQUEST, 1, "Invallid offset parameter: " + offset + ". Must be a positive integer.");
				context.err = {
					httpCode: io.response.BAD_REQUEST, 
					errCode: 1, 
					message: "Invallid offset parameter: " + offset + ". Must be a positive integer."
				};
			}
			
			var limit = context.queryParams.limit;
			if (limit === undefined || limit === null) {
				context.queryParams.limit = 10;
			}  else if(isNaN(parseIntStrict(limit)) || limit < 0) {
				self.logger.error(1, "Invallid limit parameter: " + limit + ". Must be a positive integer.");
				self.sendError(io, io.response.BAD_REQUEST, 1, "Invallid limit parameter: " + limit + ". Must be a positive integer.");
				context.err = {
					httpCode: io.response.BAD_REQUEST, 
					errCode: 1, 
					message: "Invallid offset parameter: " + offset + ". Must be a positive integer."
				};
			}

			var sort = context.queryParams.sort || null;			
			if( sort !== null && this.validSortPropertyNames && this.validSortPropertyNames.indexOf(sort)<0){
				self.sendError(io, io.response.BAD_REQUEST, 1, "Invalid sort by property name: " + sort);
				context.err = {
					httpCode: io.response.BAD_REQUEST, 
					errCode: 1, 
					message: "Invalid sort by property name: " + sort
				};
			}
			var order = context.queryParams.order || null;
			if(order!==null){
				if(sort === null){
					self.sendError(io, io.response.BAD_REQUEST, 1, "Parameter order is invalid without paramter sort to order by.");
					context.err = {
						httpCode: io.response.BAD_REQUEST, 
						errCode: 1, 
						message: "Parameter order is invalid without paramter sort to order by."
					};
				} else if(['asc', 'desc'].indexOf(order.trim().toLowerCase())<0){
					self.sendError(io.response.BAD_REQUEST, 1, "Invallid order parameter: " + order + ". Must be either ASC or DESC.");
					context.err = {
						httpCode: io.response.BAD_REQUEST, 
						errCode: 1, 
						message: "Invallid order parameter: " + order + ". Must be either ASC or DESC."
					};
				}
			} else if(sort !== null){
				context.queryParams.order = 'asc';
			}
		};
		
		var query = function(context, io){
			var offset = context.queryParams.offset;
			var limit = context.queryParams.limit;
			var sort = context.queryParams.sort;
			var order = context.queryParams.order;			
			var id = context.pathParams.id;

			var expanded = context.queryParams.expanded;
			var select = context.queryParams.select;
			if(!select && expanded !== undefined && expanded){
				if(this.dao.associations || this.dao.associationsSets)
					select = Object.keys(this.dao.associations || this.dao.associationSets).join(',');
				else {
					select = expanded;//old style daos
				}
			}
			var args = [];
			if(this.dao.associations || this.dao.orm.associationSets){
				args = [context.queryParams];
//				args[0].id = id;
			} else {
				args = Array.prototype.slice.call(arguments);
				if(args.length>5){
					args = [limit, offset, sort, order, expanded].concat(args.slice(args.length-1));
				} else {
					args = [limit, offset, sort, order, expanded];
				}
			}

		    try{
				var entities = this.dao.list.apply(this.dao.ctx || this.dao, args);
		        var jsonResponse = JSON.stringify(entities, null, 2);
		    	io.response.println(jsonResponse);
			} catch(e) {
	    	    var errorCode = io.response.INTERNAL_SERVER_ERROR ;
	    	    self.logger.error(errorCode, e.message, e.errContext || e.stack);
	        	self.sendError(io, errorCode, errorCode, e.message, e.errContext);
	        	throw e;
			}		
		};	
		
		var count = function(context, io){
		    try{
				var itemsCount = this.dao.count();
	/*			this.response.setHeader("Content-Type", "text/plain");*/			
				io.response.setHeader("Content-Type", "application/json");//TMP to accommodate the ui which handles only json
	/*	    	this.response.println(itemsCount);      	 */
		    	io.response.println('{"count":'+itemsCount+'}'); 
			} catch(e) {
	    	    var errorCode = io.response.INTERNAL_SERVER_ERROR ;
	    	    self.logger.error(errorCode, e.message, e.errContext);
	        	self.sendError(io, errorCode, errorCode, e.message, e.errContext);
	        	throw e;
			}
		};
		
		var metadata = function(context, io){
	 		try{
				var entityMetadata = this.dao.metadata();
				io.response.setHeader("Content-Type", "application/json");
				io.response.println(entityMetadata);
			} catch(e) {
	    	    var errorCode = io.response.INTERNAL_SERVER_ERROR ;
	    	    self.logger.error(errorCode, e.message, e.errContext);
	        	self.sendError(io, errorCode, errorCode, e.message, e.errContext);
	        	throw e;        	
			}		
		};
		
		this.cfg = {
			"count": {
				"get": {
					produces: ['application/json'],
					handler: count
				}
			},
			"metadata": {
				"get": {
					produces: ['application/json'],
					handler: metadata
				}
			},
			"{id}": {
				"get": {
					produces: ['application/json'],
					handler: get	
				},
				"put": {
					consumes: ['application/json'],
					handler: update
				},
				"delete": {
					handler: remove
				}
			},
			"": {
				"get": {
					produces: ['application/json'],
					beforeHandle: validateQueryInputs,
					handler: query
				},
				"post": {
					consumes: ['application/json'],
					handler: create
				}
			}
		};
		
		//Associations handler
		this.associationListHandler = function(context, io){
		    try{
		    	var associationName = context.pathParams.associationName;
		    	if(!this.dao.orm.associationSets[associationName]){
			    	var errorCode = io.response.BAD_REQUEST;
				    this.logger.error(errorCode, 'Invalid asosciation set name requested: ' + associationName);
			    	this.sendError(io, errorCode, errorCode, 'Invalid asosciation set name requested: ' + associationName);
					return;
		    	}
 			    var args = context.queryParams;
		    	args[this.dao.getPrimaryKey().name] = context.pathParams.id;
				var expansionPath = [associationName];//Until handler can be installed for paths with multiple segments
				var associationSet = this.dao.expand.apply(this.dao, [expansionPath, context.pathParams.id]);
				io.response.setStatus(io.response.OK);
				io.response.println(JSON.stringify(associationSet, null, 2));
			} catch(e) {
			    var errorCode = io.response.INTERNAL_SERVER_ERROR;
			    this.logger.error(errorCode, e.message, e.errContext);
		    	this.sendError(io, errorCode, errorCode, e.message, e.errContext);
		    	throw e;
			}
		};

		function resourceHandlerLocator(requestPath, method, queryParams, cfg, io){	
			var pathDefs = Object.keys(cfg);
			var matches = [];
			for(var i=0; i<pathDefs.length; i++){
				var pathDef = pathDefs[i];
				var resolvedPath;
				if(pathDef === requestPath){
					resolvedPath = pathDef;
					matches.push({w:1, p: resolvedPath, d: pathDef});
				} else {
					var pathDefSegments = pathDef.split('/');
					var reqPathSegments = requestPath.split('/');
					if(pathDefSegments.length === reqPathSegments.length){							
						var verbHandlers = Object.keys(cfg[pathDef]);
						if(verbHandlers && verbHandlers.length>0 && verbHandlers.indexOf(method)>-1){
							var pathParams = {};
							var resolvedPathDefSegments = pathDefSegments.map(function(pSeg, i){
								pSeg = pSeg.trim();
								if(pSeg.indexOf('{') === 0 && pSeg.indexOf('}') === pSeg.length-1) {
									var param = pSeg.substring(pSeg.indexOf('{')+1, pSeg.indexOf('}'));
									pathParams[param] = reqPathSegments[i];
									return reqPathSegments[i];
								} else {
									return pSeg;
								}
							});
							var p = resolvedPathDefSegments.join('/');
							if(p === requestPath){
								resolvedPath = p;
								var match = {w:0, p: resolvedPath, d: pathDef};
								if(Object.keys(pathParams).length>0){
									match.pathParams = pathParams;
								}
								matches.push(match);
							}
						}
					}
				}
			}
			//sort matches by weight
			matches = matches.sort(function(p, n){
				if(n.w === p.w){
					//the one with less placeholders wins
					var placeholdersCount1 = p.d.split('/').filter(function(segment){
						return new java.lang.String(segment).startsWith('{');
					}).length;
					var placeholdersCount2 = n.d.split('/').filter(function(segment){
						return java.lang.String(segment).startsWith('{');
					}).length;
					if(placeholdersCount1 > placeholdersCount2){
						n.w = n.w+1;
					} else if(placeholdersCount1 < placeholdersCount2){
						p.w = p.w+1;
					}
				}
				return n.w - p.w;
			});
			if(matches[0]){
				var verbHandler = cfg[matches[0].d][method];//todo -> make array (one handler per media type)
				if(verbHandler){
					var acceptsMediaType = io.request.getHeader('Accept');
					if(acceptsMediaType){
						acceptsMediaType = acceptsMediaType.split(',');
						acceptsMediaType = acceptsMediaType.map(function(mime){
							return mime.replace('\\','');//remove escaping
						});
					}
					var contentType = io.request.getHeader('Content-Type');
					if(contentType){
						contentType = contentType.split(',');
						contentType = contentType.map(function(mime){
							return mime.replace('\\','');//remove escaping
						});						
					}					
					//find MIME types intersections
					var matchedProducedMIME;
					if(verbHandler.produces && acceptsMediaType){
						matchedProducedMIME = acceptsMediaType.filter(function(n) {
						    return verbHandler.produces.indexOf(n) != -1;
						});
					}
					var matchedConsumesMIME;
					if(verbHandler.consumes && contentType){
						matchedConsumesMIME = contentType.filter(function(n) {
						    return verbHandler.consumes.indexOf(n) != -1;
						});
					}
					if((acceptsMediaType && matchedProducedMIME) || (contentType && matchedConsumesMIME) || (!acceptsMediaType && !verbHandler.produces) || (!contentType && !verbHandler.consumes)
						|| (acceptsMediaType.indexOf('*/*')>-1)){
						var ctx = {
									"path": {
										"resolvedPath": matches[0].p
									},
									"pathParams": {},
									"queryParams": {}
								};
						if(matches[0].pathParams){
							ctx.pathParams = matches[0].pathParams;
						}
						ctx.queryParams = queryParams;
						if(verbHandler.beforeHandle && typeof verbHandler.beforeHandle === 'function')
							verbHandler.beforeHandle.apply(self, [ctx, io]);
						if(!ctx.err){
							verbHandler.handler.apply(self, [ctx, io]);
							return;
						}
					}
				}
			} 
			self.sendError(io, io.response.BAD_REQUEST, 1, null);
			return;
		}

	  	this.service = function(request, response){
			var method = request.getMethod().toLowerCase();
			var path = request.getAttribute("path") || "";
			var queryString = request.getInfo().queryString || "";
			queryString = xss.unescapeHtml(queryString).replace('amp;','');
			var queryStringSegments = queryString.split('&');
			var queryParams = {}; 
			if(queryStringSegments.length>0){
				for(var i=0; i< queryStringSegments.length; i++){
					var seg = queryStringSegments[i];
					var kv = seg.split('=');
					var key = kv[0].trim();
					var value = kv[1]===undefined?true:kv[1].trim();
					queryParams[key] = value;
				}
			}
			var requestUrl = $.getRequest().getRequestURI();
			var requestUrlSegments = requestUrl.split('/');
			var inResourcePath = false;
			requestUrlSegments = requestUrlSegments.filter(function(seg){
				if(seg.trim().lastIndexOf('.js')>-1){
					inResourcePath = true;
				}
				return inResourcePath;
			});
			var resourcePath = requestUrlSegments.join('/');
			this.logger.info('Servicing HTTP Verb['+ method.toUpperCase() +'] for resource['+resourcePath+'] with query parameters: ' + queryParams);

			resourceHandlerLocator.apply(self, [path, method, queryParams, self.cfg, {request: request, response:response}]);	  		
	  	};
	}	

	exports.asRestAPI = function(dao, target) {
	
		target = target || function(){};
		
		//inject the dao dependency if not already present
		if(dao && !target.prototype.dao)
			target.prototype.dao = dao;
					
		//mixin the rest api into target
		RestApi.call(target.prototype);		
		
		//setup configuration for handling associations
		if(target.prototype.dao.orm && target.prototype.dao.orm.associationSets && Object.keys(target.prototype.dao.orm.associationSets).length>0){
			var resourcePathTemplate = "{id}/{associationName}";
			target.prototype.cfg[resourcePathTemplate] = {
				"get" : {
					produces: ["application/json"],
					handler: target.prototype.associationListHandler
				}
			};	
		}
		return target;
	}; 
	
})();
