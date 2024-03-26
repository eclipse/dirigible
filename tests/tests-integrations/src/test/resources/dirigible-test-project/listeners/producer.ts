import { producer } from "sdk/messaging"
import { response } from "sdk/http";

producer.queue("test-project-queue").send("Test message in queue");

producer.topic("test-project-topic").send("Test message in topic");

console.log("Successfully sent messages.")
response.println("Successfully sent messages.");