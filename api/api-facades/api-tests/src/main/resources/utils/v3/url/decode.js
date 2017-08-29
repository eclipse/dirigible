/* eslint-env node, dirigible */

var url = require('utils/v3/url');

var input = '%3C%21%5BCDATA%5B%3Cmeta+http-equiv%3D%22refresh%22+content%3D%220%3Burl%3Djavascript%3Adocument.vulnerable%3Dtrue%3B%22%3E%5D%5D%3E';
var result = url.decode(input, 'UTF-8');

result == '<![CDATA[<meta http-equiv="refresh" content="0;url=javascript:document.vulnerable=true;">]]>'
