/*******************************************************************************
 * @license
 * Copyright (c) 2014, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution 
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html). 
 * 
 ******************************************************************************/
/* eslint-env amd */
define({//Default message bundle
	'htmlOutline' : 'HTML Outline', //$NON-NLS-0$  //$NON-NLS-1$
	'htmlHover' : 'HTML Hover', //$NON-NLS-0$  //$NON-NLS-1$
	'cssOutline' : 'CSS Rule Outline', //$NON-NLS-0$  //$NON-NLS-1$
	'htmlContentAssist' : 'HTML Content Assist', //$NON-NLS-0$  //$NON-NLS-1$
	'cssContentAssist' : 'CSS Content Assist', //$NON-NLS-0$  //$NON-NLS-1$
	'cssHover' : 'CSS Hover', //$NON-NLS-0$  //$NON-NLS-1$
	'csslintValidator' : 'CSS Validator', //$NON-NLS-0$  //$NON-NLS-1$
	'pluginName': 'Orion Web Tools Support', //$NON-NLS-0$  //$NON-NLS-1$
	'pluginDescription': 'This plug-in provides web language tools support for Orion, including HTML and CSS.', //$NON-NLS-0$  //$NON-NLS-1$
	'fontHoverExampleText': 'Lorem ipsum dolor...', //$NON-NLS-0$  //$NON-NLS-1$
	
	// Validator Severities
	'ignore' : 'Ignore', //$NON-NLS-0$  //$NON-NLS-1$
	'warning' : 'Warning', //$NON-NLS-0$  //$NON-NLS-1$
	'error' : 'Error', //$NON-NLS-0$  //$NON-NLS-1$
	
	// CSS Validator Settings
	'adjoining-classes': 'Disallow adjoining classes:', //$NON-NLS-0$  //$NON-NLS-1$
	'box-model': 'Beware of broken box size:', //$NON-NLS-0$  //$NON-NLS-1$
	'box-sizing': 'Disallow use of box-sizing:', //$NON-NLS-0$  //$NON-NLS-1$
	'bulletproof-font-face': 'Use the bulletproof @font-face syntax:', //$NON-NLS-0$  //$NON-NLS-1$
	'compatible-vendor-prefixes': 'Require compatible vendor prefixes:', //$NON-NLS-0$  //$NON-NLS-1$
	'display-property-grouping': 'Require properties appropriate for display:', //$NON-NLS-0$  //$NON-NLS-1$
	'duplicate-background-images': 'Disallow duplicate background images:', //$NON-NLS-0$  //$NON-NLS-1$
	'duplicate-properties': 'Disallow duplicate properties:', //$NON-NLS-0$  //$NON-NLS-1$
	'empty-rules': 'Disallow empty rules:', //$NON-NLS-0$  //$NON-NLS-1$
	'fallback-colors': 'Require fallback colors:', //$NON-NLS-0$  //$NON-NLS-1$
	'floats': 'Disallow too many floats:', //$NON-NLS-0$  //$NON-NLS-1$
	'font-faces': 'Don\'t use too many web fonts:', //$NON-NLS-0$  //$NON-NLS-1$
	'font-sizes': 'Disallow too many font sizes:', //$NON-NLS-0$  //$NON-NLS-1$
	'gradients': 'Require all gradient definitions:', //$NON-NLS-0$  //$NON-NLS-1$
	'ids': 'Disallow IDs in selectors:', //$NON-NLS-0$  //$NON-NLS-1$
	'import': 'Disallow @import:', //$NON-NLS-0$  //$NON-NLS-1$
	'important': 'Disallow !important:', //$NON-NLS-0$  //$NON-NLS-1$
	'known-properties': 'Require use of known properties:', //$NON-NLS-0$  //$NON-NLS-1$
	'outline-none': 'Disallow outline: none:', //$NON-NLS-0$  //$NON-NLS-1$
	'overqualified-elements': 'Disallow overqualified elements:', //$NON-NLS-0$  //$NON-NLS-1$
	'qualified-headings': 'Disallow qualified headings:', //$NON-NLS-0$  //$NON-NLS-1$
	'regex-selectors': 'Disallow selectors that look like regexs:', //$NON-NLS-0$  //$NON-NLS-1$
	'rules-count': 'Rules Count:', //$NON-NLS-0$  //$NON-NLS-1$
	'selector-max-approaching': 'Warn when approaching the 4095 selector limit for IE:', //$NON-NLS-0$  //$NON-NLS-1$
	'selector-max': 'Error when past the 4095 selector limit for IE:', //$NON-NLS-0$  //$NON-NLS-1$
	'shorthand': 'Require shorthand properties:', //$NON-NLS-0$  //$NON-NLS-1$
	'star-property-hack': 'Disallow properties with a star prefix:', //$NON-NLS-0$  //$NON-NLS-1$
	'text-indent': 'Disallow negative text-indent:', //$NON-NLS-0$  //$NON-NLS-1$
	'underscore-property-hack': 'Disallow properties with an underscore prefix:', //$NON-NLS-0$  //$NON-NLS-1$
	'unique-headings': 'Headings should only be defined once:', //$NON-NLS-0$  //$NON-NLS-1$
	'universal-selector': 'Disallow universal selector:', //$NON-NLS-0$  //$NON-NLS-1$
	'unqualified-attributes': 'Disallow unqualified attribute selectors:', //$NON-NLS-0$  //$NON-NLS-1$
	'vendor-prefix': 'Require standard property with vendor prefix:', //$NON-NLS-0$  //$NON-NLS-1$
	'zero-units': 'Disallow units for 0 values:', //$NON-NLS-0$  //$NON-NLS-1$
	
	// CSS Quick Fixes
	'quickfix-empty-rules': 'Remove the rule.', //$NON-NLS-0$  //$NON-NLS-1$
	'quickfix-important': 'Remove \'!important\' annotation.', //$NON-NLS-0$  //$NON-NLS-1$
	'quickfix-zero-units': 'Remove \'px\' qualifier.', //$NON-NLS-0$  //$NON-NLS-1$
});
