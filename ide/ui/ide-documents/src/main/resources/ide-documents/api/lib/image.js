/*
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
var streams = require('io/v3/streams');
var imageIO = require('io/v3/image');
var documentLib = require("ide-documents/api/lib/document");
 
 
exports.uploadImageWithResize = function(folder, name, image, width, height) {
	
    var fileName = name;
    var mimetype = image.getContentType();
    var originalInputStream = image.getInputStream();
    var inputStream = new streams.InputStream();
    inputStream.uuid = originalInputStream.uuid;
               
    var imageType = mimetype.split('/')[1];
    
    var resizedInputStream = imageIO.resize(inputStream, imageType, width, height);
    
    image.getInputStream = function(){
    	return resizedInputStream;//new streams.InputStream(fis);
    }
    
    documentLib.uploadDocument(folder, image);
}
