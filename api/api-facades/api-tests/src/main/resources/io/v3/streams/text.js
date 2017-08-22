/* eslint-env node, dirigible */

var streams = require('io/v3/streams');

var baos = streams.createByteArrayOutputStream();
baos.writeText("some text");
var result = baos.getBytes();
var bais = streams.createByteArrayInputStream(result);
result = bais.readText();

result == "some text";
