
var sequence = require('db/sequence');
var assertEquals = require('test/assert').assertEquals;

sequence.create('mysequence');
var zero = sequence.nextval('mysequence');
var one = sequence.nextval('mysequence');
sequence.drop('mysequence');

assertEquals(zero, 1);
assertEquals(one, 2);
