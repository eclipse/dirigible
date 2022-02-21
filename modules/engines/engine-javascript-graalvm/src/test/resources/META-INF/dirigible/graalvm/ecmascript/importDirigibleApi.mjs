import { base64 } from '@dirigible/utils';
import { base64 as base642 } from '@dirigible/utils';
import { uuid, hex, escape } from '@dirigible/utils';
import * as utils from '@dirigible/utils'
import utils2 from '@dirigible/utils'

// var assertEquals = require('utils/assert').assertEquals;
const assertEquals = Java.type("org.junit.Assert").assertEquals;
assertEquals("YWRtaW46YWRtaW4=", base64.encode("admin:admin"));
assertEquals("YWRtaW46YWRtaW4=", base642.encode("admin:admin"));
assertEquals("48657820456e636f646564", hex.encode("Hex Encoded"));
assertEquals("YWRtaW46YWRtaW4=", utils.base64.encode("admin:admin"));
assertEquals("YWRtaW46YWRtaW4=", utils2.base64.encode("admin:admin"));

// assertEquals(base64.encode("admin:admin"), "YWRtaW46YWRtaW4=", "base64 import failed");
// assertEquals(base642.encode("admin:admin"), "YWRtaW46YWRtaW4=", "base642 import failed");
// assertEquals(hex.encode("Hex Encoded"), "48657820456e636f646564", "hex import failed");
// assertEquals(utils.base64.encode("admin:admin"), "YWRtaW46YWRtaW4=", "utils import failed");
// assertEquals(utils2.base64.encode("admin:admin"), "YWRtaW46YWRtaW4=", "utils2 import failed");



