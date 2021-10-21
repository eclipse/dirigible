declare module "@dirigible/cassandra" {
    module client {
        function getSession(hots:string,port:number): Session;
        function getDbResults(keyspaceName:string,query:string):ResultSet;

    }
    interface Row{
        asJson():JSON;
    }

    interface ResultSet {
        getRowAsString():string;
    }

    interface Session {
        executeQuery(query:String):string;
        getLoggedKeyspaceName():string;
        closeSession():string
        getDBREsult(keyspaveName:string,query:string):ResultSet;
    }

}