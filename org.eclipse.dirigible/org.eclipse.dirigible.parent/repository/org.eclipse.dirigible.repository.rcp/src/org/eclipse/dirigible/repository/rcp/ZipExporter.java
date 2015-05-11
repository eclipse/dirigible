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

package org.eclipse.dirigible.repository.rcp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipExporter {
	
	public static void zip(List<String> inputFolders, ZipOutputStream zipOutputStream)  throws IOException {
		for (String inputFolder : inputFolders) {
			String workspaceFolder = RCPWorkspaceMapper.getMappedName(inputFolder);
			zip(workspaceFolder, zipOutputStream);
		}
    }
	
	public static void zip(String inputFolder, ZipOutputStream zipOutputStream)  throws IOException {

        File inputFile = new File(inputFolder);

        if (inputFile.isFile())
            zipFile(inputFile,"", zipOutputStream);
        else if (inputFile.isDirectory())
            zipFolder(zipOutputStream, inputFile,"");

        zipOutputStream.close();
    }


    public static void zipFolder(ZipOutputStream zipOutputStream, File inputFolder, String parentName)  throws IOException {

        String myname = parentName + inputFolder.getName() + File.separator;

        ZipEntry folderZipEntry = new ZipEntry(myname);
        zipOutputStream.putNextEntry(folderZipEntry);

        File[] contents = inputFolder.listFiles();

        for (File f : contents){
            if (f.isFile())
                zipFile(f,myname,zipOutputStream);
            else if(f.isDirectory())
                zipFolder(zipOutputStream,f, myname);
        }
        zipOutputStream.closeEntry();
    }


    public static void zipFile(File inputFile, String parentName, ZipOutputStream zipOutputStream) throws IOException{

        ZipEntry zipEntry = new ZipEntry(parentName);
        zipOutputStream.putNextEntry(zipEntry);

        FileInputStream fileInputStream = null;
        try {
			fileInputStream = new FileInputStream(inputFile);
			byte[] buf = new byte[1024];
			int bytesRead;

			while ((bytesRead = fileInputStream.read(buf)) > 0) {
			    zipOutputStream.write(buf, 0, bytesRead);
			}

			zipOutputStream.closeEntry();
		} finally {
			if (fileInputStream != null) {
				fileInputStream.close();
			}
		}

    }

}
