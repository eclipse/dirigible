/* globals $ */
/* eslint-env node, dirigible */

var HttpController = exports.HttpController = function(oConfiguration){
	this.logger = require('log/loggers').get('arestme/HttpController');
	var xss = require("utils/xss");
	this._oConfiguration = oConfiguration || {};
	var self =this;
	
	var matchRequestUrl = function(requestPath, method, cfg){
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
		return matches;
	};
	
	var normalizeMediaTypeHeaderValue = this.normalizeMediaTypeHeaderValue = function(sMediaType){
		if(sMediaType === undefined || sMediaType === null)
			return;
		sMediaType = sMediaType.split(',');//convert to array
		sMediaType = sMediaType.map(function(mimeTypeEntry){
			return mimeTypeEntry.replace('\\','').split(';')[0].trim();//remove escaping, remove quality or other atributes
		});
		return sMediaType;
	};
	
	//find MIME types intersections
	var matchMediaType = function(request, producesMediaTypes, consumesMediaTypes){
		//TODO: add support for wildcards as per HTTP headers specification (e.g. */json or text/*)
		var isProduceMatched = false;	
		var acceptsMediaTypes = normalizeMediaTypeHeaderValue(request.getHeader('Accept'));
		if(!acceptsMediaTypes || acceptsMediaTypes.indexOf('*/*')>-1){ //output media type is not restricted
			isProduceMatched = true;
		} else  {
			var matchedProducesMIME;
			if(producesMediaTypes && producesMediaTypes.length){
				matchedProducesMIME = acceptsMediaTypes.filter(function(acceptsMediaType) {
					//TODO: improve with wildcard media types assessment instead of completematch with equals.
				    return producesMediaTypes.indexOf(acceptsMediaType) > -1;
				});
				isProduceMatched = matchedProducesMIME && matchedProducesMIME.length>0;
			}
		}
		
		var isConsumeMatched = false;
		var contentTypeMediaTypes = normalizeMediaTypeHeaderValue(request.getHeader('Content-Type'));		
		if(!consumesMediaTypes || consumesMediaTypes.indexOf('*/*')>-1){ //input media type is not restricted
			isConsumeMatched = true;
		} else  {
			var matchedConsumesMIME;
			if(contentTypeMediaTypes && consumesMediaTypes && consumesMediaTypes.length){
				matchedConsumesMIME = contentTypeMediaTypes.filter(function(contentTypeMediaType) {
					//TODO: improve with wildcard media types assessment instead of completematch with equals.
				    return consumesMediaTypes.indexOf(contentTypeMediaType) > -1;
				});
				isConsumeMatched = matchedConsumesMIME && matchedConsumesMIME.length>0;
			}
		}
		return isProduceMatched && isConsumeMatched;
	};
	
	var getHttpResourcePath = function(){
		var requestUrl = decodeURI($.getRequest().getRequestURI());
		var requestUrlSegments = requestUrl.split('/');
		var inResourcePath = false;
		requestUrlSegments = requestUrlSegments.filter(function(seg){
			if(seg.trim().lastIndexOf('.js')>-1){
				inResourcePath = true;
			}
			return inResourcePath;
		});
		return requestUrlSegments.join('/');
	};
		
	var queryStringToMap = function(queryString){
		if(!queryString)
			return;
		queryString = decodeURI(queryString);
		queryString = xss.unescapeHtml(queryString).replace(/&amp;/g, '&');
		var queryStringSegments = queryString.split('&');
		var queryParams = {};
		if(queryStringSegments.length>0){
			for(var i=0; i< queryStringSegments.length; i++){
				var seg = queryStringSegments[i];
				var kv = seg.split('=');
				var key = kv[0].trim();
				var value = kv[1]===undefined ? true : kv[1].trim();
				queryParams[key] = value;
			}
		}
		return queryParams;
	};
	
  	this.service = function(request, response){
  
  		request = request || require("net/http/request");
		var requestPath = request.getAttribute("path") || "";
		var method = request.getMethod().toLowerCase();
		
		var matches = matchRequestUrl(requestPath, method, self._oConfiguration);
		var resourceHandler;
		if(matches && matches[0]){
			var verbHandlers = self._oConfiguration[matches[0].d][method];
			if(verbHandlers){
				resourceHandler = verbHandlers.filter(function(handlerDef){
					return matchMediaType(request, handlerDef.produces, handlerDef.consumes);
				})[0];
			}
		}
		
		response = response || require("net/http/response");
		var queryParams = queryStringToMap(request.getInfo().queryString) || {};		
		var acceptsHeader = normalizeMediaTypeHeaderValue(request.getHeader('Accept')) || '[]';
		var contentTypeHeader = normalizeMediaTypeHeaderValue(request.getHeader('Content-Type')) || '[]';
		var resourcePath = getHttpResourcePath();
		var io = {request: request, response:response};
		
		if(resourceHandler){
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
			if(resourceHandler.beforeHandle){
			 	if(resourceHandler.beforeHandle.constructor !== Function)
			 		throw Error('Invalid configuration exception: verbHandler.beforeHandle is not a function');
				resourceHandler.beforeHandle.apply(self, [ctx, io]);
			}
			if(!ctx.err){
				resourceHandler.handler.apply(self, [ctx, io]);
				HttpController.prototype.closeResponse.call(this);
				self.logger.info('Serving request for resource [' + resourcePath + '], Verb['+method.toUpperCase()+'], Content-Type'+contentTypeHeader+', Accept'+acceptsHeader+' finished');
			}
		} else {
			self.logger.error('No suitable resource handler for resource [' + resourcePath + '], Verb['+method.toUpperCase()+'], Content-Type'+contentTypeHeader+', Accept'+acceptsHeader+' found');
			self.sendError(io.response.BAD_REQUEST, 'Bad Request');
		}		
  	};
};

