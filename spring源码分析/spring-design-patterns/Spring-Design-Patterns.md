# Spring Design Patterns

-----


1. **MVC** – The advantage with Spring MVC is that your controllers are POJOs as opposed to being servlets. This makes for easier testing of controllers.

Model View Controller – The advantage with Spring MVC is that your controllers are POJOs as opposed to being servlets. This makes for easier testing of controllers. One thing to note is that the controller is only required to return a logical view name, and the view selection is left to a separate ViewResolver. This makes it easier to reuse controllers for different view technologies.

2. **Front controller** – Spring provides “DispatcherServlet” to ensure an incoming request gets dispatched to your controllers.
Front Controller – Spring provides DispatcherServlet to ensure an incoming request gets dispatched to your controllers.

3. **View Helper** – Spring has a number of custom JSP tags, and velocity macros, to assist in separating code from presentation in views.

4. **Singleton** – Beans defined in spring config files are singletons by default.
Singleton – by default, beans defined in spring config file (xml) are only created once. No matter how many calls were made using getBean() method, it will always have only one bean. This is because, by default all beans in spring are singletons.
This can be overridden by using Prototype bean scope.Then spring will create a new bean object for every request.

5. **Prototype** – Instance type can be prototype.

6. **Factory** – Used for loading beans through BeanFactory and Application context.

Factory – Spring uses factory pattern to create objects of beans using Application Context reference.
// Spring uses factory pattern to create instances of the objects
BeanFactory factory = new XmlBeanFactory(new FileSystemResource(“spring.xml”));
Triangle triangle = (Triangle) factory.getBean(“triangle”);
triangle.draw();

7. **Builder** – Spring provides programmatic means of constructing BeanDefinitions using the builder pattern through Class “BeanDefinitionBuilder”.

8. **Template** – Used extensively to deal with boilerplate repeated code (such as closing connections cleanly, etc..). For example JdbcTemplate, JmsTemplate, JpaTemplate.

9. **Proxy** – Used in AOP & Remoting.

10. **DI/IOC** – It is central to the whole BeanFactory/ApplicationContext stuff.
Dependency injection/ or IoC (inversion of control) – Is the main principle behind decoupling process that Spring does

11. **View Helper** – Spring has a number of custom JSP tags, and velocity macros, to assist in separating code from presentation in views





## reference:

[https://rakeshnarayan.wordpress.com/2012/06/02/spring-design-patterns/](https://rakeshnarayan.wordpress.com/2012/06/02/spring-design-patterns/)