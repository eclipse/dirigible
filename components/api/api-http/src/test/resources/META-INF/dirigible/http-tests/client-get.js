import { HttpClient as client } from 'sdk/http/client';
import { Assert } from 'test/assert';

var result = client.get('https://raw.githubusercontent.com/eclipse/dirigible/master/NOTICE.txt');

console.log(JSON.stringify(result));

Assert.assertTrue((result !== null) && (result !== undefined));
