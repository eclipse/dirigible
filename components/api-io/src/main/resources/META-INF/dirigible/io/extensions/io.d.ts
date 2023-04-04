// @ts-ignore
class byte {
}

declare module "@dirigible/io" {
    module bytes {
        /**
         * Convert the native JavaScript byte array to Java one, to be used internally by the API layer
         * @param bytes
         */
        function toJavaBytes(bytes);

        /**
         * Convert the Java byte array to a native JavaScript one, to be used internally by the API layer
         * @param internalBytes
         */
        function toJavaScriptBytes(internalBytes);

        /**
         * Converts a text to a byte array
         * @param text
         */
        function textToByteArray(text: string);

        /**
         * Converts a byte array to text
         * @param data
         */
        function byteArrayToText(data): string;
    }
    module files {
        /**
         * Whether a file by this path exists
         * @param path
         */
        function exist(path: string): boolean;

        /**
         * List files under this path
         * @param path
         */
        function list(path: string): string[];

        /**
         * Whether the file by this path is executable
         * @param path
         */
        function isExecutable(path: string): boolean;

        /**
         * Whether the file by this path is readable
         * @param path
         */
        function isReadable(path: string): boolean;

        /**
         * Whether the file by this path is writable
         * @param path
         */
        function isWritable(path: string): boolean;

        /**
         * Whether the file by this path is hidden
         * @param path
         */
        function isHidden(path: string): boolean;

        /**
         * Whether the file by this path is directory
         * @param path
         */
        function isDirectory(path: string): boolean;

        /**
         * Whether the file by this path is file
         * @param path
         */
        function isFile(path: string): boolean;

        /**
         * Whether the files by these path1 and path2 are pointing to the same file
         * @param path1
         * @param path2
         */
        function isSameFile(path1: string, path2: string): boolean;

        /**
         * Returns the canonical path of the file by this path
         * @param path
         */
        function getCanonicalPath(path: string): string;

        /**
         * Returns the name of the file by this path
         * @param path
         */
        function getName(path: string): string;

        /**
         * Returns the parent's path of the file by this path
         * @param path
         */
        function getParentPath(path: string): string;

        /**
         * Returns the content of the given file as byte array
         * @param path
         */
        function readBytes(path: string): [];

        /**
         * Returns the content of the given file as array of Java bytes
         * @param path
         */
        function readBytesNative(path: string): string;

        /**
         * Returns the content of the given file as string
         * @param path
         */
        function readText(path: string): string;

        /**
         * Writes the given byte array content to the file
         * @param path
         * @param data
         */
        function writeBytes(path: string, data);

        /**
         * Writes the given array of Java bytes content to the file
         * @param path
         * @param data
         */
        function writeBytesNative(path: string, data);

        /**
         * Writes the given text content to the file
         * @param path
         * @param text
         */
        function writeText(path: string, text: string);

        /**
         * Returns the last modification date of the file by this path
         * @param path
         */
        function getLastModified(path: string): Date;

        /**
         * Sets the last modification date of the file by this path
         * @param path
         * @param time
         */
        function setLastModified(path: String, time: Date);

        /**
         * Returns the owner of the file by this path
         * @param path
         */
        function getOwner(path: string): string;

        /**
         * Sets the owner of the file by this path
         * @param path
         * @param owner
         */
        function setOwner(path: string, owner: string);

        /**
         * Returns the POSIX permissions of the file by this path
         * @param path
         */
        function getPermissions(path: string): string;

        /**
         * Sets the POSIX permissions of the file by this path
         * @param path
         * @param permissions
         */
        function setPermissions(path: string, permissions);

        /**
         * Returns the size of the file by this path
         * @param path
         */
        function size(path: string): number;

        /**
         * Creates a new file by the given path
         * @param path
         */
        function createFile(path: string);

        /**
         * Creates a new directory by the given path
         * @param path
         */
        function createDirectory(path: string);

        /**
         * Copies a source file to a target
         * @param source
         * @param target
         */
        function copy(source: string, target: string);

