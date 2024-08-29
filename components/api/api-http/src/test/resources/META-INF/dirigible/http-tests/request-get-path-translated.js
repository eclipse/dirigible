import { Request as request } from 'sdk/http/request';
import { Files } from 'sdk/io/files';
import { Assert } from 'test/assert';

const separator = Files.separator

Assert.assertTrue(request.getPathTranslated().endsWith(`${separator}services${separator}js${separator}http-tests${separator}request-get-path-translated.js`));
