import { Writer as writer } from 'sdk/indexing/writer';
import { Searcher as searcher } from 'sdk/indexing/searcher';
import { Assert } from 'test/assert';

writer.add("index2", "myfile1", "apache lucene", new Date(123));
writer.add("index2", "myfile2", "lucene - the search engine", new Date(234), {"name2":"value2"});
writer.add("index2", "myfile3", "search engine", new Date(345), {"name2":"value2"});

var found = searcher.between("index2", new Date(124), new Date(344));

console.log(JSON.stringify(found));

Assert.assertTrue((found !== null) && (found !== undefined) && found.length === 1);