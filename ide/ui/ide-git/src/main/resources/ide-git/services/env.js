var rs = require('http/v3/rs');
var configurations = require('core/v3/configurations');

rs.service()
	.resource('')
		.post(function(ctx, request) {
			var data = request.getJSON();
			for (var i = 0; i < data.env.length; i ++) {
				configurations.set(data.env[i].key, data.env[i].value);
			}
		})
.execute();