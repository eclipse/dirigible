/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.api.v3.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.eclipse.dirigible.api.v3.http.client.HttpClientRequestOptions;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HttpClientAsyncFacade implements IScriptingFacade {

	private static final Logger logger = LoggerFactory.getLogger(HttpClientAsyncFacade.class);
	private static IJavascriptEngineExecutor defaultEngineExecutor = (IJavascriptEngineExecutor) StaticObjects.get(StaticObjects.JAVASCRIPT_ENGINE);

	private int requestsCounter = 0;
	private List<AsyncHttpRequest> asyncHttpRequests = new ArrayList<AsyncHttpRequest>();
	private CountDownLatch countDownLatch;

	/**
	 * Create HttpResponseCallback
	 * 
	 * @param completeCallback the complete callback
	 * @return HttpResponseCallback
	 */
	public HttpResponseCallback createCallback(String completeCallback) {
		return new HttpResponseCallback(completeCallback, null, null);
	}

	/**
	 * Create HttpResponseCallback
	 * 
	 * @param completeCallback the complete callback
	 * @param failCallback the fail callback
	 * @return HttpResponseCallback
	 */
	public HttpResponseCallback createCallback(String completeCallback, String failCallback) {
		return new HttpResponseCallback(completeCallback, failCallback, null);
	}

	/**
	 * Create HttpResponseCallback
	 * 
	 * @param completeCallback the complete callback
	 * @param failCallback the fail callback
	 * @param cancelCallback the cancel callback
	 * @return HttpResponseCallback
	 */
	public HttpResponseCallback createCallback(String completeCallback, String failCallback, String cancelCallback) {
		return new HttpResponseCallback(completeCallback, failCallback, cancelCallback);
	}

	/**
	 * Performs a Async GET request for the specified URL and options
	 *
	 * @param url
	 *            the URL
	 * @param options
	 *            the options
	 * @param httpResponseCallback
	 *            the callback
	 */
	public void getAsync(String url, String options, HttpResponseCallback httpResponseCallback) {
		requestsCounter ++;
		HttpClientRequestOptions httpClientRequestOptions = HttpClientFacade.parseOptions(options);
		httpResponseCallback.setOptions(httpClientRequestOptions);
		HttpGet request = HttpClientFacade.createGetRequest(url, httpClientRequestOptions);
		asyncHttpRequests.add(new AsyncHttpRequest(request, httpResponseCallback.getCallback()));
	}

	/**
	 * Performs a Async POST request for the specified URL and options
	 *
	 * @param url
	 *            the URL
	 * @param options
	 *            the options
	 * @param httpResponseCallback
	 *            the callback
	 * @throws IOException
	 *             In case an I/O exception occurs
	 */
	public void postAsync(String url, String options, HttpResponseCallback httpResponseCallback) throws IOException {
		requestsCounter ++;
		HttpClientRequestOptions httpClientRequestOptions = HttpClientFacade.parseOptions(options);
		httpResponseCallback.setOptions(httpClientRequestOptions);
		HttpPost request = HttpClientFacade.createPostRequest(url, httpClientRequestOptions);
		asyncHttpRequests.add(new AsyncHttpRequest(request, httpResponseCallback.getCallback()));
	}

	/**
	 * Performs a Async PUT request for the specified URL and options
	 *
	 * @param url
	 *            the URL
	 * @param options
	 *            the options
	 * @param httpResponseCallback
	 *            the callback
	 * @throws IOException
	 *             In case an I/O exception occurs
	 */
	public void putAsync(String url, String options, HttpResponseCallback httpResponseCallback) throws IOException {
		requestsCounter ++;
		HttpClientRequestOptions httpClientRequestOptions = HttpClientFacade.parseOptions(options);
		httpResponseCallback.setOptions(httpClientRequestOptions);
		HttpPut request = HttpClientFacade.createPutRequest(url, httpClientRequestOptions);
		asyncHttpRequests.add(new AsyncHttpRequest(request, httpResponseCallback.getCallback()));
	}

	/**
	 * Performs a Async PATCH request for the specified URL and options
	 *
	 * @param url
	 *            the URL
	 * @param options
	 *            the options
	 * @param httpResponseCallback
	 *            the callback
	 * @throws IOException
	 *             In case an I/O exception occurs
	 */
	public void patchAsync(String url, String options, HttpResponseCallback httpResponseCallback) throws IOException {
		requestsCounter ++;
		HttpClientRequestOptions httpClientRequestOptions = HttpClientFacade.parseOptions(options);
		httpResponseCallback.setOptions(httpClientRequestOptions);
		HttpPatch request = HttpClientFacade.createPatchRequest(url, httpClientRequestOptions);
		asyncHttpRequests.add(new AsyncHttpRequest(request, httpResponseCallback.getCallback()));
	}

	/**
	 * Performs a Async DELETE request for the specified URL and options
	 *
	 * @param url
	 *            the URL
	 * @param options
	 *            the options
	 * @param httpResponseCallback
	 *            the callback
	 */
	public void deleteAsync(String url, String options, HttpResponseCallback httpResponseCallback) {
		requestsCounter ++;
		HttpClientRequestOptions httpClientRequestOptions = HttpClientFacade.parseOptions(options);
		httpResponseCallback.setOptions(httpClientRequestOptions);
		HttpDelete request = HttpClientFacade.createDeleteRequest(url, httpClientRequestOptions);
		asyncHttpRequests.add(new AsyncHttpRequest(request, httpResponseCallback.getCallback()));
	}

	/**
	 * Performs a Async HEAD request for the specified URL and options
	 *
	 * @param url
	 *            the URL
	 * @param options
	 *            the options
	 * @param httpResponseCallback
	 *            the callback
	 */
	public void headAsync(String url, String options, HttpResponseCallback httpResponseCallback) {
		requestsCounter ++;
		HttpClientRequestOptions httpClientRequestOptions = HttpClientFacade.parseOptions(options);
		httpResponseCallback.setOptions(httpClientRequestOptions);
		HttpHead request = HttpClientFacade.createHeadRequest(url, httpClientRequestOptions);
		asyncHttpRequests.add(new AsyncHttpRequest(request, httpResponseCallback.getCallback()));
	}

	/**
	 * Performs a Async TRACE request for the specified URL and options
	 *
	 * @param url
	 *            the URL
	 * @param options
	 *            the options
	 * @param httpResponseCallback
	 *            the callback
	 */
	public void traceAsync(String url, String options, HttpResponseCallback httpResponseCallback) {
		requestsCounter ++;
		HttpClientRequestOptions httpClientRequestOptions = HttpClientFacade.parseOptions(options);
		httpResponseCallback.setOptions(httpClientRequestOptions);
		HttpTrace request = HttpClientFacade.createTraceRequest(url, httpClientRequestOptions);
		asyncHttpRequests.add(new AsyncHttpRequest(request, httpResponseCallback.getCallback()));
	}

	/**
	 * Execute request asynchronously
	 * 
	 * @throws InterruptedException in case an concurrency exception occurs
	 * @throws IOException in case an I/O exception occurs
	 */
	public void execute() throws InterruptedException, IOException {
		countDownLatch = new CountDownLatch(requestsCounter);
		CloseableHttpAsyncClient httpClient = HttpAsyncClients.createDefault();
		httpClient.start();
		for (AsyncHttpRequest next : asyncHttpRequests) {
			httpClient.execute(next.getRequest(), next.getCallback());
		}
		countDownLatch.await();
		httpClient.close();
	}

	private static class AsyncHttpRequest {
		private final HttpUriRequest request;
		private final FutureCallback<HttpResponse> callback;

		private AsyncHttpRequest(HttpUriRequest request, FutureCallback<HttpResponse> callback) {
			this.request = request;
			this.callback = callback;
		}

		private HttpUriRequest getRequest() {
			return request;
		}

		private FutureCallback<HttpResponse> getCallback() {
			return callback;
		}
	}

	/**
	 * The callback handler
	 */
	public class HttpResponseCallback {

		private final FutureCallback<HttpResponse> callback;
		private HttpClientRequestOptions httpClientRequestOptions;

		HttpResponseCallback(String completeCallback, String failCallback, String cancelCallback) {
			this.callback = createFutureCallback(completeCallback, failCallback, cancelCallback);
		}

		public void setOptions(HttpClientRequestOptions httpClientRequestOptions) {
			this.httpClientRequestOptions = httpClientRequestOptions;
			
		}

		public FutureCallback<HttpResponse> getCallback() {
			return callback;
		}

		private FutureCallback<HttpResponse> createFutureCallback(String completeCallback, String failCallback, String cancelCallback) {
			return new FutureCallback<HttpResponse>() {

				private Map<Object, Object> executionContext = new HashMap<Object, Object>();

				@Override
				public void completed(HttpResponse response) {
					countDownLatch.countDown();
					if (completeCallback != null) {
						executionContext.put("response", response);
						executionContext.put("httpClientRequestOptions", httpClientRequestOptions);
						executeCallback(completeCallback, executionContext);
					}
				}

				@Override
				public void failed(Exception exception) {
					countDownLatch.countDown();
					if (failCallback != null) {
						executionContext.put("exception", exception);
						executionContext.put("httpClientRequestOptions", httpClientRequestOptions);
						executeCallback(failCallback, executionContext);
					}
				}

				@Override
				public void cancelled() {
					countDownLatch.countDown();
					if (cancelCallback != null) {
						executionContext.put("httpClientRequestOptions", httpClientRequestOptions);
						executeCallback(cancelCallback, executionContext);
					}
				}

				private void executeCallback(String completeCallback, Map<Object, Object> executionContext) {
					try {
						defaultEngineExecutor.executeServiceCode(completeCallback, executionContext);
					} catch (ScriptingException e) {
						logger.error(e.getMessage(), e);
					}
				}
			};
		}
	}
}
