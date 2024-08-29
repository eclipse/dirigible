import { Session as session } from 'sdk/http/session';
import { Assert } from 'test/assert';

session.setAttribute('attr1', 'value1');

Assert.assertEquals(JSON.stringify(session.getAttributeNames()), '["invocation.count","attr1"]');
