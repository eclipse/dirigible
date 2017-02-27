/* globals $ */
/* eslint-env node, dirigible */

const PATH = "/${fileNameNoExtension}";
const HTML_LINK = "items/${fileName}";

exports.getMenuItem = function() {
	return {  
      "name": "${pageTitle}",
      "path": PATH,
      "link": HTML_LINK
   };
};

exports.getSidebarItem = function() {
	return {  
      "name": "${pageTitle}",
      "path": PATH,
      "link": HTML_LINK
   };
};
