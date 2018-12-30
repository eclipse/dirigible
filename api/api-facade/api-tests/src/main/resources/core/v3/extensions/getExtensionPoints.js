var extensions = require('core/v3/extensions');

var result = extensions.getExtensionPoints();

result[0] == "test_extpoint1";
