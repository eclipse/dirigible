 /*******************************************************************************
 * @license
 * Copyright (c) 2014, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution 
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html). 
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*eslint-env amd*/
/* global doctrine */
define([
'orion/objects',
'orion/URITemplate',
'webtools/util',
'javascript/compilationUnit',
'i18n!webtools/nls/messages',
'csslint' //for colour object
], function(Objects, URITemplate, Util, CU, messages, CSSLint) {
	
	/**
	 * @name webtools.CSSHover
	 * @description creates a new instance of the hover support
	 * @constructor
	 * @public
	 * @param {Object} resolver The backing file resolver
	 * @param {Object} cssResultManager The back result manager
	 * @since 8.0
	 */
	function CSSHover(resolver, cssResultManager) {
	    this.resolver = resolver;
	    this.cssResultManager = cssResultManager;
	}
	
	Objects.mixin(CSSHover.prototype, /** @lends webtools.CSSHover.prototype*/ {
		
		/**
		 * @name computeHover
		 * @description Callback from the editor to compute the hover
		 * @function
		 * @public 
		 * @memberof webtools.CSSHover.prototype
		 * @param {Object} editorContext The current editor context
		 * @param {Object} ctxt The current selection context
		 */
		computeHoverInfo: function computeHover(editorContext, ctxt) {
			var that = this;
			return editorContext.getFileMetadata().then(function(meta) {
			   if(meta.contentType.id === 'text/html') {
			       return editorContext.getText().then(function(text) {
			           var blocks = Util.findStyleBlocks(text);
			           if(blocks && blocks.length > 0) {
			               var cu = new CU(blocks, meta);
			               if(cu.validOffset(ctxt.offset)) {
    			               return that.cssResultManager.getResult(cu.getEditorContext(), that._emptyRuleSet()).then(function(results) {
                    			   return that._doHover(results, ctxt, meta);
                               });
                           }
			           }
			       });
			   } else {
			       return that.cssResultManager.getResult(editorContext, that._emptyRuleSet()).then(function(results) {
        			   return that._doHover(results, ctxt, meta);
                   });
			   }
			});
		},
		
		_doHover: function _doHover(results, ctxt, metadata) {
		    if(results) {
			    var token = Util.findToken(ctxt.offset, results.tokens);
				if (token){
				    //TODO, investigate creating an AST in the CSS parser, walking tokens can be expensive
				    if(this.hasPreviousToken(token, results.tokens, 'IMPORT_SYM')) {
				        return this._getFileHover(token, metadata);
				    }
				    if(this.hasPreviousToken(token, results.tokens, 'IDENT', ['background', 'background-image', '-webkit-border-image', '-o-border-image', 'border-image', 'border-image-source', 'icon'])) {
				        return this._getImageHover(token, metadata);
				    }
				    var tok = this._isRgbLike(token, results.tokens);
				    if(tok) {
				        var color = this._collectColorId(tok, results.tokens);
		                if(color) {
		                    return this._getColorHover(color);    
		                }
				    }
					if (CSSLint.Colors[token.value]){
						return this._getColorHover(token.value);
					}
					if (/\#[0-9A-Fa-f]{1,6}/.test(token.value)){
						return this._getColorHover(token.value);	
					}
					tok = this._isFontLike(token, results.tokens);
					if(tok) {
					    var font = this._collectFontId(tok, results.tokens);
					    if(font) {
					        return this._getFontHover(tok.value, font);
					    }
					}
				}
			}
			return null;
		},
		
		_emptyRuleSet: function() {
		    var config = Object.create(null);
		    config.getRuleSet = function() {return null;};
		    return config;
		},
		
		fontLikeNames: ['font-family', 'font', 'font-size', 'font-size-adjust', 'font-stretch', 'font-style', 'font-variant', 'font-weight', 
		                  'text-decoration', 'text-shadow', 'text-transform'],
		
		_isFontLike: function _isFontLike(token, tokens) {
		    if(token && tokens) {
		        for(var i = token.index; i > -1; i--) {
		            var tok = tokens[i];
		            if(tok.type === 'IDENT' || tok.type === 'COMMA' || tok.type === 'STRING' || tok.type === 'LENGTH' || tok.type === 'NUMBER' || tok.type === 'HASH') {
		                continue;
		            } else if(tok.type === 'COLON') {
		                //the next one would have to be IDENT and 'font-family'
		                tok = tokens[i-1];
		                if(tok.type === 'IDENT' && this.fontLikeNames.indexOf(tok.value.toLowerCase()) > -1) {
		                    tok.index = i-1;
		                    return tok;
		                } else {
		                    return null;
		                }
		            } else {
		                break;
		            }
		        }
		    }
		    return null;
		},
		
		_collectFontId: function _collectFontId(token, tokens) {
		    if(token && tokens) {
		        var id = '';
		        var next = null;
		        var idx = token.index;
		        //skip the colon
		        if(tokens[idx+1].type !== 'COLON') {
		            return null;
		        }
		        ++idx;
		        for(var i = idx+1; i < tokens.length; i++) {
		            next = tokens[i];
		            if(next.type === 'IDENT' || next.type === 'COMMA' || next.type === 'STRING' || next.type === 'NUMBER' || next.type === 'LENGTH' || next.type === 'HASH') {
		                id += next.value;
		                if(i < tokens.length-1) {
		                    id += ' ';
		                }
		                continue;
		            }
		            if(next.type === 'RBRACE' || next.type === 'SEMICOLON' || next.type === 'RPAREN') {
		                return id;
		            } else {
		                break;
		            }
		        }
		    }
		    return null;
		},
		
		_getFontHover: function _getFontHover(prop, font){
			var html = '<html><body><div style="'+prop+':'+font+';margin:0px">'+messages['fontHoverExampleText']+'</div></body></html>'; //$NON-NLS-0$  //$NON-NLS-1$
			return {type: "html", content: html, height: '42px', width: '235px'};  //$NON-NLS-0$  //$NON-NLS-1$  //$NON-NLS-2$
		},
		
		_isColorFnName: function _isColorFnName(name) {
		    var val = name.toLowerCase();
		    return val === 'rgba(' || val === 'rgb(' || val === 'hsl(' || val === 'hsla(';
		},
 		
		_isRgbLike: function _isRgbLike(token, tokens) {
		    if(token.type === 'FUNCTION') {
		        if(this._isColorFnName(token.value.toLowerCase())) {
		            return token;
		        }
		    } 
		    var tok = this._isRgbLikeBody(token, tokens);
		    if(tok) {
		        return tok;
		    }
		    return null;
		},
		
		_isRgbLikeBody: function _isRgbLikeBody(token, tokens) {
		    if(token && tokens) {
		        for(var i = token.index; i > -1; i--) {
		            var tok = tokens[i];
		            if(tok.type === 'NUMBER' || tok.type === 'COMMA' || tok.type === 'PERCENTAGE') {
		                continue;
		            } else if(tok.type === 'FUNCTION') {
		                if(this._isColorFnName(tok.value)) {
		                    tok.index = i;
		                    return tok;
		                } else {
		                    return null;
		                }
		            } else {
		                break;
		            }
		        }
		    }
		    return null;
		},
		
		_collectColorId: function _collectColorId(token, tokens) {
		    if(token && tokens) {
		        var id = token.value;
		        var next = null;
		        var idx = token.index;
		        for(var i = idx+1; i < tokens.length; i++) {
		            next = tokens[i];
		            if(next.type === 'COMMA' || next.type === 'NUMBER' || next.type === 'PERCENTAGE') {
		                id += next.value;
		                continue;
		            }
		            if(next.type === 'RPAREN') {
		                id += next.value;
		                return id;
		            } else {
		                break;
		            }
		        }
		    }
		    return null;
		},
		
		hasPreviousToken: function hasPreviousToken(token, tokens, name, id) {
		    if(token && tokens) {
		        switch(token.type) {
		            case 'URI': 
		            case 'STRING': {
		                if(token.index > 0) {
		                    var prev = null;
		                    for(var i = token.index-1; i >= 0; i--) {
		                        prev = tokens[i];
		                        if(prev.type === 'COLON' || prev.type === 'STRING' || prev.type === 'URI' || prev.type === 'COMMA') {
		                            continue;
		                        } else {
		                            break;
		                        }
		                    }
		                    if(Array.isArray(id) && prev && id.indexOf(prev.value) > -1) {
		                        return true;
		                    } else if(id && prev && prev.type === name) {
		                       return id === prev.value;
    		                } else {
    		                  return prev && prev.type === name;
    		                }
		                }
		            }
		        }
            }
            return false;
		},
		
		_getFileHover: function _getFileHover(token, metadata) {
		    var path = this._getPathFromToken(token);
		    if(path) {
    	        if(/^http/i.test(path)) {
    	            return this._formatFilesHover(path);
    	        } else {
    	            var that = this;
        	        return that.resolver.getWorkspaceFile(path, {ext:'css', type:'CSS', icon:'../webtools/images/css.png'}).then(function(files) {
        		        if(files && files.length > 0) {
        		            var resolved = that.resolver.resolveRelativeFiles(path, files, metadata);
        		            if(resolved.length > 0) {
        		              return that._formatFilesHover(path, resolved);
        		            }
        		        }
        	        });
    	        }
	        }
		    return null;
		},
		
		_getPathFromToken: function _getPathFromToken(token) {
		    var path = token.value;
		    switch(token.type) {
		        case 'STRING': {
		            path = token.value.slice(1, token.value.length-1); //peel off the quotes
		            break;
		        }
		        case 'URI': {
		            var val = /^\s*(?:url)\s*\(\s*(.*)\s*\)/i.exec(token.value);
    		        if(val) {
    		            path = val[1];
    		            var c = path.charAt(0);
    		            if(c === '\'' || c === '"') {
    		                path = path.slice(1);
    		            }
    		            c = path.charAt(path.length-1);
    		            if(c === '\'' || c === '"') {
    		                path = path.slice(0, path.length-1);
    		            }
    		        } else {
    		            return null;
    		        }
		        }
		    }
		    return path;
		},
		
		/**
    	 * @description Formats the list of files as links for the hover
    	 * @function
    	 * @private
    	 * @param {String} path The path we are navigating to
    	 * @param {Array.<javascript.ScriptResolver.File>} files The array of files to linkify
    	 * @returns {String} The mardown to show in the hover
    	 */
    	_formatFilesHover: function _formatFilesHover(path, files) {
    	    if(path) {
    	        var title = null; 
    	        if(files.length > 1) {
    	            title = '###Open file for \''+path+'\'###';
    	        }
    	        var hover = '';
    	        if(Array.isArray(files)) {  
        	        for(var i = 0; i < files.length; i++) {
        	            var file = files[i];
        	            if(file.name && file.path && file.contentType) {
        	                hover += '[';
        	                if(file.contentType.icon) {
        	                    hover += '!['+file.contentType.name+']('+file.contentType.icon+')';
        	                }
        	                var href = new URITemplate("#{,resource,params*}").expand(
        		                      {
        		                      resource: file.location, 
        		                      params: {}
        		                      }); //$NON-NLS-0$
        	                hover += file.name + ']('+href+') - '+file.path+'\n\n';
        	            }
        	            
        	        }
    	        } else {
    	            var name = path.slice(path.lastIndexOf('/')+1);
    	            title = '###Open file for \''+name+'\'###';
	                hover += '[!['+name+'](../webtools/images/css.png)';
	                hover += name + ']('+path+') - '+path+'\n\n';
    	        }
    	        if(hover !== '') {
    	           return {title: title, content: hover, type:'markdown'};
    	        }
    	    }
    	    return null;
    	},
		
		_getImageHover: function _getImageHover(token, metadata) {
		      var path = this._getPathFromToken(token);
		      var that = this;
		      if(path) {
		          if(/^http/i.test(path) || /^data:image.*;base64/i.test(path)) {
    		          var html = '<html><body style="margin:1px;"><img src="'+path+'" style="width:100%;height:100%;"/></body></html>'; //$NON-NLS-0$  //$NON-NLS-1$
    			      return {type: "html", content: html, width: "100px", height: "100px"};  //$NON-NLS-0$  //$NON-NLS-1$  //$NON-NLS-2$
		          } else {
		              var idx = path.lastIndexOf('.');
		              if(idx > -1) {
		                  var ext = path.slice(idx+1);
    		              return that.resolver.getWorkspaceFile(path, {ext:ext, type:'Image', icon:'../webtools/images/file.png'}).then(function(files) {
                		        if(files) {
                		            //TODO we have to resolve each time as same-named files could be referenced from different locations
                		            //and the resolver caches all hits for the name
                		            var resolved = that.resolver.resolveRelativeFiles(path, files, metadata);
                		            if(resolved.length > 0) {
                		                 var html = '<html><body style="margin:1px;"><img src="'+resolved[0].location+'" style="width:100%;height:100%;"/></body></html>'; //$NON-NLS-0$  //$NON-NLS-1$
    			                         return {type: "html", content: html, width: "100px", height: "100px"};  //$NON-NLS-0$  //$NON-NLS-1$  //$NON-NLS-2$
                		            }
                		        }
                	        });
        	          }
		          }
		      }
		},
		
		_getColorHover: function _getColorHover(colorID){
			var html = '<html><body style=\"background-color: ' + colorID + ';\"></html>'; //$NON-NLS-0$  //$NON-NLS-1$
			return {type: "html", content: html, width: "50px", height: "25px"};  //$NON-NLS-0$  //$NON-NLS-1$  //$NON-NLS-2$
		}
		
	});

	CSSHover.prototype.contructor = CSSHover;
	
	return {
		CSSHover: CSSHover
	};
});
