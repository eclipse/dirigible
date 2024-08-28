
import { Streams } from 'sdk/io/streams';
const assertEquals = require('test/assert').assertEquals;

const baos = Streams.createByteArrayOutputStream();
baos.writeText("some text");

const bais = Streams.createByteArrayInputStream(baos.getBytes());
const result = bais.readText();

assertEquals(result, "some text");
