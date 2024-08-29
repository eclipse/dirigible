import { Response as response } from 'sdk/http/response';
import { Assert } from 'test/assert';

Assert.assertTrue(response.getHeaderNames().includes("header1","header2"));
