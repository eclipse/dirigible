import { BookRepository } from "../gen/dao/Books/BookRepository";
import { logging } from "sdk/log";

const logger = logging.getLogger("test-job-handler.ts");

const repo = new BookRepository();
const books = repo.findAll();
logger.info("Job: found [{}] books. Books: [{}]", books.length, JSON.stringify(books));
