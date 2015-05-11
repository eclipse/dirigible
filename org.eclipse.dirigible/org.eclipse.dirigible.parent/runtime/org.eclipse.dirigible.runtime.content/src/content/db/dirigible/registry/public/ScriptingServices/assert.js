exports.assertTrue = function(message, condition) {
    return org.junit.Assert.assertTrue(message, condition);
};

exports.assertFalse = function(message, condition) {
    return org.junit.Assert.assertFalse(message, condition);
};

exports.assertEquals = function(message, o1, o2) {
    return org.junit.Assert.assertEquals(message, o1, o2);
};

exports.assertNull = function(message, o) {
    return org.junit.Assert.assertNull(message, o);
};

exports.assertNotNull = function(message, o) {
    return org.junit.Assert.assertNotNull(message, o);
};

exports.fail = function(message) {
    return org.junit.Assert.fail(message);
};