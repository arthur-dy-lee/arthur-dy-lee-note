



## @ConfigurationProperties与@EnableConfigurationProperties

当某些属性的值需要配置的时候，我们一般会在`application.properties`文件中新建配置项，然后在bean中使用`@Value`注解来获取配置的值，比如下面配置数据源的代码。

```java
// jdbc config
jdbc.mysql.url=jdbc:mysql://localhost:3306/sampledb
jdbc.mysql.username=root
jdbc.mysql.password=123456
......

// 配置数据源
@Configuration
public class HikariDataSourceConfiguration {

    @Value("jdbc.mysql.url")
    public String url;
    @Value("jdbc.mysql.username")
    public String user;
    @Value("jdbc.mysql.password")
    public String password;
    
    @Bean
    public HikariDataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(password);
        // 省略部分代码
        return new HikariDataSource(hikariConfig);
    }
}
```

使用`@Value`注解注入的属性通常都比较简单，如果同一个配置在多个地方使用，也存在不方便维护的问题（考虑下，如果有几十个地方在使用某个配置，而现在你想改下名字，你改怎么做？）。对于更为复杂的配置，Spring Boot提供了更优雅的实现方式，那就是`@ConfigurationProperties`注解。我们可以通过下面的方式来改写上面的代码：

```java
@Component
//  还可以通过@PropertySource("classpath:jdbc.properties")来指定配置文件
@ConfigurationProperties("jdbc.mysql")
// 前缀=jdbc.mysql，会在配置文件中寻找jdbc.mysql.*的配置项
pulic class JdbcConfig {
    public String url;
    public String username;
    public String password;
}

@Configuration
public class HikariDataSourceConfiguration {

    @AutoWired
    public JdbcConfig config;
    
    @Bean
    public HikariDataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.url);
        hikariConfig.setUsername(config.username);
        hikariConfig.setPassword(config.password);
        // 省略部分代码
        return new HikariDataSource(hikariConfig);
    }
}
```

`@ConfigurationProperties`对于更为复杂的配置，处理起来也是得心应手，比如有如下配置文件：

```properties
#App
app.menus[0].title=Home
app.menus[0].name=Home
app.menus[0].path=/
app.menus[1].title=Login
app.menus[1].name=Login
app.menus[1].path=/login

app.compiler.timeout=5
app.compiler.output-folder=/temp/

app.error=/error/
```

可以定义如下配置类来接收这些属性

```java
@Component
@ConfigurationProperties("app")
public class AppProperties {

    public String error;
    public List<Menu> menus = new ArrayList<>();
    public Compiler compiler = new Compiler();

    public static class Menu {
        public String name;
        public String path;
        public String title;
    }

    public static class Compiler {
        public String timeout;
        public String outputFolder;
    }
}
```

`@EnableConfigurationProperties`注解表示对`@ConfigurationProperties`的内嵌支持，默认会将对应Properties Class作为bean注入的IOC容器中，即在相应的Properties类上不用加`@Component`注解。

 

 

 

 

 