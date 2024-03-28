import { logging } from "sdk/log";

const logger = logging.getLogger("book-entity-events-handler.ts");

export function onMessage(message: string) {
    logger.info("Received message [{}]", message);
}

export function onError(error: string) {
    logger.info("Received error message [{}]", error);
}
