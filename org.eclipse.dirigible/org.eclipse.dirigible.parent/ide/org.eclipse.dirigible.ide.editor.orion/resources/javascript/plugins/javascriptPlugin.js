/*******************************************************************************
 * @license
 * Copyright (c) 2013, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution 
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html). 
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*eslint-env amd */
/*
 * This module may be loaded in a web worker or a regular Window. Therefore it must NOT use the DOM or other
 * APIs not available in workers.
 */
define([
'orion/plugin',
'orion/bootstrap',
'orion/fileClient',
'orion/metrics',
'esprima',
'estraverse',
'javascript/scriptResolver',
'javascript/astManager',
'javascript/quickFixes',
'javascript/contentAssist/indexFiles/mongodbIndex',
'javascript/contentAssist/indexFiles/mysqlIndex',
'javascript/contentAssist/indexFiles/postgresIndex',
'javascript/contentAssist/indexFiles/redisIndex',
'javascript/contentAssist/indexFiles/expressIndex',
'javascript/contentAssist/indexFiles/amqpIndex',
'javascript/contentAssist/contentAssist',
'javascript/validator',
'javascript/occurrences',
'javascript/hover',
'javascript/outliner',
'orion/util',
'javascript/commands/generateDocCommand',
'javascript/commands/openDeclaration',
'orion/editor/stylers/application_javascript/syntax',
'orion/editor/stylers/application_json/syntax',
'orion/editor/stylers/application_schema_json/syntax',
'orion/editor/stylers/application_x-ejs/syntax',
'i18n!javascript/nls/messages'
], function(PluginProvider, Bootstrap, FileClient, Metrics, Esprima, Estraverse, ScriptResolver, ASTManager, QuickFixes, MongodbIndex, MysqlIndex, PostgresIndex, RedisIndex, ExpressIndex, AMQPIndex, ContentAssist, 
			EslintValidator, Occurrences, Hover, Outliner,	Util, GenerateDocCommand, OpenDeclCommand, mJS, mJSON, mJSONSchema, mEJS, javascriptMessages) {

    Bootstrap.startup().then(function(core) {

        var provider = new PluginProvider({
    		name: javascriptMessages['pluginName'], //$NON-NLS-0$
    		version: "1.0", //$NON-NLS-0$
    		description: javascriptMessages['pluginDescription'] //$NON-NLS-0$
    	});
    	
    	/**
    	 * Register the JavaScript content types
    	 */
    	provider.registerService("orion.core.contenttype", {}, { //$NON-NLS-0$
    		contentTypes: [
    		               {	id: "application/javascript", //$NON-NLS-0$
    		            	   "extends": "text/plain", //$NON-NLS-0$ //$NON-NLS-1$
    		            	   name: "JavaScript", //$NON-NLS-0$
    		            	   extension: ["js"], //$NON-NLS-0$
    		            	   imageClass: "file-sprite-javascript modelDecorationSprite" //$NON-NLS-0$
    		               }, {id: "application/json", //$NON-NLS-0$
    		            	   "extends": "text/plain", //$NON-NLS-0$ //$NON-NLS-1$
    		            	   name: "JSON", //$NON-NLS-0$
    		            	   extension: ["json", "pref"], //$NON-NLS-0$ //$NON-NLS-1$
    		            	   imageClass: "file-sprite-javascript modelDecorationSprite" //$NON-NLS-0$
    		               }, {id: "application/x-ejs", //$NON-NLS-0$
    		            	   "extends": "text/plain", //$NON-NLS-0$ //$NON-NLS-1$
    		            	   name: "Embedded Javascript", //$NON-NLS-0$
    		            	   extension: ["ejs"], //$NON-NLS-0$
    		            	   imageClass: "file-sprite-javascript modelDecorationSprite" //$NON-NLS-0$
    		               }
    		               ]
    	});
    	
    	/**
    	 * Re-init
    	 * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=462878
    	 */
    	Metrics.initFromRegistry(core.serviceRegistry);
    	
    	/**
    	 * make sure the RecoveredNode is ignored
    	 * @since 9.0
    	 */
    	Estraverse.VisitorKeys.RecoveredNode = []; //do not visit
    	/**
    	 * Create the file client early
    	 */
    	var fileClient = new FileClient.FileClient(core.serviceRegistry);
    	
    	/**
    	 * Create the script resolver
    	 * @since 8.0
    	 */
    	var scriptresolver = new ScriptResolver.ScriptResolver(fileClient);
    	/**
    	 * Create the AST manager
    	 */
    	var astManager = new ASTManager.ASTManager(Esprima);
    	
    	/**
    	 * Register AST manager as Model Change listener
    	 */
    	provider.registerService("orion.edit.model", {  //$NON-NLS-0$
    		onModelChanging: astManager.onModelChanging.bind(astManager),
    		onInputChanged: astManager.onInputChanged.bind(astManager)
    	},
    	{
    		contentType: ["application/javascript", "text/html"],  //$NON-NLS-0$
    		types: ["ModelChanging", 'Destroy', 'onSaving', 'onInputChanged']  //$NON-NLS-0$  //$NON-NLS-1$
    	});
    	
    	provider.registerServiceProvider("orion.edit.command",  //$NON-NLS-0$
    			new GenerateDocCommand.GenerateDocCommand(astManager), 
    			{
    		name: javascriptMessages["generateDocName"],  //$NON-NLS-0$
    		tooltip : javascriptMessages['generateDocTooltip'],  //$NON-NLS-0$
    		id : "generate.js.doc.comment",  //$NON-NLS-0$
    		key : [ "j", false, true, !Util.isMac, Util.isMac],  //$NON-NLS-0$
    		contentType: ['application/javascript', 'text/html']  //$NON-NLS-0$
    			}
    	);
    	
    	provider.registerServiceProvider("orion.edit.command",  //$NON-NLS-0$
    			new OpenDeclCommand.OpenDeclarationCommand(astManager, scriptresolver), 
    			{
    		name: javascriptMessages["openDeclName"],  //$NON-NLS-0$
    		tooltip : javascriptMessages['openDeclTooltip'],  //$NON-NLS-0$
    		id : "open.js.decl",  //$NON-NLS-0$
    		key : [ 114, false, false, false, false],  //$NON-NLS-0$
    		contentType: ['application/javascript']  //$NON-NLS-0$
    			}
    	);
    	
    	var quickFixComputer = new QuickFixes.JavaScriptQuickfixes(astManager);
    	
    	provider.registerServiceProvider("orion.edit.command",  //$NON-NLS-0$
    			quickFixComputer, 
    			{
        			name: javascriptMessages["removeExtraSemiFixName"],  //$NON-NLS-0$
        			tooltip : javascriptMessages['removeExtraSemiFixTooltip'],  //$NON-NLS-0$
        			scopeId: "orion.edit.quickfix",
        			id : "rm.extra.semi.fix",  //$NON-NLS-0$
        			contentType: ['application/javascript', 'text/html'],  //$NON-NLS-0$
        			validationProperties: [
                        {source: "annotation:id", match: "no-extra-semi"} //$NON-NLS-1$ //$NON-NLS-0$
                    ]
    			}
    	);
    	
    	provider.registerServiceProvider("orion.edit.command",  //$NON-NLS-0$
    			quickFixComputer, 
    			{
        			name: javascriptMessages["addFallthroughCommentFixName"],  //$NON-NLS-0$
        			tooltip : javascriptMessages['addFallthroughCommentFixTooltip'],  //$NON-NLS-0$
        			scopeId: "orion.edit.quickfix",
        			id : "add.fallthrough.comment.fix",  //$NON-NLS-0$
        			contentType: ['application/javascript', 'text/html'],  //$NON-NLS-0$
        			validationProperties: [
                        {source: "annotation:id", match: "^(?:no-fallthrough)$"} //$NON-NLS-1$ //$NON-NLS-0$
                    ]
    			}
    	);
    	
    	provider.registerServiceProvider("orion.edit.command",  //$NON-NLS-0$
    			{
        			execute: function(editorContext, context) {
        				if(context.annotation.id === 'no-fallthrough') {
        				    context.annotation.fixid = 'no-fallthrough-break';
        				}
        				return quickFixComputer.execute(editorContext, context);
        			} 
    		    },
    			{
        			name: javascriptMessages["addBBreakFixName"],  //$NON-NLS-0$
        			tooltip : javascriptMessages['addBreakFixTooltip'],  //$NON-NLS-0$
        			scopeId: "orion.edit.quickfix",
        			id : "add.fallthrough.break.fix",  //$NON-NLS-0$
        			contentType: ['application/javascript', 'text/html'],  //$NON-NLS-0$
        			validationProperties: [
                        {source: "annotation:id", match: "^(?:no-fallthrough)$"} //$NON-NLS-1$ //$NON-NLS-0$
                    ]
    			}
    	);
    	
    	provider.registerServiceProvider("orion.edit.command",  //$NON-NLS-0$
    			quickFixComputer, 
    			{
        			name: javascriptMessages["addEmptyCommentFixName"],  //$NON-NLS-0$
        			tooltip : javascriptMessages['addEmptyCommentFixTooltip'],  //$NON-NLS-0$
        			scopeId: "orion.edit.quickfix",
        			id : "add.empty.comment.fix",  //$NON-NLS-0$
        			contentType: ['application/javascript', 'text/html'],  //$NON-NLS-0$
        			validationProperties: [
                        {source: "annotation:id", match: "^(?:no-empty-block)$"} //$NON-NLS-1$ //$NON-NLS-0$
                    ]
    			}
    	);
    	
    	provider.registerServiceProvider("orion.edit.command",  //$NON-NLS-0$
    			quickFixComputer, 
    			{
        			name: javascriptMessages["addESLintEnvFixName"],  //$NON-NLS-0$
        			tooltip : javascriptMessages['addESLintEnvFixTooltip'],  //$NON-NLS-0$
        			scopeId: "orion.edit.quickfix",
        			id : "add.eslint-env.fix",  //$NON-NLS-0$
        			contentType: ['application/javascript', 'text/html'],  //$NON-NLS-0$
        			validationProperties: [
                        {source: "annotation:id", match: "^(?:no-undef-defined-inenv)$"} //$NON-NLS-1$ //$NON-NLS-0$
                    ]
    			}
    	);
    	
    	provider.registerServiceProvider("orion.edit.command",  //$NON-NLS-0$
    			quickFixComputer, 
    			{
        			name: javascriptMessages["addESLintGlobalFixName"],  //$NON-NLS-0$
        			tooltip : javascriptMessages['addESLintGlobalFixTooltip'],  //$NON-NLS-0$
        			scopeId: "orion.edit.quickfix",
        			id : "add.eslint-global.fix",  //$NON-NLS-0$
        			contentType: ['application/javascript', 'text/html'],  //$NON-NLS-0$
        			validationProperties: [
                        {source: "annotation:id", match: "^(?:no-undef-defined)$"} //$NON-NLS-1$ //$NON-NLS-0$
                    ]
    			}
    	);
    	
    	provider.registerServiceProvider("orion.edit.command",  //$NON-NLS-0$
    			{
    		execute: function(editorContext, context) {
    			if(context.annotation.id === 'no-unused-params-expr') {
    			    context.annotation.fixid = 'no-unused-params';
                    //return quickFixComputer['no-unused-params'](editorContext, context.annotation, astManager);
    			}
    			return quickFixComputer.execute(editorContext, context);
    		}
    			}, 
    			{
    				name: javascriptMessages["removeUnusedParamsFixName"],  //$NON-NLS-0$
    				tooltip : javascriptMessages['removeUnusedParamsFixTooltip'],  //$NON-NLS-0$
    				scopeId: "orion.edit.quickfix",
    				id : "remove.unused.param.fix",  //$NON-NLS-0$
    				contentType: ['application/javascript', 'text/html'],  //$NON-NLS-0$
    				validationProperties: [
                        {source: "annotation:id", match: "^(?:no-unused-params|no-unused-params-expr)$"} //$NON-NLS-1$ //$NON-NLS-0$
                    ]
    			}
    	);
    	
    	provider.registerServiceProvider("orion.edit.command",  //$NON-NLS-0$
    			quickFixComputer, 
    			{
        			name: javascriptMessages["commentCallbackFixName"],  //$NON-NLS-0$
        			tooltip : javascriptMessages['commentCallbackFixTooltip'],  //$NON-NLS-0$
        			scopeId: "orion.edit.quickfix",
        			id : "comment.callback.fix",  //$NON-NLS-0$
        			contentType: ['application/javascript', 'text/html'],  //$NON-NLS-0$
        			validationProperties: [
                        {source: "annotation:id", match: "^(?:no-unused-params-expr)$"} //$NON-NLS-1$ //$NON-NLS-0$
                    ]
    			}
    	);
    	
    	provider.registerServiceProvider("orion.edit.command",  //$NON-NLS-0$
    			quickFixComputer, 
    			{
        			name: javascriptMessages["eqeqeqFixName"],  //$NON-NLS-0$
        			tooltip : javascriptMessages['eqeqeqFixTooltip'],  //$NON-NLS-0$
        			scopeId: "orion.edit.quickfix",
        			id : "eqeqeq.fix",  //$NON-NLS-0$
        			contentType: ['application/javascript', 'text/html'],  //$NON-NLS-0$
        			validationProperties: [
                        {source: "annotation:id", match: "^(?:eqeqeq)$"} //$NON-NLS-1$ //$NON-NLS-0$
                    ]
    			}
    	);
    	
    	provider.registerServiceProvider("orion.edit.command",  //$NON-NLS-0$
    			quickFixComputer, 
    			{
        			name: javascriptMessages["unreachableFixName"],  //$NON-NLS-0$
        			tooltip : javascriptMessages['unreachableFixTooltip'],  //$NON-NLS-0$
        			scopeId: "orion.edit.quickfix",
        			id : "remove.unreachable.fix",  //$NON-NLS-0$
        			contentType: ['application/javascript', 'text/html'],  //$NON-NLS-0$
        			validationProperties: [
                        {source: "annotation:id", match: "^(?:no-unreachable)$"} //$NON-NLS-1$ //$NON-NLS-0$
                    ]
    			}
    	);
    	
    	provider.registerServiceProvider("orion.edit.command",  //$NON-NLS-0$
    			quickFixComputer, 
    			{
        			name: javascriptMessages["sparseArrayFixName"],  //$NON-NLS-0$
        			tooltip : javascriptMessages['sparseArrayFixTooltip'],  //$NON-NLS-0$
        			scopeId: "orion.edit.quickfix",
        			id : "sparse.array.fix",  //$NON-NLS-0$
        			contentType: ['application/javascript', 'text/html'],  //$NON-NLS-0$
        			validationProperties: [
                        {source: "annotation:id", match: "^(?:no-sparse-arrays)$"} //$NON-NLS-1$ //$NON-NLS-0$
                    ]
    			}
    	);
    	
    	provider.registerServiceProvider("orion.edit.command",  //$NON-NLS-0$
    			quickFixComputer, 
    			{
        			name: javascriptMessages["semiFixName"],  //$NON-NLS-0$
        			tooltip : javascriptMessages['semiFixTooltip'],  //$NON-NLS-0$
        			scopeId: "orion.edit.quickfix",
        			id : "semi.fix",  //$NON-NLS-0$
        			contentType: ['application/javascript', 'text/html'],  //$NON-NLS-0$
        			validationProperties: [
                        {source: "annotation:id", match: "^(?:semi)$"} //$NON-NLS-1$ //$NON-NLS-0$
                    ]
    			}
    	);
    	
    	provider.registerServiceProvider("orion.edit.command",  //$NON-NLS-0$
    			quickFixComputer, 
    			{
        			name: javascriptMessages["unusedVarsUnusedFixName"],  //$NON-NLS-0$
        			tooltip : javascriptMessages['unusedVarsUnusedFixTooltip'],  //$NON-NLS-0$
        			scopeId: "orion.edit.quickfix",
        			id : "unused.var.fix",  //$NON-NLS-0$
        			contentType: ['application/javascript', 'text/html'],  //$NON-NLS-0$
        			validationProperties: [
                        {source: "annotation:id", match: "^(?:no-unused-vars-unused)$"} //$NON-NLS-1$ //$NON-NLS-0$
                    ]
    			}
    	);
    	
    	provider.registerServiceProvider("orion.edit.command",  //$NON-NLS-0$
    			quickFixComputer, 
    			{
        			name: javascriptMessages["unusedFuncDeclFixName"],  //$NON-NLS-0$
        			tooltip : javascriptMessages['unusedFuncDeclFixTooltip'],  //$NON-NLS-0$
        			scopeId: "orion.edit.quickfix",
        			id : "unused.func.decl.fix",  //$NON-NLS-0$
        			contentType: ['application/javascript', 'text/html'],  //$NON-NLS-0$
        			validationProperties: [
                        {source: "annotation:id", match: "^(?:no-unused-vars-unused-funcdecl)$"} //$NON-NLS-1$ //$NON-NLS-0$
                    ]
    			}
    	);
    	
    	provider.registerServiceProvider("orion.edit.command",  //$NON-NLS-0$
    			quickFixComputer, 
    			{
        			name: javascriptMessages["noCommaDangleFixName"],  //$NON-NLS-0$
        			tooltip : javascriptMessages['noCommaDangleFixTooltip'],  //$NON-NLS-0$
        			scopeId: "orion.edit.quickfix",
        			id : "no.comma.dangle.fix",  //$NON-NLS-0$
        			contentType: ['application/javascript', 'text/html'],  //$NON-NLS-0$
        			validationProperties: [
                        {source: "annotation:id", match: "^(?:no-comma-dangle)$"} //$NON-NLS-1$ //$NON-NLS-0$
                    ]
    			}
    	);
    	
    	/**
    	 * Register the jsdoc-based outline
    	 */
    	provider.registerService("orion.edit.outliner", new Outliner.JSOutliner(astManager),  //$NON-NLS-0$
    			{ contentType: ["application/javascript"],  //$NON-NLS-0$
    		name: javascriptMessages["sourceOutline"],  //$NON-NLS-0$
    		title: javascriptMessages['sourceOutlineTitle'],  //$NON-NLS-0$
    		id: "orion.javascript.outliner.source"  //$NON-NLS-0$
    			});
    	
    	/**
    	 * Register the mark occurrences support
    	 */
    	provider.registerService("orion.edit.occurrences", new Occurrences.JavaScriptOccurrences(astManager),  //$NON-NLS-0$
    			{
    		contentType: ["application/javascript", "text/html"]	//$NON-NLS-0$ //$NON-NLS-1$
    			});
    	
    	/**
    	 * Register the hover support
    	 */
    	provider.registerService("orion.edit.hover", new Hover.JavaScriptHover(astManager, scriptresolver),  //$NON-NLS-0$
    			{
    		name: javascriptMessages['jsHover'],
    		contentType: ["application/javascript", "text/html"]	//$NON-NLS-0$ //$NON-NLS-1$
    			});
    	
    	provider.registerService("orion.edit.contentassist", new ContentAssist.JSContentAssist(astManager),  //$NON-NLS-0$
    			{
    		contentType: ["application/javascript", 'text/html'],  //$NON-NLS-0$
    		name: javascriptMessages["contentAssist"],  //$NON-NLS-0$
    		id: "orion.edit.contentassist.javascript",  //$NON-NLS-0$
    		charTriggers: "[.]",  //$NON-NLS-0$
    		excludedStyles: "(string.*)"  //$NON-NLS-0$
    			});
    	
    	var validator = new EslintValidator(astManager);
    	
    	/**
    	 * Register the ESLint validator
    	 */
    	provider.registerService("orion.edit.validator", validator,  //$NON-NLS-0$  //$NON-NLS-1$
    			{
    		contentType: ["application/javascript", "text/html"],  //$NON-NLS-0$ //$NON-NLS-1$
    		pid: 'eslint.config'  //$NON-NLS-0$
    			});
    	
    	/**
    	 * legacy pref id
    	 */
    	provider.registerService("orion.cm.managedservice", validator, {pid: "eslint.config"});
    	/**
    	 * new sectioned pref block ids
    	 */
    	provider.registerService("orion.cm.managedservice", validator, {pid: "eslint.config.potential"});
    	provider.registerService("orion.cm.managedservice", validator, {pid: "eslint.config.practices"});
    	provider.registerService("orion.cm.managedservice", validator, {pid: "eslint.config.codestyle"});
    	
    	/**
    	 * ESLint settings
    	 */
    	var ignore = 0, warning = 1, error = 2, severities = [
    	                                                      {label: javascriptMessages.ignore,  value: ignore},  //$NON-NLS-0$
    	                                                      {label: javascriptMessages.warning, value: warning},  //$NON-NLS-0$
    	                                                      {label: javascriptMessages.error,   value: error}  //$NON-NLS-0$
    	                                                      ];
    	provider.registerService("orion.core.setting",  //$NON-NLS-0$
    			{},
    			{	settings: [
    			 	           {   pid: "eslint.config.potential",  //$NON-NLS-0$
    			 	           	   order: 1,
				 	        	   name: javascriptMessages['prefPotentialProblems'],  //$NON-NLS-0$
 				 	        	   tags: "validation javascript js eslint".split(" "),  //$NON-NLS-0$  //$NON-NLS-1$
 				 	        	   category: "javascript",  //$NON-NLS-0$
 				 	        	   properties: [{	id: "no-cond-assign",  //$NON-NLS-0$ 
    			 	        	                	name: javascriptMessages["noCondAssign"], //$NON-NLS-0$
    			 	        	                	type: "number", //$NON-NLS-0$
    			 	        	                	defaultValue: error, //$NON-NLS-0$
    			 	        	                	options: severities //$NON-NLS-0$
    			 	        	                },
    			 	        	                {	id: "no-constant-condition",  //$NON-NLS-0$
    			 	        	                	name: javascriptMessages["noConstantCondition"], //$NON-NLS-0$
    			 	        	                	type: "number", //$NON-NLS-0$
    			 	        	                	defaultValue: error, //$NON-NLS-0$
    			 	        	                	options: severities //$NON-NLS-0$
    			 	        	                },
    			 	        	                {   id: "no-console",  //$NON-NLS-0$
    			 	        	                	name: javascriptMessages["noConsole"], //$NON-NLS-0$
    			 	        	                	type: "number", //$NON-NLS-0$
    			 	        	                	defaultValue: error, //$NON-NLS-0$
    			 	        	                	options: severities //$NON-NLS-0$
    			 	        	                },
     				 	        	            {	id: "no-debugger",  //$NON-NLS-0$
 				 	        	                	name: javascriptMessages["noDebugger"],  //$NON-NLS-0$
				 	        	                	type: "number",  //$NON-NLS-0$
				 	        	                	defaultValue: warning,
				 	        	                	options: severities
				 	        	                },
				 	        	                {	id: "no-dupe-keys",  //$NON-NLS-0$
				 	        	                	name: javascriptMessages["noDupeKeys"],  //$NON-NLS-0$
				 	        	                	type: "number",  //$NON-NLS-0$
				 	        	                	defaultValue: error,
				 	        	                	options: severities
				 	        	                },
				 	        	                {	id: "valid-typeof",  //$NON-NLS-0$
				 	        	                	name: javascriptMessages["validTypeof"],  //$NON-NLS-0$
				 	        	                	type: "number",  //$NON-NLS-0$
				 	        	                	defaultValue: error,
				 	        	                	options: severities
				 	        	                },
				 	        	                {	id: "no-regex-spaces",  //$NON-NLS-0$
				 	        	                	name: javascriptMessages["noRegexSpaces"], //$NON-NLS-0$
				 	        	                	type: "number", //$NON-NLS-0$
				 	        	                	defaultValue: error, //$NON-NLS-0$
				 	        	                	options: severities //$NON-NLS-0$
				 	        	                },
				 	        	                {	id: "use-isnan",  //$NON-NLS-0$
				 	        	                	name: javascriptMessages["useIsNaN"],  //$NON-NLS-0$
				 	        	                	type: "number",  //$NON-NLS-0$
				 	        	                	defaultValue: error,
				 	        	                	options: severities
				 	        	                },
				 	        	                {	id: "no-reserved-keys",
				 	        	                	name: javascriptMessages["noReservedKeys"], //$NON-NLS-0$
				 	        	                	type: "number", //$NON-NLS-0$
				 	        	                	defaultValue: error, //$NON-NLS-0$
				 	        	                	options: severities //$NON-NLS-0$
				 	        	                },
				 	        	                {	id: "no-sparse-arrays",
				 	        	                	name: javascriptMessages["noSparseArrays"],  //$NON-NLS-0$
				 	        	                	type: "number",  //$NON-NLS-0$
				 	        	                	defaultValue: warning,
				 	        	                	options: severities
				 	        	                },
				 	        	                {	id: "no-fallthrough",  //$NON-NLS-0$
				 	        	                	name: javascriptMessages["noFallthrough"],  //$NON-NLS-0$
				 	        	                	type: "number",  //$NON-NLS-0$
				 	        	                	defaultValue: error,
				 	        	                	options: severities
				 	        	                },
				 	        	                {	id: "no-comma-dangle", //$NON-NLS-0$
				 	        	                	name: javascriptMessages["noCommaDangle"], //$NON-NLS-0$
				 	        	                	type: "number", //$NON-NLS-0$
				 	        	                	defaultValue: ignore, //$NON-NLS-0$
				 	        	                	options: severities //$NON-NLS-0$
				 	        	                },
				 	        	                {	id: "no-empty-block",  //$NON-NLS-0$
				 	        	                	name: javascriptMessages["noEmptyBlock"],  //$NON-NLS-0$
				 	        	                	type: "number",  //$NON-NLS-0$
				 	        	                	defaultValue: ignore,
				 	        	                	options: severities
				 	        	                },
				 	        	                {	id: "no-extra-semi",  //$NON-NLS-0$
				 	        	                	name: javascriptMessages["unnecessarySemis"],  //$NON-NLS-0$
				 	        	                	type: "number",  //$NON-NLS-0$
				 	        	                	defaultValue: warning,
				 	        	                	options: severities
				 	        	                },
				 	        	                {	id: "no-unreachable",  //$NON-NLS-0$
				 	        	                	name: javascriptMessages["noUnreachable"],  //$NON-NLS-0$
				 	        	                	type: "number",  //$NON-NLS-0$
				 	        	                	defaultValue: error,
				 	        	                	options: severities
				 	        	                }]
				 	        	},
				 	        	{  pid: "eslint.config.practices",  //$NON-NLS-0$
				 	        	   order: 2,
				 	        	   name: javascriptMessages['prefBestPractices'],  //$NON-NLS-0$
				 	        	   tags: "validation javascript js eslint".split(" "),  //$NON-NLS-0$  //$NON-NLS-1$
				 	        	   category: "javascript",  //$NON-NLS-0$
				 	        	   properties: [{	id: "no-caller",  //$NON-NLS-0$
				 	        	                	name: javascriptMessages["noCaller"], //$NON-NLS-0$
				 	        	                	type: "number", //$NON-NLS-0$
				 	        	                	defaultValue: warning, //$NON-NLS-0$
				 	        	                	options: severities //$NON-NLS-0$
				 	        	                },
				 	        	                {	id: "eqeqeq",  //$NON-NLS-0$
				 	        	                	name: javascriptMessages["noEqeqeq"],  //$NON-NLS-0$
 				 	        	                	type: "number",  //$NON-NLS-0$
 				 	        	                	defaultValue: warning,
 				 	        	                	options: severities
 				 	        	                },
 				 	        	                {	id: "no-eval",  //$NON-NLS-0$
    			 	        	                	name: javascriptMessages["noEval"],  //$NON-NLS-0$
    			 	        	                	type: "number",  //$NON-NLS-0$
    			 	        	                	defaultValue: ignore,
    			 	        	                	options: severities
    			 	        	                },
    			 	        	                {	id: "no-iterator",  //$NON-NLS-0$
    			 	        	                	name: javascriptMessages["noIterator"], //$NON-NLS-0$
    			 	        	                	type: "number", //$NON-NLS-0$
    			 	        	                	defaultValue: error, //$NON-NLS-0$
    			 	        	                	options: severities //$NON-NLS-0$
    			 	        	                },
 				 	        	                {
    			 	        	                	id: "no-new-array", //$NON-NLS-0$
    			 	        	                	name: javascriptMessages["noNewArray"], //$NON-NLS-0$
    			 	        	                	type: "number", //$NON-NLS-0$
    			 	        	                	defaultValue: warning, //$NON-NLS-0$
    			 	        	                	options: severities //$NON-NLS-0$
    			 	        	                },
    			 	        	                {
    			 	        	                	id: "no-new-func", //$NON-NLS-0$
    			 	        	                	name: javascriptMessages["noNewFunc"], //$NON-NLS-0$
    			 	        	                	type: "number", //$NON-NLS-0$
    			 	        	                	defaultValue: warning, //$NON-NLS-0$
    			 	        	                	options: severities //$NON-NLS-0$
    			 	        	                },
    			 	        	                {
    			 	        	                	id: "no-new-object", //$NON-NLS-0$
    			 	        	                	name: javascriptMessages["noNewObject"], //$NON-NLS-0$
    			 	        	                	type: "number", //$NON-NLS-0$
    			 	        	                	defaultValue: warning, //$NON-NLS-0$
    			 	        	                	options: severities //$NON-NLS-0$
    			 	        	                },
    			 	        	                {
    			 	        	                	id: "no-new-wrappers", //$NON-NLS-0$
    			 	        	                	name: javascriptMessages["noNewWrappers"], //$NON-NLS-0$
    			 	        	                	type: "number", //$NON-NLS-0$
    			 	        	                	defaultValue: warning, //$NON-NLS-0$
    			 	        	                	options: severities //$NON-NLS-0$
    			 	        	                },
 				 	        	                {
 				 	        	                	id: "no-shadow-global", //$NON-NLS-0$
 				 	        	                	name: javascriptMessages["noShadowGlobals"], //$NON-NLS-0$
 				 	        	                	defaultValue: warning, //$NON-NLS-0$
 				 	        	                	type: "number",  //$NON-NLS-0$
 				 	        	                	options: severities //$NON-NLS-0$
 				 	        	                },
 				 	        	                {	id: "no-use-before-define",  //$NON-NLS-0$
 				 	        	                	name: javascriptMessages["useBeforeDefine"],  //$NON-NLS-0$
 				 	        	                	type: "number",  //$NON-NLS-0$
 				 	        	                	defaultValue: warning,
 				 	        	                	options: severities
 				 	        	                },
 				 	        	                {	id: "radix",  //$NON-NLS-0$
 				 	        	                    name: javascriptMessages['radix'],  //$NON-NLS-0$
 				 	        	                    type: 'number',  //$NON-NLS-0$
 				 	        	                	defaultValue: warning,
 				 	        	                	options: severities
 				 	        	                },
 				 	        	                {	id: "no-throw-literal",  //$NON-NLS-0$
 				 	        	                	name: javascriptMessages["noThrowLiteral"],  //$NON-NLS-0$
 				 	        	                	type: "number",  //$NON-NLS-0$
 				 	        	                	defaultValue: warning,
 				 	        	                	options: severities
 				 	        	                },
 				 	        	                {	id: "curly",  //$NON-NLS-0$
    			 	        	                	name: javascriptMessages["missingCurly"],  //$NON-NLS-0$
    			 	        	                	type: "number",  //$NON-NLS-0$
    			 	        	                	defaultValue: ignore,
    			 	        	                	options: severities
    			 	        	                },
 				 	        	                {	id: "no-undef",  //$NON-NLS-0$
 				 	        	                	name: javascriptMessages["undefMember"],  //$NON-NLS-0$
 				 	        	                	type: "number",  //$NON-NLS-0$
 				 	        	                	defaultValue: error,
 				 	        	                	options: severities
 				 	        	                },
 				 	        	                {	id: "no-unused-params",  //$NON-NLS-0$
 				 	        	                	name: javascriptMessages["unusedParams"],  //$NON-NLS-0$
 				 	        	                	type: "number",  //$NON-NLS-0$
 				 	        	                	defaultValue: warning,
 				 	        	                	options: severities
 				 	        	                },
 				 	        	                {	id: "no-unused-vars",  //$NON-NLS-0$
    			 	        	                	name: javascriptMessages["unusedVars"],  //$NON-NLS-0$
    			 	        	                	type: "number",  //$NON-NLS-0$
    			 	        	                	defaultValue: warning,
    			 	        	                	options: severities
    			 	        	                },
 				 	        	                {	id: "no-redeclare",  //$NON-NLS-0$
 				 	        	                    name: javascriptMessages['varRedecl'],
 				 	        	                    type: 'number',  //$NON-NLS-0$
 				 	        	                    defaultValue: warning,
 				 	        	                	options: severities
 				 	        	                },
 				 	        	                {	id: "no-shadow",  //$NON-NLS-0$
    			 	        	                	name: javascriptMessages["varShadow"],  //$NON-NLS-0$
    			 	        	                	type: "number",  //$NON-NLS-0$
    			 	        	                	defaultValue: warning,
    			 	        	                	options: severities
    			 	        	                }]
				 	            },
				 	        	{  pid: "eslint.config.codestyle",  //$NON-NLS-0$
				 	        	   order: 3,
				 	        	   name: javascriptMessages['prefCodeStyle'],  //$NON-NLS-0$
				 	        	   tags: "validation javascript js eslint".split(" "),  //$NON-NLS-0$  //$NON-NLS-1$
				 	        	   category: "javascript",  //$NON-NLS-0$
				 	        	   properties: [{	id: "missing-doc",
				 	        	                	name: javascriptMessages["missingDoc"],  //$NON-NLS-0$
				 	        	                	type: "number",  //$NON-NLS-0$
				 	        	                	defaultValue: ignore,
				 	        	                	options: severities
				 	        	                },
				 	        	                {	id: "new-parens",  //$NON-NLS-0$
				 	        	                	name: javascriptMessages["newParens"],  //$NON-NLS-0$
				 	        	                	type: "number",  //$NON-NLS-0$
				 	        	                	defaultValue: error,
				 	        	                	options: severities
				 	        	                },
				 	        	                {	id: "semi",  //$NON-NLS-0$
				 	        	                	name: javascriptMessages["missingSemi"],  //$NON-NLS-0$
				 	        	                	type: "number",  //$NON-NLS-0$
				 	        	                	defaultValue: warning,
				 	        	                	options: severities
				 	        	                },
				 	        	                {	id: "no-jslint",  //$NON-NLS-0$
				 	        	                	name: javascriptMessages["unsupportedJSLint"],  //$NON-NLS-0$
				 	        	                	type: "number",  //$NON-NLS-0$
				 	        	                	defaultValue: warning,
				 	        	                	options: severities
				 	        	                }]
				 	        	}]
    			});
    	
    	/**
    	 * Register syntax styling for js, json and json schema content
    	 */
    	var newGrammars = {};
    	mJS.grammars.forEach(function(current){
    		newGrammars[current.id] = current;
    	});
    	mJSON.grammars.forEach(function(current){
    		newGrammars[current.id] = current;
    	});
    	mJSONSchema.grammars.forEach(function(current){
    		newGrammars[current.id] = current;
    	});
    	mEJS.grammars.forEach(function(current){
    		newGrammars[current.id] = current;
    	});
    	for (var current in newGrammars) {
    		if (newGrammars.hasOwnProperty(current)) {
    			provider.registerService("orion.edit.highlighter", {}, newGrammars[current]);
    		}
    	}
    	
    	/**
    	 * Register type definitions for known JS libraries
    	 */
    	provider.registerService("orion.core.typedef", {}, {  //$NON-NLS-0$
    		id: "node.redis",  //$NON-NLS-0$
    		type: "tern",  //$NON-NLS-0$
    		defs: RedisIndex
    	});
    	provider.registerService("orion.core.typedef", {}, {  //$NON-NLS-0$
    		id: "node.mysql",  //$NON-NLS-0$
    		type: "tern",  //$NON-NLS-0$
    		defs: MysqlIndex
    	});
    	provider.registerService("orion.core.typedef", {}, {  //$NON-NLS-0$
    		id: "node.postgres",  //$NON-NLS-0$
    		type: "tern",  //$NON-NLS-0$
    		defs: PostgresIndex
    	});
    	provider.registerService("orion.core.typedef", {}, {  //$NON-NLS-0$
    		id: "node.mongodb",  //$NON-NLS-0$
    		type: "tern",  //$NON-NLS-0$
    		defs: MongodbIndex
    	});
    	provider.registerService("orion.core.typedef", {}, {  //$NON-NLS-0$
    		id: "node.express",  //$NON-NLS-0$
    		type: "tern", //$NON-NLS-0$
    		defs: ExpressIndex
    	});
    	provider.registerService("orion.core.typedef", {}, {  //$NON-NLS-0$
    		id: "node.amqp",  //$NON-NLS-0$
    		type: "tern",  //$NON-NLS-0$
    		defs: AMQPIndex
    	});
    	
    	provider.connect();
	});
});
