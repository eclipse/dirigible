import { Repository as repository } from "sdk/platform/repository";
import { Assert } from 'test/assert';

repository.createResource("/registry/public/test/file.js", "console.log('Hello World');", "application/json");
const resource = repository.getResource("/registry/public/test/file.js");
const content = resource.getText();

Assert.assertTrue(content !== undefined && content !== null);
