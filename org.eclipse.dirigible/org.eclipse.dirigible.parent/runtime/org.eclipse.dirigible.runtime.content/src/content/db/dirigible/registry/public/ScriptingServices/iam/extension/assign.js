/* globals $ */
/* eslint-env node, dirigible */

const PATH = "/assign";
const HTML_LINK = "assign.html";

exports.getHomeItem = function() {
	return {
      "image": "gavel",
      "color": "orange",
      "path": PATH,
      "link": HTML_LINK,
      "title": "Assignments",
      "description": "User Roles Assignments"
   };
};

exports.getMenuItem = function() {
	return {  
      "name": "Assignments",
      "path": PATH,
      "link": HTML_LINK
   };
};
