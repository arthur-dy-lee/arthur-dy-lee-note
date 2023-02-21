
## 一、基础知识
### 1.1 socket工作在网络7层的应用层/传输层
| 序号 | 名称 | 协议 |
| :---- | :---- | :---- |
| 7 | 应用层 | HTTP/FTP/DNS/Telnet |
| 6 | 表示层 | - |
| 5 | 会话层 | - |
| 4 | 传输层 | TCP/UDP  |
| 3 | 网络层 | IP  |
| 2 | 数据链路层 | ARP |
| 1 | 物理层 | ISO2110/IEEE802 |

其中高层，既7、6、5、4层定义了应用程序的功能，下面3层，既3、2、1层主要面向通过网络的端到端的数据流。

HTTP是基于应用层，socket是基于传输层（tcp/udp）

socket的实现部分, 就是系统协议栈部分， 应该包含了 网络层 (ip), 传输层(tcp/udp)等等。
用socket写程序的人， 就要看用socket那部分了。 如果你直接用ip层, rawsocket, 假如你自己写个tcp协议， 那你应该做的就是传输层。

如果你是用tcp/udp等协议， 做网络应用， 那应该是应用层。

我们通常所用的socket协议是基于TCP协议

TCP是面向连接的协议，UDP是面向非连接的协议

### 1.2 应用场景
TCP协议一般应用场景：能为应用程序提供可靠的通信连接，使一台计算机发出的字节流无差错地发往网络上的其他计算机，对可靠性要求高的数据通信系统往往使用TCP协议传输数据。如，网络游戏，银行交互，支付。

视频，图片，断点续传的情况下要用socket。http的协议的无状态性实现不了这个功能。

UDP协议一般应用场景：适用于一次只传送少量数据、对可靠性要求不高的应用环境。如windows的ping命令、QQ发消息。

### 1.3 SOCKET连接与TCP连接
创建Socket连接时，可以指定使用的传输层协议，Socket可以支持不同的传输层协议（TCP或UDP），当使用TCP协议进行连接时，该Socket连接就是一个TCP连接。

在什么情况获取到这个Socket呢，通过理论加测试，结论是在三次握手操作后，系统才会将这个连接交给应用层，ServerSocket 才知道有一个连接过来了。

那么系统当接收到一个TCP连接请求后，如果上层还没有接受它（假如SocketServer循环处理Socket，一次一个），那么系统将缓存这个连接请求，既然是缓存那么就是有限度的，书上介绍的是缓存3个，但是经过我的本机测试是50个，也就是说，系统将会为应用层的Socket缓存50和TCP连接（这是和系统底层有关系的），当超过指定数量后，系统将会拒绝连接。

假如缓存的TCP连接请求发送来数据，那么系统也会缓存这些数据，等待SocketServer获得这个连接的时候一并交给它。

换句话说，系统接收TCP连接请求放入缓存队列，而SocketServer从缓存队列获取Socket。

### 1.4 Socket连接与HTTP连接

由于通常情况下Socket连接就是TCP连接，因此Socket连接一旦建立，通信双方即可开始相互发送数据内容，直到双方连接断开。但在实际网络应用 中，客户端到服务器之间的通信往往需要穿越多个中间节点，例如路由器、网关、防火墙等，大部分防火墙默认会关闭长时间处于非活跃状态的连接而导致 Socket 连接断连，因此需要通过轮询告诉网络，该连接处于活跃状态。

而HTTP连接使用的是“请求—响应”的方式，不仅在请求时需要先建立连接，而且需要客户端向服务器发出请求后，服务器端才能回复数据。

很多情况下，需要服务器端主动向客户端推送数据，保持客户端与服务器数据的实时与同步。此时若双方建立的是Socket连接，服务器就可以直接将数据传送给客户端；若双方建立的是HTTP连接，则服务器需要等到客户端发送一次请求后才能将数据传回给客户端，因此，客户端定时向服务器端发送连接请求，不仅可以 保持在线，同时也是在“询问”服务器是否有新的数据，如果有就将数据传给客户端。

### 1.5 心跳包机制
所谓“心跳包”机制，其实就是服务器端按照固定的频率给客户端发送心跳包，客户端接受到心跳包之后做回应。如果服务器端发送了一个心跳包，客户端没有回应。服务器认为客服端已经不在了，就会断开长连接。

事实上这是为了保持长连接，至于这个包的内容，是没有什么特别规定的，不过一般都是很小的包，或者只包含包头的一个空包。

在TCP的机制里面，本身是存在有心跳包的机制的，也就是TCP的选项：SO_KEEPALIVE。系统默认是设置的2小时的心跳频率。但是它检查不到机器断电、网线拔出、防火墙这些断线。

说明： 服务器只是在客户端长时间没有给服务器发送数据的情况下，才会发心跳包。

比如我们制定固定频率为30秒，即：服务器每隔30秒给客户端发送一个心跳包。假如服务器和客户端互相欢快的传递数据已经超过30秒，但是此时在30秒的时候并不会发送心跳包。

为什么不会发送心跳包？

因为发送心跳包的时间，是从客户端最后一次传递数据给服务器的时间开始计算的，比如，客户端最后一次给服务器传递数据是 10点05分05秒，从此刻开始计算，如果到05分35秒的时候，客户端没有给服务器端传递数据，那么服务器就会发送心跳包给客户端，客户端接受到心跳包做回应，告诉服务器长连接处于保持中，但是如果在05分34秒的时候，客户端给服务器端传递了数据，那么此时就会从05分34秒从新开始计算，再间隔30秒....



## NioClientBoss
NioClientBoss.Class
```java

```

## QA
1/ 试一下new接口ChannelPipelineFactory，然后返回一个ChannelPipeline， 匿名内部类
bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
    public ChannelPipeline getPipeline() {
        ChannelPipeline p = Channels.pipeline();
        if (sslCtx != null) {
            p.addLast("ssl", sslCtx.newHandler(HOST, PORT));
        }
        p.addLast("discard", new DiscardClientHandler());
        return p;
    }
});

2/ pipeline();为什么可以这么写？都没有类，TelnetClientPipelineFactory也没有继承。
```java
public class TelnetClientPipelineFactory implements ChannelPipelineFactory {

    private final SslContext sslCtx;

    public TelnetClientPipelineFactory(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    public ChannelPipeline getPipeline() {
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = pipeline();

        if (sslCtx != null) {
            pipeline.addLast("ssl", sslCtx.newHandler());
        }

        // Add the text line codec combination first,
        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(
                8192, Delimiters.lineDelimiter()));
        pipeline.addLast("decoder", new StringDecoder());
        pipeline.addLast("encoder", new StringEncoder());

        // and then business logic.
        pipeline.addLast("handler", new TelnetClientHandler());

        return pipeline;
    }
}
```
