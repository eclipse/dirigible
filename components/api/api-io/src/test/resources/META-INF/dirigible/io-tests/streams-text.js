
var streams = require('io/streams');
var assertEquals = require('test/assert').assertEquals;

var baos = streams.createByteArrayOutputStream();
baos.writeText("some text");
var result = baos.getBytes();
var bais = streams.createByteArrayInputStream(result);
result = bais.readText();

assertEquals(result, "some text");
