/* eslint-env node, dirigible */

var xml2json = require('utils/v3/xml2json');

var input = '{"a":{"b":"text_b","c":"text_c","d":{"e":"text_e"}}}';
var result = xml2json.fromJson(input);

result === '<a><b>text_b</b><c>text_c</c><d><e>text_e</e></d></a>';
