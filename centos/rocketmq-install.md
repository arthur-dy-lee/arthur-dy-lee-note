# centos7安装rocketMQ4.2 



## 一、下载最新的rocketmq源码文件

> unzip rocketmq-all-4.2.0-source-release.zip

> cd rocketmq-all-4.2.0/

> mvn -Prelease-all -DskipTests clean install -U

> cd distribution/target/apache-rocketmq



## 二、启动Name Server

> cd /usr/local/rocketmq-all-4.2.0/distribution/target/apache-rocketmq/



> nohup sh bin/mqnamesrv &

> tail -f ~/logs/rocketmqlogs/namesrv.log

```
The Name Server boot success......
```

##  三、启动Broker

> nohup sh bin/mqbroker -n localhost:9876 &

> tail -f ~/logs/rocketmqlogs/broker.log

```
The broker[%s, 172.30.30.233:10911] boot success...
```



## 四、关闭服务

>  sh mqshutdown namesrv

> sh mqshutdown broker

或者通过jps查看进程，使用kill -9 pid结束进程(有时会看不见进程，但是服务仍在运行，建议用mqshutdown关闭服务)。 



## 五、TIP. 跳坑部分

### 1、无法启动NameServer 

```
[wangyanrui@bogon apache-rocketmq]$ sh bin/mqnamesrv
Java HotSpot(TM) 64-Bit Server VM warning: INFO: os::commit_memory(0x00000005c0000000, 4294967296, 0) failed; error='Cannot allocate memory' (errno=12)
#
# There is insufficient memory for the Java Runtime Environment to continue.
# Native memory allocation (mmap) failed to map 4294967296 bytes for committing reserved memory.
# An error report file with more information is saved as:
# /home/wangyanrui/rocketmq-all-4.2.0/distribution/target/apache-rocketmq/hs_err_pid2081.log
```

提示无法分配内存（前面说了，CentOS目前的内存为4G，也开了一些其他的服务）

解决办法：

修改bin目录下的runserver.sh，根据本机的内存，修改如下部分即可

 ```
JAVA_OPT="${JAVA_OPT} -server -Xms4g -Xmx4g -Xmn2g -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=320m"
 ```

### 2、无法启动Broker 

提示部分如上

解决办法：

修改bin目录下的runbroker.sh，根据本机内存，修改如下部分即可

 ```
 JAVA_OPT="${JAVA_OPT} -server -Xms8g -Xmx8g -Xmn4g"
 ```


备注：

> nohup 是永久执行  

> & 是指在后台运行 

那么，我们可以巧妙的吧他们结合起来用就是 nohup COMMAND & 这样就能使命令永久的在后台执行 

举个例子nohup tail -f nohup.out 然后退出登录，再连接，用ps -ef 你会还能看到在运行 

nohup执行后，会产生日子文件，把命令的执行中的消息保存到这个文件中，一般在当前目录下，如果当前目录不可写，那么自动保存到执行这个命令的用户的home目录下，例如root的话就保存在/root/下  



## 六、RocketMQ可视化管理平台rocket-console

**下载地址**

<https://github.com/apache/incubator-rocketmq-externals/tree/master/rocketmq-console>

**源码编译**

mvn clean package -Dmaven.test.skip=true

**运行**

java -jar rocketmq-console-ng-1.0.0.jar --server.port=12581 --rocketmq.config.namesrvAddr=192.168.3.22:9876;192.168.1.107:9876



java -jar rocketmq-console-ng-1.0.0.jar --server.port=12581 --rocketmq.config.namesrvAddr=192.168.43.230:9876



参数说明：

--server.port为运行的这个web应用的端口，如果不设置的话默认为8080；

--rocketmq.config.namesrvAddr为RocketMQ命名服务地址，如果不设置的话默认为“”。



访问url

运行jar的机器的地址:

http://localhost:12581/#/message



转自：https://www.cnblogs.com/wangyanrui/p/8688841.html