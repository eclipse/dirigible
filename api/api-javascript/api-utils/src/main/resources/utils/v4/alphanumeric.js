/*
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */

/**
 * Transforms a string to an alphanumeric sequence, stripping non-conformant characters for it.
 * @param string {String} the string to transform
 */
exports.toAlphanumeric = function(string){
	return string.replace(/[^A-Za-z0-9_]/g, '');
};
/**
 * Generates a random alphanumeric sequence with the specified length
 * @param length {Integer} Defaults to 4
 */
exports.alphanumeric = function(length){
	if(!length)
		length = 4;
	var power = length;
	var sliceIndex = -Math.abs(length);
	return ("0000" + (Math.random()*Math.pow(36,power) << 0).toString(36)).slice(sliceIndex);
};
/**
 * Tests is the provided `str` argument is a valid alphanumeric sequence.
 * @param str {String} the string to test
 */
exports.isAlphanumeric = function(str){
	// a faster alternative to checking with  /[^a-zA-Z0-9]/.test(str)
	// copy from public domain at: https://stackoverflow.com/a/25352300
	var code, i, len;
    for (i = 0, len = str.length; i < len; i++) {
		code = str.charCodeAt(i);
		if (!(code > 47 && code < 58) && // numeric (0-9)
			!(code > 64 && code < 91) && // upper alpha (A-Z)
			!(code > 96 && code < 123)) { // lower alpha (a-z)
		  return false;
		}
	  }
	return true;
}
