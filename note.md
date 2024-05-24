### Swagger常用注解

| 注解              | 说明                                                   |
| ----------------- | ------------------------------------------------------ |
| @Api              | 用在类上，例如Controller，表示对类的说明               |
| @ApiModel         | 用在类上，例如entity、DTO、VO                          |
| @ApiModelProperty | 用在属性上，描述属性信息                               |
| @ApiOperation     | 用在方法上，例如Controller的方法，说明方法的用途、作用 |



### 获取当前登录用户

该方法简化了开发，目前已知两种方法：

一是根据Token获取当前用户ID，然后根据ID查询用户对象；

二也是根据Token获取当前用户ID，但是通过引入ThreadLocal包，通过将当前线程的ID改为当前用户的ID，当需要获取当前登录用户时，仅需获取当前线程ID即可。



## Redis

在内存层面实现的key-value数据库（关系型数据库），查找更为迅速，用于热点资讯的查询、秒杀等实现。一般用于Linux。



### Redis中的数据类型

- 字符串(string):普通字符串，Redis中最简单的数据类型。

- 哈希(hash):也叫散列，类似于Java中的HashMap结构。

- 列表(list):按照插入顺序排序，可以有重复元素，类似于Java中的LinkedList。
- 集合(set):无序集合，没有重复元素，类似于Java中的HashSet。
- 有序集合(sorted set / zset):集合中每个元素关联一个分数(score)，根据分数升序排序，没有重复元素。



### Redis字符串类型常用命令

| 命令                    | 作用                                            |
| ----------------------- | ----------------------------------------------- |
| set key value           | 设置指定key的值                                 |
| get key                 | 获取指定key的值                                 |
| setex key seconds value | 设置指定key的值，并将key的过期时间设为seconds秒 |
| setnx key value         | 只有在key不存在时设置key的值                    |



### Redis哈希类型常用命令

是一个string类型的field和value的映射表，哈希特别适合用于存储对象。

| 命令                 | 作用                                  |
| -------------------- | ------------------------------------- |
| hset key field value | 将哈希表key中的字段field的值设为value |
| hget key field       | 获取存储在哈希表中指定字段的值        |
| hdel key field       | 删除存储在哈希表中的指定字段          |
| hkeys key            | 获取哈希表中所有的字段                |
| hvals key            | 获取哈希表中所有值                    |



### Redis列表类型常用命令

按照插入顺序排序。

| 命令                      | 作用                         |
| ------------------------- | ---------------------------- |
| LPUSH key value1 [value2] | 将一个或多个值插入到列表头部 |
| LRANGE key start stop     | 获取列表指定范围内的元素     |
| RPOP key                  | 移除并获取列表最后一个元素   |
| LLEN key                  | 获取列表长度                 |



### Redis Set类型常用命令

集合成员是唯一的，集合中不能出现重复的数据。

| 命令                       | 作用                     |
| -------------------------- | ------------------------ |
| SADD key member1 [member2] | 向集合添加一个或多个成员 |
| SMEMBERS key               | 返回集合中的所有成员     |
| SCARD key                  | 获取集合的成员数         |
| SINTER key1 [key2]         | 返回给定所有集合的交集   |
| SUNION key1 [key2]         | 返回所有给定集合的并集   |
| SREM key member1 [member2] | 删除集合中一个或多个成员 |



### Redis有序集合类型常用命令

有序集合是string类型元素的集合，且不允许有重复成员。每个元素都会关联一个double类型的分数。

| 命令                                     | 作用                                        |
| ---------------------------------------- | ------------------------------------------- |
| ZADD key score1 member1 [score2 member2] | 向有序集合添加一个或多个成员                |
| ZRANGE key start stop [WITHSCORES]       | 通过索引区间返回有序集合中指定区间内的成员  |
| ZINCRBY key increment member             | 有序集合中对指定成员的分数加上增量increment |
| ZREM key member [member ...]             | 移除有序集合中的一个或多个成员              |



### Redis通用命令

| 命令         | 作用                         |
| ------------ | ---------------------------- |
| KEYS pattern | 查找所有符合给定模式的key    |
| EXISTS key   | 检查给定key是否存在          |
| TYPE key     | 返回key所储存的值的类型      |
| DEL key      | 该命令用于在key存在时删除key |



### Redis的Java端

常用的有Jedis、Lettuce、Spring Data Redis。



### Spring Data Redis使用方式

操作步骤：

1. 导入Spring Data Redis的maven坐标
2. 配置redis数据源
3. 编写配置类，创建RedisTemplate对象
4. 通过RedisTemplate对象操作Redis



```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```





### Spring Cache

一个用于操控内存数据库的框架，实现了基于注解的缓存功能，只需要添加简单的注解就能实现缓存功能。

Spring Cache提供了一层抽象，底层可以切换不同的缓存实现，比如：

- EHCache
- Caffeine
- Redis



常用注解如下

| 注解           | 说明                                                         |
| -------------- | ------------------------------------------------------------ |
| @EnableCaching | 开启缓存注解功能，通常加在启动类上                           |
| @Cacheable     | 在方法执行前先查询缓存中是否有数据，如果有数据，则直接返回缓存数据；如果没有缓存数据，调用方法并将方法返回值放到缓存中 |
| @CachePut      | 将方法的返回值放到缓存中                                     |
| @CacheEvict    | 将一条或多条数据从缓存中删除                                 |

