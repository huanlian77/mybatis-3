# 带着问题看源码

- [x] MyBatis如何实现Mapper文件与Java Mapper接口一一映射的
- [x] MyBatis如何实现动态SQL的
- [x] MyBatis插件如何定制
- [x] MyBatis事务如何控制
- [x] SqlSessionFactory 和 SqlSession 是什么
- [x] StatementHandler 是什么
- [x] MyBatis架构
- [x] MyBatis如何设置避免MySQL8小时断开连接，MyBatis无法知道MySQL已经自动断开连接呢
- [x] MyBatis一级缓存、二级缓存，缓存危害
- [x] MyBatis如何自定义TypeHandler
- [x] MyBatis的Mapper映射文件和接口类中SQL是如何加载
- [x] MyBatis中的设计模式

# 回答
##  MyBatis如何实现Mapper文件与Java Mapper接口一一映射的
MyBatis从Mapper所在目录或者Classpath中读取Mapper.xml，然后进行解析，然后通过MapperBuilderAssistant#addMappedStatement()方法放入Configuration对象中，其中MapperStatement中id保存着标识，该标识是 namespace + 方法名。

## MyBatis如何实现动态SQL的

注解式的是根据SQL中`<script>`关键字判断的进不进行动态SQL判断，XML式是对所有SQL进行动态SQL盘判断。

判断方式，判断XML的Node有没有  `<Choose>、<Set>、<ForEach>、<If>、<Otherwise>、<Trim>、<Where>、<Bind>`

## MyBatis插件如何定制
继承Interceptor接口，实现intercept()方法，通过@Intercepts和@Signature两个注解指定被代理的类和方法。

Mybatis允许插件功能代理的类有四个：Executor、StatementHandler、ResultSetHandler、StatementHandler

## MyBatis事务如何控制

MyBatis提供名为 JdbcTransaction 类

该类提供 getConnection()、commit()、rollback()方法，实际上操作的是从 Datasource 中获取的 Connection 对象。 

## SqlSessionFactory 和 SqlSession 是什么

SqlSession是MyBatis对外暴露的API，通过SqlSession与MyBatis进行交互。

SqlSessionFactory用于创建SqlSession的工厂。

## StatementHandler 是什么

StatementHandler负责处理与JDBC Statement之间的交互。

- SimpleStatementHandler
	
> 这个很简单了，就是对应我们JDBC中常用的Statement接口，用于简单SQL的处理。
	
- PreparedStatementHandler
	
> 这个对应JDBC中的PreparedStatement，预编译SQL的接口。
	
- CallableStatementHandler

	> 这个对应JDBC中CallableStatement，用于执行存储过程相关的接口。

- RoutingStatementHandler

	> 这个接口是以上三个接口的路由，没有实际操作，只是负责上面三个StatementHandler的创建及调用。

## MyBatis架构

<img src="http://static2.iocoder.cn/images/MyBatis/2020_01_04/04.png" style="zoom:50%;" />


分为三层，从上到下依次为：接口层，核心处理层和接口支持层

基础支持层（对应MyBatis源码中的功能目录）:

- [x] 数据源模块（datasource）：处理数据源连接、池化等功能 
- [x] 事务管理模块（transaction）：对数据库事务进行抽象
- [x] 缓存模块（cache）：支持一级和二级缓存
- [x] Binding模块（binding）：对Mapper接口类进行代理，生成一个MapperProxy代理类，在这个代理类中实现Mapper接口中方法与Mapper.xml文件进行绑定
- [x] 反射模块（reflection）：对Java反射模块进行封装，完成对类和类对象的元数据操作
- [x] 类型转化（type）：JdbcType与JavaType映射，是利用TypeHandlerRegistry类实现的；别名是TypeAliasRegistry类实现的
- [x] 日志模块（logging）：通过LogFactory中的static代码块，按顺序加载日志实现类，找到第一个可用的日志工具
- [x] 资源加载（io）：从Resource\Url中加载资源，并且通过ClassLoaderWrapper类指定 ClassLoader 的加载顺序
- [x] 解析器模块（parsing）：解析MyBatis Config XML和 Mapper XML文件成Document，并提供一系列从Document中获取元素值的方法
- [x] 注解模块（annotations）：定义了很多注解
- [x] 异常模块（exceptions）：定义了 PersistenceException 类，每个模块的异常都继承该类。

