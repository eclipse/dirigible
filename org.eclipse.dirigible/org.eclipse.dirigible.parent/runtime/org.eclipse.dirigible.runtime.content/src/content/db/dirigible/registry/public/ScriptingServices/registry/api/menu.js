/* globals $ */
/* eslint-env node, dirigible */

var response = require('net/http/response');
var registryExtensionUtils = require('registry/extension/registryExtensionUtils');

var menu = registryExtensionUtils.getMenu();

response.println(JSON.stringify(menu));
response.flush();
response.close();