HttpController.prototype.getResourceHandlersMap = function(){
	return this._oConfiguration;
};

HttpController.prototype.addResourceHandlers = function(handlersMap){
	if(handlersMap === undefined || handlersMap === null || handlersMap.constructor !== Object)
		throw Error('Illegal argument exception: handlersMap[' + handlersMap + ']');
	var aResourcePaths = Object.keys(handlersMap);	
	for(var i=0; i<aResourcePaths.length; i++){
		var verbHandlerNames = Object.keys(handlersMap[aResourcePaths[i]]);
		for(var j=0; j<verbHandlerNames.length;j++){
			var verbHandlerDefs = handlersMap[aResourcePaths[i]][verbHandlerNames[j]];
			if(verbHandlerDefs.constructor !== Array){//Accept objects for backwards compatibility
				this.logger.warn('Verb handler value must be an array of objects. Objects will not be supported in near future');
				verbHandlerDefs = [verbHandlerDefs]
			}
			for(var k = 0; k<verbHandlerDefs.length; k++){
				var verbHandlerDef = verbHandlerDefs[k];
				HttpController.prototype.addResourceHandler.call(this, aResourcePaths[i], verbHandlerNames[j], verbHandlerDef['handler'], verbHandlerDef['consumes'], verbHandlerDef['produces'], verbHandlerDef['beforeHandler']);
			}
		}
	}
	return this;
};

HttpController.prototype.addResourceHandler = function(sPath, sMethod, fHandler, aConsumesMediaTypes, aProducesMediaTypes, fBeforeHandler){
	//validate mandatory input
	if(sPath===undefined || sPath===null)
		throw Error('Illegal argument exception: sPath['+sPath+']');
	if(sMethod===undefined || sMethod===null){
		throw Error('Illegal argument exception: sMethod['+sMethod+']');//TODO: validate method against the standard HTTP set or allow non standard too?
	}
	sMethod = sMethod.toLowerCase();
	if(fHandler===undefined || fHandler===null || fHandler.constructor !== Function)
		throw Error('Illegal argument exception: fHandler['+fHandler+']');
	//validate optionals
	if(fBeforeHandler!==undefined && fBeforeHandler!==null && fBeforeHandler.constructor !== Function)
		throw Error('Illegal argument exception: fBeforeHandler['+fBeforeHandler+']');
	if(aConsumesMediaTypes!==undefined && aConsumesMediaTypes!==null && aConsumesMediaTypes.constructor === String)//TODO: validate for conformance with mime type spec
		aConsumesMediaTypes = [aConsumesMediaTypes];
	if(aProducesMediaTypes!==undefined && aProducesMediaTypes!==null && aProducesMediaTypes.constructor === String)//TODO: validate for conformance with mime type spec
		aProducesMediaTypes = [aProducesMediaTypes];
	//construct
	var handlerDef = {};
	handlerDef['handler'] = fHandler;
	if(fBeforeHandler)
		handlerDef['beforeHandler'] = fBeforeHandler;		
	if(aConsumesMediaTypes)
		handlerDef['consumes'] = aConsumesMediaTypes;
	if(aProducesMediaTypes)
		handlerDef['produces'] = aProducesMediaTypes;

	if(this._oConfiguration[sPath] === undefined){
		this._oConfiguration[sPath] = {};
	}
	var verbHandlers = this._oConfiguration[sPath][sMethod];
	if(verbHandlers === undefined){
		this._oConfiguration[sPath][sMethod] = [];
	}
	//TODO: shoud we check for overlapping resourceHandler definitions by consumes/produces media types?
	this._oConfiguration[sPath][sMethod].push(handlerDef);
	return this;
};

HttpController.prototype.sendError = function(httpCode, errMessage) {
	var request = require("net/http/request");
	var response = require("net/http/response");
	var contentTypeHeader = this.normalizeMediaTypeHeaderValue(request.getHeader('Accept')) || ['application/json'];
	var isHtml = ['text/html']
				.some(function(mediaType){
					return contentTypeHeader.indexOf(mediaType)>-1;
				});
	response.setStatus(httpCode || response.INTERNAL_SERVER_ERROR);
	if(isHtml){
		response.sendError(httpCode, errMessage);
	} else {
	    var body = {
	    	'err': {
	    		'code': httpCode, 
	    		'message': errMessage
	    	}
	    };
	   	response.setHeader("Content-Type", "application/json");
	    response.print(JSON.stringify(body, null, 2));
	}
	this.closeResponse();
};

HttpController.prototype.closeResponse = function(){
	var response = require("net/http/response");
	response.flush();
	response.close();
};

exports.get = function(oConfiguration){
	return new HttpController(oConfiguration);
};
