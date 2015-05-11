/*******************************************************************************
 * @license
 * Copyright (c) 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution 
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html). 
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
 /*eslint-env amd, browser*/
define([
'orion/objects',
'javascript/finder',
'orion/Deferred'
], function(Objects, Finder, Deferred) {
	
	/**
	 * @description Creates a new open declaration command
	 * @constructor
	 * @public
	 * @param {javascript.ASTManager} ASTManager The backing AST manager
	 * @param {javascript.ScriptResolver} Resolver The backing script resolver 
	 * @returns {javascript.commands.OpenDeclarationCommand} A new command
	 * @since 8.0
	 */
	function OpenDeclarationCommand(ASTManager, Resolver) {
		this.astManager = ASTManager;
		this.resolver = Resolver;
	}
	
	Objects.mixin(OpenDeclarationCommand.prototype, {
		/* override */
		execute: function(editorContext/*, options*/) {
		    var that = this;
			return Deferred.all([
				this.astManager.getAST(editorContext),
				editorContext.getCaretOffset()
			]).then(function(results) {
				var node = Finder.findNode(results[1], results[0], {parents:true});
				if(node) {
				    var parents = node.parents;
				    if(parents) {
    				    var parent = parents.pop();
    				    switch(parent.type) {
    				        case 'CallExpression': {
    				            if(node.type === 'Literal' && (parent.callee.name === 'require' || parent.callee.name === 'importScripts')) {
    				                that.resolver.getWorkspaceFile(node.value).then(function(files) {
    				                    // TODO uncomment when we get a file open strategy
    				                    //window.open(that.resolver.convertToURL(files[0]));
    				                });
    				            } else {
        				            if(parent.callee.type === 'Identifier') {
            				            var decl = Finder.findDeclaration(results[1], results[0], {id: parent.callee.name, kind: Finder.SearchOptions.FUNCTION_DECLARATION});
                    					if(decl) {
                    					    return editorContext.setSelection(decl.id.range[0], decl.id.range[1]);
                    					}
                					} else if(parent.callee.type === 'MemberExpression') {
                					    //TODO need the env to find the containing object expression / func expr
                					}
            					}
            					break;
    				        }
    				        case 'ArrayExpression': {
    				            parent = parents.pop();
    				            if(parent.type === 'CallExpression' && parent.callee.name === 'define') {
    				                that.resolver.getWorkspaceFile(node.value).then(function(files) {
    				                    // TODO uncomment when we get a file open strategy
    				                    //window.open(that.resolver.convertToURL(files[0]));
    				                });
    				            }
    				            break;
    				        }
    				    }
				    }
				}
			});
		}
	});
	
	return {
		OpenDeclarationCommand : OpenDeclarationCommand
	};
});
