
#### HBase 的 rowKey 的设计原则
① Rowkey 长度原则。Rowkey 是一个二进制码流，Rowkey 的长度被很多开发者建议说设计在 10~100 个字节，不过建议是越短越好，不要超过 16 个字节。
原因如下：
（1）数据的持久化文件 HFile 中是按照 KeyValue 存储的，如果 Rowkey 过长比如 100个字节，1000 万列数据光 Rowkey 就要占用 100*1000 万=10 亿个字节，将近 1G 数据，这会极大影响 HFile 的存储效率；
（2）MemStore 将缓存部分数据到内存，如果 Rowkey 字段过长内存的有效利用率会降低，系统将无法缓存更多的数据，这会降低检索效率。因此 Rowkey 的字节长度越短越好。
② Rowkey 散列原则
如果Rowkey 是按时间戳的方式递增，不要将时间放在二进制码的前面，建议将Rowkey的高位作为散列字段，由程序循环生成，低位放时间字段，这样将提高数据均衡分布在每个Regionserver 实现负载均衡的几率。如果没有散列字段，首字段直接是时间信息将产生所有新数据都在一个 RegionServer 上堆积的热点现象，这样在做数据检索的时候负载将会集中在个别 RegionServer，降低查询效率。
③ Rowkey 唯一原则。必须在设计上保证其唯一性。

#### HBase 中一个 cell 的结构？
HBase 中通过 row 和 columns 确定的为一个存贮单元称为 cell。
Cell：由{row key, column(=<family> + <label>), version}唯一确定的单元。cell 中的数据是没有类型的，全部是字节码形式存贮。 

一个单元格就是由前面说的行键、列标示、版本号唯一确定的字节码
Hbase表的索引是行键、列族、列限定符和时间戳

#### 预分区
每一个 region 维护着 startRow 与 endRowKey，如果加入的数据符合某个 region 维护的rowKey 范围，则该数据交给这个 region 维护。那么依照这个原则，我们可以将数据所要投放的分区提前大致的规划好，以提高 HBase 性能.
预分区的目的主要是在创建表的时候指定分区数，提前规划表有多个分区，以及每个分区的区间范围，这样在存储的时候 rowkey 按照分区的区间存储，可以避免 region 热点问题。


#### Memstore触发Flush 行为的条件包括：
Memstore级别：Region中任意一个MemStore达到了 hbase.hregion.memstore.flush.size 控制的上限（默认128MB），会触发Memstore的flush。
Region级别：Region中Memstore大小之和达到了 hbase.hregion.memstore.block.multiplier hbase.hregion.memstore.flush.size 控制的上限（默认 2 128M = 256M），会触发Memstore的flush。
RegionServer级别：Region Server中所有Region的Memstore大小总和达到了 hbase.regionserver.global.memstore.upperLimit ＊ hbase_heapsize 控制的上限（默认0.4，即RegionServer 40%的JVM内存），将会按Memstore由大到小进行flush，直至总体Memstore内存使用量低于 hbase.regionserver.global.memstore.lowerLimit ＊ hbase_heapsize 控制的下限（默认0.38， 即RegionServer 38%的JVM内存）。
RegionServer中HLog数量达到上限：将会选取最早的 HLog对应的一个或多个Region进行flush（通过参数hbase.regionserver.maxlogs配置）。
HBase定期flush：确保Memstore不会长时间没有持久化，默认周期为1小时。为避免所有的MemStore在同一时间都进行flush导致的问题，定期的flush操作有20000左右的随机延时。
手动执行flush：用户可以通过shell命令 flush ‘tablename’或者flush ‘region name’分别对一个表或者一个Region进行flush。
https://www.cnblogs.com/littleatp/p/12079507.html


#### hbase 的存储结构？ 　　
Hbase 中的每张表都通过行键(rowkey)按照一定的范围被分割成多个子表（HRegion），默认一个 HRegion 超过 256M 就要被分割成两个，由 HRegionServer 管理，管理哪些 HRegion由 Hmaster 分配。 

#### Hbase 中 scan 对象的 setCache 和 setBatch 方法的使用. 
cache是面向行的优化处理，batch是面向列的优化处理。

在获取结果ResultScanner时，hbase会在你每次调用ResultScanner.next（）操作时对返回的每个Row执行一次RPC操作。hbase将执行查询请求以迭代器的模式设计，在执行next（）操作时才会真正的执行查询操作，而对每个Row都会执行一次RPC操作。
cache值得设置并不是越大越好，需要做一个平衡。cache的值越大，则查询的性能就越高，但是与此同时，每一次调用next（）操作都需要花费更长的时间，因为获取的数据更多并且数据量大了传输到客户端需要的时间就越长，一旦你超过了maximum heap the client process 拥有的值，就会报outofmemoryException异常。当传输rows数据到客户端的时候，如果花费时间过长，则会抛出ScannerTimeOutException异常。

