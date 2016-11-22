/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* globals $ java javax engine */
/* eslint-env node, dirigible */

var streams = require("io/streams");

/**
 * Creates an input stream from a stream contaning a ZIP archive
 */
exports.createZipInputStream = function(inputStream) {
	var internalZipInputStream = new java.util.zip.ZipInputStream(inputStream.getInternalObject());
	return new ZipInputStream(internalZipInputStream);
};

/**
 * Creates an output stream for a ZIP archive
 */
exports.createZipOutputStream = function(outputStream) {
	var internalZipOutputStream = new java.util.zip.ZipOutputStream(outputStream.getInternalObject());
	return new ZipOutputStream(internalZipOutputStream);
};



/**
 * ZipInputStream object
 */
function ZipInputStream(internalZipInputStream) {
	this.internalZipInputStream = internalZipInputStream;

	this.getInternalObject = function() {
		return this.internalZipInputStream;
	};

	this.getNextEntry = function() {
	    var internalZipEntry = this.internalZipInputStream.getNextEntry();
	    if (internalZipEntry === null) {
	    	return null;
    	}
		return new ZipEntry(internalZipEntry, this.internalZipInputStream, null);
	};
	
	this.close = function() {
		return this.internalZipInputStream.close();
	};

}

/**
 * ZipOutputStream object
 */
function ZipOutputStream(internalZipOutputStream) {
	this.internalZipOutputStream = internalZipOutputStream;

	this.getInternalObject = function() {
		return this.internalZipOutputStream;
	};
	
	this.createZipEntry = function(name) {
		var internalZipEntry = new java.util.zip.ZipEntry(name);
		var zipEntry = new ZipEntry(internalZipEntry, null, this.internalZipOutputStream);
		return zipEntry;
	};

	this.putNextEntry = function(zipEntry) {
		this.internalZipOutputStream.putNextEntry(zipEntry.getInternalObject());
		
		if (!zipEntry.isDirectory()){
			var internalBytes = streams.toJavaBytes(zipEntry.getBytes());
			this.internalZipOutputStream.write(internalBytes);
		}
//		zipEntry.getInternalObject().closeEntry();
	};

	this.close = function() {
		this.internalZipOutputStream.finish();
		this.internalZipOutputStream.flush();
		this.internalZipOutputStream.close();
	};

}

/**
 * ZipEntry object
 */
function ZipEntry(internalZipEntry, internalZipInputStream, internalZipOutputStream) {
	this.internalZipEntry = internalZipEntry;
	this.internalZipInputStream = internalZipInputStream;
	this.internalZipOutputStream = internalZipOutputStream;
	this.bytes = null;

	this.getInternalObject = function() {
		return this.internalZipEntry;
	};
	
	this.getInternalZipInputStream = function() {
		return this.internalZipInputStream;
	};
	
	this.getInternalZipOutputStream = function() {
		return this.internalZipOutputStream;
	};
	
	this.getBytes = function() {
		if (this.internalZipOutputStream === null) {
			throw new Error("This ZipEntry is not writeable");
		}
		return this.bytes;
	};

	this.getName = function() {
		return this.internalZipEntry.getName();
	};
	
	this.getSize = function() {
		return this.internalZipEntry.getSize();
	};
	
	this.getCompressedSize = function() {
		return this.internalZipEntry.getCompressedSize();
	};
	
	this.getTime = function() {
		return this.internalZipEntry.getTime();
	};
	
	this.getCrc = function() {
		return this.internalZipEntry.getCrc();
	};
	
	this.getComment = function() {
		return this.internalZipEntry.getComment();
	};
	
	this.isDirectory = function() {
		return this.internalZipEntry.isDirectory();
	};
	
	this.readData = function() {
		if (this.internalZipInputStream === null) {
			throw new Error("This ZipEntry is not readable");
		}
		var internalBytesBuffer = java.lang.reflect.Array.newInstance(java.lang.Byte.TYPE, 2048);
		var bytes = null;
		var output = null;
		try {
			output = new java.io.ByteArrayOutputStream();
			var len = 0;
			while ((len = this.internalZipInputStream.read(internalBytesBuffer)) > 0) {
				output.write(internalBytesBuffer, 0, len);
			}
			bytes = streams.toJavaScriptBytes(output.toByteArray());
		} finally {
			if (output !== null) {
				output.close();
			}
		}
		return bytes;
	};
	
	this.writeData = function(bytes) {
		if (this.internalZipOutputStream === null) {
			throw new Error("This ZipEntry is not writeable");
		}
		this.bytes = bytes;
	}

}


