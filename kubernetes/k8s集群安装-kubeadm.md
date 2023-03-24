# k8s集群安装



系统：centos7.6， 3节点信息如下：

| IP           | hosteName |
| ------------ | --------- |
| 192.168.3.31 | master    |
| 192.168.3.32 | node2     |
| 192.168.3.33 | node3     |

注意：官方建议每台机器至少双核2G内存，同时需确保MAC和product_uuid唯一



## 一、准备工作

### 1.1 配置selinux和firewalld

```bash
setenforce 0
sed -i "s/SELINUX=enforcing/SELINUX=disabled/g" /etc/selinux/config

systemctl stop firewalld && systemctl disable firewalld
iptables -F && iptables -X && iptables -F -t nat && iptables -X -t nat && iptables -P FORWARD ACCEPT
```



### 1.2 关闭swap分区

```bash
swapoff -a
sed -i '/ swap / s/^\(.*\)$/#\1/g' /etc/fstab
sysctl -p
```



### 1.3 设置内核参数

```bash
cat << EOF | tee /etc/sysctl.d/k8s.conf
net.bridge.bridge-nf-call-iptables=1
net.bridge.bridge-nf-call-ip6tables=1
EOF

sysctl -p /etc/sysctl.d/k8s.conf
```



### 1.4 配置yum源

```bash
# base repo
cd /etc/yum.repos.d
mv CentOS-Base.repo CentOS-Base.repo.bak
curl -o CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo
sed -i 's/gpgcheck=1/gpgcheck=0/g' /etc/yum.repos.d/CentOS-Base.repo

# docker repo
curl -o docker-ce.repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo

# k8s repo
cat <<EOF > /etc/yum.repos.d/kubernetes.repo
[kubernetes]
name=Kubernetes
baseurl=http://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64
enabled=1
gpgcheck=0
repo_gpgcheck=0
gpgkey=http://mirrors.aliyun.com/kubernetes/yum/doc/yum-key.gpg
        http://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg
EOF

# update cache
yum clean all  
yum makecache  
yum repolist
```

### 1.5 安装依赖包

```bash
yum install -y epel-release conntrack ipvsadm ipset jq sysstat curl iptables libseccomp

yum install -y yum-utils device-mapper-persistent-data lvm2
```



### 1.6 启用测试库

```bash
yum-config-manager --enable docker-ce-edge
yum-config-manager --enable docker-ce-test
```

### 1.7 安装docker

目前k8s-1.14 使用的是 dock-18.06-ce

查看可安装的docker-ce列表

```bash
yum list docker-ce --showduplicates
```

安装最新版docker-ce

```bash
yum install docker-ce
```

要安装指定版本docker

```bash
yum install docker-ce-18.06.1.ce-3.el7
```

启动并设置为开机自启动

### 1.8 安装kubeadm、kubelet和kubectl

如果安装最新版的，把版本号去掉即可。

```bash
yum -y install  kubelet-1.14.0 kubeadm-1.14.0  kubectl-1.14.0 kubernetes-cni-0.7.5
```

检查所有服务版本

```bash
[root@master ~]# rpm -qa docker-ce  kubelet kubeadm kubectl  kubernetes-cni
docker-ce-18.06.0.ce-3.el7.x86_64
kubelet-1.14.0-0.x86_64
kubectl-1.14.0-0.x86_64
kubeadm-1.14.0-0.x86_64
kubernetes-cni-0.7.5-0.x86_64
```

### 1.9 启动docker和kubelet并设置为开机自启动

```bash
systemctl enable docker
systemctl enable kubelet.service
systemctl start docker
systemctl start kubelet
```

##　二、下载相关镜像

### 2.1 获取镜像列表

```bash
kubeadm config images list
```

```bash
[root@master ~]# kubeadm config images list
I0623 21:31:50.520669   30468 version.go:96] could not fetch a Kubernetes version from the internet: unable to get URL "https://dl.k8s.io/release/stable-1.txt": Get https://dl.k8s.io/release/stable-1.txt: net/http: request canceled while waiting for connection (Client.Timeout exceeded while awaiting headers)
I0623 21:31:50.520780   30468 version.go:97] falling back to the local client version: v1.14.0
k8s.gcr.io/kube-apiserver:v1.14.0
k8s.gcr.io/kube-controller-manager:v1.14.0
k8s.gcr.io/kube-scheduler:v1.14.0
k8s.gcr.io/kube-proxy:v1.14.0
k8s.gcr.io/pause:3.1
k8s.gcr.io/etcd:3.3.10
k8s.gcr.io/coredns:1.3.1
```

