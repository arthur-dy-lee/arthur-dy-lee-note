## ssh
[root@localhost zby]# ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa  
Generating public/private rsa key pair.  
Created directory '/root/.ssh'.  
Your identification has been saved in /root/.ssh/id_rsa.  
Your public key has been saved in /root/.ssh/id_rsa.pub.  
The key fingerprint is:  
97:d3:d3:62:e6:4b:7b:9a:c0:8f:b1:5d:01:e7:5c:4e root@localhost.localdomain  
The key's randomart image is:  
+--[ RSA 2048]----+  
|                 |  
|                 |  
|            . . E|  
|           o * + |  
|        S + * = .|  
|         o = o . |  
|          + o .  |  
|           O =.  |  
|          o Bo   |  
+-----------------+  
[root@localhost zby]# cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys  
[root@localhost zby]# chmod 0600 ~/.ssh/authorized_keys

## 增加调试信息有两种方式，

1、在执行命令前加一条如下命令：

export HADOOP_ROOT_LOGGER=DEBUG,console

比如：
```
[wangqi@node001 ~]$ export HADOOP_ROOT_LOGGER=DEBUG,console

[wangqi@node001 ~]$ hdfs dfs -ls /
```
