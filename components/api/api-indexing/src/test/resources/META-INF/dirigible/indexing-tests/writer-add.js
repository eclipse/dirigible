import { Writer as writer } from 'sdk/indexing/writer';
import { Searcher as searcher } from 'sdk/indexing/searcher';
import { Assert } from 'test/assert';

writer.add("index1", "myfile1", "apache lucene", new Date(), {"name1":"value1"});
writer.add("index1", "myfile2", "lucene - the search engine", new Date(), {"name2":"value2"});

var found = searcher.search("index1", "lucene");

console.log(JSON.stringify(found));

Assert.assertTrue((found !== null) && (found !== undefined) && found.length === 2);