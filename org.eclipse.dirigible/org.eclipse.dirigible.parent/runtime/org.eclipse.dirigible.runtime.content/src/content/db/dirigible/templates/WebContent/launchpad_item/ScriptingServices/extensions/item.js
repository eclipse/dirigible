/* globals $ */
/* eslint-env node, dirigible */

const PATH = "/${fileNameNoExtension}";
const HTML_LINK = "items/${fileName}";

exports.getHomeItem = function() {
	return {
      "image": "book",
      "color": "green",
      "path": PATH,
      "link": HTML_LINK,
      "title": "${pageTitle}",
      "description": "${pageTitle} Launchpad Item"
   };
};

exports.getMenuItem = function() {
	return {  
      "name": "${pageTitle}",
      "path": PATH,
      "link": HTML_LINK
   };
};
