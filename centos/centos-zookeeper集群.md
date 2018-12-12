



## 下载

地址： [http://mirror.bit.edu.cn/apache/zookeeper/](http://mirror.bit.edu.cn/apache/zookeeper/)



## 安装

### 解压目录

/usr/local/zookeeper-3.4.12

### 创建连接文件

> ln -sf zookeeper-3.4.12 zookeeper 

### 新建文件夹

mkdir data

mkdir dataLog

### 新建myid文件

进入 data目录

> touch myid

集群的话，每台机器的myid值是不同的，我这里分别取1,2,3

将192.168.3.6的myid值设为1：

>echo "1">>myid 

### 新建 **zoo.cfg** 

> cd /usr/local/zookeeper-3.4.12

> cp zoo_sample.cfg zoo.cfg 

### 修改zoo.cfg文件

把原文件中的dataDir那一行注释掉。

>dataDir=/usr/local/zookeeper-3.4.12/data
>
>dataLogDir=/usr/local/zookeeper-3.4.12/dataLog
>
>server.1=192.168.3.6:2888:3888
>
>server.2=192.168.3.7:2888:3888
>
>server.3=192.168.3.8:2888:3888



### 关闭防火墙或开启相应的端口

省略

> firewall-cmd --zone=public --add-port=2888/tcp --permanent 
>
> firewall-cmd --zone=public --add-port=3888/tcp --permanent 
>
> firewall-cmd --zone=public --add-port=2181/tcp --permanent 



### 修改 /usr/local/zookeeper-3.4.12/bin/zkServer.sh文件

如果启动不报错，则这一步可以略过。如果报`nohup: failed to run command `java’: No such file or directory`，那么需要在`zkServer.sh`文件头加入

>export JAVA_HOME=/usr/local/jdk8
>export PATH=${JAVA_HOME}/bin:$PATH



## 启动关闭等相关命令

### 启动

> sudo /usr/local/zookeeper/bin/zkServer.sh start

### 关闭

> sudo /usr/local/zookeeper/bin/zkServer.sh stop

查看状态

>/usr/local/zookeeper/bin/zkServer.sh status



## 开机自启动

1、进入到/etc/rc.d/init.d目录下，命令是 

> **cd    /etc/rc.d/init.d** 

2、新建一个名为zookeeper的文件，命令是 

> **touch    zookeeper** 

3、添加文件内容

> vi zookeeper

>#!/bin/bash
>#chkconfig:2345 10 90
>#description:service zookeeper
>export     JAVA_HOME=/usr/local/jdk8
>export     ZOO_LOG_DIR=/usr/local/zookeeper-3.4.12/dataLog
>ZOOKEEPER_HOME=/usr/local/zookeeper-3.4.12/
>case  "$1"   in
>     start)  su  root  ${ZOOKEEPER_HOME}/bin/zkServer.sh  start;;
>     start-foreground)  su  root ${ZOOKEEPER_HOME}/bin/zkServer.sh   start-foreground;;
>     stop)  su  root  ${ZOOKEEPER_HOME}/bin/zkServer.sh  stop;;
>     status)  su root  ${ZOOKEEPER_HOME}/bin/zkServer.sh    status;;
>     restart)  su root   ${ZOOKEEPER_HOME}/bin/zkServer.sh   restart;;
>     upgrade)su root  ${ZOOKEEPER_HOME}/bin/zkServer.sh  upgrade;;
>     print-cmd)su root  ${ZOOKEEPER_HOME}/bin/zkServer.sh  print-cmd;;
>     *)  echo "requirestart|start-foreground|stop|status|restart|print-cmd";;
>esac

4、为新建的/etc/rc.d/init.d/zookeeper文件添加可执行权限，命令是 

>**chmod  +x  /etc/rc.d/init.d/zookeeper** 

5、把zookeeper这个脚本添加到开机启动项里面，命令是 

>**chkconfig  --add   zookeeper** 

6、如果想看看是否添加成功，命令是 

>**chkconfig  --list** 

最后重启机器

> reboot





