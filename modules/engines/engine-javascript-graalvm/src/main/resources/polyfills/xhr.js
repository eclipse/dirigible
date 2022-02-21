/*
 Source is originated from https://github.com/morungos/java-xmlhttprequest

 Articles about Nashorn:
 - https://blog.codecentric.de/en/2014/06/project-nashorn-javascript-jvm-polyglott/
 */
const System = Java.type('java.lang.System');
const RequestBuilder = Java.type("org.apache.http.client.methods.RequestBuilder");
//const FutureCallback = Java.type("org.apache.http.concurrent.FutureCallback");
const HttpClientBuilder = Java.type("org.apache.http.impl.client.HttpClientBuilder");
const BasicHeader = Java.type("org.apache.http.message.BasicHeader");
const ArrayList = Java.type('java.util.ArrayList');
// let ByteArrayEntity = Packages.org.apache.http.entity.ByteArrayEntity;
const StringEntity = Java.type("org.apache.http.entity.StringEntity");
// let EntityBuilder = Packages.org.apache.http.client.entity.EntityBuilder;
// let ContentType = Packages.org.apache.http.entity.ContentType;

const XMLHttpRequest = function () {
    let method, url, async, user, password, headers = {};

    this.onreadystatechange = function () {
    };

    this.onload = function () {
    };
    this.onerror = function () { };

    this.readyState = 0;
    this.response = null;
    this.responseText = null;
    this.responseType = '';
    this.status = null;
    this.statusText = null;
    this.timeout = 0; // no timeout by default
    this.ontimeout = function () {
    };
    this.withCredentials = false;
    let requestBuilder = null;

    this.abort = function () {

    };

    this.getAllResponseHeaders = function () {

    };

    this.getResponseHeader = function (key) {

    };

    this.setRequestHeader = function (key, value) {
        headers[key] = value;
    };

    this.open = function (_method, _url, _async, _user, _password) {
        this.readyState = 1;

        method = _method;
        url = _url;

        async = _async === false ? false : true;

        user = _user || '';
        password = _password || '';

        requestBuilder = RequestBuilder.create(_method);
        requestBuilder.setUri(_url);

        // for (let prop in headers) {
        //   requestBuilder.addHeader(prop, headers[prop])
        // }

        setTimeout(this.onreadystatechange, 0);
    };

    this.send = function (data) {
        let that = this;

        let clientBuilder = HttpClientBuilder.create();
        let httpHeaders = new ArrayList();

        for (let prop in headers) {
            httpHeaders.add(new BasicHeader(prop, headers[prop]));
        }

        if (window.__HTTP_SERVLET_REQUEST__) {
            let copyHeaders = ['Cookie', 'Authorization'];
            for (let i = 0; i < copyHeaders.length; i++) {
                httpHeaders.add(new BasicHeader(copyHeaders[i], window.__HTTP_SERVLET_REQUEST__.getHeader(copyHeaders[i])));
            }
        }

        clientBuilder.setDefaultHeaders(httpHeaders);

        if (data === undefined || data === null) {
            requestBuilder.setEntity(null);
        } else if (typeof data === 'string') {
            requestBuilder.setEntity(new StringEntity(data));
        } else {
            throw new Error('unsupported body data type');
        }

        let httpclient = clientBuilder.build();

        function onCompleted(response) {
                that.readyState = 4;

                let body = org.apache.http.util.EntityUtils.toString(response.getEntity(), 'UTF-8');
                that.responseText = that.response = body;

                let finalException = null;
                if (that.responseType === 'json') {
                    try {
                        that.response = JSON.parse(that.response);
                    } catch (e) {

                        // Store the error
                        finalException = e;

                    }
                }

                if (finalException) {
                    return;
                }

                const statusLine = response.getStatusLine();
                that.status = statusLine.getStatusCode();
                that.statusText = statusLine.getReasonPhrase();

                setTimeout(that.onreadystatechange, 0);
                setTimeout(that.onload, 0);
                httpclient.close();
        }
        function onFailed() {
                that.readyState = 4;
                that.status = 0;
                that.statusText = e.getMessage();
                setTimeout(that.onreadystatechange, 0);
                setTimeout(that.onerror, 0);
                httpclient.close();
        }

        const EventLoopAwareHttpClient = Java.type("org.eclipse.dirigible.engine.js.graalvm.execution.js.eventloop.EventLoopAwareHttpClient");
        let loopAwareHttpClient = new EventLoopAwareHttpClient(httpclient)
        loopAwareHttpClient.execute(requestBuilder.build(), onCompleted, onFailed);
    }
}

globalThis.XMLHttpRequest = XMLHttpRequest;