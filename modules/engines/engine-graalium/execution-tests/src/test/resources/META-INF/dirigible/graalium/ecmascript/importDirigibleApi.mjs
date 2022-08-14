import { base64 } from '@dirigible/utils';
import { uuid, hex, escape } from '@dirigible/utils';
import * as utils from '@dirigible/utils'

var assertEquals = require('utils/assert').assertEquals;

assertEquals(base64.encode("admin:admin"), "YWRtaW46YWRtaW4=", "base64 import failed");
assertEquals(hex.encode("Hex Encoded"), "48657820456e636f646564", "hex import failed");
assertEquals(utils.base64.encode("admin:admin"), "YWRtaW46YWRtaW4=", "utils import failed");