        /**
         * Moves a source file to a target
         * @param source
         * @param target
         */
        function move(source: string, target: string);

        /**
         * Deletes the file by the given path
         * @param path
         */
        function deleteFile(path: string);

        /**
         * Deletes the directory by the given path
         * @param path
         * @param forced
         */
        function deleteDirectory(path: string, forced?: boolean);

        /**
         * Creates a new temporary file by the given prefix and suffix
         * @param prefix
         * @param suffix
         */
        function createTempFile(prefix: string, suffix: string);

        /**
         * Creates a new temporary directory by the given prefix
         * @param prefix
         */
        function createTempDirectory(prefix: string);

        /**
         * Creates an InputStream pointing to a file by the given path
         * @param path
         */
        function createInputStream(path: string): InputStream;

        /**
         * Creates an OutputStream pointing to a file by the given path
         * @param path
         */
        function createOutputStream(path: string): OutputStream;

        function traverse(path);

        /**
         * Creates an OutputStream pointing to a file by the given path
         * @param path
         * @param pattern
         */
        function find(path: string, pattern): JSON;
    }
    module ftp {
        /**
         * Returns a FTP Client instance
         * @param host
         * @param port
         * @param userName
         * @param password
         */
        function getClient(host, port, userName, password): FTPClient;
    }
    module image {
        /**
         * Resize an image to the given boundaries.
         *
         * @param original original image
         * @param type type of the image
         * @param width width of the new image
         * @param height height of the new image
         */
        function resize(original: InputStream, type: string, width: number, height: number): InputStream;
    }
    module streams {
        /**
         * Copies an InputStream to an OutputStream
         * @param input
         * @param output
         */
        function copy(input: InputStream, output: OutputStream);

        /**
         * Creates an ByteArrayInputStream from the array of bytes
         * @param data
         */
        function createByteArrayInputStream(data): InputStream;

        /**
         * Creates an ByteArrayOutputStream
         */
        function createByteArrayOutputStream(): OutputStream;

        /**
         * Creates InputSteam out of NativeBytesArray
         * @param native
         */
        function createInputStream(native): InputStream;

        /**
         * Creates OutputSteam out of NativeBytesArray
         * @param native
         */
        function createOutputStream(native): OutputStream
    }
    module zip {
        /**
         * Creates zip archive from sourcePath in zipTargetPath path
         * @param sourcePath
         * @param zipTargetPath
         */
        function zip(sourcePath: string, zipTargetPath: string);

        /**
         * Unzips zip archive in zipPath to targetPath path
         * @param zipPath
         * @param targetPath
         */
        function unzip(zipPath: string, targetPath: string);

        /**
         * Returns the Zip archive reader object
         * @param inputStream
         */
        function createZipInputStream(inputStream: InputStream): ZipInputStream;

        /**
         * Returns the Zip archive writer object
         * @param outputStream
         */
        function createZipOutputStream(outputStream: OutputStream): ZipOutputStream;

        interface ZipInputStream {
            isValid(): boolean;

            /**
             * Returns the next entry from the archive or null if no more entries found
             */
            getNextEntry(): ZipEntry;

            /**
             * Reads from the zip input stream at the current entry point and returns the result as array of bytes
             */
            read(): byte[];

            /**
             * Reads from the zip input stream at the current entry point and returns the result as array of Java bytes
             */
            readNative(): byte[];

            /**
             * Reads from the zip input stream at the current entry point and returns the result as text
             */
            readText(): string;

            /**
             * Closes the zip input stream
             */
            close();
        }

        interface ZipOutputStream {
            /**
             * Finishes, flushes and closes the zip output stream
             */
            close();

            /**
             * Returns a new entry for the archive
             * @param name
             */
            createZipEntry(name): ZipEntry;

            /**
             * Writes an array of bytes to the zip output stream at the current entry point
             * @param data
             */
            write(data)

            /**
             * Writes an array of Java bytes to the zip output stream at the current entry point
             */
            writeNative();