### 2.2 生成默认kubeadm.conf文件

```bash
kubeadm config print init-defaults > kubeadm.conf
```

### 2.3 修改`kubeadm.conf`文件的镜像地址

默认为google的镜像仓库地址k8s.gcr.io，国内无法访问，需要把地址修改为国内的地址，这里使用阿里云的镜像仓库地址。
编辑`kubeadm.conf`，将`imageRepository`修改为`registry.aliyuncs.com/google_containers` 。并确认Kubernetes版本是v1.14.0，和**2.1**中的镜像列表的版本保持一致

```bash
vim kubeadm.conf 
```

将`imageRepository: k8s.gcr.io` --> 

```bash
imageRepository:registry.aliyuncs.com/google_containers
```

### 2.4 下载镜像：

```bash
kubeadm config images pull --config kubeadm.conf
```

修改tag

```bash
docker tag registry.aliyuncs.com/google_containers/kube-apiserver:v1.14.0    k8s.gcr.io/kube-apiserver:v1.14.0
docker tag registry.aliyuncs.com/google_containers/kube-controller-manager:v1.14.0    k8s.gcr.io/kube-controller-manager:v1.14.0
docker tag registry.aliyuncs.com/google_containers/kube-scheduler:v1.14.0   k8s.gcr.io/kube-scheduler:v1.14.0
docker tag registry.aliyuncs.com/google_containers/kube-proxy:v1.14.0   k8s.gcr.io/kube-proxy:v1.14.0
docker tag registry.aliyuncs.com/google_containers/pause:3.1    k8s.gcr.io/pause:3.1
docker tag registry.aliyuncs.com/google_containers/etcd:3.3.10    k8s.gcr.io/etcd:3.3.10
docker tag registry.aliyuncs.com/google_containers/coredns:1.3.1    k8s.gcr.io/coredns:1.3.1
```

再删除阿里云镜像

```bash
docker rmi registry.aliyuncs.com/google_containers/kube-apiserver:v1.14.0
docker rmi registry.aliyuncs.com/google_containers/kube-controller-manager:v1.14.0
docker rmi registry.aliyuncs.com/google_containers/kube-scheduler:v1.14.0
docker rmi registry.aliyuncs.com/google_containers/kube-proxy:v1.14.0
docker rmi registry.aliyuncs.com/google_containers/pause:3.1
docker rmi registry.aliyuncs.com/google_containers/etcd:3.3.10
docker rmi registry.aliyuncs.com/google_containers/coredns:1.3.1
```

或者使用脚本解决：

```bash
cat image.sh 
#!/bin/bash
images=(kube-proxy:v1.14.0 kube-scheduler:v1.14.0 kube-controller-manager:v1.14.0 kube-apiserver:v1.14.0 etcd:3.3.10 coredns:1.3.1 pause:3.1 )
for imageName in ${images[@]} ; do
docker pull registry.aliyuncs.com/google_containers/$imageName
docker tag  registry.aliyuncs.com/google_containers/$imageName k8s.gcr.io/$imageName
docker rmi  registry.aliyuncs.com/google_containers/$imageName
done
```

### 2.5 忽略swap错误

kubernetes集群不允许开启swap，所以我们需要忽略这个错误
编辑文件 /etc/sysconfig/kubelet，将文件里的“KUBELET_EXTRA_ARGS=”改成这样：KUBELET_EXTRA_ARGS="--fail-swap-on=false"
修改之后的文件：

```bash
cat /etc/sysconfig/kubelet
KUBELET_EXTRA_ARGS="--fail-swap-on=false"
```



## 三、master节点部署

### 3.1 初始化Kubernetes Master

这里定义先POD的网段为: `10.244.0.0/16`，API Server地址为Master节点的IP地址。命令：

```bash
kubeadm init --kubernetes-version=v1.14.0 --pod-network-cidr=10.244.0.0/16 --apiserver-advertise-address=192.168.3.31
```

