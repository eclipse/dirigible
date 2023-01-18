let UriBuilder = function UriBuilder() {
    this.pathSegments = [];
    return this;
};
UriBuilder.prototype.path = function (_pathSegments) {
    if (!Array.isArray(_pathSegments))
        _pathSegments = [_pathSegments];
    _pathSegments = _pathSegments.filter(function (segment) {
        return segment;
    }).map(function (segment) {
        if (segment.length) {
            if (segment.charAt(segment.length - 1) === '/')
                segment = segment.substring(0, segment.length - 2);
            segment = encodeURIComponent(segment);
        }
        return segment;
    });
    this.pathSegments = this.pathSegments.concat(_pathSegments);
    return this;
};
UriBuilder.prototype.build = function (isBasePath = true) {
    if (isBasePath) return '/' + this.pathSegments.join('/');
    return this.pathSegments.join('/');
}