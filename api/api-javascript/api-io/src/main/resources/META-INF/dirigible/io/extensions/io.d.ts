declare module "@dirigible/io" {
    module bytes {
        function toJavaBytes(bytes);

        function toJavaScriptBytes(internalBytes);

        function textToByteArray(text);

        function byteArrayToText(data);

        function intToByteArray(value, byteOrder);

        function byteArrayToInt(data, byteOrder);
    }

    module files {
        function exist(path): boolean;

        function isExecutable(path): boolean;

        function isReadable(path): boolean;

        function isWritable(path): boolean;

        function isHidden(path): boolean;

        function isDirectory(path): boolean;

        function isFile(path): boolean;

        function isSameFile(path): boolean;

        function getCanonicalPath(path): string;

        function getName(path): string;

        function getParentPath(path): string;

        function readBytes(path): string;

        function readBytesNative(path): string;

        function readText(path): string;

        function writeBytes(path, data): string;

        function writeBytesNative(path, data): string;

        function writeText(path, text): string;

        function getLastModified(path): string;

        function setLastModified(path, time);

        function getOwner(path): string;

        function setOwner(path): string;

        function getPermissions(path): string;

        function setPermissions(path, permissions);

        function size(path): number;

        function createFile(path);

        function createDirectory(path);

        function copy(source, target);

        function move(source, target);

        function deleteFile(path);

        function deleteDirectory(path, forced);

        function createTempFile(prefix, suffix);

        function createTempDirectory(prefix);

        function createInputStream(path);

        function createOutputStream(path);

        function traverse(path);

        function list(path);

        function find(path, pattern): JSON;
    }

    module ftp {
        function getClient(host, port, userName, password): FTPClient;
    }

    module image {
        function resize(original, type, width, height);
    }

    module streams {
        function copyLarge(input, output);

        function copy(input, output);

        function createByteArrayInputStream(data): InputStream;

        function createByteArrayOutputStream(): OutputStream;

        function createInputStream(native): InputStream;

        function createOutputStream(native): OutputStream
    }

    module zip {
        function createZipInputStream(inputStream): ZipInputStream;

        function createZipOutputStream(inputStream): ZipInputStream;

        interface ZipInputStream {
            getNextEntry(): ZipEntry;

            read()

            readNative();

            readText();

            close();
        }

        interface ZipOutputStream {
            close

            createZipEntry(name): ZipEntry;

            write(data)

            writeNative();

            writeText();

            closeEntry();
        }

        interface ZipEntry {
            getName(): string;

            getSize(): number;

            getCompressedSize(): number;

            getTime(): string;

            getCrc();

            getComment();

            getDirectory();

            isValid(): boolean
        }
    }

    interface InputStream {
        read(): any;

        readBytes(): any;

        readBytesNative(): any;

        readText(text): string;

        close();

        isValid(): boolean;
    }

    interface OutputStream {
        write(byte);

        writeBytes(data);

        writeBytesNative(data);

        writeText(text);

        close();

        getBytes();

        getBytesNative();

        getText(): string;

        isValid(): boolean;
    }

    interface FTPObject {
        getPath(): string;

        getName(): string;

        isFile(): boolean;

        isFolder(): boolean;

        getFile(): FTPFile | boolean;

        getFolder(): FTPFolder | boolean;

    }

    interface FTPFile {
        getPath(): string;

        getName(): string;

        getContent(): string;

        setContent(inputStream): boolean;

        getContentBinary(): string;

        setContentBinary(bytes): boolean;

        setContentText(text): boolean;

        appendContent(inputStream): boolean;

        appendContentBinary(bytes): boolean;

        appendContentText(text): boolean;

        delete(): boolean;
    }

    interface FTPFolder {
        getPath();

        getName();

        getFile(fileName): string;

        getFolder(folderName);

        list(): FTPObject[];

        listFiles(): FTPFile[];

        listFolder(): FTPFolder[];

        createFile(fileName, inputStream);

        createFileBinary(fileName, bytes);

        createFileText(fileName, text);

        createFolder(folderName);

        delete(): boolean;

        deleteFile(fileName): boolean;

        deleteFolder(folderName): boolean;
    }

    interface FTPClient {
        getRootFolder(): FTPFolder;

        getFile(path: string, fileName: string): [];

        getFileBinary(path: string, fileName: string): [];

        getFileText(path: string, fileName: string): string;

        getFolder(path: string, fileName: string): string;

        createFile(path, fileName, inputStream): boolean;

        createFileBinary(path, fileName, bytes): boolean;

        createFileText(path, fileName, text): boolean;

        appendFile(path, fileName, inputStream): boolean;

        appendFileBinary(path, fileName, bytes): boolean;

        appendFileText(path, fileName, text): boolean;

        createFolder(path, folderName): boolean;

        deleteFolder(path, folderName): boolean;

        close();

    }


}