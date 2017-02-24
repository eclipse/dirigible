/* globals $ */
/* eslint-env node, dirigible */

var response = require('net/http/response');
var cockpitExtensions = require('${packageName}/extension/cockpitExtensionUtils');

var menuItems = cockpitExtensions.getMenuItems();

response.println(JSON.stringify(menuItems));
response.flush();
response.close();
