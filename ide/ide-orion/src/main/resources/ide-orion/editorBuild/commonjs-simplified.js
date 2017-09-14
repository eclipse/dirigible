define([
	'tern/lib/tern',
	'tern/lib/infer'
], function (tern, infer) {

	var defs = {
		"!name": "testplugin",
		"defineModule": {
			"!type": "fn(name: string, def: fn()) -> !custom:defineModule"
		},
		"require": {
			"!type": "fn(name: string) -> !custom:require"
		}
	};
	
	tern.registerPlugin('testplugin', function (server, options) {
		server.addDefs(defs);
	});

	var moduleTypes = Object.create(null);

	infer.registerFunction('defineModule', function (self, args, argnodes) {
		if (!argnodes[0] || argnodes[0].type !== 'Literal' || typeof argnodes[0].value !== 'string' ||
			!argnodes[1] || argnodes[1].type !== 'FunctionExpression' && argnodes[1].type !== 'ArrowFunctionExpression') {

			return infer.ANull;
		}
		var moduleName = argnodes[0].value;
		var functionDefinition = argnodes[1];
		var functionType = infer.expressionType({
			node: functionDefinition,
			state: functionDefinition.scope
		});
		moduleTypes[moduleName] = functionType.retval;

		return infer.ANull;
	});

	infer.registerFunction('require', function (self, args, argnodes) {
		if (!argnodes[0] || argnodes[0].type !== 'Literal' || typeof argnodes[0].value !== 'string')
			return infer.ANull;

		var moduleName = argnodes[0].value;
		var moduleType = moduleTypes[moduleName];
		if (!moduleType) {
			moduleTypes[moduleName] = infer.ANull; // prevent fetching multiple times

			var ternServer = infer.cx().parent;
			var currentFile = argnodes[0].sourceFile.name;

			getModule('../../' + moduleName + '.js').then(function (content) {
				console.log('Adding file ' + moduleName + '.js ...');
				content = "defineModule('" + moduleName + "', () => exports = {}; " + content + " return exports;)";
				ternServer.addFile('/' + moduleName, content, currentFile);
			}).catch(function (error) {
				console.log(error);
			});

			return infer.ANull;
		}
		return moduleType;
	});

	function getModule(modulePath) {
		if (!self.fetch) {
			console.log('No Fetch API available!');
		}
		console.log('Fetching file ' + modulePath);
		return fetch(modulePath).then(function (response) {
			if (response.ok) {
			    return response.text().then(function(text) {
			        console.log('Fetched file ' + text);
				    return text;
			    });
			}
			throw new Error('Failed to fetch "' + modulePath + '": ' + response.status + ' ' + response.statusText);
		});
	}
});
