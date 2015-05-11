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
/*eslint-env amd*/
define([
	"eslint",
	"orion/objects",
	"javascript/astManager",
	"javascript/finder",
	'javascript/compilationUnit',
	"orion/i18nUtil",
	"i18n!javascript/nls/problems",
	"orion/metrics"
], function(eslint, Objects, ASTManager, Finder, CU, i18nUtil, messages, Metrics) {
	var config = {
		// 0:off, 1:warning, 2:error
		defaults: {
			"curly" : 0, //$NON-NLS-0$
			"eqeqeq": 1, //$NON-NLS-0$
			"missing-doc": 0,  //$NON-NLS-0$
			"new-parens" : 1, //$NON-NLS-0$
			"no-caller": 1, //$NON-NLS-0$
			"no-cond-assign": 2, //$NON-NLS-0$
			"no-comma-dangle": 0,  //$NON-NLS-0$
			"no-console": 2,  //$NON-NLS-0$
			"no-constant-condition": 2,  //$NON-NLS-0$"
			"no-debugger" : 1, //$NON-NLS-0$
			"no-dupe-keys" : 2, //$NON-NLS-0$
			"no-eval" : 0, //$NON-NLS-0$
			"no-extra-semi": 1, //$NON-NLS-0$
			"no-iterator": 2,  //$NON-NLS-0$
			'no-jslint' : 1,  //$NON-NLS-0$
			"no-new-array": 1, //$NON-NLS-0$
			"no-new-func": 1, //$NON-NLS-0$
			"no-new-object": 1, //$NON-NLS-0$
			"no-new-wrappers": 1, //$NON-NLS-0$
			"no-redeclare": 1, //$NON-NLS-0$
			"no-reserved-keys": 2, //$NON-NLS-0$
			"no-regex-spaces": 2, //$NON-NLS-0$
			"no-shadow": 1, //$NON-NLS-0$
			"no-shadow-global": 1, //$NON-NLS-0$
			"no-throw-literal": 1, //$NON-NLS-0$
			"no-undef": 2, //$NON-NLS-0$
			"no-unused-params": 1, //$NON-NLS-0$
			"no-unused-vars": 1, //$NON-NLS-0$
			"no-use-before-define": 1, //$NON-NLS-0$
			"radix": 1, //$NON-NLS-0$
			"semi": 1, //$NON-NLS-0$
			"use-isnan" : 2, //$NON-NLS-0$
			'no-unreachable': 2,  //$NON-NLS-0$
			'no-fallthrough' : 2,  //$NON-NLS-0$
			'no-empty-block' : 0,  //$NON-NLS-0$
			'valid-typeof': 2,  //$NON-NLS-0$
			'no-sparse-arrays': 1  //$NON-NLS-0$
		},
		
		/**
		 * @description Sets the given rule to the given enabled value
		 * @function
		 * @private
		 * @param {String} ruleId The id of the rule to change
		 * @param {Number} value The value to set the rule to
		 * @param {Object} [key] Optional key to use for complex rule configuration.
		 */
		setOption: function(ruleId, value, key) {
			if (typeof value === "number") {
				if(Array.isArray(this.rules[ruleId])) {
					var ruleConfig = this.rules[ruleId];
					if (key) {
						ruleConfig[1] = ruleConfig[1] || {};
						ruleConfig[1][key] = value;
					} else {
						ruleConfig[0] = value;
					}
				}
				else {
					this.rules[ruleId] = value;
				}
			}
		},
		
		setDefaults: function setDefaults() {
		    this.rules = Object.create(null);
		    var keys = Object.keys(this.defaults);
		    for(var i = 0; i < keys.length; i++) {
		        var key = keys[i];
		        this.rules[key] = this.defaults[key];
		    }
		}
	};

	/**
	 * @description Creates a new ESLintValidator
	 * @constructor
	 * @public
	 * @param {javascript.ASTManager} astManager The AST manager backing this validator
	 * @returns {ESLintValidator} Returns a new validator
	 */
	function ESLintValidator(astManager) {
		this.astManager = astManager;
		config.setDefaults();
	}
	
	/**
	 * @description Converts the configuration rule value to an Orion problem severity string. One of 'warning', 'error'.
	 * @param {Object} prob The problem object
	 * @returns {String} the severity string
	 */
	function getSeverity(prob) {
		var val = 2;
		var ruleConfig = config.rules[prob.ruleId];
		if(Array.isArray(ruleConfig)) {
			// Hack for missing-doc which overloads the prob.related object to expose which subrule
			// generated the problem
			var related = prob.related, ruleType = related && related.type;
			if (prob.ruleId === "missing-doc" && ruleConfig[1][ruleType] !== undefined) {
				val = ruleConfig[1][ruleType];
			} else {
				val = ruleConfig[0];
			}
		}
		else {
			val = ruleConfig;
		}
		switch (val) {
			case 1: return "warning"; //$NON-NLS-0$
			case 2: return "error"; //$NON-NLS-0$
		}
		return "error"; //$NON-NLS-0$
	}
	
	/**
	 * @description Computes the problem id to use in the framework from the ESLint problem object
	 * @param {Object} pb The original ESLint problem
	 * @returns {String} The problem id to pass into the framework
	 * @since 8.0
	 */
	function getProblemId(pb) {
	    if(pb.args) {
	        if(pb.args.pid) {
	            return pb.args.pid;
	        } else if(pb.args.nls) {
	            return pb.args.nls;
	        }
	    }
	    return pb.ruleId;
	}
	
	/**
	 * @description Converts an eslint / esprima problem object to an Orion problem object
	 * @public
	 * @param {eslint.Error|esprima.Error} e Either an eslint error or an esprima parse error.
	 * @returns {Object} Orion Problem object
	 */
	function toProblem(e) {
		var start = e.start, end = e.end;
		if (e.node) {
			// Error produced by eslint
			start = e.node.range[0];
			end = e.node.range[1];
			if (e.related && e.related.range) {
				// Flagging the entire node is distracting. Just flag the bad token.
				var relatedToken = e.related;
				start = relatedToken.range[0];
				end = relatedToken.range[1];
			}
		}
		var descriptionKey = e.args && e.args.nls ? e.args.nls : e.ruleId;
		var descriptionArgs = e.args || Object.create(null);
		var description = e.message;
		if (descriptionKey && messages[descriptionKey]) {
           description = i18nUtil.formatMessage.call(null, messages[descriptionKey], descriptionArgs);
		}
		var prob = {
		    id: getProblemId(e),
			description: description,
			severity: getSeverity(e),
		};
		if(typeof(start) !== 'undefined') {
		    prob.start = start;
		    prob.end = end;
		} else if(typeof(e.lineNumber) !== 'undefined') {
		    prob.line = e.lineNumber;
		    prob.start = e.column;
		} else {
		    prob.start = 0;
		    prob.end = 0;
		}
		if(e.opts && e.opts.args) {
		    prob.problemArgs = e.opts.args;
		}
		return prob;
	}

	Objects.mixin(ESLintValidator.prototype, {
		/**
		 * @description Extracts any errors captured by the tolerant Esprima parser and returns them
		 * @function
		 * @private
		 * @param {esprima.AST} ast The AST
		 * @returns {esprima.Error[]} The array of AST errors (if any)
		 */
		_extractParseErrors: function(ast) {
			var errors = [], errorMap = Object.create(null);
			var asterrors = ast.errors;
			if(asterrors) {
				var len = asterrors.length;
				for(var i = 0; i < len; i++) {
					var error = asterrors[i];
					var token = null;
					if(error.end && error.token) {
						token = {range: [error.index, error.end], value: error.token};
					}
					else if(ast.tokens.length > 0) {
						//error object did not contain the token infos, try to find it
						token = Finder.findToken(error.index, ast.tokens);	
					}
					var msg = error.message;
					if(errorMap[error.index] === msg) {
						continue;
					}
					errorMap[error.index] = msg;
					if(error.type) {
						switch(error.type) {
							case ASTManager.ErrorTypes.Unexpected:
							    if(token) {
    								error.args = {0: token.value, nls: "syntaxErrorBadToken"}; //$NON-NLS-0$
    								error.message = msg = error.args.nls;
								}
								break;
							case ASTManager.ErrorTypes.EndOfInput:
								error.args = {nls: "syntaxErrorIncomplete"}; //$NON-NLS-0$
								error.message = error.args.nls;
								break;
						}
					} else if(!error.token) {
					    //an untyped error with no tokens, report the failure
					    error.args = {0: error.message, nls: 'esprimaParseFailure'};
					    error.message = error.args.nls;
					    //use the line number / column
				       delete error.start;
				       delete error.end;
					}
					if(token) {
					   error.node = token;
					}
					errors.push(error);
				}
			}
			return errors;
		},
		/**
		 * @description Callback to create problems from orion.edit.validator
		 * @function
		 * @public
		 * @param {orion.edit.EditorContext} editorContext The editor context
		 * @param {Object} context The in-editor context (selection, offset, etc)
		 * @returns {orion.Promise} A promise to compute some problems
		 */
		computeProblems: function(editorContext /*, context*/) {
			var _self = this;
			return editorContext.getFileMetadata().then(function(meta) {
			    if(meta.contentType.id === 'text/html') {
			        return editorContext.getText().then(function(text) {
			            var blocks = Finder.findScriptBlocks(text);
			            if(blocks && blocks.length > 0) {
			                var cu = new CU(blocks, meta);
			                return _self.astManager.getAST(cu.getEditorContext()).then(function(ast) {
			                    //auto-assume browser env - https://bugs.eclipse.org/bugs/show_bug.cgi?id=458676
			                    var env = Object.create(null);
			                    env.browser = true;
            					return _self._validateAst(ast, env);
            				});
			            }
			        });
			    } else {
    			    return _self.astManager.getAST(editorContext).then(function(ast) {
    					return _self._validateAst(ast);
    				});
				}
			});
		},
		
		/**
		 * @description Validates the given AST
		 * @function
		 * @private
		 * @param {Object} ast The AST
		 * @param {Object} env An environment object to set in the config
		 * @returns {Array|Object} The array of problem objects
		 * @since 6.0
		 */
		_validateAst: function(ast, env) {
			var eslintErrors = [], 
				parseErrors = this._extractParseErrors(ast);
				var start = Date.now();
			try {
			    config.env = env;
				eslintErrors = eslint.verify(ast, config);
			} catch (e) {
				if(parseErrors.length < 1) {
					eslintErrors.push({
						start: 0,
						args: {0: e.toString(), nls: "eslintValidationFailure" }, //$NON-NLS-0$
						severity: "error" //$NON-NLS-0$
					});
				}
			}
			var end = Date.now() - start;
			Metrics.logTiming('language tools', 'validation', end, 'application/javascript');
			return { problems: this._filterProblems(parseErrors, eslintErrors).map(toProblem) };
		},
		
		/**
		 * @description Post-processes the ESLint generated problems to determine if there are any linting issues reported for the same 
		 * nodes as parse errors. If there are we discard those problems.
		 * @function
		 * @private
		 * @param {Array} parseErrors The array of parse errors, never <code>null</code>
		 * @param {Array} eslintErrors The array of eslint computed errors, never <code>null</code>
		 * @returns {Array} The filtered list of errors to report to the editor
		 * @since 6.0
		 */
		_filterProblems: function(parseErrors, eslintErrors) {
			var len = parseErrors.length;
			if(len < 1) {
				return eslintErrors;
			}
			var filtered = [].concat(parseErrors);
			var len2 = eslintErrors.length;
			filter: for(var i = 0; i < len2; i++) {
				var ee = eslintErrors[i];
				for(var j = 0; j < len; j++) {
					var pe = parseErrors[j];
					var node = ee.node;
					if(node && node.range[0] >= pe.index && node.range[0] <= pe.end) {
						continue filter;
					}
				}
				filtered.push(ee);
			}
			return filtered;
		},
		
		/**
		 * @description Callback from orion.cm.managedservice
		 * @function
		 * @public
		 * @param {Object} properties The properties that have been changed
		 */
		updated: function(properties) {
			if (!properties) {
				return;
			}
			var oldconfig = properties.pid === 'eslint.config';
			var keys = Object.keys(properties);
			var seen = Object.create(null);
			for(var i = 0; i < keys.length; i++) {
			    var key = keys[i];
			    var ruleId = key;
			    if(oldconfig && config.rules[key] !== config.defaults[key]) {
			        //don't overwrite a new setting with an old one
		            continue;
			    }
			    var legacy = this._legacy[ruleId];
			    if(typeof(legacy) === 'string') {
			        ruleId = legacy;
			        if(seen[ruleId]) {
			            //don't overwrite a new pref name with a legacy one
			            continue;
			        }
			    }
			    seen[ruleId] = true;
			    config.setOption(ruleId, properties[key]);
			}
		},
		
		/**
		 * @description Hook for the test suite to enable only the given rule
		 * @function
		 * @private
		 * @param {String} ruleid The id for the rule
		 * @param {Number} severity The desired severity or null
		 * @param {String} opts Option for a given rule, for example the missing-doc rule has 'decl' or 'expr'
		 * @since 8.0
		 */
		_enableOnly: function _enableOnly(ruleid, severity, opts) {
		    var keys = Object.keys(config.rules);
		    for(var i = 0; i < keys.length; i++) {
		        if(keys[i] === ruleid) {
		            config.setOption(ruleid, severity ? severity : 2, opts);
		        } else {
		            config.setOption(keys[i], 0);
		        }
		    }
		},
		
		/**
		 * All new pref ids MUST be the id of the rule they are for, but to 
		 * not break existing prefs this object translates the old pref name to its rule name
		 * @private 
		 * @since 8.0
		 */
		_legacy: {
		    'throw-error': 'no-throw-literal',
		    validate_no_cond_assign: 'no-cond-assign',
		    validate_no_constant_condition: 'no-constant-condition',
		    validate_no_caller: 'no-caller',
		    validate_eqeqeq: 'eqeqeq',
		    validate_no_console: 'no-console',
		    validate_debugger: 'no-debugger',
		    validate_eval: 'no-eval',
		    validate_no_iterator:'no-iterator',
		    validate_dupe_obj_keys: 'no-dupe-keys',
		    validate_typeof: 'valid-typeof',
		    validate_use_before_define: 'no-use-before-define',
		    validate_new_parens: 'new-parens',
		    validate_radix: 'radix',
		    validate_missing_semi: 'semi',
		    validate_no_regex_spaces: 'no-spaces-regex',
		    validate_use_isnan: 'use-isnan',
		    validate_throw_error: 'no-throw-literal',
		    validate_no_reserved_keys: 'no-reserved-keys',
		    validate_no_sparse_arrays: 'no-sparse-arrays',
		    validate_curly: 'curly',
		    validate_no_fallthrough: 'no-fallthrough',
		    validate_no_comma_dangle: 'no-comma-dangle',
		    validate_no_undef: 'no-undef',
		    validate_no_empty_block: 'no-empty-block',
		    validate_unnecessary_semi: 'no-extra-semi',
		    validate_no_jslint: 'no-jslint',
		    validate_unused_params: 'no-unused-params',
		    validate_no_unused_vars: 'no-unused-vars',
		    validate_no_unreachable: 'no-unreachable',
		    validate_no_redeclare: 'no-redeclare',
		    validate_no_shadow: 'no-shadow'
		}
		
	});
	return ESLintValidator;
});