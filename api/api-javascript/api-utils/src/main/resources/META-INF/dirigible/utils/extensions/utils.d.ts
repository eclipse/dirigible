// @ts-ignore
class byte {
}

declare module "@dirigible/utils" {
    module base64 {
        /**
         * Encode an input string to Base64
         * @param text
         */
        function encode(text: string): string;

        /**
         * Encode an byte[] to Base64
         * @param input
         */
        function encodeAsBytes(input: string): string;

        /**
         * Decode an input string from Base64
         * @param text
         */
        function decode(text: string): string;

        /**
         * Encode a NativeBytes  to Base64
         * @param text
         */
        function encodeAsNativeBytes(text: string): string;

        /**
         *  Encode  Base64 to NativeBytes.
         * @param text
         */
        function decodeAsNativeBytes(text: string): string;
    }
    module alphanumeric {
        function toAlphanumeric(string: string);

        function randomstring(length: number, charset: string): string;

        function alphanumeric(length: number, lowercase: boolean): string;

        function alpha(length: number, lowercase: boolean): string;

        function numeric(length: number): string;

        function isNumeric(str: string): boolean;

        function isAlphanumeric(str: string): boolean;
    }
    module assert {
        function assertTrue(condition: boolean, message: string);

        function assertNotNull(condition: boolean, message: string);

        function assertEquals(condition: boolean, message: string);
    }
    module digest {
        /**
         * Calculates the MD5 digest and returns the value as a 16 element byte array
         * @param input
         */
        function md5(input: any): byte[];

        /**
         * Calculates the MD5 digest and returns the value as a 32 character hex string
         * @param input
         */
        function md5Hex(input: any): string;

        /**
         * Returns an SHA-1 digest
         * @param input
         */
        function sha1(input: any): byte[];

        function sha1AsNativeBytes(input: any): string;

        /**
         * Returns an SHA-256 digest
         * @param input
         */
        function sha256(input: any): byte[];

        function sha256AsNativeBytes(input: any): string;

        /**
         * Returns an SHA-384 digest
         * @param input
         */
        function sha384(input: any): byte[];

        function sha384AsNativeBytes(input: string): string;

        /**
         * Returns an SHA-512 digest
         * @param input
         */
        function sha512(input: any): byte[];

        function sha512AsNativeBytes(input: any): string;

        /**
         * Calculates the SHA-1 digest and returns the value as a hex string
         * @param input
         */
        function sha1Hex(input: any): string;
    }
    module escape {
        /**
         * Escapes an input CSV string
         * @param input
         */
        function escapeCsv(input: string): string;

        /**
         * Escapes an input Javascript string
         * @param input
         */
        function escapeJavascript(input: string): string;

        /**
         * Escapes an input HTML3 string
         * @param input
         */
        function escapeHtml3(input: string): string;

        /**
         * Escapes an input HTML4 string
         * @param input
         */
        function escapeHtml4(input: string): string;

        /**
         * Escapes an input Java string
         * @param input
         */
        function escapeJava(input: string): string;

        /**
         * Escapes an input JSON string
         * @param input
         */
        function escapeJson(input: string): string;

        /**
         * Escapes an input XML string
         * @param input
         */
        function escapeXml(input: string): string;

        /**
         * Unescapes an input CSV string
         * @param input
         */
        function unescapeCsv(input: string): string;

        /**
         * Unescapes an input Javascript string
         * @param input
         */
        function unescapeJavascript(input: string): string;

        /**
         * Unescapes an input HTML3 string
         * @param input
         */
        function unescapeHtml3(input: string): string;

        /**
         * Unescapes an input HTML4 string
         * @param input
         */
        function unescapeHtml4(input: string): string;

        /**
         * Unescapes an input Java string
         * @param input
         */
        function unescapeJava(input: string): string;

        /**
         * Unescapes an input JSON string
         * @param input
         */
        function unescapeJson(input: string): string;

        /**
         * Unescapes an input XML string
         * @param input
         */
        function unescapeXml(input: string): string;
    }
    module hex {
        /**
         * Encode an input string to HEX
         * @param text
         */
        function encode(text: string): string;

        /**
         * Encode an input bytearray to HEX
         * @param input
         */
        function encodeAsBytes(input: byte[]): string;

        /**
         * Decode an input string from HEX
         * @param text
         */
        function decode(text: string): string;

        /**
         * Encode an input NativeByteArray to HEX
         * @param input
         */
        function encodeAsNativeBytes(input: byte[]): string;

        /**
         * Decode  an NativeByteArray from HEX
         * @param text
         */
        function decodeAsNativeBytes(text: string): byte[];
    }
    module url {
        /**
         * Encode an input string to application/x-www-form-urlencoded format
         * @param input
         * @param charset
         */
        function encode(input: string, charset: string): string;

        /**
         * Escape an input string to comply to URI RFC 3986
         * @param input
         */
        function escape(input: string): string;

        /**
         * Decode an input string from application/x-www-form-urlencoded format
         * @param input
         * @param charset
         */
        function decode(input: string, charset: string): string;

        function escapePath(text: string): string;

        function escapeForm(text: string): string;
    }
    module utf8 {
        function encode(input: string, charset: string): string;

        function escape(input: string): string;

        function byteToString(bytes: string, offset: number, length: number): string;
    }
    module uuid {
        /**
         * Returns a random UUID string
         */
        function random(): string;

        /**
         * Validates whether the provided input is a valid UUID string
         * @param input
         */
        function validate(input: string): boolean;
    }
    module xml {
        /**
         * Converts a JSON to a XML string
         * @param input
         */
        function fromJson(input: JSON): string;

        /**
         * Converts a XML to JSON string
         * @param input
         */
        function toJson(input: string): string
    }
    module qrcode{
        function generateQRCode(input:string):byte[];
    }
}
