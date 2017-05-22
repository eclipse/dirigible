/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.api;

import java.util.HashMap;
import java.util.Map;

public class ContentTypeHelper {

	public static final String TEXT_X_VCARD = "text/x-vcard";
	public static final String TEXT_X_COMPONENT = "text/x-component";
	public static final String TEXT_IULS = "text/iuls";
	public static final String TEXT_DELIMITER_SEPARATED_VALUES = "text/delimiter-separated-values";
	public static final String TEXT_TAB_SEPARATED_VALUES = "text/tab-separated-values";
	public static final String APPLICATION_X_SH = "application/x-sh";
	public static final String APPLICATION_RTF = "application/rtf";
	public static final String TEXT_RICHTEXT = "text/richtext";
	public static final String TEXT_SCRIPTLET = "text/scriptlet";
	public static final String TEXT_WEBVIEWHTML = "text/webviewhtml";
	public static final String TEXT_HTML = "text/html";
	public static final String TEXT_X_SETEXT = "text/x-setext";
	public static final String TEXT_CSS = "text/css";
	public static final String APPLICATION_JAVASCRIPT = "application/javascript";
	public static final String APPLICATION_JSON = "application/json";
	public static final String TEXT_XML = "text/xml";
	public static final String TEXT_PLAIN = "text/plain";
	public static final String APPLICATION_VND_MS_FONTOBJECT = "application/vnd.ms-fontobject";
	public static final String APPLICATION_FONT_WOFF = "application/font-woff";
	public static final String APPLICATION_X_FONT_OPENTYPE = "application/x-font-opentype";
	public static final String APPLICATION_X_FONT_TTF = "application/x-font-ttf";
	public static final String IMAGE_SVG_XML = "image/svg+xml";
	public static final String APPLICATION_ZIP = "application/zip";
	public static final String APPLICATION_X_COMPRESS = "application/x-compress";
	public static final String IMAGE_X_XWINDOWDUMP = "image/x-xwindowdump";
	public static final String IMAGE_X_XPIXMAP = "image/x-xpixmap";
	public static final String APPLICATION_VND_MS_EXCEL = "application/vnd.ms-excel";
	public static final String IMAGE_X_XBITMAP = "image/x-xbitmap";
	public static final String APPLICATION_X_MSWRITE = "application/x-mswrite";
	public static final String APPLICATION_X_MSMETAFILE = "application/x-msmetafile";
	public static final String APPLICATION_VND_MS_WORKS = "application/vnd.ms-works";
	public static final String AUDIO_X_WAV = "audio/x-wav";
	public static final String APPLICATION_X_USTAR = "application/x-ustar";
	public static final String APPLICATION_X_MSTERMINAL = "application/x-msterminal";
	public static final String IMAGE_TIFF = "image/tiff";
	public static final String APPLICATION_X_COMPRESSED = "application/x-compressed";
	public static final String APPLICATION_X_TEXINFO = "application/x-texinfo";
	public static final String APPLICATION_X_TEX = "application/x-tex";
	public static final String APPLICATION_X_TCL = "application/x-tcl";
	public static final String APPLICATION_X_TAR = "application/x-tar";
	public static final String APPLICATION_X_SHOCKWAVE_FLASH = "application/x-shockwave-flash";
	public static final String APPLICATION_X_SV4CRC = "application/x-sv4crc";
	public static final String APPLICATION_X_SV4CPIO = "application/x-sv4cpio";
	public static final String APPLICATION_VND_MS_PKISTL = "application/vnd.ms-pkistl";
	public static final String APPLICATION_VND_MS_PKICERTSTORE = "application/vnd.ms-pkicertstore";
	public static final String APPLICATION_X_WAIS_SOURCE = "application/x-wais-source";
	public static final String APPLICATION_FUTURESPLASH = "application/futuresplash";
	public static final String APPLICATION_X_STUFFIT = "application/x-stuffit";
	public static final String APPLICATION_X_SHAR = "application/x-shar";
	public static final String APPLICATION_SET_REGISTRATION_INITIATION = "application/set-registration-initiation";
	public static final String APPLICATION_SET_PAYMENT_INITIATION = "application/set-payment-initiation";
	public static final String APPLICATION_X_MSSCHEDULE = "application/x-msschedule";
	public static final String APPLICATION_X_TROFF = "application/x-troff";
	public static final String IMAGE_X_RGB = "image/x-rgb";
	public static final String IMAGE_X_CMU_RASTER = "image/x-cmu-raster";
	public static final String AUDIO_X_PN_REALAUDIO = "audio/x-pn-realaudio";
	public static final String APPLICATION_X_MSPUBLISHER = "application/x-mspublisher";
	public static final String APPLICATION_PICS_RULES = "application/pics-rules";
	public static final String IMAGE_X_PORTABLE_PIXMAP = "image/x-portable-pixmap";
	public static final String APPLICATION_VND_MS_POWERPOINT = "application/vnd.ms-powerpoint";
	public static final String IMAGE_X_PORTABLE_ANYMAP = "image/x-portable-anymap";
	public static final String IMAGE_PNG = "image/png";
	public static final String APPLICATION_X_PERFMON = "application/x-perfmon";
	public static final String APPLICATION_YND_MS_PKIPKO = "application/ynd.ms-pkipko";
	public static final String IMAGE_X_PORTABLE_GRAYMAP = "image/x-portable-graymap";
	public static final String APPLICATION_PDF = "application/pdf";
	public static final String IMAGE_X_PORTABLE_BITMAP = "image/x-portable-bitmap";
	public static final String APPLICATION_X_PKCS7_SIGNATURE = "application/x-pkcs7-signature";
	public static final String APPLICATION_X_PKCS7_CERTREQRESP = "application/x-pkcs7-certreqresp";
	public static final String APPLICATION_X_PKCS7_MIME = "application/x-pkcs7-mime";
	public static final String APPLICATION_X_PKCS7_CERTIFICATES = "application/x-pkcs7-certificates";
	public static final String APPLICATION_X_PKCS12 = "application/x-pkcs12";
	public static final String APPLICATION_PKCS10 = "application/pkcs10";
	public static final String APPLICATION_ODA = "application/oda";
	public static final String APPLICATION_VND_MS_OUTLOOK = "application/vnd.ms-outlook";
	public static final String APPLICATION_X_TROFF_MS = "application/x-troff-ms";
	public static final String APPLICATION_VND_MS_PROJECT = "application/vnd.ms-project";
	public static final String AUDIO_MPEG = "audio/mpeg";
	public static final String VIDEO_MPEG = "video/mpeg";
	public static final String VIDEO_X_SGI_MOVIE = "video/x-sgi-movie";
	public static final String VIDEO_QUICKTIME = "video/quicktime";
	public static final String APPLICATION_X_MSMONEY = "application/x-msmoney";
	public static final String AUDIO_MID = "audio/mid";
	public static final String MESSAGE_RFC822 = "message/rfc822";
	public static final String APPLICATION_X_TROFF_ME = "application/x-troff-me";
	public static final String APPLICATION_X_MSACCESS = "application/x-msaccess";
	public static final String APPLICATION_X_TROFF_MAN = "application/x-troff-man";
	public static final String AUDIO_X_MPEGURL = "audio/x-mpegurl";
	public static final String APPLICATION_X_MSMEDIAVIEW = "application/x-msmediaview";
	public static final String VIDEO_X_LA_ASF = "video/x-la-asf";
	public static final String APPLICATION_X_LATEX = "application/x-latex";
	public static final String IMAGE_JPEG = "image/jpeg";
	public static final String IMAGE_PIPEG = "image/pipeg";
	public static final String APPLICATION_X_INTERNET_SIGNUP = "application/x-internet-signup";
	public static final String APPLICATION_X_IPHONE = "application/x-iphone";
	public static final String IMAGE_IEF = "image/ief";
	public static final String IMAGE_X_ICON = "image/x-icon";
	public static final String APPLICATION_HTA = "application/hta";
	public static final String APPLICATION_MAC_BINHEX40 = "application/mac-binhex40";
	public static final String APPLICATION_WINHLP = "application/winhlp";
	public static final String APPLICATION_X_HDF = "application/x-hdf";
	public static final String APPLICATION_X_GZIP = "application/x-gzip";
	public static final String APPLICATION_X_GTAR = "application/x-gtar";
	public static final String IMAGE_GIF = "image/gif";
	public static final String X_WORLD_X_VRML = "x-world/x-vrml";
	public static final String APPLICATION_FRACTALS = "application/fractals";
	public static final String APPLICATION_ENVOY = "application/envoy";
	public static final String APPLICATION_X_DVI = "application/x-dvi";
	public static final String APPLICATION_MSWORD = "application/msword";
	public static final String APPLICATION_X_MSDOWNLOAD = "application/x-msdownload";
	public static final String APPLICATION_X_DIRECTOR = "application/x-director";
	public static final String APPLICATION_X_CSH = "application/x-csh";
	public static final String APPLICATION_PKIX_CRL = "application/pkix-crl";
	public static final String APPLICATION_X_MSCARDFILE = "application/x-mscardfile";
	public static final String APPLICATION_X_CPIO = "application/x-cpio";
	public static final String IMAGE_CIS_COD = "image/cis-cod";
	public static final String IMAGE_X_CMX = "image/x-cmx";
	public static final String APPLICATION_X_MSCLIP = "application/x-msclip";
	public static final String APPLICATION_X_X509_CA_CERT = "application/x-x509-ca-cert";
	public static final String APPLICATION_X_NETCDF = "application/x-netcdf";
	public static final String APPLICATION_X_CDF = "application/x-cdf";
	public static final String APPLICATION_VND_MS_PKISECCAT = "application/vnd.ms-pkiseccat";
	public static final String IMAGE_BMP = "image/bmp";
	public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
	public static final String APPLICATION_X_BCPIO = "application/x-bcpio";
	public static final String APPLICATION_OLESCRIPT = "application/olescript";
	public static final String VIDEO_X_MSVIDEO = "video/x-msvideo";
	public static final String AUDIO_BASIC = "audio/basic";
	public static final String VIDEO_X_MS_ASF = "video/x-ms-asf";
	public static final String APPLICATION_VND_ANDROID_PACKAGE_ARCHIVE = "application/vnd.android.package-archive";
	public static final String AUDIO_X_AIFF = "audio/x-aiff";
	public static final String APPLICATION_POSTSCRIPT = "application/postscript";

