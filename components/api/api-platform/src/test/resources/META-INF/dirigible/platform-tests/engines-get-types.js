import { Engine as engines } from 'sdk/platform/engines';
import { Assert } from 'test/assert';

const result = engines.getTypes();

Assert.assertTrue(result !== undefined && result !== null);
