

## 1. Aspect Joinpoint Advice Pointcut 区别
------

> When you go out to a restaurant, you look at a menu and see several options to choose from. You can order one or more of any of the items on the menu. But until you actually order them, they are just “opportunities to dine”. Once you place the order and the waiter brings it to your table, it’s a meal. 
>
> 当你去餐馆，你看菜单点菜，上面有很多选项，你可以点菜单上的一个或者多个菜。但点之前，他们只是可选择的选项。你一旦下单，服务员上菜到你的桌子上，才是你吃到的真正的晚餐。

### 1.1 Join point和pointcut区别

>**Join points** are the options on the menu and pointcuts are the items you select. A joinpoint is an opportunity within code for you to apply an aspect…just an opportunity. Once you take that opportunity and select one or more joinpoints and apply an aspect to them, you’ve got a **pointcut**. 

Join point 就是菜单上的选项，Pointcut就是你选的菜。Join point 你只是你切面中可以切的那些方法，一旦你选择了要切哪些方法，那就是Pointcut。

也就是说，所有在你程序中可以被调用的方法都是Join point. 使用Pointcut 表达式，那些匹配的方法，才叫Pointcut。所以你根本不用关心Join point。比如你有10个方法，你只想切2个方法，那么那10个方法就是Join point， 2个方法就是Pointcut

### 1.2 Advice  

> `Advice` is the way you take an action on your `Pointcut`. You can use before, after or even around advice to apply any action you defined.  

>Advice – Indicate the action to take either before or after the method execution.

advice就是你作用到pointcut上的方式，你可以使用befor, after 或者around

### 1.3 advisor 

>You know what is an advice, it is nothing more than a piece of code that should be executed before or after or around the method but that should not be written in the body of the method.
>
>You know the general meaning of an advisor, the one who advises. Usually the one who advises both gives the advice and tells how to apply it. In a similar way, the advisor in Spring AOP also gives the advice as well as tells for which method(s) the advice should be applied.
>
>In more simple words, an advisor is an object pointing to a point cut and an advice.

> Advisor – Group ‘Advice’ and ‘Pointcut’ into a single unit, and pass it to a proxy factory object.

advisor就是作用在具体对象上的ponitcut和advice，把ponitcut和advice合起来就是advisor。切到哪个具体方法上面，是使用的befor、after 还是around，就这个就叫advisor.

![](/blogpic/advisor.gif)



## 2.官方解释
------

Let us begin by defining some central AOP concepts. These terms are not Spring-specific. Unfortunately, AOP terminology is not particularly intuitive; however, it would be even more confusing if Spring used its own terminology. 

- *Aspect*: A modularization of a concern that cuts across multiple objects. Transaction management is a good example of a crosscutting concern in J2EE applications. In Spring AOP, aspects are implemented using regular classes (the schema-based approach) or regular classes annotated with the `@Aspect`annotation (`@AspectJ` style).
- *Join point*: A point during the execution of a program, such as the execution of a method or the handling of an exception. In Spring AOP, a join point *always*represents a method execution. Join point information is available in advice bodies by declaring a parameter of type `org.aspectj.lang.JoinPoint`.
- *Advice*: Action taken by an aspect at a particular join point. Different types of advice include "around," "before" and "after" advice. Advice types are discussed below. Many AOP frameworks, including Spring, model an advice as an *interceptor*, maintaining a chain of interceptors "around" the join point.
- *Pointcut*: A predicate that matches join points. Advice is associated with a pointcut expression and runs at any join point matched by the pointcut (for example, the execution of a method with a certain name). The concept of join points as matched by pointcut expressions is central to AOP: Spring uses the AspectJ pointcut language by default.
- *Introduction*: (Also known as an inter-type declaration). Declaring additional methods or fields on behalf of a type. Spring AOP allows you to introduce new interfaces (and a corresponding implementation) to any proxied object. For example, you could use an introduction to make a bean implement an `IsModified`interface, to simplify caching.
- *Target object*: Object being advised by one or more aspects. Also referred to as the *advised* object. Since Spring AOP is implemented using runtime proxies, this object will always be a *proxied* object.
- *AOP proxy*: An object created by the AOP framework in order to implement the aspect contracts (advise method executions and so on). In the Spring Framework, an AOP proxy will be a JDK dynamic proxy or a CGLIB proxy. *Proxy creation is transparent to users of the schema-based and @AspectJ styles of aspect declaration introduced in Spring 2.0.*
- *Weaving*: Linking aspects with other application types or objects to create an advised object. This can be done at compile time (using the AspectJ compiler, for example), load time, or at runtime. Spring AOP, like other pure Java AOP frameworks, performs weaving at runtime.

Types of advice:

- *Before advice*: Advice that executes before a join point, but which does not have the ability to prevent execution flow proceeding to the join point (unless it throws an exception).
- *After returning advice*: Advice to be executed after a join point completes normally: for example, if a method returns without throwing an exception.
- *After throwing advice*: Advice to be executed if a method exits by throwing an exception.
- *After (finally) advice*: Advice to be executed regardless of the means by which a join point exits (normal or exceptional return).
- *Around advice*: Advice that surrounds a join point such as a method invocation. This is the most powerful kind of advice. Around advice can perform custom behavior before and after the method invocation. It is also responsible for choosing whether to proceed to the join point or to shortcut the advised method execution by returning its own return value or throwing an exception.



## 参考：

[https://stackoverflow.com/questions/15447397/spring-aop-whats-the-difference-between-joinpoint-and-pointcut](https://stackoverflow.com/questions/15447397/spring-aop-whats-the-difference-between-joinpoint-and-pointcut)

[https://rakeshnarayan.wordpress.com/2012/06/02/what-is-joint-point-and-point-cut/](https://rakeshnarayan.wordpress.com/2012/06/02/what-is-joint-point-and-point-cut/)

[https://coderanch.com/t/485525/frameworks/Difference-Joint-Point-Point-Cut](https://coderanch.com/t/485525/frameworks/Difference-Joint-Point-Point-Cut)



[pointcut-and-advisor-in-spring-aop](https://java-demos.blogspot.com/2014/04/pointcut-and-advisor-in-spring-aop.html)

[https://www.mkyong.com/spring/spring-aop-example-pointcut-advisor/](https://www.mkyong.com/spring/spring-aop-example-pointcut-advisor/)



[https://docs.spring.io/spring/docs/2.0.x/reference/aop.html](https://docs.spring.io/spring/docs/2.0.x/reference/aop.html)