核心处理层：
- [x] 配置解析（builder）：解析Mapper映射文件和接口类的**增删改查**方法为MappedStatement对象
- [x] 参数映射：参数映射是多个模块完成的。核心是ParameterMapping和DefaultParameterHandler类
- [x] SQL解析（scripting）：MyBatis SQL语句会解析成一个个SqlNode，最终解析成一个SqlSource。builder模块在进行配置解析处理**增删改查**方法会调用该模块进行SQL处理，包括动态SQL的处理
- [x] SQL执行（executor和cursor）：
    - executor：SQL执行模块
    - cursor：结果处理游标
- [x] 结果集映射（mapping）: SQL执行结果与MyBatis定义的 `<ResultMap>,<Result>,<ParameterMap>,<Parameter>`映射
- [x] 插件（plugin）：实现Interceptor接口，通过Plugin代理类来实

接口层:

- [x] sqlSession（session）：MyBatis对外交互模块

## MyBatis如何设置避免MySQL8小时断开连接，MyBatis无法知道MySQL已经自动断开连接呢

错误信息：com.mysql.jdbc.exceptions.jdbc4.CommunicationsException: Communications link failure

通过设置 poolPingEnabled = true 开启SQL探测，可以从 `org.apache.ibatis.datasource.pooled.PooledDataSource` 中得知。

## MyBatis一级缓存、二级缓存，缓存危害
缓存危害：

MyBatis缓存与MyBatis及应用运行在同一个JVM中，共享同一块堆内存。当缓存数据量大时，会影响系统中其他功能的允许，所以当缓存量大时，考虑Redis、Memcache。

## MyBatis如何自定义TypeHandler
```java
@MappedTypes(String.class)
@MappedJdbcTypes(value={JdbcType.CHAR,JdbcType.VARCHAR}, includeNullJdbcType=true)
public class StringTrimmingTypeHandler implements TypeHandler<String> {
    // 省略实现 
}
```
定义一个 StringTrimmingTypeHandler 类，该类上面有 @MappedTypes 和 @MappedJdbcTypes 两个注解。
- @MappedTypes指定Java类型 
- @MappedJdbcTypes指定Jdbc类型

使用：
从 sqlSessionFactory 中获取 TypeHandlerRegistry ，调用 register() 方法，进行注册。

```java
SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader)
sqlSessionFactory.getConfiguration().getTypeHandlerRegistry().register(StringTrimmingTypeHandler.class);
```

## MyBatis的Mapper映射文件和接口类中SQL是如何加载

- MyBatis初始化时通过XMLConfigBuild解析 mybatis-config.xml 文件，然后解析到 mappers 元素，获取Mapper映射文件的路径。
- 加载Mapper映射文件，把Mapper映射文件中 `select|insert|update|delete`  元素解析为 MapperStatement 对象。{@link XMLMapperBuilder#configurationElement}
- 通过Mapper映射文件的namespace属性获取Mapper接口类的全类名 **（所以MyBatis规定namespace一定是Mapper接口类全类名的原因）**。遍历Mapper接口类所有的方法，获取方法注解，如果存在 ` @Select、@Update、@Insert、@Delete、@SelectProvider、@UpdateProvider、@InsertProvider、@DeleteProvider` 则解析为 MapperStatement 对象。{@link MapperAnnotationBuilder#getSqlSourceFromAnnotations}

## MyBatis中的设计模式

代理模式

- MapperProxy：对Mapper接口类的代理
- PooledConnection：对Java Connection类的代理
- Plugin：对Executor、StatementHandler、ResultSetHandler、StatementHandler类的代理

装饰者模式

- StatementHandler
    - RoutingStatementHandler：持有StatementHandler属性对象，与Java中的IO相似
    - BaseStatementHandler
      - PreparedStatementHandler
      - CallableStatementHandler
      - SimpleStatementHandler
- Cache
    - PerpetualCache：只有PerpetualCache是Cache基本实现，其他Cache子类都持有 Cache属性对象
    - BlockingCache
    - LruCache

工厂方法模式

- DataSourceFactory

构造器模式

- XMLConfigBuilder
- XMLMapperBuilder
- XMLStatementBuilder
- MapperAnnotationBuilder
- SqlSessionFactoryBuilder