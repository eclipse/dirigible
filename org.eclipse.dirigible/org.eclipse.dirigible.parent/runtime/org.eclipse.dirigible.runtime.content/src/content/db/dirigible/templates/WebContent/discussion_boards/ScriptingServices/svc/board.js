#set( $D = '$' )
/* globals ${D} */
/* eslint-env node, dirigible */
"use strict";

var boardsDataService = require("${packageName}/lib/boards_service_lib").get();

var handlers = boardsDataService.getResourceHandlersMap();


//ensure handling only GET methods (readonly view) , excpet for {id}/visits which is updaed also in public view
for(var resPath in handlers){
	if(["{id}/visit"].indexOf(resPath)<0){
		var resource = handlers[resPath];
		var verbs = Object.keys(resource);
		for(var i = 0; i<verbs.length; i++){
			if(verbs[i].toLowerCase()!== 'get')
				delete resource[verbs[i]];
		}
	}
}

boardsDataService.service();
