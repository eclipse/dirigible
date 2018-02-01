/* eslint-env node, dirigible */

var streams = require('io/v3/streams');

var bais = streams.createByteArrayInputStream([61, 62, 63]);
var baos = streams.createByteArrayOutputStream();
streams.copy(bais, baos);
var result = baos.getBytes();

result[1] === 62;
