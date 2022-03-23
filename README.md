# Sharding-jdbc读写分离
#####  当公司业务量急剧上升后，数据库会达到性能瓶颈。对数据库一味纵向升级很难满足业务场景！
前面我们介绍了分库分表，但是在业务量增持前期，也可以采用读写分离的方式提升数据层服务能力。
究其根本原因是大部分外部应用读多写少（二八原则）。所以可以通过Sharding-jdbc实现读写分离。
当然，读写分离涉及了主从数据库的同步问题，这里不着重介绍~测试Sharding-jdbc读写分离只需要在数据新增
后手动粘贴到从库对应的同名表即可。

    目前常用的分库分表中间件有MyCat和Sharding-JDBC。
    本项目使用Sharding-JDBC，究其原因是Sharding-JDBC更加轻量，通俗的讲对非DBA人员更加友好：你只需要根据项目
    的选型，选择Sharding-JDBC对应的配置方式（ShardingSphere-JDBC 可以通过 Java，YAML，Spring 命名空间和
    Spring Boot Starter 这 4 种方式进行配置，开发者可根据场景选择适合的配置方式。 详情请参见用户手册：
    https://shardingsphere.apache.org/document/current/cn/quick-start/shardingsphere-jdbc-quick-start/）。
    本项目采用的是Spring boot，所以自然选择的是Spring boot starter的方式进行分库分表的规则配置。
    另外，Sharding-JDBC支持同库分表，但MyCat不支持！Sharding-JDBC使用Java开发的，所以如果项目非java开发，请慎用~~

# 场景说明（需求）
#####  对订单表进行读写分离：
    新增订单时向主库的t_order表写入.
    读取订单时从从库的t_order读取.


###数据库&表 准备工作：
##### 新建两个库（主从，这里不着重讨论），在库下分别创建如下表:
    CREATE TABLE `t_order` (
      `id` int(11) NOT NULL,
      `title` varchar(255) DEFAULT NULL,
      `amount` decimal(10,2) DEFAULT NULL,
      PRIMARY KEY (`id`)
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
### Spring boot starter方式配置Sharding-jdbc说明（rw1时主从名称自定义即可）如下：   
    # 生命ShardingSphere的数据源
    spring.shardingsphere.datasource.names=primary-ds,replica-ds-0
    
    # 配置写操作数据源（主数据源）
    spring.shardingsphere.datasource.primary-ds.type=com.zaxxer.hikari.HikariDataSource
    spring.shardingsphere.datasource.primary-ds.driver-class-name=com.mysql.cj.jdbc.Driver
    spring.shardingsphere.datasource.primary-ds.jdbc-url=jdbc:mysql://localhost:3307/read-write-separate
    spring.shardingsphere.datasource.primary-ds.username=root
    spring.shardingsphere.datasource.primary-ds.password=123456
    # 配置读操作数据源（从数据源）
    spring.shardingsphere.datasource.replica-ds-0.type=com.zaxxer.hikari.HikariDataSource
    spring.shardingsphere.datasource.replica-ds-0.driver-class-name=com.mysql.cj.jdbc.Driver
    spring.shardingsphere.datasource.replica-ds-0.jdbc-url=jdbc:mysql://120.27.203.113:3306/read-write-separate
    spring.shardingsphere.datasource.replica-ds-0.username=root
    spring.shardingsphere.datasource.replica-ds-0.password=yuanban_mysql

    ### 读写分离类型
    spring.shardingsphere.rules.readwrite-splitting.data-sources.rw1.type=Static
    #### 自动发现数据源名称不用配置
    #spring.shardingsphere.rules.readwrite-splitting.data-sources.rw1.props.auto-aware-data-source-name= # 自动发现数据源名称（与数据库发现配合使用）
    spring.shardingsphere.rules.readwrite-splitting.data-sources.rw1.props.write-data-source-name=primary-ds
    # 多从用逗号分开即可
    spring.shardingsphere.rules.readwrite-splitting.data-sources.rw1.props.read-data-source-names=replica-ds-0
    # 自定义负载均衡名称
    spring.shardingsphere.rules.readwrite-splitting.data-sources.rw1.load-balancer-name=lun_xun
    # 负载均衡算法配置（轮询）
    spring.shardingsphere.rules.readwrite-splitting.load-balancers.lun_xun.type=ROUND_ROBIN
    # (ROUND_BIN算法无可配置属性)
    #spring.shardingsphere.rules.readwrite-splitting.load-balancers.lun_xun.props.xxx= # 负载均衡算法属性配置
    
    #属性配置
    spring.shardingsphere.props.sql-show=true

