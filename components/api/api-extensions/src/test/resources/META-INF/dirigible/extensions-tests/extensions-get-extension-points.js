import { Extensions as extensions } from 'sdk/extensions/extensions';
import { Assert } from 'test/assert';

var result = extensions.getExtensionPoints();

Assert.assertEquals(result[0], "test_extpoint1");
