import { root } from '../root.mjs';
import { l11, exported } from '../l11/l11.mjs';
import { expectedTestData, actualTestData } from '../l11/l11_with_dirigible_import.mjs'

var assertEquals = require('utils/assert').assertEquals;
assertEquals(root, 'root', "root import failed");
assertEquals(l11, 'l11', "l11 import failed");
assertEquals(exported, 'l12', "exported import failed");
assertEquals(expectedTestData, expectedTestData, "import of @dirigible/io or the encoding failed in l11_with_dirigible_import.mjs");

