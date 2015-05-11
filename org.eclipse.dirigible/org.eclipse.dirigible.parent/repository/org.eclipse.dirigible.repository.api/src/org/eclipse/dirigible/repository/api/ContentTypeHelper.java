/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.api;

import java.util.HashMap;
import java.util.Map;

public class ContentTypeHelper {

	private static final Map<String, String> CONTENT_TYPES = new HashMap<String, String>();
	private static final Map<String, String> BINARY_CONTENT_TYPES = new HashMap<String, String>();
	private static final Map<String, String> TEXT_CONTENT_TYPES = new HashMap<String, String>();

	static {
		BINARY_CONTENT_TYPES.put("ai", "application/postscript"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("aif", "audio/x-aiff"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("aifc", "audio/x-aiff"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("aiff", "audio/x-aiff"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("apk", //$NON-NLS-1$
				"application/vnd.android.package-archive"); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("asf", "video/x-ms-asf"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("asr", "video/x-ms-asf"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("asx", "video/x-ms-asf"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("au", "audio/basic"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("avi", "video/x-msvideo"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("axs", "application/olescript"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("bcpio", "application/x-bcpio"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("bin", "application/octet-stream"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("bmp", "image/bmp"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("cat", "application/vnd.ms-pkiseccat"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("cdf", "application/x-cdf"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("cdf", "application/x-netcdf"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("cer", "application/x-x509-ca-cert"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("class", "application/octet-stream"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("clp", "application/x-msclip"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("cmx", "image/x-cmx"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("cod", "image/cis-cod"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("cpio", "application/x-cpio"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("crd", "application/x-mscardfile"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("crl", "application/pkix-crl"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("crt", "application/x-x509-ca-cert"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("csh", "application/x-csh"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("dcr", "application/x-director"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("der", "application/x-x509-ca-cert"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("dir", "application/x-director"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("dll", "application/x-msdownload"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("dms", "application/octet-stream"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("doc", "application/msword"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("dot", "application/msword"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("dvi", "application/x-dvi"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("dxr", "application/x-director"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("eps", "application/postscript"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("evy", "application/envoy"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("exe", "application/octet-stream"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("fif", "application/fractals"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("flr", "x-world/x-vrml"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("gif", "image/gif"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("gtar", "application/x-gtar"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("gz", "application/x-gzip"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("hdf", "application/x-hdf"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("hlp", "application/winhlp"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("hqx", "application/mac-binhex40"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("hta", "application/hta"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("ico", "image/x-icon"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("ief", "image/ief"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("iii", "application/x-iphone"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("ins", "application/x-internet-signup"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("isp", "application/x-internet-signup"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("jfif", "image/pipeg"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("jpe", "image/jpeg"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("jpeg", "image/jpeg"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("jpg", "image/jpeg"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("latex", "application/x-latex"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("lha", "application/octet-stream"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("lsf", "video/x-la-asf"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("lsx", "video/x-la-asf"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("lzh", "application/octet-stream"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("m13", "application/x-msmediaview"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("m14", "application/x-msmediaview"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("m3u", "audio/x-mpegurl"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("man", "application/x-troff-man"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("mdb", "application/x-msaccess"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("me", "application/x-troff-me"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("mht", "message/rfc822"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("mhtml", "message/rfc822"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("mid", "audio/mid"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("mny", "application/x-msmoney"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("mov", "video/quicktime"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("movie", "video/x-sgi-movie"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("mp2", "video/mpeg"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("mp3", "audio/mpeg"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("mpa", "video/mpeg"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("mpe", "video/mpeg"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("mpeg", "video/mpeg"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("mpg", "video/mpeg"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("mpp", "application/vnd.ms-project"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("mpv2", "video/mpeg"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("ms", "application/x-troff-ms"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("msg", "application/vnd.ms-outlook"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("mvb", "application/x-msmediaview"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("nc", "application/x-netcdf"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("nws", "message/rfc822"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("oda", "application/oda"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("p10", "application/pkcs10"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("p12", "application/x-pkcs12"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("p7b", "application/x-pkcs7-certificates"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("p7c", "application/x-pkcs7-mime"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("p7m", "application/x-pkcs7-mime"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("p7r", "application/x-pkcs7-certreqresp"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("p7s", "application/x-pkcs7-signature"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("pbm", "image/x-portable-bitmap"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("pdf", "application/pdf"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("pfx", "application/x-pkcs12"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("pgm", "image/x-portable-graymap"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("pko", "application/ynd.ms-pkipko"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("pma", "application/x-perfmon"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("pmc", "application/x-perfmon"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("pml", "application/x-perfmon"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("pmr", "application/x-perfmon"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("pmw", "application/x-perfmon"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("png", "image/png"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("pnm", "image/x-portable-anymap"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("pot", "application/vnd.ms-powerpoint"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("ppm", "image/x-portable-pixmap"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("pps", "application/vnd.ms-powerpoint"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("ppt", "application/vnd.ms-powerpoint"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("prf", "application/pics-rules"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("ps", "application/postscript"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("pub", "application/x-mspublisher"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("qt", "video/quicktime"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("ra", "audio/x-pn-realaudio"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("ram", "audio/x-pn-realaudio"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("ras", "image/x-cmu-raster"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("rgb", "image/x-rgb"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("rmi", "audio/mid"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("roff", "application/x-troff"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("scd", "application/x-msschedule"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES
				.put("setpay", "application/set-payment-initiation"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("setreg", //$NON-NLS-1$
				"application/set-registration-initiation"); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("shar", "application/x-shar"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("sit", "application/x-stuffit"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("snd", "audio/basic"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("spc", "application/x-pkcs7-certificates"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("spl", "application/futuresplash"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("src", "application/x-wais-source"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("sst", "application/vnd.ms-pkicertstore"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("stl", "application/vnd.ms-pkistl"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("sv4cpio", "application/x-sv4cpio"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("sv4crc", "application/x-sv4crc"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("swf", "application/x-shockwave-flash"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("t", "application/x-troff"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("tar", "application/x-tar"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("tcl", "application/x-tcl"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("tex", "application/x-tex"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("texi", "application/x-texinfo"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("texinfo", "application/x-texinfo"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("tgz", "application/x-compressed"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("tif", "image/tiff"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("tiff", "image/tiff"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("tr", "application/x-troff"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("trm", "application/x-msterminal"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("ustar", "application/x-ustar"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("vrml", "x-world/x-vrml"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("wav", "audio/x-wav"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("wcm", "application/vnd.ms-works"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("wdb", "application/vnd.ms-works"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("wks", "application/vnd.ms-works"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("wmf", "application/x-msmetafile"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("wps", "application/vnd.ms-works"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("wri", "application/x-mswrite"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("wrl", "x-world/x-vrml"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("wrz", "x-world/x-vrml"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("xaf", "x-world/x-vrml"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("xbm", "image/x-xbitmap"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("xla", "application/vnd.ms-excel"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("xlc", "application/vnd.ms-excel"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("xlm", "application/vnd.ms-excel"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("xls", "application/vnd.ms-excel"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("xlt", "application/vnd.ms-excel"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("xlw", "application/vnd.ms-excel"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("xof", "x-world/x-vrml"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("xpm", "image/x-xpixmap"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("xwd", "image/x-xwindowdump"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("z", "application/x-compress"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("zip", "application/zip"); //$NON-NLS-1$ //$NON-NLS-2$
		
		BINARY_CONTENT_TYPES.put("ttf", "application/octet-stream"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("svg", "image/svg+xml"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("ttf", "application/x-font-ttf"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("otf", "application/x-font-opentype"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("woff", "application/font-woff"); //$NON-NLS-1$ //$NON-NLS-2$
		BINARY_CONTENT_TYPES.put("eot", "application/vnd.ms-fontobject"); //$NON-NLS-1$ //$NON-NLS-2$
		
		

		TEXT_CONTENT_TYPES.put("txt", "text/plain"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("xml", "text/xml"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("json", "application/json"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("js", "application/javascript"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("jslib", "application/javascript"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("table", "application/json"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("view", "application/json"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("entity", "application/json"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("ws", "application/json"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("routes", "text/xml"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("wsdl", "text/xml"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("xsl", "text/xml"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("xslt", "text/xml"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("bas", "text/plain"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("c", "text/plain"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("css", "text/css"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("etx", "text/x-setext"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("h", "text/plain"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("htm", "text/html"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("html", "text/html"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("htt", "text/webviewhtml"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("sct", "text/scriptlet"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("rtx", "text/richtext"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("rtf", "application/rtf"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("sh", "application/x-sh"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("stm", "text/html"); //$NON-NLS-1$ //$NON-NLS-2$
//		TEXT_CONTENT_TYPES.put("svg", "image/svg+xml"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("tsv", "text/tab-separated-values"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("uls", "text/iuls"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("htc", "text/x-component"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("vcf", "text/x-vcard"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("wiki", "text/html"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("wikis", "text/html"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("md", "text/html"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("markdown", "text/html"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("mdown", "text/html"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("mkdn", "text/html"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("markdown", "text/html"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("mkd", "text/html"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("mdwn", "text/html"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("textile", "text/html"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("confluence", "text/html"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("tracwiki", "text/html"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("twiki", "text/html"); //$NON-NLS-1$ //$NON-NLS-2$

		CONTENT_TYPES.putAll(BINARY_CONTENT_TYPES);
		CONTENT_TYPES.putAll(TEXT_CONTENT_TYPES);

	}

	public static final String DEFAULT_CONTENT_TYPE = "text/plain"; //$NON-NLS-1$

	public static String getContentType(String extension) {
		if (extension == null) {
			return DEFAULT_CONTENT_TYPE;
		}
		String contentType = CONTENT_TYPES.get(extension.toLowerCase());
		if (contentType == null) {
			contentType = DEFAULT_CONTENT_TYPE;
		}

		return contentType;
	}

	public static boolean isBinary(String contentType) {
		return BINARY_CONTENT_TYPES.containsValue(contentType);
	}

	public static String getExtension(String filename) {
		if (filename == null) {
			return ""; //$NON-NLS-1$
		}
		int dotIndex = filename.lastIndexOf("."); //$NON-NLS-1$
		if (dotIndex != -1) {
			return filename.substring(dotIndex + 1);
		} else {
			return ""; //$NON-NLS-1$
		}
	}
}
