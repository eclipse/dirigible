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
/*eslint-env browser, amd*/
/*global escape*/
define([], function() {
    var codeMap = {
        "androidapp": {source: "org.eclipse.orion.client.ui/web/androidapp"},
        "auth"		: {source: "org.eclipse.orion.client.ui/web/auth"},
        "browse"	: {source: "org.eclipse.orion.client.ui/web/browse"},
        "cfui"		: {source: "org.eclipse.orion.client.cf/web/cfui"},
        "chai"		: {source: "org.eclipse.orion.client.core/web/chai"},
        "compare"	: {source: "org.eclipse.orion.client.ui/web/compare"},
        "compare-tree": {source: "org.eclipse.orion.client.ui/web/compare-tree"},
        "content"	: {source: "org.eclipse.orion.client.ui/web/content"},
        "css"		: {source: "org.eclipse.orion.client.ui/web/css"},
        "csslint"	: {source: "org.eclipse.orion.client.webtools/web/csslint"},
        "doctrine"	: {source: "org.eclipse.orion.client.javascript/web/doctrine"},
        "edit"		: {source: "org.eclipse.orion.client.ui/web/edit"},
        "escope"	: {source: "org.eclipse.orion.client.javascript/web/escope"},
        "eslint"	: {source: "org.eclipse.orion.client.javascript/web/eslint"},
        "esprima"	: {source: "org.eclipse.orion.client.javascript/web/esprima"},
        "estraverse": {source: "org.eclipse.orion.client.javascript/web/estraverse"},
        "examplePages": {source: "org.eclipse.orion.client.ui/web/"},
        "examples/editor": {source: "org.eclipse.orion.client.editor/web/examples"},
        "font"		: {source: "org.eclipse.orion.client.ui/web/font"},
        "gcli"		: {source: "org.eclipse.orion.client.ui/web/gcli"},
        "git"		: {source: "org.eclipse.orion.client.git/web/git"},
        "help"		: {source: "org.eclipse.orion.client.help/web/help"},
        "helpContent": {source: "org.eclipse.orion.client.help/web/helpContent"},
        "htmlparser": {source: "org.eclipse.orion.client.webtools/web/htmlparser"},
        "images"	: {source: "org.eclipse.orion.client.ui/web/images"},
        "javascript": {source: "org.eclipse.orion.client.javascript/web/javascript"},
        "jsdiff"	: {source: "org.eclipse.orion.client.ui/web/jsdiff"},
        "js-tests/core"	: {source: "org.eclipse.orion.client.core/web/js-tests/core"},
        "js-tests/editor"	: {source: "org.eclipse.orion.client.editor/web/js-tests/editor"},
        "js-tests/javascript": {source: "org.eclipse.orion.client.javascript/web/js-tests/javascript"},
        "js-tests/ui"	: {source: "org.eclipse.orion.client.ui/web/js-tests/ui"},
        "js-tests/webtools": {source: "org.eclipse.orion.client.webtools/web/js-tests/webtools"},
        "marked"	: {source: "org.eclipse.orion.client.ui/web/marked"},
        "mixloginstatic": {source: "org.eclipse.orion.client.ui/web/mixloginstatic"},
        "mocha"	: {source: "org.eclipse.orion.client.core/web/mocha"},
        "operations": {source: "org.eclipse.orion.client.ui/web/operations"},
        "orion/cfui"		: {source: "org.eclipse.orion.client.cf/web/orion/cfui"},
        "orion/Base64.js"		: {source: "org.eclipse.orion.client.core/web/orion/Base64.js"},
        "orion/blameAnnotations.js": {source: "org.eclipse.orion.client.core/web/orion/blameAnnotations.js"},
        "orion/bootstrap.js"	: {source: "org.eclipse.orion.client.core/web/orion/bootstrap.js"},
        "orion/config.js"		: {source: "org.eclipse.orion.client.core/web/orion/config.js"},
        "orion/contentTypes.js"	: {source: "org.eclipse.orion.client.core/web/orion/contentTypes.js"},
        "orion/Deferred.js"	: {source: "org.eclipse.orion.client.core/web/orion/Deferred.js"},
        "orion/encoding-shim.js"	: {source: "org.eclipse.orion.client.core/web/orion/encoding-shim.js"},
        "orion/EventTarget.js"	: {source: "org.eclipse.orion.client.core/web/orion/EventTarget.js"},
        "orion/fileClient.js"	: {source: "org.eclipse.orion.client.core/web/orion/fileClient.js"},
        "orion/fileMap.js"	: {source: "org.eclipse.orion.client.core/web/orion/fileMap.js"},
        "orion/fileUtils.js"	: {source: "org.eclipse.orion.client.core/web/orion/fileUtils.js"},
        "orion/form.js"		: {source: "org.eclipse.orion.client.core/web/orion/form.js"},
        "orion/HTMLTemplates-shim.js": {source: "org.eclipse.orion.client.core/web/orion/HTMLTemplates-shim.js"},
        "orion/i18n.js"		: {source: "org.eclipse.orion.client.core/web/orion/i18n.js"},
        "orion/i18nUtil.js"	: {source: "org.eclipse.orion.client.core/web/orion/i18nUtil.js"},
        "orion/keyBinding.js"	: {source: "org.eclipse.orion.client.core/web/orion/keyBinding.js"},
        "orion/log.js"		: {source: "org.eclipse.orion.client.core/web/orion/log.js"},
        "orion/metatype.js"	: {source: "org.eclipse.orion.client.core/web/orion/metatype.js"},
        "orion/objects.js"		: {source: "org.eclipse.orion.client.core/web/orion/objects.js"},
        "orion/operation.js"	: {source: "org.eclipse.orion.client.core/web/orion/operation.js"},
        "orion/operationsClient.js": {source: "org.eclipse.orion.client.core/web/orion/operationsClient.js"},
        "orion/plugin.js"		: {source: "org.eclipse.orion.client.core/web/orion/plugin.js"},
        "orion/pluginregistry.js"	: {source: "org.eclipse.orion.client.core/web/orion/pluginregistry.js"},
        "orion/preferences.js"	: {source: "org.eclipse.orion.client.core/web/orion/preferences.js"},
        "orion/problems.js"	: {source: "org.eclipse.orion.client.core/web/orion/problems.js"},
        "orion/projectClient.js"	: {source: "org.eclipse.orion.client.core/web/orion/projectClient.js"},
        "orion/regex.js"		: {source: "org.eclipse.orion.client.core/web/orion/regex.js"},
        "orion/serialize.js"	: {source: "org.eclipse.orion.client.core/web/orion/serialize.js"},
        "orion/serviceregistry.js": {source: "org.eclipse.orion.client.core/web/orion/serviceregistry.js"},
        "orion/serviceTracker.js"	: {source: "org.eclipse.orion.client.core/web/orion/serviceTracker.js"},
        "orion/Storage.js"	: {source: "org.eclipse.orion.client.core/web/orion/Storage.js"},
        "orion/testHelper.js"	: {source: "org.eclipse.orion.client.core/web/orion/testHelper.js"},
        "orion/URITemplate.js"	: {source: "org.eclipse.orion.client.core/web/orion/URITemplate.js"},
        "orion/url.js"		: {source: "org.eclipse.orion.client.core/web/orion/url.js"},
        "orion/URL-shim.js"	: {source: "org.eclipse.orion.client.core/web/orion/URL-shim.js"},
        "orion/util.js"		: {source: "org.eclipse.orion.client.core/web/orion/util.js"},
        "orion/xhr.js"		: {source: "org.eclipse.orion.client.core/web/orion/xhr.js"},
        "orion/editor"		: {source: "org.eclipse.orion.client.editor/web/orion/editor"},
        "orion/git"		: {source: "org.eclipse.orion.client.git/web/orion/git"},
        "orion/help"		: {source: "org.eclipse.orion.client.help/web/orion/help"},
        "orion/banner"		: {source: "org.eclipse.orion.client.ui/web/orion/banner"},
        "orion/compare"		: {source: "org.eclipse.orion.client.ui/web/orion/compare"},
        "orion/content"		: {source: "org.eclipse.orion.client.ui/web/orion/content"},
        "orion/crawler"		: {source: "org.eclipse.orion.client.ui/web/orion/crawler"},
        "orion/edit"		: {source: "org.eclipse.orion.client.ui/web/orion/edit"},
        "orion/explorers"		: {source: "org.eclipse.orion.client.ui/web/orion/explorers"},
        "orion/globalsearch"	: {source: "org.eclipse.orion.client.ui/web/orion/globalsearch"},
        "orion/inputCompletion"	: {source: "org.eclipse.orion.client.ui/web/orion/inputCompletion"},
        "orion/mixloginstatic"	: {source: "org.eclipse.orion.client.ui/web/orion/mixloginstatic"},
        "orion/navigate"		: {source: "org.eclipse.orion.client.ui/web/orion/navigate"},
        "orion/nls"		: {source: "org.eclipse.orion.client.ui/web/orion/nls"},
        "orion/operations"	: {source: "org.eclipse.orion.client.ui/web/orion/operations"},
        "orion/projects"		: {source: "org.eclipse.orion.client.ui/web/orion/projects"},
        "orion/search"		: {source: "org.eclipse.orion.client.ui/web/orion/search"},
        "orion/searchAndReplace"	: {source: "org.eclipse.orion.client.ui/web/orion/searchAndReplace"},
        "orion/settings"		: {source: "org.eclipse.orion.client.ui/web/orion/settings"},
        "orion/shell"		: {source: "org.eclipse.orion.client.ui/web/orion/shell"},
        "orion/sites"		: {source: "org.eclipse.orion.client.ui/web/orion/sites"},
        "orion/ssh"		: {source: "org.eclipse.orion.client.ui/web/orion/ssh"},
        "orion/stringexternalizer": {source: "org.eclipse.orion.client.ui/web/orion/stringexternalizer"},
        "orion/webui"		: {source: "org.eclipse.orion.client.ui/web/orion/webui"},
        "orion/widgets"		: {source: "org.eclipse.orion.client.ui/web/orion/widgets"},
        "orion/profile"		: {source: "org.eclipse.orion.client.users/web/orion/profile"},
        "pako"	: {source: "org.eclipse.orion.client.core/web/pako"},
        "plugins/helpPlugin.html"		: {source: "org.eclipse.orion.client.help/web/plugins/helpPlugin.html"},
        "plugins/helpPlugin.js"		: {source: "org.eclipse.orion.client.help/web/plugins/helpPlugin.js"},
        "plugins/contentTemplates"	: {source: "org.eclipse.orion.client.ui/web/plugins/contentTemplates"},
        "plugins/filePlugin"		: {source: "org.eclipse.orion.client.ui/web/plugins/filePlugin"},
        "plugins/images"			: {source: "org.eclipse.orion.client.ui/web/plugins/images"},
        "plugins/languages"		: {source: "org.eclipse.orion.client.ui/web/plugins/languages"},
        "plugins/site"			: {source: "org.eclipse.orion.client.ui/web/plugins/site"},
        "plugins/asyncUpperPlugin.html"	: {source: "org.eclipse.orion.client.ui/web/plugins/asyncUpperPlugin.html"},
        "plugins/authenticationPlugin.html": {source: "org.eclipse.orion.client.ui/web/plugins/authenticationPlugin.html"},
        "plugins/authenticationPlugin.js"	: {source: "org.eclipse.orion.client.ui/web/plugins/authenticationPlugin.js"},
        "plugins/commentPlugin.html"	: {source: "org.eclipse.orion.client.ui/web/plugins/commentPlugin.html"},
        "plugins/delimiterPlugin.html"	: {source: "org.eclipse.orion.client.ui/web/plugins/delimiterPlugin.html"},
        "plugins/delimiterPlugin.js"	: {source: "org.eclipse.orion.client.ui/web/plugins/delimiterPlugin.js"},
        "plugins/fileClientPlugin.html"	: {source: "org.eclipse.orion.client.ui/web/plugins/fileClientPlugin.html"},
        "plugins/fileClientPlugin.js"	: {source: "org.eclipse.orion.client.ui/web/plugins/fileClientPlugin.js"},
        "plugins/GerritFilePlugin.html"	: {source: "org.eclipse.orion.client.ui/web/plugins/GerritFilePlugin.html"},
        "plugins/GerritFilePlugin.js"	: {source: "org.eclipse.orion.client.ui/web/plugins/GerritFilePlugin.js"},
        "plugins/gitBlamePlugin.html"	: {source: "org.eclipse.orion.client.ui/web/plugins/gitBlamePlugin.html"},
        "plugins/gitBlamePlugin.js"	: {source: "org.eclipse.orion.client.ui/web/plugins/gitBlamePlugin.js"},
        "plugins/GitHubFilePlugin.html"	: {source: "org.eclipse.orion.client.ui/web/plugins/GitHubFilePlugin.html"},
        "plugins/GitHubFilePlugin.js"	: {source: "org.eclipse.orion.client.ui/web/plugins/GitHubFilePlugin.js"},
        "plugins/metrics/googleAnalyticsPlugin.js"	: {source: "org.eclipse.orion.client.ui/web/plugins/metrics/googleAnalyticsPlugin.js"},
        "plugins/HoverTestPlugin2.html"	: {source: "org.eclipse.orion.client.ui/web/plugins/HoverTestPlugin2.html"},
        "plugins/HoverTestPlugin2.js"	: {source: "org.eclipse.orion.client.ui/web/plugins/HoverTestPlugin2.js"},
        "plugins/HTML5LocalFilePlugin.html": {source: "org.eclipse.orion.client.ui/web/plugins/HTML5LocalFilePlugin.html"},
        "plugins/jslintPlugin.html"	: {source: "org.eclipse.orion.client.ui/web/plugins/jslintPlugin.html"},
        "plugins/jslintPlugin.js"		: {source: "org.eclipse.orion.client.ui/web/plugins/jslintPlugin.js"},
        "plugins/lowerPlugin.html"	: {source: "org.eclipse.orion.client.ui/web/plugins/lowerPlugin.html"},
        "plugins/nonnlsPlugin.html"	: {source: "org.eclipse.orion.client.ui/web/plugins/nonnlsPlugin.html"},
        "plugins/nonnlsPlugin.js"		: {source: "org.eclipse.orion.client.ui/web/plugins/nonnlsPlugin.js"},
        "plugins/pageLinksPlugin.html"	: {source: "org.eclipse.orion.client.ui/web/plugins/pageLinksPlugin.html"},
        "plugins/pageLinksPlugin.js"	: {source: "org.eclipse.orion.client.ui/web/plugins/pageLinksPlugin.js"},
        "plugins/preferencesPlugin.html"	: {source: "org.eclipse.orion.client.ui/web/plugins/preferencesPlugin.html"},
        "plugins/preferencesPlugin.js"	: {source: "org.eclipse.orion.client.ui/web/plugins/preferencesPlugin.js"},
        "plugins/sampleCommandsPlugin.html": {source: "org.eclipse.orion.client.ui/web/plugins/sampleCommandsPlugin.html"},
        "plugins/sampleFilePlugin.html"	: {source: "org.eclipse.orion.client.ui/web/plugins/sampleFilePlugin.html"},
        "plugins/sampleSearchProposalPlugin_filtered.html": {source: "org.eclipse.orion.client.ui/web/plugins/sampleSearchProposalPlugin_filtered.html"},
        "plugins/sampleSearchProposalPlugin_notFiltered.html": {source: "org.eclipse.orion.client.ui/web/plugins/sampleSearchProposalPlugin_notFiltered.html"},
        "plugins/sampleSearchProposalPlugin_pageLinks.html": {source: "org.eclipse.orion.client.ui/web/plugins/sampleSearchProposalPlugin_pageLinks.html"},
        "plugins/taskPlugin.html"		: {source: "org.eclipse.orion.client.ui/web/plugins/taskPlugin.html"},
        "plugins/taskPlugin.js"		: {source: "org.eclipse.orion.client.ui/web/plugins/taskPlugin.js"},
        "plugins/toRGBPlugin.html"	: {source: "org.eclipse.orion.client.ui/web/plugins/toRGBPlugin.html"},
        "plugins/unittestPlugin.html"	: {source: "org.eclipse.orion.client.ui/web/plugins/unittestPlugin.html"},
        "plugins/upperPlugin.html"	: {source: "org.eclipse.orion.client.ui/web/plugins/upperPlugin.html"},
        "plugins/webdavFilePlugin.html"	: {source: "org.eclipse.orion.client.ui/web/plugins/webdavFilePlugin.html"},
        "plugins/webEditingPlugin.html"	: {source: "org.eclipse.orion.client.ui/web/plugins/webEditingPlugin.html"},
        "plugins/webEditingPlugin.js"	: {source: "org.eclipse.orion.client.ui/web/plugins/webEditingPlugin.js"},
        "plugins/xhrPlugin.html"		: {source: "org.eclipse.orion.client.ui/web/plugins/xhrPlugin.html"},  
        "profile"		: {source: "org.eclipse.orion.client.users/web/profile"},
        "requirejs"	: {source: "org.eclipse.orion.client.core/web/requirejs"},
        "settings"	: {source: "org.eclipse.orion.client.ui/web/settings"},
        "shell"		: {source: "org.eclipse.orion.client.ui/web/shell"},
        "sites"		: {source: "org.eclipse.orion.client.ui/web/sites"},
        "stringexternalizer": {source: "org.eclipse.orion.client.ui/web/stringexternalizer"},
        "webapp"		: {source: "org.eclipse.orion.client.ui/web/webapp"},
        "webtools"	: {source: "org.eclipse.orion.client.webtools/web/webtools" }
    };

	function buildSubPaths(segments, trimCount) {
		var newPathname = segments[0];
		for (var i = 1; i < (segments.length-trimCount); i++) {
		  newPathname += "/";
		  newPathname += segments[i];
		}
		var trimmedPath = "";
		for (i = (segments.length-trimCount); i < segments.length; i++) {
		  trimmedPath += "/";
		  trimmedPath += segments[i];
		}
		return {candidate: newPathname, trimmed: trimmedPath};
	}
	
	function getWSPath(deployedPath) {
		var match = codeMap[deployedPath]; //fast hash lookup
		if(!match) {
    		var segments = deployedPath.split('/');
    		var trimCount = 0;
    		var splitPath = null;
    		while (!match && (trimCount < segments.length)) {
    			splitPath = buildSubPaths(segments, trimCount);
    			match = codeMap[splitPath.candidate];
    			trimCount++;
    		}
		}
		if (match) {
			match = "bundles/" + match.source;
			if(splitPath) {
			    match += splitPath.trimmed;
			}
		}
		
		return match;
	}
	
	return {getWSPath: getWSPath};
});