batch用来控制每次调用next（）操作时会返回多少列，比如你设置setBatch（5），那么每一个Result实例就会返回5列，如果你的列数为17的话，那么就会获得四个Result实例，分别含有5,5,5,2个列。



#### 产生热点问题的原因
1.	hbase酌中的数据是按照字英序排序的，当大堇连续的rowkey雯中写在个别的region, 各个region之间数据分布不均衡；
2 	创建表时没有提前预分区，创建的表默认只有一个region, 大量的数据写入当前region
3. 	创建表已经提前预分区，但是设计钓rowkey没有规律可循
热点问题时解决方案：
1.	陇机数＋业务主键如果想让最近的数据快速get到，可以将时间戳加上。
2.	Rowkey设计越短越好，不要超过10~100个字节
3.	映射「egionNo, 这样既可以让数据均匀分布到各个region中，同时可以根据startkd和endkey可以get到同一批数据
 
#### Hbase二级索引
默认情况下，Hbase只支持rowkey的查询，对于多条件的组合查询的应用场景，不够给力。
如果将多条件组合查询的字段都拼接在RowKey中显然又不太可能
全表扫描再结合过滤器筛选出目标数据(太低效)，所以通过设计HBase的二级索引来解决这个问题。
这里所谓的二级索引其实就是创建新的表，并建立各列值（family：column）与行键（rowkey）之间的映射关系。这种方式需要额外的存储空间，属于一种以空间换时间的方式

#### 访问hbase中行的三种方式
与nosql数据库们一样,row key是用来检索记录的主键。只有3中方式：
1 通过单个row key访问（get访问单行数据）
2 通过row key的range（访问某个区间内的rowkey，访问多行数据）
3 全表扫描 （scan访问全本的数据）


Table在行的方向上分割为多个HRegion,Hregion是按照大小分割的,每个表一开始只有一个Hregion,随着数据不断插入表,它越来越大,默认10G,达到阈值的时候会分裂split为两个HRegion;HRegion是Hbase扩展和负载均衡的基本单元,是分布式存储的最小单元;不同的HRegion分布到不同的RegionServer上;

HBase中，表会被划分为1…n个Region，被托管在RegionServer中。Region有二个重要的属性：StartKey与EndKey表示这个Region维护的rowKey范围，当我们要读/写数据时，如果rowKey落在某个start-end key范围内，那么就会定位到目标region并且读/写到相关的数据。

#### 红黑树、b树、b+树、LSM树
红黑树属于平衡二叉树。说它不严格是因为它不是严格控制左、右子树高度或节点数之差小于等于1。
但红黑树高度依然是平均log(n),且最坏情况高度不会超过2log(n),这有数学证明。所以它算平衡树,只是不严格。不过严格与否并不影响数据结构的复杂度。
红黑树有两个重要性质：
1、红节点的孩子节点不能是红节点；
2、从根到前端节点的任意一条路径上的黑节点数目一样多。
这两条性质确保该树的高度为logN，所以是平衡树。

红黑树是每个节点都带颜色的树，节点颜色或是红色或是黑色，红黑树是一种查找树。红黑树有一个重要的性质，从根节点到叶子节点的最长的路径不多于最短的路径的长度的两倍。对于红黑树，插入，删除，查找的复杂度都是O（log N）。

红黑树性质
每个节点非红即黑.
根节点是黑的。
每个叶节点(叶节点即树尾端NUL指针或NULL节点)都是黑的.
如果一个节点是红的,那么它的两儿子都是黑的.
对于任意节点而言,其到叶子点树NIL指针的每条路径都包含相同数目的黑节点.


#### 列式存储应用场景
基于一列或比较少的列计算的时候
经常关注一张表某几列而非整表数据的时候
数据表拥有非常多的列的时候
数据表有非常多行数据并且需要聚集运算的时候
数据表列里有非常多的重复数据，有利于高度压缩
 
#### 行式存储应用场景
关注整张表内容，或者需要经常更新数据
需要经常读取整行数据
不需要聚集运算，或者快速查询需求
数据表本身数据行并不多
数据表的列本身有太多唯一性的数据


#### 倒排索引




hlog会三副本同步吗？
是否可以只取cell数据，或只读取一列的数据。
hbase的 reversed scan
编写一个工具，扫描hbase:meta表的方式来检查cloud:note是否存在空洞区间，请尽可能高效
在bucketCache缓存一个Block时，设计为先缓存Block到RAMCache，然后再异步写入IOEngine。请问这样设计有什么好处？
为什么需要单独设计一个backingMap来存放每个Block的（offset,length)，而不是直接把block存入到backingMap中？
P91
LAMX Disruptor
PREAD的scan和STREAM的scan有什么区别？分别适合什么样的扫描场景？
P163/164
P179/181
学习copy-on-write的基本原理
promotion failed和concurrent mode failure区别
hbase Procedure分布式任务流框架
ImmuatbleSegment(Array Map)和ImmuatbleSegment(Chunk Map)
WAL写入优化演进







