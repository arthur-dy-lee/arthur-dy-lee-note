# 《kubernetes权限指南2》笔记



**Master**

Master节点上运行着以下一组关键进程。
Kubernetes API Server（kube-apiserver） ， 提供了HTTP Rest接口的关键服务进程， 是Kubernetes里所有资源的
增、 删、 改、 查等操作的唯一入口， 也是集群控制的入口进程。
Kubernetes Controller Manager（kube-controller-manager） ， Kubernetes里所有资源对象的自动化控制中心， 可以
理解为资源对象的“大总管”。
Kubernetes Scheduler（kube-scheduler） ， 负责资源调度（Pod调度） 的进程， 相当于公交公司的“调度室”。
其实Master节点上往往还启动了一个etcd Server进程， 因为Kubernetes里的所有资源对象的数据全部是保存在
etcd中的。 

**Node**

Node节点才是Kubernetes集群中的工作负载节点， 每个Node
都会被Master分配一些工作负载（Docker容器） ， 当某个Node宕机时， 其上的工作负载会被Master自动转移到其他
节点上去。
每个Node节点上都运行着以下一组关键进程。
kubelet： 负责Pod对应的容器的创建、 启停等任务， 同时与Master节点密切协作， 实现集群管理的基本功能。
kube-proxy： 实现Kubernetes Service的通信与负载均衡机制的重要组件。
Docker Engine（docker） ： Docker引擎， 负责本机的容器创建和管理工作。 

一旦Node被纳入集
群管理范围， kubelet进程就会定时向Master节点汇报自身的情报， 例如操作系统、 Docker版本、 机器的CPU和内存
情况， 以及之前有哪些Pod在运行等， 这样Master可以获知每个Node的资源使用情况， 并实现高效均衡的资源调度
策略。 而某个Node超过指定时间不上报信息时， 会被Master判定为“失联”， Node的状态被标记为不可用（Not
Ready） ， 随后Master会触发“工作负载大转移”的自动流程。 



