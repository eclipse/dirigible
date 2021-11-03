declare module "@dirigible/indexing" {
    module searcher {
        function search(index, term): JSON;

        function before(index, date): JSON;

        function after(index, date): JSON;

        function between(index, lower, upper);
    }
    module writer {
        function add(index, location, contents, lastModified, parameters);
    }
}