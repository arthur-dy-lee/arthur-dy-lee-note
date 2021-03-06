

## 二、核心组件

![](https://farm8.staticflickr.com/7843/33593433968_99e7819fad_k.jpg)





[url=https://flic.kr/p/Tbx8xo][img]https://farm8.staticflickr.com/7843/33593433968_99e7819fad_k.jpg[/img][/url][url=https://flic.kr/p/Tbx8xo]architecture[/url] by [url=https://www.flickr.com/photos/151075642@N04/]arthur li[/url], 於 Flickr



- etcd保存了整个集群的状态；
- apiserver提供了资源操作的唯一入口，并提供认证、授权、访问控制、API注册和发现等机制；
- controller manager负责维护集群的状态，比如故障检测、自动扩展、滚动更新等；
- scheduler负责资源的调度，按照预定的调度策略将Pod调度到相应的机器上；
- kubelet负责维护容器的生命周期，同时也负责Volume（CVI）和网络（CNI）的管理；
- Container runtime负责镜像管理以及Pod和容器的真正运行（CRI）；
- kube-proxy负责为Service提供cluster内部的服务发现和负载均衡；

除了核心组件，还有一些推荐的Add-ons：

- kube-dns负责为整个集群提供DNS服务
- Ingress Controller为服务提供外网入口
- Heapster提供资源监控
- Dashboard提供GUI
- Federation提供跨可用区的集群
- Fluentd-elasticsearch提供集群日志采集、存储与查询