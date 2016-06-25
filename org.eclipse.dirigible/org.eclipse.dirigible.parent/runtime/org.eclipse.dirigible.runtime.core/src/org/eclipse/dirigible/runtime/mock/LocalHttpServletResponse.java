/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.mock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class LocalHttpServletResponse implements HttpServletResponse {

	// private ByteArrayOutputStream baos;

	private LocalServletOutputStream sos;

	private PrintWriter printWriter;

	public LocalHttpServletResponse(ByteArrayOutputStream baos) {
		// this.baos = baos;
		this.printWriter = new PrintWriter(baos);
		this.sos = new LocalServletOutputStream(printWriter);

	}

	@Override
	public String getCharacterEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return this.sos;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return this.printWriter;
	}

	@Override
	public void setCharacterEncoding(String charset) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setContentLength(int len) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setContentType(String type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBufferSize(int size) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getBufferSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void flushBuffer() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetBuffer() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isCommitted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLocale(Locale loc) {
		// TODO Auto-generated method stub

	}

	@Override
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addCookie(Cookie cookie) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean containsHeader(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String encodeURL(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeRedirectURL(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeUrl(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeRedirectUrl(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendError(int sc, String msg) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendError(int sc) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendRedirect(String location) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDateHeader(String name, long date) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addDateHeader(String name, long date) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setHeader(String name, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addHeader(String name, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setIntHeader(String name, int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addIntHeader(String name, int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setStatus(int sc) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setStatus(int sc, String sm) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getHeader(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getHeaders(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getHeaderNames() {
		// TODO Auto-generated method stub
		return null;
	}

	static class LocalServletOutputStream extends ServletOutputStream {

		private PrintWriter writer;

		LocalServletOutputStream(PrintWriter writer) {
			this.writer = writer;
		}

		@Override
		public void write(int b) throws IOException {
			writer.write(b);
		}

		@Override
		public void print(boolean arg0) throws IOException {
			writer.print(arg0);
		}

		@Override
		public void print(char c) throws IOException {
			writer.print(c);
		}

		@Override
		public void print(double d) throws IOException {
			writer.print(d);
		}

		@Override
		public void print(float f) throws IOException {
			writer.print(f);
		}

		@Override
		public void print(int i) throws IOException {
			writer.print(i);
		}

		@Override
		public void print(long l) throws IOException {
			writer.print(l);
		}

		@Override
		public void print(String arg0) throws IOException {
			writer.print(arg0);
		}

		@Override
		public void println() throws IOException {
			writer.println();
		}

		@Override
		public void println(boolean b) throws IOException {
			writer.println(b);
		}

		@Override
		public void println(char c) throws IOException {
			writer.println(c);
		}

		@Override
		public void println(double d) throws IOException {
			super.println(d);
		}

		@Override
		public void println(float f) throws IOException {
			super.println(f);
		}

		@Override
		public void println(int i) throws IOException {
			super.println(i);
		}

		@Override
		public void println(long l) throws IOException {
			super.println(l);
		}

		@Override
		public void println(String s) throws IOException {
			super.println(s);
		}

		@Override
		public boolean isReady() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void setWriteListener(WriteListener writeListener) {
			// TODO Auto-generated method stub

		}

		@Override
		public void flush() throws IOException {
			writer.flush();
		}

	}

	@Override
	public void setContentLengthLong(long len) {
		// TODO Auto-generated method stub

	}
}
