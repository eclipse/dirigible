const rs = require('http/rs');

const router = rs.service();
let instance = null;

exports.Controller = function (ctr) {
    instance = new ctr();
    router.execute();
}

exports.Get = createRequestDecorator("get")
exports.Post = createRequestDecorator("post")
exports.Put = createRequestDecorator("put")
exports.Patch = createRequestDecorator("patch")
exports.Delete = createRequestDecorator("delete")
exports.Head = createRequestDecorator("head")
exports.Options = createRequestDecorator("options")

function createRequestDecorator(httpMethod) {
    return function (path) {
        return function (target, propertyKey, descriptor) {
            const handler = descriptor ? descriptor.value : target;
            router[httpMethod](
                path,
                (ctx, req, res) => {
                    handleRequest(req, res, ctx, handler);
                }
            );
        };
    }
}

function handleRequest(req, res, ctx, handler) {
    const body = req.json();
    const maybeResponseBody = handler.apply(instance || {}, [body, ctx, req, res]);
    if (maybeResponseBody) {
        res.json(maybeResponseBody);
    }
}