/* globals $ */
/* eslint-env node, dirigible */

const PATH = "/organizations";
const HTML_LINK = "organizations.html";

exports.getHomeItem = function() {
	return {
      "image": "organizations",
      "color": "purple",
      "path": PATH,
      "link": HTML_LINK,
      "title": "Organizations",
      "description": "Organizations"
   };
};

exports.getMenuItem = function() {
	return {  
      "name": "Organizations",
      "path": PATH,
      "link": HTML_LINK
   };
};
