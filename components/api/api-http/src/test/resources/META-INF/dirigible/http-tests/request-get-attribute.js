import { Request as request } from 'sdk/http/request';
import { Assert } from 'test/assert';

Assert.assertEquals(request.getAttribute('attr1'), 'val1');
