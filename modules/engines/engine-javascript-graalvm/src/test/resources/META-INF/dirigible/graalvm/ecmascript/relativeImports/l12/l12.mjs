import { root } from '../root.mjs';
import { l11, exported } from '../l11/l11.mjs';

var assertEquals = require('utils/assert').assertEquals;
assertEquals(root, 'root', "root import failed");
assertEquals(l11, 'l11', "l11 import failed");
assertEquals(exported, 'l12', "exported import failed");
