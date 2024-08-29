import { Writer as writer } from 'sdk/indexing/writer';
import { Searcher as searcher } from 'sdk/indexing/searcher';
import { Assert } from 'test/assert';

writer.add("index3", "myfile1", "apache lucene", new Date(), {"name1":"value1"});
writer.add("index3", "myfile2", "lucene - the search engine", new Date(), {"name2":"value2"});

const found = searcher.search("index3", "engine");

console.log(JSON.stringify(found));

Assert.assertTrue((found !== null) && (found !== undefined) && found.length === 1);