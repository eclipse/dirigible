import { cmis } from "sdk/cms";
import { response } from "sdk/http";
import { streams } from "sdk/io";

const cmisSession = cmis.getSession();
const rootFolder = cmisSession.getRootFolder();

function printAllFiles(rootFolder) {
    const children = rootFolder.getChildren();
    response.println("Listing the children of the root folder:");
    for (let i in children) {
        response.println("Object ID: " + children[i].getId());
        response.println("Object Name: " + children[i].getName());
    }
    response.println("--------");
}

function createRandomFileName() {
    return (Math.random() + 1).toString(36).substring(2);
}

function createFile() {
    const textFileName = createRandomFileName() + ".txt";
    response.println("Creating a simple text file, " + textFileName);

    const mimetype = "text/plain; charset=UTF-8";
    const content = "This is some test content.";
    const filename = textFileName;

    const outputStream = streams.createByteArrayOutputStream();
    outputStream.writeText(content);
    const bytes = outputStream.getBytes();
    const inputStream = streams.createByteArrayInputStream(bytes);

    const contentStream = cmisSession.getObjectFactory().createContentStream(filename, bytes.length, mimetype, inputStream);

    const properties = { "cmis:name": "", "cmis:objectTypeId": "" };
    properties[cmis.OBJECT_TYPE_ID] = cmis.OBJECT_TYPE_DOCUMENT;
    properties[cmis.NAME] = filename;
    let newDocument;
    try {
        newDocument = rootFolder.createDocument(properties, contentStream, cmis.VERSIONING_STATE_MAJOR);
    } catch (e) {
        response.println("Error: " + e);
    }

    response.println("Created document with ID: " + newDocument.getId());
    response.println("--------");

    return newDocument;
}

function printDocumentContent(document) {
    const documentId = document?.getId();

    response.println("Getting content of file " + documentId);
    let doc;
    if (documentId !== undefined) {
        doc = cmisSession.getObject(documentId);
    } else {
        response.println("No content");
    }

    let contentStream = doc?.getContentStream(); // returns null if the document has no content

    if (contentStream !== null) {
        const stream = contentStream.getStream();
        const content = stream.readText();
        response.println("Contents of " + documentId + " is:\n" + content);
    } else {
        response.println("No content.");
    }
    response.println("--------");
}

function deleteDocument(document) {
    response.println("Deleting the newly created document " + document.getId());
    if (document) {
        document.delete();
    }
    response.println("--------");
}

// Execution

printAllFiles(rootFolder);

const document = createFile();

printAllFiles(rootFolder);
printDocumentContent(document);

deleteDocument(document);

response.println("Done!");