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
define(['orion/plugin',
'orion/bootstrap',
'orion/fileClient',
'orion/metrics',
'webtools/htmlAstManager',
'webtools/htmlHover',
'javascript/scriptResolver',
'webtools/htmlContentAssist', 
'webtools/htmlOutliner',
'orion/editor/stylers/text_html/syntax', 
'webtools/cssContentAssist', 
'webtools/cssValidator',
'webtools/cssOutliner',
'webtools/cssHover',
'webtools/cssQuickFixes',
'webtools/cssResultManager',
'orion/editor/stylers/text_css/syntax',
'i18n!webtools/nls/messages'
], function(PluginProvider, Bootstrap, FileClient, Metrics, HtmlAstManager, htmlHover, ScriptResolver, htmlContentAssist, htmlOutliner, 
            mHTML, cssContentAssist, mCssValidator, mCssOutliner, cssHover, cssQuickFixes, cssResultManager, mCSS, messages) {
	
	Bootstrap.startup().then(function(core) { 
    	/**
    	 * Plug-in headers
    	 */
    	var headers = {
    		name: messages["pluginName"], //$NON-NLS-0$
    		version: "1.0", //$NON-NLS-0$
    		description: messages["pluginDescription"] //$NON-NLS-0$
    	};
    	var provider = new PluginProvider(headers);
    
    	/**
    	 * Register the content types: HTML, CSS
    	 */
    	provider.registerServiceProvider("orion.core.contenttype", {}, { //$NON-NLS-0$
    		contentTypes: [
    			{	id: "text/html", //$NON-NLS-0$
    				"extends": "text/plain", //$NON-NLS-0$ //$NON-NLS-1$
    				name: "HTML", //$NON-NLS-0$
    				extension: ["html", "htm"], //$NON-NLS-0$ //$NON-NLS-1$
    				imageClass: "file-sprite-html modelDecorationSprite" //$NON-NLS-0$
    			},
    			{	id: "text/css", //$NON-NLS-0$
    				"extends": "text/plain", //$NON-NLS-0$ //$NON-NLS-1$
    				name: "CSS", //$NON-NLS-0$
    				extension: ["css"], //$NON-NLS-0$
    				imageClass: "file-sprite-css modelDecorationSprite" //$NON-NLS-0$
    			}
    		] 
    	});
    	/**
    	 * Re-init
    	 * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=462878
    	 */
    	Metrics.initFromRegistry(core.serviceRegistry);
    	/**
    	 * load file client early
    	 */
    	var fileClient = new FileClient.FileClient(core.serviceRegistry);
    	
    	/**
    	 * Register content assist providers
    	 */
    	provider.registerService("orion.edit.contentassist", //$NON-NLS-0$
    		new htmlContentAssist.HTMLContentAssistProvider(),
    		{	name: messages['htmlContentAssist'], //$NON-NLS-0$
    			contentType: ["text/html"], //$NON-NLS-0$
    			charTriggers: "<", //$NON-NLS-0$
    			excludedStyles: "(comment.*|string.*)" //$NON-NLS-0$
    		});
    
        var cssResultMgr = new cssResultManager.CssResultManager();
    	
    	/**
    	 * Register result manager as model changed listener
    	 */
    	provider.registerService("orion.edit.model", {  //$NON-NLS-0$
    		onModelChanging: cssResultMgr.onModelChanging.bind(cssResultMgr),
    		onInputChanged: cssResultMgr.onInputChanged.bind(cssResultMgr)
    	},
    	{
    		contentType: ["text/css", "text/html"],  //$NON-NLS-0$
    		types: ["ModelChanging", 'Destroy', 'onSaving', 'onInputChanged']  //$NON-NLS-0$  //$NON-NLS-1$
    	});
    
        provider.registerService("orion.edit.contentassist", //$NON-NLS-0$
    		new cssContentAssist.CssContentAssistProvider(cssResultMgr),
    		{	name: messages["cssContentAssist"], //$NON-NLS-0$
    			contentType: ["text/css", "text/html"] //$NON-NLS-0$
    		});
    		
    	/**
    	 * Register validators
    	 */
    	provider.registerService(["orion.edit.validator", "orion.cm.managedservice"], new mCssValidator(cssResultMgr), //$NON-NLS-0$  //$NON-NLS-1$
    		{
    			contentType: ["text/css" /*, "text/html"*/], //$NON-NLS-0$
    			pid: 'csslint.config'  //$NON-NLS-0$
    		});
    		
    	var htmlAstManager = new HtmlAstManager.HtmlAstManager();
    	
    	/**
    	 * Register AST manager as Model Change listener
    	 */
    	provider.registerService("orion.edit.model", {  //$NON-NLS-0$
    		onModelChanging: htmlAstManager.onModelChanging.bind(htmlAstManager),
    		onInputChanged: htmlAstManager.onInputChanged.bind(htmlAstManager)
    	},
    	{
    		contentType: ["text/html"],  //$NON-NLS-0$
    		types: ["ModelChanging", 'Destroy', 'onSaving', 'onInputChanged']  //$NON-NLS-0$  //$NON-NLS-1$
    	});
    	
    	/**
    	* Register outliners
    	*/
    	provider.registerService("orion.edit.outliner", new htmlOutliner.HtmlOutliner(htmlAstManager), //$NON-NLS-0$
    		{
    			id: "orion.webtools.html.outliner", //$NON-NLS-0$
    			name: messages["htmlOutline"], //$NON-NLS-0$
    			contentType: ["text/html"] //$NON-NLS-0$
    		});
    	
    	provider.registerService("orion.edit.outliner", new mCssOutliner.CssOutliner(),  //$NON-NLS-0$
    		{
    			id: "orion.outline.css.outliner", //$NON-NLS-0$
    			name: messages["cssOutline"], //$NON-NLS-0$
    			contentType: ["text/css"] //$NON-NLS-0$
    		});
    		
    	/**
    	 * Register syntax styling
    	 */
    	var newGrammars = {};
    	mCSS.grammars.forEach(function(current){
    		newGrammars[current.id] = current;
    	});
    	mHTML.grammars.forEach(function(current){
    		newGrammars[current.id] = current;
    	});
    	for (var current in newGrammars) {
    	    if (newGrammars.hasOwnProperty(current)) {
       			provider.registerService("orion.edit.highlighter", {}, newGrammars[current]); //$NON-NLS-0$
      		}
        }
    
        var resolver = new ScriptResolver.ScriptResolver(fileClient);
    
        /**
    	 * Register the hover support
    	 */
    	provider.registerService("orion.edit.hover", new cssHover.CSSHover(resolver, cssResultMgr),  //$NON-NLS-0$
    		{
    		    name: messages['cssHover'],	//$NON-NLS-0$
    			contentType: ["text/css", "text/html"]	//$NON-NLS-0$
    	});
    	
    	/**
    	 * Register the hover support
    	 */
    	provider.registerService("orion.edit.hover", new htmlHover.HTMLHover(htmlAstManager, resolver),  //$NON-NLS-0$
    		{
    		    name: messages['htmlHover'],	//$NON-NLS-0$
    			contentType: ["text/html"]	//$NON-NLS-0$
    	});
    	
    	/**
    	 * Register quick fixes as editor commands
    	 */
    	var cssQuickFixComputer = new cssQuickFixes.CssQuickFixes();
    		
    	provider.registerServiceProvider("orion.edit.command",  //$NON-NLS-0$
    			cssQuickFixComputer, 
    			{
    		name: messages["quickfix-empty-rules"],  //$NON-NLS-0$
    		scopeId: "orion.edit.quickfix", //$NON-NLS-0$
    		id : "quickfix-empty-rules",  //$NON-NLS-0$
    		contentType: ['text/css'],  //$NON-NLS-0$
    		validationProperties: [
    		                       {source: "annotation:id", match: "empty-rules"} //$NON-NLS-1$ //$NON-NLS-0$
    		                       ]
    			}
    	);
    	provider.registerServiceProvider("orion.edit.command",  //$NON-NLS-0$
    			cssQuickFixComputer, 
    			{
    		name: messages["quickfix-important"],  //$NON-NLS-0$
    		scopeId: "orion.edit.quickfix", //$NON-NLS-0$
    		id : "quickfix-important",  //$NON-NLS-0$
    		contentType: ['text/css'],  //$NON-NLS-0$
    		validationProperties: [
    		                       {source: "annotation:id", match: "important"} //$NON-NLS-1$ //$NON-NLS-0$
    		                       ]
    			}
    	);
    	provider.registerServiceProvider("orion.edit.command",  //$NON-NLS-0$
    			cssQuickFixComputer, 
    			{
    		name: messages["quickfix-zero-units"],  //$NON-NLS-0$
    		scopeId: "orion.edit.quickfix", //$NON-NLS-0$
    		id : "quickfix-zero-units",  //$NON-NLS-0$
    		contentType: ['text/css'],  //$NON-NLS-0$
    		validationProperties: [
    		                       {source: "annotation:id", match: "zero-units"} //$NON-NLS-1$ //$NON-NLS-0$
    		                       ]
    			}
    	);
    	
        /**
    	 * CSSLint settings
    	 */
    	var ignore = 0, warning = 1, error = 2, severities = [
    		{label: messages.ignore,  value: ignore},  //$NON-NLS-0$
    		{label: messages.warning, value: warning},  //$NON-NLS-0$
    		{label: messages.error,   value: error}  //$NON-NLS-0$
    	];
    	provider.registerService("orion.core.setting",  //$NON-NLS-0$
    		{},
    		{	settings: [
    				{	pid: "csslint.config",  //$NON-NLS-0$
    					name: messages["csslintValidator"],  //$NON-NLS-0$
    					tags: "validation webtools css csslint".split(" "),  //$NON-NLS-0$  //$NON-NLS-1$
    					category: "css",  //$NON-NLS-0$
    					properties: [
    						{
    							id: "validate_adjoining_classes", //$NON-NLS-0$
    							name: messages["adjoining-classes"], //$NON-NLS-0$
    							type: "number", //$NON-NLS-0$
    							defaultValue: warning, //$NON-NLS-0$
    							options: severities //$NON-NLS-0$
    						},
    						{
    							id: "validate_box_model", //$NON-NLS-0$
    							name: messages["box-model"], //$NON-NLS-0$
    							type: "number", //$NON-NLS-0$
    							defaultValue: warning, //$NON-NLS-0$
    							options: severities //$NON-NLS-0$
    						},
    						{
    							id: "validate_box_sizing", //$NON-NLS-0$
    							name: messages["box-sizing"], //$NON-NLS-0$
    							type: "number", //$NON-NLS-0$
    							defaultValue: warning, //$NON-NLS-0$
    							options: severities //$NON-NLS-0$
    						},
    						{
    							id: "validate_bulletproof_font_face", //$NON-NLS-0$
    							name: messages["bulletproof-font-face"], //$NON-NLS-0$
    							type: "number", //$NON-NLS-0$
    							defaultValue: warning, //$NON-NLS-0$
    							options: severities //$NON-NLS-0$
    						},
    						{
    							id: "validate_compatible_vendor_prefixes", //$NON-NLS-0$
    							name: messages["compatible-vendor-prefixes"], //$NON-NLS-0$
    							type: "number", //$NON-NLS-0$
    							defaultValue: warning, //$NON-NLS-0$
    							options: severities //$NON-NLS-0$
    						},
    						{
    							id: "validate_display_property_grouping", //$NON-NLS-0$
    							name: messages["display-property-grouping"], //$NON-NLS-0$
    							type: "number", //$NON-NLS-0$
    							defaultValue: warning, //$NON-NLS-0$
    							options: severities //$NON-NLS-0$
    						},{
    							id: "validate_duplicate_background_images", //$NON-NLS-0$
    							name: messages["duplicate-background-images"], //$NON-NLS-0$
    							type: "number", //$NON-NLS-0$
    							defaultValue: warning, //$NON-NLS-0$
    							options: severities //$NON-NLS-0$
    						},
    						{
    							id: "validate_duplicate_properties", //$NON-NLS-0$
    							name: messages["duplicate-properties"], //$NON-NLS-0$
    							type: "number", //$NON-NLS-0$
    							defaultValue: warning, //$NON-NLS-0$
    							options: severities //$NON-NLS-0$
    						},
    						{
    							id: "validate_empty_rules", //$NON-NLS-0$
    							name: messages["empty-rules"], //$NON-NLS-0$
    							type: "number", //$NON-NLS-0$
    							defaultValue: warning, //$NON-NLS-0$
    							options: severities //$NON-NLS-0$
    						},
    						{
    							id: "validate_fallback_colors", //$NON-NLS-0$
    							name: messages["fallback-colors"], //$NON-NLS-0$
    							type: "number", //$NON-NLS-0$
    							defaultValue: warning, //$NON-NLS-0$
    							options: severities //$NON-NLS-0$
    						},
    						{
    							id: "validate_floats", //$NON-NLS-0$
    							name: messages["floats"], //$NON-NLS-0$
    							type: "number", //$NON-NLS-0$
    							defaultValue: warning, //$NON-NLS-0$
    							options: severities //$NON-NLS-0$
    						},
    						{
    							id: "validate_font_faces", //$NON-NLS-0$
    							name: messages["font-faces"], //$NON-NLS-0$
    							type: "number", //$NON-NLS-0$
    							defaultValue: warning, //$NON-NLS-0$
    							options: severities //$NON-NLS-0$
    						},
    						{
    							id: "validate_font_sizes", //$NON-NLS-0$
    							name: messages["font-sizes"], //$NON-NLS-0$
    							type: "number", //$NON-NLS-0$
    							defaultValue: warning, //$NON-NLS-0$
    							options: severities //$NON-NLS-0$
    						},
    						{
    							id: "validate_gradients", //$NON-NLS-0$
    							name: messages["gradients"], //$NON-NLS-0$
    							type: "number", //$NON-NLS-0$
    							defaultValue: warning, //$NON-NLS-0$
    							options: severities //$NON-NLS-0$
    						},
    						{
    							id: "validate_ids", //$NON-NLS-0$
    							name: messages["ids"], //$NON-NLS-0$
    							type: "number", //$NON-NLS-0$
    							defaultValue: warning, //$NON-NLS-0$
    							options: severities //$NON-NLS-0$
    						},
    						{
    							id: "validate_imports", //$NON-NLS-0$
    							name: messages["import"], //$NON-NLS-0$
    							type: "number", //$NON-NLS-0$
    							defaultValue: warning, //$NON-NLS-0$
    							options: severities //$NON-NLS-0$
    						},
    						{
    							id: "validate_important", //$NON-NLS-0$
    							name: messages["important"], //$NON-NLS-0$
    							type: "number", //$NON-NLS-0$
    							defaultValue: warning, //$NON-NLS-0$
    							options: severities //$NON-NLS-0$
    						},
    						{
    							id: "validate_known_properties", //$NON-NLS-0$
    							name: messages["known-properties"], //$NON-NLS-0$
    							type: "number", //$NON-NLS-0$
    							defaultValue: warning, //$NON-NLS-0$
    							options: severities //$NON-NLS-0$
    						},
    						{
    							id: "validate_outline_none", //$NON-NLS-0$
    							name: messages["outline-none"], //$NON-NLS-0$
    							type: "number", //$NON-NLS-0$
    							defaultValue: warning, //$NON-NLS-0$
    							options: severities //$NON-NLS-0$
    						},
    						{
    							id: "validate_overqualified_elements", //$NON-NLS-0$
    							name: messages["overqualified-elements"], //$NON-NLS-0$
    							type: "number", //$NON-NLS-0$
    							defaultValue: warning, //$NON-NLS-0$
    							options: severities //$NON-NLS-0$
    						},
    						{
    							id: "validate_qualified_headings", //$NON-NLS-0$
    							name: messages["qualified-headings"], //$NON-NLS-0$
    							type: "number", //$NON-NLS-0$
    							defaultValue: warning, //$NON-NLS-0$
    							options: severities //$NON-NLS-0$
    						},
    						{
    							id: "validate_regex_selectors", //$NON-NLS-0$
    							name: messages["regex-selectors"], //$NON-NLS-0$
    							type: "number", //$NON-NLS-0$
    							defaultValue: warning, //$NON-NLS-0$
    							options: severities //$NON-NLS-0$
    						},
    						{
    							id: "validate_rules_count", //$NON-NLS-0$
    							name: messages["rules-count"], //$NON-NLS-0$
    							type: "number", //$NON-NLS-0$
    							defaultValue: warning, //$NON-NLS-0$
    							options: severities //$NON-NLS-0$
    						},
    						{
    							id: "validate_selector_max_approaching", //$NON-NLS-0$
    							name: messages["selector-max-approaching"], //$NON-NLS-0$
    							type: "number", //$NON-NLS-0$
    							defaultValue: warning, //$NON-NLS-0$
    							options: severities //$NON-NLS-0$
    						},
    						{
    							id: "validate_selector_max", //$NON-NLS-0$
    							name: messages["selector-max"], //$NON-NLS-0$
    							type: "number", //$NON-NLS-0$
    							defaultValue: warning, //$NON-NLS-0$
    							options: severities //$NON-NLS-0$
    						},
    						{
    							id: "validate_shorthand", //$NON-NLS-0$
    							name: messages["shorthand"], //$NON-NLS-0$
    							type: "number", //$NON-NLS-0$
    							defaultValue: warning, //$NON-NLS-0$
    							options: severities //$NON-NLS-0$
    						},
    						{
    							id: "validate_star_property_hack", //$NON-NLS-0$
    							name: messages["star-property-hack"], //$NON-NLS-0$
    							type: "number", //$NON-NLS-0$
    							defaultValue: warning, //$NON-NLS-0$
    							options: severities //$NON-NLS-0$
    						},
    						{
    							id: "validate_text_indent", //$NON-NLS-0$
    							name: messages["text-indent"], //$NON-NLS-0$
    							type: "number", //$NON-NLS-0$
    							defaultValue: warning, //$NON-NLS-0$
    							options: severities //$NON-NLS-0$
    						},
    						{
    							id: "validate_underscore_property_hack", //$NON-NLS-0$
    							name: messages["underscore-property-hack"], //$NON-NLS-0$
    							type: "number", //$NON-NLS-0$
    							defaultValue: warning, //$NON-NLS-0$
    							options: severities //$NON-NLS-0$
    						},
    						{
    							id: "validate_unique_headings", //$NON-NLS-0$
    							name: messages["unique-headings"], //$NON-NLS-0$
    							type: "number", //$NON-NLS-0$
    							defaultValue: warning, //$NON-NLS-0$
    							options: severities //$NON-NLS-0$
    						},
    						{
    							id: "validate_universal_selector", //$NON-NLS-0$
    							name: messages["universal-selector"], //$NON-NLS-0$
    							type: "number", //$NON-NLS-0$
    							defaultValue: warning, //$NON-NLS-0$
    							options: severities //$NON-NLS-0$
    						},
    						{
    							id: "validate_unqualified_attributes", //$NON-NLS-0$
    							name: messages["unqualified-attributes"], //$NON-NLS-0$
    							type: "number", //$NON-NLS-0$
    							defaultValue: warning, //$NON-NLS-0$
    							options: severities //$NON-NLS-0$
    						},
    						{
    							id: "validate_vendor_prefix", //$NON-NLS-0$
    							name: messages["vendor-prefix"], //$NON-NLS-0$
    							type: "number", //$NON-NLS-0$
    							defaultValue: warning, //$NON-NLS-0$
    							options: severities //$NON-NLS-0$
    						},
    						{
    							id: "validate_zero_units", //$NON-NLS-0$
    							name: messages["zero-units"], //$NON-NLS-0$
    							type: "number", //$NON-NLS-0$
    							defaultValue: warning, //$NON-NLS-0$
    							options: severities //$NON-NLS-0$
    						}]
    				}]
    		}
    	);
    
    	provider.connect();
	});
});
