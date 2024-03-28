import { Controller, Get, response } from "sdk/http"
import { query } from "sdk/db";

@Controller
class ReaderViewService {

    @Get("/")
    public getAll() {
        try {
            const sql = "SELECT * FROM READERS_VIEW";
            const resultset = query.execute(sql, []);
            return resultset;
        } catch (error: any) {
            this.handleError(error);
        }
    }

    private handleError(error: Error) {
        ReaderViewService.sendInternalServerError(error.message);
        throw error;
    }

    private static sendInternalServerError(message: string): void {
        ReaderViewService.sendResponse(500, {
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
