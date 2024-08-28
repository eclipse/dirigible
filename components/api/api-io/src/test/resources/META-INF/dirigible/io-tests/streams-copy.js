
import { Streams } from 'sdk/io/streams';
const assertEquals = require('test/assert').assertEquals;

const bais = Streams.createByteArrayInputStream([61, 62, 63]);
const baos = Streams.createByteArrayOutputStream();

Streams.copy(bais, baos);

const result = baos.getBytes();

assertEquals(result[1], 62);
