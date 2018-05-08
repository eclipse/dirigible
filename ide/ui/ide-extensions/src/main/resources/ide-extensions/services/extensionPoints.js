var rs = require('http/v3/rs');
var query = require('db/v3/query');

const LIST_EXTENSIONS = 'select * from DIRIGIBLE_EXTENSION_POINTS';

rs.service()
	.resource('')
		.get(function(ctx, request, response) {
			let resultset = query.execute(LIST_EXTENSIONS, []);
			let extensionPoints = resultset.map(function(extensionPoint) {
				return {
					'location': extensionPoint.EXTENSIONPOINT_LOCATION,
					'name': extensionPoint.EXTENSIONPOINT_NAME,
					'description': extensionPoint.EXTENSIONPOINT_DESCRIPTION,
					'createdBy': extensionPoint.EXTENSIONPOINT_CREATED_BY,
					'createdAt': extensionPoint.EXTENSIONPOINT_CREATED_AT
				}
			});
			response.println(JSON.stringify(extensionPoints));
		})
.execute();