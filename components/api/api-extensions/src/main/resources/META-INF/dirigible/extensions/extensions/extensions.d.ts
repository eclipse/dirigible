declare module "@dirigible/extensions" {
    
    module extensions {
        /**
         * Returns an array of the extensions names for the specified extension point
         * @param extensionPoint
         */
        function getExtensions(extensionPoint): string[];

        /**
         *Returns an array of the extension points names
         */
        function getExtensionPoints(): string[];
    }
    
}