执行结果：

```bash
[root@k8smaster ~]# kubeadm init --kubernetes-version=v1.14.0 --pod-network-cidr=10.244.0.0/16 --apiserver-advertise-address=192.168.3.31
[init] Using Kubernetes version: v1.14.0
[preflight] Running pre-flight checks
    [WARNING IsDockerSystemdCheck]: detected "cgroupfs" as the Docker cgroup driver. The recommended driver is "systemd". Please follow the guide at https://kubernetes.io/docs/setup/cri/
[preflight] Pulling images required for setting up a Kubernetes cluster
[preflight] This might take a minute or two, depending on the speed of your internet connection
[preflight] You can also perform this action in beforehand using 'kubeadm config images pull'
[kubelet-start] Writing kubelet environment file with flags to file "/var/lib/kubelet/kubeadm-flags.env"
[kubelet-start] Writing kubelet configuration to file "/var/lib/kubelet/config.yaml"
[kubelet-start] Activating the kubelet service
.......
[bootstrap-token] creating the "cluster-info" ConfigMap in the "kube-public" namespace
[addons] Applied essential addon: CoreDNS
[addons] Applied essential addon: kube-proxy

Your Kubernetes control-plane has initialized successfully!

To start using your cluster, you need to run the following as a regular user:

  mkdir -p $HOME/.kube
  sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
  sudo chown $(id -u):$(id -g) $HOME/.kube/config

You should now deploy a pod network to the cluster.
Run "kubectl apply -f [podnetwork].yaml" with one of the options listed at:
  https://kubernetes.io/docs/concepts/cluster-administration/addons/

Then you can join any number of worker nodes by running the following on each as root:

kubeadm join 192.168.3.31:6443 --token cytw42.8yq50eg2lry7ldcs \
    --discovery-token-ca-cert-hash sha256:243a364fd4e7eb6084ab15b5cdf1f42e01402bd4ff6f2ae8f576e8c731a727f1
[root@k8smaster ~]# 
```

初始化成功后，如下图，将最后两行内容记录下来，这个命令用来加入Worker节点时使用。

```bash
kubeadm join 192.168.3.31:6443 --token cytw42.8yq50eg2lry7ldcs \
    --discovery-token-ca-cert-hash sha256:243a364fd4e7eb6084ab15b5cdf1f42e01402bd4ff6f2ae8f576e8c731a727f1
```

如果不记得上述信息了也没关系，在master上执行命令`kubeadm token list`，就能看到token参数了，如下图所示：

```bash
[root@master ~]# kubeadm token list
TOKEN                     TTL       EXPIRES                     USAGES                   DESCRIPTION                                                EXTRA GROUPS
cytw42.8yq50eg2lry7ldcs   18h       2019-06-24T16:10:34+08:00   authentication,signing   The default bootstrap token generated by 'kubeadm init'.   system:bootstrappers:kubeadm:default-node-token
```

上一步骤初始化的要求：“To start using your cluster, you need to run the following as a regular user”，需要执行以下命令

上面输出告诉我们还需要做一些工作，执行以下命令配置kubectl，作为普通用户管理集群并在集群上工作

```bash
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config
```

### 3.2 获取pods列表

**kubectl get pods --all-namespaces**命令查看相关状态，可以看到coredns pod处于pending状态，这是因为还没有部署pod网络：

```bash
kubectl get pods --all-namespaces
```

```bash
[root@master ~]# kubectl get pods --all-namespaces
NAMESPACE     NAME                                    READY   STATUS             RESTARTS   AGE
kube-system   coredns-fb8b8dccf-vfwg2                 1/1     pending            0          5h38m
kube-system   coredns-fb8b8dccf-wf2w5                 1/1     pending            0          5h38m
kube-system   etcd-master                             1/1     Running            0          5h38m
kube-system   kube-apiserver-master                   1/1     Running            0          5h38m
kube-system   kube-controller-manager-master          1/1     Running            0          5h37m
kube-system   kube-proxy-mkmkz                        1/1     Running            0          5h38m
kube-system   kube-proxy-t5nmg                        1/1     Running            0          137m
kube-system   kube-proxy-tj2h5                        1/1     Running            0          153m
kube-system   kube-scheduler-master                   1/1     Running            0          5h38m
```



