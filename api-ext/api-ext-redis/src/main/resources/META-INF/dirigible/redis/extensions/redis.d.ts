declare module "@dirigible/redis" {
    module client {
        function getClient():Client;
        interface Client{
             append(key,value);
             bitcount(key);
             decr(key);
             del(key);
             exists(key):boolean;
             get(key);
             incr(key);
             keys(pattern);
             set(key,value);
             lindex(key,index);
             llen(key);
             lpop(key);
             lpush(key,value);
             lrange(key,start,stop);
             rpop(key);
             rpush(key,value);
        }
    }
}
