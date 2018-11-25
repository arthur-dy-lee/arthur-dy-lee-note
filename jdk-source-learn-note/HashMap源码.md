# HashMap源码解读
## Jdk1.8 HashMap源码解读
### int hash(Object key)
现在JDK1.8有新的实现了。首先你应该知道 HashMap里有个数组，叫table。而通过hash值的运算可以算出一个数组下标，决定元素存储在数组的什么位置。这样下次查找的时候可以直接通过同样的方式，直接找到元素，而不用遍历数组。

这个计算方式是 ：(数组长度 - 1) & hash。

数组长度是二的次方，2的2次方，二进制是 100，3次方 1000，4次方 10000。那么按照这个规律，那么长度减 1, 刚好是 011， 0111, 01111。这个刚好就可以当做掩码，来计算数组下标。那么就用掩码和hash做个与运算。

011    & 101010100101001001101 = 01     下标=1，数组长度=4

0111  & 101010100101001001101 = 101   下标=5，数组长度=8

01111 & 101010100101001001101 = 1101  下标=13，数组长度=16

可以发现，通过 掩码 & hash，得出的数组下标不会越界。而数组的总长度总是2的次方，就是为了方便取得掩码的。  明白了上述机制，那么现在谈一个问题。上述计算中，hash 值的高位，没有参与数组下标计算，而是被掩码给掩盖掉了。假如有一类 hash，特点是低位都是 0，高位才有变化。比如 Float类：

```java
System.out.println(Integer.toBinaryString(new Float(1).hashCode()));
System.out.println(Integer.toBinaryString(new Float(2).hashCode()));
System.out.println(Integer.toBinaryString(new Float(3).hashCode()));
System.out.println(Integer.toBinaryString(new Float(4).hashCode()));
System.out.println(Integer.toBinaryString(new Float(5).hashCode()));
System.out.println(Integer.toBinaryString(new Float(6).hashCode()));
```
输出结果
```xml
1111111000000000000000000000000
1000000000000000000000000000000
1000000010000000000000000000000
1000000100000000000000000000000   
1000000101000000000000000000000
1000000110000000000000000000000
```
可以看到低位全都是0，那么直接拿来用的话，就会把发现算出来的数组下标全都是0，这样就全都冲突了。因此，为了避免这种特殊的情况，就需要高位也参与运算，这就是需要重新计算hash值的原因。新版的HashMap加入了红黑树，红黑树会大量的调用hash计算函数。为了提高效率，作者抛弃了老版本的写法，重新简化了 hash 函数。最后，我把新版的写法贴出来，并且把注释翻译了下。你可以看看作者的原话是怎么说的。

```java

/**
 * Computes key.hashCode() and spreads (XORs) higher bits of hash to lower.
 *
 * 换算下 key.hashCode() 的值，通过异或运算（XORs) 使高位扩散到低位。
 *
 * Because the table uses power-of-two masking, sets of
 * hashes that vary only in bits above the current mask will
 * always collide.
 *
 * 由于存储元素的table数组，采用的是 2 的次方的长度，并且以此作为下标取值掩码。
 * 那么对于，只有高于当前掩码长度的位会变化的 hash 来说，计算出来数组下标就会
 * 全部冲突
 *
 * (Among known examples are sets of Float keys
 * holding consecutive whole numbers in small tables.)
 *
 * 其中一种已知的一种情况是 Float 作为 key，并且按照自然数顺序递增的存入一个小
 * 尺寸的table数组中
 *  
 * So we apply a transform that spreads the impact of higher bits
 * downward.
 *  
 * 因此我们利用一种转换，来把高位的变化性扩散的低位去。
 *
 * There is a tradeoff between speed, utility, and
 * quality of bit-spreading.
 *
 * 这是基于速度，效用和位扩散品质的一种权衡方案。
 *
 * Because many common sets of hashes
 * are already reasonably distributed (so don't benefit from
 * spreading), and because we use trees to handle large sets of
 * collisions in bins, we just XOR some shifted bits in the
 * cheapest possible way to reduce systematic lossage, as well as
 * to incorporate impact of the highest bits that would otherwise
 * never be used in index calculations because of table bounds.
 *
 * 因为许多常见的 hash 值都是适度分散的（因此位扩散的收益不大），又因为
 * 我们使用树，来管控大数量的冲突元素。使用XOR异或运算来移位，可以尽可能低成本地
 * 减少系统性损耗，也将原本不参与数组下标计算的高位的也给包含进来了。
 *
 */
static final int hash(Object key) {
    int h;  // 下面是将低16位和高16位做个异或运算，高16位保持不变
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
```

```Java
final Node<K,V> getNode(int hash, Object key) {
    Node<K,V>[] tab; Node<K,V> first, e; int n; K k;
    if ((tab = table) != null && (n = tab.length) > 0 &&
        (first = tab[(n - 1) & hash]) != null) {
        if (first.hash == hash && // always check first node
            ((k = first.key) == key || (key != null && key.equals(k))))
            return first;
        if ((e = first.next) != null) {
            if (first instanceof TreeNode)
                return ((TreeNode<K,V>)first).getTreeNode(hash, key);
            do {
                if (e.hash == hash &&
                    ((k = e.key) == key || (key != null && key.equals(k))))
                    return e;
            } while ((e = e.next) != null);
        }
    }
    return null;
}
```

其中，first = tab[(n - 1) & hash] 就是 (n-1) & hash来确认数组中的位置


```java
static final int tableSizeFor(int cap) {
    int n = cap - 1;
    n |= n >>> 1;
    n |= n >>> 2;
    n |= n >>> 4;
    n |= n >>> 8;
    n |= n >>> 16;
    return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
}
```
这个方法被调用的地方：
```Java
public HashMap(int initialCapacity, float loadFactor) {  
   /**省略此处代码**/  
   this.loadFactor = loadFactor;  
   this.threshold = tableSizeFor(initialCapacity);  
}  
```
由此可以看到，当在实例化HashMap实例时，如果给定了initialCapacity，由于HashMap的capacity都是2的幂，因此这个方法用于找到大于等于initialCapacity的最小的2的幂（initialCapacity如果就是2的幂，则返回的还是这个数）。
下面分析这个算法：
首先，为什么要对cap做减1操作。int n = cap - 1;
这是为了防止，cap已经是2的幂。如果cap已经是2的幂， 又没有执行这个减1操作，则执行完后面的几条无符号右移操作之后，返回的capacity将是这个cap的2倍。