	private static final Map<String, String> CONTENT_TYPES = new HashMap<String, String>();
	private static final Map<String, String> BINARY_CONTENT_TYPES = new HashMap<String, String>();
	private static final Map<String, String> TEXT_CONTENT_TYPES = new HashMap<String, String>();

	static {
		BINARY_CONTENT_TYPES.put("ai", APPLICATION_POSTSCRIPT); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("aif", AUDIO_X_AIFF); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("aifc", AUDIO_X_AIFF); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("aiff", AUDIO_X_AIFF); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("apk", APPLICATION_VND_ANDROID_PACKAGE_ARCHIVE); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("asf", VIDEO_X_MS_ASF); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("asr", VIDEO_X_MS_ASF); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("asx", VIDEO_X_MS_ASF); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("au", AUDIO_BASIC); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("avi", VIDEO_X_MSVIDEO); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("axs", APPLICATION_OLESCRIPT); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("bcpio", APPLICATION_X_BCPIO); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("bin", APPLICATION_OCTET_STREAM); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("bmp", IMAGE_BMP); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("cat", APPLICATION_VND_MS_PKISECCAT); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("cdf", APPLICATION_X_CDF); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("cdf", APPLICATION_X_NETCDF); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("cer", APPLICATION_X_X509_CA_CERT); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("class", APPLICATION_OCTET_STREAM); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("clp", APPLICATION_X_MSCLIP); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("cmx", IMAGE_X_CMX); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("cod", IMAGE_CIS_COD); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("cpio", APPLICATION_X_CPIO); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("crd", APPLICATION_X_MSCARDFILE); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("crl", APPLICATION_PKIX_CRL); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("crt", APPLICATION_X_X509_CA_CERT); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("csh", APPLICATION_X_CSH); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("dcr", APPLICATION_X_DIRECTOR); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("der", APPLICATION_X_X509_CA_CERT); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("dir", APPLICATION_X_DIRECTOR); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("dll", APPLICATION_X_MSDOWNLOAD); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("dms", APPLICATION_OCTET_STREAM); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("doc", APPLICATION_MSWORD); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("dot", APPLICATION_MSWORD); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("dvi", APPLICATION_X_DVI); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("dxr", APPLICATION_X_DIRECTOR); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("eps", APPLICATION_POSTSCRIPT); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("evy", APPLICATION_ENVOY); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("exe", APPLICATION_OCTET_STREAM); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("fif", APPLICATION_FRACTALS); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("flr", X_WORLD_X_VRML); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("gif", IMAGE_GIF); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("gtar", APPLICATION_X_GTAR); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("gz", APPLICATION_X_GZIP); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("hdf", APPLICATION_X_HDF); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("hlp", APPLICATION_WINHLP); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("hqx", APPLICATION_MAC_BINHEX40); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("hta", APPLICATION_HTA); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("ico", IMAGE_X_ICON); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("ief", IMAGE_IEF); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("iii", APPLICATION_X_IPHONE); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("ins", APPLICATION_X_INTERNET_SIGNUP); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("isp", APPLICATION_X_INTERNET_SIGNUP); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("jfif", IMAGE_PIPEG); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("jpe", IMAGE_JPEG); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("jpeg", IMAGE_JPEG); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("jpg", IMAGE_JPEG); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("latex", APPLICATION_X_LATEX); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("lha", APPLICATION_OCTET_STREAM); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("lsf", VIDEO_X_LA_ASF); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("lsx", VIDEO_X_LA_ASF); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("lzh", APPLICATION_OCTET_STREAM); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("m13", APPLICATION_X_MSMEDIAVIEW); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("m14", APPLICATION_X_MSMEDIAVIEW); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("m3u", AUDIO_X_MPEGURL); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("man", APPLICATION_X_TROFF_MAN); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("mdb", APPLICATION_X_MSACCESS); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("me", APPLICATION_X_TROFF_ME); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("mht", MESSAGE_RFC822); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("mhtml", MESSAGE_RFC822); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("mid", AUDIO_MID); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("mny", APPLICATION_X_MSMONEY); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("mov", VIDEO_QUICKTIME); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("movie", VIDEO_X_SGI_MOVIE); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("mp2", VIDEO_MPEG); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("mp3", AUDIO_MPEG); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("mpa", VIDEO_MPEG); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("mpe", VIDEO_MPEG); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("mpeg", VIDEO_MPEG); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("mpg", VIDEO_MPEG); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("mpp", APPLICATION_VND_MS_PROJECT); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("mpv2", VIDEO_MPEG); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("ms", APPLICATION_X_TROFF_MS); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("msg", APPLICATION_VND_MS_OUTLOOK); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("mvb", APPLICATION_X_MSMEDIAVIEW); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("nc", APPLICATION_X_NETCDF); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("nws", MESSAGE_RFC822); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("oda", APPLICATION_ODA); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("p10", APPLICATION_PKCS10); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("p12", APPLICATION_X_PKCS12); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("p7b", APPLICATION_X_PKCS7_CERTIFICATES); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("p7c", APPLICATION_X_PKCS7_MIME); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("p7m", APPLICATION_X_PKCS7_MIME); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("p7r", APPLICATION_X_PKCS7_CERTREQRESP); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("p7s", APPLICATION_X_PKCS7_SIGNATURE); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("pbm", IMAGE_X_PORTABLE_BITMAP); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("pdf", APPLICATION_PDF); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("pfx", APPLICATION_X_PKCS12); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("pgm", IMAGE_X_PORTABLE_GRAYMAP); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("pko", APPLICATION_YND_MS_PKIPKO); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("pma", APPLICATION_X_PERFMON); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("pmc", APPLICATION_X_PERFMON); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("pml", APPLICATION_X_PERFMON); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("pmr", APPLICATION_X_PERFMON); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("pmw", APPLICATION_X_PERFMON); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("png", IMAGE_PNG); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("pnm", IMAGE_X_PORTABLE_ANYMAP); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("pot", APPLICATION_VND_MS_POWERPOINT); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("ppm", IMAGE_X_PORTABLE_PIXMAP); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("pps", APPLICATION_VND_MS_POWERPOINT); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("ppt", APPLICATION_VND_MS_POWERPOINT); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("prf", APPLICATION_PICS_RULES); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("ps", APPLICATION_POSTSCRIPT); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("pub", APPLICATION_X_MSPUBLISHER); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("qt", VIDEO_QUICKTIME); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("ra", AUDIO_X_PN_REALAUDIO); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("ram", AUDIO_X_PN_REALAUDIO); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("ras", IMAGE_X_CMU_RASTER); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("rgb", IMAGE_X_RGB); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("rmi", AUDIO_MID); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("roff", APPLICATION_X_TROFF); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("scd", APPLICATION_X_MSSCHEDULE); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("setpay", APPLICATION_SET_PAYMENT_INITIATION); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("setreg", APPLICATION_SET_REGISTRATION_INITIATION); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("shar", APPLICATION_X_SHAR); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("sit", APPLICATION_X_STUFFIT); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("snd", AUDIO_BASIC); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("spc", APPLICATION_X_PKCS7_CERTIFICATES); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("spl", APPLICATION_FUTURESPLASH); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("src", APPLICATION_X_WAIS_SOURCE); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("sst", APPLICATION_VND_MS_PKICERTSTORE); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("stl", APPLICATION_VND_MS_PKISTL); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("sv4cpio", APPLICATION_X_SV4CPIO); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("sv4crc", APPLICATION_X_SV4CRC); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("swf", APPLICATION_X_SHOCKWAVE_FLASH); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("t", APPLICATION_X_TROFF); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("tar", APPLICATION_X_TAR); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("tcl", APPLICATION_X_TCL); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("tex", APPLICATION_X_TEX); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("texi", APPLICATION_X_TEXINFO); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("texinfo", APPLICATION_X_TEXINFO); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("tgz", APPLICATION_X_COMPRESSED); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("tif", IMAGE_TIFF); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("tiff", IMAGE_TIFF); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("tr", APPLICATION_X_TROFF); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("trm", APPLICATION_X_MSTERMINAL); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("ustar", APPLICATION_X_USTAR); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("vrml", X_WORLD_X_VRML); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("wav", AUDIO_X_WAV); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("wcm", APPLICATION_VND_MS_WORKS); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("wdb", APPLICATION_VND_MS_WORKS); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("wks", APPLICATION_VND_MS_WORKS); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("wmf", APPLICATION_X_MSMETAFILE); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("wps", APPLICATION_VND_MS_WORKS); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("wri", APPLICATION_X_MSWRITE); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("wrl", X_WORLD_X_VRML); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("wrz", X_WORLD_X_VRML); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("xaf", X_WORLD_X_VRML); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("xbm", IMAGE_X_XBITMAP); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("xla", APPLICATION_VND_MS_EXCEL); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("xlc", APPLICATION_VND_MS_EXCEL); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("xlm", APPLICATION_VND_MS_EXCEL); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("xls", APPLICATION_VND_MS_EXCEL); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("xlt", APPLICATION_VND_MS_EXCEL); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("xlw", APPLICATION_VND_MS_EXCEL); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("xof", X_WORLD_X_VRML); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("xpm", IMAGE_X_XPIXMAP); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("xwd", IMAGE_X_XWINDOWDUMP); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("z", APPLICATION_X_COMPRESS); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("zip", APPLICATION_ZIP); //$NON-NLS-1$

		BINARY_CONTENT_TYPES.put("ttf", APPLICATION_OCTET_STREAM); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("svg", IMAGE_SVG_XML); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("ttf", APPLICATION_X_FONT_TTF); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("otf", APPLICATION_X_FONT_OPENTYPE); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("woff", APPLICATION_FONT_WOFF); //$NON-NLS-1$
		BINARY_CONTENT_TYPES.put("eot", APPLICATION_VND_MS_FONTOBJECT); //$NON-NLS-1$

		TEXT_CONTENT_TYPES.put("txt", TEXT_PLAIN); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("xml", TEXT_XML); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("json", APPLICATION_JSON); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("js", APPLICATION_JAVASCRIPT); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("jslib", APPLICATION_JAVASCRIPT); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("table", APPLICATION_JSON); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("view", APPLICATION_JSON); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("entity", APPLICATION_JSON); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("ws", APPLICATION_JSON); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("routes", TEXT_XML); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("wsdl", TEXT_XML); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("xsl", TEXT_XML); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("xslt", TEXT_XML); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("bas", TEXT_PLAIN); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("c", TEXT_PLAIN); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("css", TEXT_CSS); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("etx", TEXT_X_SETEXT); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("h", TEXT_PLAIN); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("htm", TEXT_HTML); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("html", TEXT_HTML); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("htt", TEXT_WEBVIEWHTML); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("sct", TEXT_SCRIPTLET); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("rtx", TEXT_RICHTEXT); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("rtf", APPLICATION_RTF); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("sh", APPLICATION_X_SH); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("stm", TEXT_HTML); //$NON-NLS-1$
		// TEXT_CONTENT_TYPES.put("svg", "image/svg+xml"); //$NON-NLS-1$ //$NON-NLS-2$
		TEXT_CONTENT_TYPES.put("tsv", TEXT_TAB_SEPARATED_VALUES); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("dsv", TEXT_DELIMITER_SEPARATED_VALUES); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("uls", TEXT_IULS); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("htc", TEXT_X_COMPONENT); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("vcf", TEXT_X_VCARD); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("wiki", TEXT_HTML); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("wikis", TEXT_HTML); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("md", TEXT_HTML); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("markdown", TEXT_HTML); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("mdown", TEXT_HTML); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("mkdn", TEXT_HTML); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("markdown", TEXT_HTML); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("mkd", TEXT_HTML); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("mdwn", TEXT_HTML); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("textile", TEXT_HTML); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("confluence", TEXT_HTML); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("tracwiki", TEXT_HTML); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.put("twiki", TEXT_HTML); //$NON-NLS-1$

		CONTENT_TYPES.putAll(BINARY_CONTENT_TYPES);
		CONTENT_TYPES.putAll(TEXT_CONTENT_TYPES);

	}

	public static final String DEFAULT_CONTENT_TYPE = TEXT_PLAIN;

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
