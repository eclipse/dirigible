declare module "@dirigible/utils" {
    module base64 {
        function encode(text: string): string;

        function encodeAsBytes(input: string): string;

        function decode(text: string): string;

        function encodeAsNativeBytes(text: string): string;

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
        function md5AsNativeBytes(input: any): string;

        function md5Hex(input: any): string;

        function sha1(input: any): string;

        function sha1AsNativeBytes(input: any): string;

        function sha256(input: any): string;

        function sha256AsNativeBytes(input: any): string;

        function sha384(input: any): string;

        function sha384AsNativeBytes(input: string): string;

        function sha512(input: any): string;

        function sha512AsNativeBytes(input: any): string;

        function sha1Hex(input: any): string;
    }
    module escape {
        function escapeCsv(input: string): string;

        function escapeJavascript(input: string): string;

        function escapeHtml3(input: string): string;

        function escapeHtml4(input: string): string;

        function escapeJava(input: string): string;

        function escapeJson(input: string): string;

        function escapeXml(input: string): string;

        function unescapeCsv(input: string): string;

        function unescapeJavascript(input: string): string;

        function unescapeHtml3(input: string): string;

        function unescapeHtml4(input: string): string;

        function unescapeJava(input: string): string;

        function unescapeJson(input: string): string;

        function unescapeXml(input: string): string;
    }
    module hex {
        function encode(text: string): string;

        function encodeAsBytes(input: string): string;

        function decode(text: string): string;

        function encodeAsNativeBytes(text: string): string;

        function decodeAsNativeBytes(text: string): string;
    }

    module url {
        function encode(input: string, charset: string): string;

        function escape(input: string): string;

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
        function random(): string;

        function validate(input: string): string
    }
    module xml {
        function fromJson(input: JSON): string;

        function toJson(input: string): string
    }

   module qrcode {
          function generateQRCode(text: string): byte[];
      }

}