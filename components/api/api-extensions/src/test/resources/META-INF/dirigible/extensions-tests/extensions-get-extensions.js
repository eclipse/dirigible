import { Extensions as extensions } from 'sdk/extensions/extensions';
import { Assert } from 'test/assert';

var result = extensions.getExtensions('test_extpoint1');

Assert.assertEquals(result[0], "/test_ext_module1");
