/*******************************************************************************
 * @license
 * Copyright (c) 2012, 2015 VMware, Inc. and others.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution 
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html).
 *
 * Contributors:
 *     Andrew Eisenberg (VMware) - initial API and implementation
 *     IBM Corporation - Various improvements
 ******************************************************************************/

/*
This module contains functions for manipulating internal type signatures and
other utility functions related to types.
*/
/*eslint-env es5, browser, amd*/
/*global doctrine*/
define([
'logger',
'doctrine' //stays last, exports into global scope
], function(Logger) {
    
    /**
	 * @description The Definition class refers to the declaration of an identifier.
	 * The start and end are locations in the source code.
	 * Path is a URL corresponding to the document where the definition occurs.
	 * If range is undefined, then the definition refers to the entire document
	 * Range is a two element array with the start and end values
	 * (Exactly the same range field as is used in Esprima)
	 * If the document is undefined, then the definition is in the current document.
	 *
	 * @param String typeName
	 * @param {[Number]} range
	 * @param String path
	 */
	function Definition(typeObj, range, path) {
		this._typeObj = ensureTypeObject(typeObj);
		this.range = range;
		this.path = path;
	}

    Definition.prototype = {
		set typeObj(val) {
			var maybeObj = val;
			if (typeof maybeObj === 'string') {
				maybeObj = ensureTypeObject(maybeObj);
			}
			this._typeObj = maybeObj;
		},

		get typeObj() {
			return this._typeObj;
		}
	};

    /**
	 * @description Revivies a Definition object from a regular object
	 * @param {Object} obj The type object to revive
	 * @returns {Definition} The revived definition
	 */
    Definition.revive = function(obj) {
		var defn = new Definition();
		for (var prop in obj) {
			if (obj.hasOwnProperty(prop)) {
				if (prop === 'typeSig') {
					defn.typeObj = obj[prop];
				} else {
					defn[prop] = obj[prop];
				}
			}
		}
		return defn;
	};

    var THE_UNKNOWN_TYPE = createNameType("Object");
	var JUST_DOTS = '$$__JUST_DOTS__$$';
	var JUST_DOTS_TYPE = createNameType(JUST_DOTS);
	var JUST_DOTS_REGEX = /\$\$__JUST_DOTS__\$\$/g;
	var UNDEFINED_OR_EMPTY_OBJ = /:undefined|:\{\}/g;
	var GEN_NAME = "gen~";
	var PROTO_LENGTH = "~proto".length;

	/**
	 * @description Doctrine closure compiler style type objects
	 * @param {String} signature The Doctrine-style signature to parse
	 * @returns {Object} The Doctrine-style type object
	 */
	function ensureTypeObject(signature) {
		if (!signature) {
			return signature;
		}
		if (signature.type) {
			return signature;
		}
		try {
			return doctrine.parseParamType(signature);
		} catch(e) {
			Logger.error("doctrine failure to parse: " + signature);
			return {};
		}
	}

	/**
	 * @description Returns if the given character is upper case or not considering the locale
	 * @param {String} string A string of at least one char14acter
	 * @return {Boolean} True iff the first character of the given string is uppercase
	 */
	function isUpperCaseChar(string) {
		if (string.length < 1) {
			return false;
		}
		if (isNaN(string.charCodeAt(0))) {
			return false;
		}
		return string.toLocaleUpperCase().charAt(0) === string.charAt(0);
	}

	/**
	 * @description Returns a new NameExpression object
	 * @param {String} name The name to wrap
	 * @returns {Object} The new NameExpression object
	 */
	function createNameType(name) {
	    if (typeof name !== 'string') {
	        throw new Error('Expected string, but found: ' + JSON.parse(name));
	    }
		return { type: 'NameExpression', name: name };
	}
	
	/**
	 * @description Returns if the generated type name refers to the general object or is undefined
	 * @param {String} generatedTypeName The type name to check
	 * @param {Object} allTypes The type object
         * @param {Object} visited types visited this far during empty check
	 * @returns {Boolean} True if the generated type is 'Object' or 'undefined'
	 */
	function isEmpty(generatedTypeName, allTypes, visited) {
		if (typeof generatedTypeName !== 'string') {
			// original type was not a name expression
			return false;
		} else if (generatedTypeName === "Object" || generatedTypeName === "undefined") {
			return true;
		} else if (generatedTypeName.substring(0, GEN_NAME.length) !== GEN_NAME) {
			// not a synthetic type, so not empty
			return false;
		}

		// check for cycles in prototype chain
		if (visited) {
			if (visited[generatedTypeName]) {
				// cycle!
				return true;
			}
		} else {
			visited = {};
		}
		visited[generatedTypeName] = true;

		// now check to see if there are any non-default fields in this type
		var type = allTypes[generatedTypeName];
		var popCount = 0;
		// type should have a $$proto only and nothing else if it is empty
		for (var property in type) {
			if (type.hasOwnProperty(property)) {
				popCount++;
				if (popCount > 1) {
					break;
				}
			}
		}
		if (popCount === 1) {
			// we have an empty object literal, must check parent
			// must traverse prototype hierarchy to make sure empty
			return isEmpty(type.$$proto.typeObj.name, allTypes, visited);
		}
		return false;
	}

    /**
	 * @description Converts the type name to a number
	 * @function
	 * @param {String} typeName The name of the type
	 * @param {Object} env The type environment
	 * @returns {Number} The number corresponding to the type
	 */
	function convertToNumber(typeName, env) {
		if (typeName === "undefined") {
			return 0;
		} else if (typeName === "Object") {
			return 1;
		} else if (isEmpty(typeName, env.getAllTypes())) {
			return 2;
		} else {
			return 3;
		}
	}

	/**
	 * Determines if the left type name is more general than the right type name.
	 * Generality (>) is defined as follows:
	 * undefined > Object > Generated empty type > all other types
	 *
	 * A generated empty type is a generated type that has only a $$proto property
	 * added to it.  Additionally, the type specified in the $$proto property is
	 * either empty or is Object
	 *
	 * @param {Object} leftTypeObj
	 * @param {Object} rightTypeObj
	 * @param {Object} env
	 *
	 * @return Boolean
	 */
	function leftTypeIsMoreGeneral(leftTypeObj, rightTypeObj, env) {
		var leftTypeName = leftTypeObj.name, rightTypeName = rightTypeObj.name;

		if (!leftTypeName) {
			if (leftTypeObj.type === 'NullLiteral' || leftTypeObj.type === 'UndefinedLiteral' || leftTypeObj.type === 'VoidLiteral') {
				leftTypeName = 'undefined';
			}
		}
		
		if (!rightTypeName) {
			return false;
		}

		var leftNum = convertToNumber(leftTypeName, env);
		// avoid calculating the rightNum if possible
		if (leftNum === 0) {
			return rightTypeName !== "undefined";
		} else if (leftNum === 1) {
			return rightTypeName !== "undefined" && rightTypeName !== "Object";
		} else if (leftNum === 2) {
			return rightTypeName !== "undefined" && rightTypeName !== "Object" && !isEmpty(rightTypeName, env.getAllTypes());
		} else {
			return false;
		}
	}

    /**
	 * @description Find the global objects given the AST comments and the lint options
	 * @function
	 * @private
	 * @param {Array} comments The array of comment nodes from the AST
	 * @param {Object} lintOptions The lint options
	 * @since 8.0
	 */
	function findGlobalObject(comments, lintOptions) {
		for (var i = 0; i < comments.length; i++) {
			var comment = comments[i];
			var value = comment.value.trim();
			if (comment.type === "Block" && value.match(/^(?:jslint|jshint|eslint-env).*/)) {
				// the lint options section.  now look for the browser or node
				if (value.match(/^(?:jslint|jshint)\s+.*(?:browser|amd)\s*:\s*true(?:\s+|$).*/) || 
				    value.match(/^eslint-env\s+.*(?:browser|amd)(?:\s+|$).*/)) {
					return "Window";
				} else if (value.match(/^(?:jslint|jshint)\s+.*node\s*:\s*true(?:\s+|$).*/) ||
				           value.match(/^eslint-env\s+.*node(?:\s+|$).*/)) {
					return "Module";
				} else {
					return "Global";
				}
			}
		}
		if (lintOptions && lintOptions.options) {
			if (lintOptions.options.browser === true || lintOptions.options.amd === true) {
				return "Window";
			} else if (lintOptions.options.node === true) {
				return "Module";
			}
		}
		return "Global";
	}

	return {
		Definition : Definition,

		// now some functions that handle types signatures, styling, and parsing

		/** constant that defines generated type name prefixes */
		GEN_NAME : GEN_NAME,

		// type parsing
		isArrayType : function(typeObj) {
			return typeObj.type === 'ArrayType' || typeObj.type === 'TypeApplication';
		},

		isFunctionOrConstructor : function(typeObj) {
			return typeObj.type === 'FunctionType';
		},

		isPrototypeName : function(typeName) {
			return typeName.slice(typeName.length - PROTO_LENGTH, PROTO_LENGTH) === "~proto";
		},

		/**
		 * returns a parameterized array type with the given type parameter
		 */
		parameterizeArray : function(parameterTypeObj) {
			return {
				type: 'ArrayType',
				elements: [parameterTypeObj]
			};
		},

		/**
		 * return a TypeApplication of Array
		 */
		createAppliedArray: function(applicationTypeObj) {
			return {
				type: 'TypeApplication',
				applications: [
					applicationTypeObj
				],
				expression: createNameType('Array')
			};
		},

		createFunctionType : function(params, result, isConstructor) {
			var functionTypeObj = {
				type: 'FunctionType',
				params: params,
				result: result
			};
			if (isConstructor) {
				functionTypeObj.params = functionTypeObj.params || [];
			    // TODO should we also do 'this'?
				functionTypeObj.params.push({
					type: 'ParameterType',
					name: 'new',
					expression: result
				});
			}
			return functionTypeObj;
		},

		/**
		 * If this is a parameterized array type, then extracts the type,
		 * Otherwise object
		 */
		extractArrayParameterType : function(arrayObj) {
			var elts;
			if (arrayObj.type === 'TypeApplication') {
				if (arrayObj.expression.name === 'Array') {
					elts = arrayObj.applications;
				} else {
					return arrayObj.expression;
				}
			} else if (arrayObj.type === 'ArrayType') {
				elts = arrayObj.elements;
			} else {
				// not an array type
				return arrayObj;
			}
			if (elts.length > 0) {
				return elts[0];
			} else {
				return THE_UNKNOWN_TYPE;
			}
		},

		extractReturnType : function(fnType) {
			return fnType.result || (fnType.type === 'FunctionType' ? this.UNDEFINED_TYPE: fnType);
		},

		// TODO should we just return a typeObj here???
		parseJSDocComment : function(docComment) {
			var result = { };
			result.params = {};
			if (docComment) {
				var commentText = docComment.value;
				if (!commentText) {
					return result;
				}
				try {
					var rawresult = doctrine.parse(commentText, {recoverable:true, unwrap : true, tags : ['param', 'type', 'return', 'returns']});
					// transform result into something more manageable
					var rawtags = rawresult.tags;
					if (rawtags) {
						for (var i = 0; i < rawtags.length; i++) {
							switch (rawtags[i].title) {
								case "typedef":
								case "define":
								case "type": {
									result.type = rawtags[i].type;
									break;
								}
								case "return": 
								case "returns": {
									result.rturn = rawtags[i].type;
									break;
								}
								case "param": {
									// remove square brackets
									var name = rawtags[i].name;
									if (name.charAt(0) === '[' && name.charAt(name.length -1) === ']') {
										name = name.substring(1, name.length-1);
									}
									result.params[name] = rawtags[i].type;
									break;
								}
							}
						}
					}
				} catch (e) {
					//TODO continue on
				}
			}
			return result;
		},


		/**
		 * takes this jsdoc type and recursively splits out all record types into their own type
		 * also converts unknown name types into Objects
		 * @see https://developers.google.com/closure/compiler/docs/js-for-compiler
		 */
		convertJsDocType : function(jsdocType, env, doCombine, depth) {
		    if (typeof depth !== 'number') {
		        depth = 0;
		    }
			if (!jsdocType) {
				return THE_UNKNOWN_TYPE;
			}

			var self = this;
			var name = jsdocType.name;
			var allTypes = env.getAllTypes();
			switch (jsdocType.type) {
				case 'NullableLiteral':
				case 'AllLiteral':
				case 'NullLiteral':
				case 'UndefinedLiteral':
				case 'VoidLiteral':
					return {
						type: jsdocType.type
					};
				case 'UnionType':
					return {
						type: jsdocType.type,
						elements: jsdocType.elements.map(function(elt) {
							return self.convertJsDocType(elt, env, doCombine, depth);
						})
					};
				case 'RestType':
					return {
						type: jsdocType.type,
						expression: self.convertJsDocType(jsdocType.expression, env, doCombine, depth)
					};
				case 'ArrayType':
					return {
						type: jsdocType.type,
						elements: jsdocType.elements.map(function(elt) {
							return self.convertJsDocType(elt, env, doCombine, depth);
						})
					};
				case 'FunctionType':
					var fnType = {
						type: jsdocType.type,
						params: jsdocType.params.map(function(elt) {
							return self.convertJsDocType(elt, env, doCombine, depth);
						})
					};
					if (jsdocType.result) {
						// prevent recursion on functions that return themselves
						fnType.result = depth > 2 /*&& jsdocType.result.type === 'FunctionType'*/ ?
							JUST_DOTS_TYPE :
							self.convertJsDocType(jsdocType.result, env, doCombine, depth);
					}
					return fnType;
				case 'TypeApplication':
					var typeApp = {
						type: jsdocType.type,
						expression: self.convertJsDocType(jsdocType.expression, env, doCombine, depth)

					};
					if (jsdocType.applications) {
                        typeApp.applications = jsdocType.applications.map(function(elt) {
							return self.convertJsDocType(elt, env, doCombine, depth);
						});
					}
					return typeApp;
				case 'ParameterType':
					return {
						type: jsdocType.type,
						name: name,
						expression: jsdocType.expression ?
							self.convertJsDocType(jsdocType.expression, env, doCombine, depth) :
							null
					};
				case 'NonNullableType':
				case 'OptionalType':
				case 'NullableType':
					return {
						prefix: true,
						type: jsdocType.type,
						expression: self.convertJsDocType(jsdocType.expression, env, doCombine, depth)
					};
				case 'NameExpression':
					if (doCombine && env.isSyntheticName(name)) {
						// Must mush together all properties for this synthetic type
						var origFields = allTypes[name];
						if (origFields.$$fntype) {
							// just represent the function type directly, not as an object type
							return self.convertJsDocType(origFields.$$fntype, env, doCombine, depth);
						}
						// must combine a record type
						var newFields = [];
						Object.keys(origFields).forEach(function(key) {
							if (key === '$$proto') {
								// maybe should traverse the prototype
								return;
							}
							if (key === '$$isBuiltin') {
								return;
							}
							var prop = origFields[key];
							// if we're already serializing an enclosing object type (depth > 0),
							// and the field type is itself an object type, just represent the type
							// with '...' rather than recursing further
							var fieldType = depth > 0 && (prop.typeObj.type === 'NameExpression' && env.isSyntheticName(prop.typeObj.name) && !allTypes[prop.typeObj.name].$$fntype) ?
							     JUST_DOTS_TYPE :
							     self.convertJsDocType(prop.typeObj, env, doCombine, depth+1);
							newFields.push({
								type: 'FieldType',
								key: key,
								value: fieldType
							});
						});
						return {
							type: 'RecordType',
							fields: newFields
						};
					} else {
						if (allTypes[name]) {
							return createNameType(name);
						} else {
						    //may have been a proto lookup i.e. Connection..prototype
						    var names = name.split('..');
						    if(names.length === 2 && names[1] === 'prototype') {
						        return createNameType(names[0]);
						    }
						}
						var capType = name[0].toUpperCase() + name.substring(1);
						if (allTypes[capType]) {
							return createNameType(capType);
						}
					}
					return THE_UNKNOWN_TYPE;
				case 'FieldType':
					return {
						type: jsdocType.type,
						key: jsdocType.key,
						value: self.convertJsDocType(jsdocType.value, env, doCombine, depth)
					};
				case 'RecordType':
					if (doCombine) {
						// when we are combining, do not do anything special for record types
						return {
							type: jsdocType.type,
							params: jsdocType.fields.map(function(elt) {
								return self.convertJsDocType(elt, env, doCombine, depth+1);
							})
						};
					} else {
						// here's where it gets interesting
						// create a synthetic type in the env and then
						// create a property in the env type for each record property
						var fields = { };
						for (var i = 0; i < jsdocType.fields.length; i++) {
							var field = jsdocType.fields[i];
							var convertedField = self.convertJsDocType(field, env, doCombine, depth+1);
							fields[convertedField.key] = convertedField.value;
						}
						// create a new type to store the record
						var obj = env.newFleetingObject();
						for (var prop in fields) {
							if (fields.hasOwnProperty(prop)) {
								// add the variable to the new object, which happens to be the top-level scope
								env.addVariable(prop, obj.name, fields[prop]);
							}
						}
						return obj;
					}
			}
			return THE_UNKNOWN_TYPE;
		},

		createNameType : createNameType,

		createParamType : function(name, typeObj) {
			return {
				type: 'ParameterType',
				name: name,
				expression: typeObj
			};
		},

		convertToSimpleTypeName : function(typeObj) {
			switch (typeObj.type) {
				case 'NullableLiteral':
				case 'AllLiteral':
				case 'NullLiteral':
					return "Object";

				case 'UndefinedLiteral':
				case 'VoidLiteral':
					return "undefined";

				case 'NameExpression':
					return typeObj.name;

				case 'TypeApplication':
				case 'ArrayType':
					return "Array";

				case 'FunctionType':
					return "Function";

				case 'UnionType':
					return typeObj.expressions && typeObj.expressions.length > 0 ?
						this.convertToSimpleTypeName(typeObj.expressions[0]) :
						"Object";

				case 'RecordType':
					return "Object";

				case 'FieldType':
					return this.convertToSimpleTypeName(typeObj.value);

				case 'NonNullableType':
				case 'OptionalType':
				case 'NullableType':
				case 'ParameterType':
					return this.convertToSimpleTypeName(typeObj.expression);
			}
		},

		// type styling
		styleAsProperty : function(prop, useHtml) {
			return useHtml ? '<span style="color: blue;font-weight:bold;">' + prop + '</span>': prop;
		},
		styleAsType : function(type, useHtml) {
			return useHtml ? '<span style="color: black;">' + type + '</span>': type;
		},
		styleAsOther : function(text, useHtml) {
			return useHtml ? '<span style="font-weight:bold; color:purple;">' + text + '</span>': text;
		},

		/**
		 * creates a human readable type name from the name given
		 */
		createReadableType : function(typeObj, env, useFunctionSig, depth) {
			if (useFunctionSig) {
				typeObj = this.convertJsDocType(typeObj, env, true);
				var res = doctrine.type.stringify(typeObj, {compact: true});
				res = res.replace(JUST_DOTS_REGEX, "{...}");
				res = res.replace(UNDEFINED_OR_EMPTY_OBJ, "");
				return res;
			} else {
				typeObj = this.extractReturnType(typeObj);
				return this.createReadableType(typeObj, env, true, depth);
			}
		},
		/**
		 * @description Returns if the node is recovered or not
		 * @param {Object} node The AST node 
		 * @returns {Boolean} If the node is a recovered node or not  
		 * @since 9.0
		 */
		isRecovered: function isRecovered(node) {
		  return node.recovered === true || node.type === 'RecoveredNode';  
		},
		leftTypeIsMoreGeneral: leftTypeIsMoreGeneral,
		findGlobalObject: findGlobalObject,
		isEmpty: isEmpty,
		ensureTypeObject: ensureTypeObject,
		isUpperCaseChar: isUpperCaseChar,
		OBJECT_TYPE: THE_UNKNOWN_TYPE,
		UNDEFINED_TYPE: createNameType("undefined"),
		NUMBER_TYPE: createNameType("Number"),
		BOOLEAN_TYPE: createNameType("Boolean"),
		STRING_TYPE: createNameType("String"),
		ARRAY_TYPE: createNameType("Array"),
		FUNCTION_TYPE: createNameType("Function")
	};
	
});

