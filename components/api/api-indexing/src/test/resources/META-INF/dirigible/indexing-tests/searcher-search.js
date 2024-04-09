
var writer = require('indexing/writer');
var searcher = require('indexing/searcher');
var assertTrue = require('test/assert').assertTrue;

writer.add("index3", "myfile1", "apache lucene", new Date(), {"name1":"value1"});
writer.add("index3", "myfile2", "lucene - the search engine", new Date(), {"name2":"value2"});

var found = searcher.search("index3", "engine");

console.log(JSON.stringify(found));

assertTrue((found !== null) && (found !== undefined) && found.length === 1);