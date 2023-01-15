declare module "@dirigible/platform" {
    class byte{}
    class JavaBytes{}
    
    module engines {
        interface Engine {
            /**
             * Executes a given module with a given context
             * @param projectName
             * @param projectFilePath
             * @param projectFilePathParam
             * @param parameters
             * @param debug
             */
            execute(projectName, projectFilePath, projectFilePathParam, parameters, debug):object;

        }

        /**
         * Returns the engine object per type provided
         * @param type
         */
        function getEngine(type): Engine;

        /**
         * Returns the list of the registered engine types
         */
        function getTypes(): string[];
    }
    
    module lifecycle {
        /**
         * Publish project from the workspace, the project parameter is optional
         * @param user
         * @param workspace
         * @param project
         */
        function publish(user, workspace, project):boolean;

        /**
         * Unpublish project from the workspace, the project parameter is optional
         * @param project
         */
        function unpublish(project):boolean;
    }
    
    module registry {
        /**
         * Gets the content of resource by path, as byte array
         * @param path
         */
        function getContent(path:string): byte[];

        /**
         * Gets the content of resource by path, as array of Java bytes
         * @param path
         */
        function getContentNative(path:string): JavaBytes[];

        /**
         * Gets the content of resource by path, as text
         * @param path
         */
        function getText(path:string): string;

        /**
         * Find resources under certain path (e.g. /) by pattern (e.g. *.js)
         * @param path
         * @param pattern
         */
        function find(path:string, pattern): string[];
    }
    
    module repository {
        interface Resource {
            /**
             * Gets the Resource name
             */
            getName(): string;

            /**
             * Gets the Resource path
             */
            getPath(): string;

            /**
             * Gets the Resource parent Collection
             */
            getParent(): Collection;

            /**
             * Get the Resource information
             */
            getInformation(): EntityInformation;

            /**
             * Create new Resource
             */
            create();

            /**
             * Delete the Resource
             */
            delete();

            /**
             * Rename the Resource
             * @param name
             */
            renameTo(name:string);

            /**
             * Moves the Resource to a new location
             * @param path
             */
            moveTo(path:string);

            /**
             * Copy the Resource to a new location
             * @param path
             */
            copyTo(path:string);

            /**
             * Returns true if the Resource exists.
             */
            exists(): boolean;

            /**
             * Returns true if the Resource is empty
             */
            isEmpty(): boolean;

            /**
             * Returns the content of the Resource as text
             */
            getText(): string;

            /**
             * Returns the content of the Resource
             */
            getContent(): byte[];

            /**
             *Returns the content of the Resource
             */
            getContentNative():JavaBytes[];

            /**
             * Sets the Resource content as text
             * @param text
             */
            setText(text:string);

            /**
             * Sets the Resource content
             * @param content
             */
            setContent(content);

            /**
             * Sets the Resource content as array of Java bytes
             * @param content
             */
            setContentNative(content:JavaBytes);

            /**
             * Returns true if the Resource content is binary
             */
            isBinary(): boolean;

            /**
             * Returns the content type of the Resource
             */
            getContentType():string;
        }

        interface EntityInformation {
            /**
             * Gets the entity name
             */
            getName(): string;

            /**
             * Gets the entity path
             */
            getPath(): string;

            /**
             * Gets the entity permissions
             */
            getPermissions(): string;

            /**
             * Gets the entity size
             */
            getSize(): number;

            /**
             * Gets the entity createdBy
             */
            getCreatedBy(): string;

            /**
             * Gets the entity createdAt
             */
            getCreatedAt(): string;

            /**
             * Gets the entity modifiedBy
             */
            getModifiedBy(): string;

            /**
             * Gets the entity modifiedAt
             */
            getModifiedAt(): string;
        }

        interface Collection {
            /**
             * Gets the Collection name
             */
            getName(): string;

            /**
             * Gets the Collection path
             */
            getPath(): string;

            /**
             * Gets the Collection parent Collection
             */
            getParent(): Collection;

            /**
             * Get the Collection information
             */
            getInformation(): EntityInformation;

            /**
             * Create new Collection
             */
            create();

            /**
             * Delete the Collection
             */
            delete()

            /**
             * Rename the Collection
             * @param name
             */
            renameTo(name:string);

            /**
             * Moves the Collection to a new location
             * @param path
             */
            moveTo(path:string);

            /**
             * Copy the Collection to a new location
             * @param path
             */
            copyTo(path:string);

            /**
             * Returns true if the Collection exists
             */
            exists(): boolean;

            /**
             * Returns true if the Collection is empty
             */
            isEmpty(): boolean;

            /**
             * Gets the names of the Collections in this Collection
             */
            getCollectionsNames(): string[];

            /**
             * Create new Collection
             * @param name
             */
            createCollection(name:string): Collection;

            /**
             * Get Collection by name
             * @param name
             */
            getCollection(name:string): Collection;

            /**
             * Remove Collection by name
             * @param name
             */
            removeCollection(name:string);

            /**
             * Gets the names of the Resources in this Collection
             */
            getResourcesNames():string[];

            /**
             * Get Resource by name
             * @param name
             */
            getResource(name:string): Resource;

            /**
             * Remove Resource by name
             * @param name
             */
            removeResource(name:string);

            /**
             * Create new Resource
             * @param name
             * @param content
             */
            createResource(name, content): Resource;
        }

        /**
         * Get Resource by path
         * @param path
         */
        function getResource(path:string): Resource;

        /**
         * Creates Resource programmatically
         * @param path
         * @param content
         * @param contentType
         */
        function createResource(path, content, contentType): Resource;

        /**
         * Creates Resource programmatically, with array of Java bytes
         * @param path
         * @param content
         */
        function updateResource(path, content): Resource;

        /**
         * Updates Resource content, with array of Java bytes
         * @param path
         * @param content
         */
        function updateResourceNative(path, content): Resource;

        /**
         * Delete Resource by path
         * @param path
         */
        function deleteResource(path:string);

        /**
         * Get Collection by path
         * @param path
         */
        function getCollection(path:string): Collection;

        /**
         * Creates Collection programmatically
         * @param path
         */
        function createCollection(path:string): Collection;

        /**
         * Delete Collection by path
         * @param path
         */
        function deleteCollection(path:string);

        /**
         * Find resources under certain path (e.g. /) by pattern (e.g. *.js)
         * @param path
         * @param pattern
         */
        function find(path:string, pattern): string[];
    }
    
    module workspace {
        interface Folder {
            /**
             * Returns the name of the Folder programmatically
             */
            getName(): string;

            /**
             * Returns the path of the Folder programmatically
             */
            getPath(): string;

            /**
             * Creates a new Folder by name programmatically
             * @param path
             */
            createFolder(path:string): Folder;

            /**
             * Check whether this Folder object does exist programmatically
             */
            exists(): boolean;

            /**
             * Check whether a Folder by given path exists in this Folder programmatically
             * @param path
             */
            existsFolder(path:string): boolean;

            /**
             * Gets a Folder by path programmatically
             * @param path
             */
            getFolder(path:string): Folder;

            /**
             * Gets all the Folders under the path programmatically
             * @param path
             */
            getFolders(path:string): Folders;

            /**
             * Deletes a Folder by path programmatically
             * @param path
             */
            deleteFolder(path:string): boolean;

            /**
             * Creates a new File by name programmatically
             * @param path
             * @param input
             */
            createFile(path:string, input:string): File;

            /**
             * Check whether a File by given path exists in this Folder programmatically
             * @param path
             */
            existsFile(path:string): boolean;

            /**
             * Gets a File by path programmatically
             * @param path
             */
            getFile(path): File;

            /**
             * Gets all the Files under the path programmatically
             * @param path
             */
            getFiles(path): Files;

            /**
             * Deletes a File by path programmatically
             * @param path
             */
            deleteFile(path);
        }

        interface File {
            /**
             * Returns the name of the File programmatically
             */
            getName(): string;

            /**
             * Returns the path of the File programmatically
             */
            getPath(): string;

            /**
             * Returns the Content Type of the File programmatically
             */
            getContentType(): string;

            /**
             * Returns the Binary flag of the File programmatically
             */
            isBinary(): boolean;

            /**
             * Returns the Content of the File programmatically
             */
            getContent(): byte[];

            /**
             * Returns the Content of the File programmatically
             */
            getText(): string;

            /**
             * Sets the Content of the File programmatically by the given bytes input
             * @param input
             */
            setContent(input:string);

            setText(input);

            /**
             * Check whether this File object does exist programmatically
             */
            exists(): boolean;
        }

        interface Files {
            /**
             * Returns the size of this Files list programmatically
             */
            size(): number;

            /**
             * Gets a File by index programmatically
             * @param index
             */
            get(index:number): File;
        }

        interface Folders {
            /**
             * Returns the size of this Folders list programmatically
             */
            size(): number;

            /**
             * Gets a Folder by index programmatically
             * @param index
             */
            get(index:number): Folder;
        }

        interface Project {
            /**
             * Returns the name of the Project programmatically
             */
            getName(): string;

            /**
             * Returns the path of the Project programmatically
             */
            getPath(): string;

            /**
             * Creates a new Folder by name programmatically
             * @param path
             */
            createFolder(path:string): Folder;

            /**
             * Check whether this Project object does exist programmatically
             */
            exists(): boolean;

            /**
             * Check whether a Folder by given path exists in this Project programmatically
             * @param path
             */
            existsFolder(path:string): boolean;

            /**
             * Gets a Folder by path programmatically
             * @param path
             */
            getFolder(path:string): Folder;

            /**
             * Gets all the Folders under the path programmatically
             * @param path
             */
            getFolders(path:string): Folders;

            /**
             * Deletes a Folder by path programmatically
             * @param path
             */
            deletesFolder(path): boolean;

            /**
             * Creates a new File by name programmatically
             * @param path
             * @param input
             */
            createFile(path:string, input?:string): File;

            /**
             * Check whether a File by given path exists in this Project programmatically
             * @param path
             */
            existsFile(path): boolean;

            /**
             * Gets a File by path programmatically
             * @param path
             */
            getFile(path): File;

            /**
             * Gets all the Files under the path programmatically
             * @param path
             */
            getFiles(path): Files;

            /**
             * Deletes a File by path programmatically
             * @param path
             */
            deleteFile(path);

        }

        interface Projects {
            /**
             * Returns the size of this Projects list programmatically
             */
            size(): number;

            /**
             * Gets a Project by index programmatically
             * @param index
             */
            get(index): Project;
        }

        interface Workspace {
            /**
             * List the names of the available workspaces programmatically
             */
            getProjects(): Projects;

            /**
             * Creates a new Project programmatically
             * @param name
             */
            createProject(name:string): Project;

            /**
             * Gets the Project by name programmatically
             * @param name
             */
            getProject(name:string): Projects;

            /**
             * Deletes the Project by name programmatically
             * @param name
             */
            deleteProject(name:string);

            /**
             * Check whether this Workspace object does exist programmatically
             */
            exists(): boolean;

            /**
             * Check whether a Folder by given path exists in this Workspace programmatically
             * @param path
             */
            existsFolder(path:string): boolean;

            /**
             * Check whether a File by given path exists in this Workspace programmatically
             * @param path
             */
            existsFile(path:string): boolean;

            /**
             * Copies a given Project programmatically
             * @param source
             * @param target
             */
            copyProject(source, target:string);

            /**
             * Moves a given Project programmatically
             * @param source
             * @param target
             */
            moveProject(source:string, target:string);

        }

        /**
         * Creates a new Workspace programmatically
         * @param name
         */
        function createWorkspace(name:string): Workspace;

        /**
         * Gets the Workspace by name programmatically
         * @param name
         */
        function getWorkspace(name:string): Workspace;

        /**
         * List the names of the available workspaces programmatically
         */
        function getWorkspacesNames(): string[];

        /**
         * Deletes the Workspace by name programmatically
         * @param name
         */
        function deleteWorkspace(name:string);
    }
}
