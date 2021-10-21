declare module "@dirigible/platform" {
    module engines {
        interface Engine {
            execute(module, context);

            executeCode(module, context);
        }

        function getEngine(type): Engine;

        function getTypes(): JSON;
    }
    module lifecycle {
        function publish(user, workspace, project)

        function unpublish(user, workspace, project);
    }
    module registry {
        function getContent(path): string;

        function getContentNative(path): string;

        function getText(path): string;

        function find(path, pattern): JSON;
    }
    module repository {
        interface Resource {
            getName(): string;

            getPath(): string;

            getParent(): Collection;

            getInformation(): EntityInformation;

            create();

            delete()

            renameTo(name);

            moveTo(path);

            copyTo(path);

            exists(): boolean;

            isEmpty(): boolean;

            getText(): string;

            getContent(): string;

            getContentNative();

            setText(text);

            setContent(content);

            setContentNative(content);

            isBinary(): boolean;

            getContentType();
        }

        interface EntityInformation {
            getName(): string;

            getPath(): string;

            getPermissions(): string;

            getSize(): number;

            getCreatedBy(): string;

            getCreatedAt(): string;

            getModifiedBy(): string;

            getModifiedAt(): string;
        }

        interface Collection {
            getName(): string;

            getPath(): string;

            getParent(): Collection;

            getInformation(): EntityInformation;

            create();

            delete()

            renameTo(name);

            moveTo(path);

            copyTo(path);

            exists(): boolean;

            isEmpty(): boolean;

            getCollectionsNames(): string;

            createCollection(name): Collection;

            getCollection(name): Collection;

            removeCollection(name);

            getResourcesNames();

            getResource(name): Resource;

            removeResource(name);

            createResource(name, content): Resource;
        }

        function getResource(path): Resource;

        function createResource(path, content, contentType): Resource;

        function updateResource(path, content): Resource;

        function updateResourceNative(path, content): Resource;

        function deleteResource(path);

        function getCollection(path): Collection;

        function createCollection(path): Collection;

        function deleteCollection(path);

        function find(path, pattern): JSON;
    }
    //TODO d.ts files dont allow "-" in module name
    // module template-engines {
    //     interface TemplateEngine{
    //         generate(template,parameters);
    //         setSm(sm);
    //         setEm(em);
    //     }
    //     function getDefaultEngine();
    //     function getMustacheEngine():TemplateEngine;
    //     function getVelocityEngine():TemplateEngine;
    //     function getJavascriptEngine():TemplateEngine;
    //     function generate(template,parameters);
    //     function generateFromFile(location,parameters);
    // }
    module workspace {
        interface Folder {
            getName(): string;

            getPath(): string;

            createFolder(path): Folder;

            exists(): boolean;

            existsFolder(path): boolean;

            getFolder(path): Folder;

            getFolders(path): Folders;

            deleteFolder(path): boolean;

            createFile(path, input): File;

            existsFile(path): boolean;

            getFile(path): File;

            getFiles(path): Files;

            deleteFile(path);
        }

        interface File {
            getName(): string;

            getPath(): string;

            getContentType(): string;

            isBinary(): boolean;

            getContent(): any;

            getText(): string;

            setContent(input);

            setText(input);

            exists(): boolean;
        }

        interface Files {
            size(): number;

            get(index): File;
        }

        interface Folders {
            size(): number;

            get(index): Folder;
        }

        interface Project {
            getName(): string;

            getPath(): string;

            createFolder(path): Folder;

            exists(): boolean;

            existsFolder(path): boolean;

            getFolder(path): Folder;

            getFolders(path): Folders;

            deleteFolder(path): boolean;

            createFile(path, input): File;

            existsFile(path): boolean;

            getFile(path): File;

            getFiles(path): Files;

            deleteFile(path);

        }

        interface Projects {
            size(): number;

            get(index): Project;
        }

        interface Workspace {
            getProjects(): Projects;

            createProject(name): Projects;

            getProject(name): Projects;

            deleteProject(name);

            exists(): boolean;

            existsFolder(path): boolean;

            existsFile(path): boolean;

            copyProject(source, target): boolean;

            moveProject(source, target): boolean;

        }

        function createWorkspace(name): Workspace;

        function getWorkspace(name): Workspace;

        function getWorkspacesNames(): string[];

        function deleteWorkspace(name);
    }
}