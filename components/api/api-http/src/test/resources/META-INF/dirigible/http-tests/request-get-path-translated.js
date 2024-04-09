
const request = require('http/request');
const separator = require('io/files').separator;
const assertTrue = require('test/assert').assertTrue;

assertTrue(request.getPathTranslated().endsWith(`${separator}services${separator}js${separator}http-tests${separator}request-get-path-translated.js`));
