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
    'pluginName': 'Orion JavaScript Tool Support',
    'pluginDescription': 'This plugin provides JavaScript tools support for Orion, like editing, search, navigation, validation, and code completion.',
	'error': 'Error',  //$NON-NLS-0$  //$NON-NLS-1$
	'warning' : 'Warning',  //$NON-NLS-0$  //$NON-NLS-1$
	'ignore' : 'Ignore',  //$NON-NLS-0$  //$NON-NLS-1$
	'prefCodeStyle':'Code Style',
	'prefBestPractices':'Best Practices',
	'prefPotentialProblems':'Potential Programming Problems',
	'sourceOutline' : 'Source Outline', //$NON-NLS-0$  //$NON-NLS-1$
	'sourceOutlineTitle': 'JavaScript source outline',  //$NON-NLS-0$  //$NON-NLS-1$
	'contentAssist' : 'JavaScript content assist', //$NON-NLS-0$  //$NON-NLS-1$
	'eslintValidator' : 'JavaScript Validator', //$NON-NLS-0$  //$NON-NLS-1$
	'missingCurly' : 'Statements not enclosed in braces:', //$NON-NLS-0$  //$NON-NLS-1$
	'noCaller' : 'Discouraged \'arguments.caller\' or \'arguments.callee\' use:', //$NON-NLS-0$  //$NON-NLS-1$
	'noCommaDangle' : 'Trailing commas in object expressions:', //$NON-NLS-0$  //$NON-NLS-1$
    'noCondAssign' : 'Assignments in conditional expressions:', //$NON-NLS-0$  //$NON-NLS-1$
    'noConsole' : 'Discouraged console use in browser code:', //$NON-NLS-0$  //$NON-NLS-1$
    'noConstantCondition' : 'Constant as conditional expression:', //$NON-NLS-0$  //$NON-NLS-1$
    'noRegexSpaces' : 'Multiple spaces in regular expressions:', //$NON-NLS-0$  //$NON-NLS-1$
    'noReservedKeys' : 'Reserved words used as property keys:', //$NON-NLS-0$  //$NON-NLS-1$
	'noEqeqeq' : 'Discouraged \'==\' use:', //$NON-NLS-0$  //$NON-NLS-1$
	'noDebugger' : 'Discouraged \'debugger\' statement use:', //$NON-NLS-0$  //$NON-NLS-1$
	'noEval' : 'Discouraged \'eval()\' use:', //$NON-NLS-0$  //$NON-NLS-1$
	'noDupeKeys' : 'Duplicate object keys:', //$NON-NLS-0$  //$NON-NLS-1$
	'noIterator': 'Discouraged __iterator__ property use:', //$NON-NLS-0$  //$NON-NLS-1$
	'useIsNaN' : 'NaN not compared with isNaN():', //$NON-NLS-0$  //$NON-NLS-1$
	'missingDoc' : 'Missing JSDoc:', //$NON-NLS-0$  //$NON-NLS-1$
	'noUnreachable' : 'Unreachable code:', //$NON-NLS-0$  //$NON-NLS-1$
	'noFallthrough' : 'Switch case fall-through:', //$NON-NLS-0$  //$NON-NLS-1$
	'useBeforeDefine' : 'Member used before definition:', //$NON-NLS-0$  //$NON-NLS-1$
	'noEmptyBlock' : 'Undocumented empty block:', //$NON-NLS-0$  //$NON-NLS-1$
	'newParens' : 'Missing parentheses in constructor call:', //$NON-NLS-0$  //$NON-NLS-1$
	'noNewArray': 'Discouraged \'new Array()\':', //$NON-NLS-0$  //$NON-NLS-1$
	'noNewFunc': 'Discouraged \'new Function()\':', //$NON-NLS-0$  //$NON-NLS-1$
	'noNewObject': 'Discouraged \'new Object()\':', //$NON-NLS-0$  //$NON-NLS-1$
	'noNewWrappers': 'Discouraged wrapper objects:', //$NON-NLS-0$  //$NON-NLS-1$
	'missingSemi' : 'Missing semicolons:', //$NON-NLS-0$  //$NON-NLS-1$
	'unusedVars' : 'Unused variables:', //$NON-NLS-0$  //$NON-NLS-1$
	'varRedecl' : 'Variable re-declarations:', //$NON-NLS-0$  //$NON-NLS-1$
	'varShadow': 'Variable shadowing:', //$NON-NLS-0$  //$NON-NLS-1$
	'undefMember' : 'Undeclared global reference:', //$NON-NLS-0$  //$NON-NLS-1$
	'unnecessarySemis' : 'Unnecessary semicolons:', //$NON-NLS-0$  //$NON-NLS-1$
	'unusedParams' : 'Unused parameters:', //$NON-NLS-0$  //$NON-NLS-1$
	'unsupportedJSLint' : 'Unsupported environment directive:',  //$NON-NLS-0$  //$NON-NLS-1$
	'noThrowLiteral': 'Literal used in \'throw\':',  //$NON-NLS-0$  //$NON-NLS-1$
	'generateDocName' : 'Generate Element Comment',  //$NON-NLS-0$  //$NON-NLS-1$
	'generateDocTooltip' : 'Generate a JSDoc-like comment for the selected JavaScript element',  //$NON-NLS-0$  //$NON-NLS-1$
	'openDeclName' : 'Open Declaration',  //$NON-NLS-0$  //$NON-NLS-1$
	'openDeclTooltip' : 'Open the declaration of the selected element',  //$NON-NLS-0$  //$NON-NLS-1$
	'validTypeof': 'Invalid \'typeof\' comparison',  //$NON-NLS-0$ //$NON-NLS-1$
	'noSparseArrays': 'Sparse array declarations', //$NON-NLS-0$ //$NON-NLS-1$
	'jsHover': 'JavaScript Hover Provider', //$NON-NLS-0$ //$NON-NLS-1$
	'removeExtraSemiFixName': 'Remove extra semicolon', //$NON-NLS-0$ //$NON-NLS-1$
	'removeExtraSemiFixTooltip': 'Removes the extra semicolon', //$NON-NLS-0$ //$NON-NLS-1$
	'addFallthroughCommentFixName': 'Add $FALLTHROUGH$ comment', //$NON-NLS-0$ //$NON-NLS-1$
	'addFallthroughCommentFixTooltip': 'Add the $FALLTHROUGH$ line comment', //$NON-NLS-0$ //$NON-NLS-1$
	'addEmptyCommentFixName': 'Comment empty block', //$NON-NLS-0$ //$NON-NLS-1$
	'addEmptyCommentFixTooltip': 'Add a TODO comment to the empty block', //$NON-NLS-0$ //$NON-NLS-1$
	'addESLintEnvFixName': 'Add to eslint-env directive', //$NON-NLS-0$ //$NON-NLS-1$
	'addESLintEnvFixTooltip': 'Add to eslint-env directive to filter the known member', //$NON-NLS-0$ //$NON-NLS-1$
	'addESLintGlobalFixName': 'Add to globals directive', //$NON-NLS-0$ //$NON-NLS-1$
	'addESLintGlobalFixTooltip': 'Add to globals directive to filter the unknown member', //$NON-NLS-0$ //$NON-NLS-1$
	'removeUnusedParamsFixName': 'Remove parameter', //$NON-NLS-0$ //$NON-NLS-1$
	'removeUnusedParamsFixTooltip': 'Remove the unused parameter, keeping side effects', //$NON-NLS-0$ //$NON-NLS-1$
	'commentCallbackFixName': 'Add @callback to function', //$NON-NLS-0$ //$NON-NLS-1$
	'commentCallbackFixTooltip': 'Document the function with @callback, ignoring unused parameters', //$NON-NLS-0$ //$NON-NLS-1$
	'eqeqeqFixName': 'Update operator', //$NON-NLS-0$ //$NON-NLS-1$
	'eqeqeqFixTooltip': 'Update the operator to the expected one', //$NON-NLS-0$ //$NON-NLS-1$
	'unreachableFixName': 'Remove unreachable code', //$NON-NLS-0$ //$NON-NLS-1$
	'unreachableFixTooltip': 'Remove the unreachable code', //$NON-NLS-0$ //$NON-NLS-1$
	'sparseArrayFixName': 'Convert to normal array', //$NON-NLS-0$ //$NON-NLS-1$
	'sparseArrayFixTooltip': 'Remove sparse entries and convert to normal array', //$NON-NLS-0$ //$NON-NLS-1$
	'semiFixName': 'Add missing \';\'', //$NON-NLS-0$ //$NON-NLS-1$
	'semiFixTooltip': 'Add the missing \';\'', //$NON-NLS-0$ //$NON-NLS-1$
	'radix': 'Missing radix parameter to parseInt()', //$NON-NLS-0$ //$NON-NLS-1$
	'unusedVarsUnusedFixName': 'Remove unused variable', //$NON-NLS-0$ //$NON-NLS-1$
	'unusedVarsUnusedFixTooltip': 'Remove the unused variable, keeping side effects', //$NON-NLS-0$ //$NON-NLS-1$
	'unusedFuncDeclFixName': 'Remove unused function', //$NON-NLS-0$ //$NON-NLS-1$
	'unusedFuncDeclFixTooltip': 'Remove the unused function, keeping side effects', //$NON-NLS-0$ //$NON-NLS-1$
	'noCommaDangleFixName': 'Remove extra \',\'', //$NON-NLS-0$ //$NON-NLS-1$
	'noCommaDangleFixTooltip': 'Remove the extra comma', //$NON-NLS-0$ //$NON-NLS-1$
	'addBBreakFixName': 'Add break statement', //$NON-NLS-0$ //$NON-NLS-1$
	'addBBreakFixTooltip': 'Add a break statement to the proceeding line', //$NON-NLS-0$ //$NON-NLS-1$
	'noShadowGlobals': 'Global shadowing:' //$NON-NLS-0$ //$NON-NLS-1$
});
