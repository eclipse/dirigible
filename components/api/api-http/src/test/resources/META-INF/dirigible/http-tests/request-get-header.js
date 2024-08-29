import { Request as request } from 'sdk/http/request';
import { Assert } from 'test/assert';

Assert.assertEquals(request.getHeader('header1'), 'header1');
