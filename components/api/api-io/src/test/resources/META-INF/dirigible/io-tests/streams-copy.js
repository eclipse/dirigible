
var streams = require('io/streams');
var assertEquals = require('test/assert').assertEquals;

var bais = streams.createByteArrayInputStream([61, 62, 63]);
var baos = streams.createByteArrayOutputStream();
streams.copy(bais, baos);
var result = baos.getBytes();

assertEquals(result[1], 62);
