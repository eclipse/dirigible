import { Controller, Get, Post, Delete } from "sdk/http"
import { cmis, Document, Folder } from "sdk/cms";
import { response } from "sdk/http";

@Controller
class FolderService {

    private isFolder(doc: Document | Folder): boolean {
        return doc.getType().getId() === 'cmis:folder';
    }

    @Get("/folders")
    public getAll(_: any, ctx: any) {
        try {
            const cmisSession = cmis.getSession();
            const rootFolder = cmisSession.getRootFolder();

            const children = rootFolder.getChildren();

            const folders = [];
            for (let i in children) {
                const doc = children[i];
                if (this.isFolder(doc)) {
                    folders.push({
                        id: doc.getId(),
                        name: doc.getName(),
                        "type": doc.getType().getId(),
                        path: doc.getPath()

                    });
                }
            }

            return folders;
        } catch (error: any) {
            this.handleError(error);
        }
    }

    @Get("/folders/:id")
    public getById(_: any, ctx: any) {
        try {
            const id = ctx.pathParameters.id;
            const cmisSession = cmis.getSession();
            const doc = cmisSession.getObjectByPath(id);
            if (this.isFolder(doc)) {
                return {
                    id: doc.getId(),
                    name: doc.getName(),
                    type: doc.getType()
                }
            } else {
                FolderService.sendResponse(404, `Folder with id [${id}] was not foumce`);
            }
        } catch (error: any) {
            this.handleError(error);
        }
    }

    @Post("/folders")
    public create(entity: any) {
        try {
            const cmisSession = cmis.getSession();

            const folderName = entity.name;
            const folderPath = entity.path;
            const folder = cmisSession.createFolder(folderPath + folderName);
            return {
                id: folder.getId(),
                name: folder.getName(),
                path: folder.getPath()
            };
        } catch (error: any) {
            this.handleError(error);
        }
    }

    @Delete("/folders/:id")
    public deleteById(_: any, ctx: any) {
        try {
            const id = ctx.pathParameters.id;
            const cmisSession = cmis.getSession();

            const obj = cmisSession.getObjectByPath(id);
            obj.delete();
            return `Folder with id [${id}] was deleted`;
        } catch (error: any) {
            this.handleError(error);
        }
    }

    private handleError(error: Error) {
        FolderService.sendInternalServerError(error.message);
        throw error;
    }

    private static sendInternalServerError(message: string): void {
        FolderService.sendResponse(500, {
            "code": 500,
            "message": message
        });
    }

    private static sendResponse(status: number, body?: any): void {
        response.setContentType("application/json");
        response.setStatus(status);
        if (body) {
            response.println(JSON.stringify(body));
        }
    }

}
