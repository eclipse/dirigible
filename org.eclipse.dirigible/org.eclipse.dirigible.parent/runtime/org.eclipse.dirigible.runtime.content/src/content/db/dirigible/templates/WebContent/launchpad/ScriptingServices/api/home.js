/* globals $ */
/* eslint-env node, dirigible */

var response = require('net/http/response');
var launchpadExtensions = require('${packageName}/extension/launchpadExtensionUtils');

var homeItems = launchpadExtensions.getHomeItems();

response.println(JSON.stringify(homeItems));
response.flush();
response.close();
