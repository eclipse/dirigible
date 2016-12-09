/* globals $ */
/* eslint-env node, dirigible */

var response = require('net/http/response');
var registryExtensionUtils = require('registry/extension/registryExtensionUtils');

var homeData = registryExtensionUtils.getHomeData('Develop');

response.println(JSON.stringify(homeData));
response.flush();
response.close();