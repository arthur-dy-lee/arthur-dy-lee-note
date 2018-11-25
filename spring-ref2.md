IV. Data Access
----------
# 13. Transaction Management
----------
## 13.1. Introduction to Spring Framework transaction management
Java Transaction API (JTA)

Java Persistence API (JPA)


## 13.2. Advantages of the Spring Framework’s transaction support model
### 13.2.1. Global transactions
### 13.2.2. Local transactions
### 13.2.3. Spring Framework’s consistent programming model
## 13.3. Understanding the Spring Framework transaction abstraction
A transaction strategy is defined by the org.springframework.transaction.PlatformTransactionManager interface:
```Java
public interface PlatformTransactionManager {
  TransactionStatus getTransaction(
  TransactionDefinition definition) throws TransactionException;
  void commit(TransactionStatus status) throws TransactionException;
  void rollback(TransactionStatus status) throws TransactionException;
}
```

The TransactionStatus interface provides a simple way for transactional code to control transaction execution and query transaction status. The concepts should be familiar, as they are common to all transaction APIs:
```Java
public interface TransactionStatus extends SavepointManager {
  boolean isNewTransaction();
  boolean hasSavepoint();
  void setRollbackOnly();
  boolean isRollbackOnly();
  void flush();
  boolean isCompleted();
}
```
## 13.4. Synchronizing resources with transactions
### 13.4.1. High-level synchronization approach
### 13.4.2. Low-level synchronization approach
### 13.4.3. TransactionAwareDataSourceProxy
## 13.5. Declarative transaction management 
The Spring Framework enables you to customize transactional behavior, by using AOP. For example, you can insert custom behavior in the case of transaction rollback. You can also add arbitrary advice, along with the transactional advice.

The Spring Framework does not support propagation of transaction contexts across remote calls, as do high-end application servers. If you need this feature, we recommend that you use EJB.

### 13.5.1. Understanding the Spring Framework’s declarative transaction implementation

The most important concepts to grasp with regard to the Spring Framework’s declarative transaction support are that this support is enabled via AOP proxies, and that the transactional advice is driven by metadata (currently XML- or annotation-based). The combination of AOP with transactional metadata yields an AOP proxy that uses a TransactionInterceptor in conjunction with an appropriate PlatformTransactionManager implementation to drive transactions around method invocations.

