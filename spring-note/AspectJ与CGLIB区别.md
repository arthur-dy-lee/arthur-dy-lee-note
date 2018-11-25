

# AspectJ

AspectJ定义了AOP语法，所以它有一个专门的编译器用来生成遵守Java字节编码规范的Class文件；即静态代理

静态代理是指使用 AOP 框架提供的命令进行编译，从而在编译阶段就可生成 AOP 代理类，因此也称为编译时增强；

# CGLIB

而动态代理则在运行时借助于 JDK 动态代理、CGLIB 等在内存中“临时”生成 AOP 动态代理类，因此也被称为运行时增强。

Spring aop与Aspectj的关系，前者动态代理最终是用的JDK api或者CGLIB来实现的；只是spring支持了Aspect的注解标签，没有依赖原生的aspect编译器；