            /**
             * Writes a text to the zip output stream at the current entry point
             */
            writeText(text: string);

            /**
             * Closes the current entry (optional)
             */
            closeEntry();
        }

        interface ZipEntry {
            /**
             * Returns the name of the entry
             */
            getName(): string;

            /**
             * Returns the size of the entry
             */
            getSize(): number;

            /**
             * Returns the compressed size of the entry
             */
            getCompressedSize(): number;

            /**
             * Returns the time stamp of the entry
             */
            getTime(): number;

            /**
             * Returns the CRC sum of the entry
             */
            getCrc(): number;

            /**
             * Returns the comment text of the entry
             */
            getComment(): number;

            /**
             * Returns true if the entry represents a directory and false otherwise
             */
            getDirectory();

            /**
             * Returns true if the entry is a valid one and false otherwise (after last)
             */
            isValid(): boolean;
        }
    }

    interface InputStream {
        /**
         * Reads a single byte from this InputStream
         */
        read(): byte;

        /**
         * Returns the array of bytes contained in this InputStream
         */
        readBytes(): byte[];

        readBytesNative(): any;

        /**
         * Returns a string representation of the array of bytes contained in this InputStream
         */
        readText(): string;

        /**
         * Closes this InputStream to release the resources
         */
        close();

        /**
         * Returns true if inputstream is valid
         */
        isValid(): boolean;
    }

    interface OutputStream {
        /**
         * Write byte to this OutputStream
         * @param byte
         */
        write(byte);

        /**
         * Writes a single byte to this OutputStream
         * @param byte
         */
        writeByte(byte: byte);

        /**
         * Writes the array of bytes to this OutputStream
         * @param bytes
         */
        writeBytes(bytes: byte[]): byte[];

        /**
         * Writes the array of NativeBytes to this OutputStream
         * @param data
         */
        writeBytesNative(data);

        /**
         * Writes the text to this OutputStream
         * @param text
         */
        writeText(text: string);

        /**
         * Returns a string representation of the array of bytes contained in this InputStream
         */
        readText(): string

        /**
         * Close this OutputStream
         */
        close();

        /**
         * Gets bytes from this OutputStream
         */
        getBytes();

        /**
         * Gets bytes from this OutputStream
         */
        getBytesNative();

        /**
         * Get text from this OutputStream
         */
        getText(): string;

        /**
         * Returns true if OutputStream is valid
         */
        isValid(): boolean;
    }

    interface FTPObject {
        /**
         * Gets the object path
         */
        getPath(): string;

        /**
         * Gets the object name
         */
        getName(): string;

        /**
         * Returns true if the object is file
         */
        isFile(): boolean;

        /**
         * Returns true if the object is folder
         */
        isFolder(): boolean;

        /**
         * Gets object as FTPFile
         */
        getFile(): FTPFile;

        /**
         * Gets object as FTPFolder
         */
        getFolder(): FTPFolder;

    }

    interface FTPFile {
        /**
         * Gets the folder path
         */
        getPath(): string;

        /**
         * Gets the folder name
         */
        getName(): string;

        /**
         * Gets the file content
         */
        getContent(): InputStream;

        /**
         * Gets the file content
         */
        getContentText(): string

        /**
         * Gets the file content
         * @param inputStream
         */
        getContentBinary(inputStream);

        /**
         * Sets the file content from an InputStream
         * @param inputStream
         */
        setContent(inputStream: InputStream): boolean;

        /**
         * Sets the file content from byte array
         * @param bytes
         */
        setContentBinary(bytes): boolean;

        /**
         * Sets the file content from string
         * @param text
         */
        setContentText(text: string): boolean;

        /**
         * Appends file content from an InputStream
         * @param inputStream
         */
        appendContent(inputStream: InputStream): boolean;

        /**
         * Appends file content from an byte array
         * @param bytes
         */
        appendContentBinary(bytes): boolean;

        /**
         * Appends file content from string
         * @param text
         */
        appendContentText(text: string): boolean;