### 3.3 查看集群的健康状态

`kubectl get cs`命令查看健康状态

```bash
[root@master ~]# kubectl get cs
NAME                 STATUS    MESSAGE             ERROR
scheduler            Healthy   ok
controller-manager   Healthy   ok
etcd-0               Healthy   {"health":"true"}
```

### 3.4 部署Pod网络

>You must install a pod network add-on so that your pods can communicate with each other.
>您必须安装一个pod网络附加组件，以便您的pod可以彼此通信。
>The network must be deployed before any applications. Also, CoreDNS will not start up before a network is installed. kubeadm only supports Container Network Interface (CNI) based networks (and does not support kubenet).
>pod网络附加组件是必须安装的，这样pod能够彼此通信，而且网络必须在任何应用程序之前部署。另外，CoreDNS在安装网络之前不会启动。kubeadm只支持基于容器网络接口(CNI)的网络。

支持的Pod网络有JuniperContrail/TungstenFabric、Calico、Canal、Cilium、Flannel、Kube-router、Romana、Wave Net等。

这里我们部署Calico网络，Calico是一个纯三层的方案，其好处是它整合了各种云原生平台(Docker、Mesos 与 OpenStack 等)，每个 Kubernetes 节点上通过 Linux Kernel 现有的 L3 forwarding 功能来实现 vRouter 功能。

```bash
kubectl apply -f https://docs.projectcalico.org/v3.3/getting-started/kubernetes/installation/hosted/rbac-kdd.yaml

kubectl apply -f https://docs.projectcalico.org/v3.3/getting-started/kubernetes/installation/hosted/kubernetes-datastore/calico-networking/1.7/calico.yaml
```

使用 `kubectl get pods --all-namespaces`命令查看运行状态：

还没部署好的时候，状态（ContainerCreating），需要等个几分钟，几分钟之后，所有容器就变成了running状态，可以进行下一步了

```bash
[root@master ~]# kubectl get pods --all-namespaces
NAMESPACE     NAME                                    READY   STATUS             RESTARTS   AGE
kube-system   calico-node-b8xsh                       2/2     Running            0          137m
kube-system   calico-node-fbn6b                       2/2     Running            0          5h36m
kube-system   calico-node-gfm5l                       2/2     Running            0          153m
kube-system   coredns-fb8b8dccf-vfwg2                 1/1     Running            0          5h38m
kube-system   coredns-fb8b8dccf-wf2w5                 1/1     Running            0          5h38m
kube-system   etcd-master                             1/1     Running            0          5h38m
kube-system   kube-apiserver-master                   1/1     Running            0          5h38m
kube-system   kube-controller-manager-master          1/1     Running            0          5h37m
kube-system   kube-proxy-mkmkz                        1/1     Running            0          5h38m
kube-system   kube-proxy-t5nmg                        1/1     Running            0          137m
kube-system   kube-proxy-tj2h5                        1/1     Running            0          153m
kube-system   kube-scheduler-master                   1/1     Running            0          5h38m
```

## 四、worker节点加入

在master节点上查看当前集群的所有节点，只有master

```bash
kubectl get nodes
```

```bash
[root@master ~]# kubectl get nodes
NAME     STATUS   ROLES    AGE     VERSION
master   Ready    master   5h43m   v1.14.0
```

### 4.1 在worker节点上将Worker节点加入集群

在worker节点上执行

```bash
kubeadm join 192.168.3.31:6443 --token cytw42.8yq50eg2lry7ldcs \
    --discovery-token-ca-cert-hash sha256:243a364fd4e7eb6084ab15b5cdf1f42e01402bd4ff6f2ae8f576e8c731a727f1
```

在master节点上检查加入结果

```bash
kubectl get nodes
```
补充：当在worker节点上刚刚执行完加入集群的令牌之后，中间有出现ErrImagePull的状态，等几分钟再看，就已经OK了
```bash
[root@master ~]# kubectl get nodes
NAME     STATUS   ROLES    AGE     VERSION
master   Ready    master   5h43m   v1.14.0
node2    Ready    <none>   142m    v1.14.0
node3    Ready    <none>   158m    v1.14.0
```

等几分钟查看pod状态，就OK了：

```bash
kubectl get pods --all-namespaces
```

