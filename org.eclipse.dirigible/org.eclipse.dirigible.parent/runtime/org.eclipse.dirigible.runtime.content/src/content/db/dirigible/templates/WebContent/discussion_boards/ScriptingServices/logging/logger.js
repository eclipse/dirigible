/* globals $ */
/* eslint-env node, dirigible */
exports.logger = {
		error: function(errCode, errMessage, errContext){
			if(this.logThreshold > 0){
				var ctxSegment = this.ctx!==undefined?'['+this.ctx+']: ':'';
				var errCodeSegment = errCode!==undefined?'['+errCode+']: ':'';
				console.error(ctxSegment + errCodeSegment + errMessage);
			    if (errContext !== undefined && errContext !== null) {
			    	console.error(JSON.stringify(errContext));
			    }			
			}
		},
		info: function(message){
			if(this.logThreshold > 1){
				var ctxSegment = this.ctx!==undefined?'['+this.ctx+']: ':'';
				console.info(ctxSegment + message);
			}
		},
		logThreshold: 2
	};
