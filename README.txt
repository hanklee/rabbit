Simple Database Access Operations And Redis Cache System base on Java

Feature:

Using XML configure the DAO and Cache System
Mysql,Redis DAO solution
Cache System major base on Redis


ChangeLog

1.2.beta

1.2.1 CacheDBObjectDAO add method cleanAllTableCacheObject,

1.2.2 MapToObjectHandler class method void handler(T o); -> boolean handler(T o);
if handler return false the MapToObject return null

1.3.beta

1.3.1 remove memory cache , only redis cache, and sync to redis cache to database

1.3.2 DBObject.xml class-table configure can specify which datasource, is not using default.

1.3.3 HandlCacheJob -> CacheHandler , RedisHandlCacheJob -> RedisCacheHandler

1.3.4 DBObjectHelper -> DBObjectManager

1.3.5 improve Redis cache configure

1.4.beta

1.4.1 add redis DAO, CacheDAO which is handle the cache operation data.

1.4.2 performance, key-value is better than  key-Map

1.4.3 delete SimpleObjectListHandler, SimpleMapToObject, SimpleObjectHandler

1.4.4 add JSONObj class, this class contains some json operations

1.5.beta

2.0.beta

2.0.1  move BoneCP to HikariCP, add no-sql(redis,elasticsearch,mongodb) support.

DBObjectDAO change name to SQLDAO

add Redis DAO operation

//todo list


No support redis cluster

Log System
