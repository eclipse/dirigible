/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.runtime.core.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * JSON serializer/deserializer
 *
 * @param <T>
 */
@Provider
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GsonMessageBodyHandler<T> implements MessageBodyWriter<T>, MessageBodyReader<T> {

	private static final Gson GSON = new Gson();

	@Override
	public long getSize(final T t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
		return -1;
	}

	@Override
	public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
		return true;
	}

	@Override
	public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
		return true;
	}

	@Override
	public T readFrom(final Class<T> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType,
			final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException, WebApplicationException {

		final Reader entityReader = new InputStreamReader(entityStream, "UTF-8");
		Type targetType;
		if (Collection.class.isAssignableFrom(type)) {
			targetType = genericType;
		} else {
			targetType = type;
		}

		return type.cast(GSON.fromJson(entityReader, targetType));
	}

	@Override
	public void writeTo(final T t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType,
			final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException, WebApplicationException {

		if (!String.class.isAssignableFrom(type)) {
			entityStream.write(GSON.toJson(t).getBytes("UTF-8"));
		} else {
			entityStream.write(((String) t).getBytes("UTF-8"));
		}
	}
}
