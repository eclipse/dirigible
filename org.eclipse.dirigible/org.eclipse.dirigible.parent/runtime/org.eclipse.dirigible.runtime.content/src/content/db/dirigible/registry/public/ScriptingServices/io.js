// deprecated: use io/streams
exports.read = function(input) {
    var output = io.readLines(input);
    output = output.toArray().join('\n');
    return output;
};