import { Request } from 'sdk/http/request';
import { Assert } from 'test/assert';

Assert.assertEquals(Request.getRemoteUser(), 'user');
