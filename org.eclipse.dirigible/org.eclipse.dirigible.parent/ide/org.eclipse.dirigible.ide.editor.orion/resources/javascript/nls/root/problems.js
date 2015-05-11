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
define({
    'syntaxErrorIncomplete': 'Syntax error, incomplete statement.',  //$NON-NLS-0$  //$NON-NLS-1$
    'syntaxErrorBadToken': 'Syntax error on token \'${0}\', delete this token.',  //$NON-NLS-0$  //$NON-NLS-1$
    'esprimaParseFailure': 'Esprima failed to parse this file because an error occurred: ${0}',  //$NON-NLS-0$ //$NON-NLS-1$
    'eslintValidationFailure': 'ESLint failed to validate this file because an error occurred: ${0}',  //$NON-NLS-0$  //$NON-NLS-1$
	'curly': 'Statement should be enclosed in braces.',  //$NON-NLS-0$  //$NON-NLS-1$
	'eqeqeq' : 'Expected \'${0}\' and instead saw \'${1}\'.',  //$NON-NLS-0$  //$NON-NLS-1$
	'missing-doc' : 'Missing documentation for function \'${0}\'.',  //$NON-NLS-0$  //$NON-NLS-1$
	'new-parens' : 'Missing parentheses invoking constructor.', //$NON-NLS-0$  //$NON-NLS-1$
	'no-caller': '\'arguments.${0}\' is deprecated.', //$NON-NLS-0$  //$NON-NLS-1$
	'no-comma-dangle': 'Trailing commas in object expressions are discouraged.',  //$NON-NLS-0$ //$NON-NLS-1$
	'no-cond-assign': 'Expected a conditional expression and instead saw an assignment.',  //$NON-NLS-0$ //$NON-NLS-1$
	'no-console': 'Discouraged use of console in browser-based code.', //$NON-NLS-0$ //$NON-NLS-1$
	'no-constant-condition': 'Discouraged use of constant as a conditional expression.', //$NON-NLS-0$ //$NON-NLS-1$
	'no-debugger': '\'debugger\' statement use is discouraged.',  //$NON-NLS-0$  //$NON-NLS-1$
	'no-dupe-keys' : 'Duplicate object key \'${0}\'.', //$NON-NLS-0$  //$NON-NLS-1$
	'no-empty-block' : 'Empty block should be removed or commented.',  //$NON-NLS-0$ //$NON-NLS-1$
	'no-eval' : '${0} function calls are discouraged.', //$NON-NLS-0$  //$NON-NLS-1$
	'no-extra-semi' : 'Unnecessary semicolon.', //$NON-NLS-0$  //$NON-NLS-1$
	'no-fallthrough' : 'Switch case may be entered by falling through the previous case.', //$NON-NLS-0$  //$NON-NLS-1$
	'no-iterator' : 'Discouraged __iterator__ property use.', //$NON-NLS-0$  //$NON-NLS-1$
	'no-jslint' : 'The \'${0}\' directive is unsupported, please use eslint-env.', //$NON-NLS-0$  //$NON-NLS-1$
	'no-new-array' : 'Use the array literal notation \'[]\'.', //$NON-NLS-0$  //$NON-NLS-1$
	'no-new-func' : 'The Function constructor is eval.', //$NON-NLS-0$  //$NON-NLS-1$
	'no-new-object' : 'Use the object literal notation \'{}\' or Object.create(null).', //$NON-NLS-0$  //$NON-NLS-1$
	'no-new-wrappers' : 'Do not use \'${0}\' as a constructor.', //$NON-NLS-0$  //$NON-NLS-1$
	'no-redeclare' : '\'${0}\' is already defined.', //$NON-NLS-0$  //$NON-NLS-1$
	'no-regex-spaces' : 'Avoid multiple spaces in regular expressions. Use \' {${0}}\' instead.', //$NON-NLS-0$  //$NON-NLS-1$
	'no-reserved-keys' : 'Reserved words should not be used as property keys.', //$NON-NLS-0$  //$NON-NLS-1$
	'no-shadow' : '\'${0}\' is already declared in the upper scope.', //$NON-NLS-0$  //$NON-NLS-1$
	'no-sparse-arrays': 'Sparse array declarations should be avoided.',  //$NON-NLS-0$ //$NON-NLS-1$
	'no-throw-literal': 'Throw an Error instead.', //$NON-NLS-0$  //$NON-NLS-1$
	'no-undef-defined' : '\'${0}\' is undefined.', //$NON-NLS-0$  //$NON-NLS-1$
	'no-undef-readonly': '\'${0}\' is readonly.',  //$NON-NLS-0$  //$NON-NLS-1$
	'no-unreachable' : 'Unreachable code.', //$NON-NLS-0$  //$NON-NLS-1$
	'no-unused-params' : 'Parameter \'${0}\' is never used.', //$NON-NLS-0$  //$NON-NLS-1$
	'no-unused-vars-unused' : '\'${0}\' is unused.', //$NON-NLS-0$  //$NON-NLS-1$
	'no-unused-vars-unused-funcdecl' : 'Function \'${0}\' is unused.', //$NON-NLS-0$  //$NON-NLS-1$
	'no-unused-vars-unread' : '\'${0}\' is unread.', //$NON-NLS-0$  //$NON-NLS-1$
	'no-use-before-define': '\'${0}\' was used before it was defined.', //$NON-NLS-0$  //$NON-NLS-1$
	'radix': 'Missing radix parameter.', //$NON-NLS-0$  //$NON-NLS-1$
	'semi': 'Missing semicolon.', //$NON-NLS-0$  //$NON-NLS-1$
	'use-isnan': 'Use the isNaN function to compare with NaN.', //$NON-NLS-0$  //$NON-NLS-1$
	'valid-typeof' : 'Invalid typeof comparison.',  //$NON-NLS-0$ //$NON-NLS-1$
	'no-shadow-global' : 'Variable \'${0}\' shadows a global member.',  //$NON-NLS-0$ //$NON-NLS-1$
	'no-shadow-global-param' : 'Parameter \'${0}\' shadows a global member.'  //$NON-NLS-0$ //$NON-NLS-1$
});
