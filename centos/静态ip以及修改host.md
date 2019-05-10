



sudo gedit /etc/sysconfig/network-scripts/ifcfg-eth0

sudo gedit /etc/sysconfig/network

sudo gedit /etc/sysconfig/network-scripts/ens33

```xml
DEVICE=eth0
TYPE=Ethernet
ONBOOT=yes
NM_CONTROLLED=yes
BOOTPROTO=static
IPADDR=192.168.3.13
NETMASK=255.255.255.0
GETWAY=192.168.3.1
DNS1=8.8.8.8
HWADDR=00:0c:29:de:05:99
IPV6INIT=no
USERCTL=no
```

service network  restart


修改hostname:
hostnamectl set-hostname arthur11

