var rs = require('http/v3/rs');
var query = require('db/v3/query');

const LIST_EXTENSIONS = 'select * from DIRIGIBLE_EXTENSIONS';

rs.service()
	.resource('')
		.get(function(ctx, request, response) {
			let resultset = query.execute(LIST_EXTENSIONS, []);
			let extensions = resultset.map(function(extension) {
				return {
					'location': extension.EXTENSION_LOCATION,
					'extensionPoint': extension.EXTENSION_EXTENSIONPOINT_NAME,
					'module': extension.EXTENSION_MODULE,
					'description': extension.EXTENSION_DESCRIPTION,
					'createdBy': extension.EXTENSION_CREATED_BY,
					'createdAt': extension.EXTENSION_CREATED_AT
				}
			});
			response.println(JSON.stringify(extensions));
		})
.execute();