![](https://farm5.staticflickr.com/4479/23622132018_db74d48933_b.jpg)

### 13.5.2. Example of declarative transaction implementation
The following configuration is explained in detail in the next few paragraphs.
```XML
<!-- from the file 'context.xml' -->
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xmlns:aop="http://www.springframework.org/schema/aop"
xmlns:tx="http://www.springframework.org/schema/tx"
xsi:schemaLocation="
http://www.springframework.org/schema/beans
http://www.springframework.org/schema/beans/spring-beans.xsd
http://www.springframework.org/schema/tx
http://www.springframework.org/schema/tx/spring-tx.xsd
http://www.springframework.org/schema/aop
http://www.springframework.org/schema/aop/spring-aop.xsd">
  <!-- this is the service object that we want to make transactional -->
  <bean id="fooService" class="x.y.service.DefaultFooService"/>
  <!-- the transactional advice (what 'happens'; see the <aop:advisor/> bean below) -->
  <tx:advice id="txAdvice" transaction-manager="txManager">
  <!-- the transactional semantics... -->
    <tx:attributes>
      <!-- all methods starting with 'get' are read-only -->
      <tx:method name="get*" read-only="true"/>
      <!-- other methods use the default transaction settings (see below) -->
      <tx:method name="*"/>
    </tx:attributes>
  </tx:advice>
  <!-- ensure that the above transactional advice runs for any execution
  of an operation defined by the FooService interface -->
  <aop:config>
    <aop:pointcut id="fooServiceOperation" expression="execution(*x.y.service.FooService.*(..))"/>
    <aop:advisor advice-ref="txAdvice" pointcut-ref="fooServiceOperation"/>
  </aop:config>
  <!-- don't forget the DataSource -->
  <bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
    <property name="driverClassName" value="oracle.jdbc.driver.OracleDriver"/>
    <property name="url" value="jdbc:oracle:thin:@rj-t42:1521:elvis"/>
    <property name="username" value="scott"/>
    <property name="password" value="tiger"/>
  </bean>
  <!-- similarly, don't forget the PlatformTransactionManager -->
  <bean id="txManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
    <property name="dataSource" ref="dataSource"/>
  </bean>
  <!-- other <bean/> definitions here -->
</beans>
```
The transaction-manager attribute of the <tx:advice/> tag is set to the name of the PlatformTransactionManager bean that is going to drive the  
txManager bean.

```java
public final class Boot {
  public static void main(final String[] args) throws Exception {
    ApplicationContext ctx = new ClassPathXmlApplicationContext("context.xml", Boot.class);
    FooService fooService = (FooService) ctx.getBean("fooService");
    fooService.insertFoo (new Foo());
  }
}
```
The output from running the preceding program will resemble the following. (The Log4J output and the stack trace from the UnsupportedOperationException thrown by the insertFoo(..) method of the DefaultFooService class have been truncated for clarity.)

```xml
<!-- the Spring container is starting up... -->
[AspectJInvocationContextExposingAdvisorAutoProxyCreator] - Creating implicit proxy for bean 'fooService' with 0 common interceptors and 1 specific interceptors
<!-- the DefaultFooService is actually proxied -->
[JdkDynamicAopProxy] - Creating JDK dynamic proxy for [x.y.service.DefaultFooService]
<!-- ... the insertFoo(..) method is now being invoked on the proxy -->
[TransactionInterceptor] - Getting transaction for x.y.service.FooService.insertFoo
<!-- the transactional advice kicks in here... -->
[DataSourceTransactionManager] - Creating new transaction with name [x.y.service.FooService.insertFoo]
[DataSourceTransactionManager] - Acquired Connection [org.apache.commons.dbcp.PoolableConnection@a53de4]
for JDBC transaction
<!-- the insertFoo(..) method from DefaultFooService throws an exception... -->
[RuleBasedTransactionAttribute] - Applying rules to determine whether transaction should rollback on java.lang.UnsupportedOperationException
[TransactionInterceptor] - Invoking rollback for transaction on x.y.service.FooService.insertFoo due to
throwable [java.lang.UnsupportedOperationException]
<!-- and the transaction is rolled back (by default, RuntimeException instances cause rollback) -->
[DataSourceTransactionManager] - Rolling back JDBC transaction on Connection
[org.apache.commons.dbcp.PoolableConnection@a53de4]
[DataSourceTransactionManager] - Releasing JDBC Connection after transaction
[DataSourceUtils] - Returning JDBC Connection to DataSource
Exception in thread "main" java.lang.UnsupportedOperationException at x.y.service.DefaultFooService.insertFoo(DefaultFooService.java:14)
<!-- AOP infrastructure stack trace elements removed for clarity -->
at $Proxy0.insertFoo(Unknown Source)
at Boot.main(Boot.java:11)
```

### 13.5.3. Rolling back a declarative transaction
Checked exceptions that are thrown from a transactional method do not result in rollback in the default configuration.
Spring的事务实现采用基于AOP的拦截器来实现，如果没有在事务配置的时候注明回滚的checked exception，那么只有在发生了unchecked exception的时候，才会进行事务回滚。

Checked exception 是在编译时在语法上必须处理的异常，因此必须在语法上以try..catch加以处理；
Unchecked exception是运行时异常，它继承java.lang.RuntimeException

You can configure exactly which Exception types mark a transaction for rollback, including checked exceptions. The following XML snippet demonstrates how you configure rollback for a checked, application-specific Exception type.

```xml
<tx:advice id="txAdvice" transaction-manager="txManager">
  <tx:attributes>
    <tx:method name="get*" read-only="true" rollback-for="NoProductInStockException"/>
    <tx:method name="*"/>
  </tx:attributes>
</tx:advice>
```

You can also specify 'no rollback rules', if you do not want a transaction rolled back when an exception is thrown.
```xml
<tx:advice id="txAdvice">
  <tx:attributes>
    <tx:method name="updateStock" no-rollback-for="InstrumentNotFoundException"/>
    <tx:method name="*"/>
  </tx:attributes>
</tx:advice>
```
When the Spring Framework’s transaction infrastructure catches an exception and is consults configured rollback rules to determine whether to mark the transaction for rollback, the strongest matching rule wins. So in the case of the following configuration, any exception other than an InstrumentNotFoundException results in a rollback of the attendant transaction.

最强大的匹配的规则获胜
```xml
<tx:advice id="txAdvice">
  <tx:attributes>
    <tx:method name="*" rollback-for="Throwable" no-rollback-for="InstrumentNotFoundException"/>
  </tx:attributes>
</tx:advice>
```
You can also indicate a required rollback programmatically. Although very simple, this process is quite invasive, and tightly couples your code to the Spring Framework’s transaction infrastructure:
```java
public void resolvePosition() {
  try {
    // some business logic...
  } catch (NoProductInStockException ex) {
    // trigger rollback programmatically
    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
  }
}
```

### 13.5.4. Configuring different transactional semantics for different beans
### 13.5.5. <tx:advice/> settings
The default <tx:advice/> settings are:
 - Propagation setting is REQUIRED.
 - Any RuntimeException triggers rollback, and any checked Exception does not.
 - Isolation level is DEFAULT.
 - Transaction is read/write.
 - Transaction timeout defaults to the default timeout of the underlying transaction system, or none if timeouts are not supported.

### 13.5.6. Using @Transactional
enable the configuration of transactional behavior based on annotations

<tx:annotation-driven transaction-manager="txManager"/>

```xml
<!-- from the file 'context.xml' -->
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xmlns:aop="http://www.springframework.org/schema/aop"
xmlns:tx="http://www.springframework.org/schema/tx"
xsi:schemaLocation="
http://www.springframework.org/schema/beans
http://www.springframework.org/schema/beans/spring-beans.xsd
http://www.springframework.org/schema/tx
http://www.springframework.org/schema/tx/spring-tx.xsd
http://www.springframework.org/schema/aop
http://www.springframework.org/schema/aop/spring-aop.xsd">
  <!-- this is the service object that we want to make transactional -->
  <bean id="fooService" class="x.y.service.DefaultFooService"/>
  <!-- enable the configuration of transactional behavior based on annotations -->
  <tx:annotation-driven transaction-manager="txManager"/><!-- a PlatformTransactionManager is still required -->
  <bean id="txManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
    <!-- (this dependency is defined somewhere else) -->
    <property name="dataSource" ref="dataSource"/>
  </bean>
  <!-- other <bean/> definitions here -->
</beans>
```

> You can omit the transaction-manager attribute in the <tx:annotation-driven/> tag
if the bean name of the PlatformTransactionManager that you want to wire in has the
name transactionManager. If the PlatformTransactionManager bean that you want to
dependency-inject has any other name, then you have to use the transaction-manager
attribute explicitly, as in the preceding example.


> The @EnableTransactionManagement annotation provides equivalent support if you are using Java based configuration.


Since none of yours transaction managers are named transactionManager, you must specify which transaction manager should be used for methods marked with @Transactional.
```java
@Transactional("transactionManager1")
//...
@Transactional("transactionManager2")
//...
```

If you do annotate protected, private or package-visible methods with the @Transactional annotation, no error is raised, but the annotated method does not exhibit the configured transactional settings. Consider the use of AspectJ (see below) if you need to annotate non-public methods.

> Spring recommends that you only annotate concrete classes (and methods of concrete classes) with the @Transactional annotation, as opposed to annotating interfaces.You certainly can place the @Transactional annotation on an interface (or an interface method), but this works only as you would expect it to if you are using interface-based proxies.

在同一个类的方法内调用它也不会启动一个新的事务

>In proxy mode (which is the default), only external method calls coming in through the proxy are intercepted. This means that self-invocation, in effect, a method within the target object calling another method of the target object, will not lead to an actual transaction at runtime even if the invoked method is marked with @Transactional.

Consider the use of AspectJ mode (see mode attribute in table below) if you expect self-invocations to be wrapped with transactions as well.

> The proxy-target-class attribute controls what type of transactional proxies are created for classes annotated with the @Transactional annotation. If proxy-target-class is set to true, class-based proxies are created. If proxy-target-class is false or if the attribute is omitted, standard JDK interface-based proxies are created.

>@EnableTransactionManagement and <tx:annotation-driven/> only looks for
@Transactional on beans in the same application context they are defined in.

#### @Transactional settings
The default @Transactional settings are as follows:
 - Propagation setting is PROPAGATION_REQUIRED.
 - Isolation level is ISOLATION_DEFAULT.
 - Transaction is read/write.
 - Transaction timeout defaults to the default timeout of the underlying transaction system, or to none if timeouts are not supported.
 - Any RuntimeException triggers rollback, and any checked Exception does not.

#### Multiple Transaction Managers with @Transactional

```java
public class TransactionalService {
  @Transactional("order")
  public void setSomething(String name) { ... }

  @Transactional("account")
  public void doSomething() { ... }
}
```
could be combined with the following transaction manager bean declarations in the application context

```xml
<tx:annotation-driven/>
<bean id="transactionManager1" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
  ...
  <qualifier value="order"/>
</bean>
<bean id="transactionManager2" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
  ...
  <qualifier value="account"/>
</bean>

```
In this case, the two methods on TransactionalService will run under separate transaction managers, differentiated by the "order" and "account" qualifiers.

#### Custom shortcut annotations
If you find you are repeatedly using the same attributes with @Transactional on many different methods, then Spring’s meta-annotation support allows you to define custom shortcut annotations for your specific use cases. For example, defining the following annotations
```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Transactional("order")
public @interface OrderTx {
}

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Transactional("account")
public @interface AccountTx {
}
```
allows us to write the example from the previous section as
```java
public class TransactionalService {
  @OrderTx
  public void setSomething(String name) { ... }
  @AccountTx
  public void doSomething() { ... }
}
```
### 13.5.7. Transaction propagation
#### Required
#### RequiresNew
#### Nested
### 13.5.8. Advising transactional operations
### 13.5.9. Using @Transactional with AspectJ
## 13.6. Programmatic transaction management
### 13.6.1. Using the TransactionTemplate
#### Specifying transaction settings
### 13.6.2. Using the PlatformTransactionManager
## 13.7. Choosing between programmatic and declarative transaction management
## 13.8. Transaction bound event
## 13.9. Application server-specific integration
### 13.9.1. IBM WebSphere
### 13.9.2. Oracle WebLogic Server
## 13.10. Solutions to common problems
### 13.10.1. Use of the wrong transaction manager for a specific DataSource
## 13.11. Further Resources
# 14. DAO support
----------
## 14.1. Introduction
## 14.2. Consistent exception hierarchy
## 14.3. Annotations used for configuring DAO or Repository classes
# 15. Data access with JDBC
-----------
## 15.1. Introduction to Spring Framework JDBC
### 15.1.1. Choosing an approach for JDBC database access
### 15.1.2. Package hierarchy
## 15.2. Using the JDBC core classes to control basic JDBC processing and error handling
### 15.2.1. JdbcTemplate
#### Examples of JdbcTemplate class usage
#### JdbcTemplate best practices
### 15.2.2. NamedParameterJdbcTemplate
### 15.2.3. SQLExceptionTranslator
### 15.2.4. Executing statements
### 15.2.5. Running queries
### 15.2.6. Updating the database
### 15.2.7. Retrieving auto-generated keys
## 15.3. Controlling database connections
### 15.3.1. DataSource
### 15.3.2. DataSourceUtils
### 15.3.3. SmartDataSource
### 15.3.4. AbstractDataSource
### 15.3.5. SingleConnectionDataSource
### 15.3.6. DriverManagerDataSource
### 15.3.7. TransactionAwareDataSourceProxy
### 15.3.8. DataSourceTransactionManager
## 15.4. JDBC batch operations
### 15.4.1. Basic batch operations with the JdbcTemplate
### 15.4.2. Batch operations with a List of objects
### 15.4.3. Batch operations with multiple batches
## 15.5. Simplifying JDBC operations with the SimpleJdbc classes
### 15.5.1. Inserting data using SimpleJdbcInsert
### 15.5.2. Retrieving auto-generated keys using SimpleJdbcInsert
### 15.5.3. Specifying columns for a SimpleJdbcInsert
### 15.5.4. Using SqlParameterSource to provide parameter values
### 15.5.5. Calling a stored procedure with SimpleJdbcCall
### 15.5.6. Explicitly declaring parameters to use for a SimpleJdbcCall
### 15.5.7. How to define SqlParameters
### 15.5.8. Calling a stored function using SimpleJdbcCall
### 15.5.9. Returning ResultSet/REF Cursor from a SimpleJdbcCall
## 15.6. Modeling JDBC operations as Java objects
### 15.6.1. SqlQuery
### 15.6.2. MappingSqlQuery
### 15.6.3. SqlUpdate
### 15.6.4. StoredProcedure
## 15.7. Common problems with parameter and data value handling
### 15.7.1. Providing SQL type information for parameters
### 15.7.2. Handling BLOB and CLOB objects
### 15.7.3. Passing in lists of values for IN clause
### 15.7.4. Handling complex types for stored procedure calls
## 15.8. Embedded database support
### 15.8.1. Why use an embedded database?
### 15.8.2. Creating an embedded database using Spring XML
### 15.8.3. Creating an embedded database programmatically
### 15.8.4. Selecting the embedded database type
#### Using HSQL
#### Using H2
#### Using Derby
### 15.8.5. Testing data access logic with an embedded database
### 15.8.6. Generating unique names for embedded databases
### 15.8.7. Extending the embedded database support
## 15.9. Initializing a DataSource
### 15.9.1. Initializing a database using Spring XML
#### Initialization of other components that depend on the database
# 16. Object Relational Mapping (ORM) Data Access
## 16.1. Introduction to ORM with Spring
## 16.2. General ORM integration considerations
### 16.2.1. Resource and transaction management
### 16.2.2. Exception translation
## 16.3. Hibernate
### 16.3.1. SessionFactory setup in a Spring container
### 16.3.2. Implementing DAOs based on plain Hibernate API
### 16.3.3. Declarative transaction demarcation
### 16.3.4. Programmatic transaction demarcation
### 16.3.5. Transaction management strategies
### 16.3.6. Comparing container-managed and locally defined resources
### 16.3.7. Spurious application server warnings with Hibernate
## 16.4. JPA
### 16.4.1. Three options for JPA setup in a Spring environment
#### LocalEntityManagerFactoryBean
#### Obtaining an EntityManagerFactory from JNDI
#### LocalContainerEntityManagerFactoryBean
#### Dealing with multiple persistence units
### 16.4.2. Implementing DAOs based on JPA: EntityManagerFactory and EntityManager
### 16.4.3. Spring-driven JPA transactions
### 16.4.4. JpaDialect and JpaVendorAdapter
### 16.4.5. Setting up JPA with JTA transaction management
# 17. Marshalling XML using O/X Mappers
----------
## 17.1. Introduction
### 17.1.1. Ease of configuration
### 17.1.2. Consistent Interfaces
### 17.1.3. Consistent Exception Hierarchy
## 17.2. Marshaller and Unmarshaller
### 17.2.1. Marshaller
### 17.2.2. Unmarshaller
### 17.2.3. XmlMappingException
### 17.3. Using Marshaller and Unmarshaller
### 17.4. XML Schema-based Configuration
## 17.5. JAXB
### 17.5.1. Jaxb2Marshaller
#### XML Schema-based Configuration
## 17.6. Castor
### 17.6.1. CastorMarshaller
### 17.6.2. Mapping
### XML Schema-based Configuration
## 17.7. JiBX
### 17.7.1. JibxMarshaller
#### XML Schema-based Configuration
## 17.8. XStream
### 17.8.1. XStreamMarshaller
V. The Web
--------------
# 18. Web MVC framework
--------------
## 18.1. Introduction to Spring Web MVC framework
### 18.1.1. Features of Spring Web MVC
### 18.1.2. Pluggability of other MVC implementations
## 18.2. The DispatcherServlet
### 18.2.1. Special Bean Types In the WebApplicationContext
### 18.2.2. Default DispatcherServlet Configuration
### 18.2.3. DispatcherServlet Processing Sequence
## 18.3. Implementing Controllers
### 18.3.1. Defining a controller with @Controller
### 18.3.2. Mapping Requests With @RequestMapping
#### Composed @RequestMapping Variants
#### @Controller and AOP Proxying
#### New Support Classes for @RequestMapping methods in Spring MVC 3.1
#### URI Template Patterns
#### URI Template Patterns with Regular Expressions
#### Path Patterns
#### Path Pattern Comparison
#### Path Patterns with Placeholders
#### Suffix Pattern Matching
#### Suffix Pattern Matching and RFD
#### Matrix Variables
#### Consumable Media Types
#### Producible Media Types
#### Request Parameters and Header Values
#### HTTP HEAD and HTTP OPTIONS
### 18.3.3. Defining @RequestMapping handler methods
#### Supported method argument types
#### Supported method return types
#### Binding request parameters to method parameters with @RequestParam
#### Mapping the request body with the @RequestBody annotation
#### Mapping the response body with the @ResponseBody annotation
#### Creating REST Controllers with the @RestController annotation
#### Using HttpEntity
#### Using @ModelAttribute on a method
#### Using @ModelAttribute on a method argument
#### Using @SessionAttributes to store model attributes in the HTTP session between requests
#### Using @SessionAttribute to access pre-existing global session attributes
#### Using @RequestAttribute to access request attributes
#### Working with "application/x-www-form-urlencoded" data
#### Mapping cookie values with the @CookieValue annotation
#### Mapping request header attributes with the @RequestHeader annotation
#### Method Parameters And Type Conversion
#### Customizing WebDataBinder initialization
#### Advising controllers with @ControllerAdvice and @RestControllerAdvice
#### Jackson Serialization View Support
#### Jackson JSONP Support
#### 18.3.4. Asynchronous Request Processing
#### Exception Handling for Async Requests
#### Intercepting Async Requests
#### HTTP Streaming
#### HTTP Streaming With Server-Sent Events
#### HTTP Streaming Directly To The OutputStream
#### Configuring Asynchronous Request Processing
### 18.3.5. Testing Controllers
## 18.4. Handler mappings
### 18.4.1. Intercepting requests with a HandlerInterceptor
## 18.5. Resolving views
### 18.5.1. Resolving views with the ViewResolver interface
### 18.5.2. Chaining ViewResolvers
### 18.5.3. Redirecting to Views
#### RedirectView
#### The redirect: prefix
#### The forward: prefix
### 18.5.4. ContentNegotiatingViewResolver
## 18.6. Using flash attributes
## 18.7. Building URIs
### 18.7.1. Building URIs to Controllers and methods
### 18.7.2. Building URIs to Controllers and methods from views
## 18.8. Using locales
### 18.8.1. Obtaining Time Zone Information
### 18.8.2. AcceptHeaderLocaleResolver
### 18.8.3. CookieLocaleResolver
### 18.8.4. SessionLocaleResolver
### 18.8.5. LocaleChangeInterceptor
## 18.9. Using themes
### 18.9.1. Overview of themes
### 18.9.2. Defining themes
### 18.9.3. Theme resolvers
## 18.10. Spring’s multipart (file upload) support
### 18.10.1. Introduction
### 18.10.2. Using a MultipartResolver with Commons FileUpload
### 18.10.3. Using a MultipartResolver with Servlet 3.0
### 18.10.4. Handling a file upload in a form
### 18.10.5. Handling a file upload request from programmatic clients
## 18.11. Handling exceptions
### 18.11.1. HandlerExceptionResolver
### 18.11.2. @ExceptionHandler
### 18.11.3. Handling Standard Spring MVC Exceptions
### 18.11.4. REST Controller Exception Handling
### 18.11.5. Annotating Business Exceptions With @ResponseStatus
### 18.11.6. Customizing the Default Servlet Container Error Page
## 18.12. Web Security
## 18.13. Convention over configuration support
### 18.13.1. The Controller ControllerClassNameHandlerMapping
### 18.13.2. The Model ModelMap (ModelAndView)
### 18.13.3. The View - RequestToViewNameTranslator
## 18.14. HTTP caching support
### 18.14.1. Cache-Control HTTP header
### 18.14.2. HTTP caching support for static resources
### 18.14.3. Support for the Cache-Control, ETag and Last-Modified response headers in Controllers
### 18.14.4. Shallow ETag support
## 18.15. Code-based Servlet container initialization
## 18.16. Configuring Spring MVC
### 18.16.1. Enabling the MVC Java Config or the MVC XML Namespace
### 18.16.2. Customizing the Provided Configuration
### 18.16.3. Conversion and Formatting
### 18.16.4. Validation
### 18.16.5. Interceptors
### 18.16.6. Content Negotiation
### 18.16.7. View Controllers
### 18.16.8. View Resolvers
### 18.16.9. Serving of Resources
### 18.16.10. Falling Back On the "Default" Servlet To Serve Resources
### 18.16.11. Path Matching
### 18.16.12. Message Converters
### 18.16.13. Advanced Customizations with MVC Java Config
### 18.16.14. Advanced Customizations with the MVC Namespace
# 19. View technologies
## 19.1. Introduction
## 19.2. Thymeleaf
## 19.3. Groovy Markup Templates
### 19.3.1. Configuration
### 19.3.2. Example
### 19.4. FreeMarker
### 19.4.1. Dependencies
### 19.4.2. Context configuration
### 19.4.3. Creating templates
### 19.4.4. Advanced FreeMarker configuration
### 19.4.5. Bind support and form handling
#### The bind macros
#### Simple binding
#### Form input generation macros
#### HTML escaping and XHTML compliance
## 19.5. JSP & JSTL
### 19.5.1. View resolvers
### 19.5.2. 'Plain-old' JSPs versus JSTL
### 19.5.3. Additional tags facilitating development
### 19.5.4. Using Spring’s form tag library
#### Configuration
#### The form tag
#### The input tag
#### The checkbox tag
#### The checkboxes tag
#### The radiobutton tag
#### The radiobuttons tag
#### The password tag
#### The select tag
#### The option tag
#### The options tag
#### The textarea tag
#### The hidden tag
#### The errors tag
#### HTTP Method Conversion
#### HTML5 Tags
## 19.6. Script templates
### 19.6.1. Dependencies
### 19.6.2. How to integrate script based templating
### 19.7. XML Marshalling View
### 19.8. Tiles
### 19.8.1. Dependencies
### 19.8.2. How to integrate Tiles
#### UrlBasedViewResolver
#### ResourceBundleViewResolver
#### SimpleSpringPreparerFactory and SpringBeanPreparerFactory
## 19.9. XSLT
### 19.9.1. My First Words
#### Bean definitions
#### Standard MVC controller code
#### Document transformation
## 19.10. Document views (PDF/Excel)
### 19.10.1. Introduction
### 19.10.2. Configuration and setup
#### Document view definitions
#### Controller code
#### Subclassing for Excel views
#### Subclassing for PDF views
## 19.11. Feed Views
## 19.12. JSON Mapping View
## 19.13. XML Mapping View
# 20. CORS Support
-------------
## 20.1. Introduction
## 20.2. Controller method CORS configuration
## 20.3. Global CORS configuration
### 20.3.1. JavaConfig
### 20.3.2. XML namespace
## 20.4. Advanced Customization
## 20.5. Filter based CORS support
# 21. Integrating with other web frameworks
--------------
## 21.1. Introduction
## 21.2. Common configuration
## 21.3. JavaServer Faces 1.2
### 21.3.1. SpringBeanFacesELResolver (JSF 1.2+)
### 21.3.2. FacesContextUtils
## 21.4. Apache Struts 2.x
## 21.5. Tapestry 5.x
## 21.6. Further Resources
# 22. WebSocket Support
---------------
## 22.1. Introduction
### 22.1.1. WebSocket Fallback Options
### 22.1.2. A Messaging Architecture
### 22.1.3. Sub-Protocol Support in WebSocket
### 22.1.4. Should I Use WebSocket?
## 22.2. WebSocket API
### 22.2.1. Create and Configure a WebSocketHandler
### 22.2.2. Customizing the WebSocket Handshake
### 22.2.3. WebSocketHandler Decoration
### 22.2.4. Deployment Considerations
### 22.2.5. Configuring the WebSocket Engine
### 22.2.6. Configuring allowed origins
## 22.3. SockJS Fallback Options
### 22.3.1. Overview of SockJS
### 22.3.2. Enable SockJS
### 22.3.3. HTTP Streaming in IE 8, 9: Ajax/XHR vs IFrame
### 22.3.4. Heartbeat Messages
### 22.3.5. Servlet 3 Async Requests
### 22.3.6. CORS Headers for SockJS
### 22.3.7. SockJS Client
## 22.4. STOMP Over WebSocket Messaging Architecture
### 22.4.1. Overview of STOMP
### 22.4.2. Enable STOMP over WebSocket
### 22.4.3. Flow of Messages
### 22.4.4. Annotation Message Handling
### 22.4.5. Sending Messages
### 22.4.6. Simple Broker
### 22.4.7. Full-Featured Broker
### 22.4.8. Connections To Full-Featured Broker
### 22.4.9. Using Dot as Separator in @MessageMapping Destinations
### 22.4.10. Authentication
### 22.4.11. Token-based Authentication
### 22.4.12. User Destinations
### 22.4.13. Listening To ApplicationContext Events and Intercepting Messages
### 22.4.14. STOMP Client
### 22.4.15. WebSocket Scope
### 22.4.16. Configuration and Performance
### 22.4.17. Runtime Monitoring
### 22.4.18. Testing Annotated Controller Methods
### 23. WebFlux framework
--------------
## 23.1. Introduction
### 23.1.1. What is Reactive Programming?
### 23.1.2. Reactive API and Building Blocks
## 23.2. Spring WebFlux Module
### 23.2.1. Server Side
#### Annotation-based Programming Model
#### Functional Programming Model
### 23.2.2. Client Side
### 23.2.3. Request and Response Body Conversion
### 23.2.4. Reactive WebSocket Support
### 23.2.5. Testing
## 23.3. Getting Started
### 23.3.1. Spring Boot Starter
### 23.3.2. Manual Bootstrapping
### 23.3.3. Examples
