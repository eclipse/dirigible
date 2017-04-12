/* globals $ */
/* eslint-env node, dirigible */

const PATH = "/positions";
const HTML_LINK = "positions.html";

exports.getHomeItem = function() {
	return {
      "image": "positions",
      "color": "purple",
      "path": PATH,
      "link": HTML_LINK,
      "title": "Positions",
      "description": "Positions"
   };
};

exports.getMenuItem = function() {
	return {  
      "name": "Positions",
      "path": PATH,
      "link": HTML_LINK
   };
};
