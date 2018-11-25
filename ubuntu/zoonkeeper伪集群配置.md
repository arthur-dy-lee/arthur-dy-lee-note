
## 1/ 下载[zookeeper-3.4.10.tar.gz](http://download.csdn.net/detail/paincupid/9904473)

## 2/ 放置目录
D:\ProgramData\zookeeper-3.4.10\zk1

## 3/ 配置zoo.cfg
在文件夹 D:\ProgramData\zookeeper-3.4.10\zk1\conf 中加入文件

### zoo1.cfg
```xml
tickTime=2000
dataDir=D:\\ProgramData\\zookeeper-3.4.10\\zk1\\dataTmp\\zookeeper\\1
clientPort=2181
initLimit=10
syncLimit=5
server.1=127.0.0.1:2888:3888
server.2=127.0.0.1:2889:3889
server.3=127.0.0.1:2890:3890
```
### zoo2.cfg
```xml
tickTime=2000
dataDir=D:\\ProgramData\\zookeeper-3.4.10\\zk1\\dataTmp\\zookeeper\\2
clientPort=2182
initLimit=10
syncLimit=5
server.1=127.0.0.1:2888:3888
server.2=127.0.0.1:2889:3889
server.3=127.0.0.1:2890:3890
```
### zoo3.cfg
```xml
tickTime=2000
dataDir=D:\\ProgramData\\zookeeper-3.4.10\\zk1\\dataTmp\\zookeeper\\3
clientPort=2183
initLimit=10
syncLimit=5
server.1=127.0.0.1:2888:3888
server.2=127.0.0.1:2889:3889
server.3=127.0.0.1:2890:3890
```
### 说明
#### a.在集群模式下，集群中每台机器都需要感知到整个集群是由哪几台机器组成的，在配置文件中，可以按照这样的格式进行配置，每一行都代表一个机器配置：server.id=host:port:port，

其中，id被称为 Server ID，用来标识该机器在集群中的机器序列号。同时，在每台Zookeeper机器上，我们都需要在数据目录（即dataDir参数指定的那个目录）下创建一个myid文件，该文件只有一行内容，并且是一个数字，即对应于每台机器的Server ID 数字。

#### b.在Zk的设计中，集群中所有机器上的zoo.cfg文件的内容都应该是一致的。因此最好使用svn或是git把此文件管理起来，确保每个机器都能共享到一份相同的配置。

#### c.上面也提到了，myid文件中只有一个数字，即一个Server ID。例如，server.1的myid文件内容就是"1"。注意，清确保每个服务器的myid文件中的数字不同，并且和自己所在机器的zoo.cfg中server.id=houst:port:port的id一致。另外，id的范围是1~255。

#### d.参数的意义：

> tickTime：默认值为3000，单位是毫秒（ms），可以不配置。参数tickTime用于配置Zookeeper中最小时间单元的长度，很多运行时的时间间隔都是使用tickTime的倍数来表示的。例如，Zk中会话的最小超时时间默认是2*tickTime。

> dataDir：该参数无默认值，必须配置。参数dataDir用于配置Zookeeper服务器存储快照文件的目录。

> clientPort：参数clientPort用于配置当前服务器对外的服务端口，客户端会通过该端口和Zk服务器创建连接，一般设置为2181。

> initLimit：该参数默认值：10，表示是参数tickTime值的10倍，必须配置，且为正整数。该参数用于配置Leader服务器等待Follower启动，并完成数据同步的时间。Follower服务器在启动过程中，会与Leader建立连接并完成对数据的同步，从而确定自己对外提高服务的起始状态。leader服务器允许Follower在initLimit时间内完成这个工作。

> syncLimit：该参数默认值：5，表示是参数tickTime值的5倍，必须配置，且为正整数。该参数用于配置Leader服务器和Follower之间进行心跳检测的最大延时时间。在Zk集群运行的过程中，Leader服务器会与所有的Follower进行心跳检测来确定该服务器是否存活。如果Leader服务器在syncLimit时间内无法获取到Follower的心跳检测响应，那么Leader就会认为该Follower已经脱离了和自己的同步。