```bash
[root@master ~]# kubectl get pods --all-namespaces
NAMESPACE     NAME                                    READY   STATUS             RESTARTS   AGE
kube-system   calico-node-b8xsh                       2/2     Running            0          145m
kube-system   calico-node-fbn6b                       2/2     Running            0          5h43m
kube-system   calico-node-gfm5l                       2/2     Running            0          160m
kube-system   coredns-fb8b8dccf-vfwg2                 1/1     Running            0          5h46m
kube-system   coredns-fb8b8dccf-wf2w5                 1/1     Running            0          5h46m
kube-system   etcd-master                             1/1     Running            0          5h45m
kube-system   kube-apiserver-master                   1/1     Running            0          5h45m
kube-system   kube-controller-manager-master          1/1     Running            0          5h45m
kube-system   kube-proxy-mkmkz                        1/1     Running            0          5h46m
kube-system   kube-proxy-t5nmg                        1/1     Running            0          145m
kube-system   kube-proxy-tj2h5                        1/1     Running            0          160m
kube-system   kube-scheduler-master                   1/1     Running            0          5h45m
```

### 4.2 修改pod的调度策略

k8s的pod默认不会调度到master节点，如果部署的是单节点的集群，就需要按照下面的方式修改pod的调度策略（<https://v1-12.docs.kubernetes.io/docs/setup/independent/create-cluster-kubeadm/#pod-network>）

执行

```bash
kubectl taint nodes --all node-role.kubernetes.io/master-
```

输出如下，即代表完成

> This will remove the `node-role.kubernetes.io/master` taint from any nodes that have it, including the master node, meaning that the scheduler will then be able to schedule pods everywhere

```bash
node "test-01" untainted
taint "node-role.kubernetes.io/master:" not found
taint "node-role.kubernetes.io/master:" not found
```

### 4.3 从集群中移除节点

如果需要从集群中移除`node1`这个Node执行下面的命令：

在master节点上执行：

```bash
kubectl drain node2 --delete-local-data --force --ignore-daemonsets
kubectl delete node node2
```

在`ndoe2`上执行 ......

```bash
kubeadm reset
ifconfig cni0 down
ip link delete cni0
....
rm -rf /var/lib/cni/
```

在master上执行

```bash
kubectl delete node node2
```



## 五、部署dashboard

关于dashboard的介绍和部署方式可参考：<https://kubernetes.io/docs/tasks/access-application-cluster/web-ui-dashboard/#accessing-the-dashboard-ui>
这里部署的是1.10.1版本

### 5.1 下载部署dashboard的yaml文件到本地并修改拉取镜像地址

由于yaml配置文件中指定镜像从google拉取，先下载yaml文件到本地，修改配置从阿里云仓库拉取镜像。

```bash
wget https://raw.githubusercontent.com/kubernetes/dashboard/v1.10.1/src/deploy/recommended/kubernetes-dashboard.yaml
```

修改拉取镜像地址为阿里云的地址：

```bash
image: registry.cn-hangzhou.aliyuncs.com/google_containers/kubernetes-dashboard-amd64:v1.10.1
```

### 5.2 部署dashboard

```bash
kubectl create -f kubernetes-dashboard.yaml
```

查看Pod 的状态为running说明dashboard已经部署成功

```bash
kubectl get pods --all-namespaces
kubectl get pod --namespace=kube-system -o wide | grep dashboard
```



```bash
[root@master ~]# kubectl get pods --all-namespaces
NAMESPACE     NAME                                    READY   STATUS             RESTARTS   AGE
kube-system   calico-node-b8xsh                       2/2     Running            0          155m
kube-system   calico-node-fbn6b                       2/2     Running            0          5h53m
kube-system   calico-node-gfm5l                       2/2     Running            0          170m
kube-system   coredns-fb8b8dccf-vfwg2                 1/1     Running            0          5h56m
kube-system   coredns-fb8b8dccf-wf2w5                 1/1     Running            0          5h56m
kube-system   etcd-master                             1/1     Running            0          5h55m
kube-system   kube-apiserver-master                   1/1     Running            0          5h55m
kube-system   kube-controller-manager-master          1/1     Running            0          5h54m
kube-system   kube-proxy-mkmkz                        1/1     Running            0          5h56m
kube-system   kube-proxy-t5nmg                        1/1     Running            0          155m
kube-system   kube-proxy-tj2h5                        1/1     Running            0          170m
kube-system   kube-scheduler-master                   1/1     Running            0          5h55m
kube-system   kubernetes-dashboard-5d9599dc98-llmh8   1/1     Running            0          147m
[root@master ~]# kubectl get pod --namespace=kube-system -o wide | grep dashboard
kubernetes-dashboard-5d9599dc98-llmh8   1/1     Running   0          147m    10.244.2.2     node2    <none>           <none>
```

