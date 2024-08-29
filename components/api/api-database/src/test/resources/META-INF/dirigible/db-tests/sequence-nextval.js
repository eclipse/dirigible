import { Sequence as sequence } from 'sdk/db/sequence';
import { Assert } from 'test/assert';

sequence.create('mysequence');
var zero = sequence.nextval('mysequence');
var one = sequence.nextval('mysequence');
sequence.drop('mysequence');

Assert.assertEquals(zero, 1);
Assert.assertEquals(one, 2);
