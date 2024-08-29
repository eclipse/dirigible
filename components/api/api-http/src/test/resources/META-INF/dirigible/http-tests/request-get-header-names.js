import { Request as request } from 'sdk/http/request';
import { Assert } from 'test/assert';

Assert.assertEquals(JSON.stringify(request.getHeaderNames()), '["Authorization","header1","header2"]');
