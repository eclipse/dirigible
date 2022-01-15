import { bytes } from "@dirigible/io"

export const expectedTestData = "test123";

const testDataBytes = bytes.textToByteArray(expectedTestData);
export const actualTestData = bytes.byteArrayToText(testDataBytes);