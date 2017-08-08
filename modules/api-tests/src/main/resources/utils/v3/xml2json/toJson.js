/* eslint-env node, dirigible */

var xml2json = require('utils/v3/xml2json');

var input = '<a><b>text_b</b><c>text_c</c><d><e>text_e</e></d></a>';
var result = xml2json.toJson(input);

JSON.parse(result).a.d.e === 'text_e';