> server.id：该参数无默认值，在单机模式下可以不配置。该参数用于配置组成Zk集群的机器列表，其中id即为Server ID，与每台服务器myid文件中的数字相对应。同时，在该参数中，会配置两个端口：第一个端口用于指定Follower服务器与Leader进行运行时通信和数据同步时所使用的端口，第二个端口测专门用于进行Leader选举过程中的投票通信。

## 4/创建myid文件
在dataDir所配置的目录下，创建一个名为myid的文件，在该文件的第一行写上一个数字，和zoo.cfg中当前机器的编号对应上。即：

在D:\ProgramData\zookeeper-3.4.10\zk1\dataTmp\zookeeper\1 文件夹下创建值为"1"的myid文件。
在D:\ProgramData\zookeeper-3.4.10\zk1\dataTmp\zookeeper\2 文件夹下创建值为"2"的myid文件。
在D:\ProgramData\zookeeper-3.4.10\zk1\dataTmp\zookeeper\3 文件夹下创建值为"3"的myid文件。

## 5/创建zkServer
在D:\ProgramData\zookeeper-3.4.10\zk1\bin下创建zkServer-1.cmd/zkServer-2.cmd/zkServer-3.cmd

zkServer-1.cmd
```xml
@echo off
setlocal
call "%~dp0zkEnv.cmd"

set ZOOMAIN=org.apache.zookeeper.server.quorum.QuorumPeerMain
set ZOOCFG=..\conf\zoo1.cfg
echo on
call %JAVA% "-Dzookeeper.log.dir=%ZOO_LOG_DIR%" "-Dzookeeper.root.logger=%ZOO_LOG4J_PROP%" -cp "%CLASSPATH%" %ZOOMAIN% "%ZOOCFG%" %*

endlocal
```

zkServer-2.cmd
```xml
@echo off
setlocal
call "%~dp0zkEnv.cmd"

set ZOOMAIN=org.apache.zookeeper.server.quorum.QuorumPeerMain
set ZOOCFG=..\conf\zoo2.cfg
echo on
call %JAVA% "-Dzookeeper.log.dir=%ZOO_LOG_DIR%" "-Dzookeeper.root.logger=%ZOO_LOG4J_PROP%" -cp "%CLASSPATH%" %ZOOMAIN% "%ZOOCFG%" %*

endlocal
```

zkServer-3.cmd
```xml
@echo off
setlocal
call "%~dp0zkEnv.cmd"

set ZOOMAIN=org.apache.zookeeper.server.quorum.QuorumPeerMain
set ZOOCFG=..\conf\zoo3.cfg
echo on
call %JAVA% "-Dzookeeper.log.dir=%ZOO_LOG_DIR%" "-Dzookeeper.root.logger=%ZOO_LOG4J_PROP%" -cp "%CLASSPATH%" %ZOOMAIN% "%ZOOCFG%" %*

endlocal
```

## 6/启动 zkServer-1.cmd/zkServer-2.cmd/zkServer-3.cmd

刚启动第一个Zk时会不断报错，这是正常的，因为集群中其它的Zk还未起来，Zk集群之间的心跳检测未检测到其它Zk，等集群中所有的Zk都启动后，就不会报错了。

## 7/启动客户端 zkCli.cmd
cmd命令进入：D:\ProgramData\zookeeper-3.4.10\zk1\bin
```xml
可以通过Zk提供的简易客户端来进行验证，双击下zookeeper-*/bin/zkCli.cmd来启动Zk简易客户端，或者通过命令zkCli.cmd -server 127.0.0.1:2181来启动，然后通过使用ls命名（列出Zk指定节点下的所有子节点）来验证Zk已经启动完成
```

参考：
[http://blog.csdn.net/a906998248/article/details/50815031](http://blog.csdn.net/a906998248/article/details/50815031)
[http://blog.csdn.net/morning99/article/details/40426133](http://blog.csdn.net/morning99/article/details/40426133)
