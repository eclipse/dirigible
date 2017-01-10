/* globals $ */
/* eslint-env node, dirigible */
exports.jasmine_console_reporter = {
	specStarted: function(result) {
		console.info('[Spec started]: ' + result);
	},
	specDone: function(result) {
		console.info('[Spec done]: ' + result);
	},
	suiteStarted: function(result) {
    	console.info('[Suite started]: ' + result);
	},	
	suiteDone: function(result) {
    	console.info('[Suite done]: ' + result);
	},
	jasmineStarted: function(suiteInfo) {
		console.info('[Jasmine started]: ' + suiteInfo);
	},
	jasmineDone: function() {
		console.info('[Jasmine done]');
	}
};