        /**
         * Deletes the file
         */
        delete(): boolean;
    }

    interface FTPFolder {
        /**
         * Gets the folder path
         */
        getPath(): string;

        /**
         * Gets the folder name
         */
        getName(): string;

        /**
         * Gets FTPFile by fileName
         * @param fileName
         */
        getFile(fileName: string): FTPFile;

        /**
         * Gets FTPFolder by folderName
         * @param folderName
         */
        getFolder(folderName: string): FTPFolder;

        /**
         * Gets array of FTPObjects
         */
        list(): FTPObject[];

        /**
         * Gets array of FTPFiles
         */
        listFiles(): FTPFile[];

        /**
         * Gets array of FTPFolder
         */
        listFolder(): FTPFolder[];

        /**
         * Creates file from InputStream and return true if the file was created successfully
         * @param fileName
         * @param inputStream
         */
        createFile(fileName: string, inputStream: InputStream): boolean;

        /**
         * Creates file from byte array and return true if the file was created successfully
         * @param fileName
         * @param bytes
         */
        createFileBinary(fileName: string, bytes: byte[]): boolean;

        /**
         * Creates file from string and return true if the file was created successfully
         * @param fileName
         * @param text
         */
        createFileText(fileName: string, text: string): boolean;

        /**
         * Creates FTPFolder
         * @param folderName
         */
        createFolder(folderName): FTPFolder;

        /**
         * Deletes the current folder
         */
        delete(): boolean;

        /**
         * Deletes FTPFile
         * @param fileName
         */
        deleteFile(fileName: string): boolean;

        /**
         * Deletes FTPFolder
         * @param folderName
         */
        deleteFolder(folderName: string): boolean;
    }

    interface FTPClient {
        /**
         * Gets the root folder
         */
        getRootFolder(): FTPFolder;

        /**
         * Gets the file content as an input stream
         * @param path
         * @param fileName
         */
        getFile(path: string, fileName: string): InputStream;

        /**
         * Gets the file content as byte array
         * @param path
         * @param fileName
         */
        getFileBinary(path: string, fileName: string): byte[];

        /**
         * Gets the file content as string
         * @param path
         * @param fileName
         */
        getFileText(path: string, fileName: string): string;

        /**
         * Gets the folder
         * @param path
         * @param fileName
         */
        getFolder(path: string, fileName: string): string;

        /**
         * Creates file from InputStream and return true if the file was created successfully
         * @param path
         * @param fileName
         * @param inputStream
         */
        createFile(path: string, fileName: string, inputStream: InputStream): boolean;

        /**
         * Creates file from byte array and return true if the file was created successfully
         * @param path
         * @param fileName
         * @param bytes
         */
        createFileBinary(path: string, fileName: string, bytes: byte[]): boolean;

        /**
         * Creates file from string and return true if the file was created successfully
         * @param path
         * @param fileName
         * @param text
         */
        createFileText(path: string, fileName: string, text: string): boolean;

        /**
         * Appends InputStream to file and return true if the file was created successfully
         * @param path
         * @param fileName
         * @param inputStream
         */
        appendFile(path: string, fileName: string, inputStream: InputStream): boolean;

        /**
         * Appends byte array to file and return true if the file was created successfully
         * @param path
         * @param fileName
         * @param bytes
         */
        appendFileBinary(path: string, fileName: string, bytes: byte[]): boolean;

        /**
         * Appends string to file and return true if the file was created successfully
         * @param path
         * @param fileName
         * @param text
         */
        appendFileText(path: string, fileName: string, text: string): boolean;

        /**
         * Creates folder
         * @param path
         * @param folderName
         */
        createFolder(path: string, folderName: string): boolean;

        /**
         * Deletes file
         * @param path
         * @param fileName
         */
        deleteFiles(path: string, fileName: string): boolean;

        /**
         * Deletes folder
         * @param path
         * @param folderName
         */
        deleteFolder(path: string, folderName: string): boolean;

        /**
         * Closes the FPT client
         */
        close();
    }
}
