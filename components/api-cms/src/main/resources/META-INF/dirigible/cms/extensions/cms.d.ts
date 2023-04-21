declare module "@dirigible/cms" {
    module cmis {
        //----------Base--------
        const OBJECT_TYPE_DOCUMENT: "cmis:document";
        const NAME: "cmis:name";
        const OBJECT_ID: "cmis:objectId";
        const OBJECT_TYPE_ID: "cmis:objectTypeId";
        const BASE_TYPE_ID: "cmis:baseTypeId";
        const CREATED_BY: "cmis:createdBy";
        const CREATION_DATE: "cmis:creationDate";
        const LAST_MODIFIED_BY: "cmis:lastModifiedBy";
        const LAST_MODIFICATION_DATE: "cmis:lastModificationDate";
        const CHANGE_TOKEN: "cmis:changeToken";

        // ---- Relationship ----
        const SOURCE_ID: "cmis:sourceId";
        const TARGET_ID: "cmis:targetId";

        // ---- Policy ----
        const POLICY_TEXT = "cmis:policyText";

        // ---- Versioning States ----
        const VERSIONING_STATE_NONE = "none";
        const VERSIONING_STATE_MAJOR = "major";
        const VERSIONING_STATE_MINOR = "minor";
        const VERSIONING_STATE_CHECKEDOUT = "checkedout";

        // ---- Object Types ----
        const OBJECT_TYPE_FOLDER: "cmis:folder";
        const OBJECT_TYPE_RELATIONSHIP: "cmis:relationship";
        const OBJECT_TYPE_POLICY: "cmis:policy";
        const OBJECT_TYPE_ITEM: "cmis:item";
        const OBJECT_TYPE_SECONDARY: "cmis:secondary";


        // const METHOD_READ = CMIS_METHOD_READ;
        // const METHOD_WRITE = CMIS_METHOD_WRITE;

        /**
         * Returns the CMIS connection session to the CMS system
         */
        function getSession(): Session;

        /**
         * Returns array of CMIS access constraints for the specified path and method
         * @param path
         * @param method
         */
        function getAccessDefinitions(path: string, method: string): string []

    }

    interface Session {
        /**
         * Returns the information about the CMIS repository
         */
        getRepositoryInfo(): RepositoryInfo

        /**
         * Returns the root folder of this repository
         */
        getRootFolder(): Folder

        /**
         * Returns a CMIS Object by name
         * @param objectId
         */
        getObject(objectId: string): CmisObject

        /**
         * Returns the ObjectFactory utility
         */
        getObjectFactory(): ObjectFactory

        /**
         * Returns a CMIS Object by path
         * @param path
         */
        getObjectByPath(path): CmisObject

    }

    interface RepositoryInfo {
        /**
         * Returns the ID of the CMIS repository
         */
        getId(): string

        /**
         * Returns the Name of the CMIS repository
         */
        getName(): string
    }

    interface Folder {
        /**
         * Returns the ID of this Folder
         */
        getId(): string

        /**
         * Returns the Name of this Folder
         */
        getName(): string

        /**
         * Creates a new folder under this Folder
         * @param properties
         */
        createFolder(properties: JSON): Folder

        /**
         * Creates a new document under this Folder
         * @param properties
         * @param contentStream
         * @param versioningState
         */
        createDocument(properties: object, contentStream, versioningState): Document

        /**
         * Returns an array of CmisObject sub-elements of this Folder
         */
        getChildren(): CmisObject[]

        /**
         * Returns the Path of this Folder
         */
        getPath(): string

        /**
         * Returns the parent Folder of this Folder
         */
        getRootFolder(): Folder

        /**
         * Deletes this Folder
         */
        delete();

        /**
         * Renames this Folder
         * @param newName
         */
        rename(newName: string)

        deleteTree()

        getType(): TypeDefinition;

        PARENT_ID: "cmis:parentId";
        ALLOWED_CHILD_OBJECT_TYPE_IDS: "cmis:allowedChildObjectTypeIds";
        PATH: "cmis:path";
    }

    interface CmisObject {
        /**
         * Returns the ID of this CmisObject
         */
        getId(): string

        /**
         * Returns the Name of this CmisObject
         */
        getName(): string

        /**
         * Returns the Type of this CmisObject
         */
        getType(): TypeDefinition

        /**
         * Deletes this CmisObject
         */
        delete();

        /**
         * Renames this CmisObject
         */
        rename();

        getContentStream();
    }

    interface ObjectFactory {
        /**
         * Returns a newly created ContentStream object
         * @param filename
         * @param length
         * @param mimetype
         * @param inputStream
         */
        createContentStream(filename: string, length: number, mimetype: string, inputStream): ContentStream;
    }

    interface ContentStream {
        /**
         * Returns the InputStream of this ContentStream object
         */
        getStream()

        /**
         * Returns mimeType for content stream
         */
        getMimeType(): string
    }

    interface Document {
        /**
         * Returns the ID of this Document
         */
        getId(): string

        /**
         * Returns the Name of this Document
         */
        getName(): string

        /**
         * Returns the ContentStream representing the contents of this Document
         */
        getContentStream(): ContentStream

        /**
         * Renames this Document
         * @param newName
         */
        rename(newName: string)

        IS_IMMUTABLE: "cmis:isImmutable";
        IS_LATEST_VERSION: "cmis:isLatestVersion";
        IS_MAJOR_VERSION: "cmis:isMajorVersion";
        IS_LATEST_MAJOR_VERSION: "cmis:isLatestMajorVersion";
        VERSION_LABEL: "cmis:versionLabel";
        VERSION_SERIES_ID: "cmis:versionSeriesId";
        IS_VERSION_SERIES_CHECKED_OUT: "cmis:isVersionSeriesCheckedOut";
        VERSION_SERIES_CHECKED_OUT_BY: "cmis:versionSeriesCheckedOutBy";
        VERSION_SERIES_CHECKED_OUT_ID: "cmis:versionSeriesCheckedOutId";
        CHECKIN_COMMENT: "cmis:checkinComment";
        CONTENT_STREAM_LENGTH: "cmis:contentStreamLength";
        CONTENT_STREAM_MIME_TYPE: "cmis:contentStreamMimeType";
        CONTENT_STREAM_FILE_NAME: "cmis:contentStreamFileName";
        CONTENT_STREAM_ID: "cmis:contentStreamId";

        delete();
    }

    interface TypeDefinition {
        getId(): string
    }


}





