sudo gedit /etc/rc.local

## 启动zookeeper
cd /usr/local/zk/bin
sudo ./zkServer.sh start ../conf/zoo1.cfg

sudo /usr/local/zk/bin/zkServer.sh start /usr/local/zk/conf/zoo1.cfg
sudo /usr/local/zk/bin/zkServer.sh start /usr/local/zk/conf/zoo2.cfg
sudo /usr/local/zk/bin/zkServer.sh start /usr/local/zk/conf/zoo3.cfg

echo ruok|nc 127.0.0.1 2181 测试是否启动了该Server，若回复imok表示已经启动
echo kill | nc 127.0.0.1 2181 ,关掉server
echo conf | nc 127.0.0.1 2181 ,输出相关服务配置的详细信息。

sudo /usr/local/zk/bin/zkCli.sh -server 192.168.3.13:2181
sudo ./zkCli.sh -server localhost:2181

create /arthur ""
create /arthur/leeapp ""
create /arthur/leeapp/name dy
create /arthur/leeapp/name2 susan


## 启动zkui
cd /usr/local/zkui
nohup java -jar zkui-2.0-SNAPSHOT-jar-with-dependencies.jar &

## 启动kafka前启动zookeeper(kafaka自带)
sudo ./bin/zookeeper-server-start.sh ./config/zookeeper4.properties


## 修改 server4.properties/server4.properties/server4.properties
sudo gedit config/server4.properties
sudo gedit config/server5.properties

## 启动kafka
sudo ./bin/kafka-server-start.sh config/server3.properties &
sudo ./bin/kafka-server-start.sh config/server4.properties &
sudo ./bin/kafka-server-start.sh config/server5.properties &

## 创建topic
sudo ./bin/kafka-topics.sh --create --zookeeper localhost:2184 --replication-factor 1 --partitions 1 --topic kafkatopic2
或者
bin/kafka-topics.sh --create --zookeeper localhost:2184 --replication-factor 1 --partitions 1 --topic test

## 查看自己创建的topic信息
bin/kafka-topics.sh --list --zookeeper localhost:2184

##发送消息
bin/kafka-console-producer.sh --broker-list localhost:9093 --topic test

## 启动consumer消费消息
bin/kafka-console-consumer.sh --zookeeper localhost:2184 --topic test --from-beginning

## 如果已经有了一个集群，运行descirbe topics 命令查看状态
bin/kafka-topics.sh --describe --zookeeper localhost:2184 --topic my-replicated-topic

=======================================================

# java_home
export JAVA_HOME=/usr/lib/jvm/java8

# zookeeper
./usr/local/zk/bin/zkServer.sh start zoo1.cfg
./usr/local/zk/bin/zkServer.sh start zoo2.cfg
./usr/local/zk/bin/zkServer.sh start zoo3.cfg

#kafka
./bin/zookeeper-server-start.sh ./config/zookeeper4.properties
./bin/kafka-server-start.sh config/server3.properties &

exit 0


=======================================================

mysql 启动
service mysql start



=======================================================
常用命令
