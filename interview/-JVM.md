

#### classLoader作用，怎么样把一个类装载起来，装载在jvm的哪一部分？

classLoader分类
bootstrap,加载 JAVA_HOME\lib 目录中。
ExtClassLoader，加载JAVA_HOME\jre\lib\ext 目录中
appClassLoader（也叫System ClassLoader),加载用户路径（classpath）上的类库。

lvpri
loading加载。将二进制字节流转化为方法区的运行时数据结构，读入内存，并为之创建一个java.lang.Class对象。（二运数入内存，创class）
validate验证。确保 Class 文件的字节流中包含的信息是否符合当前虚拟机的要求，并且不会危害虚拟机自身的安全。
prepare准备。为类变量分配内存并设置类变量的初始值，静态常量此时为0
resolve解析。将常量池中的符号引用替换为直接引用的过程。
initialization初始化。声明类变量时指定初始值和使用静态初始化块为类变量指定初始值



#### 如果想往java类中分配1G的内存空间，需要怎么样？注意哪些问题





------------



#### JVM带用命令
