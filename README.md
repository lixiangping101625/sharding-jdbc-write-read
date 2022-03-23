# 分库分表说明
#####  当公司业务量急剧上升后，数据库会达到性能瓶颈。对数据库一味纵向升级很难满足业务场景！
与对单节点数据库升级相比更建议使用分库分表的方案。目前常用的分库分表中间件有MyCat和Sharding-JDBC。
本项目使用Sharding-JDBC，究其原因是Sharding-JDBC更加轻量，通俗的讲对非DBA人员更加友好：你只需要根据项目
的选型，选择Sharding-JDBC对应的配置方式（ShardingSphere-JDBC 可以通过 Java，YAML，Spring 命名空间和
Spring Boot Starter 这 4 种方式进行配置，开发者可根据场景选择适合的配置方式。 详情请参见用户手册：
https://shardingsphere.apache.org/document/current/cn/quick-start/shardingsphere-jdbc-quick-start/）。
本项目采用的是Spring boot，所以自然选择的是Spring boot starter的方式进行分库分表的规则配置。
另外，Sharding-JDBC支持同库分表，但MyCat不支持！Sharding-JDBC使用Java开发的，所以如果项目非java开发，请慎用~~

# 场景说明（需求）
#####  对订单表进行分库分表：
    订单id为奇数时数据落到ds0库，订单id为偶数时数据落到ds1库.
    user_id为奇数时数据落到t_order_0表，user_id为偶数时数据落到t_order_1表.


###数据库&表 准备工作：
##### 新建两个库，在库下分别创建如下真实表:
    CREATE TABLE `t_order_0` (
      `id` bigint(11) NOT NULL,
      `order_amount` decimal(10,2) NOT NULL,
      `order_status` int(11) NOT NULL,
      `user_id` bigint(11) NOT NULL,
      PRIMARY KEY (`id`) USING BTREE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
    
    CREATE TABLE `t_order_1` (
      `id` bigint(11) NOT NULL,
      `order_amount` decimal(10,2) NOT NULL,
      `order_status` int(11) NOT NULL,
      `user_id` bigint(11) NOT NULL,
      PRIMARY KEY (`id`) USING BTREE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

###pom添加依赖：
    参见官方文档：https://shardingsphere.apache.org/document/current/cn/user-manual/shardingsphere-jdbc/spring-boot-starter/
    <!--sharding-jdbc-->
    <dependency>
        <groupId>org.apache.shardingsphere</groupId>
        <artifactId>shardingsphere-jdbc-core-spring-boot-starter</artifactId>
        <version>5.1.0</version>
    </dependency>
    
### ——————————————————————————    
### Spring boot starter方式配置Sharding-jdbc说明    
#### 生命ShardingSphere的数据源
spring.shardingsphere.datasource.names=ds0,ds1
### 配置第 1 个数据源
spring.shardingsphere.datasource.ds0.type=com.zaxxer.hikari.HikariDataSource
spring.shardingsphere.datasource.ds0.driver-class-name=com.mysql.cj.jdbc.Driver
spring.shardingsphere.datasource.ds0.jdbc-url=jdbc:mysql://localhost:3307/shard_order
spring.shardingsphere.datasource.ds0.username=root
spring.shardingsphere.datasource.ds0.password=123456
### 配置第 2 个数据源
spring.shardingsphere.datasource.ds1.type=com.zaxxer.hikari.HikariDataSource
spring.shardingsphere.datasource.ds1.driver-class-name=com.mysql.cj.jdbc.Driver
spring.shardingsphere.datasource.ds1.jdbc-url=jdbc:mysql://120.27.203.113:3306/sharding_order
spring.shardingsphere.datasource.ds1.username=root
spring.shardingsphere.datasource.ds1.password=yuanban_mysql
### 分库规则（其实就是拼接实际节点）
spring.shardingsphere.rules.sharding.tables.t_order.actual-data-nodes=ds$->{0..1}.t_order_$->{0..1}
### 分库策略（这里采用单分片键）
##### #数据库分片字段为`id`
spring.shardingsphere.rules.sharding.tables.t_order.database-strategy.standard.sharding-column=id
##### #数据库分片算法名称（自定义）
spring.shardingsphere.rules.sharding.tables.t_order.database-strategy.standard.sharding-algorithm-name=database-inline

####分表策略，同分库策略
##### #数据表分片算字段为`user_id`
spring.shardingsphere.rules.sharding.tables.t_order.table-strategy.standard.sharding-column=user_id
##### #数据表分片算法名称（自定义）
spring.shardingsphere.rules.sharding.tables.t_order.table-strategy.standard.sharding-algorithm-name=table-inline

##### 分片算法配置
##### #分库选择`行表达式分片算法`
spring.shardingsphere.rules.sharding.sharding-algorithms.database-inline.type=INLINE
##### #分库规则：根据`id`对`2`取模：值为0到`ds0`库，值为1到`ds1`库
spring.shardingsphere.rules.sharding.sharding-algorithms.database-inline.props.algorithm-expression=ds$->{id % 2}
##### #分表页选择`行表达式分片算法`
spring.shardingsphere.rules.sharding.sharding-algorithms.table-inline.type=INLINE
##### #分表规则：根据`user_id`对`2`取模：值为0到`t_order_0`表，值为1到`t_order_1`表
spring.shardingsphere.rules.sharding.sharding-algorithms.table-inline.props.algorithm-expression=t_order_$->{user_id % 2}

