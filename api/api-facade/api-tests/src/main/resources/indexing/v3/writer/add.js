var writer = require('indexing/v3/writer');
var searcher = require('indexing/v3/searcher');

writer.add("index1", "myfile1", "apache lucene", new Date(), {"name1":"value1"});
writer.add("index1", "myfile2", "lucene - the search engine", new Date(), {"name2":"value2"});

var found = searcher.search("index1", "lucene");

console.log(JSON.stringify(found));

((found !== null) && (found !== undefined) && found.length === 2);