#set( $D = '$' )
/* globals ${D} */
/* eslint-env node, dirigible */
"use strict";

var commentsDataService = require("${packageName}/lib/comments_service_lib").get();

//ensure handling only GET methods (readonly view)
var handlers = commentsDataService.getResourceHandlersMap();
for(var resPath in handlers){
	var resource = handlers[resPath];
	var verbs = Object.keys(resource);
	for(var i = 0; i<verbs.length; i++){
		if(verbs[i].toLowerCase()!== 'get')
			delete resource[verbs[i]];
	}
}

commentsDataService.service();
