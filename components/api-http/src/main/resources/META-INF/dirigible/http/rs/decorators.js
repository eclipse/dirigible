const rs = require('http/rs');

const router = rs.service();
let instance = null;

exports.Controller = function (ctr) {
    instance = new ctr();
    router.execute();
}

function registerRequestHandler(
    descriptor,
    path,
    method,
) {
    router[method](
        path,
        (ctx, req, res) => {
            handleRequest(req, res, ctx, descriptor);
        }
    );
}

function handleRequest(req, res, ctx, handler) {
    const body = req.json();
    const maybeResponseBody = handler.apply(instance || {}, [body, ctx, req, res]);
    if (maybeResponseBody) {
        res.json(maybeResponseBody);
    }
}

exports.Get = function (path) {
    return function (target, propertyKey, descriptor) {
        const handler = descriptor ? descriptor.value : target;
        return registerRequestHandler(handler, path, "get");
    };
}

exports.Post = function (path) {
    return function (target) {
        return registerRequestHandler(target, path, "post");
    };
}

exports.Put = function (path) {
    return function (target) {
        return registerRequestHandler(target, path, "put");
    };
}

exports.Patch = function (path) {
    return function (target) {
        return registerRequestHandler(target, path, "patch");
    };
}

exports.Delete = function (path) {
    return function (target) {
        return registerRequestHandler(target, path, "delete");
    };
}

exports.Head = function (path) {
    return function (target) {
        return registerRequestHandler(target, path, "head");
    };
}

exports.Options = function (path) {
    return function (target) {
        return registerRequestHandler(target, path, "options");
    };
}