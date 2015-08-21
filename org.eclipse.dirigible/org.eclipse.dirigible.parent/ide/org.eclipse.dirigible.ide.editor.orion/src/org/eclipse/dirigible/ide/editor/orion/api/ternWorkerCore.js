/*******************************************************************************
 * @license
 * Copyright (c) 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution 
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html). 
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*globals importScripts onmessage:true doctrine onconnect:true requirejs*/
/*eslint-env node, browser*/
var lang ='en'; //$NON-NLS-1$
var sear = self.location.search;
if(sear) {
	var langs = sear.split('worker-language'); //$NON-NLS-1$
	if(Array.isArray(langs) && langs.length === 2) {
		lang = langs[1].slice(1);
	}
} 
requirejs.config({locale: lang});
require([
	'tern/lib/tern',
	'tern/plugin/doc_comment',
	'tern/plugin/orionAmqp',
	'tern/plugin/angular',
	//'tern/plugin/component',
	'tern/plugin/orionExpress',	
	'tern/plugin/orionMongoDB',
	'tern/plugin/orionMySQL',	
	'tern/plugin/orionNode',
	'tern/plugin/orionPostgres',
	'tern/plugin/orionRedis',
	'tern/plugin/orionRequire',
	'tern/plugin/ternPlugins',
	'tern/plugin/openImplementation',
	'json!tern/defs/ecma5.json',
	'json!tern/defs/ecma6.json',
	'json!tern/defs/browser.json',
	'json!tern/defs/dirigible.json',
	'javascript/handlers/ternAssistHandler',
	'javascript/handlers/ternDeclarationHandler',
	'javascript/handlers/ternHoverHandler',
	'javascript/handlers/ternOccurrencesHandler',
	'javascript/handlers/ternRenameHandler',
	'javascript/handlers/ternPluginsHandler',
	'javascript/handlers/ternRefsHandler',
	'javascript/handlers/ternImplementationHandler',
	'i18n!javascript/nls/workermessages',
	'orion/i18nUtil'
],
/* @callback */ function(Tern, docPlugin, orionAMQPPlugin, angularPlugin,/* componentPlugin,*/ orionExpressPlugin, orionMongoDBPlugin,
							orionMySQLPlugin, orionNodePlugin, orionPostgresPlugin, orionRedisPlugin, orionRequirePlugin, ternPluginsPlugin, 
							openImplPlugin, ecma5, ecma6, browser, dirigible, AssistHandler, DeclarationHandler, HoverHandler, OccurrencesHandler, 
							RenameHandler, PluginsHandler, RefsHandler, ImplHandler, Messages, i18nUtil) {
    
    var ternserver, pendingReads = Object.create(null);
    
    /**
     * @description Start up the Tern server, send a message after trying
     */
    function startServer() {
        var options = {
                async: true,
                debug:true,
                defs: [ecma5, ecma6, browser, dirigible],
                projectDir: '/', //$NON-NLS-1$
                plugins: {
                    doc_comment: {
                    	name: Messages['ternDocPluginName'],
                    	description: Messages['ternDocPluginDescription'],
                        fullDocs: true,
                        version: '0.10.0', //$NON-NLS-1$
                        removable: false
                    },
                    orionAmqp: {
                    	name: Messages['orionAMQPPluginName'],
                    	description: Messages['orionAMQPPluginDescription'],
                    	version: '0.9.1', //$NON-NLS-1$
                    	removable: true,
                    	env: 'amqp' //$NON-NLS-1$
                    },
                    angular: {
                    	name: Messages['orionAngularPluginName'],
                    	description: Messages['orionAngularPluginDescription'],
                    	version: '0.10.0', //$NON-NLS-1$
                    	removable: true
                    },
                   /* component: {
                    	name: Messages['orionComponentPluginName'],
                    	description: Messages['orionComponentPluginDescription'],
                    	version: '0.10.0', //$NON-NLS-1$
                    	removable: true,
                    },*/
                    orionExpress: {
                    	name: Messages['orionExpressPluginName'],
                    	description: Messages['orionExpressPluginDescription'],
                    	version: '4.12.4', //$NON-NLS-1$
                    	removable: true,
                    	env: 'express' //$NON-NLS-1$
                    },
                    orionMongoDB: {
                    	name: Messages['orionMongoDBPluginName'],
                    	description: Messages['orionMongoDBPluginDescription'],
                    	version: '1.1.21', //$NON-NLS-1$
                    	removable: true,
                    	env: 'mongodb' //$NON-NLS-1$
                    },
                    orionMySQL: {
                    	name: Messages['orionMySQLPluginName'],
                    	description: Messages['orionMySQLPluginDescription'],
                    	version: '2.7.0', //$NON-NLS-1$
                    	removable: true,
                    	env: 'mysql' //$NON-NLS-1$
                    },
                    orionNode: {
                    	name: Messages['orionNodePluginName'],
                    	description: Messages['orionNodePluginDescription'],
                    	version: '0.10.0', //$NON-NLS-1$
                    	removable: true
                    },
                    orionPostgres: {
                    	name: Messages['orionPostgresPluginName'],
                    	description: Messages['orionPostgresPluginDescription'],
                    	version: '4.4.0', //$NON-NLS-1$
	                   	removable: true,
	                   	env: 'pg' //$NON-NLS-1$
                    },
                    orionRedis: {
                    	name: Messages['orionRedisPluginName'],
                    	description: Messages['orionRedisPluginDescription'],
                    	version: '0.12.1', //$NON-NLS-1$
                    	removable: true,
                    	env: 'redis' //$NON-NLS-1$
                    },
                    orionRequire: {
                    	name: Messages['orionRequirePluginName'],
                    	description: Messages['orionRequirePluginDescription'],
                    	version: '0.10.0', //$NON-NLS-1$
                    	removable: true
                    },
                    plugins: {
                    	name: Messages['ternPluginsPluginName'],
                    	description: Messages['ternPluginsPluginDescription'],
                    	version: '1.0', //$NON-NLS-1$
                    	removable: false
                    },
                    openImplementation: {
                    	name : Messages['openImplPluginName'],
                    	description: Messages['openImplPluginDescription'],
                    	version: '1.0', //$NON-NLS-1$
                    	removable: false
                    }
                },
                getFile: _getFile
            };
        
        ternserver = new Tern.Server(options);
        post('server_ready'); //$NON-NLS-1$
    }
    startServer();
    
    /**
     * @description Worker callback when a message is sent to the worker
     * @callback
     */
    onmessage = function(evnt) {
        if(typeof(evnt.data) === 'object') {
            var _d = evnt.data;
            if(typeof(_d.request) === 'string') {
                switch(_d.request) {
                    case 'completions': {
                        AssistHandler.computeProposals(ternserver, _d.args, post);
                        break;
                    }
                    case 'occurrences': {
                        OccurrencesHandler.computeOccurrences(ternserver, _d.args, post);
                        break;
                    }
                    case 'definition': {
                        DeclarationHandler.computeDeclaration(ternserver, _d.args, post);
                        break;
                    }
                    case 'documentation': {
                        HoverHandler.computeHover(ternserver, _d.args, post);
                        break;
                    }
                    case 'rename': {
                        RenameHandler.computeRename(ternserver, _d.args, post);
                        break;
                    }
                    case 'refs': {
                    	RefsHandler.computeRefs(ternserver, _d.args, post);
                    	break;
                    }
                    case 'implementation': {
                    	ImplHandler.computeImplementation(ternserver, _d.args, post);
                    	break;
                    }
                    case 'addFile': {
                    	ternserver.addFile(_d.args.file, _d.args.source);
                    	break;
                    }
                    case 'delfile': {
                        _deleteFile(_d.args);
                        break;
                    }
                    case 'read': {
                        _contents(_d.args);
                        break;
                    }
                    case 'installed_plugins': {
                    	PluginsHandler.getInstalledPlugins(ternserver, _d.args, post);
                    	break;
                    }
                    case 'install_plugins': {
                    	PluginsHandler.installPlugins(ternserver, _d.args, post);
                    	break;
                    }
                    case 'remove_plugins': {
                    	PluginsHandler.removePlugins(ternserver, _d.args, post);
                    	break;
                    }
                    case 'plugin_enablement': {
                    	PluginsHandler.setPluginEnablement(ternserver, _d.args, post);
                    	break;
                    }
                    case 'environments': {
                    	PluginsHandler.getEnvironments(ternserver, _d.args, post);
                    	break;
                    }
                }
            }
        }
    };
    
    /**
     * @description Worker callback when an error occurs
     * @callback
     */
   	onerror = function(evnt) {
    	post(evnt);
    };
    
    /**
     * @description Worker callback when a shared worker starts up
     * @callback
     */
    onconnect = function(evnt) {
    	this.port = evnt.ports[0];
    	this.port.onmessage = onmessage;
    	this.port.start();
    };
    
    /**
     * @description Sends the given message back to the client. If the msg is null, send an Error
     * object with the optional given error message
     * @param {Object} msg The message to send back to the client
     * @param {String} errormsg The optional error message to send back to the client if the main message is null
     */
    function post(msg, errormsg) {
    	if(!msg) {
    		msg = new Error(errormsg ? errormsg : Messages['unknownError']);
    	}
    	if(this.port) {
    		this.port.postMessage(msg);
    	} else {
    		postMessage(msg);
    	}
    }
    
    /**
     * @description Notifies the Tern server that file contents are ready
     * @param {Object} args The args from the message
     */
    function _contents(args) {
        var err = args.error;
        var contents = args.contents;
        var file = args.file;
        var reads = pendingReads[file];
        if(Array.isArray(reads)) {
            var f = reads.shift();
            if(typeof(f) === 'function') {
            	f(err, contents);
            }
        }
        reads = pendingReads[args.logical];
        if(Array.isArray(reads)) {
        	f = reads.shift();
            if(typeof(f) === 'function') {
            	f(err, {contents: contents, file:file, logical:args.logical});
            }
        }
    }
    
    /**
     * @description Removes a file from Tern
     * @param {Object} args the request args
     */
    function _deleteFile(args) {
        if(ternserver && typeof(args.file) === 'string') {
            ternserver.delFile(args.file);
        } else {
            post(i18nUtil.formatMessage(Messages['failedDeleteRequest'], args.file)); 
        }
    }
    
    /**
     * @description Read a file from the workspace into Tern
     * @private
     * @param {String} file The full path of the file
     * @param {Function} callback The callback once the file has been read or failed to read
     */
    function _getFile(file, callback) {
    	if(ternserver) {
        	var _f = file;
           if(typeof(file) === 'object') {
           		_f = file.logical;
           }
           if(!Array.isArray(pendingReads[_f])) {
           		pendingReads[_f] = [];
           }
           pendingReads[_f].push(callback);
           post({request: 'read', args: {file:file}}); //$NON-NLS-1$
	    } else {
	       post(i18nUtil.formatMessage(Messages['failedReadRequest'], _f)); //$NON-NLS-1$
	    }
    }
});