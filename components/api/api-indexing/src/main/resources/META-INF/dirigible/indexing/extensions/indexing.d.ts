declare module "@dirigible/indexing" {
    /**
     * The Indexing Searcher is the object used for free-text or exact periods searches over the added documents with the Indexing Writer. This version is backed by the Apache Lucene.
     */
    module searcher {
        /**
         * Returns an array of document descriptors matching the term
         */
        function search(index, term): JSON;

        /**
         * Returns an array of document descriptors where lastModified is before the date
         * @param index
         * @param date
         */
        function before(index, date): JSON;

        /**
         * Returns an array of document descriptors where lastModified is after the date
         * @param index
         * @param date
         */
        function after(index, date): JSON;

        /**
         * Returns an array of document descriptors where lastModified is between the lower and upper
         * @param index
         * @param lower
         * @param upper
         */
        function between(index, lower, upper);
    }
    /**
     * The Indexing Writer is an object which can store a text content with additional parameters for later high-performant free-text search. This version is backed by the Apache Lucene.
     */
    module writer {
        /**
         * Adds a document contents with the given location and parameters to an index
         * @param index
         * @param location
         * @param contents
         * @param lastModified
         * @param parameters
         */
        function add(index, location, contents, lastModified?, parameters?);
    }
}
