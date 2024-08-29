import { Request as request } from 'sdk/http/request';
import { Assert } from 'test/assert';

Assert.assertEquals(request.getPathInfo(), '/services/js/http-tests/request-get-path-info.js');
