hadoop学习笔记

yarn是怎么均衡的分配任务的？怎么做到性能好的机器分配更多的任务

hadoop如何配置网络拓扑，以达到把网络看作是一棵树。 P70

apache Oozie 工作流系统，控制流节点组成的DAG（有向无环图）


Hadoop包括以下四个基本模块：
Hadoop基础功能库：支持其他Hadoop模块的通用程序包。 
HDFS：一个分布式文件系统，能够以高吞吐量访问应用的数据。 
YARN：一个作业调度和资源管理框架。 
MapReduce：一个基于YARN的大数据并行处理程序。

除了基本模块，Hadoop相关的其他项目还包括：
Ambari：一个基于Web的工具，用于配置、管理和监控Hadoop集群。支持HDFS、MapReduce、Hive、HCatalog、HBase、ZooKeeper、Oozie、Pig和Sqoop。Ambari还提供显示集群健康状况的仪表盘，如热点图等。Ambari以图形化的方式查看MapReduce、Pig和Hive应用程序的运行情况，因此可以通过对用户友好的方式诊断应用 的性能问题。 
Avro：一个数据序列化系统。 
Cassandra：一个可扩展的无单点故障的NoSQL多主数据库。
Chukwa：一个用于大型分布式系统的数据采集系统。 
HBase：一个可扩展的分布式数据库，支持大表的结构化数据存储。
Hive：一个数据仓库基础架构，提供数据汇总和命令行的即席查询功能。 
Mahout：一个可扩展的机器学习和数据挖掘库。 
Pig：一个用于并行计算的高级数据流语言和执行框架。 
Spark：一个处理Hadoop数据的、高速的、通用的计算引擎。Spark提供了一种简单而富于表达能力的编程模式，支持包括ETL、机器学习、数据流处理、图像计算 等多种应用。 
Tez：一个完整的数据流编程框架，在YARN之上建立，提供强大而灵活的引擎，执行任意的有向无环图（DAG）数据处理任务，既支持批处理又支持交互式的用 户场景。Tez已经被Hive、Pig等Hadoop生态圈的组件所采用，用来替代MapReduce作为底层执行引擎。 
ZooKeeper：一个用于分布式应用的高性能协调服务。




==================

Percolator 模型

TiKV 源码解析系列 - Raft 的优化
https://zhuanlan.zhihu.com/p/25735592