同时，Dashboard 会在 kube-system namespace 中创建自己的 Deployment 和 Service：

```bash
kubectl get deployment kubernetes-dashboard --namespace=kube-system
kubectl get service kubernetes-dashboard --namespace=kube-system
```

如下所示

```bash
[root@master ~]# kubectl get deployment kubernetes-dashboard --namespace=kube-system
NAME                   READY   UP-TO-DATE   AVAILABLE   AGE
kubernetes-dashboard   1/1     1            1           148m
[root@master ~]# kubectl get service kubernetes-dashboard --namespace=kube-system
NAME                   TYPE       CLUSTER-IP       EXTERNAL-IP   PORT(S)         AGE
kubernetes-dashboard   NodePort   10.104.111.154   <none>        443:30006/TCP   149m

```

### 5.3 配置使用nodeport方式访问dashport

访问dashboard的方式有很多，这里使用的是配置nodeport的方式来访问。

修改文件`kubernetes-dashboard.yaml`，将service type和nodeport添加进去，注意k8s只支持30000以上的端口

```bash
vim kubernetes-dashboard.yaml
```

加入`type: NodePort`和`nodePort: 30006`，如下所示：

```bash
# ------------------- Dashboard Service ------------------- #

kind: Service
apiVersion: v1
metadata:
  labels:
    k8s-app: kubernetes-dashboard
  name: kubernetes-dashboard
  namespace: kube-system
spec:
  type: NodePort
  ports:
    - port: 443
      targetPort: 8443
      nodePort: 30006
  selector:
    k8s-app: kubernetes-dashboard
```

修改后，重新应用配置文件

```bash
kubectl apply -f kubernetes-dashboard.yaml
```

```bash
[root@master ~]# kubectl apply -f kubernetes-dashboard.yaml
secret/kubernetes-dashboard-certs unchanged
serviceaccount/kubernetes-dashboard unchanged
role.rbac.authorization.k8s.io/kubernetes-dashboard-minimal unchanged
rolebinding.rbac.authorization.k8s.io/kubernetes-dashboard-minimal unchanged
deployment.apps/kubernetes-dashboard unchanged
service/kubernetes-dashboard unchanged

```

端口已经变成了30006

```bash
[root@master ~]# kubectl get service -n kube-system | grep dashboard
kubernetes-dashboard   NodePort    10.104.111.154   <none>        443:30006/TCP            150m
```



### 5.4 生成访问证书

```bash
grep 'client-certificate-data' ~/.kube/config | head -n 1 | awk '{print $2}' | base64 -d >> kubecfg.crt
grep 'client-key-data' ~/.kube/config | head -n 1 | awk '{print $2}' | base64 -d >> kubecfg.key
openssl pkcs12 -export -clcerts -inkey kubecfg.key -in kubecfg.crt -out kubecfg.p12 -name "kubernetes-client"
```

将生成的kubecfg.p12证书导入到Windows中，直接双击打开，下一步导入即可。

注意：导入完成后需重启浏览器。



# 参考

----

[Docker教程-01.安装docker-ce-18.06](https://www.cnblogs.com/tssc/p/9562951.html)

[K8S学习之centos7系统下Kubeadm方式搭建k8s集群](https://blog.51cto.com/10950710/2373669)

[kubeadm搭建kubernetes集群之三：加入node节点](https://blog.csdn.net/boling_cavalry/article/details/78703364)

[Kubeadm部署Kubernetes1.14.3集群](https://blog.51cto.com/14268033/2408590)

[使用kubeadm部署k8s测试环境(centos7)](https://segmentfault.com/a/1190000019465098)

[Kubeadm部署Kubernetes1.14.1集群](http://www.mamicode.com/info-detail-2703357.html)